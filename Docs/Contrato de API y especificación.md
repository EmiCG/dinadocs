# 📝 Contrato de API y Especificación (DynaDocs)

Este documento detalla el contrato de cada componente de la arquitectura final C-S-R (Controller-Service-Repository) para la aplicación DynaDocs, incluyendo ejemplos de requests y responses para facilitar la integración con el frontend.

---

## 1. 📦 Paquete `model` (Estructura de Datos)

Define las clases que transportan y persisten los datos.

### 1.1. `GenerationRequest` (DTO - Request Body)

Propósito: Recibir el JSON del Frontend para la generación de PDF.

| Atrib | Tipo | Propósito |
| :--- | :--- | :--- |
| **`templateType`** | `String` | **RF-02**: Identificador de la plantilla a utilizar (Ej: "Factura", "Perfil"). |
| **`data`** | `Map<String, Object>` | **RF-03, RF-04**: Pares clave-valor con la información dinámica. <br><br> **Manejo de Imágenes**: Si una plantilla requiere una imagen (ej. `{{foto_usuario}}`), el cliente (Flutter) debe convertir la imagen seleccionada a **Base64** y enviarla como un `String` dentro de este mapa. (Ej: `"foto_usuario": "data:image/jpeg;base64,iVBOR..."`). |

#### Ejemplo de Request:
```json
{
  "templateType": "Factura",
  "data": {
    "numero_factura": "12345",
    "nombre_cliente": "Juan Pérez",
    "monto_total": "1500.00",
    "foto_usuario": "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQEASABIA..."
  }
}
```

---

## 2. 🌐 Paquete `controller` (API REST Endpoints)

Actúan como la interfaz HTTP del sistema. Delegan toda la lógica al servicio.

### 2.1. `PdfController`

| Endpoint | Método | Seguridad (Nivel 1) | Descripción y Contrato |
| :--- | :--- | :--- | :--- |
| **`POST /api/generate`** | `generateDocument(GenerationRequest)` | Autenticado (Cualquier Rol) | **RF-03, RF-05**: Inicia la generación y descarga del PDF. |

#### Ejemplo de Request:
```json
{
  "templateType": "Factura",
  "data": {
    "numero_factura": "12345",
    "nombre_cliente": "Juan Pérez",
    "monto_total": "1500.00"
  }
}
```

#### Ejemplo de Response (Éxito):
- **Código HTTP:** `200 OK`
- **Tipo:** `application/pdf`
- **Cabecera:** `Content-Disposition: attachment; filename="document.pdf"`

#### Ejemplo de Response (Error):
```json
{
  "error": "Template not found"
}
```

---

### 2.2. `AuthController`

| Endpoint | Método | Seguridad (Nivel 1) | Descripción y Contrato |
| :--- | :--- | :--- | :--- |
| **`POST /api/register`** | `registerUser(User user)` | Público | Registra un nuevo `User`. |
| **`POST /api/login`** | `authenticateUser(LoginRequest)` | Público | Inicia sesión. |

#### Ejemplo de Request (Registro):
```json
{
  "name": "Juan Pérez",
  "email": "juan.perez@gmail.com",
  "password": "123456",
  "role": "USUARIO"
}
```

#### Ejemplo de Response (Registro):
```json
{
  "id": 1,
  "name": "Juan Pérez",
  "email": "juan.perez@gmail.com",
  "role": "USUARIO"
}
```

#### Ejemplo de Request (Login):
```json
{
  "email": "juan.perez@gmail.com",
  "password": "123456"
}
```

#### Ejemplo de Response (Login):
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

---

### 2.3. `TemplateController` (CRUD Completo)

| Endpoint | Método | Seguridad (Nivel 1) | Descripción y Contrato |
| :--- | :--- | :--- | :--- |
| **`POST /api/templates`** | `createTemplate(Template)` | Autenticado | Crea una nueva plantilla. |
| **`GET /api/templates`** | `getAllTemplates()` | Autenticado | Lista las plantillas disponibles. |
| **`GET /api/templates/{id}`** | `getTemplateById(id)` | Autenticado | Obtiene una plantilla específica. |
| **`PUT /api/templates/{id}`** | `updateTemplate(id, template)` | Autenticado | Actualiza una plantilla existente. |
| **`DELETE /api/templates/{id}`** | `deleteTemplate(id)` | Autenticado | Elimina una plantilla. |

