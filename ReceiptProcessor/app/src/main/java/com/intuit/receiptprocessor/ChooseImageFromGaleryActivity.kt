package com.intuit.receiptprocessor

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore

class ChooseImageFromGaleryActivity : AppCompatActivity() {

    val REQUEST_IMAGE_FROM_GALLERY  = 1

    companion object {
        val  IMAGE_INTENT_DATA = "com.intuit.receiptprocessor.imageintent"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        openGalleryIntent()
    }



    /*
       Function - Launch Real camera on Screen
   */
    private fun openGalleryIntent() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_FROM_GALLERY)
        }
    }


    /*
      Function - Handle Camera Output
    */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_FROM_GALLERY -> {
                    setResultAndFinishActivity(data)
                }
            }
        }
    }

    /*
      Function - Finish Activity,set image bitmap as output
    */
    private fun setResultAndFinishActivity(data: Intent?)
    {
        val output = Intent()
        val extras = data?.getExtras()
        output.putExtra(IMAGE_INTENT_DATA,extras)
        setResult(Activity.RESULT_OK,output)

    }

}
