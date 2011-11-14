/*******************************************************************************
 * Copyright (c) 2011 Adrian Cristian Ionescu.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Adrian Cristian Ionescu - initial API and implementation
 ******************************************************************************/
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
