package org.lplibs4j;

import junit.framework.TestCase;
import org.junit.Test;
import org.lplibs4j.api.implementation.SparseMatrix;
import org.lplibs4j.api.implementation.SparseVector;
import org.lplibs4j.api.util.Matrix;
import org.lplibs4j.api.util.NonZeroElementIterator;

// import org.junit.Before;

public class SparseVectorTest extends TestCase {

    @Test
    public void testThis () {
        assertTrue(Boolean.TRUE);
        assertFalse(Boolean.FALSE);
    }

    @Test
    public void testEquals () {

        SparseVector a1 = new SparseVector(5, 3);
        a1.set(0, 1.);
        a1.set(1, 2.);
        a1.set(3, 3.);

        assertTrue(a1.equals(a1));

        SparseVector a2 = new SparseVector(4, 3);
        a2.set(0, 1.);
        a2.set(1, 2.);
        a2.set(3, 3.);

        assertTrue(a2.equals(a2));

        assertFalse(a2.equals(a1));
        assertFalse(a1.equals(a2));

        SparseVector zero = new SparseVector(5, 0);

        assertTrue(zero.equals(zero));

        a1.set(0, 0.);
        a1.set(1, 0.);
        a1.set(3, 0.);

        assertTrue(zero.equals(a1));
        assertTrue(a1.equals(zero));

        a2.set(0, 0.);
        a2.set(1, 0.);
        a2.set(3, 0.);

        assertFalse(zero.equals(a2));
        assertFalse(a2.equals(zero));
    }

    @Test
    public void testCopyConstructor () {

        SparseVector a1 = new SparseVector (5, 3);
        a1.set(0, 1.);
        a1.set(1, 2.);
        a1.set(3, 3.);

        assertTrue(a1.equals(a1));

        SparseVector a2 = new SparseVector(a1);

        assertTrue(a2.equals(a1));
        assertTrue(a1.equals(a2));

        a2.set(0, 0.);

        assertFalse(a2.equals(a1));

        SparseVector a3 = new SparseVector(a1);

        assertFalse(a2.equals(a3));
        assertFalse(a3.equals(a2));

        a3.set(0, 0.);

        assertTrue(a3.equals(a2));
        assertFalse(a1.equals(a2));
        assertFalse(a3.equals(a1));
    }

    @Test
    public void testClone () {

        SparseVector a1 = new SparseVector (5, 3);
        a1.set(0, 1.);
        a1.set(1, 2.);
        a1.set(3, 3.);

        assertTrue(a1.equals(a1));

        SparseVector a2 = a1.clone();

        assertTrue(a2.equals(a1));
        assertTrue(a1.equals(a2));

        a2.set(0, 0.);

        assertFalse(a2.equals(a1));

        SparseVector a3 = a1.clone();

        assertFalse(a2.equals(a3));
        assertFalse(a3.equals(a2));

        a3.set(0, 0.);

        assertTrue(a3.equals(a2));
        assertFalse(a1.equals(a2));
        assertFalse(a3.equals(a1));
    }

    @Test
    public void testSet () {

        SparseVector a1 = new SparseVector(10, 3);
        a1.set(0, 1.);
        a1.set(1, 2.);
        a1.set(4, 3.);

        double[] expected = new double[] {1., 2., 0., 0., 3., 0., 0., 0., 0., 0.};
        for (int i = 0; i < a1.getSize(); i++)
            assertEquals(a1.get()[i], expected[i], 0.);

        a1.set(0, 0.);
        a1.set(1, 0.);
        a1.set(4, 0.);
        for (int i = 0; i < a1.getSize(); i++)
            assertEquals(a1.get()[i], 0., 0.);

        // maybe add more tests here?
    }

    @Test
    public void testNonZeroElementIterator () {
        SparseVector a1 = new SparseVector(10, 3);
        a1.set(0, 1.);
        a1.set(1, 2.);
        a1.set(4, 3.);

        int j = 0;
        NonZeroElementIterator iter = a1.getNonZeroElementIterator();
        while(iter.hasNext()) {
            iter.next();
            j++;
        }
        assertTrue(j == a1.getUsed());

        iter = a1.getNonZeroElementIterator();

        assertTrue(a1.getColNum() == 1);

        for(int i = 0; i < a1.getIndex().length && iter.hasNext(); i++) {
            assertEquals(0, iter.getActuali());
            assertEquals(a1.get(a1.getIndex()[i]), iter.next(), 0.);
            assertEquals(a1.getIndex()[i], iter.getActualj());
        }

        SparseVector a2 = new SparseVector(5, 0);
        iter = a2.getNonZeroElementIterator();
        assertFalse(iter.hasNext());


    }

