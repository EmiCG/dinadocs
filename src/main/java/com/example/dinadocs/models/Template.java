package com.example.dinadocs.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;

/**
 * Entidad JPA que define la estructura de una plantilla de documento.
 * Incluye la lista de marcadores de posición necesarios para el llenado.
 *
 */
@Entity
@Table(name = "plantillas")
public class Template {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String content;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @Column(nullable = false)
    private boolean isPublic;

    /**
     * Lista de identificadores (placeholders) que la plantilla requiere
     * para ser completada (ej. "nombre_cliente", "fecha").
     *
     */
    @ElementCollection
    @CollectionTable(name = "template_placeholders", joinColumns = @JoinColumn(name = "template_id"))
    @Column(name = "placeholder")
    private List<String> placeholders;

    public Template() {

    }

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
    public List<String> getPlaceholders() {
        return placeholders;
    }
    public void setPlaceholders(List<String> placeholders) {
        this.placeholders = placeholders;
    }
}