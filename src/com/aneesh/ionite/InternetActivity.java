package com.aneesh.ionite;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class InternetActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.internet);

        Button but = (Button) (findViewById(R.id.button));
        but.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click

                Intent intent = new Intent(InternetActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        Log.d("New Avt", "stuf");
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(InternetActivity.this);
        dlgAlert.setMessage(
                "You are not connected to the internet. Connect to a cellular network or WiFi and click 'Try Again'.");
        dlgAlert.setTitle("No Connection!");
        dlgAlert.setPositiveButton("OK", null);
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();

    }

}
