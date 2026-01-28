package DataAccess.dto;

public class LugarDTO {
    private Integer IdLugar;
    private String Nombre;
    private String Descripcion;
    private String Estado;

    public LugarDTO() {}

    public Integer getIdLugar() { return IdLugar; }
    public void setIdLugar(Integer idLugar) { this.IdLugar = idLugar; }
    public String getNombre() { return Nombre; }
    public void setNombre(String nombre) { this.Nombre = nombre; }
    public String getDescripcion() { return Descripcion; }
    public void setDescripcion(String descripcion) { this.Descripcion = descripcion; }
    public String getEstado() { return Estado; }
    public void setEstado(String estado) { this.Estado = estado; }

    @Override
    public String toString() {
        return String.format("Lugar [ID: %d | Nombre: %s | Estado: %s]", IdLugar, Nombre, Estado);
    }
}