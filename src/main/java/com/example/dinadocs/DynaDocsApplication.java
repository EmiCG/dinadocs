package com.example.dinadocs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

/**
 * Clase principal de la aplicación DynaDocs.
 * Plataforma para la creación y reutilización de plantillas personalizadas de documentos.
 * 
 * @author DynaDocs Team
 * @version 1.0
 * @since 2025-12-03
 */
@SpringBootApplication
public class DynaDocsApplication {

	/**
	 * Método principal que inicia la aplicación Spring Boot.
	 * 
	 * @param args argumentos de línea de comandos
	 */
	public static void main(String[] args) {
		SpringApplication.run(DynaDocsApplication.class, args);
	}

}
