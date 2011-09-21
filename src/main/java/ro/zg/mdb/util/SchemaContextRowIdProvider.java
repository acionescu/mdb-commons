package ro.zg.mdb.util;

import ro.zg.mdb.core.exceptions.MdbException;
import ro.zg.mdb.core.meta.ObjectDataModel;
import ro.zg.mdb.core.meta.SchemaContext;

public class SchemaContextRowIdProvider<T> implements RowIdProvider {
    private SchemaContext schemaContext;
    private ObjectDataModel<T> objectDataModel;

    public SchemaContextRowIdProvider(SchemaContext schemaContext, ObjectDataModel<T> objectDataModel) {
	super();
	this.schemaContext = schemaContext;
	this.objectDataModel = objectDataModel;
    }

    @Override
    public String provideRowId() throws MdbException {
	return "" + schemaContext.getNextValForSequence(objectDataModel.getTypeName());
    }

}
