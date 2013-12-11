package org.lplibs4j.api.constraints;


import org.lplibs4j.api.implementation.SparseVector;

public interface StochasticConstraint extends Constraint {

    public LinearConstraint[] getExtensiveForm();

    public double[] getC();
    // FIXME: generalize to Matrix?
    public SparseVector getCSparse();

    public double[] getH();
    public double[][] getT();
    public SparseVector[] getTSparse();
}
