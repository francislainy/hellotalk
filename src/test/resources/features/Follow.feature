@Follow
Feature: Following/Follower relationships

  Background: Set up user
    Given I add a user to the DB with username "mary@email.com" and password "Password123!"
    Given I add a second user to the DB with username "john@email.com" and password "1234"
    Given an authenticated user with username "john@email.com" and password "1234" logs into the system

  Scenario: User can follow another user
    When the authenticated user triggers the request to follow another user
    Then The creation request is successful
    And the follower user should have their list of users they follow updated to include the user they are following
    And the followed user should have their list of followers updated to include the new follower
    And I delete the following relationship

  Scenario: User can stop following another user
    When the authenticated user triggers the request to follow another user
    Then The creation request is successful
    And the authenticated user triggers the request to stop following the other user
    And the follower user should have their list of users they follow updated to not include the user they are no longer following
    And the followed user should have their list of followers updated to not include the new follower


