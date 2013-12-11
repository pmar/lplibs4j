package org.lplibs4j;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lplibs4j.api.solver.LinearProgramSolver;
import org.lplibs4j.solver.constraints.LinearSmallerThanEqualsConstraint;
import org.lplibs4j.solver.lpsolver.SolverFactory;
import org.lplibs4j.solver.problems.LinearProgram;

// import org.lplibs4j.api.problems.constraints.LinearBiggerThanEqualsConstraint;

public class SolverFactoryTest extends TestCase {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testSolveVerySmallLP() {

        /**
         * Taken from R. Fourer, D. M. Gay, B. W. Kernighan, "AMPL", Thomson Brooks/Cole, 2006
         */
        LinearProgram lp = new LinearProgram(new double[]{25.0, 30.0});
        lp.addConstraint(new LinearSmallerThanEqualsConstraint(new double[]{(1.0/200.0), (1.0/140.0)}, 40, "Time"));
        lp.setLowerbound(new double[]{0.0, 0.0});
        lp.setUpperbound(new double[]{6000, 4000});
        lp.setInteger(0);
        lp.setInteger(1);

        // System.out.print(lp.convertToCPLEX());

        LinearProgramSolver lpsolver = SolverFactory.newDefault();

        // This line should hold, if no service providing jar is provided
//		Assert.assertEquals(lpsolver, null);

        // The following four lines should hold, if a functioning service providing jar is provided
        double[] solution = lpsolver.solve(lp);
        double[] expected = {6000.0, 1400.0};
        for (int i = 0; i < solution.length; i++)
            Assert.assertEquals(expected[i], solution[i], 0.0);

        System.out.println("New method invocation!");
        LinearProgramSolver lpsolver2 = SolverFactory.newDefault();
        System.out.println("What is going on here?");

    }

}
