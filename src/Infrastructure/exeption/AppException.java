package Infrastructure.exeption;

import Infrastructure.config.AppConfig;
import Infrastructure.console.CMDColor;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * <h1>AppException</h1>
 * Centraliza el manejo de incidentes en Pyraline.
 * Permite registrar logs técnicos y mostrar mensajes amigables al usuario.
 * * @author Mateo Sebastian Ríos Taco
 * @version 2.1
 */
public class AppException extends Exception {

    /**
     * Constructor para capturar excepciones con trazabilidad completa.
     * @param showMsg   Mensaje amigable para el usuario.
     * @param e         Excepción raíz capturada.
     * @param clase     Clase donde se originó el error.
     * @param metodo    Método donde se originó el error.
     */
    public AppException(String showMsg, Exception e, Class<?> clase, String metodo) {
        super((showMsg == null || showMsg.isBlank()) ? AppConfig.MSG_DEFAULT_ERROR : showMsg);
        saveLogFile(e != null ? e.getMessage() : "Sin detalle técnico", clase, metodo);
    }

    /**
     * Constructor para errores de lógica (sin excepción previa).
     */
    public AppException(String showMsg, Class<?> clase, String metodo) {
        this(showMsg, null, clase, metodo);
    }

    /**
     * Persiste el error en el log físico de Pyraline de forma segura.
     */
    private void saveLogFile(String logMsg, Class<?> clase, String metodo) {
        String logPath = AppConfig.getLOGFILE();
        if (logPath == null || logPath.isEmpty()) 
            logPath = "storage/Logs/PyralineSystem.log"; 

        try {
            File file = new File(logPath);
            if (file.getParentFile() != null && !file.getParentFile().exists()) 
                file.getParentFile().mkdirs();

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String className = (clase != null) ? clase.getSimpleName() : "UnknownClass";
            String entry = String.format("[%s] %s.%s() ❱ %s", timestamp, className, metodo, logMsg);

            // Uso de try-with-resources para asegurar el cierre del archivo
            try (FileWriter fw = new FileWriter(logPath, true);
                 PrintWriter writer = new PrintWriter(fw)) {
                
                System.err.println(CMDColor.RED + " [PYRALINE-LOG] " + entry + CMDColor.RESET);
                writer.println(entry);
            }
        } catch (Exception ex) {
            System.err.println("CRÍTICO: Falló la escritura del log: " + ex.getMessage());
        }
    }
}