package Infrastructure;

import com.fazecast.jSerialComm.SerialPort;
import BusinessLogic.ArduinoPollingService; // Vinculación necesaria
import java.io.InputStream;

public class ArduinoSensor {
    private SerialPort puertoSerial;

    /**
     * Inicia la conexión serial y entrega los datos al servicio de monitoreo.
     */
    public void conectar(String puertoNombre, ArduinoPollingService service) {
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
                                    // Se envía el dato al método procesarLectura del servicio
                                    service.procesarLectura(dato); 
                                }
                                sb.setLength(0);
                            } else {
                                sb.append(c);
                            }
                        }
                        Thread.sleep(100);
                    }
                } catch (Exception e) {
                    System.err.println("(!) Error en lectura serial: " + e.getMessage());
                }
            });
            hilo.setDaemon(true); 
            hilo.start();
        } else {
            System.err.println("(!) ERROR: No se pudo abrir el puerto " + puertoNombre);
        }
    }
}