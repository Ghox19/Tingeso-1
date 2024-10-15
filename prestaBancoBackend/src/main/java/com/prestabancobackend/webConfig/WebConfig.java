package com.prestabancobackend.webConfig;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/*
    Descripcion: Esta clase `WebConfig` es una clase de configuración que implementa la interfaz `WebMvcConfigurer`.
    Su propósito es configurar el CORS (Cross-Origin Resource Sharing) para la aplicación, permitiendo o restringiendo
    las solicitudes HTTP desde diferentes orígenes.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /*
        Descripcion: Este método configura las reglas de CORS para la aplicación.
        Permite solicitudes de cualquier origen (`allowedOrigins("*")`) y limita
        los métodos HTTP permitidos a GET, POST, PUT y DELETE.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*") // Especifica el origen permitido
                .allowedMethods("GET", "POST", "PUT", "DELETE");
    }
}