package Infrastructure;

import Infrastructure.Tools.CMD;
import java.io.FileInputStream;
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
    public static final String MSG_DEFAULT_ERROR = "Error inesperado en el sistema.";
    protected abstract String getImgSplash();
}