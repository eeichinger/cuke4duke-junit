package org.oaky.cuke4duke;

import java.util.Properties;

public class TypedProperties {

    private final Properties properties;

    public TypedProperties(Properties properties) {
        Check.assertNotNull(properties, "properties are required");
        this.properties = properties;
    }

    public Class getClass(String propertyName, Class defaultValue) {
        if (this.properties.containsKey(propertyName)) {
            try {
                Object val = this.properties.get(propertyName);
                if (val instanceof Class) {
                    return (Class) val;
                }
                return Class.forName(""+val);
            } catch (Exception e) {
                throw new RuntimeException("Error accessing property '" + propertyName + "'", e);
            }
        }
        return defaultValue;
    }

    public Boolean getBoolean(String propertyName, Boolean defaultValue) {
        if (this.properties.containsKey(propertyName)) {
            Object val = this.properties.get(propertyName);
            try {
                if (val instanceof Boolean) {
                    return (Boolean) val;
                }
                return Boolean.valueOf(""+val);
            } catch (Exception e) {
                throw new RuntimeException("Error accessing property '" + propertyName + "'", e);
            }
        }
        return defaultValue;
    }

    public String getString(String propertyName, String defaultValue) {
        if (this.properties.containsKey(propertyName)) {
            return this.properties.getProperty(propertyName);
        }
        return defaultValue;
    }
}
