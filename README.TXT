
Cuke4Duke JUnit Runner

Author: Erich Eichinger
Home: http://github.com/eeichinger/cuke4duke-junit

This project provides a JUnit integration with Cuke4Duke, the popular Cucumber framework on the Java platform. Use it to run Cucumber features by standard JUnit4 technology, which allows for much easier developing and debugging features.

Here's a snippet taken from the examples:

// HelloWorldFeature.java
@RunWith(Cuke4DukeJUnit4Runner.class)
@FeatureConfiguration("features/HelloWorld.feature")
public class HelloWorldFeature {
}

// HelloWorld.feature
Scenario: Maven/Cucumber/Java successfully interact
	Given The Greeting is 'Hello'
	When The Subject is 'Cucumber'
	Then The Message is 'Hello, Cucumber'

// HelloWorldSteps.java
public class HelloWorldSteps {

    String currentGreeting;
    String currentSubject;

    private final HelloWorldService helloWorldService;

    public HelloWorldSteps(HelloWorldService helloWorldService) {
        this.helloWorldService = helloWorldService;
    }

    @Given("^The Greeting is '(.*)'$")
    public void setGreeting(String greeting) {
        currentGreeting = greeting;
    }

    @When("^The Subject is '(.*)'$")
    public void setSubject(String subject) {
        currentSubject = subject;
    }

    @Then("^The Message is '(.*)'$")
    public void assertMessageEquals(String expectedGreeting) {
        String actualMsg = helloWorldService.getMessage(currentGreeting, currentSubject);
        assertEquals(expectedGreeting, actualMsg);
    }
}
