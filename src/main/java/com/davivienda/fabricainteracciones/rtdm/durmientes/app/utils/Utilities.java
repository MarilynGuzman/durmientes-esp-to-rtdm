package com.davivienda.fabricainteracciones.rtdm.durmientes.app.utils;


public final class Utilities {
    // Formatea el valor de la variable valor_trx quitando los dos ceros decimales
    public static String format_val_trx(String valor_trx) {
        if (valor_trx.length() <= 1) {
            return "0";
        }
        else {
            return valor_trx.substring(0, valor_trx.length()-2);
        }
    }

    // Valida si el string es null
    public static String checkNull(String strToCheck) {
        if (strToCheck == null) {
            return "null";
        }
        else {
            return strToCheck;
        }
    }
}