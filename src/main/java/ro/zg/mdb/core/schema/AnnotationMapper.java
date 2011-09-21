package ro.zg.mdb.core.schema;

import java.lang.annotation.Annotation;


public interface AnnotationMapper<A extends Annotation,C extends AnnotationMapperContext<A>> {

//    void map(T target, A annotation, H annotationHolder);
    void map(C amc);
}
