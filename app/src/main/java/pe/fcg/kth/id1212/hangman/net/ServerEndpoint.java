package pe.fcg.kth.id1212.hangman.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ServerEndpoint {
    private static final int SO_TIMEOUT = 1_800_000;
    private Socket serverSocket;
    private boolean connected = false;

    public void connect(String host, int port, RawMessageHandler rawMessageHandler) throws IOException {
        serverSocket = new Socket();
        serverSocket.connect(new InetSocketAddress(host, port), 500);
        serverSocket.setSoTimeout(SO_TIMEOUT);
        connected = true;
        new Thread(new Listener(rawMessageHandler)).start();
    }

    public void disconnect() throws IOException {
        serverSocket.close();
        serverSocket = null;
        connected = false;
    }

    public boolean isConnected() {
        return connected;
    }

    public void sendMessage(String message) throws IOException {
        NetUtils.sendMessage(serverSocket.getOutputStream(), message);
    }

    private class Listener implements Runnable {
        private final RawMessageHandler rawMessageHandler;

        Listener(RawMessageHandler rawMessageHandler) {
            this.rawMessageHandler = rawMessageHandler;
        }

        @Override
        public void run() {
            try {
                while(connected) {
                    String receivedMessage = receiveMessage();
                    rawMessageHandler.handleIncoming(receivedMessage);
                }
            } catch(IOException ioe) {
                if(connected) {
                    rawMessageHandler.handleLostConnection();
                }
            }
        }

        private String receiveMessage() throws IOException {
            return NetUtils.receiveMessage(serverSocket.getInputStream());
        }
    }
}
