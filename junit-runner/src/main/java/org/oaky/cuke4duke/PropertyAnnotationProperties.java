package org.oaky.cuke4duke;

import java.lang.reflect.Method;
import java.util.Properties;

public class PropertyAnnotationProperties extends Properties {

    public PropertyAnnotationProperties(Object instance, Properties defaultValues) {
        if (defaultValues != null) {
            super.putAll(defaultValues);
        }

        if (instance == null) return;

        Class<? extends Object> aClass = instance.getClass();
        parseMethods(instance, aClass);
        for(Class intf:aClass.getInterfaces()) {
            parseMethods(instance, intf);
        }
    }

    private void parseMethods(Object instance, Class<? extends Object> aClass) {
        for(Method m: aClass.getMethods()) {
            Property p = m.getAnnotation(Property.class);
            if (p != null) {
                try {
                    Object val = m.invoke(instance);
                    // TODO: handling non-null defaults probably need improvement
                    if (val instanceof String && !Check.hasText((String) val)) continue;
                    if (val instanceof Class && val == Object.class) continue;
                    this.put(p.value(), val);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
