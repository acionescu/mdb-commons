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

import junit.framework.Assert;

import org.junit.Test;

import ro.zg.mdb.core.filter.Constraints;

public class ConstraintsTest {

    @Test
    public void testConstraints() {
	/* eq */
	Assert.assertTrue(Constraints.isSatisfied(Constraints.eq(5), 5));
	Assert.assertFalse(Constraints.isSatisfied(Constraints.eq(5), 7));
	Assert.assertFalse(Constraints.isSatisfied(Constraints.eq(5).not(), 5));
	Assert.assertTrue(Constraints.isSatisfied(Constraints.eq(5).not(), 9));
	
	/* gt */
	Assert.assertFalse(Constraints.isSatisfied(Constraints.gt(5), 5));
	Assert.assertTrue(Constraints.isSatisfied(Constraints.gt(5,true), 5));
	Assert.assertFalse(Constraints.isSatisfied(Constraints.gt(5), 2));
	Assert.assertTrue(Constraints.isSatisfied(Constraints.gt(5), 34));
	Assert.assertTrue(Constraints.isSatisfied(Constraints.gt(5).not(), 2));
	
	/* lt */
	Assert.assertFalse(Constraints.isSatisfied(Constraints.lt(5), 5));
	Assert.assertTrue(Constraints.isSatisfied(Constraints.lt(5,true), 5));
	Assert.assertFalse(Constraints.isSatisfied(Constraints.lt(5), 7));
	Assert.assertTrue(Constraints.isSatisfied(Constraints.lt(5), 1));
	Assert.assertFalse(Constraints.isSatisfied(Constraints.lt(5).not(), 1));
	
	/* between */
	Assert.assertTrue(Constraints.isSatisfied(Constraints.between(1,10),5));
	Assert.assertFalse(Constraints.isSatisfied(Constraints.between(1,10),1));
	Assert.assertFalse(Constraints.isSatisfied(Constraints.between(1,10),10));
	Assert.assertTrue(Constraints.isSatisfied(Constraints.between(1,10,true,false),1));
	Assert.assertTrue(Constraints.isSatisfied(Constraints.between(1,10,false,true),10));
	Assert.assertFalse(Constraints.isSatisfied(Constraints.between(1,10),-3));
	Assert.assertFalse(Constraints.isSatisfied(Constraints.between(1,10),30));
	Assert.assertFalse(Constraints.isSatisfied(Constraints.between(1,10).not(),5));
	Assert.assertTrue(Constraints.isSatisfied(Constraints.between(1,10).not(),-5));
	Assert.assertTrue(Constraints.isSatisfied(Constraints.between(1,10).not(),20));
	
    }

}
