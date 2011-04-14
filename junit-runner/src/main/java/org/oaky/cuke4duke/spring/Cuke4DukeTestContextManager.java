package org.oaky.cuke4duke.spring;

import org.springframework.test.context.TestContextManager;
import org.springframework.test.context.TestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public class Cuke4DukeTestContextManager extends TestContextManager {
    private final Class testClass;
    private final Object testInstance;
    private final Method testMethod;

    public Class getTestClass() {
        return testClass;
    }

    public Cuke4DukeTestContextManager(Class<?> testClass) {
        super(testClass);
        this.testClass = testClass;
        try {
            this.testInstance = testClass.newInstance();
            this.testMethod = testClass.getMethod("toString");
            applyTransactionAttributeSourceFix(getTestExecutionListeners());
            prepareTestInstance(this.testInstance);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * The default transaction annotation lookup logic doesnt pick up a @Transaction annotation on
     * a class without methods
     */
    private static void applyTransactionAttributeSourceFix(List<TestExecutionListener> testExecutionListeners) throws NoSuchFieldException, IllegalAccessException {
        for(int i=0;i<testExecutionListeners.size();i++) {
            TestExecutionListener testExecutionListener = testExecutionListeners.get(i);
            if (testExecutionListener instanceof TransactionalTestExecutionListener) {
                Class<? extends TestExecutionListener> testExecutionListenerClass = testExecutionListener.getClass();
                Field field = testExecutionListenerClass.getDeclaredField("attributeSource");
                field.setAccessible(true);
                field.set(testExecutionListener, new SimpleAnnotationTransactionAttributeSource());
                break;
            };
        }
    }

    @Override
    public void prepareTestInstance(Object testInstance) throws Exception {
        super.prepareTestInstance(testInstance);
    }

    public void beforeTestMethod() {
        try {
            super.beforeTestMethod(testInstance, testMethod);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void afterTestMethod() {
        try {
            super.afterTestMethod(testInstance, testMethod, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
