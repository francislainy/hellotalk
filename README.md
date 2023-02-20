# HelloTalk

HelloTalk is a Spring Boot API designed to feed a language learning system similar to the HelloTalk app. It allows users to interact with other users who are native in the language they are interested in learning but  are also learning the language their partner is native in. For example, a Brazilian person learning Chinese can interact with Chinese users who are learning Portuguese.

# Audience

This project is for anyone interested in checking my skills in Java and Spring Boot.

# Technology Stack

The API is built using Java and follows the Spring MVC approach. It was built using Maven and uses a Test-Driven Development (TDD) approach, with Pact tests being written before new functionality is added.

# Getting Started
To clone the repository and run the project, follow these steps:

- Make sure you have Java 17 installed.

- Clone the repository: git clone https://github.com/francislainy/HelloTalk.git

- Navigate to the project root directory: cd HelloTalk

- Build the project: `mvn clean install`

- Run the project: `mvn spring-boot:run`

# Tests and Static Analysis

## Unit Tests:
`mvn test`

## Functional tests:

`mvn -Dtest="functionaltests.*IT" integration-test`

## Pact:

### Consumer:
`mvn -Dtest="pact.consumer.*IT" integration-test`

### Provider:
`mvn -Dtest="pact.provider.*IT" integration-test`

## Sonarqube

`mvn clean sonar:sonar`

## Spotless

`mvn spotless:apply`

# Feedback

Feedback and suggestions for improving this project are very welcome. Feel free to open an issue or pull request on the project's Github repository.

# Disclaimer

HelloTalk is a playground app built for learning purposes and as a portfolio of my skills. While it mimics some of the functionality of the HelloTalk app, it is not intended for commercial use.
