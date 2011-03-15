package org.oaky.cuke4duke;

import cuke4duke.internal.jvmclass.SpringFactory;
import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class TypedPropertiesTest {
    @Test
    public void should_return_typed_value() {
        Properties underlyingProperties = new Properties();
        underlyingProperties.put("rawBoolean", false);
        underlyingProperties.put("booleanString", "true");
        underlyingProperties.put("classByName", SpringFactory.class.getName());
        underlyingProperties.put("rawClass", SpringFactory.class);
        underlyingProperties.put("simpleString", "stringvalue");
        
        TypedProperties props = new TypedProperties(underlyingProperties);

        assertEquals("stringvalue", props.getString("simpleString", null));
        assertEquals(SpringFactory.class, props.getClass("classByName", null));
        assertEquals(SpringFactory.class, props.getClass("rawClass", null));
        assertEquals(Boolean.FALSE, props.getBoolean("rawBoolean", null));
        assertEquals(Boolean.TRUE, props.getBoolean("booleanString", null));
    }
}
