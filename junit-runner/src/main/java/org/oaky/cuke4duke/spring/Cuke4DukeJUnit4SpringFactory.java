package org.oaky.cuke4duke.spring;

import cuke4duke.StepMother;
import cuke4duke.internal.jvmclass.ObjectFactory;
import org.oaky.cuke4duke.Cuke4DukeJUnit4Runner;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.test.context.TestContextManager;

import java.util.ArrayList;
import java.util.List;

public class Cuke4DukeJUnit4SpringFactory implements ObjectFactory {
    private Cuke4DukeTestContextManager tcm;
    private StaticApplicationContext appContext;
    private final List<Class<?>> classes = new ArrayList<Class<?>>();
    private final List<Object> instances = new ArrayList<Object>();

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
        tcm = new Cuke4DukeTestContextManager(Cuke4DukeJUnit4Runner.getCurrentFeatureClass());
        appContext = new StaticApplicationContext();
        appContext.getBeanFactory().addBeanPostProcessor(new TestContextBeanPostProcessor(tcm));
        for (Class<?> clazz : classes) {
            BeanDefinition bd = BeanDefinitionBuilder.genericBeanDefinition(clazz)
                    .setLazyInit(true)
                    .getBeanDefinition();
            appContext.registerBeanDefinition(clazz.getName() + "[" + this.hashCode() + "]", bd);
        }
        for (Object instance : instances) {
            appContext.getBeanFactory().registerSingleton(instance.getClass().getName() + "[" + this.hashCode() + ", instance:" + instance.hashCode() + "]", instance);
        }
        appContext.refresh();
        try {
            tcm.beforeTestMethod();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void disposeObjects() {
        try {
            tcm.afterTestMethod();
            appContext.close();
        } finally {
            tcm = null;
            appContext = null;
        }
    }

    public boolean canHandle(Class<?> clazz) {
        return !clazz.getName().startsWith(this.getClass().getPackage().getName());
//        return true;
    }

    public void addClass(Class<?> clazz) {
        classes.add(clazz);
    }

    public void addStepMother(StepMother instance) {
        instances.add(instance);
    }

    /*
        public <T> T getComponent(Class<T> type) {
            return pico.getComponent(type);
        }
    */
    public <T> T getComponent(Class<T> type) {
        List beans = new ArrayList(appContext.getBeansOfType(type).values());
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
