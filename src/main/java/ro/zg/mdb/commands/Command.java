package ro.zg.mdb.commands;

import ro.zg.mdb.core.exceptions.MdbException;

public interface Command<T> {
   
    T execute() throws MdbException;
}
