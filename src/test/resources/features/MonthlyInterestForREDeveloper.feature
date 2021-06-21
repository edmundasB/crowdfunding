Feature: Monthly interest calculation to RE developer (without Profitus)
  Scenario: Payment from payment schedule is coming
    Given Investments monthly list in sheet 3
    When Comes date of the payment from developer monthly payment schedule (2019-8-30)
    Then RE developer pays 4652.84 EUR of interest