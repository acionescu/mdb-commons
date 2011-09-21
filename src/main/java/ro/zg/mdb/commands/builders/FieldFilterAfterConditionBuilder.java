package ro.zg.mdb.commands.builders;

public class FieldFilterAfterConditionBuilder<T,R> extends FieldFilterBuilder<T, R> {

    public FieldFilterAfterConditionBuilder(FilterBuilderContext<T, R> filterContext) {
	super(filterContext);
    }

    public FieldFilterBuilder<T,R> field(String fieldName) {
	return filterContext.setCurrentField(fieldName);
    }
}
