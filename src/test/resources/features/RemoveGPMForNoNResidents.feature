Feature: Eliminating ‘GPM’ taxes before the interest payment
  Scenario: There is non-residents investors among project investors
    Given Investors list with not resident investor in sheet 2
    When Comes date of the payment (2019-02-28)
    Then Non-resident Investor 100 receives interest without GPM taxes 9.92 EUR