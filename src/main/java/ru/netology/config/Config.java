package ru.netology.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {
    private int port;
    private String host;

    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    public Config() {
        FileInputStream fis;
        Properties property = new Properties();

        try {
            fis = new FileInputStream("src/main/java/ru/netology/resources/config.properties");
            property.load(fis);

            this.port = Integer.parseInt(property.getProperty("server.port"));
            this.host = property.getProperty("server.host");


        } catch (IOException e) {
            throw new RuntimeException("ERROR: The properties file is missing!");
        }

    }
}
