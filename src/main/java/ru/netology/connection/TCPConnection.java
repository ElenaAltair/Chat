package ru.netology.connection;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

// TCP (Transmission Control Protocol — протокол управления передачей) — один из основных протоколов передачи данных интернета.
// class TCPConnection - этот класс реализует(описывает) одно TCP соединение
// (Этот класс будет универсальным, мы его будем использовать и в серверной и в клиентской части,
// но, т.к. клиентская и серверная часть должны отрабатывать по разному
// создадим интерфейс TCPConnectionListener)
// (если серверу приходит сообщение, то он рассылает его всем клиентам; если клиенту, то он где-то у себя его отображает)
public class TCPConnection {

    // В нашем TCP соединении должны быть:
    private final Socket socket; // 1) сокет, который с ним связан
    private final Thread thread; // 2) поток, который слушает, входящие соединения (постоянно читает поток ввода, если что-то прилетает, то он генерирует событие)
    private final TCPConnectionListener eventListener; // 3) экземпляр интерфейса TCPConnectionListener

    // 4) потоки ввода вывода (наше TCP соединение будет работать со строками):
    private final BufferedReader in; // а) поток ввода
    private final BufferedWriter out; // б) поток вывода

    // конструктор (сокет создаётся из принимаемого на вход ip-адреса и порта)
    // (этот конструктор для клиента)
    public TCPConnection(TCPConnectionListener eventListener, String ipAddr, int port) throws IOException {
        this(eventListener, new Socket(ipAddr, port));
    }

    // конструктор (на вход, принимающий готовый объект сокета (Socket socket) и с этим сокетом, создающий соединение)
    // (этот конструктор для сервера)
    public TCPConnection(TCPConnectionListener eventListener, Socket socket) throws IOException {

        this.eventListener = eventListener;
        this.socket = socket; // спрашиваем и запоминаем сокет

        // Далее у этого сокета получаем входящий и исходящий поток для того, чтобы принимать какие-то байты и писать какие-то байты
        // делается это двумя методами: getInputStream() и getOutputStream()
        // (мы достаем входящий и исходящий поток с помощью методов getInputStream() и getOutputStream()
        // и оборачиваем их в BufferedReader и BufferedWriter, которые умеют читать и писать строчки)
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));

        // Создаём и запускаем новый поток, который слушает входящие соединения
        thread = new Thread(new Runnable() { // Чтобы этот поток что-то выполнял, надо передать ему экземпляр класса, который реализует интерфейс Runnable
            // переопределяем метод run (в этом методе слушаем, входящие соединения)
            @Override
            public void run() { //
                try {
                    // Когда поток стартовал и соединение готово к использованию
                    eventListener.onConnectionReady(TCPConnection.this);
                    // получаем строчки (делаем это в бесконечном цикле):
                    while (!thread.isInterrupted()) { // пока поток не прерван

                        // методом in.readLine() читаем строку и отдаём её в eventListener
                        eventListener.onReceiveString(TCPConnection.this, in.readLine());
                    }
                } catch (IOException e) {
                    eventListener.onException(TCPConnection.this, e);
                } finally {
                    eventListener.onDisconnect(TCPConnection.this); // по окончании прерываем соединение
                }
            }
        });
        thread.start(); // запускаем поток
    }

    public synchronized void sendString(String value) { // метод отправляющий сообщения (строчки)
        try {
            out.write(value + "\r\n"); // записываем нашу строчку в потоке вывода ( "\r\n" - признак окончания строки (возврат каретки и переход на новую строку))
            out.flush(); // метод flush сбрасывает все буферы и отправляет нашу строчку
        } catch (IOException e) {
            eventListener.onException(TCPConnection.this, e);
            disconnect(); // если строчку не получилось передать, прерываем соединение
        }
    }

    public synchronized void disconnect() { // метод для разрыва соединения, когда нам это необходимо
        thread.interrupt(); // прерываем поток
        try {
            socket.close(); // закрываем сокет
        } catch (IOException e) {
            eventListener.onException(TCPConnection.this, e);
        }
    }

    @Override
    public String toString() {
        return "TCPConnection: " + socket.getInetAddress() + ": " + socket.getPort();
    }
}
