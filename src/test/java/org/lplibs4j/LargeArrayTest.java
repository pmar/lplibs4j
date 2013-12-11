package org.lplibs4j;

public class LargeArrayTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        long time = System.currentTimeMillis();
        byte[] arr = new byte[1<<22];
        time =  System.currentTimeMillis() -time;
        System.out.println(time);
        for (int i = 0; i < arr.length; i++) {
            arr[i]=23;
        }
    }

}
