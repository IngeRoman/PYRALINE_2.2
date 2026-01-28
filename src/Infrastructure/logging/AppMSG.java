package Infrastructure.logging;

import javax.swing.JOptionPane;

/**
 * Utilidad para estandarizar la comunicación visual con el usuario.
 * Centraliza los estilos de los cuadros de diálogo de Pyraline.
 */
public abstract class AppMSG {

    // Título estándar para mantener la identidad del software
    private static final String TITLE = "PYRALINE SYSTEM";

    /**
     * Muestra una notificación de éxito o información general.
     */
    public static void showInformation(String msg) {
        // Validación preventiva: si el mensaje es nulo, evita el cuadro vacío
        String text = (msg != null) ? msg : "Operación realizada con éxito.";
        JOptionPane.showMessageDialog(null, text, TITLE, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Muestra una alerta de precaución (ej. campos incompletos).
     * Este es el "punto medio" que le faltaba a tu clase.
     */
    public static void showWarning(String msg) {
        JOptionPane.showMessageDialog(null, msg, "PYRALINE WARNING", JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Muestra una alerta de fallo crítico.
     */
    public static void showError(String msg) {
        try {
            String text = (msg != null) ? msg : "Ha ocurrido un error inesperado.";
            JOptionPane.showMessageDialog(null, text, "PYRALINE ERROR", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            // Si el entorno gráfico falla, al menos lo imprime en consola
            System.err.println("Fallo al mostrar cuadro de error: " + msg);
        }
    }

    /**
     * Solicita confirmación del usuario antes de una acción sensible.
     * @return true si el usuario presiona 'SÍ'.
     */
    public static boolean showConfirmYesNo(String msg) {
        try {
            return (JOptionPane.showConfirmDialog(null, msg, "PYRALINE QUESTION", 
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION);
        } catch (Exception e) {
            // En caso de error en la UI, por seguridad retorna false (No cancelar)
            return false;
        }
    }
}