package pe.fcg.kth.id1212.hangman.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import pe.fcg.kth.id1212.hangman.R;

public class MainActivity extends AppCompatActivity {
    public static final String SERVER_ADDRESS = "SERVER_ADDRESS";
    public static final String SERVER_PORT = "SERVER_PORT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void connect(View view) {
        boolean ok = true;
        EditText editTextAddress = findViewById(R.id.editTextAddress);
        if(editTextAddress.getText().toString().equalsIgnoreCase("")) {
            editTextAddress.setError("Required");
            ok = false;
        }
        EditText editTextPort = findViewById(R.id.editTextPort);
        if(editTextPort.getText().toString().equalsIgnoreCase("")) {
            editTextPort.setError("Required");
            ok = false;
        }
        if(ok) {
            Intent intent = new Intent(this, GameActivity.class);
            String address = editTextAddress.getText().toString();
            Integer port = Integer.valueOf(editTextPort.getText().toString());
            intent.putExtra(SERVER_ADDRESS, address);
            intent.putExtra(SERVER_PORT, port);
            startActivity(intent);
        }
    }
}
