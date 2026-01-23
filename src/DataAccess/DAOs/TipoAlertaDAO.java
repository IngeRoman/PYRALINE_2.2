package DataAccess.DAOs;

import DataAccess.DTOs.TipoAlertaDTO;
import DataAccess.Helpers.DataHelperSQLiteDAO;
import Infrastructure.AppException;

public class TipoAlertaDAO extends DataHelperSQLiteDAO<TipoAlertaDTO> {

    public TipoAlertaDAO() throws AppException {
        super(TipoAlertaDTO.class, "TipoAlerta", "IdTipoAlerta");
    }
}