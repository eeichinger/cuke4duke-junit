package org.oaky.cuke4duke.spring;

import cuke4duke.annotation.Before;
import cuke4duke.annotation.I18n;
import cuke4duke.annotation.I18n.EN.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;

/**
 * TODO: describe purpose of class/interface
 */
public class CukeAnnotationTesterTest {

    public static class TestClass1 {}

    public static class TestClass2 {
        @When("")
        public void method() {}
    }

    public static class TestClass3 extends TestClass2 {
    }

    @RunWith(Runner.class)
    public static class TestClass4 {
        @When("")
        public void method() {}
    }
    @Test
    public void should_reject_class_without_any_annotation() throws Exception {
        assertRejects(TestClass1.class);
    }

    @Test
    public void should_identify_class_with_methodannotation() throws Exception {
        assertAccepts(TestClass2.class);
    }

    @Test
    public void should_identify_class_with_inherited_methodannotation() throws Exception {
        assertAccepts(TestClass3.class);
    }

    @Test
    public void should_reject_method_with_RunWith_annotation() throws Exception {
        assertRejects(TestClass4.class);
    }

    private static void assertAccepts(Class clazz) {
        CukeAnnotationTester tester = new CukeAnnotationTester();
        boolean result = tester.isMatch(clazz);
        Assert.assertTrue(result);
    }

    private static void assertRejects(Class clazz) {
        CukeAnnotationTester tester = new CukeAnnotationTester();
        boolean result = tester.isMatch(clazz);
        Assert.assertFalse(result);
    }
}
