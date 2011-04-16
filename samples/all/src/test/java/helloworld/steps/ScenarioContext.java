package helloworld.steps;

import org.springframework.beans.factory.DisposableBean;

public class ScenarioContext implements DisposableBean {
    String currentGreeting;
    String currentSubject;

    public ScenarioContext() {
        System.out.println("ScenarioContext.init<>()");
    }

    public void destroy() throws Exception {
        System.out.println("ScenarioContext.destroy()");
    }
}
