package com.davivienda.fabricainteracciones.rtdm.durmientes.app.processor;

import com.davivienda.fabricainteracciones.rtdm.durmientes.app.exception.SendEventToRTDMException;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.json.simple.JsonObject;

/**
 * Genera archivo con información de errores presentados en ejecución para enviar posteriormente notificación por correo
 */
public class MailFileProcessor implements Processor{

    /**
     * Genera la estructura de archivo de notificación en formato Json para envio de notificaciones por correo
     * @param exchange objeto de Camel compuesto que contiene propiedades almacenadas durante ejecución
     */
    @Override
    public void process(Exchange exchange) throws SendEventToRTDMException {
        try {
            String mailRecipient = exchange.getIn().getHeader("MailRecipient",String.class);
            String mailSender = exchange.getIn().getHeader("MailSender",String.class);
            String mailBody = exchange.getIn().getHeader("MailBody",String.class);
            String carbonCopy = "";
            String attachments = "";

            JsonObject mailRequest = new JsonObject();
            mailRequest.put("toDto", mailRecipient);
            mailRequest.put("subjectDto", "Error de en lectura de respuestas de Eloqua");
            mailRequest.put("ccDto", carbonCopy);
            mailRequest.put("fromDto", mailSender);
            mailRequest.put("bodyDto", mailBody);
            mailRequest.put("attachmentsDto", attachments);

            exchange.getOut().setBody(mailRequest.toJson());
        } catch (Exception ex) {
            throw new SendEventToRTDMException(ex.getMessage());
        }
    }
}
