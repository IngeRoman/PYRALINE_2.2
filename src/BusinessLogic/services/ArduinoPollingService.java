package BusinessLogic.services;

import DataAccess.dao.PYRALINEDAO;
import DataAccess.dto.PYRALINEDTO;
import Infrastructure.config.AppConfig;
import Infrastructure.exeption.AppException;
import UI.form.PyralineDashboard;


/**
 * Servicio encargado de procesar las lecturas térmicas del sensor Arduino.
 * Gestiona la lógica de comparación contra el umbral de seguridad, activa las 
 * alertas en la interfaz de usuario y persiste los eventos en la base de datos.
 * </p>
 * @version 2.2
 */

public class ArduinoPollingService {
    private PYRALINEDAO pyralineDAO;
    private PyralineDashboard dashboard;
    private float umbralAlarma; 
    private boolean alertaActiva = false;



        /**
     * Inicializa el servicio y recupera la configuración de umbral del sistema.
     * @param dash Instancia del Dashboard para control visual y sonoro.
     * @throws AppException Si ocurre un error al inicializar el DAO o la configuración.
     */


    public ArduinoPollingService(PyralineDashboard dash) throws AppException {
 try {
            this.pyralineDAO = new PYRALINEDAO();
            this.dashboard = dash;
            // CARGA INICIAL: Recupera el umbral guardado en el archivo .properties
            this.umbralAlarma = AppConfig.getUmbralPersistido(); 
        } catch (Exception e) {
            // Registra fallos de inicio en el log técnico de Pyraline
            throw new AppException("Error al inicializar los componentes del servicio.", 
                                  e, getClass(), "Constructor");
        }
    }


/**
     * Notifica al Dashboard sobre el estado del hardware (cable USB/Serial).
     * @param conectado true si existe comunicación activa.
     */

    public void notificarEstadoConexion(boolean conectado) {
        if (dashboard != null) {
            dashboard.actualizarEstadoHardware(conectado);
        }
    }

/**
     * Actualiza el umbral en tiempo real y asegura su persistencia física en disco.
     * @param nuevoUmbral Valor límite de temperatura en Celsius.
     */
    public void setUmbralAlarma(float nuevoUmbral) {
        try {
            this.umbralAlarma = nuevoUmbral;
            AppConfig.guardarUmbral(nuevoUmbral); 
            System.out.println(">>> [SISTEMA] Umbral guardado permanentemente: " + nuevoUmbral + "°C");
        } catch (Exception e) {
            // Captura errores de escritura en el archivo app.properties
            new AppException("No se pudo guardar el umbral físicamente.", e, getClass(), "setUmbralAlarma");
        }
    }

      /**
     * Procesa la cadena de texto cruda proveniente del sensor.
     * <p>
     * Convierte la lectura, evalúa el umbral y activa/desactiva el modo de alerta
     * en el Dashboard según sea necesario.
     * </p>
     * @param rawData Texto plano recibido del sensor (ej. "28.4").
     */
    

    public void procesarLectura(String rawData) {
        try {
            if (rawData == null || rawData.isBlank()) return;
            // El dato ya viene limpio de ArduinoSensor
            float tempActual = Float.parseFloat(rawData.trim());
            
            // Comparación contra el umbral gestionado
            boolean sobreUmbral = tempActual > umbralAlarma; 

            if (dashboard != null) {
                // Actualiza visuales de temperatura y estado en el monitor
                dashboard.actualizarMonitoreo(tempActual, sobreUmbral);

                // --- LÓGICA DE ALERTA VISUAL Y SONORA ---
                if (sobreUmbral && !alertaActiva) {
                    System.out.println(">>> [ALERTA] ¡Temperatura Crítica! Activando sirena...");
                    
                    // Ordenamos al Dashboard activar la sirena y el sonido
                    dashboard.setModoAlerta(true); 
                    
                    registrarEvento(tempActual, 1); // 1: Inicio de Alerta
                    alertaActiva = true;
                } 
                else if (!sobreUmbral && alertaActiva) {
                    System.out.println(">>> [SISTEMA] Regreso a normalidad. Desactivando sirena...");
                    
                    // Ordenamos al Dashboard apagar la sirena
                    dashboard.setModoAlerta(false); 
                    
                    registrarEvento(tempActual, 3); // 3: Normalización
                    alertaActiva = false;
                }
            }

            } catch (NumberFormatException e) {
            // Registra si el sensor envía datos corruptos o no numéricos
            new AppException("Formato de lectura inválido: " + rawData, e, getClass(), "procesarLectura");

        } catch (Exception e) {
            System.err.println("(!) Error al procesar dato térmico: " + e.getMessage());
             // Captura cualquier otro fallo inesperado en la lógica del servicio
            new AppException("Error al procesar la señal térmica.", e, getClass(), "procesarLectura");
        }
    }

     /**
     * Registra el evento térmico en el historial de la base de datos SQLite.
     * @param valor Magnitud de la temperatura al momento del evento.
     * @param tipo Identificador del tipo de evento (Alerta/Normalización).
     */

    private void registrarEvento(float valor, int tipo) {
        try {
            // Instancia del DTO con los parámetros de Pyraline (Lugar, Tipo, Valor)
            PYRALINEDTO registro = new PYRALINEDTO(1, tipo, valor);
            boolean exito = pyralineDAO.create(registro);
            
            if (exito) {
                System.out.println("(✓) ÉXITO: Registro guardado en el historial de Pyraline.");
                if (dashboard != null) {
            // Refresca la tabla histórica en la interfaz gráfica
                    dashboard.refrescarHistorial(); 
                }
            }
        } catch (AppException e) {
             // El error técnico ya fue logueado en la capa de datos; informamos fallo local
            System.err.println("(!) Error de persistencia: El evento no se guardó en la DB.");
        } catch (Exception e) {
            // Captura errores inesperados de instanciación o lógica
            new AppException("Fallo crítico al intentar registrar el evento térmico.", 
                             e, getClass(), "registrarEvento");
        }
    }
}