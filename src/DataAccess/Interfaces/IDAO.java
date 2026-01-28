package DataAccess.interfaces;

import java.util.List;

import Infrastructure.exeption.AppException;

public interface IDAO<T> {
    List<T> readAll()            throws AppException;
    T       readBy (Integer id)  throws AppException;
    boolean create (T entity)    throws AppException;
    boolean update (T entity)    throws AppException;
    boolean delete (Integer id)  throws AppException;
    Integer getCountReg()        throws AppException;
    Integer getMinReg(String tableCelName) throws AppException;
    Integer getMaxReg(String tableCelName) throws AppException;
}
