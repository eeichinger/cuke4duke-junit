package helloworld;

import cuke4duke.annotation.I18n.EN.Given;
import cuke4duke.annotation.I18n.EN.Then;
import cuke4duke.annotation.I18n.EN.When;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;

import static org.junit.Assert.assertEquals;

public class HelloWorldSteps {

    public static class HelloWorldContext {
        String currentGreeting;
        String currentSubject;
    }

    private final HelloWorldContext helloWorldContext;

    private HelloWorldContext getContext() {
        return helloWorldContext;
    }

    @Autowired
    private DataSource ds;

    private final HelloWorldService helloWorldService;

    @Autowired
    public HelloWorldSteps(HelloWorldService helloWorldService) {
        this.helloWorldContext = new HelloWorldContext();
        this.helloWorldService = helloWorldService;
    }

    @Given("^The Greeting is (.*)$")
    public void setGreeting(String greeting) {
        getContext().currentGreeting = greeting;
    }

    @When("^The Subject is (.*)$")
    public void setSubject(String subject) {
        getContext().currentSubject = subject;
    }

    @Then("^The Message is (.*)$")
    public void assertMessageEquals(String expectedGreeting) {
        String actualMsg = helloWorldService.getMessage(getContext().currentGreeting, getContext().currentSubject);
        assertEquals(expectedGreeting, actualMsg);
    }
}
