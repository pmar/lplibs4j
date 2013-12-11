package org.lplibs4j.api.solver;

import org.lplibs4j.api.constraints.Constraint;
import org.lplibs4j.api.util.Matrix;

import java.util.ArrayList;

/**
 * User: pmar@ppolabs.com
 * Date: 12/10/13
 * Time: 11:24 PM
 */
public interface QuadraticProgram {

    Matrix getQ();

    void setQ(Matrix q);

    void setQ(double[][] q);

    /* (non-Javadoc)
         * @see nmi.ConstrainedProblem#addConstraint(nmi.constraints.Constraint)
         */
    boolean addConstraint(Constraint c);

    /* (non-Javadoc)
         * @see nmi.ConstrainedProblem#isFeasable(double[])
         */
    ArrayList<Constraint> getViolatedContraints(double[] x);
}
