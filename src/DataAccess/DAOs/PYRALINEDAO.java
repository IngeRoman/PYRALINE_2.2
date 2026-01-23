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
        // Apuntamos a la VISTA 'wsPRYLINE' para las lecturas por defecto
        super(PYRALINEDTO.class, "wsPRYLINE", "IdPYRALINE");
    }

    /**
     * Guarda un registro de alarma en la base de datos.
     * Se debe invocar desde el controlador cuando la temperatura del Arduino 
     * supere el umbral establecido.
     */
    @Override
    public boolean create(PYRALINEDTO entity) throws AppException {
        String query = "INSERT INTO PYRALINE (IdLugar, IdTipoAlerta, Temperatura) VALUES (?, ?, ?)";
        // Usamos try-with-resources para cerrar la conexión automáticamente
        try (Connection conn = openConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, entity.getIdLugar());
            pstmt.setInt(2, entity.getIdTipoAlerta());
            pstmt.setFloat(3, entity.getTemperatura());
            
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            throw new AppException("Error al registrar alarma en la tabla PYRALINE", e, getClass(), "create()");
        }
    }

    /**
     * Lee el historial completo de alarmas desde la vista wsPRYLINE.
     * Esta vista ya trae los nombres de los lugares y tipos de alerta unidos (JOIN).
     */
    @Override
    public List<PYRALINEDTO> readAll() throws AppException {
        List<PYRALINEDTO> lista = new ArrayList<>();
        // Seleccionamos las columnas tal cual están definidas en la VIEW del script SQL
        String query = "SELECT IdPYRALINE, Lugar, TipoAlerta, Temperatura, Estado, FechaHora, FechaModifica FROM wsPRYLINE";
        
        try (Connection conn = openConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                PYRALINEDTO dto = new PYRALINEDTO();
                
                dto.setIdPYRALINE(rs.getInt("IdPYRALINE"));
                dto.setTemperatura(rs.getFloat("Temperatura"));
                dto.setEstado(rs.getString("Estado"));
                dto.setFechaHora(rs.getString("FechaHora"));
                dto.setFechaModifica(rs.getString("FechaModifica"));
                
                // NOTA: Si tu DTO tiene campos String para los nombres, asígnalos aquí.
                // Si solo tiene campos int para IDs, estos campos de la vista son informativos.
                // Ejemplo si usas campos genéricos o el DTO soporta los nombres:
                // dto.setNombreLugar(rs.getString("Lugar")); 
                // dto.setNombreAlerta(rs.getString("TipoAlerta"));
                
                lista.add(dto);
            }
        } catch (SQLException e) {
            throw new AppException("Error al leer la vista wsPRYLINE", e, getClass(), "readAll()");
        }
        return lista;
    }
}