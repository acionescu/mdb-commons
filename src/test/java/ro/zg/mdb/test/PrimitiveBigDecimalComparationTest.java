/*******************************************************************************
 * Copyright 2011 Adrian Cristian Ionescu
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
