package com.example.project_iot.activities.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.project_iot.R;
import com.example.project_iot.activities.authorisation.Login;
import com.example.project_iot.activities.authorisation.Registration;

public class Change_Password extends AppCompatActivity {
    private static Activity activity;
    private EditText old_password;
    private EditText new_password;
    private EditText repeat_new_password;
    private Button change_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        old_password = (EditText) findViewById(R.id.old_password);
        new_password = (EditText) findViewById(R.id.new_password);
        repeat_new_password = (EditText) findViewById(R.id.repeat_new_password);
        change_password = (Button) findViewById(R.id.change_password);

        change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(changePassword()){
                    Intent intent = new Intent(Change_Password.this, Menu.class);
                    startActivity(intent);
                }
            }
        });
    }

    private boolean changePassword() {
        String old_password = this.old_password.getText().toString();
        String new_password = this.new_password.getText().toString();
        String repeated_new_pass = this.repeat_new_password.getText().toString();
        if (old_password.equals("") || new_password.equals("") || repeated_new_pass.equals("")){
            Toast.makeText(getApplicationContext(), "Zostało puste pole", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!new_password.equals(repeated_new_pass)){
            Toast.makeText(getApplicationContext(), "Wpisane nowa hasła nie są takie same", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
