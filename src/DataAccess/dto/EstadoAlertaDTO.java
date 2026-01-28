package DataAccess.dto;

/**
 * Entidad que transporta los datos de la tabla 'EstadoAlerta'.
 * Actúa como contenedor (DTO) para el intercambio de información entre capas.
 */
public class EstadoAlertaDTO {
    private Integer IdEstadoAlerta;
    private String  Nombre;
    private String  Descripcion;
    private String  FechaCreacion;
    private String  FechaModifica;

    /** Constructor por defecto necesario para la instanciación mediante reflexión. */
    public EstadoAlertaDTO() {}

    /** Constructor completo para inicialización manual de la entidad. */
    public EstadoAlertaDTO(Integer idEstadoAlerta, String nombre, String descripcion, 
                           String fechaCreacion, String fechaModifica) {
        this.IdEstadoAlerta = idEstadoAlerta;
        this.Nombre = nombre;
        this.Descripcion = descripcion;
        this.FechaCreacion = fechaCreacion;
        this.FechaModifica = fechaModifica;
    }

    // --- Métodos de Acceso (Getters y Setters) ---

    public Integer getIdEstadoAlerta() { return IdEstadoAlerta; }
    public void setIdEstadoAlerta(Integer idEstadoAlerta) { this.IdEstadoAlerta = idEstadoAlerta; }

    public String getNombre() { return Nombre; }
    public void setNombre(String nombre) { this.Nombre = nombre; }

    public String getDescripcion() { return Descripcion; }
    public void setDescripcion(String descripcion) { this.Descripcion = descripcion; }

    public String getFechaCreacion() { return FechaCreacion; }
    public void setFechaCreacion(String fechaCreacion) { this.FechaCreacion = fechaCreacion; }

    public String getFechaModifica() { return FechaModifica; }
    public void setFechaModifica(String fechaModifica) { this.FechaModifica = fechaModifica; }

    /** @return Representación textual de la entidad para propósitos de depuración. */
    @Override
    public String toString() {
        return "EstadoAlertaDTO [ID=" + IdEstadoAlerta + 
               ", Nombre=" + Nombre + 
               ", Descripcion=" + Descripcion + "]";
    }
}