package com.sm.mvn;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest {

    private App app;

    @Before
    public void setUp() throws Exception {

        app = new App();
    }

    @Test
    public void testReadPOMFile() throws Exception {

        app.readPOMfile("src/test/resources/sample_pom.xml");
    }

}
