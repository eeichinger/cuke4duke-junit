package helloworld.steps;

import cuke4duke.annotation.I18n.EN.When;
import org.springframework.beans.factory.DisposableBean;

public class WhenSteps implements DisposableBean {

    private final ScenarioContext context;

    public WhenSteps(ScenarioContext context) {
        this.context = context;
    }

    public void destroy() throws Exception {
        // whatever you need to do at the end of a scenario
    }

    @When("^The Subject is (.*)$")
    public void setSubject(String subject) {
        context.currentSubject = subject;
    }
}
