package helloworld;

import org.junit.runner.RunWith;
import org.oaky.cuke4duke.Cuke4DukeJUnit4Runner;
import org.oaky.cuke4duke.FeatureConfiguration;
import org.springframework.test.context.ContextConfiguration;

@RunWith(Cuke4DukeJUnit4Runner.class)
@FeatureConfiguration("features/HelloWorld.feature:12")
@ContextConfiguration("HelloWorldFeature-context.xml")
public class HelloWorldSingleScenarioFeature {


}
