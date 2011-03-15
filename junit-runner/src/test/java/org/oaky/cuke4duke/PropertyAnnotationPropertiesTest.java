package org.oaky.cuke4duke;

import cuke4duke.internal.jvmclass.SpringFactory;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class PropertyAnnotationPropertiesTest {

    @FeatureConfiguration(value = "myValue", objectFactory = SpringFactory.class, strict = "false")
    private static class AnnotatedTestClass {}

    @Test
    public void should_use_propertynames_from_propertyannotation() {
        Object annotationInstance =
                AnnotatedTestClass.class.getAnnotation(FeatureConfiguration.class);
        PropertyAnnotationProperties props = new PropertyAnnotationProperties(annotationInstance, null);

        assertEquals(3, props.size());
        assertEquals("myValue", props.get("cuke4duke.featureName") );
        assertEquals(SpringFactory.class, props.get("cuke4duke.objectFactory"));
        assertEquals("false", props.get("cuke4duke.strict"));
    }
}
