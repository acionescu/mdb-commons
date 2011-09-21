package ro.zg.mdb.core.schema;

import java.lang.annotation.Annotation;

public class AnnotationMapperContext<A extends Annotation> {
    private A annotation;
    

    public AnnotationMapperContext(A annotation) {
	super();
	this.annotation = annotation;
    }

    /**
     * @return the annotation
     */
    public A getAnnotation() {
        return annotation;
    }

    /**
     * @param annotation the annotation to set
     */
    public void setAnnotation(A annotation) {
        this.annotation = annotation;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "AnnotationMapperContext [annotation=" + annotation + "]";
    }
    
}
