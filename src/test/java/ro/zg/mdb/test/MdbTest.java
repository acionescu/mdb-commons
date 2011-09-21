package ro.zg.mdb.test;

import org.junit.Assert;
import org.junit.Test;

public class MdbTest {

    @Test
    public void testNotPersistableObject() {
	try {
//	    pm.save(new Object());
	    Assert.fail("Should have failed because the object is not persistable");
	} catch (Exception e) {

	}
    }
}
