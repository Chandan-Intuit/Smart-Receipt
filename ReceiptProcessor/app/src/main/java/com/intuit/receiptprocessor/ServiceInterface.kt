package com.intuit.receiptprocessor

interface ServiceInterface {

    fun post(path: String, params: String, completionHandler: (response: String?) -> Unit)

}