package Infrastructure;

import Infrastructure.Tools.CMDColor;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Gestiona las excepciones del sistema Pyraline, centralizando el log 
 * y la mensajería al usuario sin rastros de otros proyectos.
 */
public class AppException extends Exception {

    public AppException(String showMsg, Exception e, Class<?> clase, String metodo) {
        // Usa el mensaje por defecto de AppConfig si showMsg llega vacío
        super((showMsg == null || showMsg.isBlank()) ? AppConfig.MSG_DEFAULT_ERROR : showMsg);
        
        // Registra el error técnico en el archivo log de Pyraline
        saveLogFile(e != null ? e.getMessage() : "Sin detalle técnico", clase, metodo);
    }

    private void saveLogFile(String logMsg, Class<?> clase, String metodo) {
        // Obtiene la ruta desde app.properties (storage/Logs/PyralineSystem.log)
        String logPath = AppConfig.getLOGFILE();
        if (logPath == null || logPath.isEmpty()) 
            logPath = "storage/Logs/PyralineSystem.log"; 

        try {
            File file = new File(logPath);
            // Crea las carpetas si no existen (evita el error de 'FileNotFound')
            if (file.getParentFile() != null) 
                file.getParentFile().mkdirs();

            // Formato limpio: [FECHA] CLASE.METODO() ❱ MENSAJE
            String entry = String.format("[%s] %s.%s() ❱ %s", 
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), 
                    (clase != null ? clase.getSimpleName() : "Desconocido"), 
                    metodo, 
                    logMsg);

            // 'true' para adjuntar (append) y no borrar lo anterior
            try (PrintWriter writer = new PrintWriter(new FileWriter(logPath, true))) {
                // Muestra en consola roja solo durante desarrollo
                System.err.println(CMDColor.RED + " [PYRALINE-LOG] " + entry + CMDColor.RESET);
                writer.println(entry);
            }
        } catch (Exception ex) {
            // Si falla el log, al menos imprimimos en consola el error original
            System.err.println("No se pudo escribir en el log: " + ex.getMessage());
        }
    }
}