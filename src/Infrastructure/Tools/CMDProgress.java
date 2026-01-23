package Infrastructure.Tools;

public abstract class CMDProgress {

    private CMDProgress() {}
    
    /**
     * avance de tareas con spinner
     */
    public static String showSpinner()  
    {
        String c= "|/-\\|"; 
        for (int x =0 ; x <= 100 ; x++) {
            String srt = "\r "+ c.charAt(x % c.length()) + " " + x + " % ";
            System.out.print(srt);
            try {
                Thread.sleep(10);
            } catch (InterruptedException _) {
                Thread.currentThread().interrupt();
            }
        }
        return "";
    }

    /**
     * Muestra una barra de progreso en la consola.
     * @param c
     * @throws InterruptedException
     */
    public static void showBar(String c) throws InterruptedException {
        int sizeBar = 30;
        for (int i = 0; i <= 100; i++) {
            int nroChar = i * sizeBar / 100;  // número de caracteres llenos
            String bar = "\r[" 
                        + c.repeat(nroChar) 
                        + " ".repeat(sizeBar - nroChar) 
                        + "] " + i + " % ";
            System.out.print(bar);
            Thread.sleep(10);
        }
    }

    public static String progressBar(int progressBarSize, long currentPosition, long startPositoin, long finishPosition) 
    {
        char pb = '?';
        char stat = '?';
        StringBuilder bar = new StringBuilder();
        int nPositions = progressBarSize;

        for (int p = 0; p < nPositions; p++)
            bar.append(pb);
        int ststus = (int) (100 * (currentPosition - startPositoin) / (finishPosition - startPositoin));
        int move = (nPositions * ststus) / 100;
        return "[" + bar.substring(0, move).replace(pb, stat) + ststus + "%" + bar.substring(move, bar.length()) + "]";
    }
}
