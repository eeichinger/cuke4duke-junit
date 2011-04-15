package org.oaky.cuke4duke.spring;

import org.junit.runner.RunWith;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

/**
 */
public class CukeAnnotationTester {

    private final String packageName;

    public CukeAnnotationTester() {
        packageName = cuke4duke.annotation.I18n.class.getName();
    }

    public boolean isMatch(Class clazz) {
        boolean result = clazz.getAnnotation(RunWith.class) != null;
        if (result) return false;
        
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
