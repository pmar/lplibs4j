package org.lplibs4j;

import java.util.BitSet;
import java.util.Random;

public class EstimateNumberOfDiffererentEpitopes {

    public static void main(String[] args) {
        int numpep = 1000000;
        int numacids = 20;
        int epilength = 5;
        int numepitopes = (int)Math.pow(numacids, epilength);

        BitSet bs = new BitSet();
        Random rng = new Random();
        for (int i = 0; i <numpep; i++) {
            int index = rng.nextInt(numepitopes);
            bs.set(index);
        }
        System.out.println(bs.cardinality());
		/*double[] running = new double[numepitopes];
		running[0] = 1.0;
		double prob = 1.0/Math.pow(numacids, epilength);
		for (int k = 1; k < numepitopes ; k++) {
			running[k] = running[k-1]*(1-k*prob);
		}
		
		double probalt = 1.0;
		double max = Double.NEGATIVE_INFINITY;
		int maxk = -1;
		for (int k = numepitopes; k >= 0; k--) {
			//System.out.println(k +" " + probalt + " " + running[k] +  "  "+ probalt * running[k]);
			if ((probalt * running[k])>max) {
				maxk = k;
				max = probalt * running[k];
			}
			probalt = probalt * (k*prob);
		}
		System.out.println(maxk);*/


    }
}
