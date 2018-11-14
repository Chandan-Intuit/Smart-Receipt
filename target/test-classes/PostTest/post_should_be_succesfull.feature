Feature: The post should be successful

  Background:

    * def taskRequestBody = '[{"xLeftRel":15,"yTopRel":12,"isTotal":0,"isNumeric":0,"category":3},{"xLeftRel":12, "yTopRel":69,"isTotal":0,"isNumeric":0,"category":3},{"xLeftRel":12,"yTopRel":78,"isTotal":0,"isNumeric":0,"category":3},{"xLeftRel":12,"yTopRel":90,"isTotal":1,"isNumeric":0,"category":5},{"xLeftRel":72,"yTopRel":61,"isTotal":0,"IsNumeric":1,"category":4},{"xLeftRel":75,"yTopRel":69,"isTotal":0,"isNumeric":1,"category":4},{"xLeftRel":75,"yTopRel":78,"isTotal":0,"isNumeric":1,"category":4},{"xLeftRel":70,"yTopRel":90,"isTotal":0,"isNumeric":1,"category":6},{"xLeftRel":26,"yTopRel":2,"isTotal":0,"isNumeric":0,"category":0},{"xLeftRel":32,"yTopRel":16,"isTotal":0,"isNumeric":0,"category":1},{"xLeftRel":24,"yTopRel":26,"isTotal":0,"isNumeric":0, "category":1},{"xLeftRel":19,"yTopRel":34,"isTotal":0,"isNumeric":0,"category":2},{"xLeftRel":30,"yTopRel":43,"isTotal":0,"isNumeric":0,"category":2}]'

    Scenario:

      Given url 'http://127.0.0.1:8080'
      And path '/test'
      And request taskRequestBody
      And header Accept = 'application/json'
      When method post
      Then status 200

      * def result = response
      * match result == '#notnull'
      * match result == '#string'
      * match result contains 'vendor address'



