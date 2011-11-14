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

import org.junit.Assert;
import org.junit.Test;

public class MdbTest {

    @Test
    public void testNotPersistableObject() {
	try {
//	    pm.save(new Object());
	    Assert.fail("Should have failed because the object is not persistable");
	} catch (Exception e) {

	}
    }
}
