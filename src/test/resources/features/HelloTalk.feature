Feature: Validate the Hello Talk endpoints

  Scenario: Validate fields for users endpoint
    Given I access the users endpoint
    Then I get a 200 successful response
    And The response has all the expected fields for the users endpoint
