package DataAccess.dto;

/**
 * DTO para el registro de Alarmas PYRALINE.
 * Optimizado para transporte de datos desde la DB y visualización en UI.
 */
public class PYRALINEDTO {
    private Integer IdPYRALINE;
    private Integer IdLugar;
    private Integer IdTipoAlerta;
    private Float   Temperatura;
    private String  Estado;         // 'A' para Activo, 'X' para Borrado Lógico
    private String  FechaHora;
    private String  FechaModifica;

    // Campos adicionales para mostrar nombres en la interfaz (provenientes de la VIEW)
    private String  LugarNombre;
    private String  TipoAlertaNombre;

    // Constructor vacío
    public PYRALINEDTO() {}

    // Constructor para insertar una nueva Alarma (Arduino -> DB)
    public PYRALINEDTO(Integer idLugar, Integer idTipoAlerta, Float temperatura) {
        this.IdLugar = idLugar;
        this.IdTipoAlerta = idTipoAlerta;
        this.Temperatura = temperatura;
        this.Estado = "A";
    }

    // Constructor completo (usado para cargar historial desde la VIEW)
    public PYRALINEDTO(Integer idPYRALINE, String lugarNombre, String tipoAlertaNombre, 
                       Float temperatura, String estado, String fechaHora, String fechaModifica) {
        this.IdPYRALINE = idPYRALINE;
        this.LugarNombre = lugarNombre;
        this.TipoAlertaNombre = tipoAlertaNombre;
        this.Temperatura = temperatura;
        this.Estado = estado;
        this.FechaHora = fechaHora;
        this.FechaModifica = fechaModifica;
    }

    // --- Getters y Setters ---
    public Integer getIdPYRALINE() { return IdPYRALINE; }
    public void setIdPYRALINE(Integer idPYRALINE) { this.IdPYRALINE = idPYRALINE; }
    
    public Integer getIdLugar() { return IdLugar; }
    public void setIdLugar(Integer idLugar) { this.IdLugar = idLugar; }
    
    public Integer getIdTipoAlerta() { return IdTipoAlerta; }
    public void setIdTipoAlerta(Integer idTipoAlerta) { this.IdTipoAlerta = idTipoAlerta; }
    
    public Float getTemperatura() { return Temperatura; }
    public void setTemperatura(Float temperatura) { this.Temperatura = temperatura; }
    
    public String getEstado() { return Estado; }
    public void setEstado(String estado) { this.Estado = estado; }
    
    public String getFechaHora() { return FechaHora; }
    public void setFechaHora(String fechaHora) { this.FechaHora = fechaHora; }
    
    public String getFechaModifica() { return FechaModifica; }
    public void setFechaModifica(String fechaModifica) { this.FechaModifica = fechaModifica; }

    public String getLugarNombre() { return LugarNombre; }
    public void setLugarNombre(String lugarNombre) { this.LugarNombre = lugarNombre; }

    public String getTipoAlertaNombre() { return TipoAlertaNombre; }
    public void setTipoAlertaNombre(String tipoAlertaNombre) { this.TipoAlertaNombre = tipoAlertaNombre; }

    @Override
    public String toString() {
        // Mejorado para usar nombres si están disponibles, si no, usar IDs
        String lugar = (LugarNombre != null) ? LugarNombre : IdLugar.toString();
        return String.format("| %-4d | Temp: %-6.2f | Lugar: %-15s | Hora: %-19s |", 
            IdPYRALINE, Temperatura, lugar, FechaHora);
    }
}