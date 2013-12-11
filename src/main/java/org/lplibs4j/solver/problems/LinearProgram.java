package org.lplibs4j.solver.problems;

import org.lplibs4j.api.constraints.Constraint;
import org.lplibs4j.api.implementation.SparseVector;
import org.lplibs4j.api.solver.LinearProgramSolver;
import org.lplibs4j.solver.constraints.LinearBiggerThanEqualsConstraint;
import org.lplibs4j.solver.constraints.LinearSmallerThanEqualsConstraint;
import org.lplibs4j.solver.lpsolver.SolverFactory;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Basic description for a linear program.
 *
 * In an ordinary production usage, this class usually gets instanced first. Then all
 * constraints and boundaries are added and finally, a solver is instanced to solve
 * the linear program.
 *
 * @author  planatsc
 */
public class LinearProgram extends org.lplibs4j.api.problems.LinearProgram {

    HashMap<String, Integer> indexmap;

    /**
     * Creates a basic maximization problem with no target function and no constraints.
     */
    public LinearProgram() {
        super();
        minproblem = false;
        constraints = new ArrayList<Constraint>();
    }

    /**
     * Creates a basic maximization problem with the given target function and no constraints.
     *
     * @param pc the vector of the target function
     */
    public LinearProgram(double[] pc) {
        super();
        this.minproblem = false;
        this.constraints = new ArrayList<Constraint>();
        this.c = (SparseVector) new SparseVector(pc);
        this.isinteger  = new boolean[pc.length];
        this.isboolean  = new boolean[pc.length];

    }

    /**
     * Creates a basic maximization problem with the given target function and no constraints.
     *
     * @param c the vector of the target function in its sparse representation
     */
    public LinearProgram(SparseVector c) {
        super();
        this.minproblem = false;
        this.constraints = new ArrayList<Constraint>();
        this.c = (SparseVector) c;
        this.isinteger  = new boolean[c.getSize()];
        this.isboolean  = new boolean[c.getSize()];
    }

    /**
     * Creates a shallow copy of a given linear program.
     *
     * @param lp the program to be copied
     */
    // TODO Is this a shallow or a deep copy?
    @SuppressWarnings("unchecked")
    public LinearProgram(LinearProgram lp) {
        super();
        this.minproblem = lp.minproblem;
        this.isinteger = lp.isinteger.clone();
        this.isboolean = lp.isboolean.clone();

        this.c = lp.c.clone();
        this.constraints = (ArrayList<Constraint>) lp.constraints.clone();
        if (lp.hasBounds()) {
            this.upperbound = lp.upperbound.clone();
            this.lowerbound = lp.lowerbound.clone();
        }

    }

	/* (non-Javadoc)
	 * @see nmi.ConstrainedProblem#isFeasable(double[])
	 */
    /**
     * Checks if all constraints are fulfilled for the vector <code>x</code>.
     *
     * @param x vector which is to be checked
     * @return <code>true</code>, if the vector <code>x</code> fulfills all
     * constraints, otherwise <code>false</code>
     */
    public boolean isFeasable(double[] x) {
        for (Constraint c : constraints) {
            if (!c.isSatisfiedBy(x)) return false;
        }
        return true;
    }
	
	/* (non-Javadoc)
	 * @see nmi.ConstrainedProblem#getConstraints()
	 */
    /**
     * Returns the current constraint list.
     *
     * @return the list of the current constraints
     */
    public ArrayList<Constraint> getConstraints() {
        return constraints;
    }

    /**
     * Evaluates the target function at position <code>x</code>.
     *
     * @param x the position, where the target function is to be evaluated
     * @return the value of the target function at said position
     */
    public double evaluate(double[] x) {
        return c.dot(x);

        //c.dot (new SparseMatrix(new double[][]{x})).get(0, 0);
    }

    /**
     * Returns a short String description of the class.
     *
     * @return Returns the String "Linear Program"
     */
    public String getName() {
        return "Linear Program";
    }
	
/*
	public LinearProgram getDual() {
		 Iterator<Constraint> citerator = constraints.iterator();
		 int dualsize = constraints.size();
		 SparseVector dualc =  new SparseVector(dualsize, dualsize);
		 int counter = 0;
		 while (citerator.hasNext()) {
			LinearConstraint constraint = (LinearConstraint) citerator.next();
			dualc.set(counter++, constraint.getT());
		 }
		 ArrayList<Constraint> dualconstraints = new ArrayList<Constraint>();
		 for (int i = 0; i < dualc.getSize(); i++) {
			 dualconstraints.add();
		 }
		 // TODO add dual constraints
		//LinearProgram dual = new LinearProgram();
	}

	public LinearProgram getStandardFormLP() {}
*/


    public static void main(String[] args) {

        LinearProgram lp = new LinearProgram(new double[]{10.0, 6.0, 4.0});
        lp.addConstraint(new LinearSmallerThanEqualsConstraint(new double[]{1.0,1.0,1.0}, 320,"p"));
        lp.addConstraint(new LinearSmallerThanEqualsConstraint(new double[]{10.0,4.0,5.0}, 650,"q"));
        lp.addConstraint(new LinearBiggerThanEqualsConstraint(new double[]{2.0,-2.0,6.0}, 100,"r1"));

        lp.setLowerbound(new double[]{30.0,0.0,0.0});

        //lp.addConstraint(new LinearEqualsConstraint(new double[]{1.0,1.0,1.0}, 100,"t"));

        // lp.setInteger(0);
        // lp.setInteger(1);
        lp.setInteger(2);

        System.out.print(lp.convertToCPLEX());
        System.out.println();
        //System.out.print(lp.convertToGMPL());

        lp = new LinearProgram(new double[]{25.0, 30.0});
        lp.addConstraint(new LinearSmallerThanEqualsConstraint(new double[]{(1.0 / 200.0), (1.0 / 140.0)}, 40, "Time"));
        lp.setLowerbound(new double[]{0.0, 0.0});
        lp.setUpperbound(new double[]{6000, 4000});
        lp.setInteger(0);
        lp.setInteger(1);

        System.out.println();
        //System.out.print(lp.convertToGMPL());

        LinearProgramSolver solver = SolverFactory.getSolver("GLPK");
        solver.solve(lp);
        // Note to Schobi: change from BigDecimal to this methods in the conversion options
        System.out.println("Abs von -2.0: " + Math.abs(-2.0));
        System.out.println("Signum von -2.0: " + Math.signum(-2.0));
    }

}

