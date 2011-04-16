package helloworld;

import org.junit.Assert;
import org.junit.runner.RunWith;
import org.oaky.cuke4duke.Cuke4DukeJUnit4Runner;
import org.oaky.cuke4duke.FeatureConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

@RunWith(Cuke4DukeJUnit4Runner.class)
@FeatureConfiguration("features/HelloWorld.feature")
@ContextConfiguration
public class HelloWorldFeature {

    @Autowired
    HelloWorldService helloWorldService;

    @org.junit.Before
    public void setupScenario() {
        Assert.assertNull(helloWorldService.getMessagePrefix());
        helloWorldService.setMessagePrefix("Howdy and ");
    }

    @org.junit.After
    public void teardownScenario() {
        helloWorldService.setMessagePrefix(null);
    }
}
