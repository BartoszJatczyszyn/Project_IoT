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
import com.example.project_iot.database.DatabaseHelperFactory;
import com.example.project_iot.database.IDatabaseHelper;
import com.example.project_iot.utils.DigestUtils;

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
        activity = this;

        old_password = findViewById(R.id.old_password);
        new_password = findViewById(R.id.new_password);
        repeat_new_password = findViewById(R.id.repeat_new_password);
        change_password = findViewById(R.id.change_password);

        change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });
    }

    private void changePassword() {
        String old_password = DigestUtils.sha256(this.old_password.getText().toString());
        String new_password = DigestUtils.sha256(this.new_password.getText().toString());
        String repeated_new_pass = DigestUtils.sha256(this.repeat_new_password.getText().toString());

        int id_user = getApplicationContext().getSharedPreferences("ProjectIoTPref", 0)
                .getInt("session_user_id", -1);

        if (old_password.equals("") || new_password.equals("") || repeated_new_pass.equals("")) {
            Toast.makeText(getApplicationContext(), "Zostało puste pole", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!new_password.equals(repeated_new_pass)) {
            Toast.makeText(getApplicationContext(), "Wpisane nowa hasła nie są takie same", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread() {
            public int result;

            @Override
            public void run() {


                IDatabaseHelper iDatabaseHelper = DatabaseHelperFactory.getMysqlDatabase();
                if (!iDatabaseHelper.open()) {
                    activity.runOnUiThread(() -> {
                        Toast.makeText(getApplicationContext(), "Nie udało się połączyć z systemem.", Toast.LENGTH_SHORT).show();
                    });
                    return;
                }

                if (!iDatabaseHelper.isPasswordCorrect(id_user, old_password)) {
                    activity.runOnUiThread(() -> {
                        Toast.makeText(getApplicationContext(), "Stare hasło nie jest poprawne", Toast.LENGTH_SHORT).show();
                    });
                    return;
                }

                this.result = iDatabaseHelper.resetPassword(id_user, new_password);
                if (result == 0) {
                    activity.runOnUiThread(() -> {
                        Toast.makeText(getApplicationContext(), "Wystąpił nieoczekiwany błąd.", Toast.LENGTH_SHORT).show();
                    });
                    return;
                }

                activity.runOnUiThread(() -> {
                    Toast.makeText(getApplicationContext(), "Hasło zostało zmienione!!", Toast.LENGTH_SHORT).show();
                });

                openHome();
            }
        }.start();
    }

    private void openHome() {
        Intent intent = new Intent(Change_Password.this, Menu.class);
        startActivity(intent);
    }
}
