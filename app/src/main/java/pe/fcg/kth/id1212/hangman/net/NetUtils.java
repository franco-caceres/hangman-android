package pe.fcg.kth.id1212.hangman.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

class NetUtils {
    private static final int SIZE_OF_INT = 4;
    private static final int BUFFER_SIZE = SIZE_OF_INT;

    static void sendMessage(OutputStream outputStream, String message) throws IOException {
        if(System.getProperty("test") != null) {
            try {
                Thread.sleep(5000);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        byte[] packagedMessage = getPackagedMessage(message);
        outputStream.write(packagedMessage);
        outputStream.flush();
    }

    static String receiveMessage(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        byte[] messageLengthBytes = new byte[SIZE_OF_INT];
        byte[] tempMessageBytes = new byte[BUFFER_SIZE];
        int idx;
        // read message length first
        int messageLengthBytesRead = 0;
        int messageBytesRead = 0;
        int bytesRead;
        idx = 0;
        while(messageLengthBytesRead < SIZE_OF_INT) {
            bytesRead = inputStream.read(buffer, 0, BUFFER_SIZE);
            if(bytesRead < 0) {
                return null;
            }
            messageLengthBytesRead += bytesRead;
            if(messageLengthBytesRead > SIZE_OF_INT) {
                int extraBytes = messageLengthBytesRead - SIZE_OF_INT;
                int neededBytes = bytesRead - extraBytes;
                for(int i = 0; i < neededBytes; i++) {
                    messageLengthBytes[idx++] = buffer[i];
                }
                // put remaining extra bytes in the message byte array
                messageBytesRead = extraBytes;
                idx = 0;
                for(int i = neededBytes; i < bytesRead; i++) {
                    tempMessageBytes[idx++] = buffer[i];
                }
            } else {
                for(int i = 0; i < bytesRead; i++) {
                    messageLengthBytes[idx++] = buffer[i];
                }
            }
        }
        // then read the actual message
        int messageLength = ByteBuffer.wrap(messageLengthBytes).getInt();
        byte[] fullMessage = new byte[messageLength];
        idx = 0;
        // if we already read some bytes while reading the message length
        if(messageBytesRead > 0) {
            for(int i = 0; i < messageBytesRead; i++) {
                fullMessage[idx++] = tempMessageBytes[i];
            }
        }
        while(messageBytesRead < messageLength) {
            bytesRead = inputStream.read(buffer, 0, BUFFER_SIZE);
            messageBytesRead += bytesRead;
            for(int i = 0; i < bytesRead; i++) {
                fullMessage[idx++] = buffer[i];
            }
        }
        return new String(fullMessage);
    }

    private static byte[] getPackagedMessage(String message) {
        byte[] messageBytes = message.getBytes();
        byte[] messageLengthBytes = intToByteArray(messageBytes.length);
        byte[] packagedMessage = new byte[SIZE_OF_INT + messageBytes.length];
        System.arraycopy(messageLengthBytes, 0, packagedMessage, 0, SIZE_OF_INT);
        System.arraycopy(messageBytes, 0, packagedMessage, SIZE_OF_INT, messageBytes.length);
        return packagedMessage;
    }

    private static byte[] intToByteArray(int number) {
        byte[] arr = new byte[SIZE_OF_INT];
        for(int i = SIZE_OF_INT - 1; i >= 0; i--) {
            arr[i] = (byte) (number & 0b11111111);
            number >>= 8;
        }
        return arr;
    }
}
