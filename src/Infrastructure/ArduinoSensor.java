package Infrastructure;

import com.fazecast.jSerialComm.SerialPort;
import BusinessLogic.ArduinoPollingService;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ArduinoSensor {
    private SerialPort puertoSerial;
    private static String ultimaLectura = "0.0";
    private volatile boolean intentandoReconectar = false;

    public String detectarPuertoAutomatico() {
        SerialPort[] puertos = SerialPort.getCommPorts();
        for (SerialPort p : puertos) {
            String desc = p.getDescriptivePortName().toUpperCase();
            if (desc.contains("USB") || desc.contains("ARDUINO") || desc.contains("CH340") || desc.contains("SERIAL")) {
                return p.getSystemPortName();
            }
        }
        return null;
    }

    public void conectar(String puertoNombre, ArduinoPollingService service) {
        // REFACTORIZACIÓN: Si no hay puerto al inicio, activamos el escaneo de una vez
        if (puertoNombre == null) {
            manejarDesconexion(service);
            return;
        }

        puertoSerial = SerialPort.getCommPort(puertoNombre);
        puertoSerial.setBaudRate(9600);
        puertoSerial.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 200, 0);

        if (puertoSerial.openPort()) {
            System.out.println("\033[1;32m(✓) HARDWARE: Conectado en " + puertoNombre + "\033[0m");
            intentandoReconectar = false;
            if (service != null) service.notificarEstadoConexion(true);

            Thread hilo = new Thread(() -> {
                try (InputStream in = puertoSerial.getInputStream()) {
                    StringBuilder sb = new StringBuilder();
                    while (!intentandoReconectar && puertoSerial.isOpen()) {
                        // Verificación de integridad física del cable
                        if (puertoSerial.bytesAvailable() == -1) break; 

                        if (in.available() > 0) {
                            int byteRead = in.read();
                            if (byteRead == -1) break;
                            char c = (char) byteRead;
                            if (c == '\n' || c == '\r') {
                                String dato = sb.toString().trim().replaceAll("[^\\d.]", ""); 
                                if (!dato.isEmpty()) {
                                    ultimaLectura = dato;
                                    if (service != null) service.procesarLectura(dato); 
                                }
                                sb.setLength(0);
                            } else {
                                sb.append(c);
                            }
                        }
                        Thread.sleep(100);
                    }
                } catch (Exception e) { }
                finally {
                    manejarDesconexion(service);
                }
            });
            hilo.setDaemon(true); 
            hilo.start();
        } else {
            manejarDesconexion(service);
        }
    }

    private void manejarDesconexion(ArduinoPollingService service) {
        if (intentandoReconectar) return;
        intentandoReconectar = true;

        if (puertoSerial != null) puertoSerial.closePort();
        
        // Notificamos al Dashboard de la EPN que el sensor no está
        if (service != null) service.notificarEstadoConexion(false);
        registrarErrorEnLog("Sistema iniciado sin hardware o conexión perdida.");

        // Bucle infinito de búsqueda
        new Thread(() -> {
            while (intentandoReconectar) {
                System.out.println(">>> [BÚSQUEDA] Escaneando puertos USB...");
                String nuevoPuerto = detectarPuertoAutomatico();
                if (nuevoPuerto != null) {
                    conectar(nuevoPuerto, service);
                    break;
                }
                try { Thread.sleep(2000); } catch (Exception e) { }
            }
        }).start();
    }

    private void registrarErrorEnLog(String mensaje) {
        try (FileWriter fw = new FileWriter(AppConfig.getLOGFILE(), true);
             PrintWriter pw = new PrintWriter(fw)) {
            String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            pw.println("[" + fecha + "] [HARDWARE] " + mensaje);
        } catch (Exception e) { }
    }
}