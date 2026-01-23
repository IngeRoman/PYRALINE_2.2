package DataAccess.DTOs;

public class TipoAlertaDTO {

    private Integer IdTipoAlerta; // Corregido de String a Integer
    private String Nombre;
    private String Descripcion;
    private String FechaCreacion;
    private String FechaModifica;

    public TipoAlertaDTO() {}

    public TipoAlertaDTO(Integer idTipoAlerta, String nombre, String descripcion, String fechaCreacion, String fechaModifica) {
        IdTipoAlerta = idTipoAlerta;
        Nombre = nombre;
        Descripcion = descripcion;
        FechaCreacion = fechaCreacion;
        FechaModifica = fechaModifica;
    }

    public Integer getIdTipoAlerta() { return IdTipoAlerta; }
    public void setIdTipoAlerta(Integer idTipoAlerta) { IdTipoAlerta = idTipoAlerta; }

    public String getNombre() { return Nombre; }
    public void setNombre(String nombre) { Nombre = nombre; }

    public String getDescripcion() { return Descripcion; }
    public void setDescripcion(String descripcion) { Descripcion = descripcion; }

    public String getFechaCreacion() { return FechaCreacion; }
    public void setFechaCreacion(String fechaCreacion) { FechaCreacion = fechaCreacion; }

    public String getFechaModifica() { return FechaModifica; }
    public void setFechaModifica(String fechaModifica) { FechaModifica = fechaModifica; }

    @Override
    public String toString() {
        return String.format("TipoAlerta [ID: %d | Nombre: %-10s | Desc: %s]", 
            IdTipoAlerta, Nombre, Descripcion);
    }
}