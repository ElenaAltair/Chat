package ru.netology.client;

import ru.netology.config.Config;
import ru.netology.connection.TCPConnection;
import ru.netology.connection.TCPConnectionListener;
import ru.netology.log.Log;

import java.io.IOException;
import java.util.Scanner;

public class ChatClient implements TCPConnectionListener {
    private int port;
    private String host;
    private TCPConnection connection;

    public ChatClient() {

        // Обращаемся к классу Config, где из файла config.properties,
        // расположенного в по адресу "src/main/java/ru/netology/resources/config.properties",
        // получаем номер порта
        port = new Config().getPort();
        // и хост
        host = new Config().getHost();
        try {
            // при подключении создаём новое TCP соединение
            connection = new TCPConnection(this, host, port);

            Scanner scanner = new Scanner(System.in);

            System.out.println("Здравствуйте! Введите свое имя, пожалуйста: ");
            String userName = scanner.nextLine();
            if (userName.trim().isEmpty()) userName = "UserName";

            System.out.println("Введите сообщение или /exit для выхода из чата:");
            String message = "";

            // запускаем бесконечный цикл
            // (пока пользователь не введёт /exit наше соединение будет работать и передавать все строки, которые будет вводить пользователь)
            while (!(message = scanner.nextLine()).equals("/exit")) {//
                connection.sendString(userName + ": " + message);
            }

            connection.disconnect(); // разрываем соединение

        } catch (IOException e) {
            System.out.println("Исключение: " + e);
            printMsg("Исключение: " + e, "", " (class: MainClient / method: onException)");
        }

    }


    @Override
    public void onConnectionReady(TCPConnection tcpConnection) { // соединение готово к использованию / произошло подключение нового клиента
        System.out.println("Соединение установлено...");
        printMsg("Соединение установлено...", "", " (class: MainClient / method: onConnectionReady)");
    }

    @Override
    public void onReceiveString(TCPConnection tcpConnection, String value) { // мы приняли строчку
        System.out.println(value);
        printMsg(value, "", " (class: MainClient / method: onConnectionReady)");
    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) { // клиент прервал соединение

        System.out.println("Соединение закрыто...");
        printMsg("Соединение закрыто...", "", " (class: MainClient / method: onDisconnect)");
    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception e) { // произошло какое-то исключение
        System.out.println("Исключение: " + e);
        printMsg("Исключение: " + e, "", " (class: MainClient / method: onException)");
    }

    // метод log моего класса Log, записывает новою строчку в файл File.log,
    // расположенного по адресу src/main/java/ru/netology/resources/File.log
    public synchronized void printMsg(String value, String help1, String help2) {
        Log.log(value, help1, help2);
    }

}
