package com.intuit.receiptprocessor

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore

class ImageCaptureActivity : AppCompatActivity() {

    val REQUEST_IMAGE_CAPTURE  = 1

    companion object {
        val  CAMERA_INTENT_DATA = "com.intuit.receiptprocessor.cameraintent"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(hasCamera())
            launchCameraIntent()
    }

    /*
        Function - Check If Device has camera in built or not
    */
    private fun hasCamera(): Boolean {
        return packageManager.hasSystemFeature(
                PackageManager.FEATURE_CAMERA_ANY)
    }


    /*
       Function - Launch Real camera on Screen
   */
    private fun launchCameraIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }


    /*
      Function - Handle Camera Output
    */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
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
        output.putExtra(CAMERA_INTENT_DATA,extras)
        setResult(Activity.RESULT_OK,output)

    }

}