#### Ejemplo de Request (Crear Plantilla):
```json
{
  "name": "Factura",
  "content": "<html><body><h1>Factura Nro: {{numero_factura}}</h1></body></html>",
  "isPublic": true
}
```

#### Ejemplo de Response (Crear Plantilla):
```json
{
  "id": 1,
  "name": "Factura",
  "content": "<html><body><h1>Factura Nro: {{numero_factura}}</h1></body></html>",
  "isPublic": true,
  "owner": {
    "id": 2,
    "name": "Creador",
    "email": "creator@gmail.com"
  }
}
```

#### Ejemplo de Request (Actualizar Plantilla):
```json
{
  "name": "Factura Actualizada",
  "content": "<html><body><h1>Factura Nro: {{numero_factura}}</h1><p>Cliente: {{nombre_cliente}}</p></body></html>",
  "isPublic": true
}
```

#### Ejemplo de Response (Actualizar Plantilla):
```json
{
  "id": 1,
  "name": "Factura Actualizada",
  "content": "<html><body><h1>Factura Nro: {{numero_factura}}</h1><p>Cliente: {{nombre_cliente}}</p></body></html>",
  "isPublic": true,
  "owner": {
    "id": 2,
    "name": "Creador",
    "email": "creator@gmail.com"
  }
}
```

#### Ejemplo de Request (GET /api/templates):
- **URL:** `/api/templates`
- **Método:** `GET`
- **Headers:**
  ```json
  {
    "Authorization": "Bearer <token>"
  }
  ```

