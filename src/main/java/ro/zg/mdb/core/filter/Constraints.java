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
