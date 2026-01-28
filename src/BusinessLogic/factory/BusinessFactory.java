package BusinessLogic.factory;

import java.util.List;

import DataAccess.interfaces.IDAO;
import Infrastructure.exeption.AppException;

/**
 * Fábrica genérica para la gestión de DAOs.
 * Centraliza las operaciones CRUD del sistema Pyraline.
 */
public class BusinessFactory<T> {
    private final IDAO<T> oDAO;

    /**
     * Instancia el DAO dinámicamente mediante reflexión.
     * @param classDAO Clase que implementa la interfaz IDAO.
     */
    public BusinessFactory(Class<? extends IDAO<T>> classDAO) {
        try {
            // Intenta crear una instancia física del DAO solicitado
            this.oDAO = classDAO.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            // Captura fallos de instanciación (ej. constructor privado o clase no encontrada)
            AppException er = new AppException("Error al inicializar el componente de datos.", e, getClass(), "Constructor");
            throw new RuntimeException(er);
        }
    }

    /** @return Lista de registros activos. */
    public List<T> getAll() throws AppException {
        try {
            return oDAO.readAll();
        } catch (Exception e) {
            // Captura errores de consulta masiva o conexión perdida
            throw new AppException("Fallo al recuperar lista de registros.", e, getClass(), "getAll");
        }
    }

    /** @return Entidad por ID. */
    public T getBy(Integer id) throws AppException {
        try {
            return oDAO.readBy(id);
        } catch (Exception e) {
            // Captura errores si el ID es nulo o la consulta falla
            throw new AppException("Error al buscar registro por ID: " + id, e, getClass(), "getBy");
        }
    }

    /** @return true si se insertó correctamente. */
    public boolean add(T oT) throws AppException {
        try {
            return oDAO.create(oT);
        } catch (Exception e) {
            // Captura errores de restricción de integridad o sintaxis en el INSERT
            throw new AppException("No se pudo agregar el nuevo registro.", e, getClass(), "add");
        }
    }

    /** @return true si se actualizó correctamente. */
    public boolean upd(T oT) throws AppException {
        try {
            return oDAO.update(oT);
        } catch (Exception e) {
            // Captura errores durante la ejecución del UPDATE en la base de datos
            throw new AppException("Fallo en la actualización de datos.", e, getClass(), "upd");
        }
    }

    /** @return true si se eliminó correctamente. */
    public boolean del(Integer id) throws AppException {
        try {
            return oDAO.delete(id);
        } catch (Exception e) {
            // Captura errores de eliminación física o lógica (Estado)
            throw new AppException("Error al intentar eliminar el registro.", e, getClass(), "del");
        }
    }

    /** @return Máximo valor de una columna. */
    public Integer getMaxReg(String cellName) throws AppException {
        try {
            return oDAO.getMaxReg(cellName);
        } catch (Exception e) {
            // Captura si la columna no existe o la tabla está vacía
            throw new AppException("Error al obtener máximo de: " + cellName, e, getClass(), "getMaxReg");
        }
    }

    /** @return Mínimo valor de una columna. */
    public Integer getMinReg(String cellName) throws AppException {
        try {
            return oDAO.getMinReg(cellName);
        } catch (Exception e) {
            // Captura fallos en la consulta de mínimos
            throw new AppException("Error al obtener mínimo de: " + cellName, e, getClass(), "getMinReg");
        }
    }

    /** @return Total de registros activos. */
    public Integer getCountReg() throws AppException {
        try {
            return oDAO.getCountReg();
        } catch (Exception e) {
            // Captura fallos en la ejecución del COUNT(*)
            throw new AppException("Fallo al contabilizar registros.", e, getClass(), "getCountReg");
        }
    }
}