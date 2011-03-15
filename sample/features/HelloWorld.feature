Feature: Hello World Feature
  In order to ensure that my installation works
  As a Developer
  I want to run a quick Cucumber test

Scenario: Maven/Cucumber/Java successfully interact
	Given The Greeting is Hello
	When The Subject is World
	Then The Message is Hello, World

Scenario: A global scenario
	Given The Greeting is Hallo
	When The Subject is Welt
	Then The Message is Hallo, Welt