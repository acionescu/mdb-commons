package ro.zg.mdb.core.filter;

import java.util.Map;

import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.mdb.core.meta.ObjectDataModel;

public interface ObjectConstraint {
    boolean process(ObjectConstraintContext objectContext) throws MdbException;
    boolean isSatisfied(Map<String,Object> values);
    boolean isPossible(ObjectDataModel objectDataModel);
}
