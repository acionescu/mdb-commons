package ro.zg.mdb.core.schema;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import ro.zg.mdb.core.annotations.Indexed;
import ro.zg.mdb.core.annotations.PrimaryKey;
import ro.zg.mdb.core.annotations.Required;
import ro.zg.mdb.core.annotations.Unique;

public class AnnotationMappersManager<A extends Annotation>{
    private Map<Class<?>,ObjectDataModelAnnotationMapper<? extends Annotation>> mappers;
    public AnnotationMappersManager() {
	mappers = new HashMap<Class<?>, ObjectDataModelAnnotationMapper<? extends Annotation>>();
	mappers.put(Required.class, new RequiredMapper());
	mappers.put(PrimaryKey.class, new PrimaryKeyMapper());
	mappers.put(Unique.class, new UniqueMapper());
	mappers.put(Indexed.class, new IndexMapper());
    }
    
    public void map(ObjectDataModelAnnotationMapperContext<A> amc) {
	ObjectDataModelAnnotationMapper mapper = mappers.get(amc.getAnnotation().annotationType());
	if(mapper!=null) {
	    mapper.map(amc);
	}
	else {
	    System.out.println("No mapper for "+amc);
	}
    }
}
