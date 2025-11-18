package com.example.dinadocs.models;

// Importación correcta para Spring Boot 3+
import jakarta.persistence.*;

/**
 * Entidad JPA (Modelo) que representa una plantilla (Template) en la base de datos.
 * Almacena el contenido HTML/CSS y la lógica de propiedad (pública o privada).
 *
 * @see com.example.dinadocs.services.TemplateService (Servicio que gestiona esta entidad)
 */
@Entity
@Table(name = "plantillas")
public class Template {

    /**
     * Identificador único auto-incremental para la plantilla.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre único de la plantilla (ej. "Factura", "Perfil") 
     * usado por el 'templateType' del GenerationRequest.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Contenido HTML/CSS de la plantilla.
     * Almacenado como texto largo (Lob) en la base de datos.
     */
    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /**
     * Relación con el usuario dueño.
     * Es NULL si la plantilla es pública (creada por un CREADOR o ADMIN).
     * Apunta al User si es una plantilla privada (creada por un USUARIO).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    /**
     * Flag para determinar la visibilidad de la plantilla.
     * true = Pública (visible para todos).
     * false = Privada (visible solo para el 'owner').
     */
    @Column(nullable = false)
    private boolean isPublic;

    /**
     * Lista de placeholders (ej. "titulo", "foto_usuario") que el backend
     * extrajo del 'content' cuando el CREADOR guardó la plantilla.
     * Esto le dice al frontend qué campos de formulario renderizar (RF-06).
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "template_placeholders",
                     joinColumns = @JoinColumn(name = "template_id"))
    @Column(name = "placeholder")
    private java.util.List<String> placeholders;

    // --- Constructores, Getters y Setters ---

    public Template() {}

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public User getOwner() {
        return owner;
    }
    public void setOwner(User owner) {
        this.owner = owner;
    }
    public boolean isPublic() {
        return isPublic;
    }
    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }
    public java.util.List<String> getPlaceholders() {
        return placeholders;
    }
    public void setPlaceholders(java.util.List<String> placeholders) {
        this.placeholders = placeholders;
    }
}