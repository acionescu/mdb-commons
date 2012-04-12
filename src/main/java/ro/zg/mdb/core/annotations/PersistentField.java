package ro.zg.mdb.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import ro.zg.mdb.constants.PersistenceMetadataType;
import ro.zg.metadata.annotations.AnnotationType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@AnnotationType(PersistenceMetadataType.PERSISTENCE)
public @interface PersistentField {
    String name();
    Class<?>[] allowedTypes() default {};
}
