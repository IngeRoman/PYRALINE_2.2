package BusinessLogic.logic;

import DataAccess.dao.UsuarioDAO;
import Infrastructure.exeption.AppException;

/**
 * Lógica de negocio para la gestión de usuarios y autenticación.
 * Actúa como filtro de validación antes de acceder a la base de datos.
 */
public class UsuarioBL {
    private UsuarioDAO usuarioDAO;

    /**
     * Inicializa el acceso a datos para usuarios.
     * @throws AppException si ocurre un error al instanciar el DAO.
     */
    public UsuarioBL() throws AppException {
        try {
            this.usuarioDAO = new UsuarioDAO();
        } catch (Exception e) {
            // Captura errores de inicialización (ej. driver no cargado)
            throw new AppException("No se pudo inicializar el servicio de usuarios.", e, getClass(), "Constructor");
        }
    }

    /**
     * Valida las credenciales de acceso al sistema Pyraline.
     * @param email Correo del usuario.
     * @param password Contraseña de acceso.
     * @return true si las credenciales son válidas.
     * @throws AppException si hay un fallo técnico en la base de datos.
     */
    public boolean validarAcceso(String email, String password) throws AppException {
        try {
            // Regla de negocio: evitar peticiones innecesarias a la DB si faltan datos
            if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
                return false;
            }
            
            // Intenta realizar el login mediante el componente de datos
            return usuarioDAO.login(email, password);
            
        } catch (Exception e) {
            // Captura errores de conexión o fallos en la consulta SQL de login
            throw new AppException("Fallo técnico durante la validación de credenciales.", e, getClass(), "validarAcceso");
        }
    }
}