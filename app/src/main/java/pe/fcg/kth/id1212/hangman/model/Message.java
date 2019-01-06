package pe.fcg.kth.id1212.hangman.model;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.StringJoiner;

public class Message {
    public enum Type {
        STARTNEW,
        NEWGAMEINFO,
        GUESS,
        RESULT,
        CONNECTIONLOST
    }

    private static final String MESSAGE_DELIMITER = "#";
    public static final int GUESS_POS = 0;
    public static final int SCORE_POS = 0;
    public static final int STATUS_POS = 1;
    public static final int REMAINING_ATTEMPTS_POS = 2;
    public static final int GUESSED_SO_FAR_POS = 3;
    public static final int WORD_LENGTH_POS = 4;
    private Type type;
    private String[] content;

    public Message(Type type, String... content) {
        this.type = type;
        this.content = content;
    }

    public Message(String message) {
        parse(message);
    }

    private void parse(String message) {
        if(message == null) {
            type = Type.CONNECTIONLOST;
        } else {
            String[] parts = message.split(MESSAGE_DELIMITER);
            type = Type.valueOf(parts[0]);
            if(parts.length > 1) {
                content = new String[parts.length - 1];
                System.arraycopy(parts, 1, content, 0, parts.length - 1);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public String toString() {
        StringJoiner stringJoiner = new StringJoiner(MESSAGE_DELIMITER);
        stringJoiner.add(type.toString());
        for(String part : content) {
            stringJoiner.add(part);
        }
        return stringJoiner.toString();
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String[] getContent() {
        return content;
    }

    public void setContent(String[] content) {
        this.content = content;
    }
}
