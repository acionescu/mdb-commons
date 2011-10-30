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
