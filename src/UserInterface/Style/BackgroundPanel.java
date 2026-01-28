package UserInterface.Style;

import java.awt.*;
import java.net.URL;
import javax.swing.*;

public class BackgroundPanel extends JPanel {
    private Image imgCircuits;
    private boolean mostrarCuadricula = false; 

    public BackgroundPanel() {
        this.imgCircuits = cargarImagen("Splash.png"); 
        this.setLayout(null);
    }

    
    public void configurarDashboard(String nombreImagen) {
        this.imgCircuits = cargarImagen(nombreImagen);
        this.mostrarCuadricula = true;
        this.setLayout(new BorderLayout()); // Cambiamos a BorderLayout para el Dashboard
        repaint();
    }

    private Image cargarImagen(String nombreArchivo) {
        try {
            URL url = getClass().getResource("/Infrastructure/resources/img/" + nombreArchivo);
            if (url != null) return new ImageIcon(url).getImage();
        } catch (Exception e) {}
        return null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        if (imgCircuits != null) {
            g2.drawImage(imgCircuits, 0, 0, getWidth(), getHeight(), this);
        }
        
        // NUEVO: Cuadrícula técnica neón que solo se ve en el Dashboard
        if (mostrarCuadricula) {
            g2.setColor(new Color(255, 85, 0, 40)); // Naranja Neón con transparencia
            for (int i = 0; i < getWidth(); i += 50) g2.drawLine(i, 0, i, getHeight());
            for (int i = 0; i < getHeight(); i += 50) g2.drawLine(0, i, getWidth(), i);
        }
    }
}