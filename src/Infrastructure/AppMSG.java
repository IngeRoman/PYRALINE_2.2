package Infrastructure;

import javax.swing.JOptionPane;

/**
 * Utilidad para mostrar mensajes estandarizados en el sistema Pyraline.
 * Capa: Infrastructure
 */
public abstract class AppMSG {

    /**
     * Muestra una alerta de información (el método que te faltaba).
     */
    public static void showInformation(String msg) {
        JOptionPane.showMessageDialog(null, msg, "PYRALINE SYSTEM", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Muestra una alerta de error.
     */
    public static void showError(String msg) {
        JOptionPane.showMessageDialog(null, msg, "PYRALINE ERROR", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Muestra una confirmación (Sí/No).
     */
    public static boolean showConfirmYesNo(String msg) {
        return (JOptionPane.showConfirmDialog(null, msg, "PYRALINE QUESTION", 
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION);
    }
}