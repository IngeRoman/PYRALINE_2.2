package DataAccess.DAOs;

import DataAccess.DTOs.LugarDTO;
import DataAccess.Helpers.DataHelperSQLiteDAO;
import Infrastructure.AppException;

public class LugarDAO extends DataHelperSQLiteDAO<LugarDTO> {
    public LugarDAO() throws AppException {
        super(LugarDTO.class, "Lugar", "IdLugar");
    }
}