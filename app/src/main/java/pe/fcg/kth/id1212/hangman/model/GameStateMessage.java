package pe.fcg.kth.id1212.hangman.model;

public class GameStateMessage {
    public enum Status {WIN, LOSS, PLAYING}
    private int score;
    private Status status;
    private int remainingAttempts;
    private String guessedSoFar;
    private int wordLength;

    public GameStateMessage(Message message) {
        String[] content = message.getContent();
        score = Integer.valueOf(content[Message.SCORE_POS]);
        status = Status.valueOf(content[Message.STATUS_POS]);
        remainingAttempts = Integer.valueOf(content[Message.REMAINING_ATTEMPTS_POS]);
        guessedSoFar = content[Message.GUESSED_SO_FAR_POS];
        wordLength = Integer.valueOf(content[Message.WORD_LENGTH_POS]);
    }

    public int getScore() {
        return score;
    }

    public Status getStatus() {
        return status;
    }

    public int getRemainingAttempts() {
        return remainingAttempts;
    }

    public String getGuessedSoFar() {
        char[] wordCharacters = new char[wordLength];
        for(int i = 0; i < wordLength; i++) {
            wordCharacters[i] = '_';
        }
        if(guessedSoFar.length() > 0) {
            String[] guessedLetters = guessedSoFar.split(",");
            for(int i = 0; i < guessedLetters.length; i += 2) {
                char letter = guessedLetters[i].charAt(0);
                int position = Integer.valueOf(guessedLetters[i+1]);
                wordCharacters[position] = letter;
            }
        }
        StringBuilder sb = new StringBuilder();
        for(char c : wordCharacters) {
            sb.append(c);
            sb.append(" ");
        }
        return sb.toString();
    }
}
