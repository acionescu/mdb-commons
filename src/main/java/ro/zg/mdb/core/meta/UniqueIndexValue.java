package ro.zg.mdb.core.meta;

import ro.zg.mdb.constants.Constants;

public class UniqueIndexValue {
    private String name;
    private String value;
    private boolean composite=false;
    
    public UniqueIndexValue(String name) {
	super();
	this.name = name;
    }

    public void addValue(String v) {
	if(value==null) {
	    value=v;
	}
	else {
	    value+=Constants.COMPOSITE_INDEX_SEPARATOR+v;
	    composite=true;
	}
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @return the composite
     */
    public boolean isComposite() {
        return composite;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "UniqueIndexValue [name=" + name + ", value=" + value + ", composite=" + composite + "]";
    }
    
}
