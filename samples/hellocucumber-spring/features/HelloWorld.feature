Feature: Integrate Cucumber with JUnit and Spring
  In order to reuse my Spring knowledge
  As a Spring developer
  I want to use Spring in my cucumber tests

Scenario: Spring integration prepares Step instance including invoking JUnit's Before annotation on Feature class
	Given The Greeting is 'Hello'
	When The Subject is 'Cucumber with Spring'
	Then The Message is 'Howdy and Hello, Cucumber with Spring'

Scenario: Spring integration also invokes JUnit's After annotation
	Given The Greeting is 'Hy'
	When The Subject is 'Cucumber with Spring'
	Then The Message is 'Howdy and Hy, Cucumber with Spring'
