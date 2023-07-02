Feature: Validate the user related endpoints

  Background: Set up user
    Given I add a user to the DB

  Scenario: Validate fields for get users endpoint
    Given I access the get users endpoint
    And I get a 200 successful response
    And The response has all the expected fields for the get users endpoint

  Scenario: Validate fields for get users endpoint against
    Given I access the users DB data
    Given I access the get users endpoint
    And I get a 200 successful response
    And I validate the response for the get users endpoint against the database

#    this fails as the steps are in the moment class
  Scenario: User creates moment and that gets added to his existing list of moments
    And the user creates a moment with some basic and simple content
    Then the moment should be created successfully
    And the user should be able to see the moment in their list of moments



