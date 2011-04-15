package org.oaky.cuke4duke.spring;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

/**
 */
public class AnnotationTester {

    private final String packageName;

    public AnnotationTester(Class packageClass) {
        packageName = packageClass.getPackage().getName();
    }

    public boolean isMatch(Class clazz) {
        boolean result = this.hasAnyAnnotationWithinPackage(clazz);
        if (result) return true;
        for(AnnotatedElement ae:clazz.getMethods()) {
            result = this.hasAnyAnnotationWithinPackage(ae);
            if (result) return true;
        }
        return false;
    }

    private boolean hasAnyAnnotationWithinPackage(AnnotatedElement ae) {
        for(Annotation ann: ae.getAnnotations()) {
            if (ann.annotationType().getName().startsWith(packageName)) {
                return true;
            }
        }
        return false;
    }
}
