package org.lplibs4j;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.junit.Test;
import org.lplibs4j.api.solver.LinearProgramSolver;
import org.lplibs4j.solver.constraints.LinearBiggerThanEqualsConstraint;
import org.lplibs4j.solver.constraints.LinearSmallerThanEqualsConstraint;
import org.lplibs4j.solver.constraints.StochasticBiggerThanEqualsConstraint;
import org.lplibs4j.solver.constraints.StochasticSmallerThanEqualsConstraint;
import org.lplibs4j.solver.lpsolver.SolverFactory;
import org.lplibs4j.solver.problems.LinearProgram;
import org.lplibs4j.solver.problems.StochasticProgram;

public class StochasticProgramTest extends TestCase {

    @Test
    public void testThis() {
        assertTrue(Boolean.TRUE);
        assertFalse(Boolean.FALSE);
    }

    @Test
    public void testFarmerExampleSimple() {
        LinearProgram lp = new LinearProgram(new double[]{150.0, 230.0, 260.0, 238.0, -170.0, 210.0, -150.0, -36.0, -10.0});
        lp.addConstraint(new LinearSmallerThanEqualsConstraint(
                new double[]{1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0}, 500.0, "Land"));
        lp.addConstraint(new LinearBiggerThanEqualsConstraint(
                new double[]{2.5, 0.0, 0.0, 1.0, -1.0, 0.0, 0.0, 0.0, 0.0}, 200.0, "Wheat"));
        lp.addConstraint(new LinearBiggerThanEqualsConstraint(
                new double[]{0.0, 3.0, 0.0, 0.0, 0.0, 1.0, -1.0, 0.0, 0.0}, 240.0, "Corn"));
        lp.addConstraint(new LinearSmallerThanEqualsConstraint(
                new double[]{0.0, 0.0, -20.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0}, 0.0, "Sugar Beets"));
        lp.addConstraint(new LinearSmallerThanEqualsConstraint(
                new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0}, 6000., "Selling Beets"));
        lp.setLowerbound(new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0});
        lp.setMinProblem(Boolean.TRUE);

        System.out.print(lp.convertToCPLEX());

        LinearProgramSolver lpsolver = SolverFactory.getSolver("GLPK");

        double[] solution = lpsolver.solve(lp);
        double[] expected = {120., 80., 300., 0., 100., 0., 0., 6000., 0.};
        for (int i = 0; i < solution.length; i++)
            Assert.assertEquals(expected[i], solution[i], 0.0);
    }

    @Test
    public void testFarmerExampleExtensiveOld() {
        LinearProgram lp = new LinearProgram(new double[]{150., 230., 260., (170. / -3.), (238./3.), (150. / -3.), (210. / 3.), (36./-3.), (10./-3.),
                (170. / -3.), (238./3.), (150. / -3.), (210. / 3.), (36./-3.), (10./-3.),
                (170. / -3.), (238./3.), (150. / -3.), (210. / 3.), (36./-3.), (10./-3.)});
        lp.addConstraint(new LinearSmallerThanEqualsConstraint(
                new double[]{1., 1., 1., 0., 0., 0., 0.,
                        0., 0., 0., 0., 0., 0., 0.,
                        0., 0., 0., 0., 0., 0., 0.}, 500., "Land"));
        lp.addConstraint(new LinearBiggerThanEqualsConstraint(
                new double[]{3., 0., 0., -1., 1., 0., 0.,
                        0., 0., 0., 0., 0., 0., 0.,
                        0., 0., 0., 0., 0., 0., 0.}, 200., "Wheat - Scenario 1"));
        lp.addConstraint(new LinearBiggerThanEqualsConstraint(
                new double[]{2.5, 0., 0., 0., 0., 0., 0.,
                        0., 0., -1., 1., 0., 0., 0.,
                        0., 0., 0., 0., 0., 0., 0.}, 200., "Wheat - Scenario 2"));
        lp.addConstraint(new LinearBiggerThanEqualsConstraint(
                new double[]{2., 0., 0., 0., 0., 0., 0.,
                        0., 0., 0., 0., 0., 0., 0.,
                        0., -1., 1., 0., 0., 0., 0.}, 200., "Wheat - Scenario 3"));

        lp.addConstraint(new LinearBiggerThanEqualsConstraint(
                new double[]{0., 3.6, 0., 0., 0., -1., 1.,
                        0., 0., 0., 0., 0., 0., 0.,
                        0., 0., 0., 0., 0., 0., 0.}, 240., "Corn - Scenario 1"));
        lp.addConstraint(new LinearBiggerThanEqualsConstraint(
                new double[]{0., 3., 0., 0., 0., 0., 0.,
                        0., 0., 0., 0., -1., 1., 0.,
                        0., 0., 0., 0., 0., 0., 0.}, 240., "Corn - Scenario 2"));
        lp.addConstraint(new LinearBiggerThanEqualsConstraint(
                new double[]{0., 2.4, 0., 0., 0., 0., 0.,
                        0., 0., 0., 0., 0., 0., 0.,
                        0., 0., 0., -1., 1., 0., 0.}, 240., "Corn - Scenario 3"));

        lp.addConstraint(new LinearSmallerThanEqualsConstraint(
                new double[]{0., 0., -24., 0., 0., 0., 0.,
                        1., 1., 0., 0., 0., 0., 0.,
                        0., 0., 0., 0., 0., 0., 0.}, 0., "Sugar Beets - Scenario 1"));
        lp.addConstraint(new LinearSmallerThanEqualsConstraint(
                new double[]{0., 0., -20., 0., 0., 0., 0.,
                        0., 0., 0., 0., 0., 0., 1.,
                        1., 0., 0., 0., 0., 0., 0.}, 0., "Sugar Beets - Scenario 2"));
        lp.addConstraint(new LinearSmallerThanEqualsConstraint(
                new double[]{0., 0., -16., 0., 0., 0., 0.,
                        0., 0., 0., 0., 0., 0., 0.,
                        0., 0., 0., 0., 0., 1., 1.}, 0., "Sugar Beets - Scenario 3"));

        lp.addConstraint(new LinearSmallerThanEqualsConstraint(
                new double[]{0., 0., 0., 0., 0., 0., 0.,
                        1., 0., 0., 0., 0., 0., 0.,
                        0., 0., 0., 0., 0., 0., 0.}, 6000., "Selling Beets - Scenario 1"));
        lp.addConstraint(new LinearSmallerThanEqualsConstraint(
                new double[]{0., 0., 0., 0., 0., 0., 0.,
                        0., 0., 0., 0., 0., 0., 1.,
                        0., 0., 0., 0., 0., 0., 0.}, 6000., "Selling Beets - Scenario 2"));
        lp.addConstraint(new LinearSmallerThanEqualsConstraint(
                new double[]{0., 0., 0., 0., 0., 0., 0.,
                        0., 0., 0., 0., 0., 0., 0.,
                        0., 0., 0., 0., 0., 1., 0.}, 6000., "Selling Beets - Scenario 3"));

        lp.setLowerbound(new double[]{0., 0., 0., 0., 0., 0., 0.,
                0., 0., 0., 0., 0., 0., 0.,
                0., 0., 0., 0., 0., 0., 0.});

        lp.setMinProblem(Boolean.TRUE);

        LinearProgramSolver lpsolver =SolverFactory.getSolver("GLPK");

        double[] solution = lpsolver.solve(lp);
        double[] expected = new double[]{170., 80., 250., 310., 0., 48., 0.,
                6000., 0., 225., 0., 0., 0., 5000.,
                0., 140., 0., 0., 48., 4000., 0.};

        for (int i = 0; i < solution.length; i++)
            Assert.assertEquals(expected[i], solution[i], 0.00001);
    }

    @Test
    public void testFarmerExampleNew() {

        StochasticProgram sp = new StochasticProgram(new double[]{150., 230., 260.},
                new double[]{(1./ 3.), (1./3.), (1./3.)},
                new double[][]{{-170., 238., -150., 210., -36., -10.},
                        {-170., 238., -150., 210., -36., -10.},
                        {-170., 238., -150., 210., -36., -10.}});

        sp.addConstraint(new StochasticBiggerThanEqualsConstraint(
                new double[][]{{3., 0., 0.}, {2.5, 0., 0.}, {2., 0., 0.}},
                new double[]{-1., 1., 0., 0., 0., 0.}, new double[]{200., 200., 200.}, "Wheat"));

        sp.addConstraint(new StochasticBiggerThanEqualsConstraint(
                new double[][]{{0., 3.6, 0.}, {0., 3., 0.}, {0., 2.4, 0.}},
                new double[]{0., 0., -1., 1., 0., 0.}, new double[]{240., 240., 240.}, "Corn"));

        sp.addConstraint(new StochasticSmallerThanEqualsConstraint(
                new double[][]{{0., 0., -24.}, {0., 0., -20.}, {0., 0., -16.}},
                new double[]{0., 0., 0., 0., 1., 1.}, new double[]{0., 0., 0.}, "Sugar Beets"));

        sp.addConstraint(new StochasticSmallerThanEqualsConstraint(
                new double[][]{{0., 0., 0.}, {0., 0., 0.}, {0., 0., 0.}},
                new double[]{0., 0., 0., 0., 1., 0.}, new double[]{6000., 6000., 6000.}, "Selling Beets"));

        sp.addConstraint(new LinearSmallerThanEqualsConstraint(
                new double[]{1., 1., 1.}, 500., "Land"));

        sp.setLowerbound(new double[]{0., 0., 0., 0., 0., 0., 0., 0., 0.});
        sp.setMinProblem(Boolean.TRUE);

        LinearProgram lp = sp.getExtensiveForm();

        LinearProgramSolver solver = SolverFactory.getSolver("GLPK");

        double[] solution = solver.solve(lp);
        double[] expected = new double[]{170., 80., 250., 310., 0., 48., 0.,
                6000., 0., 225., 0., 0., 0., 5000.,
                0., 140., 0., 0., 48., 4000., 0.};

        for (int i = 0; i < solution.length; i++)
            Assert.assertEquals(expected[i], solution[i], 0.00001);

        // System.out.println(sp.getExtensiveForm().convertToCPLEX());

    }
}
