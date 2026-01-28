package DataAccess.dto;

public class UsuarioDTO {
    private Integer IdUsuario;
    private String  Email;
    private String  Password;
    private String  Estado;

    public UsuarioDTO() {}
    public UsuarioDTO(String email, String password) {
        this.Email = email;
        this.Password = password;
    }

    public Integer getIdUsuario() { return IdUsuario; }
    public void setIdUsuario(Integer idUsuario) { IdUsuario = idUsuario; }
    public String getEmail() { return Email; }
    public void setEmail(String email) { Email = email; }
    public String getPassword() { return Password; }
    public void setPassword(String password) { Password = password; }
    public String getEstado() { return Estado; }
    public void setEstado(String estado) { Estado = estado; }
}