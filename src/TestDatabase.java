import Infrastructure.AppConfig;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;

public class TestDatabase {
    public static void main(String[] args) {
        String url = AppConfig.getDATABASE();
        String sqlPath = "storage/scripts/DDL_DML.sql";
        
        System.out.println(">>> INICIANDO TEST DE BASE DE DATOS PARA PYRALINE");

        // 1. LIMPIEZA TOTAL (Borrado físico del archivo .sqlite)
        try {
            String dbRawPath = url.replace("jdbc:sqlite:", "");
            File dbFile = new File(dbRawPath);
            if (dbFile.exists() && dbFile.delete()) {
                System.out.println("(✓) Base de datos anterior eliminada correctamente.");
            }
        } catch (Exception e) { /* Ignorar si no se puede borrar */ }

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            
            // Leemos el script original
            String sqlContent = new String(Files.readAllBytes(Paths.get(sqlPath)));
            
            // CAMBIO CLAVE: Usamos sqlContent directamente para no romper los comentarios
            String[] queries = sqlContent.split(";");
            
            String bufferTrigger = ""; 

            System.out.println("--- Ejecutando Comandos del Script ---");
            
            for (int i = 0; i < queries.length; i++) {
                String q = queries[i].trim();
                if (q.isEmpty() || q.startsWith("--")) continue;

                // LÓGICA DE TRIGGER: Unimos las piezas hasta encontrar el END
                if (q.startsWith("CREATE TRIGGER")) {
                    System.out.println("   -> Detectado inicio de Trigger...");
                    bufferTrigger = q + "; "; 
                    continue; 
                }

                if (!bufferTrigger.isEmpty() && q.equalsIgnoreCase("END")) {
                    bufferTrigger += q; 
                    System.out.println("   -> Ejecutando TRIGGER COMPLETO.");
                    stmt.execute(bufferTrigger);
                    bufferTrigger = ""; 
                    continue;
                }

                // Ejecución de comandos normales (Tablas e Inserts)
                if (bufferTrigger.isEmpty()) {
                    String preview = (q.length() > 45 ? q.substring(0, 45).replace("\n", " ") + "..." : q.replace("\n", " "));
                    System.out.println("   [" + i + "]: " + preview);
                    stmt.execute(q);
                }
            }
            
            System.out.println("\n ¡ÉXITO TOTAL! Estructura creada.");

            // 2. VERIFICACIÓN DE DATOS
            System.out.println("\n--- VERIFICACIÓN FINAL ---");
            
            // Verificar Lugares
            System.out.println(">> Tabla Lugar:");
            try (ResultSet rs = stmt.executeQuery("SELECT Nombre FROM Lugar")) {
                while (rs.next()) System.out.println("   - " + rs.getString("Nombre"));
            }

            // Verificar Usuario (Crucial para el Login)
            System.out.println("\n>> Tabla Usuario (Acceso EPN):");
            String sqlUser = "SELECT Email, Password, Estado FROM Usuario";
            try (ResultSet rs = stmt.executeQuery(sqlUser)) {
                if (!rs.isBeforeFirst()) System.err.println("   (!) ERROR: No hay usuarios en la tabla.");
                while (rs.next()) {
                    System.out.println("   - Email:    " + rs.getString("Email"));
                    System.out.println("   - Password: " + rs.getString("Password"));
                    System.out.println("   - Estado:   " + rs.getString("Estado"));
                }
            }

            System.out.println("\n>>> TEST FINALIZADO: Todo listo para el Login.");

        } catch (Exception e) {
            System.err.println("\n ERROR CRÍTICO:");
            System.err.println("Mensaje: " + e.getMessage());
            e.printStackTrace();
        }
    }
}