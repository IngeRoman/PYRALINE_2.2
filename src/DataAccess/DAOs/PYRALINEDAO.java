package DataAccess.DAOs;

import DataAccess.DTOs.PYRALINEDTO;
import DataAccess.Helpers.DataHelperSQLiteDAO;
import Infrastructure.AppException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PYRALINEDAO extends DataHelperSQLiteDAO<PYRALINEDTO> {
    
    public PYRALINEDAO() throws AppException {
        super(PYRALINEDTO.class, "wsPRYLINE", "IdPYRALINE");
    }

    /**
     * Elimina todos los registros de la tabla física PYRALINE.
     * Es vital para el botón de mantenimiento del Dashboard.
     */
    public boolean deleteAll() throws AppException {
        // Ejecutamos el comando sobre la TABLA física, no sobre la vista.
        String query = "DELETE FROM PYRALINE"; 
        try (Connection conn = openConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.executeUpdate(query);
            System.out.println(">>> [DAO] Historial vaciado correctamente por Mateo.");
            return true;
        } catch (SQLException e) {
            throw new AppException("Error al vaciar el historial de PYRALINE", e, getClass(), "deleteAll()");
        }
    }

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
            throw new AppException("Error al registrar alarma en PYRALINE", e, getClass(), "create()");
        }
    }

    @Override
    public List<PYRALINEDTO> readAll() throws AppException {
        List<PYRALINEDTO> lista = new ArrayList<>();
        // Consultamos la vista wsPRYLINE para obtener los datos formateados y ordenados.
        String query = "SELECT IdPYRALINE, IdTipoAlerta, Temperatura, Estado, FechaHora, FechaModifica FROM wsPRYLINE ORDER BY FechaHora DESC";
        
        try (Connection conn = openConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                PYRALINEDTO dto = new PYRALINEDTO();
                
                dto.setIdPYRALINE(rs.getInt("IdPYRALINE"));
                dto.setIdTipoAlerta(rs.getInt("IdTipoAlerta")); // Vital para los colores del Dashboard.
                dto.setTemperatura(rs.getFloat("Temperatura"));
                dto.setEstado(rs.getString("Estado"));
                dto.setFechaHora(rs.getString("FechaHora"));
                dto.setFechaModifica(rs.getString("FechaModifica"));
                
                lista.add(dto);
            }
        } catch (SQLException e) {
            throw new AppException("Error al leer la vista wsPRYLINE.", e, getClass(), "readAll()");
        }
        return lista;
    }
}