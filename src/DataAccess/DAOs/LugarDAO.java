package DataAccess.DAOs;

import DataAccess.DTOs.LugarDTO;
import DataAccess.Helpers.DataHelperSQLiteDAO;
import Infrastructure.AppException;

/**
 * DAO para la tabla 'Lugar'.
 * Gestiona las ubicaciones físicas donde se instalan los sensores térmicos.
 */
public class LugarDAO extends DataHelperSQLiteDAO<LugarDTO> {

    /**
     * Inicializa el DAO vinculando el DTO con la tabla 'Lugar'.
     * @throws AppException si ocurre un error en el motor genérico de persistencia.
     */
    public LugarDAO() throws AppException {
        // Obligatorio: super() es la primera instrucción para evitar errores de compilación
        super(LugarDTO.class, "Lugar", "IdLugar");

    }
}