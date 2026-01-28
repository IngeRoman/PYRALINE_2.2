package DataAccess.dao;

import DataAccess.dto.TipoAlertaDTO;
import DataAccess.helpers.DataHelperSQLiteDAO;
import Infrastructure.exeption.AppException;

/**
 * DAO especializado en la tabla 'TipoAlerta'.
 * Define las categorías de incidentes térmicos (Fuego, Advertencia, Normal).
 */
public class TipoAlertaDAO extends DataHelperSQLiteDAO<TipoAlertaDTO> {

    /**
     * Inicializa el acceso a datos para tipos de alerta.
     * @throws AppException si el motor genérico falla al mapear la tabla o conectar.
     */
    public TipoAlertaDAO() throws AppException {
        // Regla de Java: super() debe ser la primera línea.
        // La detección de errores (tabla no encontrada, PK inválida) ocurre en el padre.
        super(TipoAlertaDTO.class, "TipoAlerta", "IdTipoAlerta");

    }
}