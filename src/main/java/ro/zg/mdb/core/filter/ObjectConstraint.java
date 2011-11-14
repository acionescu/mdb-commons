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

import java.util.Map;

import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.mdb.core.meta.data.ObjectDataModel;

public interface ObjectConstraint {
    boolean process(ObjectConstraintContext<?> objectContext) throws MdbException;
    boolean isSatisfied(Map<String,Object> values);
    boolean isPossible(ObjectDataModel<?> objectDataModel);
}
