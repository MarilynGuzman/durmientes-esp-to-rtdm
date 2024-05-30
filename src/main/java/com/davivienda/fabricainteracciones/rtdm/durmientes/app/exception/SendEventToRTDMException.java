package com.davivienda.fabricainteracciones.rtdm.durmientes.app.exception;

/**
 * Excepcion personalizada manejo fallos en ejecución
 */
public class SendEventToRTDMException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * @param message contiene información del error presentado
     */
    public SendEventToRTDMException(String message) {
        super(message);
    }
}
