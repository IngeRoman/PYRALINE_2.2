package UI.form;

import DataAccess.dao.PYRALINEDAO;
import DataAccess.dto.PYRALINEDTO;
import Infrastructure.config.AppConfig;
import Infrastructure.exeption.AppException;
import Infrastructure.logging.AppMSG;
import Infrastructure.style.AppStyle;
import UI.components.BackgroundPanel;


import java.awt.*;
import java.net.URL;
import java.util.List;
import javax.sound.sampled.*; 
import javax.swing.*;
import javax.swing.border.LineBorder;

import BusinessLogic.services.ArduinoPollingService;

/**
 * Dashboard principal del sistema Pyraline.
 * Gestiona la visualización de sensores, bitácora histórica y configuración.
 * @version 2.2
 */
public class PyralineDashboard extends JFrame {
    private JLabel lblTemp, lblEstado, lblLogo, lblValorUmbral, lblSirena;
    private JPanel pnlCards; 
    private CardLayout cardLayout;
    private JTextPane txtHistorial;
    private Timer timerParpadeo; 
    private Clip clipAlerta;     
    private boolean esRojo = false;
    private final Color COLOR_PURPLE = new Color(160, 0, 255); 
    
    private float valorUmbralTemporal = AppConfig.getUmbralPersistido(); 
    private ArduinoPollingService pollingService;

    public PyralineDashboard() {
        configurarVentana();
        inicializarUI();
    }

