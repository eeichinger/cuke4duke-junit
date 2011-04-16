package helloworld;

import cuke4duke.annotation.I18n.EN.Given;
import cuke4duke.annotation.I18n.EN.Then;
import cuke4duke.annotation.I18n.EN.When;

import static org.junit.Assert.assertEquals;

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
