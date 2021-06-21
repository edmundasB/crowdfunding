Feature: Last check before the interest payment
  Scenario: Any payment from payment schedule is coming
    Given Investors list for checking in sheet 1
    When Interest payment is initiated by RE developer
    And Sum of all investors interest repayments and sum of RE developer interest payments is equal
    Then Interest payment is successfully completed