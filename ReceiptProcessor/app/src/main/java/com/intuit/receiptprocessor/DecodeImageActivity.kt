package com.intuit.receiptprocessor

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.intuit.receiptprocessor.R.id.processReceipt
import kotlinx.android.synthetic.main.activity_decode_image.*
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.net.URLEncoder
import java.nio.charset.StandardCharsets.UTF_8

class DecodeImageActivity : AppCompatActivity() {

    private var mCurrentPhotoPath: String = ""
    internal var mBlockData: ArrayList<TextBlock> = ArrayList<TextBlock>()
    internal var mBlockText: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_decode_image)
        processReceipt.setOnClickListener{decodeImage()}
        processGalleryImage(getIntent().getParcelableExtra<Intent>("ImageBitmap"))
        FirebaseApp.initializeApp(this);
        defaultImageCheckbox?.setOnClickListener{checkBoxToggled()}

    }


    private fun processGalleryImage(data: Intent?) {
        if (data != null)
        {
            val contentURI = data!!.data

            try
            {
                mCurrentPhotoPath = getRealPathFromURI(contentURI);
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                if(defaultImageCheckbox.isChecked)
                    image.setImageDrawable(applicationContext.getResources().getDrawable(R.drawable.receipt))
                else
                    image.setImageBitmap(bitmap)
            }
            catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this@DecodeImageActivity, "Failed!", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private  fun getRealPathFromURI(contentUri : Uri) : String
    {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = managedQuery(contentUri,
                proj, // WHERE clause selection arguments (none)
                null, null, null)// Which columns to return
        // WHERE clause; which rows to return (all rows)
        // Order-by clause (ascending by name)

        val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }



    internal var BlockText = ""
    internal var LineText = ""
    internal var ElementText = ""
    internal var imageHeight = 0
    internal var imageWidth = 0
    internal var i = 3
    internal var index = 0


    fun decodeImage() {
        mBlockData = ArrayList();
        val dest = File(mCurrentPhotoPath)
        val fis: FileInputStream
        fis = FileInputStream(dest)
        val bitmap : Bitmap
        /*To Do -
         * Remove if else condition below and replace code with
          * bitmap = BitmapFactory.decodeStream(fis)
           * it means we are getting  bitmap from image which is coming from gallery not from drawable*/
        if(defaultImageCheckbox.isChecked == false)
             bitmap = BitmapFactory.decodeStream(fis)
        else
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.receipt);
        val ei = ExifInterface(mCurrentPhotoPath)
        val orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED)

        var rotatedBitmap: Bitmap;
        when (orientation) {

            ExifInterface.ORIENTATION_ROTATE_90 -> rotatedBitmap = rotateImage(bitmap, 90f)

            ExifInterface.ORIENTATION_ROTATE_180 -> rotatedBitmap = rotateImage(bitmap, 180f)

            ExifInterface.ORIENTATION_ROTATE_270 -> rotatedBitmap = rotateImage(bitmap, 270f)

            ExifInterface.ORIENTATION_NORMAL -> rotatedBitmap = bitmap
            else -> rotatedBitmap = bitmap
        }

        val image3: FirebaseVisionImage

        image3 = FirebaseVisionImage.fromBitmap(rotatedBitmap)

        imageWidth = image3.bitmapForDebugging.width
        imageHeight = image3.bitmapForDebugging.height

        val detector = FirebaseVision.getInstance()
                .visionTextDetector

        val result = detector.detectInImage(image3)
                .addOnSuccessListener { firebaseVisionText ->
                    index++
                    for (block in firebaseVisionText.blocks) {
                        for (line in block.lines) {
                            val cornerPoints = line.cornerPoints
                            val currentBlock = TextBlock()

                            //currentBlock.BlockText = line.getText();

                            currentBlock.xLeftRel = 100 * cornerPoints!![0].x / imageWidth
                            currentBlock.yTopRel = 100 * cornerPoints[0].y / imageHeight
                            if (line.text.toLowerCase().contains("total"))
                                currentBlock.isTotal = 1

                            val isNumericTemp = line.text.replace("$", "")

                            try {
                                val number = java.lang.Double.parseDouble(isNumericTemp)
                                currentBlock.isNumeric = 1
                            } catch (e: Exception) {
                                Log.d("Chandan", e.message)
                                currentBlock.isNumeric = 0
                            }
                            mBlockData.add(currentBlock)
                            mBlockText.add(line.text)


                        }
                    }
                    readHardcodedResponseStartFinalActivity()

                }
                .addOnFailureListener {
                    var i = 0
                    i++
                }
    }

    fun rotateImage(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height,
                matrix, true)
    }

    fun fillTextAgainstModelWithRealServiceCall()
    {
        val service = ServiceVolley()
        val apiController = APIController(service)
        val path = "test"
        val gson = Gson()
        val json = gson.toJson(mBlockData)
        var finalOutPut = ""


        apiController.post(path, json) { response ->



            val listType = object : TypeToken<List<String>>() { }.type

            var responseList = gson.fromJson<List<String>>(response, listType)

            var i = 0
            for (s in mBlockText) {

                finalOutPut += s + "----->" + responseList?.get(i) + "\n\n"
                i++
            }

            val myIntent = Intent(this@DecodeImageActivity,
                    ParsedImageTextActivity::class.java)
            myIntent.putExtra("ParsedText", finalOutPut)
            startActivity(myIntent)
        }

    }

    private fun readHardcodedResponseStartFinalActivity()
    {
        var ParsedJson: String? = null
        try {

            val assetManager = assets
            val response = assetManager.open("response.json")
            val size = response.available()
            val buffer = ByteArray(size)
            response.read(buffer)
            response.close()
            ParsedJson = String(buffer, UTF_8)
        } catch (ex: IOException) {
            ex.printStackTrace()

        }

        var predict = Predictions()
        val gson = Gson()
        predict = gson.fromJson(ParsedJson, Predictions::class.java)
        var FinalOutput = ""

        var i = 0
        for (s in mBlockText) {

            FinalOutput += s + "----->" + predict.prediction?.get(i) + "\n\n"
            i++
        }
        val myIntent = Intent(this@DecodeImageActivity,
                ParsedImageTextActivity::class.java)
        myIntent.putExtra("ParsedText", FinalOutput)
        startActivity(myIntent)



    }

    override fun onPause() {
        super.onPause()
        mBlockText.clear()
        mBlockData.clear()
    }

    fun checkBoxToggled()
    {
        if(defaultImageCheckbox.isChecked)
            image.setImageDrawable(applicationContext.getResources().getDrawable(R.drawable.receipt))

        else
        {
            processGalleryImage(getIntent().getParcelableExtra<Intent>("ImageBitmap"))
        }


    }

}