    @Test
    public void testPlus () {

        SparseVector v1 = new SparseVector(10, 3);
        v1.set(0, 1.);
        v1.set(2, 2.);
        v1.set(4, 3.);

        SparseVector v2 = new SparseVector (10, 3);
        v2.set(0, 3.);
        v2.set(2, 2.);
        v2.set(4, 1.);

        SparseVector result = v1.plus(v2);
        NonZeroElementIterator iter = result.getNonZeroElementIterator();

        while(iter.hasNext())
            assertEquals(4., iter.next());

        v1.set(0,0.);
        v1.set(2,0.);
        v1.set(4,0.);

        SparseVector result2 = v1.plus(v2);
        NonZeroElementIterator iter2 = result2.getNonZeroElementIterator();
        iter = v2.getNonZeroElementIterator();

        while(iter.hasNext() && iter2.hasNext())
            assertEquals(iter.next(), iter2.next());

        assertFalse(iter.hasNext() || iter2.hasNext());

        v1.set(0,1.);
        v1.set(2,1.);
        v1.set(4,1.);

        SparseVector v3 = new SparseVector(10, 7);
        v3.set(1,1.);
        v3.set(3,1.);
        v3.set(5,1.);
        v3.set(6,1.);
        v3.set(7,1.);
        v3.set(8,1.);
        v3.set(9,1.);

        result = v3.plus(v1);
        iter = result.getNonZeroElementIterator();

        while (iter.hasNext())
            assertEquals(1., iter.next());

        assertFalse(iter.hasNext());

        result = (SparseVector) v3.plus(v3.times(-1.));
        iter = result.getNonZeroElementIterator();

        while (iter.hasNext())
            assertEquals(0., iter.next());

        assertFalse(iter.hasNext());
    }

    @Test
    public void testTimes () {
        SparseVector vnull = new SparseVector(10,0);

        SparseVector v1 = new SparseVector(new double[]{1., 0., 0., 2., 0., 3., 0., 0., 0., 0.});
        SparseVector v2 = new SparseVector(new double[]{1., 0., 2., 0., 3., 0., 0., 0., 0., 0.});

        Matrix result = vnull.transpose().times(v1);
        assertEquals(0., result.get(0, 0), 0.);

        result = v1.transpose().times(vnull);
        assertEquals(0., result.get(0, 0), 0.);

        result = v1.transpose().times(v2);

        assertEquals(1., result.get(0,0), 0.);
        assertEquals(1, result.getRowNum());
        assertEquals(1, result.getColNum());

        result = v1.times(v2.transpose());

        assertEquals(10, result.getRowNum());
        assertEquals(10, result.getColNum());

        assertTrue(result instanceof SparseMatrix);

        Matrix expected = new SparseMatrix(10, 10);
        expected.set(0, 0, 1.);
        expected.set(0, 2, 2.);
        expected.set(0, 4, 3.);
        expected.set(3, 0, 2.);
        expected.set(3, 2, 4.);
        expected.set(3, 4, 6.);
        expected.set(5, 0, 3.);
        expected.set(5, 2, 6.);
        expected.set(5, 4, 9.);

        assertTrue(result.equals(expected));

        assertTrue(result.equals(expected));
		/*
		result = v1.transpose().times(v1);
		
		assertEquals(14., result.get(0, 0), 0.);
		assertEquals(1, result.getRowNum());
		assertEquals(1, result.getColNum());
		*/
        assertEquals(1, v2.transpose().getRowNum());
        assertEquals(10, v2.transpose().getColNum());

        result = v2.transpose().times(result);

        assertEquals(1, result.getRowNum());
        assertEquals(10, result.getColNum());

        expected = new SparseVector(10, 3);
        expected.set(0, 0, 1.);
        expected.set(2, 0, 2.);
        expected.set(4, 0, 3.);

        assertTrue(result.equals(expected));
    }

    @Test
    public void testDot ()  {
        double[] v1values = new double[]{1., 0., 0., 2., 0., 3., 0., 0., 0., 0.};
        double[] v2values = new double[]{1., 0., 2., 0., 3., 0., 0., 0., 0., 0.};

        SparseVector v1 = new SparseVector (v1values);
        SparseVector v2 = new SparseVector (v2values);

        assertEquals(14., v1.dot(v1), 0.);
        assertEquals(14., v2.dot(v2), 0.);
        assertEquals(1., v1.dot(v2), 0.);
        assertEquals(1., v2.dot(v1), 0.);

        assertEquals(14., v1.dot(v1values));
        assertEquals(14., v2.dot(v2values));
        assertEquals(1., v1.dot(v2values));
        assertEquals(1., v2.dot(v1values));

        double[] v3values = new double[]{3., 0., 3., 0., 0., 2., 0., 0., 0., 0};

        SparseVector v3 = new SparseVector (v3values);

        assertEquals(9., v1.dot(v3), 0.);
        assertEquals(9., v3.dot(v1), 0.);
        assertEquals(9., v2.dot(v3), 0.);
        assertEquals(9., v3.dot(v2), 0.);

        assertEquals(9., v1.dot(v3values), 0.);
        assertEquals(9., v2.dot(v3values), 0.);

        double[] v4values = new double[]{1., 0., 0., 0., 0., 0., 0., 1., 0., 1.};
        double[] v5values = new double[]{1., 0., 1., 1., 1., 0., 1., 0., 1., 1.};

        SparseVector v4 = new SparseVector (v4values);
        SparseVector v5 = new SparseVector (v5values);

        assertEquals(2., v4.dot(v5));
        assertEquals(2., v5.dot(v4));
        assertEquals(2., v4.dot(v5values));
        assertEquals(2., v5.dot(v4values));
    }
}
