package helloworld;

import org.junit.Ignore;
import org.oaky.cuke4duke.FeatureConfiguration;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

@Ignore
@FeatureConfiguration("features/IgnoredFeature.feature")
@ContextConfiguration("HelloWorldFeature-context.xml")
@Transactional
public class IgnoredFeature implements BeanFactoryPostProcessor {

    @Autowired
    Object ms;

    public IgnoredFeature() {
    }

    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        throw new UnsupportedOperationException("spring should never instantiate even infrastructure classes from the --require path");
    }
}
