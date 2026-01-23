package Infrastructure.Tools;

public abstract class CMD {

    private CMD() {}

    public static void clear() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static void println(String msg){
        System.out.println(CMDColor.CYAN + msg + CMDColor.RESET);
    } 
    public static void printlnError(String msg){
        System.out.println(CMDColor.RED + msg + CMDColor.RESET);
    } 
}
