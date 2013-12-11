package org.lplibs4j.api.solver;

import org.lplibs4j.api.constraints.LinearBiggerThanEqualsConstraint;
import org.lplibs4j.api.constraints.LinearEqualsConstraint;
import org.lplibs4j.api.constraints.LinearSmallerThanEqualsConstraint;
import org.lplibs4j.api.constraints.QuadraticConstraint;

public interface QuadraticProgramSolver {

    public double[] solve(QuadraticProgram qp);
    public void addLinearBiggerThanEqualsConstraint(LinearBiggerThanEqualsConstraint c);
    public void addLinearSmallerThanEqualsConstraint(LinearSmallerThanEqualsConstraint c);
    public void addQuadraticSmallerThanEqualsContraint(QuadraticConstraint c);
    public void addEqualsConstraint(LinearEqualsConstraint c);

}
