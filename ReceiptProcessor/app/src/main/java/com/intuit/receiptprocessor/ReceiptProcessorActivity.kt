package com.intuit.receiptprocessor

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.view.View
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.activity_receipt_processor.*

class ReceiptProcessorActivity : AppCompatActivity() {

    companion object {
        val  CAMERA_ACTIVITY_REQUEST_CODE = 101;
        val  GALLERY_ACTIVITY_REQUEST_CODE = 102;
        val DECODE_IMAGE_ACTIVITY= 103;

    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_receipt_processor)
        //listener function for gallery icon click
        validatePermissions();
        gallery?.setOnClickListener{openGallery()}

        /*To do - Set listener function for camera click*/

    }


    private fun validatePermissions() {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(object: PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse?) {

                    }

                    override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest?,
                                                                    token: PermissionToken?) {
                        AlertDialog.Builder(this@ReceiptProcessorActivity)
                                .setTitle(R.string.storage_permission_rationale_title)
                                .setMessage(R.string.storage_permition_rationale_message)
                                .setNegativeButton(android.R.string.cancel,
                                        { dialog, _ ->
                                            dialog.dismiss()
                                            token?.cancelPermissionRequest()
                                        })
                                .setPositiveButton(android.R.string.ok,
                                        { dialog, _ ->
                                            dialog.dismiss()
                                            token?.continuePermissionRequest()
                                        })
                                .setOnDismissListener({ token?.cancelPermissionRequest() })
                                .show()
                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                        Snackbar.make(ReceiptActivity_container!!,
                                R.string.storage_permission_denied_message,
                                Snackbar.LENGTH_LONG)
                                .show()
                    }
                })
                .check()
    }

    private fun openGallery()
    {
        val galleryIntent = Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(galleryIntent, GALLERY_ACTIVITY_REQUEST_CODE)
        }

    }




    private fun openCamera()
    {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, CAMERA_ACTIVITY_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode != Activity.RESULT_OK)
            return;

        if (requestCode == CAMERA_ACTIVITY_REQUEST_CODE) {


            startImageDecodeActivity(data)

        } else if (requestCode == GALLERY_ACTIVITY_REQUEST_CODE){
            /* */
            startImageDecodeActivity(data)
        }

        else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }


    private fun startImageDecodeActivity(data: Intent?)
    {
        val intent = Intent(this,DecodeImageActivity::class.java)
        intent.putExtra("ImageBitmap",data)
        startActivityForResult(intent,DECODE_IMAGE_ACTIVITY)
    }

    private  fun setListenerForCameraClick()
    {
        camera?.setOnClickListener{openCamera()}
    }
}
