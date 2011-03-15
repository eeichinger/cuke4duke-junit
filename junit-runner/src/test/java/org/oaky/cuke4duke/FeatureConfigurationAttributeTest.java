package org.oaky.cuke4duke;

import cuke4duke.internal.jvmclass.SpringFactory;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class FeatureConfigurationAttributeTest {
    @Test
    public void should_read_properties_from_classpath_local_overriding_global() {
        FeatureConfigurationAttribute fca = new FeatureConfigurationAttribute(this.getClass());
        assertEquals("GEMHOME_PATH_FROM_LOCALPROPERTIESFILE", fca.getGemHome());
        assertEquals(SpringFactory.class, fca.getObjectFactoryClass());
    }
}
