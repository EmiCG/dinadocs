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
        /* --- Configuración A4 --- */
        @page {
            size: A4;
            margin: 0;
        }

        body {
            margin: 0;
            padding: 0;
            background-color: #555; /* Fondo oscuro para ver la hoja en pantalla */
            font-family: 'Arial', sans-serif; /* Fuente estándar limpia */
            color: #000;
            -webkit-print-color-adjust: exact;
        }

        .page {
            width: 210mm;
            min-height: 297mm;
            background-color: white;
            margin: 20px auto;
            position: relative;
            padding: 15mm; /* Margen interno de la hoja */
            box-sizing: border-box;
            box-shadow: 0 0 15px rgba(0,0,0,0.5);
            overflow: hidden; /* Evita que nada se salga de la hoja visualmente */
        }

        /* --- ENCABEZADO (Corrección de Imagen) --- */
        .header {
            display: flex;
            justify-content: space-between;
            align-items: flex-start; /* Alinea arriba para que el logo no baje el texto */
            margin-bottom: 15px;
            gap: 20px; /* Espacio entre logo y texto */
        }

        .logo-container {
            /* Dimensiones fijas para el área del logo */
            width: 160px; 
            height: 140px; 
            /* Flexbox para centrar la imagen dentro de este cuadro */
            display: flex;
            align-items: center; 
            justify-content: flex-start;
            overflow: hidden; /* IMPORTANTE: Corta lo que sobre si es gigante */
        }

        .logo-img {
            /* La imagen nunca excederá el ancho/alto del contenedor */
            max-width: 100%;
            max-height: 100%;
            object-fit: contain; /* Se ajusta sin deformarse */
            display: block;
        }

        .company-info {
            text-align: right;
            font-size: 11px;
            line-height: 1.3;
            flex: 1; /* Toma el espacio restante */
        }

        .company-name {
            font-weight: bold;
            font-size: 14px;
            text-transform: uppercase;
            margin-bottom: 5px;
        }

        .rfc-line {
            font-weight: bold;
            margin-bottom: 8px;
            display: block;
        }

        /* --- Barra Amarilla --- */
        .yellow-bar {
            background-color: #FFFF00; /* Amarillo puro */
            height: 6px;
            width: 100%;
            margin-bottom: 15px;
        }

        /* --- Datos Cliente y Título --- */
        .meta-data {
            font-size: 12px;
            margin-bottom: 20px;
        }

        .date-right {
            text-align: right;
            font-weight: bold;
            margin-bottom: 15px;
        }

        .client-block {
            text-align: left;
            font-weight: bold;
            text-transform: uppercase;
            width: 80%;
            line-height: 1.4;
        }

        .document-title {
            text-align: center;
            font-weight: bold;
            font-size: 14px;
            margin-top: 20px;
            margin-bottom: 5px;
            letter-spacing: 1px;
        }

        .currency {
            text-align: right;
            font-size: 10px;
            margin-bottom: 2px;
        }

        /* --- Tabla --- */
        table {
            width: 100%;
            border-collapse: collapse;
            border: 2px solid black; /* Marco exterior grueso */
            font-size: 11px;
        }

        th {
            background-color: #d9d9d9; /* Gris suave */
            border: 1px solid black;
            padding: 6px;
            text-align: center;
            font-weight: bold;
            text-transform: uppercase;
        }

        td {
            border: 1px solid black;
            padding: 4px 6px;
            vertical-align: middle;
            height: 22px; /* Altura mínima de fila */
        }

        /* Anchos de columnas ajustados a la imagen */
        .col-cant { width: 8%; text-align: center; }
        .col-concept { width: 62%; text-align: left; }
        .col-unit { width: 15%; text-align: right; }
        .col-import { width: 15%; text-align: right; }

        /* --- Totales --- */
        .totals-wrapper {
            display: flex;
            justify-content: flex-end;
            margin-top: 15px;
        }

        .totals-table {
            border: none;
            width: auto;
        }

        .totals-table td {
            border: none;
            padding: 4px 10px;
            text-align: right;
            font-size: 12px;
        }

        .label-total { font-weight: bold; }
        
        /* Checkbox decorativo gris en la derecha (como en la imagen) */
        .checkbox-decoration {
            position: absolute;
            right: 15mm;
            top: 55%;
            width: 15px;
            height: 15px;
            border: 1px solid #999;
            background: #fff;
        }

        /* Ajustes de impresión */
        @media print {
            body { background: none; }
            .page { 
                margin: 0; 
                box-shadow: none; 
                width: 100%;
                height: 100%; 
                border: none;
            }
        }
    </style>
</head>
<body>

    <div class="page">
        <div class="header">
            <div class="logo-container">
                <img src="{{url_logo_empresa}}" alt="Logo" class="logo-img">
            </div>
            
            <div class="company-info">
                <div class="company-name">{{nombre_emisor}}</div>
                <span class="rfc-line">R.F.C {{rfc_emisor}}</span>
                <div>Régimen Fiscal: {{regimen_fiscal}}</div>
                <div style="margin-top:4px;">Domicilio Fiscal: {{direccion_fiscal}}</div>
                <div>{{ciudad_estado_cp}}</div>
            </div>
        </div>

        <div class="yellow-bar"></div>

        <div class="meta-data">
            <div class="date-right">{{lugar_y_fecha}}</div>
            
            <div class="client-block">
                {{nombre_cliente}}<br>
                PRESENTE, PRESENTE
            </div>
        </div>

        <div class="document-title">PRESUPUESTO</div>
        <div class="currency">Moneda: MXN – peso mexicano</div>

        <table>
            <thead>
                <tr>
                    <th class="col-cant">CANTIDAD</th>
                    <th class="col-concept">CONCEPTO</th>
                    <th class="col-unit">P. UNITARIO</th>
                    <th class="col-import">IMPORTE</th>
                </tr>
            </thead>
            <tbody>
                {{#partidas}}
                <tr>
                    <td class="col-cant">{{cantidad}}</td>
                    <td class="col-concept">{{descripcion}}</td>
                    <td class="col-unit">{{precio_unitario}}</td>
                    <td class="col-import">{{importe}}</td>
                </tr>
                {{/partidas}}

                {{^partidas}}
                <tr><td>&nbsp;</td><td></td><td></td><td></td></tr>
                <tr><td>&nbsp;</td><td></td><td></td><td></td></tr>
                <tr><td>&nbsp;</td><td></td><td></td><td></td></tr>
                <tr><td>&nbsp;</td><td></td><td></td><td></td></tr>
                <tr><td>&nbsp;</td><td></td><td></td><td></td></tr>
                {{/partidas}}
            </tbody>
        </table>

        <div class="totals-wrapper">
            <table class="totals-table">
                <tr>
                    <td class="label-total">SUBTOTAL:</td>
                    <td>$ {{subtotal}}</td>
                </tr>
                <tr>
                    <td class="label-total">IVA (16%):</td>
                    <td>$ {{iva}}</td>
                </tr>
                <tr>
                    <td class="label-total">TOTAL:</td>
                    <td>$ {{total_final}}</td>
                </tr>
            </table>
        </div>

        <div class="checkbox-decoration"></div>

    </div>

</body>
</html>
                """;

        createTemplateIfNotFound("Presupuesto de Obra", presupuestoObra);
    }
}