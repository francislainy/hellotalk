Feature: Moment Posting and Commenting

  Background: Set up user
    Given I add a user to the DB
#    Given an authenticated user with name "John" logs into the system

  Scenario: User creates moment and that gets added to his existing list of moments
    And the user creates a moment with some basic and simple content
    Then the moment should be created successfully
    And the user should be able to see the moment in their list of moments

#  Scenario: User creates a comment to a moment belonging to another user
#    And the user creates a moment with content "I enjoy learn English"
#    Then an authenticated user with name "John" and password "1234" logs into the system
#    And the user adds a comment on the moment with grammar correction "I enjoy learning English"
#    Then the comment should be added to the list of comments for that moment
#
#  Scenario: User likes and unlikes comment belonging to another user
#    Given the user creates a moment with content "I enjoy learning English"
#    Then an authenticated user with name "Mary" logs into the system
#    When the user likes the moment
#    Then the moment should receive a like from the user
#    And the total number of likes for the moment should increase
#    When the user removes his like for the moment
#    Then the moment should remove the like from the user
#    And the total number of likes for the moment should decrease
#
#  Scenario: User can like and unlike their own moment
#    Given the user creates a moment with content "I enjoy learning English"
#    When the user likes the moment
#    Then the moment should receive a like from the user
#    Then the moment should receive a like from the user
#    And the total number of likes for the moment should increase
#    When the user removes his like for the moment
#    Then the moment should remove the like from the user
#    And the total number of likes for the moment should decrease
#
#  Scenario: User edits their own moment
#    And an existing moment created by the user with content "I enjoy learn English"
#    When the user edits the moment to "I enjoy learning English"
#    Then the moment should be updated successfully
#
#  Scenario: User edits their own moment
#    And an existing moment created by the user with content "I enjoy learn English"
#    When the user edits the moment to "I enjoy learning English"
#    Then the moment should be updated successfully
#
#  Scenario: User not able to edit someone else's moment
#    And an existing moment created by the user with content "I enjoy learn English"
#    Given an authenticated user with name "Mary" logs into the system
#    When the user attempts to edit the moment that belongs to "Mary"
#    Then the moment should not be updated
#
#  Scenario: User deletes their own moment
#    And an existing moment created by the user "John" and with content "I enjoy learning English"
#    When the user deletes the moment
#    Then the moment should be deleted successfully
#    And the moment should no longer exist in the system
#
#  Scenario: User can't delete someone else's moment
#    And an existing moment created by the user "John" and with content "I enjoy learning English"
#    And an authenticated user with name "Mary" logs into the system
#    When the user deletes the moment
#    Then the moment should not be deleted
#    And the moment should still exist in the the system
#
