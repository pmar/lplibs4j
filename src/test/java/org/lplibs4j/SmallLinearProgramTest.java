package org.lplibs4j;

import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lplibs4j.api.solver.LinearProgramSolver;
import org.lplibs4j.solver.constraints.LinearBiggerThanEqualsConstraint;
import org.lplibs4j.solver.constraints.LinearSmallerThanEqualsConstraint;
import org.lplibs4j.solver.lpsolver.SolverFactory;
import org.lplibs4j.solver.problems.LinearProgram;

public class SmallLinearProgramTest {

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

        LinearProgramSolver lpsolver = SolverFactory.getSolver("GLPK");

        double[] solution = lpsolver.solve(lp);
        double[] expected = {6000.0, 1400.0};
        for (int i = 0; i < solution.length; i++)
            Assert.assertEquals(expected[i], solution[i], 0.0);

    }

    @Test
    public void testSolveSmallLP() {

        /**
         * Taken from R. Fourer, D. M. Gay, B. W. Kernighan, "AMPL", Thomson Brooks/Cole, 2006
         * TODO: Seitenangabe
         */
        LinearProgram lp = new LinearProgram(new double[]{3.19, 2.59, 2.29, 2.89, 1.89, 1.99, 1.99, 2.49});
        lp.setMinProblem(Boolean.TRUE);
        lp.addConstraint(new LinearBiggerThanEqualsConstraint
                (new double[]{60.0, 8.0, 8.0, 40.0, 15.0, 70.0, 25.0, 60.0}, 700, "Vitamin A"));
        lp.addConstraint(new LinearBiggerThanEqualsConstraint
                (new double[]{20.0, 0.0, 10.0, 40.0, 35.0, 30.0, 50.0, 20.0}, 700, "Vitamin C"));
        lp.addConstraint(new LinearBiggerThanEqualsConstraint
                (new double[]{10.0, 20.0, 15.0, 35.0, 15.0, 15.0, 25.0, 15.0}, 700, "Vitamin B1"));
        lp.addConstraint(new LinearBiggerThanEqualsConstraint
                (new double[]{15.0, 20.0, 10.0, 10.0, 15.0, 15.0, 15.0, 10.0}, 700, "Vitamin B2"));

        lp.setLowerbound(new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0});

        LinearProgramSolver lpsolver = SolverFactory.getSolver("GLPK");

        double[] solution = lpsolver.solve(lp);
        double[] expected = {0.0, 0.0, 0.0, 0.0, 46.66667, -3.6916e-18, -4.0534e-16, 0.0};

        for(int i = 0; i < expected.length; i++)
            Assert.assertEquals(solution[i], expected[i], 0.0001);

    }

    @Test
    public void testSmallIntegerLP() {
        LinearProgram lp = new LinearProgram(new double[]{1., 1., 1.});
        lp.setMinProblem(Boolean.TRUE);

        lp.addConstraint(new LinearBiggerThanEqualsConstraint
                (new double[]{1., 0., 0.}, 5., "x_1 >= 5"));
        lp.addConstraint(new LinearBiggerThanEqualsConstraint
                (new double[]{0., 1., 0.}, 3., "x_2 >= 3"));
        lp.addConstraint(new LinearBiggerThanEqualsConstraint
                (new double[]{0., 0., 1.}, 4., "x_3 >= 4"));

        lp.setIsinteger(new boolean[]{Boolean.TRUE, Boolean.TRUE, Boolean.TRUE});

        LinearProgramSolver lpsolver = SolverFactory.getSolver("GLPK");

        double[] solution = lpsolver.solve(lp);
        double[] expected = {5., 3., 4.};

        for (int i = 0; i < expected.length; i++)
            Assert.assertEquals(solution[i], expected[i], 0.0001);

    }

    @Test
    public void testIntegerLP() {
        LinearProgram lp = new LinearProgram(new double[]{2., 0., 1., 0.5, 0.5});
        lp.setMinProblem(Boolean.TRUE);

        lp.setLowerbound(new double[]{1., 1., 1., 1., 1.});

        lp.addConstraint(new LinearBiggerThanEqualsConstraint
                (new double[]{0.5, 0., 0., 2., 0.}, 2., "x_1 + x_4 >= 2"));
        lp.addConstraint(new LinearSmallerThanEqualsConstraint
                (new double[]{0., 2., 0., 2., 1.}, 5., "2 + x_4 + 0.5*x_5 <= 5"));
        lp.addConstraint(new LinearBiggerThanEqualsConstraint
                (new double[]{0., 0., 3., 0., 10.}, 16., "3*x_3 + 5*x_5 >= 10"));

        for (int i = 0; i < lp.getDimension(); i++)
            lp.setInteger(i);

        LinearProgramSolver lpsolver = SolverFactory.getSolver("GLPK");

        double[] solution = lpsolver.solve(lp);
        double[] expected = new double[]{1., 1., 2., 1., 1.};

        for (int i = 0; i < lp.getDimension() || i < expected.length; i++)
            Assert.assertEquals(expected[i], solution[i], 0.0001);
    }

    @Test
    public void testSmallBooleanLP() {
        LinearProgram lp = new LinearProgram(new double[]{1., 2., 3., 4., 5.});

        lp.setMinProblem(Boolean.FALSE);
        lp.addConstraint(new LinearBiggerThanEqualsConstraint
                (new double[]{1., 1., 1., 1., 1.}, 3., "set at least 3"));
        lp.addConstraint(new LinearBiggerThanEqualsConstraint
                (new double[]{1., 1., 1., 0., 0.} , 1., "one of the first tree"));
        lp.addConstraint(new LinearBiggerThanEqualsConstraint
                (new double[]{0., 0., 1., 1., 1.}, 1., "one of the last tree"));
        lp.addConstraint(new LinearSmallerThanEqualsConstraint
                (new double[]{1., 5., 3., 2., 4.}, 9., "cool constraint"));

        for(int i = 0; i < lp.getDimension(); i++)
            lp.setBinary(i);

        LinearProgramSolver lpsolver = SolverFactory.getSolver("GLPK");

        double[] solution = lpsolver.solve(lp);
        double[] expected = new double[]{0., 0., 1., 1., 1.};

        for (int i = 0; i < expected.length; i++)
            Assert.assertEquals(expected[i], solution[i], 0.0001);

        for (double d : solution)
            Assert.assertTrue(d == 0. || d == 1.);

        Assert.assertEquals(14, lp.getConstraints().size());
    }

    @Test
    public void testBooleanLP() {
        LinearProgram lp = new LinearProgram(new double[]{1., 2., 3., 4., 5., 6., 7., 8., 9., 10.});

        lp.setMinProblem(Boolean.FALSE);

        lp.addConstraint(new LinearSmallerThanEqualsConstraint
                (new double[]{1., 1., 1., 1., 1., 1., 1., 1., 1., 1.}, 7., "max 7"));
        lp.addConstraint(new LinearBiggerThanEqualsConstraint
                (new double[]{1., 1., 1., 1., 0., 0., 0., 0., 0., 0.}, 1., "one of the 1"));
        lp.addConstraint(new LinearBiggerThanEqualsConstraint
                (new double[]{0., 0., 0., 0., 0., 0., 1., 1., 1., 1.}, 1., "one of the l"));
        lp.addConstraint(new LinearBiggerThanEqualsConstraint
                (new double[]{0., 0., 0., 1., 1., 1., 1., 0., 0., 0.}, 1., "one of the m"));
        lp.addConstraint(new LinearSmallerThanEqualsConstraint
                (new double[]{0., 0., 0., 1., 0., 0., 1., 0., 0., 0.}, 1., "only one in S"));
        lp.addConstraint(new LinearSmallerThanEqualsConstraint
                (new double[]{3., 3., 1., 0., 0., 0., 2., 0., 5., 5.}, 5., "cool constraint"));

        for(int i = 0; i < lp.getDimension(); i++)
            lp.setBinary(i);

        LinearProgramSolver lpsolver = SolverFactory.getSolver("GLPK");

        double[] solution = lpsolver.solve(lp);
        double[] expected = new double[]{0., 0., 0., 1., 1., 1., 0., 1., 0., 1.};

        for(int i = 0; i < expected.length; i++)
            Assert.assertEquals(expected[i], solution[i], 0.0001);

        Assert.assertEquals(26, lp.getConstraints().size());

    }

}
