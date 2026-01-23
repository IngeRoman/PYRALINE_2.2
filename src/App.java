import DataAccess.DAOs.LugarDAO;
import DataAccess.DAOs.PYRALINEDAO;
import DataAccess.DAOs.TipoAlertaDAO;
import DataAccess.DTOs.PYRALINEDTO;
import Infrastructure.AppConfig;
import Infrastructure.ArduinoSensor;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;

public class App {
    // Colores ANSI para el diseño profesional en consola
    private static final String RESET = "\033[0m";
    private static final String HEADER = "\033[1;36m"; // Cyan Bold
    private static final String SUCCESS = "\033[1;32m"; // Green Bold
    private static final String ERROR = "\033[1;31m";   // Red Bold
    private static final String BORDER = "\033[0;34m";  // Blue

    public static void main(String[] args) {
        try {
            // 0. INICIAR HARDWARE (Sensor de Temperatura)
            ArduinoSensor sensor = new ArduinoSensor();
            sensor.conectar("COM3"); // Asegúrate de que coincida con tu puerto real

            // Limpiar consola para una mejor visualización
            System.out.print("\033[H\033[2J");
            System.out.flush();
            
            System.out.println(HEADER + "=========================================");
            System.out.println("   SISTEMA PYRALINE: MONITOREO TÉRMICO   ");
            System.out.println("=========================================" + RESET);

            // 1. Asegurar que la DB existe e instalar tablas/vistas/triggers
            instalarBaseDeDatos();

            // 2. Mostrar Dashboard inicial con registros de la DB
            mostrarDashboardEstatico();

            System.out.println("\n" + SUCCESS + "(✓) Sistema de base de datos Pyraline cargado." + RESET);

            // --- BUCLE DE MONITOREO EN VIVO ---
            System.out.println(HEADER + "\n>>> MONITOREO ACTIVO (Registro automático en alarma)" + RESET);
            System.out.println("Presione Ctrl+C para detener el sistema...\n");

            double UMBRAL_ALARMA = 40.0; 

            while (true) {
                String tempStr = ArduinoSensor.getUltimaLectura();
                double tempActual = Double.parseDouble(tempStr);

                // VISUALIZACIÓN: Actualización en la misma línea
                System.out.print("\r🌡️  Temperatura actual: " + (tempActual > UMBRAL_ALARMA ? ERROR : SUCCESS) 
                                 + String.format("%.2f", tempActual) + " °C" + RESET + "    ");

                // PERSISTENCIA: Registro automático si supera el umbral
                if (tempActual > UMBRAL_ALARMA) {
                    System.out.println("\n" + ERROR + " [!] ALERTA: Temperatura Crítica Detectada." + RESET);
                    
                    try {
                        PYRALINEDAO dao = new PYRALINEDAO();
                        // Lugar 1: Laboratorio Central, Alerta 1: Exceso de Temperatura (según DDL_DML.sql)
                        PYRALINEDTO alarma = new PYRALINEDTO(1, 1, (float)tempActual);
                        
                        if(dao.create(alarma)) {
                            System.out.println(SUCCESS + " (✓) Alarma guardada en historial." + RESET);
                        }
                    } catch (Exception e) {
                        System.err.println("\n(!) Error al persistir alarma: " + e.getMessage());
                    }
                    
                    // Pausa de 5 segundos para evitar registros masivos por un mismo evento de calor
                    Thread.sleep(5000); 
                }

                Thread.sleep(1000); 
            }

        } catch (Exception e) {
            System.err.println(ERROR + "(!) Error Crítico en el sistema: " + e.getMessage() + RESET);
        }
    }

    private static void mostrarDashboardEstatico() {
        try {
            System.out.println(HEADER + "\n>>> ZONAS DE COBERTURA" + RESET);
            printList("| ID | NOMBRE          | ESTADO |", new LugarDAO().readAll()); 

            System.out.println(HEADER + "\n>>> PROTOCOLOS DE ALERTA" + RESET);
            printList("| ID | NOMBRE          | DESCRIPCION                    |", new TipoAlertaDAO().readAll()); 

            System.out.println(HEADER + "\n>>> HISTORIAL RECIENTE (PYRALINE)" + RESET);
            printList("| ID | TEMP. REGISTRADA | LUGAR ID | ALERTA ID | FECHA HORA         |", new PYRALINEDAO().readAll());
        } catch (Exception e) { 
            System.err.println(ERROR + "(!) Error al cargar Dashboard: " + e.getMessage() + RESET); 
        }
    }

    private static void printList(String header, List<?> lista) {
        String separator = BORDER + "-".repeat(header.length()) + RESET;
        System.out.println(separator);
        System.out.println(HEADER + header + RESET);
        System.out.println(separator);
        
        if (lista == null || lista.isEmpty()) {
            System.out.println("  (Sin registros activos)  ");
        } else {
            for (Object o : lista) {
                System.out.println(o.toString());
            }
        }
        System.out.println(separator);
    }

    private static void instalarBaseDeDatos() {
        String dbPath = AppConfig.getDATABASE().replace("jdbc:sqlite:", "");
        File dbFile = new File(dbPath);
        
        if (dbFile.getParentFile() != null && !dbFile.getParentFile().exists()) {
            dbFile.getParentFile().mkdirs();
        }
        
        try {
            if (!dbFile.exists() || dbFile.length() < 100) {
                System.out.println("(!) Configurando nueva base de datos PYRALINE...");
                String sqlPath = "storage/scripts/DDL_DML.sql";
                String sqlContent = new String(Files.readAllBytes(Paths.get(sqlPath)));
                String url = AppConfig.getDATABASE();
                
                try (Connection conn = DriverManager.getConnection(url);
                     Statement stmt = conn.createStatement()) {
                    
                    String[] queries = sqlContent.split(";");
                    for (String query : queries) {
                        String trimmedQuery = query.trim();
                        if (!trimmedQuery.isEmpty() && !trimmedQuery.startsWith("--")) {
                            stmt.execute(trimmedQuery);
                        }
                    }
                    System.out.println(SUCCESS + "(✓) Estructura de tablas instalada correctamente." + RESET);
                }
            }
        } catch (Exception e) {
            System.err.println(ERROR + "(!) Fallo en Instalación de DB: " + e.getMessage() + RESET);
        }
    }
}