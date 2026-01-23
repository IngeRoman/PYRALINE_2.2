package BusinessLogic;

import DataAccess.DAOs.PYRALINEDAO;
import DataAccess.DTOs.PYRALINEDTO;
import Infrastructure.AppException;

public class ArduinoPollingService {
    private PYRALINEDAO pyralineDAO;
    private final float UMBRAL_MAXIMO = 70.0f; // Configura aquí tu límite de alarma
    private final int ID_LUGAR_ACTUAL = 2;     // Ej: Zona de Servidores

    public ArduinoPollingService() throws AppException {
        this.pyralineDAO = new PYRALINEDAO();
    }

    /**
     * Este método debe ser llamado cada vez que llega una línea de texto
     * desde el puerto Serial del Arduino.
     */
    public void procesarLecturaArduino(String rawData) {
        try {
            // 1. Limpiar y convertir el dato
            float temperatura = Float.parseFloat(rawData.trim());

            // 2. MOSTRAR SIEMPRE (Requisito: El usuario debe ver la temperatura actual)
            System.out.println("Monitoreo actual: " + temperatura + "°C");
            // Aquí llamarías a tu interfaz: dashboard.updateTemp(temperatura);

            // 3. LOGICA DE ALARMA (Requisito: Solo guardar si ocurre una alarma)
            if (temperatura >= UMBRAL_MAXIMO) {
                registrarAlarmaEnBaseDatos(temperatura);
            }

        } catch (NumberFormatException e) {
            System.err.println("Error: El Arduino envió un dato no numérico: " + rawData);
        } catch (AppException e) {
            System.err.println("Error de persistencia: " + e.getMessage());
        }
    }

    private void registrarAlarmaEnBaseDatos(float valorTemperatura) throws AppException {
        // IdTipoAlerta 1 = 'Exceso de Temperatura' según nuestro script SQL
        PYRALINEDTO alarma = new PYRALINEDTO(ID_LUGAR_ACTUAL, 1, valorTemperatura);
        
        if (pyralineDAO.create(alarma)) {
            System.out.println("⚠️ [ALERTA] Temperatura crítica guardada en historial.");
        }
    }
}