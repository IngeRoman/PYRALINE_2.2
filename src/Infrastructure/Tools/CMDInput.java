package Infrastructure.Tools;

import java.util.Scanner;

public abstract class CMDInput {
    static Scanner sc = new Scanner(System.in);
    private CMDInput(){}
    
    /**
     * Captura un numero entero y que sea mayor a cero
     * @param etiqueta
     * @return numero entero positivo
     */
    public static int getNumeroPositivo(String etiqueta, String errorMsg){
        int n =-1;
        String str;
        do{
            System.out.print(etiqueta);
            str =  sc.next();
            try {
                n = Integer.parseInt(str);
            } catch (Exception _) {
                System.out.println(errorMsg);
            }
        }while(n<0);
        return n;
    }
    
    public static String getCaracteres(String etiqueta)
    {
        String str="";
        do {
            System.out.print(etiqueta);
            str =  sc.next().trim();
            if (str.trim().equals(""))
                System.out.println(" :( Valor no valido... !");
        } while (str.isEmpty());
        return str;
    }

    public static void pressKey()
    {
        System.out.print("\n\nPress any key..");
        try{
            System.in.read();
        }catch(Exception _){
            System.out.println("");
        }
    }
}
