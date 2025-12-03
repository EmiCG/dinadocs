package com.example.dinadocs.config;

import com.example.dinadocs.models.Template;
import com.example.dinadocs.repositories.TemplateRepository;
import com.example.dinadocs.models.User;
import com.example.dinadocs.repositories.UserRepository;
import com.example.dinadocs.models.Role;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

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

    public DataInitializer(TemplateRepository templateRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.templateRepository = templateRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Método de ayuda para crear una plantilla, solo si no existe.
     */
    private void createTemplateIfNotFound(String name, String content) {
        if (templateRepository.findByName(name).isEmpty()) {
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
            System.out.println("SEEDER: Creada plantilla '" + name + "'");
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

        createTemplateIfNotFound(
            "Factura",
            "<html>"
           + "<body>"
           + "<h1>Factura Nro: {{numero_factura}}</h1>"
           + "<p>Cliente: <strong>{{nombre_cliente}}</strong></p>"
           + "<p>Monto Total: <strong>${{monto_total}}</strong></p>"
           + "</body>"
           + "</html>"
        );

        createTemplateIfNotFound(
            "Perfil",
            "<html>"
           + "<head>"
           + "<style>"
           + ".profile-pic { width: 100px; height: 100px; border-radius: 50%; object-fit: cover; }"
           + "</style>"
           + "</head>"
           + "<body>"
           + "<h1>Perfil de Usuario</h1>"
           + "<img src='{{foto_usuario}}' class='profile-pic' />"
           + "<h2>{{nombre_usuario}}</h2>"
           + "</body>"
           + "</html>"
        );

        String presupuestoCompuesto = """
                <html>
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            margin: 40px;
                            font-size: 14px;
                            color: #333;
                        }
                        .header-container {
                            display: flex;
                            justify-content: space-between;
                            align-items: flex-start;
                            border-bottom: 3px solid #0056b3; /* Color azul corporativo */
                            padding-bottom: 20px;
                        }
                        /* ESTILOS PARA EL LOGO DEL USUARIO */
                        .user-logo {
                            width: 150px;       /* Ancho fijo para el logo */
                            max-height: 100px;  /* Altura máxima */
                            object-fit: contain; /* Asegura que la imagen quepa sin deformarse */
                        }
                        .invoice-details {
                            text-align: right;
                            font-size: 12px;
                            color: #555;
                        }
                        .invoice-details h1 {
                            margin: 0;
                            color: #0056b3;
                            font-size: 32px;
                        }
                        .client-info {
                            margin-top: 30px;
                            padding: 15px;
                            background-color: #f9f9f9;
                            border-radius: 5px;
                        }
                        .client-info strong {
                            display: block;
                            margin-bottom: 5px;
                            color: #000;
                        }
                        .items-table {
                            width: 100%;
                            border-collapse: collapse;
                            margin-top: 30px;
                        }
                        .items-table th, .items-table td {
                            border: 1px solid #ddd;
                            padding: 12px;
                        }
                        .items-table th {
                            background-color: #0056b3;
                            color: white;
                            text-align: left;
                        }
                        .items-table .amount {
                            text-align: right;
                        }
                        .totals-container {
                            width: 350px;
                            margin-left: auto;
                            margin-top: 20px;
                        }
                        .totals-table {
                            width: 100%;
                        }
                        .totals-table td {
                            padding: 10px;
                        }
                        .totals-table .label {
                            text-align: right;
                            font-weight: bold;
                        }
                        .totals-table .value {
                            text-align: right;
                            width: 130px;
                        }
                        .totals-table .grand-total .label {
                            font-size: 20px;
                        }
                        .totals-table .grand-total .value {
                            font-size: 20px;
                            font-weight: bold;
                            border-top: 3px solid #0056b3;
                        }
                    </style>
                </head>
                <body>
                    <div class="header-container">
                        <div>
                            <img src="{{logo_empresa}}" class="user-logo" alt="Logo de Empresa">
                        </div>
                        <div class="invoice-details">
                            <h1>PRESUPUESTO</h1>
                            <strong>Fecha:</strong> {{fecha_presupuesto}}<br>
                            <strong>Presupuesto #:</strong> {{numero_presupuesto}}
                        </div>
                    </div>
                    <div class="client-info">
                        <strong>Presupuesto Para:</strong>
                        {{nombre_cliente}}<br>
                        {{direccion_cliente}}<br>
                        {{email_cliente}}
                    </div>
                    <table class="items-table">
                        <thead>
                            <tr>
                                <th>Descripción</th>
                                <th class="amount">Total</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td>{{descripcion_item_1}}</td>
                                <td class="amount">$ {{monto_item_1}}</td>
                            </tr>
                            <tr>
                                <td>{{descripcion_item_2}}</td>
                                <td class="amount">$ {{monto_item_2}}</td>
                            </tr>
                        </tbody>
                    </table>
                    <div class="totals-container">
                        <table class="totals-table">
                            <tr>
                                <td class="label">Subtotal:</td>
                                <td class="value">$ {{subtotal}}</td>
                            </tr>
                            <tr>
                                <td class="label">IVA (16%):</td>
                                <td class="value">$ {{iva}}</td>
                            </tr>
                            <tr class="grand-total">
                                <td class="label">TOTAL:</td>
                                <td class="value">$ {{total}}</td>
                            </tr>
                        </table>
                    </div>
                </body>
                </html>
                """;

        createTemplateIfNotFound("PresupuestoCompuesto", presupuestoCompuesto);

        createTemplateIfNotFound(
            "FacturaDinamica",
            "<!DOCTYPE html>\n"
           + "<html>\n"
           + "<head>\n"
           + "    <title>Factura</title>\n"
           + "</head>\n"
           + "<body>\n"
           + "    <h1>Factura</h1>\n"
           + "    <p>Cliente: {{cliente}}</p>\n"
           + "    <p>Fecha: {{fecha}}</p>\n"
           + "    <p>Productos:</p>\n"
           + "    <ul>\n"
           + "        {{#productos}}\n"
           + "        <li>{{nombre}} - {{precio}} USD</li>\n"
           + "        {{/productos}}\n"
           + "    </ul>\n"
           + "    <p>Total: {{total}} USD</p>\n"
           + "</body>\n"
           + "</html>"
        );

        createTemplateIfNotFound(
            "Curriculum Vitae",
            "<html>\n"
           + "<head>\n"
           + "    <title>Curriculum Vitae</title>\n"
           + "    <style>\n"
           + "        body { font-family: Arial, sans-serif; margin: 20px; }\n"
           + "        h1 { color: #0056b3; }\n"
           + "        p { margin: 5px 0; }\n"
           + "        ul { padding-left: 20px; }\n"
           + "        li { margin-bottom: 5px; }\n"
           + "    </style>\n"
           + "</head>\n"
           + "<body>\n"
           + "    <h1>Curriculum Vitae</h1>\n"
           + "    <p><strong>Nombre:</strong> {{nombre}}</p>\n"
           + "    <p><strong>Fecha de Nacimiento:</strong> {{fecha_nacimiento}}</p>\n"
           + "    <p><strong>Dirección:</strong> {{direccion}}</p>\n"
           + "    <p><strong>Teléfono:</strong> {{telefono}}</p>\n"
           + "    <p><strong>Email:</strong> {{email}}</p>\n"
           + "    <h2>Experiencia Laboral</h2>\n"
           + "    <ul>\n"
           + "        {{#experiencia}}\n"
           + "        <li>{{puesto}} en {{empresa}} ({{fecha_inicio}} - {{fecha_fin}})</li>\n"
           + "        {{/experiencia}}\n"
           + "    </ul>\n"
           + "    <h2>Educación</h2>\n"
           + "    <ul>\n"
           + "        {{#educacion}}\n"
           + "        <li>{{grado}} en {{institucion}} ({{fecha_inicio}} - {{fecha_fin}})</li>\n"
           + "        {{/educacion}}\n"
           + "    </ul>\n"
           + "</body>\n"
           + "</html>"
        );

        createTemplateIfNotFound(
            "Resumen de Vida",
            "<html>\n"
           + "<head>\n"
           + "    <title>Resumen de Vida</title>\n"
           + "</head>\n"
           + "<body>\n"
           + "    <h1>Resumen de Vida</h1>\n"
           + "    <p>Nombre: {{nombre}}</p>\n"
           + "    <p>Edad: {{edad}}</p>\n"
           + "    <p>Profesión: {{profesion}}</p>\n"
           + "    <p>Habilidades: {{habilidades}}</p>\n"
           + "    <p>Intereses: {{intereses}}</p>\n"
           + "</body>\n"
           + "</html>"
        );

        createTemplateIfNotFound(
            "Perfil",
            "<html>\n"
           + "<head>\n"
           + "    <title>Perfil</title>\n"
           + "</head>\n"
           + "<body>\n"
           + "    <h1>Perfil</h1>\n"
           + "    <p>Nombre: {{nombre}}</p>\n"
           + "    <p>Edad: {{edad}}</p>\n"
           + "    <p>Correo Electrónico: {{email}}</p>\n"
           + "    <p>Teléfono: {{telefono}}</p>\n"
           + "    <p>Dirección: {{direccion}}</p>\n"
           + "</body>\n"
           + "</html>"
        );

        createTemplateIfNotFound(
            "Carta de Recomendación",
            "<html>\n"
           + "<head>\n"
           + "    <title>Carta de Recomendación</title>\n"
           + "    <style>\n"
           + "        body { font-family: Arial, sans-serif; margin: 20px; }\n"
           + "        h1 { color: #0056b3; }\n"
           + "        p { margin: 5px 0; }\n"
           + "    </style>\n"
           + "</head>\n"
           + "<body>\n"
           + "    <h1>Carta de Recomendación</h1>\n"
           + "    <p><strong>Para:</strong> {{nombre_recomendado}}</p>\n"
           + "    <p><strong>De:</strong> {{nombre_recomendador}}</p>\n"
           + "    <p><strong>Fecha:</strong> {{fecha}}</p>\n"
           + "    <p><strong>Contenido:</strong> {{contenido}}</p>\n"
           + "</body>\n"
           + "</html>"
        );

        createTemplateIfNotFound(
            "Pagaré",
            "<html>\n"
           + "<head>\n"
           + "    <title>Pagaré</title>\n"
           + "</head>\n"
           + "<body>\n"
           + "    <h1>Pagaré</h1>\n"
           + "    <p>Deudor: {{nombre_deudor}}</p>\n"
           + "    <p>Acreedor: {{nombre_acreedor}}</p>\n"
           + "    <p>Monto: {{monto}}</p>\n"
           + "    <p>Fecha de Pago: {{fecha_pago}}</p>\n"
           + "    <p>Contenido: {{contenido}}</p>\n"
           + "</body>\n"
           + "</html>"
        );

        createTemplateIfNotFound(
            "Recibo de Pago",
            "<html>\n"
           + "<head>\n"
           + "    <title>Recibo de Pago</title>\n"
           + "    <style>\n"
           + "        body { font-family: Arial, sans-serif; margin: 20px; }\n"
           + "        h1 { color: #0056b3; }\n"
           + "        p { margin: 5px 0; }\n"
           + "        table { width: 100%; border-collapse: collapse; margin-top: 20px; }\n"
           + "        th, td { border: 1px solid #ddd; padding: 10px; text-align: left; }\n"
           + "        th { background-color: #0056b3; color: white; }\n"
           + "    </style>\n"
           + "</head>\n"
           + "<body>\n"
           + "    <h1>Recibo de Pago</h1>\n"
           + "    <p><strong>Nombre:</strong> {{nombre}}</p>\n"
           + "    <p><strong>Monto:</strong> {{monto}}</p>\n"
           + "    <p><strong>Fecha:</strong> {{fecha}}</p>\n"
           + "    <p><strong>Concepto:</strong> {{concepto}}</p>\n"
           + "</body>\n"
           + "</html>"
        );

        createTemplateIfNotFound(
            "Factura con Conceptos",
            "<html>\n"
           + "<head>\n"
           + "    <title>Factura</title>\n"
           + "</head>\n"
           + "<body>\n"
           + "    <h1>Factura</h1>\n"
           + "    <p>Cliente: {{cliente}}</p>\n"
           + "    <p>Fecha: {{fecha}}</p>\n"
           + "    <p>Conceptos:</p>\n"
           + "    <ul>\n"
           + "        {{#conceptos}}\n"
           + "        <li>{{descripcion}} - {{cantidad}} x {{precio_unitario}} = {{total}}</li>\n"
           + "        {{/conceptos}}\n"
           + "    </ul>\n"
           + "    <p>Total: {{total_factura}}</p>\n"
           + "</body>\n"
           + "</html>"
        );
    }
}