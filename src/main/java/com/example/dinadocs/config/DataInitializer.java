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

        String presupuestoObra = """
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Presupuesto de Obra</title>
    <style>
        @page {
            size: A4;
            margin: 0;
        }
        body {
            margin: 0;
            padding: 0;
            background-color: #555;
            font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif;
            color: #000;
        }
        .page {
            width: 210mm;
            min-height: 297mm;
            background-color: white;
            margin: 20px auto;
            position: relative;
            box-shadow: 0 0 15px rgba(0,0,0,0.3);
            padding: 15mm 15mm; 
            box-sizing: border-box;
        }
        .header {
            display: table;
            width: 100%;
            margin-bottom: 10px;
        }
        .logo-container {
            display: table-cell;
            width: 150px;
            height: 120px;
            vertical-align: top;
            border: 1px dashed #ccc; 
            text-align: center;
        }
        .logo-img {
            max-width: 100%;
            max-height: 100%;
        }
        .company-info {
            display: table-cell;
            text-align: right;
            font-size: 11px;
            line-height: 1.4;
            vertical-align: top;
            padding-left: 20px;
        }
        .company-name {
            font-weight: bold;
            font-size: 14px;
            margin-bottom: 5px;
            text-transform: uppercase;
        }
        .rfc {
            font-weight: bold;
            margin-bottom: 10px;
            display: block;
        }
        .yellow-bar {
            background-color: #FFFF00;
            height: 8px;
            width: 100%;
            margin-bottom: 20px;
        }
        .meta-info {
            text-align: right;
            font-size: 12px;
            margin-bottom: 20px;
        }
        .date-line {
            font-weight: bold;
            margin-bottom: 15px;
        }
        .client-block {
            text-align: left;
            font-weight: bold;
            font-size: 12px;
            text-transform: uppercase;
            width: 80%;
            margin-bottom: 20px;
            line-height: 1.4;
        }
        .doc-title {
            text-align: center;
            font-weight: bold;
            font-size: 14px;
            margin-bottom: 10px;
            letter-spacing: 1px;
        }
        .currency-label {
            text-align: right;
            font-size: 11px;
            margin-bottom: 2px;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            border: 2px solid black;
            font-size: 11px;
            margin-bottom: 20px;
        }
        th {
            background-color: #cccccc;
            border: 1px solid black;
            padding: 5px;
            font-weight: bold;
            text-transform: uppercase;
            text-align: center;
        }
        td {
            border: 1px solid black;
            padding: 5px;
            height: 20px;
            vertical-align: middle;
        }
        .col-cant { width: 10%; text-align: center; }
        .col-desc { width: 60%; text-align: left; }
        .col-unit { width: 15%; text-align: right; }
        .col-imp { width: 15%; text-align: right; }
        .totals-section {
            text-align: right;
            margin-top: 10px;
        }
        .totals-table {
            display: inline-block;
            border: none;
            font-size: 12px;
        }
        .totals-table td {
            border: none;
            padding: 3px 10px;
            text-align: right;
        }
        .totals-label {
            font-weight: bold;
        }
        @media print {
            body { background: none; }
            .page { margin: 0; box-shadow: none; width: 100%; height: 100%; }
            .logo-container { border: none; }
        }
    </style>
</head>
<body>
    <div class="page">
        <div class="header">
            <div class="logo-container">
                <img src="{{url_logo_empresa}}" alt="Logo Empresa" class="logo-img">
            </div>
            <div class="company-info">
                <div class="company-name">{{nombre_emisor}}</div>
                <span class="rfc">R.F.C {{rfc_emisor}}</span>
                <div>Régimen Fiscal: {{regimen_fiscal}}</div>
                <div style="margin-top: 5px;">Domicilio Fiscal: {{direccion_fiscal}}</div>
                <div>{{ciudad_estado}}</div>
            </div>
        </div>
        <div class="yellow-bar"></div>
        <div class="meta-info">
            <div class="date-line">{{lugar_y_fecha}}</div>
        </div>
        <div class="client-block">
            {{nombre_cliente_completo}}<br>
            PRESENTE, PRESENTE
        </div>
        <div class="doc-title">PRESUPUESTO</div>
        <div class="currency-label">Moneda: MXN – peso mexicano</div>
        <table>
            <thead>
                <tr>
                    <th class="col-cant">CANTIDAD</th>
                    <th class="col-desc">CONCEPTO</th>
                    <th class="col-unit">P. UNITARIO</th>
                    <th class="col-imp">IMPORTE</th>
                </tr>
            </thead>
            <tbody>
                {{#partidas}}
                <tr>
                    <td class="col-cant">{{cantidad}}</td>
                    <td class="col-desc">{{descripcion}}</td>
                    <td class="col-unit">{{precio_unitario}}</td>
                    <td class="col-imp">{{importe}}</td>
                </tr>
                {{/partidas}}
                {{^partidas}}
                <tr><td>&nbsp;</td><td></td><td></td><td></td></tr>
                <tr><td>&nbsp;</td><td></td><td></td><td></td></tr>
                <tr><td>&nbsp;</td><td></td><td></td><td></td></tr>
                <tr><td>&nbsp;</td><td></td><td></td><td></td></tr>
                {{/partidas}}
            </tbody>
        </table>
        <div class="totals-section">
            <table class="totals-table">
                <tr>
                    <td class="totals-label">SUBTOTAL:</td>
                    <td>$ {{subtotal}}</td>
                </tr>
                <tr>
                    <td class="totals-label">IVA (16%):</td>
                    <td>$ {{iva}}</td>
                </tr>
                <tr>
                    <td class="totals-label">TOTAL:</td>
                    <td>$ {{total_final}}</td>
                </tr>
            </table>
        </div>
    </div>
</body>
</html>
                """;

        createTemplateIfNotFound("Presupuesto de Obra", presupuestoObra);
    }
}