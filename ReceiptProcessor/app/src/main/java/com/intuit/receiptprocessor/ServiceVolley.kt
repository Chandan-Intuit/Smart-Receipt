package com.intuit.receiptprocessor

import android.util.Log
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import java.io.UnsupportedEncodingException

class ServiceVolley :  ServiceInterface {

    val TAG = ServiceVolley::class.java.simpleName
    val basePath = "http://192.168.0.102:8080/"



    override fun post(path: String, params: String, completionHandler: (response: String?) -> Unit) {

        Log.d(TAG,params)


        val stringRequest = object : StringRequest(Request.Method.POST, basePath + path, Response.Listener { response ->
            Log.d(TAG, "/post request OK! Response: $response")
            completionHandler(response)
        }, Response.ErrorListener { e ->
            completionHandler(null)
        }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }
            override fun getBody(): ByteArray {
                try {
                    return params.toByteArray(charset("utf-8"))
                }
                catch ( exp : UnsupportedEncodingException)
                {
                    var byteArray :  ByteArray = ByteArray(0)
                    return byteArray
                }

            }
        }
        stringRequest.setRetryPolicy(DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))
        Application.instance?.addToRequestQueue(stringRequest, TAG)
    }
}