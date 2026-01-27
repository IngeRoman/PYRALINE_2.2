package UserInterface.Form;

import BusinessLogic.ArduinoPollingService;
import DataAccess.DAOs.PYRALINEDAO;
import DataAccess.DTOs.PYRALINEDTO;
import Infrastructure.AppConfig; 
import Infrastructure.AppStyle;
import UserInterface.Style.BackgroundPanel;
import java.awt.*;
import java.net.URL;
import java.util.List;
import javax.sound.sampled.*; // Para el soporte de audio .wav
import javax.swing.*;
import javax.swing.border.LineBorder;

public class PyralineDashboard extends JFrame {
    private JLabel lblTemp, lblEstado, lblLogo, lblValorUmbral, lblSirena;
    private JPanel pnlCards; 
    private CardLayout cardLayout;
    private JTextPane txtHistorial;
    private Timer timerParpadeo; // Para el efecto visual de emergencia
    private Clip clipAlerta;     // Para la sirena de YouTube (.wav)
    private boolean esRojo = false;
    private String currentCard = "CARD_HOME"; 
    private final Color COLOR_PURPLE = new Color(160, 0, 255); 
    
    private float valorUmbralTemporal = AppConfig.getUmbralPersistido(); 
    private ArduinoPollingService pollingService;

