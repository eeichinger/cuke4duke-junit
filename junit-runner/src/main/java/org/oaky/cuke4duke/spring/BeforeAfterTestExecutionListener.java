package org.oaky.cuke4duke.spring;

import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class BeforeAfterTestExecutionListener extends AbstractTestExecutionListener {
    
    @Override
    public void afterTestMethod(TestContext testContext) throws Exception {
        Object instance = testContext.getTestInstance();
        Method after = getMethod(instance.getClass(), org.junit.After.class);
        if (after != null) {
            after.invoke(instance);
        }
    }

    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception {
        Object instance = testContext.getTestInstance();
        Method before = getMethod(instance.getClass(), org.junit.Before.class);
        if (before != null) {
            before.invoke(instance);
        }
    }

    private Method getMethod(Class clazz, Class<? extends Annotation> annotationType) {
        for(Method ae:clazz.getMethods()) {
            boolean result = ae.isAnnotationPresent(annotationType);
            if (result) return ae;
        }
        return null;
    }
}
