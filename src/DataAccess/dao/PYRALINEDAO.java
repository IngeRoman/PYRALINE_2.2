package DataAccess.dao;

import DataAccess.dto.PYRALINEDTO;
import DataAccess.helpers.DataHelperSQLiteDAO;
import Infrastructure.exeption.AppException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO especializado en la gestión de registros del sistema Pyraline.
 * Maneja la persistencia en la tabla física y la lectura desde la vista wsPRYLINE.
 */
public class PYRALINEDAO extends DataHelperSQLiteDAO<PYRALINEDTO> {
    
    /**
     * Inicializa el DAO vinculando la vista y la clave primaria principal.
     * @throws AppException si el motor genérico falla al conectar.
     */
    public PYRALINEDAO() throws AppException {
        // Vinculación con la vista para lecturas genéricas
        super(PYRALINEDTO.class, "wsPRYLINE", "IdPYRALINE");
    }

    /**
     * Vacía completamente el historial de alertas en la tabla física.
     * @return true si el truncado de la tabla fue exitoso.
     * @throws AppException si hay errores de acceso o la tabla está bloqueada.
     */
    public boolean deleteAll() throws AppException {
        String query = "DELETE FROM PYRALINE"; 
        try (Connection conn = openConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.executeUpdate(query);
            System.out.println(">>> [DAO] Historial vaciado correctamente.");
            return true;
        } catch (SQLException e) {
            // Captura errores de sintaxis SQL o fallos de conexión durante el borrado
            throw new AppException("Fallo técnico al vaciar el historial físico.", e, getClass(), "deleteAll()");
        }
    }

    /**
     * Registra una nueva alerta térmica capturada por el sensor.
     * @param entity Objeto con datos de lugar, tipo de alerta y temperatura.
     * @return true si el insert fue confirmado.
     * @throws AppException si los datos son inválidos o falla la restricción de integridad.
     */
    @Override
    public boolean create(PYRALINEDTO entity) throws AppException {
        String query = "INSERT INTO PYRALINE (IdLugar, IdTipoAlerta, Temperatura) VALUES (?, ?, ?)";
        try (Connection conn = openConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, entity.getIdLugar());
            pstmt.setInt(2, entity.getIdTipoAlerta());
            pstmt.setFloat(3, entity.getTemperatura());
            
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            // Captura errores de inserción (ej. columnas inexistentes o tipos incompatibles)
            throw new AppException("Error al registrar el evento térmico en la base de datos.", e, getClass(), "create()");
        }
    }

    /**
     * Obtiene el historial completo desde la vista wsPRYLINE.
     * @return Lista de registros ordenados de forma descendente por fecha.
     * @throws AppException si ocurre un fallo en la ejecución del SELECT.
     */
    @Override
    public List<PYRALINEDTO> readAll() throws AppException {
        List<PYRALINEDTO> lista = new ArrayList<>();
        String query = "SELECT IdPYRALINE, IdTipoAlerta, Temperatura, Estado, FechaHora, FechaModifica FROM wsPRYLINE ORDER BY FechaHora DESC";
        
        try (Connection conn = openConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                PYRALINEDTO dto = new PYRALINEDTO();
                
                dto.setIdPYRALINE(rs.getInt("IdPYRALINE"));
                dto.setIdTipoAlerta(rs.getInt("IdTipoAlerta")); 
                dto.setTemperatura(rs.getFloat("Temperatura"));
                dto.setEstado(rs.getString("Estado"));
                dto.setFechaHora(rs.getString("FechaHora"));
                dto.setFechaModifica(rs.getString("FechaModifica"));
                
                lista.add(dto);
            }
        } catch (SQLException e) {
            // Captura fallos de lectura, especialmente si la vista wsPRYLINE está corrupta o falta
            throw new AppException("No se pudo leer la vista histórica wsPRYLINE.", e, getClass(), "readAll()");
        }
        return lista;
    }
}