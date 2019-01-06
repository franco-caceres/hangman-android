package pe.fcg.kth.id1212.hangman.net;

public interface RawMessageHandler {
    void handleIncoming(String rawMessage);
    void handleLostConnection();
}
