@Message
Feature: Sending and Retrieving Messages

  Background: Set up user
    Given I add a user to the DB with username "mary@email.com" and password "Password123!"
    Given I add a user to the DB with username "john@email.com" and password "1234"
    Given an authenticated user with username "john@email.com" and password "1234" logs into the system

  Scenario: User sends message to another user
    When the user sends a message to another user
    Then the message should be created successfully

  Scenario: User edits their message
    And the user creates a message with content "I enjoy learn English"
    When the user edits the text for the message to "I enjoy learning English"
    Then the message should have its text updated successfully across the whole system

  Scenario: User not able to edit someone else's message
    And the user creates a message with content "I enjoy learn English"
    Then an authenticated user with username "mary@email.com" and password "Password123!" logs into the system
    When the authenticated user attempts to edit the message that belongs to user "John"
    Then the system should block the user with a forbidden error

  Scenario: User deletes their own message
    And the user creates a message with content "I enjoy learning English"
    When the user deletes the message
    And the message should no longer exist in the system

  Scenario: User can't delete someone else's message
    And the user creates a message with content "I enjoy learn English"
    Then an authenticated user with username "mary@email.com" and password "Password123!" logs into the system
    Then the user attempts to delete the message
    And the message should still exist in the system
