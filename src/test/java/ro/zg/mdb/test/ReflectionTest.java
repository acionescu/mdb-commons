package ro.zg.mdb.test;

import java.lang.annotation.Annotation;
import java.lang.annotation.Target;
import java.util.Arrays;

public class ReflectionTest {

    public static void main(String[] args) throws Exception {
//	Class<?> clazz = Class.forName("ro.zg.mdb.core.meta.persistence.data.PersistentObjectMetadataImpl");
//	for(Annotation a : clazz.getAnnotations()) {
//	    System.out.println(Arrays.toString(((Target)a.annotationType().getAnnotation(Target.class)).value()));
//	}
	
//	System.out.println(Class.class.isAssignableFrom(Class.class));
	System.out.println(Class.class.getSuperclass().equals(Class.class));
    }
    
}
