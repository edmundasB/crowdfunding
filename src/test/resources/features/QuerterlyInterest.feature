Feature: Quarterly interest payment for investor
  Scenario: Payment from payment quarterly schedule is coming
    Given Investors list in sheet 1
    When Comes date of the payment from payment schedule (2019-05-31)
    Then User 9 receives 25.21 EUR of interest