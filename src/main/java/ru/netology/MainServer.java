package ru.netology;

import ru.netology.config.Config;
import ru.netology.server.ChatServer;

public class MainServer {

    public static void main(String[] args) { // Точка входа для запуска Сервера

        // Обращаемся к классу Config, где из файла config.properties,
        // расположенного в по адресу "src/main/java/ru/netology/resources/config.properties",
        // получаем номер порта
        int port = new Config().getPort();

        new ChatServer(port);
    }
}
