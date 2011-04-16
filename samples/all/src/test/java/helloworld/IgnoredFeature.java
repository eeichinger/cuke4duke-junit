package helloworld;

import org.junit.Ignore;
import org.oaky.cuke4duke.FeatureConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

@Ignore
@FeatureConfiguration("features/HelloWorld.feature")
@ContextConfiguration("HelloWorldFeature-context.xml")
@Transactional
public class IgnoredFeature {

    @Autowired
    MessageSource ms;

    public IgnoredFeature() {
        throw new UnsupportedOperationException("should never be invoked!");
    }
}
