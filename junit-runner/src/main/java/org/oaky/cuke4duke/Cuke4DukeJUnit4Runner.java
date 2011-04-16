package org.oaky.cuke4duke;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Cuke4DukeJUnit4Runner extends Runner {

    private final FeatureConfigurationAttribute fca;

    public Cuke4DukeJUnit4Runner(Class clazz) {
        fca = new FeatureConfigurationAttribute(clazz);
    }

    @Override
    public Description getDescription() {
        return Description.createTestDescription(fca.getFeatureClass(), fca.getFeatureFilename());
    }

    @Override
    public void run(RunNotifier runNotifier) {
        runNotifier.fireTestStarted(getDescription());
        
        try {
            JUnitRunnerCucumberTask task = new JUnitRunnerCucumberTask();
            String classpath = System.getProperty("java.class.path");
            task.setClasspath(classpath);
	        String gemHome = fca.getGemHome();
	        task.setGemHome(gemHome);

            String args = buildCommandlineArgs(task);
            task.setArgs(args);
            task.setObjectFactory(fca.getObjectFactoryClass());

            currentFeatureClassHolder.set(fca.getFeatureClass());
            task.execute();
        } catch (Throwable e) {
            runNotifier.fireTestFailure(new Failure(getDescription(), e));
        } finally {
            currentFeatureClassHolder.remove();
            runNotifier.fireTestFinished(getDescription());
        }
    }

    private String buildCommandlineArgs(JUnitRunnerCucumberTask task) {
        StringBuffer argsBuffer = new StringBuffer();
        requireClasspath(argsBuffer);

        if (fca.isStrict()) {
            argsBuffer.append(" --strict");
        }

        if (Check.hasText(fca.getTags())) {
            String[] tags = fca.getTags().split(" ");
            for(String tag:tags) {
                argsBuffer.append(" --tags " + tag);
            }
        }

        if (Check.hasText(fca.getCustomArguments())) {
            argsBuffer.append(" " + fca.getCustomArguments());
        }

        argsBuffer.append(" " + fca.getFeatureFilename());

        return argsBuffer.toString();
    }

    private void requireClasspath(StringBuffer argsBuffer) {
        List<String> classpathElements = new ArrayList<String>();

        String require = fca.getRequire();
        if (require != null && require.trim().length() > 0) {
            require = require.replace(';',':');
            String[] cpes = require.split(":");
            for(String cpe:cpes) {
                classpathElements.add(cpe);
            }
        } else {
            String classpath = System.getProperty("java.class.path");
            String[] cpes = classpath.split(File.pathSeparator);
            for (String classpathElement : cpes) {
                if (!classpathElement.endsWith(".jar")
                    && !classpathElement.contains("junit-runner"))
                    classpathElements.add(classpathElement);
            }
        }

        for (String classpathElement : classpathElements) {
            if (!classpathElement.endsWith(".jar")
                && !classpathElement.contains("junit-runner"))
                argsBuffer.append(" --require '").append(classpathElement).append("' ");
        }
    }

    private static ThreadLocal<Class> currentFeatureClassHolder =
            new ThreadLocal<Class>();

    public static Class getCurrentFeatureClass() {
        return currentFeatureClassHolder.get();
    }
}
