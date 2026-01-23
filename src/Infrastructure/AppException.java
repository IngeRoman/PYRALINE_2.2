package Infrastructure;

import Infrastructure.Tools.CMDColor;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AppException extends Exception {
    public AppException(String showMsg, Exception e, Class<?> clase, String metodo) {
        super((showMsg == null || showMsg.isBlank()) ? AppConfig.MSG_DEFAULT_ERROR : showMsg);
        saveLogFile(e != null ? e.getMessage() : "Sin detalle", clase, metodo);
    }

    private void saveLogFile(String logMsg, Class<?> clase, String metodo) {
        String logPath = AppConfig.getLOGFILE();
        if (logPath == null) logPath = "storage/Logs/AppErrors.log";

        File file = new File(logPath);
        if (file.getParentFile() != null) file.getParentFile().mkdirs();

        String entry = String.format("[%s] %s.%s ❱ %s", 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), 
                (clase != null ? clase.getSimpleName() : "N/A"), metodo, logMsg);

        try (PrintWriter writer = new PrintWriter(new FileWriter(logPath, true))) {
            System.err.println(CMDColor.RED + "LOG: " + entry + CMDColor.RESET);
            writer.println(entry);
        } catch (Exception ex) { }
    }
}