package ru.netology.connection;

import org.junit.jupiter.api.Test;
import ru.netology.client.ChatClient;
import ru.netology.server.ChatServer;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class TCPConnectionTest {

    @Test
    void tcpConnectionTest() {
        String exc = "";

        try {
            new TCPConnection(new ChatClient(), "localhost", 9999);
        } catch (IOException e) {
            exc = String.valueOf(e);
        }
        assertEquals("java.net.ConnectException: Connection refused: connect", exc);
    }


}