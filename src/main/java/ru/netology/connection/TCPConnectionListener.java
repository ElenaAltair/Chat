package ru.netology.connection;

// Класс TCPConnection будет универсальным, мы его будем использовать и в серверной и в клиентской части,
// но, т.к. клиентская и серверная часть должны отрабатывать по разному
// создадим интерфейс TCPConnectionListener
// (Напоминаем class TCPConnection - этот класс реализует(описывает) одно TCP соединение)
public interface TCPConnectionListener {

    // В нашем соединении могут возникнуть, следующии ситуации:
    void onConnectionReady(TCPConnection tcpConnection);// 1) соединение готово к использованию (произошло подключение нового клиента)

    void onReceiveString(TCPConnection tcpConnection, String value); // 2) мы приняли строчку

    void onDisconnect(TCPConnection tcpConnection); // 3) (клиент прервал соединение)

    void onException(TCPConnection tcpConnection, Exception e); // 4) произошло какое-то исключение
}
