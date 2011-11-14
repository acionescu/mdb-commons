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
package ro.zg.mdb.persistence;

import ro.zg.util.io.file.FileHandler;
import ro.zg.util.io.file.LineHandler;

public interface PersistenceManager {

    void read(String id, LineHandler handler) throws PersistenceException;

    void readAllChildren(String parentId, FileHandler handler) throws PersistenceException;

    String[] listChildren(String parentId) throws PersistenceException;
    
    boolean create(String id) throws PersistenceException;

    void write(String id, String data) throws PersistenceException;

    void append(String id, String data) throws PersistenceException;

    boolean exists(String id) throws PersistenceException;
    
    boolean delete(String id);

    long countChildren(String id) throws PersistenceException;

    String getPath(String... ids);

    String getPath(String p1, String p2);

    PersistenceManager getPersistenceManager(String id) throws PersistenceException;
}
