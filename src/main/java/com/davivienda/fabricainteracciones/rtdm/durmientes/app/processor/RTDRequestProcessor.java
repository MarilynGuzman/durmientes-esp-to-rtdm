package com.davivienda.fabricainteracciones.rtdm.durmientes.app.processor;

import java.util.List;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.davivienda.rtdm.aes.AESRealTimeDecisionManager;
import com.davivienda.fabricainteracciones.rtdm.durmientes.app.exception.SendEventToRTDMException;
import com.davivienda.fabricainteracciones.rtdm.durmientes.app.utils.Utilities;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.json.simple.JsonObject;
import org.springframework.util.StopWatch;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Procesa el evento recibido del componente ESP para enviar solicitud hacia RTDM
 */
public class RTDRequestProcessor implements Processor{

    @Value("${aes.key.dmt}")
    String KEY;

    private static final String TIME_ZONE = "America/Bogota";
    private static final int VERSION = 1;
    private static final Logger logger = LoggerFactory.getLogger(RTDRequestProcessor.class);

    /**
     * Obtiene los campos requeridos para la solicitud de envío de eventos hacia RTDM  y crea el cuerpo del mensaje en Formato Json
     * @param exchange objeto de Camel compuesto que contiene propiedades almacenadas durante ejecución
     */
    @Override
    public void process(Exchange exchange) throws SendEventToRTDMException {

        try {
            // Instanciamos AESRealTimeDecisionManager
            AESRealTimeDecisionManager cypher = new AESRealTimeDecisionManager();
            // Instanciamos StopWatch
            StopWatch watch = new StopWatch();

            // Creamos un array que contiene los nombres de las variables
            // que serán seleccionadas para armar el objeto JSON
            String[] varArray = new String[]{"tracking_esp", "tidnid","id_trx", "id_caso",
                                            "nro_cuenta", "valor_trx","cl_digital", "cl_joven",
                                            "nro_tarjeta", "comercio1", "comercio2", "tipo_td"};
            String[] varArrayDB = new String[]{"tracking_esp", "id_trx", "id_caso", "tidnid", "valor_trx",
                                              "cl_digital", "cl_joven", "nro_cuenta",
                                              "comercio1", "comercio2", "nro_tarjeta", "tipo_td",
                                              "fecha_transaccion", "hora_transaccion", "fecha_creacion"};
            List<String> varList = Arrays.asList(varArray);
            List<String> varListDB = Arrays.asList(varArrayDB);

            String espResponse = exchange.getIn().getBody(String.class);
            espResponse = espResponse.substring(1, espResponse.length()-1);
            String[] espResponseElements = espResponse.split(",");
            Map<String,String> espResponseData = new HashMap<>();
            for(String element: espResponseElements) {
                String[] keyValue = element.split("=");
                if(keyValue.length > 1) {
                    espResponseData.put(keyValue[0].trim(), keyValue[1].trim());
                }else {
                    espResponseData.put(keyValue[0].trim(), null);
                }
            }

            // Damos formato al valor de la variable valor_trx y
            // reemplazamos el nuevo valor en la key valor_trx
            // pendiente ajustar ESP - Valor SUBstring
            espResponseData.replace("valor_trx", Utilities.format_val_trx(espResponseData.get("valor_trx")));

            JsonObject request =  new JsonObject();
            JsonObject inputs = new JsonObject();
            Map<String,Object> espRespToDb = new HashMap<>();

            watch.start();
            for (Map.Entry<String,String> entry : espResponseData.entrySet()) {
                if(varList.contains(entry.getKey())){
                    inputs.put(entry.getKey(), cypher.encrypt(Utilities.checkNull(entry.getValue()), KEY));
                }
                if(varListDB.contains(entry.getKey())){
                    espRespToDb.put(entry.getKey(), entry.getValue());
                }
            }
            watch.stop();
            logger.info("Tiempo transcurrido Modulo Encriptacion: " + watch.getTotalTimeSeconds() + "s" +
                        " | " + "Variables encriptadas: " + inputs);

            request.put("inputs", inputs);
            request.put("version", VERSION);
            request.put("clientTimeZone", TIME_ZONE);
            exchange.setProperty("EspResToDb", espRespToDb);
            exchange.getOut().setBody(request.toJson());
        }catch(Exception ex) {
            throw new SendEventToRTDMException(ex.getMessage());
        }
    }

}
