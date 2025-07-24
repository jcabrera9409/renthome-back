package org.renthome.dto;

public class AuthResponse {

    private String token;
    private String type = "Bearer";
    private String email;
    private String nombre;
    private Long expiresIn;

    public AuthResponse() {}

    public AuthResponse(String token, String email, String nombre, Long expiresIn) {
        this.token = token;
        this.email = email;
        this.nombre = nombre;
        this.expiresIn = expiresIn;
    }

    // Getters y Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    @Override
    public String toString() {
        return "AuthResponse{" +
                "type='" + type + '\'' +
                ", email='" + email + '\'' +
                ", nombre='" + nombre + '\'' +
                ", expiresIn=" + expiresIn +
                '}';
    }
}
