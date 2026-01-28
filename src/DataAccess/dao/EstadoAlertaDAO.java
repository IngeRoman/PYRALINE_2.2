package DataAccess.dao;

import DataAccess.dto.EstadoAlertaDTO;
import DataAccess.helpers.DataHelperSQLiteDAO;
import Infrastructure.exeption.AppException;

/**
 * DAO para la gestión de estados de alerta.
 * Hereda la lógica de persistencia genérica de Pyraline.
 */
public class EstadoAlertaDAO extends DataHelperSQLiteDAO<EstadoAlertaDTO> {

    /**
     * Inicializa el DAO vinculando el DTO con su tabla física.
     * @throws AppException si el motor genérico falla al conectar o mapear.
     */
    public EstadoAlertaDAO() throws AppException {
        // REGLA DE JAVA: super debe ser siempre la primera línea.
        // El manejo de errores ya ocurre dentro del constructor de DataHelper.
        super(EstadoAlertaDTO.class, "EstadoAlerta", "IdEstadoAlerta");
        
        
    }
}