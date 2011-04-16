package helloworld;

import cuke4duke.annotation.I18n.EN.Given;
import cuke4duke.annotation.I18n.EN.Then;
import cuke4duke.annotation.I18n.EN.When;

import static org.junit.Assert.assertEquals;

public class HelloWorldSteps {

    public static class HelloWorldScenarioContext {
        String currentGreeting;
        String currentSubject;
    }

    private final HelloWorldScenarioContext helloWorldScenarioContext;
    private final HelloWorldService helloWorldService;

    public HelloWorldSteps(HelloWorldService helloWorldService) {
        this.helloWorldService = helloWorldService;
        this.helloWorldScenarioContext = new HelloWorldScenarioContext();
    }

    private HelloWorldScenarioContext getContext() {
        return helloWorldScenarioContext;
    }

    @Given("^The Greeting is '(.*)'$")
    public void setGreeting(String greeting) {
        getContext().currentGreeting = greeting;
    }

    @When("^The Subject is '(.*)'$")
    public void setSubject(String subject) {
        getContext().currentSubject = subject;
    }

    @Then("^The Message is '(.*)'$")
    public void assertMessageEquals(String expectedGreeting) {
        String actualMsg = helloWorldService.getMessage(getContext().currentGreeting, getContext().currentSubject);
        assertEquals(expectedGreeting, actualMsg);
    }
}
