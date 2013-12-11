package org.lplibs4j;

import org.junit.*;
import org.lplibs4j.solver.problems.LinearProgram;

public class LinearProgramTest {

    @Test
    public void testEvaluate() {
        LinearProgram lp = new LinearProgram(new double[]{1.0, 1.0});
        double result = lp.evaluate(new double[]{1.0, 1.0});
        Assert.assertEquals("Evaluation is wrong: ", 2.0, result, 0.00001);
        result = lp.evaluate(new double[]{0.0, 0.0});
        Assert.assertEquals("Evaluation is wrong: ", 0.0, result, 0.00001);
    }
}
