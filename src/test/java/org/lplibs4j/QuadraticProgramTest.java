package org.lplibs4j;

import org.junit.*;
import org.lplibs4j.solver.qpsolver.QuadraticProgram;




public class QuadraticProgramTest {

    @Test
    public void testEvaluate() {
        QuadraticProgram qp = new QuadraticProgram(new double[][]{{0.0,0.0},{0.0,0.0}},new double[]{1.0,1.0});
        double result = qp.evaluate(new double[]{1.0,1.0});

        Assert.assertEquals("Evaluation is wrong: ", 2.0, result, 0.0001);
        result = qp.evaluate(new double[]{0.0,0.0});
        Assert.assertEquals("Evaluation is wrong: ", 0.0, result, 0.0001);
    }

    @Test
    public void testSmallQP() {
        QuadraticProgram qp = new QuadraticProgram(new double[][]{{2, 1},{1, 2}}, new double[]{0.,0.});

        double result1 = qp.evaluate(new double[]{1., 0.});
        Assert.assertEquals("Evaluation is wrong: ", 1., result1, 0.0001);

        double result2 = qp.evaluate(new double[]{0., 1.});
        Assert.assertEquals("Evaluation is wrong: ", 1., result2, 0.0001);

        qp.setMinProblem(Boolean.TRUE);
        qp.setLowerbound(new double[]{5., 5.});
        qp.setUpperbound(new double[]{10., 10.});

		/* TODO
		 * Why should
		 *
		 * | 2 1 |
		 * | 1 2 |
		 *
		 * be not positive semi-definite?
		 *
		 * it is BUT CPLEX means a negative semi-definite matrix, which is needed in case of a quadratic max problem *sic*
		 */

		/*QuadraticProgramSolver qpsolver = new CPLEXSolver();
		double[] solution = qpsolver.solve(qp);
		double[] expected = new double[]{10., 10.};

		for(int i = 0; i < expected.length; i++)
			Assert.assertEquals(expected[i], solution[i], 0.0001);
		*/
    }

}
