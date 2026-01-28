package Infrastructure.config;

import Infrastructure.console.CMD;
import Infrastructure.exeption.AppException;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * <h1>AppConfig</h1>
 * Gestiona la configuración global y persistencia de parámetros del sistema.
 * Implementa seguridad en la carga de archivos y manejo de tipos.
 */
public abstract class AppConfig {
    private static final Properties props = new Properties();
    private static final String APP_PROPERTIES = "app.properties";
    public static final String MSG_DEFAULT_ERROR = "Error inesperado en el sistema.";

    static {
        loadProperties();
    }

    /**
     * Intenta cargar el archivo de propiedades desde la raíz o el ClassLoader.
     */
    private static void loadProperties() {
        try (InputStream is = new FileInputStream(APP_PROPERTIES)) {
            props.load(is);
        } catch (Exception e) {
            try (InputStream isAlt = AppConfig.class.getClassLoader().getResourceAsStream(APP_PROPERTIES)) {
                if (isAlt != null) props.load(isAlt);
                else CMD.printlnError("No se pudo cargar app.properties");
            } catch (Exception e2) {
                // Silencioso para evitar bucles en el inicio
            }
        }
    }

    public static String getDATABASE() { return props.getProperty("db.File"); }
    public static String getLOGFILE()  { return props.getProperty("df.logFile"); }

    /**
     * Recupera el umbral con manejo de errores numéricos.
     * @return float valor del umbral o 32.0f si falla la lectura.
     */
    public static float getUmbralPersistido() { 
        try {
            return Float.parseFloat(props.getProperty("app.umbral", "32.0"));
        } catch (NumberFormatException e) {
            // Registramos el error de formato en el log si el archivo fue manipulado
            new AppException("Error de formato en umbral", e, AppConfig.class, "getUmbralPersistido");
            return 32.0f;
        }
    }

    /**
     * Guarda el umbral y registra cualquier fallo en el log oficial.
     * @param valor Nuevo umbral de temperatura.
     */
    public static void guardarUmbral(float valor) {
        props.setProperty("app.umbral", String.valueOf(valor));
        try (FileOutputStream out = new FileOutputStream(APP_PROPERTIES)) {
            props.store(out, "Configuracion Pyraline -");
        } catch (Exception e) {
            // Blindaje: Si no puede guardar, el sistema se entera vía log
            new AppException("No se pudo escribir en app.properties", e, AppConfig.class, "guardarUmbral");
        }
    }

    protected abstract String getImgSplash();
}