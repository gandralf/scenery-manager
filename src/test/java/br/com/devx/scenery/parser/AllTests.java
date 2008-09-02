package br.com.devx.scenery.parser;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {
    public static Test suite() {
        TestSuite result = new TestSuite();
        result.addTestSuite(SceneryParserTest.class);

        return result;
    }
}
