package org.lplibs4j.glpksolver;

import org.gnu.glpk.GlpkSolver;
import org.lplibs4j.api.constraints.*;
import org.lplibs4j.api.problems.LinearProgram;
import org.lplibs4j.api.solver.LinearProgramSolver;

import java.util.ArrayList;

public class SolverGLPK implements LinearProgramSolver {

    GlpkSolver solver;
    int rowcount;
    int timeconstraint = -1;

    /**
     * @return the timeconstraint
     */
    public int getTimeconstraint() {
        return timeconstraint;
    }

    /**
     * @param timeconstraint the timeconstraint to set
     */
    public void setTimeconstraint(int timeconstraint) {
        this.timeconstraint = timeconstraint;
    }

    public double[] solve(LinearProgram lp) {

		/* add variables */
        try {
            // ToDo: this should be taken care with the factory!
            System.loadLibrary("glpkjni");
            solver = new GlpkSolver();
        } catch (UnsatisfiedLinkError ex) {
            System.err.println("Can't instantiate solver:");
            System.err.println(" ** " + ex.getClass().getName() + ": " + ex.getMessage());
            System.err.println(
                    " ** java.library.path: " + System.getProperty("java.library.path"));
            System.err.println("Probably you don't have GLPK JNI properly installed.");
            return null;
        }

        // solver.setRealParm(GlpkSolver.LPX_K_TMLIM, 100);
        // solver.setIntParm(GlpkSolver.LPX_K_TMLIM, 100);
        solver.enablePrints(false);  // turn this to "false" to prevent printouts
        if (lp.isMIP()) {
            solver.setClss(GlpkSolver.LPX_MIP);
        } else {
            solver.setClss(GlpkSolver.LPX_LP);
        }

		/* we usually expect a max problem, but I have to think about that..*/

        solver.setObjDir((lp.isMinProblem()) ? GlpkSolver.LPX_MIN : GlpkSolver.LPX_MAX);

		/* set columns */
        double[] c = lp.getC();
        solver.addCols(c.length);

        for (int i = 0; i < c.length; i++) {
            solver.setColName(i + 1, "x" + i);
            solver.setObjCoef(i + 1, c[i]);
        }

        if (!lp.hasBounds()) {
            for (int i = 0; i < c.length; i++) {
                solver.setColBnds(i + 1, GlpkSolver.LPX_FR, 0, 0);
            }
        } else {
            for (int i = 0; i < c.length; i++) {
                solver.setColBnds(i + 1, GlpkSolver.LPX_DB, lp.getLowerbound()[i], lp.getUpperbound()[i]); //TODO
            }
        }

		/* add variable types */

        boolean[] integers = lp.getIsinteger();

        for (int i = 0; i < integers.length; i++) {
            solver.setColKind(i + 1, (integers[i]) ? GlpkSolver.LPX_IV : GlpkSolver.LPX_CV);
        }

        boolean[] booleans = lp.getIsboolean();

        for (int i = 0; i < booleans.length; i++) {
            if (booleans[i]) {
                solver.setColKind(i + 1, GlpkSolver.LPX_IV);
                solver.setColBnds(i + 1, GlpkSolver.LPX_DB, 0, 1); //TODO
            }
        }

		/* add constraints */

        transferConstraints(lp);

        if (timeconstraint > 0) {
            System.out.println("Setting time constraint to:" + timeconstraint + " seconds");
            solver.setRealParm(GlpkSolver.LPX_K_TMLIM, (double) timeconstraint);
        }

        double[] result = null;

        int res = solver.simplex();

        if (!lp.isMIP()) {

            //System.out.println("Maximum: " + solver.getObjVal());

            if (res != GlpkSolver.LPX_E_OK ||
                    (solver.getStatus() != GlpkSolver.LPX_OPT &&
                            solver.getStatus() != GlpkSolver.LPX_FEAS)) {
                System.err.println("simplex() failed");
            } else {
                result = new double[c.length];
                for (int i = 0; i < result.length; i++) {
                    result[i] = solver.getColPrim(i + 1);
                    System.out.println("x" + (i + 1) + ": " + result[i]);
                }
            }
        } else {
            res = solver.integer();
            //  System.out.println("SOLVER STATUS: " + solver.getPrimStat());
            if (res != GlpkSolver.LPX_E_OK ||
                    (solver.mipStatus() != GlpkSolver.LPX_I_OPT &&
                            solver.mipStatus() != GlpkSolver.LPX_I_FEAS)) {

                System.err.println("integer() failed");
            } else {
                //		System.out.println("Maximum: " + solver.mipObjVal());
                //		System.out.println("MIP STATUS: " + solver.mipStatus());
                result = new double[c.length];
                for (int i = 0; i < result.length; i++) {
                    result[i] = solver.mipColVal(i + 1);
                    //	System.out.println("x" +(i+1) +": " + result[i]);
                }
            }
        }
        //solver.deleteProb();
        return result;
    }

    public int[] getIntegerSolution() {
        int length = solver.getNumCols();
        int[] result = new int[length];
        for (int i = 1; i <= length; i++) {
            result[i - 1] = (int) solver.mipColVal(i);
        }
        return result;
    }

    private void transferConstraints(LinearProgram lp) {
        ArrayList<Constraint> constraints = lp.getConstraints();
        rowcount = 0;

		/* add rows */
        solver.addRows(constraints.size());

		/* add row/names borders */
        for (Constraint constraint : constraints) {
            ((LinearConstraint) constraint).addToLinearProgramSolver(this);
        }

        int nonzeroa = 0;
        for (Constraint constraint : constraints) {

            double[] c = ((LinearConstraint) constraint).getC();
            for (int i = 0; i < c.length; i++) {
                if (c[i] != 0.0) nonzeroa++;
            }
        }

        int[] ia = new int[nonzeroa + 1];
        int[] ja = new int[nonzeroa + 1];
        double[] ar = new double[nonzeroa + 1];

        rowcount = 0;
        nonzeroa = 0;

        for (Constraint constraint : constraints) {

            rowcount++;
            double[] c = ((LinearConstraint) constraint).getC();

            //System.out.print(constraint.getName() + " ");
            for (int i = 0; i < c.length; i++) {
                if (c[i] != 0.0) {
                    nonzeroa++;
                    ia[nonzeroa] = rowcount;
                    ja[nonzeroa] = i + 1;
                    ar[nonzeroa] = c[i];
                    //	System.out.print((i+1) + "(" + c[i] + ") ");
                }
            }
            //	System.out.println();

        }
        solver.loadMatrix(nonzeroa, ia, ja, ar);
    }

    public void addLinearBiggerThanEqualsConstraint(LinearBiggerThanEqualsConstraint c) {
        rowcount++;
        solver.setRowName(rowcount, c.getName());
        solver.setRowBnds(rowcount, GlpkSolver.LPX_LO, c.getT(), 0.0);
    }

    public void addLinearSmallerThanEqualsConstraint(LinearSmallerThanEqualsConstraint c) {
        rowcount++;
        solver.setRowName(rowcount, c.getName());
        solver.setRowBnds(rowcount, GlpkSolver.LPX_UP, 0.0, c.getT());
    }

    public void addEqualsConstraint(LinearEqualsConstraint c) {
        rowcount++;
        solver.setRowName(rowcount, c.getName());
        solver.setRowBnds(rowcount, GlpkSolver.LPX_FX, c.getT(), c.getT());

    }

    public String getName() {
        return "GLPK";
    }

    public String[] getLibraryNames() {
        return new String[]{"glpkjni"};
    }

}
