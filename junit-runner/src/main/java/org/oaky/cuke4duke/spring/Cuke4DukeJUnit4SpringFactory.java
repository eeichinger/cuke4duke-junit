package org.oaky.cuke4duke.spring;

import cuke4duke.StepMother;
import cuke4duke.annotation.I18n;
import cuke4duke.internal.jvmclass.ObjectFactory;
import org.oaky.cuke4duke.Cuke4DukeJUnit4Runner;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.test.context.TestContextManager;

import java.util.ArrayList;
import java.util.List;

public class Cuke4DukeJUnit4SpringFactory implements ObjectFactory {

    private Cuke4DukeTestContextManager tcm;
    private DefaultListableBeanFactory beanFactory;
    private final List<Class<?>> classes = new ArrayList<Class<?>>();
    private final List<Object> instances = new ArrayList<Object>();
    private final CukeAnnotationTester annotationTester = new CukeAnnotationTester();
    
    private static class TestContextBeanPostProcessor extends InstantiationAwareBeanPostProcessorAdapter {
        private final TestContextManager tcm;

        private TestContextBeanPostProcessor(TestContextManager tcm) {
            this.tcm = tcm;
        }

        @Override
        public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
            try {
                tcm.prepareTestInstance(bean);
            } catch (Exception e) {
                throw new BeanInitializationException("failed to process test object instance", e);
            }
            return super.postProcessAfterInstantiation(bean, beanName);    //To change body of overridden methods use File | Settings | File Templates.
        }
    }

    public Cuke4DukeJUnit4SpringFactory() {
    }

    public void createObjects() {
        Class currentFeatureClass = Cuke4DukeJUnit4Runner.getCurrentFeatureClass();
        tcm = new Cuke4DukeTestContextManager(currentFeatureClass);

        beanFactory = new DefaultListableBeanFactory();
        beanFactory.addBeanPostProcessor(new TestContextBeanPostProcessor(tcm));

        for (Class<?> clazz : classes) {
            registerBean(clazz);
        }
        for (Object instance : instances) {
            beanFactory.registerSingleton(instance.getClass().getName() + "[" + this.hashCode() + ", instance:" + instance.hashCode() + "]", instance);
        }
//        beanFactory.refresh();
        try {
            tcm.beforeTestMethod();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void registerBean(Class<?> clazz) {
        BeanDefinition bd = BeanDefinitionBuilder.genericBeanDefinition(clazz)
                .setLazyInit(true)
                .getBeanDefinition();
        beanFactory.registerBeanDefinition(clazz.getName() + "[" + this.hashCode() + "]", bd);
    }

    public void disposeObjects() {
        try {
            tcm.afterTestMethod();
            beanFactory.destroySingletons();
        } finally {
            tcm = null;
            beanFactory = null;
        }
    }

    public boolean canHandle(Class<?> clazz) {
//        return !clazz.getName().startsWith(this.getClass().getPackage().getName());
        return annotationTester.isMatch(clazz);
    }

    public void addClass(Class<?> clazz) {
        classes.add(clazz);
    }

    public void addStepMother(StepMother instance) {
        instances.add(instance);
    }

    public <T> T getComponent(Class<T> type) {
        List beans = new ArrayList(beanFactory.getBeansOfType(type).values());
        if (beans.size() == 1) {
            T testInstance = (T) beans.get(0);
            return testInstance;
        } else {
            throw new RuntimeException("Found " + beans.size() + " Beans for class " + type + ". Expected exactly 1.");
        }
    }

    public List<Class<?>> getClasses() {
        return classes;
    }
}
