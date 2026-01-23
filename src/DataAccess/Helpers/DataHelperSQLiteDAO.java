package DataAccess.Helpers;

import DataAccess.Interfaces.IDAO;
import Infrastructure.AppConfig;
import Infrastructure.AppException;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataHelperSQLiteDAO<T> implements IDAO<T> {
    protected final Class<T> DTOClass;
    protected final String tableName;
    protected final String tablePK;
    private static Connection conn = null;

    public DataHelperSQLiteDAO(Class<T> dtoClass, String tableName, String tablePK) throws AppException {
        this.DTOClass = dtoClass;
        this.tableName = tableName;
        this.tablePK = tablePK;
        try { openConnection(); } catch (SQLException e) {
            throw new AppException("Fallo conexión DB: " + e.getMessage(), e, getClass(), "Constructor");
        }
    }

    protected static synchronized Connection openConnection() throws SQLException {
        if (conn == null || conn.isClosed()) {
            try { Class.forName("org.sqlite.JDBC"); } 
            catch (ClassNotFoundException e) { throw new SQLException("Falta Driver SQLite."); }
            
            String dbUrl = AppConfig.getDATABASE(); 
            if (dbUrl == null) throw new SQLException("URL nula en app.properties.");
            conn = DriverManager.getConnection(dbUrl);
        }
        return conn;
    }

    @Override
    public List<T> readAll() throws AppException {
        List<T> list = new ArrayList<>();
        String sql = "SELECT * FROM " + tableName + " WHERE Estado = 'A'";
        try (Statement stmt = openConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapResultSetToEntity(rs));
        } catch (SQLException e) {
            throw new AppException("Error leyendo tabla '" + tableName + "': " + e.getMessage(), e, getClass(), "readAll");
        }
        return list;
    }

    protected T mapResultSetToEntity(ResultSet rs) throws AppException {
        try {
            T instance = DTOClass.getDeclaredConstructor().newInstance();
            ResultSetMetaData meta = rs.getMetaData();
            for (int i = 1; i <= meta.getColumnCount(); i++) {
                String col = meta.getColumnLabel(i);
                Object val = rs.getObject(i);
                try {
                    Field field = DTOClass.getDeclaredField(col);
                    field.setAccessible(true);
                    if (val instanceof Number) {
                        if (field.getType() == Integer.class) val = ((Number) val).intValue();
                        if (field.getType() == Float.class) val = ((Number) val).floatValue();
                    }
                    field.set(instance, val);
                } catch (NoSuchFieldException e) { }
            }
            return instance;
        } catch (Exception e) {
            throw new AppException("Error mapeo", e, getClass(), "map");
        }
    }
    
    // Stubs
    public T readBy(Integer id) throws AppException { return null; }
    public boolean create(T entity) throws AppException { return false; }
    public boolean update(T entity) throws AppException { return false; }
    public boolean delete(Integer id) throws AppException { return false; }
    public Integer getCountReg() throws AppException { return 0; }
    public Integer getMinReg(String n) throws AppException { return 0; }
    public Integer getMaxReg(String n) throws AppException { return 0; }
}