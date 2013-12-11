package org.lplibs4j.api.constraints;

/**
 * User: pmar@ppolabs.com
 * Date: 12/10/13
 * Time: 11:08 PM
 */
public interface LinearEqualsConstraint {
    void setC(double[] pc);

    void setT(double t);

    String getName();

    double getT();
}
