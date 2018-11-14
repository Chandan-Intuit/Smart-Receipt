Feature: stateful mock server

Background:
* configure cors = true
* def FileUtils = Java.type('com.intuit.karate.FileUtils')
* def runtime = java.lang.Runtime.getRuntime()
* def exec = function(cmd){ return FileUtils.toString(runtime.exec(cmd).getInputStream()) }


Scenario: pathMatches('/test')
    * def json = request
    * print json
    * def response = exec('python3 receipt_estimator_completed.py --reciept_json '  + json)
    * print response



