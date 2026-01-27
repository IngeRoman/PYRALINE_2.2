import Infrastructure.AppConfig;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.*;

public class TestDatabase {
    public static void main(String[] args) {
        String url = AppConfig.getDATABASE();
        String sqlPath = "storage/scripts/DDL_DML.sql";
        
        System.out.println(">>> REPARANDO ESTRUCTURA DE BASE DE DATOS - PYRALINE");

        // 1. LIMPIEZA FÍSICA
        try {
            String dbRawPath = url.replace("jdbc:sqlite:", "");
            File dbFile = new File(dbRawPath);
            if (dbFile.exists() && dbFile.delete()) {
                System.out.println("(✓) Archivo .sqlite eliminado para reconstrucción limpia.");
            }
        } catch (Exception e) { }

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            
            System.out.println("--- Ejecutando Script de RM ---");
            
            // LECTOR LÍNEA POR LÍNEA: Más seguro para Triggers
            try (BufferedReader reader = new BufferedReader(new FileReader(sqlPath))) {
                StringBuilder buffer = new StringBuilder();
                String line;
                boolean enTrigger = false;

                while ((line = reader.readLine()) != null) {
                    String trimmedLine = line.trim();
                    
                    // Saltamos líneas vacías o comentarios simples
                    if (trimmedLine.isEmpty() || trimmedLine.startsWith("--")) continue;

                    buffer.append(line).append("\n");

                    // Detectamos si entramos en un bloque de TRIGGER
                    if (trimmedLine.toUpperCase().startsWith("CREATE TRIGGER")) {
                        enTrigger = true;
                    }

                    // Si termina en ';' y no estamos dentro de un trigger, o si es el 'END;' de un trigger
                    if ((trimmedLine.endsWith(";") && !enTrigger) || (enTrigger && trimmedLine.toUpperCase().startsWith("END;"))) {
                        String sql = buffer.toString().trim();
                        try {
                            stmt.execute(sql);
                            // Resumen de ejecución para Mateo
                            String preview = (sql.length() > 50) ? sql.substring(0, 50).replace("\n", " ") + "..." : sql;
                            System.out.println("  [OK]: " + preview);
                        } catch (SQLException ex) {
                            System.err.println("  [ERROR] en comando: " + sql);
                            System.err.println("  Detalle: " + ex.getMessage());
                        }
                        buffer.setLength(0); // Limpiamos el buffer para el siguiente comando
                        enTrigger = false;
                    }
                }
            }
            
            validarEstructuraFinal(stmt);

        } catch (Exception e) {
            System.err.println("\n(!) ERROR CRÍTICO DE SISTEMA:");
            e.printStackTrace();
        }
    }

    private static void validarEstructuraFinal(Statement stmt) throws SQLException {
        System.out.println("\n--- VERIFICACIÓN DE COMPATIBILIDAD ---");
        
        // Verificamos el usuario de acceso para el Login
        try (ResultSet rs = stmt.executeQuery("SELECT Email, Password FROM Usuario LIMIT 1")) {
            if(rs.next()) {
                System.out.println("(✓) Acceso EPN: OK");
                System.out.println("    User: " + rs.getString("Email") + " | Pass: " + rs.getString("Password"));
            }
        }
        
        System.out.println("\n>>> TEST FINALIZADO: Todo listo para el Login.");
    }
}