package com.example.dinadocs.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;

/**
 * Entidad JPA que representa una plantilla de documento HTML.
 * Las plantillas pueden ser públicas o privadas, y contienen placeholders
 * que serán reemplazados con datos dinámicos al generar PDFs.
 * 
 * <p>Estructura de la tabla en base de datos:
 * <ul>
 *   <li>Tabla: plantillas</li>
 *   <li>Relación ManyToOne con User (owner)</li>
 *   <li>Colección de placeholders en tabla auxiliar template_placeholders</li>
 * </ul>
 * 
 * @author DynaDocs Team
 * @version 1.0
 * @since 2025-12-03
 * @see User
 * @see com.example.dinadocs.services.TemplateService
 */
@Entity
@Table(name = "plantillas")
public class Template {

    /**
     * Identificador único de la plantilla (clave primaria).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre descriptivo de la plantilla.
     * No puede ser nulo.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Contenido HTML de la plantilla con placeholders Mustache.
     * Se almacena como TEXT para permitir contenido extenso.
     */
    @Column(columnDefinition = "TEXT")
    private String content;

    /**
     * Usuario propietario de la plantilla.
     * Relación ManyToOne con carga LAZY.
     * Se ignora en la serialización JSON para evitar recursión infinita.
     */
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    /**
     * Indica si la plantilla es pública (accesible para todos)
     * o privada (solo para el propietario).
     * No puede ser nulo.
     */
    @Column(nullable = false)
    private boolean isPublic;

    /**
     * Lista de identificadores (placeholders) que la plantilla requiere
     * para ser completada. Ejemplos: "nombre_cliente", "fecha", "total".
     * 
     * <p>Los placeholders se extraen automáticamente del contenido HTML
     * usando expresiones regulares que buscan patrones {{placeholder}}.
     * Se almacenan en una tabla auxiliar (template_placeholders).
     * 
     * @see com.example.dinadocs.services.TemplateService#extractPlaceholders(String)
     */
    @ElementCollection
    @CollectionTable(name = "template_placeholders", joinColumns = @JoinColumn(name = "template_id"))
    @Column(name = "placeholder")
    private List<String> placeholders;

    /**
     * Constructor por defecto requerido por JPA.
     */
    public Template() {

    }

    /**
     * Obtiene el ID de la plantilla.
     * @return el identificador único
     */
    public Long getId() {
        return id;
    }
    
    /**
     * Establece el ID de la plantilla.
     * @param id el identificador único a asignar
     */
    public void setId(Long id) {
        this.id = id;
    }
    
    /**
     * Obtiene el nombre de la plantilla.
     * @return el nombre descriptivo
     */
    public String getName() {
        return name;
    }
    
    /**
     * Establece el nombre de la plantilla.
     * @param name el nombre descriptivo a asignar
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Obtiene el contenido HTML de la plantilla.
     * @return el contenido HTML con placeholders
     */
    public String getContent() {
        return content;
    }
    
    /**
     * Establece el contenido HTML de la plantilla.
     * @param content el contenido HTML a asignar
     */
    public void setContent(String content) {
        this.content = content;
    }
    
    /**
     * Obtiene el usuario propietario de la plantilla.
     * @return el usuario owner
     */
    public User getOwner() {
        return owner;
    }
    
    /**
     * Establece el usuario propietario de la plantilla.
     * @param owner el usuario a asignar como propietario
     */
    public void setOwner(User owner) {
        this.owner = owner;
    }
    
    /**
     * Verifica si la plantilla es pública.
     * @return true si es pública, false si es privada
     */
    public boolean isPublic() {
        return isPublic;
    }
    
    /**
     * Establece la visibilidad de la plantilla.
     * @param isPublic true para pública, false para privada
     */
    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }
    
    /**
     * Obtiene la lista de placeholders requeridos.
     * @return lista de nombres de placeholders
     */
    public List<String> getPlaceholders() {
        return placeholders;
    }
    
    /**
     * Establece la lista de placeholders requeridos.
     * @param placeholders lista de nombres de placeholders a asignar
     */
    public void setPlaceholders(List<String> placeholders) {
        this.placeholders = placeholders;
    }
}