@Moment
Feature: Moment Posting and Commenting

  Background: Set up user
    Given I add a user to the DB with username "mary@email.com" and password "Password123!"
    Given I add a user to the DB with username "john@email.com" and password "1234"
    Given an authenticated user with username "john@email.com" and password "1234" logs into the system

  Scenario: User creates moment and that gets added to his existing list of moments
    When the user creates a moment with content "I enjoy learn English"
    Then the moment should be created successfully
    And the user should be able to see the moment in their list of moments

  Scenario: User creates a comment to a moment belonging to another user
    When the user creates a moment with content "I enjoy learn English"
    Given an authenticated user with username "mary@email.com" and password "1234" logs into the system
    And the user adds a comment to the moment with grammar correction "I enjoy learning English"
    Then the comment should be added to the list of comments for that moment

  Scenario: User likes and unlikes comment belonging to another user
    Given the user creates a moment with content "I enjoy learning English"
    Then an authenticated user with username "mary@email.com" and password "Password123!" logs into the system
    When the user with username "john@email.com" likes the moment
    Then the moment should indicate it has received a like from the user with username "john@email.com"
    And the total number of likes for the moment should increase by 1
    When the user removes his like for the moment
    And the total number of likes for the moment should decrease by 1

  Scenario: User can like and unlike their own moment
    Given the user creates a moment with content "I enjoy learning English"
    When the user with username "john@email.com" likes the moment
    Then the moment should indicate it has received a like from the user with username "john@email.com"
    And the total number of likes for the moment should increase by 1
    When the user removes his like for the moment
    And the total number of likes for the moment should decrease by 1

  Scenario: User edits their own moment
    And the user creates a moment with content "I enjoy learn English"
    When the user edits the text for the moment to "I enjoy learning English"
    Then the moment should have its text updated successfully across the whole system

  Scenario: User not able to edit someone else's moment
    And the user creates a moment with content "I enjoy learn English"
    Then an authenticated user with username "mary@email.com" and password "Password123!" logs into the system
    When the authenticated user attempts to edit the moment that belongs to user "John"
    Then the system should block the user with a forbidden error

  Scenario: User deletes their own moment
    And the user creates a moment with content "I enjoy learning English"
    When the user deletes the moment
    And the moment should no longer exist in the system

  Scenario: User can't delete someone else's moment
    And the user creates a moment with content "I enjoy learn English"
    Then an authenticated user with username "mary@email.com" and password "Password123!" logs into the system
    Then the user attempts to delete the moment
    And the moment should still exist in the system
