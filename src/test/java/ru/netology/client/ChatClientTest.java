package ru.netology.client;

import org.junit.jupiter.api.Test;
import ru.netology.connection.TCPConnection;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ChatClientTest {

    @Test
    void onConnectionReady() {

        String exc = "";
        ChatClient chatClient = new ChatClient();
        try {
           chatClient.onConnectionReady(new TCPConnection(chatClient,"localhost", 9999));
        } catch (IOException e) {
            exc = "Исключение: " + e;
        }
        assertEquals("Исключение: java.net.ConnectException: Connection refused: connect", exc);

    }


}