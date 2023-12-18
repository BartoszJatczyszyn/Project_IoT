package com.example.project_iot.activities.authorisation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.project_iot.R;
import com.example.project_iot.activities.main.Menu;
import com.example.project_iot.database.DatabaseHelperFactory;
import com.example.project_iot.database.IDatabaseHelper;
import com.example.project_iot.database.SQLiteDatabaseHelper;
import com.example.project_iot.utils.DigestUtils;

public class Login extends AppCompatActivity {

    private Activity activity;

    private EditText login;
    private EditText haslo;
    private Button zaloguj;
    private Button zarejestruj;


    com.example.project_iot.database.SQLiteDatabaseHelper SQLiteDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = this;

        setContentView(R.layout.activity_login);

        SQLiteDatabaseHelper = new SQLiteDatabaseHelper(this);

        login = (EditText) findViewById(R.id.login);
        haslo = (EditText) findViewById(R.id.haslo);
        zaloguj = (Button) findViewById(R.id.info_zapisz);
        zarejestruj = (Button) findViewById(R.id.zarejestruj);

        zarejestruj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Registration.class);
                startActivity(intent);
            }
        });

        zaloguj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = login.getText().toString();
                String password = DigestUtils.sha256(haslo.getText().toString());

                new Thread() {
                    @Override
                    public void run() {

                        IDatabaseHelper iDatabaseHelper = DatabaseHelperFactory.getMysqlDatabase();
                        if (!iDatabaseHelper.open()) {
                            activity.runOnUiThread(() -> {
                                Toast.makeText(getApplicationContext(), "Nie udało się połączyć z systemem.", Toast.LENGTH_SHORT).show();
                            });
                            return;
                        }

                        int userId = iDatabaseHelper.authoriseUser(username, password);

                        if (userId >= 0) {

                            iDatabaseHelper.close();
                            activity.runOnUiThread(() -> {
                                Toast.makeText(getApplicationContext(), "Zalogowano", Toast.LENGTH_SHORT).show();
                                openHome(userId);
                            });

                        } else {
                            iDatabaseHelper.close();
                            activity.runOnUiThread(() -> {
                                Toast.makeText(getApplicationContext(), "Nieprawidłowe dane", Toast.LENGTH_SHORT).show();
                            });
                        }

                    }
                }.start();
            }
        });
    }
    public void openHome(int userId) {

        getApplicationContext().getSharedPreferences("ProjectIoTPref", 0)
                .edit().putInt("session_user_id", userId).commit();

        Intent intent=new Intent(getBaseContext(), Menu.class);
        startActivity(intent);
    }
}