    /** Configuración inicial de la ventana principal. */
    private void configurarVentana() {
        setTitle("PYRALINE SYSTEM - DASHBOARD");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    /** Carga de paneles, recursos y estilos. */
    private void inicializarUI() {
        BackgroundPanel mainPanel = new BackgroundPanel();
        mainPanel.configurarDashboard("Splash.png"); 
        setContentPane(mainPanel);

        // Intenta cargar la sirena para alertas críticas
        configurarAudio("/Infrastructure/resources/sounds/alarma.wav");

        // --- CONSTRUCCIÓN DE SIDEBAR ---
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS)); 
        sidebar.setOpaque(false);
        sidebar.setPreferredSize(new Dimension(260, 600));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 25, 60, 25));

        cargarLogo(sidebar);
        sidebar.add(crearBotonNavegacion("HOME", "CARD_HOME"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 15)));
        sidebar.add(crearBotonNavegacion("CONFIGURACIÓN", "CARD_CONFIG"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 15)));
        sidebar.add(crearBotonNavegacion("ALERTAS", "CARD_ALERTS"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 15)));
        sidebar.add(crearBotonSalida());
        
        mainPanel.add(sidebar, BorderLayout.WEST);

        // --- CONTENEDOR DE VISTAS DINÁMICAS ---
        cardLayout = new CardLayout();
        pnlCards = new JPanel(cardLayout);
        pnlCards.setOpaque(false);
        pnlCards.add(crearVistaHome(), "CARD_HOME");
        pnlCards.add(crearVistaHistorialMasivo(), "CARD_ALERTS");
        pnlCards.add(crearVistaConfiguracion(), "CARD_CONFIG");

        mainPanel.add(pnlCards, BorderLayout.CENTER);

        // Timer para el efecto de parpadeo en emergencia
        timerParpadeo = new Timer(500, e -> ejecutarEfectoSirena());
    }

    /**
     * Carga el archivo de audio para la alarma.
     * @param ruta Path relativo del recurso .wav.
     */
    private void configurarAudio(String ruta) {
        try {
            URL soundUrl = getClass().getResource(ruta);
            if (soundUrl != null) {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundUrl);
                clipAlerta = AudioSystem.getClip();
                clipAlerta.open(audioStream);
            }
        } catch (Exception e) {
            // No detiene el programa, pero registra el fallo técnico
            new AppException("Fallo al cargar recurso de audio.", e, getClass(), "configurarAudio");
        }
    }

    /**
     * MÉTODO MAESTRO: Activa o desactiva la alerta sonora y visual.
     * @param activa Estado de la emergencia.
     */
    public void setModoAlerta(boolean activa) {
        SwingUtilities.invokeLater(() -> {
            try {
                if (activa) {
                    if (!timerParpadeo.isRunning()) {
                        timerParpadeo.start();
                        if (clipAlerta != null) {
                            clipAlerta.setFramePosition(0);
                            clipAlerta.loop(Clip.LOOP_CONTINUOUSLY);
                        }
                    }
                } else {
                    timerParpadeo.stop();
                    if (clipAlerta != null) clipAlerta.stop();
                    limpiarEstadoVisual();
                }
            } catch (Exception e) {
                new AppException("Error al gestionar modo alerta UI.", e, getClass(), "setModoAlerta");
            }
        });
    }

    /** Notifica desconexión física del hardware. */
    public void actualizarEstadoHardware(boolean conectado) {
        SwingUtilities.invokeLater(() -> {
            if (conectado) {
                lblEstado.setText("ESTADO: SISTEMA EN LÍNEA");
                lblEstado.setForeground(Color.GREEN);
                lblTemp.setForeground(Color.WHITE);
            } else {
                setModoAlerta(false);
                lblEstado.setText("ERROR: SENSOR DESCONECTADO");
                lblEstado.setForeground(Color.RED);
                lblTemp.setText("TEMPERATURA: -- °C");
                lblTemp.setForeground(Color.GRAY);
            }
        });
    }

    /** Actualiza la medición térmica en tiempo real. */
    public void actualizarMonitoreo(float temp, boolean esAlerta) {
        lblTemp.setText("TEMPERATURA: " + String.format("%.2f", temp) + " °C");
        if (esAlerta) {
            lblEstado.setText("ESTADO: CRÍTICO");
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

        JLabel titulo = new JLabel("CONFIGURACIÓN DE SENSORES");
        titulo.setFont(AppStyle.FONT_BOLD.deriveFont(24f));
        titulo.setForeground(Color.WHITE);
        gbc.gridy = 0; gbc.insets = new Insets(0, 0, 40, 100);
        pnlMain.add(titulo, gbc);

        // --- SELECTOR DE UMBRAL ---
        JPanel pnlSelector = new JPanel(new BorderLayout());
        pnlSelector.setOpaque(false);
        pnlSelector.setPreferredSize(new Dimension(400, 65));
        pnlSelector.setBorder(new LineBorder(COLOR_PURPLE, 2, true));

        JLabel txtCambiar = new JLabel("   AJUSTAR LÍMITE");
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

        // --- BOTÓN GUARDAR ---
        JButton btnGuardar = new JButton("GUARDAR CAMBIOS EN DISCO");
        btnGuardar.setPreferredSize(new Dimension(300, 45));
        btnGuardar.setFont(AppStyle.FONT_BOLD);
        btnGuardar.setBackground(Color.BLACK);
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setBorder(new LineBorder(Color.GREEN, 2));
        
        btnGuardar.addActionListener(e -> {
            try {
                if (pollingService != null) {
                    pollingService.setUmbralAlarma(valorUmbralTemporal);
                    AppMSG.showInformation("Configuración guardada: Umbral a " + valorUmbralTemporal + "°C");
                }
            } catch (Exception ex) {
                AppMSG.showError("No se pudo persistir la configuración.");
            }
        });

        gbc.gridy = 2; gbc.insets = new Insets(30, 0, 0, 100);
        pnlMain.add(btnGuardar, gbc);

        return pnlMain;
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

        JLabel titulo = new JLabel("BITÁCORA DE EVENTOS TÉRMICOS");
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

        JButton btnBorrar = new JButton("VACIAR HISTORIAL FÍSICO");
        btnBorrar.setFont(AppStyle.FONT_BOLD.deriveFont(12f));
        btnBorrar.setBackground(new Color(60, 0, 0)); 
        btnBorrar.setForeground(Color.WHITE);
        btnBorrar.setBorder(new LineBorder(Color.RED, 1));
        
        btnBorrar.addActionListener(e -> {
            if (AppMSG.showConfirmYesNo("¿Seguro que desea eliminar todos los registros de Pyraline?")) {
                try {
                    if (new PYRALINEDAO().deleteAll()) {
                        refrescarHistorial();
                        AppMSG.showInformation("Historial vaciado correctamente.");
                    }
                } catch (AppException ex) {
                    AppMSG.showError("Fallo al limpiar la base de datos.");
                }
            }
        });
        pnlContenedor.add(btnBorrar, BorderLayout.SOUTH);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1.0; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.BOTH; 
        gbc.insets = new Insets(10, 10, 20, 100); 
        pnlMain.add(pnlContenedor, gbc);
        return pnlMain;
    }

    /** Consulta la base de datos y formatea el log en HTML. */
    public void refrescarHistorial() {
        try {
            StringBuilder html = new StringBuilder("<html><body style='font-family:Consolas; color:white; font-size:13px;'>");
            List<PYRALINEDTO> logs = new PYRALINEDAO().readAll(); 
            
            if (logs.isEmpty()) {
                html.append("<p style='text-align:center; color:#888; padding-top:150px;'>LISTA VACÍA: No hay alertas registradas.</p>");
            } else {
                for (PYRALINEDTO log : logs) {
                    String color = (log.getIdTipoAlerta() == 3) ? "#00FF78" : "#FF5500"; 
                    String msg = (log.getIdTipoAlerta() == 3) ? "ESTADO: NORMALIZADO" : "ALERTA: SOBRE UMBRAL";
                    html.append("<div style='margin-bottom:10px; border-bottom: 1px solid #333;'>")
                        .append("<b style='color:").append(color).append(";'>[ ").append(log.getFechaHora()).append(" ]</b><br>")
                        .append("&nbsp;&nbsp;").append(msg).append(" (").append(log.getTemperatura()).append(" °C)</div>");
                }
            }
            html.append("</body></html>");
            txtHistorial.setText(html.toString()); 
        } catch (AppException e) {
            AppMSG.showError("No se pudo refrescar la bitácora.");
        }
    }

    // --- MÉTODOS DE APOYO Y ESTILO ---

    private void ejecutarEfectoSirena() {
        lblSirena.setVisible(!lblSirena.isVisible());
        esRojo = !esRojo;
        lblEstado.getParent().setBackground(esRojo ? new Color(120, 0, 0, 150) : new Color(0, 0, 0, 0));
    }

    private void limpiarEstadoVisual() {
        lblSirena.setVisible(false);
        lblEstado.getParent().setBackground(new Color(0, 0, 0, 0));
    }

    private void actualizarLabelUmbral() {
        lblValorUmbral.setText(String.format("%.1f", valorUmbralTemporal) + " °C  ");
    }

    private void cargarLogo(JPanel sidebar) {
        ImageIcon iconLogo = cargarIcono("/Infrastructure/resources/img/logo.png", 160, 140); 
        if (iconLogo != null) {
            lblLogo = new JLabel(iconLogo);
            lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
            sidebar.add(lblLogo);
            sidebar.add(Box.createRigidArea(new Dimension(0, 30))); 
        }
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
            cardLayout.show(pnlCards, card);
            if(card.equals("CARD_ALERTS")) refrescarHistorial(); 
        });
        return b;
    }

    private JButton crearBotonSalida() {
        JButton b = crearBotonNavegacion("CERRAR SESIÓN", "");
        for(java.awt.event.ActionListener al : b.getActionListeners()) b.removeActionListener(al);
        b.addActionListener(e -> { this.dispose(); new PyralineLogin().setVisible(true); });
        return b;
    }

    private void estilizarBotonFlecha(JButton b) {
        b.setOpaque(false); b.setContentAreaFilled(false);
        b.setForeground(COLOR_PURPLE); b.setBorder(null);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private ImageIcon cargarIcono(String path, int width, int height) {
        URL url = getClass().getResource(path);
        if (url != null) {
            ImageIcon icon = new ImageIcon(url);
            return new ImageIcon(icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));
        }
        return null;
    }
}