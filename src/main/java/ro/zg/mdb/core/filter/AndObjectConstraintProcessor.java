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
