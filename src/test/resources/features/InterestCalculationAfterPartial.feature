Feature: Interest calculation after partial loan repayments
  Scenario: Interest payment from payment schedule after repayments
    Given Given Investors monthly list  in sheet      5
    And Partial repayment date 2019-01-26 and partial repayment sum 34000.0 € and days from last return 71
    And Partial repayment date 2019-02-04 and partial repayment sum 123000.0 € and days from last return 80
    And Partial repayment date 2019-04-07 and partial repayment sum 15000.0 € and days from last return 142
    And Interest payment date 2018-12-31
    And Interest payment date 2019-01-31
    And Interest payment date 2019-02-28
    When Comes date of the payment from payment schedule 2019-02-28
    Then User id-9 receives 2019-02-28 5.79 € interest test