#### Ejemplo de Response (GET /api/templates):
```json
[
  {
    "id": 1,
    "name": "Factura",
    "content": "<html><body><h1>Factura Nro: {{numero_factura}}</h1><p>Cliente: <strong>{{nombre_cliente}}</strong></p><p>Monto Total: <strong>${{monto_total}}</strong></p></body></html>",
    "placeholders": [
      "numero_factura",
      "nombre_cliente",
      "monto_total"
    ],
    "public": true
  },
  {
    "id": 2,
    "name": "Perfil",
    "content": "<html><head><style>.profile-pic { width: 100px; height: 100px; border-radius: 50%; object-fit: cover; }</style></head><body><h1>Perfil de Usuario</h1><img src='{{foto_usuario}}' class='profile-pic' /><h2>{{nombre_usuario}}</h2></body></html>",
    "placeholders": [
      "foto_usuario",
      "nombre_usuario"
    ],
    "public": true
  },
  {
    "id": 3,
    "name": "PresupuestoCompuesto",
    "content": "<html>\n<head>\n    <meta charset=\"UTF-8\">\n    <style>\n        body {\n            font-family: Arial, sans-serif;\n            margin: 40px;\n            font-size: 14px;\n            color: #333;\n        }\n        .header-container {\n            display: flex;\n            justify-content: space-between;\n            align-items: flex-start;\n            border-bottom: 3px solid #0056b3; /* Color azul corporativo */\n            padding-bottom: 20px;\n        }\n        /* ESTILOS PARA EL LOGO DEL USUARIO */\n        .user-logo {\n            width: 150px;       /* Ancho fijo para el logo */\n            max-height: 100px;  /* Altura máxima */\n            object-fit: contain; /* Asegura que la imagen quepa sin deformarse */\n        }\n        .invoice-details {\n            text-align: right;\n            font-size: 12px;\n            color: #555;\n        }\n        .invoice-details h1 {\n            margin: 0;\n            color: #0056b3;\n            font-size: 32px;\n        }\n        .client-info {\n            margin-top: 30px;\n            padding: 15px;\n            background-color: #f9f9f9;\n            border-radius: 5px;\n        }\n        .client-info strong {\n            display: block;\n            margin-bottom: 5px;\n            color: #000;\n        }\n        .items-table {\n            width: 100%;\n            border-collapse: collapse;\n            margin-top: 30px;\n        }\n        .items-table th, .items-table td {\n            border: 1px solid #ddd;\n            padding: 12px;\n        }\n        .items-table th {\n            background-color: #0056b3;\n            color: white;\n            text-align: left;\n        }\n        .items-table .amount {\n            text-align: right;\n        }\n        .totals-container {\n            width: 350px;\n            margin-left: auto;\n            margin-top: 20px;\n        }\n        .totals-table {\n            width: 100%;\n        }\n        .totals-table td {\n            padding: 10px;\n        }\n        .totals-table .label {\n            text-align: right;\n            font-weight: bold;\n        }\n        .totals-table .value {\n            text-align: right;\n            width: 130px;\n        }\n        .totals-table .grand-total .label {\n            font-size: 20px;\n        }\n        .totals-table .grand-total .value {\n            font-size: 20px;\n            font-weight: bold;\n            border-top: 3px solid #0056b3;\n        }\n    </style>\n</head>\n<body>\n    <div class=\"header-container\">\n        <div>\n            <img src=\"{{logo_empresa}}\" class=\"user-logo\" alt=\"Logo de Empresa\">\n        </div>\n        <div class=\"invoice-details\">\n            <h1>PRESUPUESTO</h1>\n            <strong>Fecha:</strong> {{fecha_presupuesto}}<br>\n            <strong>Presupuesto #:</strong> {{numero_presupuesto}}\n        </div>\n    </div>\n    <div class=\"client-info\">\n        <strong>Presupuesto Para:</strong>\n        {{nombre_cliente}}<br>\n        {{direccion_cliente}}<br>\n        {{email_cliente}}\n    </div>\n    <table class=\"items-table\">\n        <thead>\n            <tr>\n                <th>Descripción</th>\n                <th class=\"amount\">Total</th>\n            </tr>\n        </thead>\n        <tbody>\n            <tr>\n                <td>{{descripcion_item_1}}</td>\n                <td class=\"amount\">$ {{monto_item_1}}</td>\n            </tr>\n            <tr>\n                <td>{{descripcion_item_2}}</td>\n                <td class=\"amount\">$ {{monto_item_2}}</td>\n            </tr>\n        </tbody>\n    </table>\n    <div class=\"totals-container\">\n        <table class=\"totals-table\">\n            <tr>\n                <td class=\"label\">Subtotal:</td>\n                <td class=\"value\">$ {{subtotal}}</td>\n            </tr>\n            <tr>\n                <td class=\"label\">IVA (16%):</td>\n                <td class=\"value\">$ {{iva}}</td>\n            </tr>\n            <tr class=\"grand-total\">\n                <td class=\"label\">TOTAL:</td>\n                <td class=\"value\">$ {{total}}</td>\n            </tr>\n        </table>\n    </div>\n</body>\n</html>",
    "placeholders": [
      "logo_empresa",
      "fecha_presupuesto",
      "numero_presupuesto",
      "nombre_cliente",
      "direccion_cliente",
      "email_cliente",
      "descripcion_item_1",
      "monto_item_1",
      "descripcion_item_2",
      "monto_item_2",
      "subtotal",
      "iva",
      "total"
    ],
    "public": true
  },
  {
    "id": 4,
    "name": "FacturaDinamica",
    "content": "<!DOCTYPE html>\n<html>\n<head>\n    <title>Factura</title>\n</head>\n<body>\n    <h1>Factura</h1>\n    <p>Cliente: {{cliente}}</p>\n    <p>Fecha: {{fecha}}</p>\n    <p>Productos:</p>\n    <ul>\n        {{#productos}}\n        <li>{{nombre}} - {{precio}} USD</li>\n        {{/productos}}\n    </ul>\n    <p>Total: {{total}} USD</p>\n</body>\n</html>",
    "placeholders": [
      "cliente",
      "fecha",
      "#productos",
      "nombre",
      "precio",
      "/productos",
      "total"
    ],
    "public": true
  }
]
```

#### Ejemplo de Response (Eliminar Plantilla):
- **Código HTTP:** `204 No Content`

---

## 3. 🧠 Paquete `service` (Lógica de Negocio)

Contienen la lógica de negocio pura y la validación. Llaman a los Repositorios para la persistencia.

### 3.1. `PdfGenerationService`

| Método | Contrato de Datos | Responsabilidad |
| :--- | :--- | :--- |
| **`+ generatePdf(request, authUser)`** | **Recibe:** `GenerationRequest`, `User`. **Retorna:** `byte[]`. | Coordina todo el flujo de generación de PDF. |

---

## 4. 💾 Paquete `repository` (Capa de Acceso a Datos)

Interfaces de Spring Data JPA que gestionan la comunicación con la Base de Datos.

### 4.1. `UserRepository`

* **Método Custom:** `Optional<User> findByUsername(String username);`

### 4.2. `TemplateRepository`

* **Método Custom:** `Optional<Template> findByName(String name);`
* **Método Custom:** `List<Template> findByIsPublicTrueOrOwner(User user);`