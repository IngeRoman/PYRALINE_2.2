package DataAccess.DAOs;

import DataAccess.DTOs.UsuarioDTO;
import DataAccess.Helpers.DataHelperSQLiteDAO;
import Infrastructure.AppException;
import java.sql.*;

public class UsuarioDAO extends DataHelperSQLiteDAO<UsuarioDTO> {
    public UsuarioDAO() throws AppException {
        super(UsuarioDTO.class, "Usuario", "IdUsuario");
    }

    public boolean login(String email, String password) throws AppException {
        String sql = "SELECT COUNT(*) FROM " + tableName + " WHERE Estado = 'A' AND Email = ? AND Password = ?";
        try (PreparedStatement pstmt = openConnection().prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            throw new AppException("Error en login DAO", e, getClass(), "login");
        }
    }
}