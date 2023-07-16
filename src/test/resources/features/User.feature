@User
Feature: Validate the user related endpoints

  Background: Set up user
    Given I add a user to the DB with username "mary@email.com" and password "Password123!"

  Scenario: Validate fields for get users endpoint
    Given I access the get users endpoint
    And I get a 200 successful response
    And The response has all the expected fields for the get users endpoint

  Scenario: Validate fields for get users endpoint against
    Given I access the users DB data
    Given I access the get users endpoint
    And I get a 200 successful response
    And I validate the response for the get users endpoint against the database




