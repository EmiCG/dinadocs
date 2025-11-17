package com.example.dinadocs.models;

// Importación correcta para Spring Boot 3+
import jakarta.persistence.*;
import java.util.List;

/**
 * Entidad JPA (Modelo) que representa la tabla 'usuarios'.
 * Basado en la sección 1.2 del "Contrato de API y especificación.md".
 */
@Entity
@Table(name = "usuarios")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Template> templates;

    // --- Constructores, Getters y Setters ---

    public User() {}

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public Role getRole() {
        return role;
    }
    public void setRole(Role role) {
        this.role = role;
    }
    public List<Template> getTemplates() {
        return templates;
    }
    public void setTemplates(List<Template> templates) {
        this.templates = templates;
    }
}