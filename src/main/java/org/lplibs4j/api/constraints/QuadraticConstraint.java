package org.lplibs4j.api.constraints;


import org.lplibs4j.api.implementation.SparseVector;
import org.lplibs4j.api.solver.QuadraticProgramSolver;
import org.lplibs4j.api.util.Matrix;

public interface QuadraticConstraint extends Constraint {

    public void addToQuadraticProgramSolver(QuadraticProgramSolver solver);
    public Matrix getQ();
    public double[] getC();

    public SparseVector getCSparse();
    public double getT();

}
