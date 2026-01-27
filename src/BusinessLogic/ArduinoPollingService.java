package BusinessLogic;

import DataAccess.DAOs.PYRALINEDAO;
import DataAccess.DTOs.PYRALINEDTO;
import Infrastructure.AppConfig; 
import Infrastructure.AppException;
import UserInterface.Form.PyralineDashboard;

public class ArduinoPollingService {
    private PYRALINEDAO pyralineDAO;
    private PyralineDashboard dashboard;
    private float umbralAlarma; 
    private boolean alertaActiva = false;

    public ArduinoPollingService(PyralineDashboard dash) throws AppException {
        this.pyralineDAO = new PYRALINEDAO();
        this.dashboard = dash;
        // CARGA INICIAL: Recupera el umbral guardado por Mateo en el archivo .properties
        this.umbralAlarma = AppConfig.getUmbralPersistido(); 
    }

    /**
     * Notifica al Dashboard sobre el estado del cable USB.
     */
    public void notificarEstadoConexion(boolean conectado) {
        if (dashboard != null) {
            dashboard.actualizarEstadoHardware(conectado);
        }
    }

    /**
     * Actualiza el umbral en tiempo real y lo guarda físicamente en disco.
     */
    public void setUmbralAlarma(float nuevoUmbral) {
        this.umbralAlarma = nuevoUmbral;
        AppConfig.guardarUmbral(nuevoUmbral); 
        System.out.println(">>> [SISTEMA] Umbral guardado permanentemente: " + nuevoUmbral + "°C");
    }

    public void procesarLectura(String rawData) {
        try {
            // El dato ya viene limpio de ArduinoSensor
            float tempActual = Float.parseFloat(rawData.trim());
            
            // Comparación contra el umbral gestionado por Mateo Sebastian
            boolean sobreUmbral = tempActual > umbralAlarma; 

            if (dashboard != null) {
                dashboard.actualizarMonitoreo(tempActual, sobreUmbral);

                // --- LÓGICA DE ALERTA VISUAL Y SONORA ---
                if (sobreUmbral && !alertaActiva) {
                    System.out.println(">>> [ALERTA] ¡Temperatura Crítica! Activando sirena...");
                    
                    // Ordenamos al Dashboard activar la sirena y el sonido
                    dashboard.setModoAlerta(true); 
                    
                    registrarEvento(tempActual, 1); 
                    alertaActiva = true;
                } 
                else if (!sobreUmbral && alertaActiva) {
                    System.out.println(">>> [SISTEMA] Regreso a normalidad. Desactivando sirena...");
                    
                    // Ordenamos al Dashboard apagar la sirena
                    dashboard.setModoAlerta(false); 
                    
                    registrarEvento(tempActual, 3); 
                    alertaActiva = false;
                }
            }
        } catch (Exception e) {
            System.err.println("(!) Error al procesar dato térmico: " + e.getMessage());
        }
    }

    private void registrarEvento(float valor, int tipo) {
        try {
            PYRALINEDTO registro = new PYRALINEDTO(1, tipo, valor);
            boolean exito = pyralineDAO.create(registro);
            
            if (exito) {
                System.out.println("(✓) ÉXITO: Registro guardado en el historial de Pyraline.");
                if (dashboard != null) {
                    dashboard.refrescarHistorial(); 
                }
            }
        } catch (AppException e) {
            System.err.println("(!) ERROR DE PERSISTENCIA: " + e.getMessage());
        }
    }
}