package ro.zg.mdb.test;

import java.math.BigDecimal;

public class PrimitiveBigDecimalComparationTest {

    
    public static void main(String[] args) {
	
	int iterations=10000000;
	long start=System.currentTimeMillis();
	for(int i=0;i<iterations;i++) {
	    boolean test=Long.MAX_VALUE*Math.random() > Long.MAX_VALUE*Math.random();
	}
	long duration=System.currentTimeMillis()-start;
	System.out.println(iterations+" primitive comparations took "+(duration)+" ms");
	
	/* Long */
	 start=System.currentTimeMillis();
	for(int i=0;i<iterations;i++) {
	    int test=new Long((long)(Long.MAX_VALUE*Math.random())).compareTo(new Long((long)(Long.MAX_VALUE*Math.random())));
	}
	 duration=System.currentTimeMillis()-start;
	System.out.println(iterations+" Long comparations took "+(duration)+" ms");
	
	
	/* Bigdecimal*/
	start=System.currentTimeMillis();
	for(int i=0;i<iterations;i++) {
	    int test=new BigDecimal(Long.MAX_VALUE*Math.random()).compareTo(new BigDecimal(Long.MAX_VALUE*Math.random()));
	}
	duration=System.currentTimeMillis()-start;
	System.out.println(iterations+" BigDecimal comparations took "+(duration)+" ms");
	
    }
    
    
}
