package org.lplibs4j;

import org.lplibs4j.api.solver.LinearProgramSolver;
import org.lplibs4j.solver.constraints.LinearBiggerThanEqualsConstraint;
import org.lplibs4j.solver.constraints.LinearSmallerThanEqualsConstraint;
import org.lplibs4j.solver.lpsolver.SolverFactory;
import org.lplibs4j.solver.problems.LinearProgram;

/**
 * Hello world for GLPK library!
 */
public class AppGLPK {

    public static void main(String[] args) {

        System.out.println("Welcome to the Tester for the LPLibs4j distribution package");
        System.out.println("In the following a few linear programs are solved by a");
        System.out.println("module in the classpath. Possibly, this is exciting to you!\n");

        // A sample linear program
        LinearProgram lp = new LinearProgram(new double[]{10.0, 6.0, 4.0});
        lp.addConstraint(new LinearSmallerThanEqualsConstraint(new double[]{1.0, 1.0, 1.0}, 320, "p"));
        lp.addConstraint(new LinearSmallerThanEqualsConstraint(new double[]{10.0, 4.0, 5.0}, 650, "q"));
        lp.addConstraint(new LinearBiggerThanEqualsConstraint(new double[]{2.0, 2.0, 6.0}, 100, "r1"));

        lp.setLowerbound(new double[]{30.0, 0.0, 0.0});

        //lp.addConstraint(new LinearEqualsConstraint(new double[]{1.0,1.0,1.0}, 100,"t"));

        lp.setInteger(0);
        lp.setInteger(1);
        lp.setInteger(2);

        LinearProgramSolver solver = SolverFactory.newDefault();

        solver.solve(lp);

        System.out.println("That seemed to work!\nWe also try to solve a small MIP ...\n");

        lp = new LinearProgram(new double[]{2., 0., 1., 0.5, 0.5});
        lp.setMinProblem(Boolean.TRUE);

        lp.setLowerbound(new double[]{1., 1., 1., 1., 1.});

        lp.addConstraint(new LinearBiggerThanEqualsConstraint
                (new double[]{0.5, 0., 0., 2., 0.}, 2., "x_1 + x_4 >= 2"));
        lp.addConstraint(new LinearSmallerThanEqualsConstraint
                (new double[]{0., 2., 0., 2., 1.}, 5., "2 + x_4 + 0.5*x_5 <= 5"));
        lp.addConstraint(new LinearBiggerThanEqualsConstraint
                (new double[]{0., 0., 3., 0., 10.}, 16., "3*x_3 + 5*x_5 >= 16"));

        for (int i = 0; i < lp.getDimension(); i++)
            lp.setInteger(i);

        // solver.setTimeconstraint(20);

        double[] solution = solver.solve(lp);
        double[] expected = new double[]{1., 1., 2., 1., 1.};

        if (solution.length != expected.length)
            System.out.println("ERROR: Dimension mismatch between computed vs expected solution!");
        for (int i = 0; i < expected.length; i++) {
            if (Math.abs(solution[i] - expected[i]) < 0.01)
                System.out.println("Variable " + i + " successfully found!");
            else
                System.out.println("WARNING: Variable " + i + " not found successfully!");
        }


        System.out.println("Now we'll also try to load any specific solver you passed on the command line:\n");

        // TODO Check this program again!
        lp = new LinearProgram(new double[]{1., 1., 1.});
        lp.setMinProblem(Boolean.TRUE);

        lp.addConstraint(new LinearBiggerThanEqualsConstraint
                (new double[]{1., 0., 0.}, 5., "x_1 >= 5"));
        lp.addConstraint(new LinearBiggerThanEqualsConstraint
                (new double[]{0., 1., 0.}, 3., "x_2 >= 3"));
        lp.addConstraint(new LinearBiggerThanEqualsConstraint
                (new double[]{0., 0., 1.}, 4., "x_3 >= 4"));

        lp.setIsinteger(new boolean[]{Boolean.TRUE, Boolean.TRUE, Boolean.TRUE});

        solution = solver.solve(lp);
        expected = new double[]{5., 3., 4.};

        for (int i = 0; i < args.length; i++) {
            System.out.println("\n\nTesting solver: " + args[i]);
            solver = SolverFactory.getSolver(args[i]);
            solver.solve(lp);

            if (solution.length != expected.length)
                System.out.println("ERROR: Dimension mismatch between computed vs expected solution!");
            for (int j = 0; j < expected.length; j++) {
                if (Math.abs(solution[j] - expected[j]) < 0.01)
                    System.out.println("Variable " + j + " successfully found!");
                else
                    System.out.println("WARNING: Variable " + j + " not found sucessfully!");
            }
        }

        System.out.println("\nWell, we hope you enjoyed our little demo. Have a nice day!");
    }

}
