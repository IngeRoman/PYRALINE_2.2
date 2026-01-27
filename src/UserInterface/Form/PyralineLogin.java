package UserInterface.Form;

import BusinessLogic.ArduinoPollingService; 
import BusinessLogic.UsuarioBL;
import Infrastructure.*; // Importa ArduinoSensor y AppStyle
import UserInterface.Style.BackgroundPanel;
import java.awt.*;
import javax.swing.*;

public class PyralineLogin extends JFrame {
    private JTextField     txtEmail;
    private JPasswordField txtPassword;
    private JButton         btnAcceder;
    private JLabel          lblUsuario, lblPassword, lblTitulo;

    public PyralineLogin() {
        setTitle("PYRALINE SYSTEM - Acceso");
        setSize(1000, 600);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setSize(1000, 600);
        setContentPane(layeredPane);

        // 1. Capa Fondo
        BackgroundPanel bg = new BackgroundPanel();
        bg.setBounds(0, 0, 1000, 600);
        layeredPane.add(bg, JLayeredPane.DEFAULT_LAYER);

        // 2. Capa de Componentes
        ImageIcon originalIcon = new ImageIcon(getClass().getResource("/Infrastructure/resources/img/name.png"));
        int targetWidth = 480; 
        Image scaledImage = originalIcon.getImage().getScaledInstance(targetWidth, -1, Image.SCALE_SMOOTH);
        ImageIcon finalIcon = new ImageIcon(scaledImage);
        
        lblTitulo = new JLabel(finalIcon);
        int xPosTitle = (1000 - targetWidth) / 2;
        lblTitulo.setBounds(xPosTitle, 15, targetWidth, finalIcon.getIconHeight()); 
        layeredPane.add(lblTitulo, JLayeredPane.PALETTE_LAYER);

        lblUsuario = new JLabel("USUARIO");
        lblUsuario.setBounds(345, 190, 310, 30);
        lblUsuario.setForeground(AppStyle.COLOR_ACCENT);
        lblUsuario.setFont(AppStyle.FONT_BOLD);
        layeredPane.add(lblUsuario, JLayeredPane.PALETTE_LAYER);

        lblPassword = new JLabel("CONTRASEÑA");
        lblPassword.setBounds(345, 280, 310, 30);
        lblPassword.setForeground(AppStyle.COLOR_ACCENT);
        lblPassword.setFont(AppStyle.FONT_BOLD);
        layeredPane.add(lblPassword, JLayeredPane.PALETTE_LAYER);

        txtEmail = new JTextField();
        txtEmail.setBounds(345, 220, 310, 42);
        txtEmail.setOpaque(false);
        txtEmail.setForeground(Color.WHITE);
        txtEmail.setBorder(AppStyle.createBorderRect());
        txtEmail.setCaretColor(AppStyle.COLOR_ACCENT);
        txtEmail.setFont(AppStyle.FONT_BOLD);
        layeredPane.add(txtEmail, JLayeredPane.PALETTE_LAYER);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(345, 310, 310, 42);
        txtPassword.setOpaque(false);
        txtPassword.setForeground(Color.WHITE);
        txtPassword.setBorder(AppStyle.createBorderRect());
        txtPassword.setCaretColor(AppStyle.COLOR_ACCENT);
        layeredPane.add(txtPassword, JLayeredPane.PALETTE_LAYER);

        btnAcceder = new JButton("ACCEDER");
        btnAcceder.setBounds(345, 400, 310, 55);
        btnAcceder.setBackground(AppStyle.COLOR_ACCENT);
        btnAcceder.setForeground(Color.WHITE);
        btnAcceder.setFont(AppStyle.FONT_BOLD);
        btnAcceder.setCursor(AppStyle.CURSOR_HAND);
        btnAcceder.setFocusPainted(false);
        layeredPane.add(btnAcceder, JLayeredPane.PALETTE_LAYER);

        // --- LÓGICA DE ACCESO Y CONEXIÓN DE HARDWARE ---
        btnAcceder.addActionListener(e -> {
            try {
                UsuarioBL bl = new UsuarioBL();
                if (bl.validarAcceso(txtEmail.getText(), new String(txtPassword.getPassword()))) {
                    
                    this.dispose();
                    PyralineDashboard dashboard = new PyralineDashboard();

                    try {
                        ArduinoPollingService service = new ArduinoPollingService(dashboard);
                        dashboard.setPollingService(service);
                        
                        // --- REFACTORIZACIÓN: DETECCIÓN ACTIVA ---
                        ArduinoSensor sensor = new ArduinoSensor();
                        String puertoDetectado = sensor.detectarPuertoAutomatico();
                        
                        // Siempre llamamos a conectar. Si puertoDetectado es null,
                        // el sensor activará solo el hilo de búsqueda infinita.
                        sensor.conectar(puertoDetectado, service); 
                        
                        if (puertoDetectado != null) {
                            System.out.println("(✓) Hardware detectado en: " + puertoDetectado);
                        } else {
                            System.err.println("(!) Sistema iniciado en modo búsqueda: Esperando conexión física.");
                        }
                        
                        dashboard.setVisible(true);
                        System.out.println("(✓) Sistema Pyraline activado para Mateo Sebastian.");
                        
                    } catch (Exception ex) {
                        System.err.println("(!) Error al iniciar servicios de Pyraline: " + ex.getMessage());
                        dashboard.setVisible(true);
                    }

                } else {
                    AppMSG.showError("Credenciales incorrectas.");
                }
            } catch (AppException ex) {
                AppMSG.showError("Error técnico: " + ex.getMessage());
            }
        });
    }
}