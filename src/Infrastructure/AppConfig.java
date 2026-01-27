package Infrastructure;

import Infrastructure.Tools.CMD;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Properties;

public abstract class AppConfig {
    private static final Properties props = new Properties();
    private static final String APP_PROPERTIES = "app.properties";

    static {
        try (InputStream is = new FileInputStream(APP_PROPERTIES)) {
            props.load(is);
        } catch (Exception e) {
            try (InputStream isAlt = AppConfig.class.getClassLoader().getResourceAsStream("app.properties")) {
                if (isAlt != null) props.load(isAlt);
                else CMD.printlnError("No se pudo cargar app.properties");
            } catch (Exception e2) { }
        }
    }

    public static String getDATABASE() { return props.getProperty("db.File"); }
    public static String getLOGFILE()  { return props.getProperty("df.logFile"); }
    
    // --- NUEVAS FUNCIONES PARA PERSISTENCIA DEL UMBRAL ---

    /**
     * Recupera el umbral guardado. Si no existe, devuelve 32.0 por defecto.
     */
    public static float getUmbralPersistido() { 
        return Float.parseFloat(props.getProperty("app.umbral", "32.0")); 
    }

    /**
     * Guarda el nuevo umbral físicamente en el archivo app.properties.
     */
    public static void guardarUmbral(float valor) {
        props.setProperty("app.umbral", String.valueOf(valor));
        try (FileOutputStream out = new FileOutputStream(APP_PROPERTIES)) {
            props.store(out, "Configuracion Pyraline -");
        } catch (Exception e) {
            System.err.println("(!) Error: No se pudo escribir en app.properties");
        }
    }

    public static final String MSG_DEFAULT_ERROR = "Error inesperado en el sistema.";
    protected abstract String getImgSplash();
}