    public PyralineDashboard() {
        setTitle("PYRALINE SYSTEM - DASHBOARD");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        BackgroundPanel mainPanel = new BackgroundPanel();
        mainPanel.configurarDashboard("Splash.png"); 
        setContentPane(mainPanel);

        // --- CARGA DE RECURSOS (Audio de Mateo) ---
        configurarAudio("/Infrastructure/resources/sounds/alarma.wav");

        // --- SIDEBAR ---
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS)); 
        sidebar.setOpaque(false);
        sidebar.setPreferredSize(new Dimension(260, 600));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 25, 60, 25));

        lblLogo = new JLabel();
        ImageIcon iconLogo = cargarIcono("/Infrastructure/resources/img/logo.png", 160, 140); 
        if (iconLogo != null) {
            lblLogo.setIcon(iconLogo);
            lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
            sidebar.add(lblLogo);
            sidebar.add(Box.createRigidArea(new Dimension(0, 30))); 
        }

        sidebar.add(crearBotonNavegacion("HOME", "CARD_HOME"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 15)));
        sidebar.add(crearBotonNavegacion("CONFIGURACIÓN", "CARD_CONFIG"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 15)));
        sidebar.add(crearBotonNavegacion("ALERTAS", "CARD_ALERTS"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 15)));
        sidebar.add(crearBotonSalida());
        
        mainPanel.add(sidebar, BorderLayout.WEST);

        cardLayout = new CardLayout();
        pnlCards = new JPanel(cardLayout);
        pnlCards.setOpaque(false);

        pnlCards.add(crearVistaHome(), "CARD_HOME");
        pnlCards.add(crearVistaHistorialMasivo(), "CARD_ALERTS");
        pnlCards.add(crearVistaConfiguracion(), "CARD_CONFIG");

        mainPanel.add(pnlCards, BorderLayout.CENTER);

        // --- TIMER DE ALERTA VISUAL ---
        timerParpadeo = new Timer(500, e -> {
            lblSirena.setVisible(!lblSirena.isVisible());
            esRojo = !esRojo;
            lblEstado.getParent().setBackground(esRojo ? new Color(120, 0, 0) : new Color(0, 0, 0, 0));
        });
    }

    private void configurarAudio(String ruta) {
        try {
            URL soundUrl = getClass().getResource(ruta);
            if (soundUrl != null) {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundUrl);
                clipAlerta = AudioSystem.getClip();
                clipAlerta.open(audioStream);
            }
        } catch (Exception e) {
            System.err.println("(!) Error al cargar audio: " + e.getMessage());
        }
    }

    /**
     * MÉTODO MAESTRO: Controla la emergencia (Visual + Sonoro).
     */
    public void setModoAlerta(boolean activa) {
        SwingUtilities.invokeLater(() -> {
            if (activa) {
                if (!timerParpadeo.isRunning()) {
                    timerParpadeo.start();
                    if (clipAlerta != null) {
                        clipAlerta.setFramePosition(0);
                        clipAlerta.loop(Clip.LOOP_CONTINUOUSLY); // Suena hasta apagar la alerta
                    }
                }
            } else {
                timerParpadeo.stop();
                if (clipAlerta != null) clipAlerta.stop();
                lblSirena.setVisible(false);
                lblEstado.getParent().setBackground(new Color(0, 0, 0, 0));
            }
        });
    }

    public void actualizarEstadoHardware(boolean conectado) {
        SwingUtilities.invokeLater(() -> {
            if (conectado) {
                lblEstado.setText("ESTADO: SISTEMA EN LÍNEA");
                lblEstado.setForeground(Color.GREEN);
                lblTemp.setForeground(Color.WHITE);
            } else {
                setModoAlerta(false); // Apagamos sirenas si no hay hardware
                lblEstado.setText("ERROR: SENSOR DESCONECTADO");
                lblEstado.setForeground(Color.RED);
                lblTemp.setText("TEMPERATURA: -- °C");
                lblTemp.setForeground(Color.GRAY);
            }
        });
    }

    public void actualizarMonitoreo(float temp, boolean esAlerta) {
        lblTemp.setText("TEMPERATURA: " + String.format("%.2f", temp) + " °C");
        if (esAlerta) {
            lblEstado.setText("ESTADO: ALERT");
            lblEstado.setForeground(AppStyle.COLOR_FONT); 
        } else {
            lblEstado.setText("ESTADO: NORMAL");
            lblEstado.setForeground(Color.GREEN);
        }
    }

    private JPanel crearVistaHome() {
        JPanel pnlHome = new JPanel(new GridBagLayout());
        pnlHome.setOpaque(false);
        JPanel pnlMonitor = new JPanel(new GridBagLayout());
        pnlMonitor.setOpaque(false);
        GridBagConstraints gbcM = new GridBagConstraints();
        gbcM.gridx = 0; gbcM.anchor = GridBagConstraints.CENTER;

        lblTemp = new JLabel("TEMPERATURA: -- °C");
        lblTemp.setFont(AppStyle.FONT_BOLD.deriveFont(28f)); 
        lblTemp.setForeground(Color.WHITE);
        lblTemp.setBorder(AppStyle.createBorderRect()); 

        lblEstado = new JLabel("ESTADO: NORMAL");
        lblEstado.setFont(AppStyle.FONT_BOLD.deriveFont(18f));
        lblEstado.setForeground(Color.GREEN);

        // Icono de Sirena
        lblSirena = new JLabel();
        ImageIcon iconSirena = cargarIcono("/Infrastructure/resources/img/sirena.png", 100, 100);
        if (iconSirena != null) lblSirena.setIcon(iconSirena);
        lblSirena.setVisible(false);

        gbcM.gridy = 0; gbcM.insets = new Insets(0, 0, 15, 0); pnlMonitor.add(lblTemp, gbcM);
        gbcM.gridy = 1; gbcM.insets = new Insets(15, 0, 0, 0); pnlMonitor.add(lblEstado, gbcM);
        gbcM.gridy = 2; gbcM.insets = new Insets(20, 0, 0, 0); pnlMonitor.add(lblSirena, gbcM);

        GridBagConstraints gbcP = new GridBagConstraints();
        gbcP.anchor = GridBagConstraints.CENTER;
        gbcP.insets = new Insets(0, 0, 0, 180); 
        pnlHome.add(pnlMonitor, gbcP);
        return pnlHome;
    }

    private JPanel crearVistaConfiguracion() {
        JPanel pnlMain = new JPanel(new GridBagLayout());
        pnlMain.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel titulo = new JLabel("CONFIGURACIÓN");
        titulo.setFont(AppStyle.FONT_BOLD.deriveFont(24f));
        titulo.setForeground(Color.WHITE);
        gbc.gridy = 0; gbc.insets = new Insets(0, 0, 40, 100);
        pnlMain.add(titulo, gbc);

        JPanel pnlSelector = new JPanel(new BorderLayout());
        pnlSelector.setOpaque(false);
        pnlSelector.setPreferredSize(new Dimension(400, 65));
        pnlSelector.setBorder(new LineBorder(COLOR_PURPLE, 2, true));

        JLabel txtCambiar = new JLabel("  CAMBIAR UMBRAL");
        txtCambiar.setFont(AppStyle.FONT_BOLD.deriveFont(16f));
        txtCambiar.setForeground(Color.WHITE);
        pnlSelector.add(txtCambiar, BorderLayout.WEST);

        lblValorUmbral = new JLabel(valorUmbralTemporal + " °C  ");
        lblValorUmbral.setFont(AppStyle.FONT_BOLD.deriveFont(20f));
        lblValorUmbral.setForeground(AppStyle.COLOR_ACCENT);
        lblValorUmbral.setHorizontalAlignment(SwingConstants.RIGHT);
        pnlSelector.add(lblValorUmbral, BorderLayout.CENTER);

        JPanel pnlFlechas = new JPanel(new GridLayout(2, 1));
        pnlFlechas.setOpaque(false);
        JButton btnUp = new JButton("▲");
        JButton btnDown = new JButton("▼");
        estilizarBotonFlecha(btnUp);
        estilizarBotonFlecha(btnDown);

        btnUp.addActionListener(e -> { valorUmbralTemporal += 0.5f; actualizarLabelUmbral(); });
        btnDown.addActionListener(e -> { valorUmbralTemporal -= 0.5f; actualizarLabelUmbral(); });

        pnlFlechas.add(btnUp);
        pnlFlechas.add(btnDown);
        pnlSelector.add(pnlFlechas, BorderLayout.EAST);

        gbc.gridy = 1; pnlMain.add(pnlSelector, gbc);

        JButton btnGuardar = new JButton("GUARDAR CONFIGURACIÓN");
        btnGuardar.setPreferredSize(new Dimension(300, 45));
        btnGuardar.setFont(AppStyle.FONT_BOLD);
        btnGuardar.setBackground(Color.BLACK);
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setBorder(new LineBorder(Color.GREEN, 2));
        
        btnGuardar.addActionListener(e -> {
            if (pollingService != null) {
                pollingService.setUmbralAlarma(valorUmbralTemporal);
                JOptionPane.showMessageDialog(this, "<html><b style='color:green;'>UMBRAL GUARDADO</b><br>Límite actualizado a " + valorUmbralTemporal + "°C</html>");
            }
        });

        gbc.gridy = 2; gbc.insets = new Insets(30, 0, 0, 100);
        pnlMain.add(btnGuardar, gbc);

        JLabel lblFuego = new JLabel();
        ImageIcon img = cargarIcono("/Infrastructure/resources/img/fire_icon.png", 80, 80);
        if (img != null) lblFuego.setIcon(img);
        gbc.gridy = 3; gbc.insets = new Insets(40, 0, 0, 100);
        pnlMain.add(lblFuego, gbc);

        return pnlMain;
    }

    private void estilizarBotonFlecha(JButton b) {
        b.setOpaque(false); b.setContentAreaFilled(false);
        b.setForeground(COLOR_PURPLE); b.setBorder(null);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void actualizarLabelUmbral() {
        lblValorUmbral.setText(String.format("%.1f", valorUmbralTemporal) + " °C  ");
    }

    private JPanel crearVistaHistorialMasivo() {
        JPanel pnlMain = new JPanel(new GridBagLayout());
        pnlMain.setOpaque(false);
        
        JPanel pnlContenedor = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(new Color(0, 0, 0, 225)); 
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        pnlContenedor.setOpaque(false);
        pnlContenedor.setBorder(new LineBorder(COLOR_PURPLE, 2));

        JLabel titulo = new JLabel("HISTORIAL DE ALERTAS - MONITOREO TOTAL");
        titulo.setFont(AppStyle.FONT_BOLD.deriveFont(22f));
        titulo.setForeground(Color.WHITE);
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        titulo.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        pnlContenedor.add(titulo, BorderLayout.NORTH);

        txtHistorial = new JTextPane();
        txtHistorial.setContentType("text/html");
        txtHistorial.setEditable(false);
        txtHistorial.setOpaque(false);

        JScrollPane scroll = new JScrollPane(txtHistorial);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder(0, 25, 20, 25));
        pnlContenedor.add(scroll, BorderLayout.CENTER);

        JButton btnBorrar = new JButton("LIMPIAR REGISTROS DEL SISTEMA");
        btnBorrar.setFont(AppStyle.FONT_BOLD.deriveFont(12f));
        btnBorrar.setBackground(new Color(60, 0, 0)); 
        btnBorrar.setForeground(Color.WHITE);
        btnBorrar.setFocusPainted(false);
        btnBorrar.setBorder(new LineBorder(Color.RED, 1));
        
        btnBorrar.addActionListener(e -> {
            int resp = JOptionPane.showConfirmDialog(this, "¿Seguro que desea vaciar el historial?", "Atención Mateo", JOptionPane.YES_NO_OPTION);
            if(resp == JOptionPane.YES_OPTION) {
                try {
                    if(new PYRALINEDAO().deleteAll()) {
                        refrescarHistorial();
                        JOptionPane.showMessageDialog(this, "Historial vaciado correctamente.");
                    }
                } catch (Exception ex) { }
            }
        });
        pnlContenedor.add(btnBorrar, BorderLayout.SOUTH);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH; 
        gbc.insets = new Insets(10, 10, 20, 100); 
        pnlMain.add(pnlContenedor, gbc);
        return pnlMain;
    }

    public void refrescarHistorial() {
        try {
            StringBuilder html = new StringBuilder("<html><body style='font-family:Consolas; color:white; font-size:14px;'>");
            List<PYRALINEDTO> logs = new PYRALINEDAO().readAll(); 
            if (logs.isEmpty()) {
                html.append("<p style='text-align:center; color:#888; padding-top:200px;'>LOG VACÍO: Esperando eventos...</p>");
            } else {
                for (PYRALINEDTO log : logs) {
                    String color = (log.getIdTipoAlerta() == 3) ? "#00FF78" : "#FF5500"; 
                    String msg = (log.getIdTipoAlerta() == 3) ? "ESTADO: NORMAL RESTAURADO" : "ALERTA: TEMPERATURA CRÍTICA DETECTADA";
                    html.append("<div style='margin-bottom:12px; border-bottom: 1px solid #333; padding-bottom:8px;'>")
                        .append("<b style='color:").append(color).append(";'> ▶ FECHA: </b>").append(log.getFechaHora()).append("<br>")
                        .append("<span style='color:").append(color).append(";'> &nbsp;&nbsp;&nbsp;")
                        .append(msg).append(" (").append(log.getTemperatura()).append(" °C)</span></div>");
                }
            }
            html.append("</body></html>");
            txtHistorial.setText(html.toString()); 
        } catch (Exception e) { }
    }

    public void setPollingService(ArduinoPollingService service) {
        this.pollingService = service;
    }

    private JButton crearBotonNavegacion(String t, String card) {
        JButton b = new JButton(t);
        b.setMaximumSize(new Dimension(210, 50)); 
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setFont(AppStyle.FONT_BOLD); b.setForeground(Color.WHITE); b.setBackground(Color.BLACK);
        b.setCursor(AppStyle.CURSOR_HAND); b.setBorder(BorderFactory.createLineBorder(COLOR_PURPLE, 2));
        b.setFocusPainted(false);
        b.addActionListener(e -> {
            this.currentCard = card;
            cardLayout.show(pnlCards, card);
            if(card.equals("CARD_ALERTS")) refrescarHistorial(); 
        });
        return b;
    }

    private JButton crearBotonSalida() {
        JButton b = crearBotonNavegacion("LOG OUT", "");
        for(java.awt.event.ActionListener al : b.getActionListeners()) b.removeActionListener(al);
        b.addActionListener(e -> { this.dispose(); new PyralineLogin().setVisible(true); });
        return b;
    }

    private ImageIcon cargarIcono(String path, int width, int height) {
        URL url = getClass().getResource(path);
        if (url != null) {
            ImageIcon icon = new ImageIcon(url);
            Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        }
        return null;
    }
}