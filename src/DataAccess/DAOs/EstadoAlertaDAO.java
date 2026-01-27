package DataAccess.DAOs;

import DataAccess.DTOs.EstadoAlertaDTO;
import DataAccess.Helpers.DataHelperSQLiteDAO;
import Infrastructure.AppException;

public class EstadoAlertaDAO extends DataHelperSQLiteDAO<EstadoAlertaDTO> {

    public EstadoAlertaDAO() throws AppException {
        super(EstadoAlertaDTO.class, "EstadoAlerta", "IdEstadoAlerta");
    }
}
