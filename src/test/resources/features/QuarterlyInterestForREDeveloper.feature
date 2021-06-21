Feature: Quarterly interest calculation to RE developer (without Profitus)
  Scenario: Payment from payment schedule is coming
    Given Investments quarterly list in sheet 4
    When Comes date of the payment from quarterly payments schedule (2019-12-31)
    Then RE developer pays 13808.42 EUR of quarterly interest

