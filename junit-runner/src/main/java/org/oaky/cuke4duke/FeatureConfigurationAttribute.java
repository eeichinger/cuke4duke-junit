package org.oaky.cuke4duke;

import org.apache.tools.ant.BuildException;
import org.jruby.embed.util.SystemPropertyCatcher;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import static org.jruby.util.URLUtil.getPath;

public class FeatureConfigurationAttribute {

    private static final String GLOBAL_RUNTIMEPROPERTIES_FILENAME = "cuke4dukejunitrunner.properties";

    private final Class featureClass;
    private final TypedProperties runtimeProperties;

    private final String featureName;
    private final String gemHome;

    private final Class objectFactoryClass;
    private final boolean strict;
    private final String tags;
    private final String customArguments;

    public FeatureConfigurationAttribute(Class clazz) {
        Check.assertNotNull(clazz, "clazz is a mandatory argument");
        featureClass = clazz;

        FeatureConfiguration cfg = (FeatureConfiguration) clazz.getAnnotation(FeatureConfiguration.class);
        Properties annotationProperties = new PropertyAnnotationProperties(cfg, loadRuntimeProperties(clazz));
        
        runtimeProperties = new TypedProperties(annotationProperties);

        featureName = runtimeProperties.getString("cuke4duke.featureName", calculateFeatureFileFromClass(clazz));
        gemHome = runtimeProperties.getString("jruby.gem.home", calculateJGemHome());
        objectFactoryClass = runtimeProperties.getClass("cuke4duke.objectFactory", null);
        strict = runtimeProperties.getBoolean("cuke4duke.strict", true);
        tags = runtimeProperties.getString("cuke4duke.tags", null);
        customArguments = runtimeProperties.getString("cuke4duke.customArguments", null);
    }

    public Class getFeatureClass() {
        return featureClass;
    }

    public Class getObjectFactoryClass() {
        return objectFactoryClass;
    }

    public String getFeatureFilename() {
        return featureName;
    }

    public boolean isStrict() {
        return strict;
    }

    public String getTags() {
        return tags;
    }

    public String getGemHome() {
        return gemHome;
    }

    public String getCustomArguments() {
        return customArguments;
    }

    protected String calculateFeatureFileFromClass(Class clazz) {
        String featureName = clazz.getSimpleName();
        if (featureName.endsWith("Feature")) {
            featureName =  featureName.substring(0, featureName.length() - "Feature".length());
        }
        return "features/" + featureName + ".feature";
    }

    protected String calculateJGemHome() {
//        String jruby_home = coalesce(System.getenv("GEM_HOME"), System.getenv("JRUBY_HOME"));
	    String jruby_home = findGemHome(this);
        return jruby_home;
    }

	public static String findGemHome(Object instance) {
	    String jrubyhome;
	    if ((jrubyhome = System.getenv("GEM_HOME")) != null) {
	        return jrubyhome;
	    } else if ((jrubyhome = System.getProperty("jruby.gem.home")) != null) {
	        return jrubyhome;
	    } else if ((jrubyhome = findFromJar(instance)) != null) {
	        return jrubyhome;
	    } else {
	        return null;
	    }
	}

	public static String findFromJar(Object instance) {
	    URL resource = instance.getClass().getResource("/META-INF/jruby.gem.home");
	    if (resource == null) {
	        return null;
	    }

	    String location = null;
	    if (resource.getProtocol().equals("jar")) {
	        location = getPath(resource);
	        if (!location.startsWith("file:") && !location.startsWith("/")) {
	            // for remote-sourced classpath resources, just use classpath:
	            location = "classpath:/META-INF/jruby.gem.home";
	        }
	    } else {
//		    location = "classpath:/META-INF/jruby.gem.home";
		    if (resource.getProtocol().equals("file")) {
			    location = new File(resource.getFile()).getAbsolutePath();
		    } else {
			    location = "classpath:/META-INF/jruby.gem.home";
		    }
	    }

	    // Trim trailing slash. It confuses OSGi containers...
	    if (location.endsWith("/")) {
	        location = location.substring(0, location.length() - 1);
	    }

	    return location;
	}

    protected Properties loadRuntimeProperties(Class clazz) {
        Properties props = new Properties();
        ClassLoader classLoader = clazz.getClassLoader();
        if (classLoader.getResource(GLOBAL_RUNTIMEPROPERTIES_FILENAME) != null) {
            loadPropertiesFromResource(props, classLoader, GLOBAL_RUNTIMEPROPERTIES_FILENAME);
        }
        String propertyFilename = clazz.getName().replace('.', '/') + "-cuke4dukejunitrunner.properties";
        if (classLoader.getResource(propertyFilename)!=null) {
            loadPropertiesFromResource(props, classLoader, propertyFilename);
        }
        return props;
    }

    private void loadPropertiesFromResource(Properties props, ClassLoader classLoader, String propertyFilename) {
        try {
            InputStream propsFile = classLoader.getResourceAsStream(propertyFilename);
            props.load(propsFile);
        } catch (Exception e) {
            throw new BuildException("Failed loading properties from " + propertyFilename, e);
        }
    }

    private static <T> T coalesce(T... values) {
        for(T val:values) {
            if (val != null) {
                return val;
            }
        }
        return null;
    }
}
