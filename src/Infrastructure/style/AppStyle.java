package Infrastructure.style;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public abstract class AppStyle {
    private static final String FONT_FAMILY     = "NovaMono";
    

    public static final Color COLOR_ACCENT      = new Color(255, 85, 0);   // Naranja Neón
    public static final Color COLOR_FONT        = new Color(220, 10, 20);  // Rojo técnico
    public static final Color COLOR_FONT_LIGHT  = new Color(100, 100, 100);
    public static final Color COLOR_CURSOR      = Color.BLACK;
    public static final Font FONT               = new Font(FONT_FAMILY, Font.PLAIN, 14);
    public static final Font FONT_BOLD          = new Font(FONT_FAMILY, Font.BOLD, 15);
    public static final Cursor CURSOR_HAND      = new Cursor(Cursor.HAND_CURSOR);
    public static final CompoundBorder createBorderRect(){
        return BorderFactory.createCompoundBorder(new LineBorder(COLOR_ACCENT), new EmptyBorder(5, 5, 5, 5));
    }

    // --- ADICIONALES PARA EL DASHBOARD ---
    public static final Color COLOR_PURPLE      = new Color(160, 0, 255);  // Morado Cyberpunk
    public static final Color COLOR_BG          = new Color(10, 10, 10);   // Fondo oscuro profundo
    public static final Color COLOR_NORMAL      = new Color(0, 255, 120);  // Verde Neón (Estado OK)
    public static final Font  FONT_TITLE        = new Font(FONT_FAMILY, Font.BOLD, 28);
}