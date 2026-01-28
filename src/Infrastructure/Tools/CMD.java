package Infrastructure.Tools;

/**
 * Utilidades para la gestión de la consola de comandos (Command Line Interface).
 * Proporciona métodos para limpieza de pantalla y salida de texto con formato de color.
 */
public abstract class CMD {

    /** Constructor privado para evitar la instanciación de la clase de utilidad. */
    private CMD() {}

    /**
     * Limpia la terminal utilizando códigos de escape ANSI.
     * Envía la instrucción de 'Home' y 'Clear Screen' al flujo de salida.
     */
    public static void clear() {
        try {
            // ANSI: \033[H (Mueve cursor al inicio), \033[2J (Limpia pantalla)
            System.out.print("\033[H\033[2J");
            System.out.flush();
        } catch (Exception e) {
            // En caso de que el flujo de salida esté bloqueado o no soporte ANSI
            System.out.println("\n--- Pantalla Reiniciada ---\n");
        }
    }

    /**
     * Imprime un mensaje informativo con color CIAN.
     * @param msg Texto a mostrar en consola.
     */
    public static void println(String msg) {
        // Aplica el color cian y resetea el formato al terminar la línea
        System.out.println(CMDColor.CYAN + msg + CMDColor.RESET);
    } 

    /**
     * Imprime un mensaje de error con color ROJO.
     * @param msg Texto descriptivo del fallo.
     */
    public static void printlnError(String msg) {
        // Se utiliza para reportar fallos visuales rápidos en la terminal
        System.out.println(CMDColor.RED + msg + CMDColor.RESET);
    } 
}