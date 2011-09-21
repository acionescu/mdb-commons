package ro.zg.mdb.test;

import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;

import ro.zg.mdb.persistence.MemoryPersistenceManager;
import ro.zg.mdb.util.FullReader;

public class MemoryPersistenceManagerTest {
    
    @Test
    public void testMemoryPersistenceManager() throws Exception{
	MemoryPersistenceManager mpm = new MemoryPersistenceManager();
	String[] names=new String[] {"1","2/3","3/6/8"};
	String[] data1=new String[] {"here","we","go"};
	String[] data2=new String[] {"are","you","there"};
 	
	Assert.assertFalse(mpm.exists(names[0]));
	for(int i=0;i<names.length;i++) {
	    mpm.write(names[i], data1[i]);
	    Assert.assertTrue(mpm.exists(names[i]));
	    
	    FullReader reader = new FullReader();
	    mpm.read(names[i], reader);
	    Assert.assertTrue(reader.getData().contains(data1[i]));
	}
	
	for(int i=0;i<names.length;i++) {
	    mpm.append(names[i], data2[i]);
	    
	    FullReader reader = new FullReader();
	    mpm.read(names[i], reader);
	    Set<String> data=reader.getData();
	    Assert.assertTrue(data.contains(data2[i]));
	    Assert.assertEquals(2, data.size());
	}
	
	for(int i=0;i<names.length;i++) {
	    mpm.write(names[i], data1[i]);
	    Assert.assertTrue(mpm.exists(names[i]));
	    
	    FullReader reader = new FullReader();
	    mpm.read(names[i], reader);
	    Set<String> data=reader.getData();
	    Assert.assertTrue(data.contains(data1[i]));
	    Assert.assertFalse(data.contains(data2[i]));
	    Assert.assertEquals(1, data.size());
	}
	
	Assert.assertTrue(mpm.exists(names[1]));
	mpm.delete(names[1]);
	Assert.assertFalse(mpm.exists(names[1]));
	
	boolean created1 = mpm.create("9/9/.9");
	Assert.assertTrue(mpm.exists("9/9"));
	mpm.print(System.out);
    }
    
}
