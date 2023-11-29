package com.example.project_iot.activities.authorisation;

import android.app.Activity;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project_iot.R;
import com.example.project_iot.activities.home.Home;
import com.example.project_iot.database.DatabaseHelperFactory;
import com.example.project_iot.database.IDatabaseHelper;
import com.example.project_iot.database.SQLiteDatabaseHelper;
import com.example.project_iot.utils.DigestUtils;

public class Registration extends AppCompatActivity {
    private static Activity activity;

    private View _bg__rejestracja;
    private View _bg___button_zaloguj_si__ek1;
    private View rectangle_2;
    private TextView zaloguj;
    private View _bg___button_zarejestruj_sie_ek1;
    private View rectangle_3;
    private TextView zarejestruj;
    private TextView rejestracja;
    private ImageView image_2;
    private View _bg__group_4_ek1;
    private View rectangle_1;
    private EditText powtorz_haslo;
    private View _bg__login_ek1;
    private View rectangle_1_ek1;
    private EditText login_ek4;
    private View _bg__haslo_ek1;
    private View rectangle_1_ek2;
    private EditText haslo_ek4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        activity = this;

        _bg__rejestracja = (View) findViewById(R.id._bg__rejestracja);
        _bg___button_zaloguj_si__ek1 = (View) findViewById(R.id._bg___button_zaloguj_si__ek1);
        rectangle_2 = (View) findViewById(R.id.rectangle_2);
        zaloguj = (TextView) findViewById(R.id.zaloguj);
        _bg___button_zarejestruj_sie_ek1 = (View) findViewById(R.id._bg___button_zarejestruj_sie_ek1);
        rectangle_3 = (View) findViewById(R.id.rectangle_3);
        zarejestruj = (TextView) findViewById(R.id.zarejestruj);
        rejestracja = (TextView) findViewById(R.id.rejestracja);
        image_2 = (ImageView) findViewById(R.id.image_2);
        _bg__group_4_ek1 = (View) findViewById(R.id._bg__group_4_ek1);
        rectangle_1 = (View) findViewById(R.id.rectangle_1);
        powtorz_haslo = (EditText) findViewById(R.id.powtorz_haslo);
        _bg__login_ek1 = (View) findViewById(R.id._bg__login_ek1);
        rectangle_1_ek1 = (View) findViewById(R.id.rectangle_1_ek1);
        login_ek4 = (EditText) findViewById(R.id.login_ek4);
        _bg__haslo_ek1 = (View) findViewById(R.id._bg__haslo_ek1);
        rectangle_1_ek2 = (View) findViewById(R.id.rectangle_1_ek2);
        haslo_ek4 = (EditText) findViewById(R.id.haslo_ek4);

        _bg___button_zaloguj_si__ek1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Registration.this, Login.class);
                startActivity(intent);
            }
        });

        _bg___button_zarejestruj_sie_ek1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = login_ek4.getText().toString();
                String password = haslo_ek4.getText().toString();
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
                                            login_ek4.setText("");
                                            haslo_ek4.setText("");
                                            powtorz_haslo.setText("");
                                            openHome();
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
    public void openHome() {
        Intent intent=new Intent(this, Home.class);
        startActivity(intent);
    }
}

