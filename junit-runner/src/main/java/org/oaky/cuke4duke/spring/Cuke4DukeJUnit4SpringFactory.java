package org.oaky.cuke4duke.spring;

import cuke4duke.StepMother;
import cuke4duke.annotation.I18n;
import cuke4duke.internal.jvmclass.ObjectFactory;
import org.oaky.cuke4duke.Cuke4DukeJUnit4Runner;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
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
    
    private static class TestContextBeanPostProcessor extends
            AutowiredAnnotationBeanPostProcessor {
//            InstantiationAwareBeanPostProcessorAdapter {
        private final Cuke4DukeTestContextManager tcm;

        private TestContextBeanPostProcessor(Cuke4DukeTestContextManager tcm, BeanFactory beanFactory) {
            super.setBeanFactory(beanFactory);
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

        BeanFactory parentBeanFactory = tcm.getContext().getApplicationContext().getAutowireCapableBeanFactory();
        beanFactory = new DefaultListableBeanFactory(parentBeanFactory);
        beanFactory.addBeanPostProcessor(new TestContextBeanPostProcessor(tcm, beanFactory));

        for (Class<?> clazz : classes) {
            registerBean(clazz);
        }
        for (Object instance : instances) {
            beanFactory.registerSingleton(instance.getClass().getName() + "[" + this.hashCode() + ", instance:" + instance.hashCode() + "]", instance);
        }

        try {
            tcm.beforeTestMethod();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void registerBean(Class<?> clazz) {
        BeanDefinition bd = BeanDefinitionBuilder.genericBeanDefinition(clazz)
                .setLazyInit(true)
                .setAutowireMode(AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR)
                .getBeanDefinition();
        beanFactory.registerBeanDefinition(clazz.getName() + "[" + this.hashCode() + "]", bd);
    }

    public void disposeObjects() {
        try {
            beanFactory.destroySingletons();
        } finally {
            beanFactory = null;
        }
        try {
            tcm.afterTestMethod();
        } finally {
            tcm = null;
        }
    }

    public boolean canHandle(Class<?> clazz) {
        // we're only interested in Step implementations
//        return annotationTester.isMatch(clazz);
        return true;
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
