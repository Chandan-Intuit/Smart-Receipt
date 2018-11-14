package com.intuit.receiptprocessor

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_parsed_image_text.*

class ParsedImageTextActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parsed_image_text)

        val intent = intent
        val parsedText = intent.getStringExtra("ParsedText")

        textView_ParsedText.text = parsedText
    }
}
