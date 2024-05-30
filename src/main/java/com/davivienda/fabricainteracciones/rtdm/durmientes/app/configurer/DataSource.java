package com.davivienda.fabricainteracciones.rtdm.durmientes.app.configurer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import co.sas.integ.app.encrypt.JasyptEncryption;

public class DataSource extends BasicDataSource {

    private static Properties properties = new Properties();
    private static final Logger logger = LoggerFactory.getLogger(DataSource.class);

    public DataSource() {
        try (InputStream input = new FileInputStream("config/application.properties")){
            JasyptEncryption jasEncrypt = new JasyptEncryption();
            properties.load(input);
            StringBuilder url = new StringBuilder();
            url.append("jdbc:postgresql://")
                    .append(properties.getProperty("db_url")).append("/")
                    .append(properties.getProperty("db_database"));

            this.setUrl(url.toString());
            this.setUsername(properties.getProperty("db_user"));
            this.setPassword(jasEncrypt.decryptText(properties.getProperty("db_password")));
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        }
    }
}
