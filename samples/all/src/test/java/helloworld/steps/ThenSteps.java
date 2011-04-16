package helloworld.steps;

import cuke4duke.annotation.I18n.EN.*;
import helloworld.HelloWorldService;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;

public class ThenSteps implements DisposableBean {

    private final ScenarioContext context;
    private final HelloWorldService helloWorldService;

    @Autowired
    public ThenSteps(HelloWorldService helloWorldService, ScenarioContext scenarioContext) {
        this.context = scenarioContext;
        this.helloWorldService = helloWorldService;
    }

    public void destroy() throws Exception {
        // whatever you need to do at the end of a scenario
    }

    @Then("^The Message is (.*)$")
    public void assertMessageEquals(String expectedGreeting) {
        String actualMsg = helloWorldService.getMessage(context.currentGreeting, context.currentSubject);
        assertEquals(expectedGreeting, actualMsg);
    }
}
