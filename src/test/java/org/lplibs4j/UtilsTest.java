package org.lplibs4j;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.lplibs4j.api.implementation.SparseMatrix;
import org.lplibs4j.api.implementation.SparseVector;

public class UtilsTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    /*
     * TODO: Split into several methods, clean up a bit
     */
    @Test
    public void testSparseVector() {
        SparseVector e3 = new SparseVector(new double[]{1.0, 1.0, 1.0});

        Assert.assertEquals(e3.dot(e3), 3.0, 0.0);
        Assert.assertEquals(e3.getSize(), 3);
        Assert.assertEquals(e3.get().length, 3);
        for (double d : e3.get())
            Assert.assertEquals(d, 1.0, 0.0);

        // SparseVector v1 = new SparseVector(new double[]{1.0, 0.0, 0.0, 2.5, 3.75});
        SparseVector v1 = new SparseVector(5, 3);
        v1.set(0, 1.0);
        v1.set(3, 2.5);
        v1.set(4, 3.75);

        Assert.assertEquals(v1.getSize(), v1.get().length);
        Assert.assertEquals(v1.getData().length, 3);

        Assert.assertTrue(v1.equals(v1));
        Assert.assertTrue(v1.equals(new SparseVector(new double[]{1.0, 0.0, 0.0, 2.5, 3.75})));
        Assert.assertFalse(v1.equals(new SparseVector(new double[]{1.0, 0.0, 2.5, 0.0, 3.75})));

        SparseVector v2 = new SparseVector(new double[]{0.0, 1.0, 0.0, 2.5, 0.0});
        Assert.assertEquals(v2.getUsed(), 2);

        v1 = v1.add(v2);
        Assert.assertTrue(v1.equals(new SparseVector(new double[]{1.0, 1.0, 0.0, 5.0, 3.75})));

        v2 = v2.times(-1.);
        v1 = v1.add(v2);
        Assert.assertTrue(v1.equals(new SparseVector(new double[]{1., 0., 0., 2.5, 3.75})));
        v1 = v1.add(v2);
        Assert.assertTrue(v1.equals(new SparseVector(new double[]{1., -1., 0., 0., 3.75})));

        SparseVector zero = v1.add(v1.times(-1.));
        Assert.assertEquals(zero.getSize(), zero.get().length);
		
		/*
		 * TODO: This test won't work - is it worth to implement it?
		 */
        // Assert.assertEquals(zero.getData().length, 0);
        for (double c : zero.get())
            Assert.assertEquals(c, 0.0, 0.0);

    }

    @Test
    public void testSparseMatrix() {

        SparseMatrix E5 = new SparseMatrix(5, 5);
        for (int i = 0; i < 5; i++)
            E5.set(i, i, 1.);

        Assert.assertTrue(E5.equals(E5));
        Assert.assertTrue(E5.equals(E5.transpose()));

        SparseMatrix A = new SparseMatrix(5, 5);

        Assert.assertEquals(5, A.getRowNum());
        Assert.assertEquals(5, A.getColNum());

        for (int i = 0; i < A.getRowNum(); i++)
            for (int j = 0; j < A.getColNum(); j++)
                Assert.assertEquals(0.0, A.get(i, j), 0.0);

        A.set(1, 1, 1.);
        A.set(1, 2, 2.);
        A.set(2, 1, 3.);
        A.set(2, 2, 4.);

        Assert.assertTrue(A.equals(A.times(E5)));
        Assert.assertTrue(A.equals(E5.times(A)));

        SparseMatrix At = new SparseMatrix(5,5);
        At.set(1, 1, 1.);
        At.set(1, 2, 3.);
        At.set(2, 1, 2.);
        At.set(2, 2, 4.);

        Assert.assertTrue(A.equals(At.transpose()));
        Assert.assertTrue(At.equals(A.transpose()));
        Assert.assertTrue(A.equals(A.transpose().transpose()));
        Assert.assertTrue(At.equals(At.transpose().transpose()));

        SparseMatrix B = new SparseMatrix(5, 5);
        B.set(3, 3, 1.); // Damn you, indexing from 0 to N-1
        B.set(3, 4, 2.); // When it comes to indexing I'm really more a mathematician
        B.set(4, 3, 3.);
        B.set(4, 4, 4.);

        // Zeromatrix
        SparseMatrix zero = new SparseMatrix(5, 5);
        Assert.assertTrue(zero.equals(A.times(B)));
        Assert.assertTrue(zero.equals(B.times(A)));

    }

    @Test
    public void testSubmatrix() {

        SparseMatrix E5 = new SparseMatrix(5, 5);
        for (int i = 0; i < 5; i++)
            E5.set(i, i, 1.);

        Assert.assertTrue(E5.equals(E5));
        Assert.assertTrue(E5.equals(E5.transpose()));

        SparseMatrix A = new SparseMatrix(5, 5);

        Assert.assertEquals(5, A.getRowNum());
        Assert.assertEquals(5, A.getColNum());

        for (int i = 0; i < A.getRowNum(); i++)
            for (int j = 0; j < A.getColNum(); j++)
                Assert.assertEquals(0.0, A.get(i, j), 0.0);

        A.set(0, 0, 1.);
        A.set(0, 1, 2.);
        A.set(1, 0, 3.);
        A.set(1, 1, 4.);

        SparseMatrix zero3 = new SparseMatrix(3, 3);
        Assert.assertTrue(zero3.equals(A.submatrix(2, 5)));

        SparseMatrix SmallA = new SparseMatrix(3, 3);
        SmallA.set(0, 0, 4.);
        Assert.assertTrue(SmallA.equals(A.submatrix(1, 4)));

        SparseMatrix B = new SparseMatrix(5, 5);
        B.set(0, 0, 1.);
        B.set(0, 1, 2.);
        B.set(0, 2, 3.);
        B.set(1, 0, 4.);
        B.set(1, 1, 5.);
        B.set(1, 2, 6.);
        B.set(2, 0, 7.);
        B.set(2, 1, 8.);
        B.set(2, 2, 9.);

        SparseMatrix SmallB = new SparseMatrix(2, 2);

        SmallB.set(0, 0, 5.);
        SmallB.set(0, 1, 6.);
        SmallB.set(1, 0, 8.);
        SmallB.set(1, 1, 9.);
        Assert.assertTrue(SmallB.equals(B.submatrix(1, 3)));

        SmallB.set(0, 0, 1.);
        SmallB.set(0, 1, 2.);
        SmallB.set(1, 0, 4.);
        SmallB.set(1, 1, 5.);
        Assert.assertTrue(SmallB.equals(B.submatrix(0, 2)));

    }

    // DAMN YOU, YOU STUPID CODE!!
    @Test
    public void testMatrixAddition() {

        SparseMatrix A = new SparseMatrix(5,5);
        A.set(3, 3, 1.);
        A.set(3, 4, 2.);
        A.set(4, 3, 3.);
        A.set(4, 4, 4.);

        SparseMatrix B = new SparseMatrix(5, 5);
        B.set(0, 0, 1.);
        B.set(0, 1, 2.);
        B.set(0, 2, 3.);
        B.set(1, 0, 4.);
        B.set(1, 1, 5.);
        B.set(1, 2, 6.);
        B.set(2, 0, 7.);
        B.set(2, 1, 8.);
        B.set(2, 2, 9.);

        SparseMatrix result = new SparseMatrix(B);
        Assert.assertTrue(B.equals(B));
        Assert.assertTrue(result.equals(B));
        result.set(3, 3, 1.);
        result.set(3, 4, 2.);
        result.set(4, 3, 3.);
        result.set(4, 4, 4.);

        Assert.assertTrue(result.equals(A.add(B)));
        Assert.assertTrue(result.submatrix(0, 3).equals(B.submatrix(0, 3)));
        Assert.assertTrue(result.submatrix(3, 5).equals(A.submatrix(3, 5)));
    }

}
