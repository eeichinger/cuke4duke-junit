Feature: Integrate Cucumber with JUnit
  In order to speed up our development of executable specifications
  As every stakeholder in this project
  I want to run cucumber as ordinary junit test

Scenario: Maven/Cucumber/Java successfully interact
	Given The Greeting is 'Hello'
	When The Subject is 'Cucumber'
	Then The Message is 'Hello, Cucumber'
