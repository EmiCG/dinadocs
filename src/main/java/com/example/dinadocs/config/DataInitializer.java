package com.example.dinadocs.config;

import com.example.dinadocs.models.Template;
import com.example.dinadocs.repositories.TemplateRepository;
import com.example.dinadocs.models.User;
import com.example.dinadocs.repositories.UserRepository;
import com.example.dinadocs.models.Role;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * "Seeder" de la Base de Datos.
 * Esta clase se ejecuta automáticamente al iniciar Spring Boot y
 * se encarga de poblar la base de datos con datos de prueba iniciales
 * (como las plantillas públicas).
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final TemplateRepository templateRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructor para inyección de dependencias.
     * 
     * @param templateRepository repositorio de plantillas
     * @param userRepository repositorio de usuarios
     * @param passwordEncoder encoder de contraseñas BCrypt
     */
    public DataInitializer(TemplateRepository templateRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.templateRepository = templateRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Lee el contenido de un archivo HTML desde resources/templates/
     */
    private String loadTemplateFromFile(String filename) throws IOException {
        ClassPathResource resource = new ClassPathResource("templates/" + filename);
        return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }

    /**
     * Método de ayuda para crear una plantilla desde archivo, solo si no existe.
     */
    private void createTemplateFromFile(String name, String filename) {
        if (templateRepository.findByName(name).isEmpty()) {
            try {
                String content = loadTemplateFromFile(filename);
                Template newTemplate = new Template();
                newTemplate.setName(name);
                newTemplate.setContent(content);
                newTemplate.setPublic(true);

                // Asignar el propietario al creador por defecto con ID 2
                User owner = userRepository.findById(2L).orElseThrow(() -> new RuntimeException("Usuario creador no encontrado"));
                newTemplate.setOwner(owner);

                List<String> placeholders = extractPlaceholders(content);
                newTemplate.setPlaceholders(placeholders);

                templateRepository.save(newTemplate);
                System.out.println("SEEDER: Creada plantilla '" + name + "' desde archivo '" + filename + "'");
            } catch (IOException e) {
                System.err.println("ERROR: No se pudo cargar la plantilla '" + filename + "': " + e.getMessage());
            }
        }
    }

    /**
     * Método privado que usa Regex para encontrar todos los placeholders.
     */
    private List<String> extractPlaceholders(String htmlContent) {
        List<String> matches = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\{\\{([^\\}]+)\\}\\}");
        Matcher matcher = pattern.matcher(htmlContent);

        while (matcher.find()) {
            matches.add(matcher.group(1));
        }
        return matches;
    }

    /**
     * Método para crear usuarios de prueba si no existen.
     */
    private void createDefaultUsers() {
        if (userRepository.findByEmail("admin@gmail.com").isEmpty()) {
            User admin = new User();
            admin.setName("Administrador");
            admin.setEmail("admin@gmail.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
        }

        if (userRepository.findByEmail("creator@gmail.com").isEmpty()) {
            User creator = new User();
            creator.setName("Creador");
            creator.setEmail("creator@gmail.com");
            creator.setPassword(passwordEncoder.encode("creator123"));
            creator.setRole(Role.CREADOR);
            userRepository.save(creator);
        }

        
        if (userRepository.findByEmail("creator2@gmail.com").isEmpty()) {
            User creator = new User();
            creator.setName("Creador");
            creator.setEmail("creator@gmail.com");
            creator.setPassword(passwordEncoder.encode("creator123"));
            creator.setRole(Role.CREADOR);
            userRepository.save(creator);
        }

        
        if (userRepository.findByEmail("creator3@gmail.com").isEmpty()) {
            User creator = new User();
            creator.setName("Creador");
            creator.setEmail("creator@gmail.com");
            creator.setPassword(passwordEncoder.encode("creator123"));
            creator.setRole(Role.CREADOR);
            userRepository.save(creator);
        }

        if (userRepository.findByEmail("user@gmail.com").isEmpty()) {
            User user = new User();
            user.setName("Usuario");
            user.setEmail("user@gmail.com");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setRole(Role.USUARIO);
            userRepository.save(user);
        }
    }

    @Override
    public void run(String... args) throws Exception {
        createDefaultUsers();

        // Cargar plantillas desde archivos HTML en resources/templates/
        createTemplateFromFile("Factura con Conceptos", "factura-con-conceptos.html");
        createTemplateFromFile("Presupuesto de Obra", "presupuesto-obra.html");
        createTemplateFromFile("Factura Moderna", "factura-moderna.html");
        createTemplateFromFile("Portada de Proyecto", "portada-proyecto.html");
        createTemplateFromFile("Curriculum Vitae", "curriculum-vitae.html");
        createTemplateFromFile("Carta de Recomendación", "carta-recomendacion.html");
        createTemplateFromFile("Contrato de Servicios", "contrato-de-servicios-simple.html");
        createTemplateFromFile("Certificado de curso", "certificado.html");
        createTemplateFromFile("Orden de Compra", "orden-de-compra.html");
        createTemplateFromFile("Orden de servicio tecnico"," orden-de-servicio.html");
    }
    
}