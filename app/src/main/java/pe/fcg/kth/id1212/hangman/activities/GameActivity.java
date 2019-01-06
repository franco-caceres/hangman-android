package pe.fcg.kth.id1212.hangman.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;

import pe.fcg.kth.id1212.hangman.R;
import pe.fcg.kth.id1212.hangman.model.GameStateMessage;
import pe.fcg.kth.id1212.hangman.model.Message;
import pe.fcg.kth.id1212.hangman.net.RawMessageHandler;
import pe.fcg.kth.id1212.hangman.net.ServerEndpoint;

public class GameActivity extends AppCompatActivity implements RawMessageHandler {
    private ServerEndpoint serverEndpoint = new ServerEndpoint();
    private ConstraintLayout layoutPlaying;
    private ConstraintLayout layoutResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        setUpActivity();
    }

    private void setUpActivity() {
        Intent intent = getIntent();
        String address = intent.getStringExtra(MainActivity.SERVER_ADDRESS);
        Integer port = intent.getIntExtra(MainActivity.SERVER_PORT, 0);
        if(address == null && !serverEndpoint.isConnected()) {
            finish();
            return;
        }
        new ConnectToServerTask().execute(address, port.toString());
        layoutPlaying = findViewById(R.id.layoutPlaying);
        layoutResult = findViewById(R.id.layoutResult);
    }

    public void guess(View view) {
        boolean ok = true;
        EditText editTextGuess = findViewById(R.id.editTextGuess);
        if(editTextGuess.getText().toString().equalsIgnoreCase("")) {
            editTextGuess.setError(getString(R.string.error_required));
            ok = false;
        }
        if(ok) {
            Message message = new Message(Message.Type.GUESS, editTextGuess.getText().toString().toLowerCase());
            new SendMessageTask().execute(message);
        }
    }

    public void keepPlaying(View view) {
        Message message = new Message(Message.Type.STARTNEW);
        new SendMessageTask().execute(message);
    }

    public void disconnect(View view) throws IOException {
        serverEndpoint.disconnect();
        finish();
    }

    @Override
    public void handleIncoming(String rawMessage) {
        GameStateMessage gsm = new GameStateMessage(new Message(rawMessage));
        runOnUiThread(() -> handleIncomingGameState(gsm));
    }

    private void handleIncomingGameState(GameStateMessage gsm) {
        TextView textViewScore = findViewById(R.id.textViewScore);
        textViewScore.setText(String.valueOf(gsm.getScore()));
        switch(gsm.getStatus()) {
            case WIN:
                handleWin(gsm);
                break;
            case LOSS:
                handleLoss(gsm);
                break;
            case PLAYING:
                handlePlaying(gsm);
                break;
        }

    }

    private void handleWin(GameStateMessage gsm) {
        if(layoutResult.getVisibility() != ConstraintLayout.VISIBLE) {
            layoutResult.setVisibility(ConstraintLayout.VISIBLE);
            layoutPlaying.setVisibility(ConstraintLayout.INVISIBLE);
        }
        TextView textViewResult = findViewById(R.id.textViewResult);
        textViewResult.setText(getString(R.string.message_you_won));
    }

    private void handleLoss(GameStateMessage gsm) {
        if(layoutResult.getVisibility() != ConstraintLayout.VISIBLE) {
            layoutResult.setVisibility(ConstraintLayout.VISIBLE);
            layoutPlaying.setVisibility(ConstraintLayout.INVISIBLE);
        }
        TextView textViewResult = findViewById(R.id.textViewResult);
        textViewResult.setText(getString(R.string.message_you_lost));
    }

    private void handlePlaying(GameStateMessage gsm) {
        if(layoutPlaying.getVisibility() != ConstraintLayout.VISIBLE) {
            layoutPlaying.setVisibility(ConstraintLayout.VISIBLE);
            layoutResult.setVisibility(ConstraintLayout.INVISIBLE);
        }
        EditText editTextGuess = findViewById(R.id.editTextGuess);
        editTextGuess.setText("");
        TextView textViewRemainingAttempts = findViewById(R.id.textViewRemainingAttempts);
        textViewRemainingAttempts.setText(String.valueOf(gsm.getRemainingAttempts()));
        TextView textViewGuessedSoFar = findViewById(R.id.textViewGuessedSoFar);
        textViewGuessedSoFar.setText(gsm.getGuessedSoFar());
    }

    @Override
    public void handleLostConnection() {
        runOnUiThread(() -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(GameActivity.this);
            alertDialogBuilder.setMessage(getString(R.string.message_server_disconnected))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.button_ok), (dialog, id) -> GameActivity.this.finish());
            alertDialogBuilder.create().show();
        });
    }

    private class ConnectToServerTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            String address = strings[0];
            Integer port = Integer.valueOf(strings[1]);
            try {
                serverEndpoint.connect(address, port, GameActivity.this);
            } catch (IOException e) {
                runOnUiThread(() -> {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(GameActivity.this);
                    alertDialogBuilder.setMessage(getString(R.string.message_could_not_connect))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.button_ok), (dialog, id) -> GameActivity.this.finish());
                    alertDialogBuilder.create().show();
                });
            }
            return null;
        }

        protected void onPostExecute(Void v) {
            layoutPlaying.setVisibility(ConstraintLayout.VISIBLE);
        }
    }

    private class SendMessageTask extends AsyncTask<Message, Void, Void> {

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Void doInBackground(Message... messages) {
            Message message = messages[0];
            try {
                serverEndpoint.sendMessage(message.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
