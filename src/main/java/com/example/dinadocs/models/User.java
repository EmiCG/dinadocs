package com.example.dinadocs.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;

/**
 * Entidad JPA que representa a un usuario del sistema.
 * Se identifica principalmente por su correo electrónico.
 * 
 * @author DynaDocs Team
 * @version 1.0
 * @since 2025-12-03
 */
@Entity
@Table(name = "usuarios")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @JsonIgnore
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Template> templates;

    /**
     * Constructor por defecto requerido por JPA.
     */
    public User() {}

    /** @return el ID del usuario */
    public Long getId() {
        return id;
    }
    /** @param id el ID a asignar */
    public void setId(Long id) {
        this.id = id;
    }
    /** @return el nombre del usuario */
    public String getName() {
        return name;
    }
    /** @param name el nombre a asignar */
    public void setName(String name) {
        this.name = name;
    }
    /** @return el email del usuario */
    public String getEmail() {
        return email;
    }
    /** @param email el email a asignar */
    public void setEmail(String email) {
        this.email = email;
    }
    /** @return la contraseña encriptada */
    public String getPassword() {
        return password;
    }
    /** @param password la contraseña a asignar */
    public void setPassword(String password) {
        this.password = password;
    }
    /** @return el rol del usuario */
    public Role getRole() {
        return role;
    }
    /** @param role el rol a asignar */
    public void setRole(Role role) {
        this.role = role;
    }
    /** @return lista de plantillas del usuario */
    public List<Template> getTemplates() {
        return templates;
    }
    /** @param templates lista de plantillas a asignar */
    public void setTemplates(List<Template> templates) {
        this.templates = templates;
    }
}