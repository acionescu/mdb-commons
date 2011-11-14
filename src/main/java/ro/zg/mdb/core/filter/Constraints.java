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
package ro.zg.mdb.core.filter;

import java.math.BigDecimal;

import ro.zg.mdb.core.filter.constraints.Between;
import ro.zg.mdb.core.filter.constraints.Eq;
import ro.zg.mdb.core.filter.constraints.Gt;
import ro.zg.mdb.core.filter.constraints.Lt;
import ro.zg.mdb.core.filter.constraints.Not;
import ro.zg.mdb.core.filter.constraints.Or;

public class Constraints {
    public static <T> Eq<T> eq(T value){
	return new Eq<T>(value);
    }
    
    public static Gt gt(int limit) {
	return new Gt(new BigDecimal(limit));
    }
    
    public static Gt gt(int limit,boolean inclusive) {
	return new Gt(new BigDecimal(limit),inclusive);
    }
    
    public static Lt lt(int limit) {
	return new Lt(new BigDecimal(limit));
    }
    
    public static Lt lt(int limit,boolean inclusive) {
	return new Lt(new BigDecimal(limit),inclusive);
    }
    
    public static Between between(int min,int max) {
	return new Between(new BigDecimal(min), new BigDecimal(max));
    }
    
    public static Between between(int min,int max,boolean minInclusive, boolean maxInclusive) {
	return new Between(new BigDecimal(min), new BigDecimal(max),minInclusive,maxInclusive);
    }
    
    public static <T> boolean isSatisfied(Eq<T> eq, T value) {
	return eq.isSatisfied(value);
    }
    
    public static boolean isSatisfied(Gt gt, int value) {
	return gt.isSatisfied(new BigDecimal(value));
    }
    
    public static boolean isSatisfied(Lt lt, int value) {
	return lt.isSatisfied(new BigDecimal(value));
    }
    
    public static boolean isSatisfied(Between bt, int value) {
	return bt.isSatisfied(new BigDecimal(value));
    }
    
    public static <T> boolean isSatisfied(Not<T> not, T value) {
	return not.isSatisfied(value);
    }
    
    public static <T> boolean isSatisfied(Or<T> or, T value) {
	return or.isSatisfied(value);
    }
    
    public static boolean isSatisfied(Or<BigDecimal> or, int value) {
	return or.isSatisfied(new BigDecimal(value));
    }
}
