package DataAccess.dao;

import DataAccess.dto.UsuarioDTO;
import DataAccess.helpers.DataHelperSQLiteDAO;
import Infrastructure.exeption.AppException;

import java.sql.*;

/**
 * DAO para la gestión de usuarios y autenticación.
 * Maneja las credenciales de acceso al sistema Pyraline.
 */
public class UsuarioDAO extends DataHelperSQLiteDAO<UsuarioDTO> {

    /**
     * Vincula el DTO con la tabla 'Usuario'.
     * @throws AppException si falla la conexión inicial.
     */
    public UsuarioDAO() throws AppException {
        // Obligatorio: super() debe ser la primera línea.
        super(UsuarioDTO.class, "Usuario", "IdUsuario");
    }

    /**
     * Valida credenciales de acceso contra la base de datos.
     * @param email Correo electrónico del usuario.
     * @param password Contraseña de acceso.
     * @return true si el usuario existe y está activo.
     * @throws AppException si ocurre un error en la consulta SQL.
     */
    public boolean login(String email, String password) throws AppException {
        String sql = "SELECT COUNT(*) FROM " + tableName + " WHERE Estado = 'A' AND Email = ? AND Password = ?";
        
        // try-with-resources: asegura el cierre automático de la conexión y resultados
        try (PreparedStatement pstmt = openConnection().prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                // Captura el conteo: si es mayor a 0, las credenciales son correctas
                return rs.next() && rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            // Captura errores de sintaxis, conexión perdida o tabla inexistente
            throw new AppException("Fallo técnico durante la validación de acceso.", e, getClass(), "login");
        }
    }
}