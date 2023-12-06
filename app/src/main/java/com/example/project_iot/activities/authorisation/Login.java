package com.example.project_iot.activities.authorisation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.project_iot.R;
import com.example.project_iot.activities.home.Home;
import com.example.project_iot.database.DatabaseHelperFactory;
import com.example.project_iot.database.IDatabaseHelper;
import com.example.project_iot.database.SQLiteDatabaseHelper;
import com.example.project_iot.utils.DigestUtils;

import java.io.IOException;
import java.util.Vector;

public class Login extends AppCompatActivity {

    private static Activity activity;

    Button btn_register, btn_login;
    EditText txt_username, txt_password;

    com.example.project_iot.database.SQLiteDatabaseHelper SQLiteDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = this;

        setContentView(R.layout.activity_login);

        SQLiteDatabaseHelper = new SQLiteDatabaseHelper(this);

        txt_username = (EditText)findViewById(R.id.et_lusername);
        txt_password = (EditText)findViewById(R.id.et_lpassword);

        btn_login = (Button)findViewById(R.id.btn_llogin);
        btn_register = (Button)findViewById(R.id.btn_lregister);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Registration.class);
                startActivity(intent);
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = txt_username.getText().toString();
                String password = DigestUtils.sha256(txt_password.getText().toString());

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
        activity = null;
        Intent intent=new Intent(getBaseContext(), Home.class);
        intent.putExtra("USER_ID", userId);
        startActivity(intent);
    }
}