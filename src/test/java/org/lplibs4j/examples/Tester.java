package org.lplibs4j.examples; /**
 * A simple test class to test the distribution of the LPLibs4j.
 *
 * @author michael
 */

import org.lplibs4j.api.constraints.Constraint;
import org.lplibs4j.api.solver.LinearProgramSolver;
import org.lplibs4j.glpksolver.SolverGLPK;
import org.lplibs4j.solver.constraints.LinearBiggerThanEqualsConstraint;
import org.lplibs4j.solver.constraints.LinearSmallerThanEqualsConstraint;
import org.lplibs4j.solver.problems.LinearProgram;
import org.lplibs4j.util.LibraryLoaderGLPK;

import java.util.ArrayList;
import java.util.Iterator;

public class Tester {

    // TODO: this should be

    public static void main(String[] args) {

        LibraryLoaderGLPK.loadLibraryAndCheckErrors();

        LinearProgram lp = new LinearProgram(new double[]{10.0, 6.0, 4.0});
        lp.addConstraint(new LinearSmallerThanEqualsConstraint(new double[]{1.0, 1.0, 1.0}, 320, "p"));
        lp.addConstraint(new LinearSmallerThanEqualsConstraint(new double[]{10.0, 4.0, 5.0}, 650, "q"));
        lp.addConstraint(new LinearBiggerThanEqualsConstraint(new double[]{2.0, 2.0, 6.0}, 100, "r1"));

        lp.setLowerbound(new double[]{30.0, 0.0, 0.0});

        lp.setInteger(0);
        lp.setInteger(1);
        lp.setInteger(2);

        LinearProgramSolver solver = new SolverGLPK();

        System.out.println(solver.solve(lp)[0]);
        double[] sol = solver.solve(lp);
        ArrayList<Constraint> constraints = lp.getConstraints();
        for (Iterator<Constraint> iterator = constraints.iterator(); iterator.hasNext(); ) {
            Constraint constraint = iterator.next();
            if (constraint.isSatisfiedBy(sol)) {
                System.out.println(constraint.getName() + " satisfied");
            }

        }
    }
}

