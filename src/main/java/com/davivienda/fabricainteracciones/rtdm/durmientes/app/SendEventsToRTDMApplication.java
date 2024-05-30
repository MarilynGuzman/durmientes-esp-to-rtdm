package com.davivienda.fabricainteracciones.rtdm.durmientes.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

/**
 * Inicia la aplicación de envío de eventos recibidos de ESP hacia RTDM
 */
@ImportResource("classpath:camel-context.xml")
@SpringBootApplication
public class SendEventsToRTDMApplication {

    public static void main(String[] args) {
        SpringApplication.run(SendEventsToRTDMApplication.class, args);
    }

}
