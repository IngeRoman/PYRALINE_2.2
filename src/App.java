import javax.swing.SwingUtilities;

import UI.form.PyralineLogin;

public class App {
    public static void main(String[] args) {
        // Configuramos el estilo visual antes de iniciar
        System.out.println(">>> Iniciando Sistema PYRALINE <<<");

        SwingUtilities.invokeLater(() -> {
            // Iniciamos con el Login por seguridad
            PyralineLogin login = new PyralineLogin();
            login.setVisible(true);
        });
    }
}