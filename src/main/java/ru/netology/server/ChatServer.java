package ru.netology.server;

import ru.netology.connection.TCPConnection; // class TCPConnection - этот класс реализует(описывает) одно TCP соединение
import ru.netology.connection.TCPConnectionListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

import static ru.netology.log.Log.log;

// Сервер - сущность:
// 1) принимающая входящие соединения,
// 2) держащая сколько-то входящих соединений активными
// 3) и рассылающая сообщения, т.е. принимает сообщения от пользователей и рассылает их другим пользователям

// В java для взаимодействия с сетью существует два основных класса:
// 1) ServerSocket (умеющий слушать какой-то порт, принимать входящее соединение,
// создавать объект сокета, который связан с этим соединением и готовый сокет нам отдавать
// 2) и Socket (с помощью этого класса можно устанавливать соединение)
public class ChatServer implements TCPConnectionListener {

    public final ArrayList<TCPConnection> connections = new ArrayList<>(); // здесь храним список соединений наших клиентов

    // конструктор
    public ChatServer(int port) {

        System.out.println("Server running...");
        try (ServerSocket serverSocket = new ServerSocket(port)) { // ServerSocket (класс, умеющий слушать какой-то порт и принимающий, входящее соединение)
            // в бесконечном цикле наш сервер будет слушать и принимать входящие соединения
            while (true) {
                try {
                    // при подключении каждого нового клиента создаём новое TCP соединение
                    new TCPConnection(this, serverSocket.accept()); // как только новое соединение установилось метод accept() возвращает объект сокета
                } catch (IOException e) { // отлавливаются исключения, при подключении клиентов
                    System.out.println("TCPConnection exception: " + e);
                    log("TCPConnection exception: " + e, "", " (class: ChatServer)");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized void onConnectionReady(TCPConnection tcpConnection) { // соединение готово к использованию / произошло подключение нового клиента
        connections.add(tcpConnection); // добавили новое соединение в список соединений клиентов
        try {
            // отправили сообщение всем клиентам о том, что клиент установил соединение
            sendToAllConnections("Client connected: " + tcpConnection, "", " (class: ChatServer / method: onConnectionReady)");
        } catch (IOException e) {
            System.out.println("TCPConnection exception: " + e);
            log("TCPConnection exception: " + e, "", " (class: ChatServer / method: onConnectionReady)");
        }
    }

    @Override
    public synchronized void onReceiveString(TCPConnection tcpConnection, String value) { // мы приняли строчку
        try {
            // отправили всем клиентам строчку, переданную клиентом
            sendToAllConnections(value, "Client: " + tcpConnection + " send string: ", " (class: ChatServer / method: onReceiveString)");
        } catch (IOException e) {
            System.out.println("TCPConnection exception: " + e);
            log("TCPConnection exception: " + e, "", " (class: ChatServer / method: onReceiveString)");
        }
    }

    @Override
    public synchronized void onDisconnect(TCPConnection tcpConnection) { // клиент прервал соединение
        connections.remove(tcpConnection); // удалили из списка соединений клиентов, соединение клиента, прервавшего соединение

        try {
            // отправили сообщение всем клиентам о том, что клиент прервал соединение
            sendToAllConnections("Client disconnected: " + tcpConnection, "", " (class: ChatServer / method: onDisconnect)");
        } catch (IOException e) {
            System.out.println("TCPConnection exception: " + e);
            log("TCPConnection exception: " + e, "", " (class: ChatServer / method: onDisconnect)");
        }
    }

    @Override
    public synchronized void onException(TCPConnection tcpConnection, Exception e) { // произошло какое-то исключение
        System.out.println("TCPConnection exception: " + e);
        log("TCPConnection exception: " + e, "", " (class: ChatServer / method: onException)");
    }

    private synchronized void sendToAllConnections(String value, String help1, String help2) throws IOException {
        System.out.println(value);

        // передали сообщение всем клиентам
        for (TCPConnection connection : connections) connection.sendString(value);

        // это метод моего класса Log, который записывает новою строчку в файл File.log,
        // расположенного по адресу src/main/java/ru/netology/resources/File.log
        log(value, help1, help2);
    }

}
