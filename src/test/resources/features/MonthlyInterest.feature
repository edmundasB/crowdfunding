Feature: Monthly interest payment for investor
  Scenario: Payment from monthly payment schedule is coming
    Given Given Investors monthly list  in sheet       5
    When Comes date of the payment from monthly payment schedule (2019-02-28)
    Then User 9 receives 8.44 EUR of monthly interest