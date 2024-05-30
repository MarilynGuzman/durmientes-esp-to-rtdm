package com.davivienda.fabricainteracciones.rtdm.durmientes.app.configurer;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Gestiona la comunicación SSL hacia el componente ESP
 */
@Component
public class StartSSLContext {

    private static final Logger logger = LoggerFactory.getLogger(StartSSLContext.class);

    public StartSSLContext() {
        startSSl();
    }

    /**
     * Inicializa las variables de entorno para la comunicación con ESP
     */
    public void startSSl() {
        String espHome = System.getenv("DFESP_HOME");
        try {
            if(espHome == null) {
                Map<String,String> variables = new HashMap<>();
                variables.put("DFESP_HOME", "D:\\ESP");
                setEnv(variables);
            }
        }catch(Exception ex) {
            logger.error(ex.getMessage());
        }
    }

    /**
     * Asigna la nueva variable de entorno al sistema
     * @param newenv contiene el valor de la variable de entorno a adicionar
     * @throws ClassNotFoundException Excepción lanzada cuando no se encuentra una clase durante la carga de la aplicación
     * @throws IllegalAccessException Excepción lanzada cuando se intenta acceder a un método cuya visibilidad no es permitida
     * @throws NoSuchFieldException Indica que la clase no contiene el campo que se intenta asignar valor
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void setEnv(Map<String, String> newenv) throws ClassNotFoundException, IllegalAccessException, NoSuchFieldException{
        try {
            Class<?> processEnvironmentClass = Class.forName("java.lang.ProcessEnvironment");
            Field theEnvironmentField = processEnvironmentClass.getDeclaredField("theEnvironment");
            theEnvironmentField.setAccessible(true);
            Map<String, String> env = (Map<String, String>) theEnvironmentField.get(null);
            env.putAll(newenv);
            Field theCaseInsensitiveEnvironmentField = processEnvironmentClass.getDeclaredField("theCaseInsensitiveEnvironment");
            theCaseInsensitiveEnvironmentField.setAccessible(true);
            Map<String, String> cienv = (Map<String, String>) theCaseInsensitiveEnvironmentField.get(null);
            cienv.putAll(newenv);
        } catch (NoSuchFieldException e) {
            Class[] classes = Collections.class.getDeclaredClasses();
            Map<String, String> env = System.getenv();
            for (Class cl : classes) {
                String nameX = cl.getName();
                if ("java.util.Collections$UnmodifiableMap".equals(nameX)) {
                    Field field = cl.getDeclaredField("m");
                    field.setAccessible(true);
                    Object obj = field.get(env);
                    Map<String, String> map = (Map<String, String>) obj;
                    map.clear();
                    map.putAll(newenv);
                }
            }
        }
    }

}
