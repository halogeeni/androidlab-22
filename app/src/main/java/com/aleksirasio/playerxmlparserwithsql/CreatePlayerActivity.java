package com.aleksirasio.playerxmlparserwithsql;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class CreatePlayerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_player);
    }

    public void submitPlayer(View view) {
        EditText playerName = (EditText) findViewById(R.id.playerName);
        EditText playerNumber = (EditText) findViewById(R.id.playerNumber);

        String name = playerName.getText().toString().trim();
        String numberInput = playerNumber.getText().toString().trim();

        if(name.length() > 0 && numberInput.length() > 0) {
            int number;

            try {
                number = Integer.parseInt(numberInput);
                Intent createPlayerIntent = new Intent();
                Player p = new Player(name, number);
                createPlayerIntent.putExtra("newPlayer", p);
                setResult(Activity.RESULT_OK, createPlayerIntent);
                finish();
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Number parsing failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
