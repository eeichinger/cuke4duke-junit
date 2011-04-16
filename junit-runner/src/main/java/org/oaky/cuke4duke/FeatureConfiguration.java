package org.oaky.cuke4duke;

@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target({java.lang.annotation.ElementType.TYPE})
public @interface FeatureConfiguration {
    /**
     * specify the feature filename and optionally the scenario linenumber, e.g.<br>
     * <ul>
     *     <li>"features" to run all features within the features folder</li>
     *     <li>"features/HelloWorld.feature" to run all scenarios within this feature only</li>
     *     <li>"features/HelloWorld.feature:6" to run the HelloWorld scenario on line 6</li>
     * </ul>
     */
    @Property("cuke4duke.featureName")
    String value();
    @Property("cuke4duke.objectFactory")
    Class objectFactory() default Object.class;
    @Property("cuke4duke.strict")
    String strict() default "";
    @Property("cuke4duke.tags")
    String tags() default "";
    @Property("cuke4duke.require")
    String require() default "";
    @Property("cuke4duke.customArguments")
    String customArguments() default "";
}
