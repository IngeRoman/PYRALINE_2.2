package Infrastructure;

import com.fazecast.jSerialComm.SerialPort;
import java.io.InputStream;

public class ArduinoSensor {
    private static String ultimaLectura = "0.0";
    private SerialPort puertoSerial;

    public void conectar(String puertoNombre) {
        puertoSerial = SerialPort.getCommPort(puertoNombre);
        puertoSerial.setBaudRate(9600);
        puertoSerial.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);

        if (puertoSerial.openPort()) {
            System.out.println("\033[1;32m(✓) HARDWARE: Sensor conectado en " + puertoNombre + "\033[0m");
            
            Thread hilo = new Thread(() -> {
                try (InputStream in = puertoSerial.getInputStream()) {
                    StringBuilder sb = new StringBuilder();
                    while (true) {
                        if (in.available() > 0) {
                            char c = (char) in.read();
                            if (c == '\n' || c == '\r') {
                                String dato = sb.toString().trim();
                                if (!dato.isEmpty()) {
                                    ultimaLectura = dato;
                                }
                                sb.setLength(0);
                            } else {
                                sb.append(c);
                            }
                        }
                        Thread.sleep(100);
                    }
                } catch (Exception e) {
                    System.err.println("(!) Error sensor: " + e.getMessage());
                }
            });
            hilo.setDaemon(true); 
            hilo.start();
        } else {
            System.err.println("(!) ERROR: No se detecta Arduino en " + puertoNombre);
        }
    }

    public static String getUltimaLectura() {
        return ultimaLectura;
    }
}