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

import java.util.Set;

import ro.zg.mdb.core.exceptions.MdbException;

public class AndObjectConstraintProcessor extends AbstractObjectConstraintProcessor{

    @Override
    public boolean apply(ObjectConstraintContext<?> occ) throws MdbException {
	if (occ.getPendingObjectContextsSize() > 1) {
	    ObjectConstraintContext<?> firstOcc = occ.popPendingObjectContext();
	    ObjectConstraintContext<?> secondOcc = occ.popPendingObjectContext();

	    if (firstOcc == secondOcc) {
		if (firstOcc != occ) {
		    return firstOcc.applyAnd();
		}
	    } else {
		return reconciliate(firstOcc, secondOcc);
	    }
	}
	occ.setIntersection(true);
	occ.setSimple(false);
	
	Set<String> allowed = getIntersection(occ.getAllowedRowsIds());
	Set<String> restricted = getUnion(occ.getRestrictedRowsIds());
	occ.pushAllowedRows(allowed);
	occ.pushRestrictedRows(restricted);

	if (!restricted.isEmpty()) {
	    if (!allowed.isEmpty()) {
		allowed.removeAll(restricted);
	    }
	    return true;
	}
	return !allowed.isEmpty();
    }

    @Override
    protected boolean process(ObjectConstraintContext<?> occ) throws MdbException {
	return occ.applyAnd();
    }

}
