package org.lplibs4j;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        UtilsTest.class,
        LinearProgramTest.class,
        SmallLinearProgramTest.class,
        QuadraticProgramTest.class})
public class AllTests {

    @BeforeClass
    public static void setUpClasses() {
        System.out.println("Test Suite for org.lplibs4j.");
        System.out.println("Master up.");
    }

    @AfterClass
    public static void tearDownClasses() {
        System.out.println("Master down.");
    }

}
