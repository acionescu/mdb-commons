package ro.zg.mdb.util;

import ro.zg.mdb.core.exceptions.MdbException;

public interface RowIdProvider {
    String provideRowId() throws MdbException;
}
