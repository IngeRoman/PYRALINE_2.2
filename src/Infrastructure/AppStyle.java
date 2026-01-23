//  © 2K26 ❱──💀──❰ pat_mic ? code is life : life is code
package Infrastructure;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public abstract class AppStyle {
    private static final String FONT_FAMILY     = "NovaMono";
    
    public static final Color COLOR_FONT        = new Color(220, 10, 20); //(218, 8, 40)
    public static final Color COLOR_FONT_LIGHT  = new Color(100, 100, 100);
    public static final Color COLOR_CURSOR      = Color.black;
    public static final Color COLOR_BORDER      = Color.lightGray;

    public static final Font  FONT              = new Font(FONT_FAMILY, Font.PLAIN, 14);
    public static final Font  FONT_SMALL        = new Font(FONT_FAMILY, Font.PLAIN, 10);
    public static final Font  FONT_BOLD         = new Font(FONT_FAMILY, Font.BOLD | Font.PLAIN, 15);

    public static final int ALIGNMENT_LEFT  = SwingConstants.LEFT;
    public static final int ALIGNMENT_RIGHT = SwingConstants.RIGHT;
    public static final int ALIGNMENT_CENTER= SwingConstants.CENTER;

    public static final Cursor CURSOR_HAND    = new Cursor(Cursor.HAND_CURSOR);
    public static final Cursor CURSOR_DEFAULT = new Cursor(Cursor.DEFAULT_CURSOR);

    private AppStyle() {}
    public static final CompoundBorder createBorderRect(){
        return BorderFactory.createCompoundBorder(  new LineBorder(Color.lightGray),
                                                    new EmptyBorder(5, 5, 5, 5));
    }
}

