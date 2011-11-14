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
package ro.zg.mdb.util;

import java.util.ArrayList;
import java.util.Collection;

import ro.zg.mdb.core.meta.data.ObjectsLink;

public class ObjectsLinkUtil {
    public static Collection<String> getRows(Collection<ObjectsLink> links, boolean first){
	Collection<String> result=new ArrayList<String>();
	if(links == null) {
	    return result;
	}
	for(ObjectsLink ol : links) {
	    if(first) {
		result.add(ol.getFirstRowId());
	    }
	    else {
		result.add(ol.getSecondRowId());
	    }
	}
	return result;
    }
}
