Feature: Validate the Hello Talk endpoints

  Scenario: Validate fields for users endpoint
    Given I access the users endpoint
    Then I get a 200 successful response
    And The response has all the expected fields for the users endpoint

  Scenario: Validate fields for users endpoint against database
    Given I access the users data from the database for a list of users
    When I access the users endpoint
    Then I get a 200 successful response
    And I validate the api data against the DB for the users endpoint
