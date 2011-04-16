package helloworld.steps;

import cuke4duke.annotation.I18n.EN.*;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;

public class GivenSteps  implements DisposableBean {

    private final ScenarioContext context;

    @Autowired
    public GivenSteps(ScenarioContext context) {
        this.context = context;
    }

    public void destroy() throws Exception {
        // whatever you need to do at the end of a scenario
    }

    @Given("^The Greeting is (.*)$")
    public void setGreeting(String greeting) {
        context.currentGreeting = greeting;
    }
}
