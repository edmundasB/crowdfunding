Feature: Interest for early partial loan repayment calculation (for RE developer)
Scenario: RE developer repays part of loan early
Given Period for which interest is calculated 62 days, amount of partial repayment 35000.00 €
When RE developer informs about repayment
Then RE developer pays 630.38 € of interests with repayment


  Scenario: RE developer repays part of loan early
    Given Period for which interest is calculated 62 days, amount of partial repayment 35000.00 €
    When RE developer informs about repayment
    Then User 9 receives 71.58 €