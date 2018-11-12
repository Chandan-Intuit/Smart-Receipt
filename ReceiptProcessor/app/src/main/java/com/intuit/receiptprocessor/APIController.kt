package com.intuit.receiptprocessor

class APIController constructor(serviceInjection: ServiceInterface): ServiceInterface {
    private val service: ServiceInterface = serviceInjection

    override fun post(path: String, params: String, completionHandler: (response: String?) -> Unit) {
        service.post(path, params, completionHandler)
    }
}