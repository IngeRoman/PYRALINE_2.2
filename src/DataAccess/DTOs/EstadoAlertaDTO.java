package DataAccess.DTOs;

public class EstadoAlertaDTO {
private Integer IdEstadoAlerta;
private String  Nombre;
private String  Descripcion;
private String  FechaCreacion;
private String  FechaModifica;

public EstadoAlertaDTO() {}
public EstadoAlertaDTO(Integer idEstadoAlerta, String nombre, String descripcion, String fechaCreacion,
        String fechaModifica) {
    IdEstadoAlerta = idEstadoAlerta;
    Nombre = nombre;
    Descripcion = descripcion;
    FechaCreacion = fechaCreacion;
    FechaModifica = fechaModifica;
}



public Integer getIdEstadoAlerta() {
    return IdEstadoAlerta;
}

public void setIdEstadoAlerta(Integer idEstadoAlerta) {
    IdEstadoAlerta = idEstadoAlerta;
}

public String getNombre() {
    return Nombre;
}

public void setNombre(String nombre) {
    Nombre = nombre;
}

public String getDescripcion() {
    return Descripcion;
}

public void setDescripcion(String descripcion) {
    Descripcion = descripcion;
}

public String getFechaCreacion() {
    return FechaCreacion;
}

public void setFechaCreacion(String fechaCreacion) {
    FechaCreacion = fechaCreacion;
}

public String getFechaModifica() {
    return FechaModifica;
}

public void setFechaModifica(String fechaModifica) {
    FechaModifica = fechaModifica;
}

@Override
public String toString() {
    return "EstadoAlertaDTO [IdEstadoAlerta=" + getIdEstadoAlerta()
                             + ", Nombre=" + getNombre() + 
                            ", Descripcion="+ getDescripcion() +
                             ", FechaCreacion=" + getFechaCreacion() +
                              ", FechaModifica=" + getFechaModifica() + "]";



    

}
}