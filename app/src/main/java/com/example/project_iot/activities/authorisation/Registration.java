package com.example.project_iot.activities.authorisation;

import android.app.Activity;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.project_iot.R;
import com.example.project_iot.activities.main.Menu;
import com.example.project_iot.database.DatabaseHelperFactory;
import com.example.project_iot.database.IDatabaseHelper;
import com.example.project_iot.utils.DigestUtils;

public class Registration extends AppCompatActivity {
    private Activity activity;

    private EditText login;
    private EditText haslo;
    private EditText powtorz_haslo;
    private Button zaloguj;
    private Button zarejestruj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        activity = this;

        login = (EditText) findViewById(R.id.login);
        haslo = (EditText) findViewById(R.id.haslo);
        powtorz_haslo = (EditText) findViewById(R.id.powtorz_haslo);
        zaloguj = (Button) findViewById(R.id.info_zapisz);
        zarejestruj = (Button) findViewById(R.id.zarejestruj);
        zaloguj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Registration.this, Login.class);
                startActivity(intent);
            }
        });

        zarejestruj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = login.getText().toString();
                String password = haslo.getText().toString();
                String confirm_password = powtorz_haslo.getText().toString();

                if(username.equals("") || password.equals("") || confirm_password.equals("")){
                    Toast.makeText(getApplicationContext(), "Zostało puste pole", Toast.LENGTH_SHORT).show();
                } else {
                    if(password.equals(confirm_password)){

                        new Thread() {
                            @Override
                            public void run() {
                                IDatabaseHelper iDatabaseHelper = DatabaseHelperFactory.getMysqlDatabase();
                                if (!iDatabaseHelper.open()){
                                    activity.runOnUiThread(() -> {
                                        Toast.makeText(getApplicationContext(), "Nie udało się połączyć z systemem.", Toast.LENGTH_SHORT).show();
                                    });
                                    return;
                                }

                                Boolean usernameExists = iDatabaseHelper.isUsernameTaken(username);

                                if(!usernameExists){
                                    int userId = iDatabaseHelper.insert(username, DigestUtils.sha256(password));
                                    if(userId > -1){
                                        iDatabaseHelper.close();
                                        activity.runOnUiThread(() -> {
                                            Toast.makeText(getApplicationContext(), "Zarejestrowano i zalogowano", Toast.LENGTH_SHORT).show();
                                            login.setText("");
                                            haslo.setText("");
                                            powtorz_haslo.setText("");
                                            openHome(userId);
                                        });
                                    }
                                }else{
                                    iDatabaseHelper.close();
                                    activity.runOnUiThread(() -> {
                                        Toast.makeText(getApplicationContext(), "Nazwa użytkownika jest już zajęta", Toast.LENGTH_SHORT).show();
                                    });
                                }
                            }
                        }.start();
                    }else{
                        Toast.makeText(getApplicationContext(), "Hasło nie pasuje", Toast.LENGTH_SHORT).show();
                    }

                }
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

