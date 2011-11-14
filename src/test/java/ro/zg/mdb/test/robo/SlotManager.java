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
package ro.zg.mdb.test.robo;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;

import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.mdb.core.meta.SchemaManager;

public class SlotManager {
    private SchemaManager dao;
    
    private int inserts=0;
    private int updates=0;
    private int reads=0;
    private int deletes=0;
    
    private int insertErrors=0;
    private int updateErrors=0;
    private int readErrors=0;
    private int deleteErrors=0;
    
    public SlotManager(SchemaManager dao) {
	super();
	this.dao = dao;
    }

    public Slot createSlot(Slot slot) {
	try {
	    inserts++;
	    return dao.createCommand(Slot.class).insert(slot).execute();
	} catch (MdbException e) {
//	    e.printStackTrace();
	    insertErrors++;
	}
	return null;
    }
    
    public Slot readSlot(int position) {
	try {
	    reads++;
	    Collection<Slot> slots = dao.createCommand(Slot.class).get().where().field("position").eq(position).execute();
	    if(slots.size()==0) {
		return null;
	    }
	    return new ArrayList<Slot>(slots).get(0);
	} catch (MdbException e) {
	    readErrors++;
	    e.printStackTrace();
	}
	return null;
    }
    
    public long updateSlot(Slot slot) {
	try {
	    updates++;
	    return dao.createCommand(Slot.class).update(slot).where().field("position").eq(slot.getPosition()).execute();
	} catch (MdbException e) {
	    updateErrors++;
	    throw new RuntimeException(e);
//	    return 0;
	}
    }
    
    public long deleteSlot(int position) {
	try {
	    deletes++;
	    return dao.createCommand(Slot.class).delete().where().field("position").eq(position).execute();
	} catch (MdbException e) {
	    deleteErrors++;
	    e.printStackTrace();
	}
	return 0;
    }
    
    public void printStats(PrintStream ps) {
	StringBuffer sb=new StringBuffer();
	sb.append("\ninserts: "+inserts);
	sb.append("\nupdates: "+updates);
	sb.append("\nreads: "+reads);
	sb.append("\ndeletes: "+deletes);
	sb.append("\ninsertErrors: "+insertErrors);
	sb.append("\nupdateErrors: "+updateErrors);
	sb.append("\nreadErrors: "+readErrors);
	sb.append("\ndeleteErrors: "+deleteErrors);
	ps.println(sb.toString());
    }
}
