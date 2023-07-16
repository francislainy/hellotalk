@Moment
Feature: Moment Posting and Commenting

  Background: Set up user
    Given I add a user to the DB with username "mary11@email.com" and password "Password123!"
    Given I add a user to the DB with username "john11@email.com" and password "1234"
#    Given an authenticated user with username "john@email.com" and password "1234" logs into the system

#  Scenario: User creates moment and that gets added to his existing list of moments
#    When the user creates a moment with content "I enjoy learn English"
#    Then the moment should be created successfully
#    And the user should be able to see the moment in their list of moments

  Scenario: User creates a comment to a moment belonging to another user
    When the user creates a moment with content "I enjoy learn English"
    And the user adds a comment to the moment with grammar correction "I enjoy learning English"
    Then the comment should be added to the list of comments for that moment
