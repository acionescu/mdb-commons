package ro.zg.mdb.core.filter.constraints;



public class Range<T extends Comparable<T>> {
    private T minValue;
    private T maxValue;
    private Class<?> type;

    public Range(T minValue, T maxValue) {
	super();
	this.minValue = minValue;
	this.maxValue = maxValue;
	if(minValue != null) {
	    type=minValue.getClass();
	}
	else if(maxValue != null) {
	    type=maxValue.getClass();
	}
    }

    /**
     * @return the minValue
     */
    public T getMinValue() {
	return minValue;
    }

    /**
     * @return the maxValue
     */
    public T getMaxValue() {
	return maxValue;
    }
    
    /**
     * 
     * @param value
     * @return 0 if the value is in this range
     * <br/>
     * -1 if the value is equal with min
     * <br/>
     * 1 if the value is equal with max
     * <br/>
     * -2 if value is less than min
     * <br/>
     * 2 if value is more than max
     */
    public int checkValue(T value) {
	int minComp = minValue.compareTo(value);
	if(minComp==0) {
	    return -1;
	}
	else if(minComp < 0) {
	    return -2;
	}
	
	return maxValue.compareTo(value)+1;
    }
    
    @SuppressWarnings("unchecked")
    public T fromString(String value) {
	try {
	    return (T)type.getConstructor(new Class<?>[] {String.class}).newInstance(new Object[] {value});
	} catch (Exception e) {
	    throw new IllegalArgumentException("Failed to create object for value "+value,e);
	} 
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((maxValue == null) ? 0 : maxValue.hashCode());
	result = prime * result + ((minValue == null) ? 0 : minValue.hashCode());
	result = prime * result + ((type == null) ? 0 : type.hashCode());
	return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	Range other = (Range) obj;
	if (maxValue == null) {
	    if (other.maxValue != null)
		return false;
	} else if (!maxValue.equals(other.maxValue))
	    return false;
	if (minValue == null) {
	    if (other.minValue != null)
		return false;
	} else if (!minValue.equals(other.minValue))
	    return false;
	if (type == null) {
	    if (other.type != null)
		return false;
	} else if (!type.equals(other.type))
	    return false;
	return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "Range [minValue=" + minValue + ", maxValue=" + maxValue + ", type=" + type + "]";
    }
    
}
