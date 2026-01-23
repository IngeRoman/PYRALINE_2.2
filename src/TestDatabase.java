import Infrastructure.AppConfig;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;

public class TestDatabase {
    public static void main(String[] args) {
        String url = AppConfig.getDATABASE();
        String sqlPath = "storage/scripts/DDL_DML.sql";
        
        System.out.println(">>> INICIANDO TEST DE BASE DE DATOS ");

        // 1. LIMPIEZA TOTAL (Borrado físico)
        try {
            String dbRawPath = url.replace("jdbc:sqlite:", "");
            File dbFile = new File(dbRawPath);
            if (dbFile.exists() && dbFile.delete()) {
                System.out.println("(✓) Base de datos anterior eliminada.");
            }
        } catch (Exception e) { /* Ignorar errores de borrado */ }

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            
            String sqlContent = new String(Files.readAllBytes(Paths.get(sqlPath)));
            
            // Limpieza básica de saltos de linea
            String cleanSql = sqlContent.replaceAll("\\r\\n|\\r|\\n", " ");
            String[] queries = cleanSql.split(";");
            
            String bufferTrigger = ""; // Variable para guardar pedazos de trigger

            System.out.println("--- Ejecutando Comandos ---");
            
            for (int i = 0; i < queries.length; i++) {
                String q = queries[i].trim();
                if (q.isEmpty() || q.startsWith("--")) continue;

                // LÓGICA INTELIGENTE:
                // Si encontramos el inicio de un Trigger, NO lo ejecutamos, lo guardamos.
                if (q.startsWith("CREATE TRIGGER")) {
                    System.out.println("Detectado inicio de Trigger... uniendo piezas.");
                    bufferTrigger = q + "; "; // Guardamos la primera parte y devolvemos el punto y coma
                    continue; // Saltamos a la siguiente vuelta del bucle
                }

                // Si tenemos algo en el buffer (la primera parte del trigger) y encontramos el END
                if (!bufferTrigger.isEmpty() && q.equalsIgnoreCase("END")) {
                    bufferTrigger += q; // Pegamos el END
                    System.out.println("Ejecutando TRIGGER COMPLETO.");
                    stmt.execute(bufferTrigger);
                    bufferTrigger = ""; // Limpiamos el buffer
                    continue;
                }

                // Si es un comando normal (CREATE TABLE, INSERT, etc.)
                if (bufferTrigger.isEmpty()) {
                    String preview = (q.length() > 40 ? q.substring(0, 40) + "..." : q);
                    System.out.println("Ejecutando [" + i + "]: " + preview);
                    stmt.execute(q);
                }
            }
            
            System.out.println("\n ¡ÉXITO TOTAL! Estructura completa creada.");

            // 2. VERIFICACIÓN
            System.out.println("--- Verificando Tabla Lugar ---");
            try (ResultSet rs = stmt.executeQuery("SELECT Nombre FROM Lugar")) {
                while (rs.next()) {
                    System.out.println("   -> Dato: " + rs.getString("Nombre"));
                }
            }

        } catch (Exception e) {
            System.err.println("\n ERROR CRÍTICO:");
            System.err.println("Mensaje: " + e.getMessage());
            e.printStackTrace();
        }
    }
}