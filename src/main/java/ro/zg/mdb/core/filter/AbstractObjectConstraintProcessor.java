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

import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

import ro.zg.mdb.core.exceptions.MdbException;

public abstract class AbstractObjectConstraintProcessor implements ObjectConstraintProcessor {

    protected Set<String> getIntersection(Deque<Set<String>> stack) {
	Set<String> s1 = null;
	Set<String> s2 = null;

	if (stack.size() > 0) {
	    s1 = stack.pop();
	} else {
	    return new HashSet<String>();
	}
	if (stack.size() > 0) {
	    s2 = stack.pop();
	} else {
	    return s1;
	}

	s1.retainAll(s2);
	return s1;
    }

    protected Set<String> getUnion(Deque<Set<String>> stack) {
	Set<String> s1 = null;
	Set<String> s2 = null;

	if (stack.size() > 0) {
	    s1 = stack.pop();
	} else {
	    return new HashSet<String>();
	}
	if (stack.size() > 0) {
	    s2 = stack.pop();
	} else {
	    return s1;
	}

	s1.addAll(s2);
	return s1;
    }

    protected boolean reconciliate(ObjectConstraintContext<?> firstOcc, ObjectConstraintContext<?> secondOcc)
	    throws MdbException {

	if (firstOcc.getDepth() == secondOcc.getDepth()) {
	    if (firstOcc == secondOcc) {
		return process(firstOcc);
	    } else {
		ObjectConstraintContext<?> firstParentOcc = firstOcc.getParentContext();
		ObjectConstraintContext<?> secondParentOcc = secondOcc.getParentContext();
		reconciliateWithNested(firstParentOcc, firstOcc);
		reconciliateWithNested(secondParentOcc, secondOcc);
		return reconciliate(firstParentOcc, secondParentOcc);
	    }
	} else if (firstOcc.getDepth() > secondOcc.getDepth()) {
	    ObjectConstraintContext<?> firstParentOcc = firstOcc.getParentContext();
	    reconciliateWithNested(firstParentOcc, firstOcc);
	    return reconciliate(firstParentOcc, secondOcc);
	} else {
	    ObjectConstraintContext<?> secondParentOcc = secondOcc.getParentContext();
	    reconciliateWithNested(secondParentOcc, secondOcc);
	    return reconciliate(firstOcc, secondParentOcc);
	}
    }

    private void reconciliateWithNested(ObjectConstraintContext<?> parent, ObjectConstraintContext<?> nestedContext)
	    throws MdbException {
	boolean rowsExtracted = parent.extractRowsFromNested(nestedContext);
	if (rowsExtracted) {
	    process(parent);
	}
    }

    protected abstract boolean process(ObjectConstraintContext<?> occ) throws MdbException;

}
