package BusinessLogic;

import DataAccess.DAOs.PYRALINEDAO;
import DataAccess.DTOs.PYRALINEDTO;
import Infrastructure.AppConfig; // Importación necesaria para la persistencia
import Infrastructure.AppException;
import UserInterface.Form.PyralineDashboard;

public class ArduinoPollingService {
    private PYRALINEDAO pyralineDAO;
    private PyralineDashboard dashboard;
    private float umbralAlarma; // Variable dinámica
    private boolean alertaActiva = false;

    public ArduinoPollingService(PyralineDashboard dash) throws AppException {
        this.pyralineDAO = new PYRALINEDAO();
        this.dashboard = dash;
        // CARGA INICIAL: Lee el valor guardado en el archivo .properties
        this.umbralAlarma = AppConfig.getUmbralPersistido(); 
    }

    /**
     * Actualiza el umbral en tiempo real y lo guarda permanentemente en disco.
     */
    public void setUmbralAlarma(float nuevoUmbral) {
        this.umbralAlarma = nuevoUmbral;
        // PERSISTENCIA: Se graba en el archivo para que no se pierda al reiniciar
        AppConfig.guardarUmbral(nuevoUmbral); 
        System.out.println(">>> [SISTEMA] Umbral guardado permanentemente: " + nuevoUmbral + "°C");
    }

    public void procesarLectura(String rawData) {
        try {
            float tempActual = Float.parseFloat(rawData.trim());
            // Comparamos contra la variable dinámica cargada desde memoria
            boolean sobreUmbral = tempActual > umbralAlarma; 

            if (dashboard != null) {
                dashboard.actualizarMonitoreo(tempActual, sobreUmbral);
            }

            if (sobreUmbral && !alertaActiva) {
                System.out.println(">>> Registrando ALERTA (ID 1)...");
                registrarEvento(tempActual, 1); 
                alertaActiva = true;
            } 
            else if (!sobreUmbral && alertaActiva) {
                System.out.println(">>> Registrando NORMALIDAD (ID 3)...");
                registrarEvento(tempActual, 3); 
                alertaActiva = false;
            }
        } catch (Exception e) {
            System.err.println("(!) Error: " + e.getMessage());
        }
    }

    private void registrarEvento(float valor, int tipo) {
        try {
            PYRALINEDTO registro = new PYRALINEDTO(1, tipo, valor);
            boolean exito = pyralineDAO.create(registro);
            
            if (exito) {
                System.out.println("(✓) GUARDADO REAL EN DB: Tipo " + tipo);
                if (dashboard != null) {
                    dashboard.refrescarHistorial(); 
                }
            }
        } catch (AppException e) {
            System.err.println("(!) ERROR DE SQLITE: " + e.getMessage());
        }
    }
}