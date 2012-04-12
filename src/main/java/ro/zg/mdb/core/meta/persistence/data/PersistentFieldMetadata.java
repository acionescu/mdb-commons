package ro.zg.mdb.core.meta.persistence.data;

import java.util.Collection;

import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.metadata.commons.FieldMetadata;

public interface PersistentFieldMetadata<T> extends FieldMetadata<T> {
    
    boolean isIndexed();

    LinkMetadata getLinkMetadata();

    String getSequenceId();

    String getUniqueIndexId();
    
    Object createFromValue(Collection<?> values) throws MdbException;
    
    boolean isObjectId();

}
