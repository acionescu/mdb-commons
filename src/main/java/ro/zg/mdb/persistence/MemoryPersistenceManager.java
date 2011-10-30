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

import java.io.PrintStream;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import ro.zg.util.io.file.FileHandler;
import ro.zg.util.io.file.LineHandler;

public class MemoryPersistenceManager implements PersistenceManager {
    private String separator = "/";
    private String rootNodePath = "";
    private Node rootNode;

    public MemoryPersistenceManager() {
	super();
	rootNode = new Node(rootNodePath);
	rootNode.root = true;
    }

    @Override
    public void append(String id, String data) throws PersistenceException {
	rootNode.getNode(id, true).appendData(data);

    }

    @Override
    public long countChildren(String id) throws PersistenceException {
	Node child = rootNode.getNode(id, false);
	if (child != null) {
	    return child.childrenCount();
	}
	return 0;
    }

    @Override
    public boolean exists(String id) throws PersistenceException {
	Node child = rootNode.getNode(id, false);
	return child != null;
    }

    @Override
    public String getPath(String... ids) {
	boolean first = true;
	String path = "";
	for (String id : ids) {
	    if (first) {
		path = id;
		first = false;
	    } else {
		path += separator + id;
	    }
	}
	return path;
    }

    @Override
    public String getPath(String p1, String p2) {
	return p1 + separator + p2;
    }

    @Override
    public PersistenceManager getPersistenceManager(String id) throws PersistenceException {
	MemoryPersistenceManager mpm = new MemoryPersistenceManager();
	mpm.rootNode = rootNode.getNode(id, true);
	return mpm;
    }

    @Override
    public String[] listChildren(String parentId) throws PersistenceException {
	return rootNode.listChildren(parentId);
    }

    @Override
    public void read(String id, LineHandler handler) throws PersistenceException {
	Node node = rootNode.getNode(id, false);
	readNode(node, handler);
    }

    private void readNode(Node node, LineHandler handler) {
	if (node != null && node.isLeaf()) {
	    for (String line : node.getData()) {
		if (!handler.handle(line)) {
		    break;
		}
	    }
	}
    }

    @Override
    public void readAllChildren(String parentId, FileHandler handler) throws PersistenceException {
	Node node = rootNode.getNode(parentId, false);
	if (node != null) {
	    for (Node child : node.getChildren().values()) {
		String id = child.getId();
		if (!handler.startFile(id)) {
		    continue;
		}
		readNode(child, handler);
		if (!handler.endFile(id)) {
		    break;
		}
	    }
	}
    }

    @Override
    public void write(String id, String data) throws PersistenceException {
	rootNode.getNode(id, true).writeData(data);
    }

    public void print(PrintStream printStream) {
	rootNode.print(printStream, "");
    }

    private class Node {
	private String id;
	private Set<String> data = Collections.synchronizedSet(new LinkedHashSet<String>());
	private Map<String, Node> children = Collections.synchronizedMap(new LinkedHashMap<String, Node>());
	private boolean leaf;
	private boolean root;

	public Node(String id) {
	    super();
	    this.id = id;
	}

	public void appendData(String line) {
	    data.add(line);
	    leaf = true;
	}

	public void writeData(String line) {
	    data.clear();
	    appendData(line);
	}

	public int childrenCount() {
	    return children.size();
	}

	public boolean childExists(String id) throws PersistenceException {
	    Node child = getNode(id, false);
	    return child != null;
	}

	public String[] listChildren(String id) throws PersistenceException {
	    Node node = getNode(id, false);
	    if (node == null) {
		return new String[0];
	    }
	    return node.children.keySet().toArray(new String[0]);
	}

	public Node getNode(String path, boolean create) throws PersistenceException {
	    return getNode(path, create, true);
	}

	public Node getNode(String path, boolean create, boolean allowSelf) throws PersistenceException {
	    int separatorIndex = path.indexOf(separator);
	    Node child = null;
	    String childName = null;
	    if (separatorIndex < 0) {
		childName = path;
	    } else {
		childName = path.substring(0, separatorIndex);
	    }
	    if (leaf) {
		if (create && separatorIndex > 0) {
		    throw new PersistenceException("Failed to create path '" + path + "' under leaf '" + id + "'");
		}
		return null;
	    }
	    if (allowSelf && id.equals(childName)) {
		child = this;
	    } else {
		child = getChild(childName,create);
		if(child==null) {
		    return null;
		}
	    }
	    
	    if (separatorIndex < 0) {
		return child;
	    }

	    String rest = "";
	    if (separatorIndex < path.length() - 1) {
		rest = path.substring(separatorIndex + 1);
	    }
	    return child.getNode(rest, create, false);
	}
	
	private synchronized Node getChild(String id,boolean create) {
	    Node child = children.get(id);
	    if(child == null && create) {
		return addChild(id);
	    }
	    return child;
	}

	public boolean deleteChild(String id) {
	    return deleteChild(id, true);
	}

	public boolean deleteChild(String id, boolean allowSelf) {
	    if (allowSelf && !root && id.startsWith(this.id)) {
		id = id.substring(this.id.length() + 1);
	    }

	    int separatorIndex = id.indexOf(separator);
	    if (separatorIndex < 0) {
		Node removed = removeChild(id);
		if (removed == null) {
		    System.out.println("deleted vax " + id);
		}
		return true;
	    }
	    String childName = id.substring(0, separatorIndex);
	    Node n = getChild(childName,false);
	    if (n != null) {
		String rest = "";
		if (separatorIndex < id.length() - 1) {
		    rest = id.substring(separatorIndex + 1);
		}
		return n.deleteChild(rest, false);
	    }
	    return true;
	}
	
	private synchronized Node removeChild(String id) {
	    return children.remove(id);
	}

	private Node addChild(String name) {
	    Node child = new Node(name);
	    children.put(name, child);
	    return child;
	}

	/**
	 * @return the id
	 */
	public String getId() {
	    return id;
	}

	/**
	 * @return the children
	 */
	public Map<String, Node> getChildren() {
	    return new LinkedHashMap<String, Node>(children);
	}

	/**
	 * @return the data
	 */
	public Set<String> getData() {
	    return data;
	}

	/**
	 * @return the leaf
	 */
	public boolean isLeaf() {
	    return leaf;
	}

	/**
	 * @return the root
	 */
	public boolean isRoot() {
	    return root;
	}

	public void print(PrintStream pm, String offset) {
	    if (leaf) {
		pm.println(offset + id + ":" + data);
		return;
	    }
	    pm.println(offset + id);
	    offset = offset + " ";
	    for (Node child : new LinkedHashSet<Node>(children.values())) {
		child.print(pm, offset);
	    }
	}
    }

    @Override
    public boolean create(String id) throws PersistenceException {
	if(exists(id)) {
	    System.out.println("overwriting node "+id);
	}
	Node n = rootNode.getNode(id, true);
	return (n != null);
    }

    @Override
    public boolean delete(String id) {
	return rootNode.deleteChild(id);
    }

}
