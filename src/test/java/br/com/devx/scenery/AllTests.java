package br.com.devx.scenery;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {
    public static Test suite() {
        TestSuite result = new TestSuite();
        result.addTestSuite(ObjectWrapperTest.class);
        result.addTestSuite(TemplateAdapterTest.class);
        result.addTestSuite(TemplateFormatStrategyTest.class);
        result.addTest(br.com.devx.scenery.parser.AllTests.suite());

        return result;
    }
}
