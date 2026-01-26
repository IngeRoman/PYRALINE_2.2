package BusinessLogic;

import DataAccess.DAOs.UsuarioDAO;
import Infrastructure.AppException;

public class UsuarioBL {
    private UsuarioDAO usuarioDAO;

    public UsuarioBL() throws AppException {
        this.usuarioDAO = new UsuarioDAO();
    }

    public boolean validarAcceso(String email, String password) throws AppException {
        // Regla de negocio: evitar campos vacíos antes de ir a la base de datos
        if (email == null || email.isEmpty() || password == null || password.isEmpty())
            return false;
            
        return usuarioDAO.login(email, password);
    }
}