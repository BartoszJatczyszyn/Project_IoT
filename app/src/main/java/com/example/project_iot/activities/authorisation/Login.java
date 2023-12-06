package com.example.project_iot.activities.authorisation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
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

public class Login extends AppCompatActivity {

    private static Activity activity;

    private View _bg__logowanie_ek2;
    private View rectangle_1;
    private EditText login_ek4;
    private View rectangle_1_ek1;
    private EditText haslo_ek4;
    private View _bg__button_zaloguj_si_;
    private View rectangle_2;
    private TextView zaloguj;
    private View _bg__button_zarejestruj_sie;
    private View rectangle_3;
    private TextView zarejestruj;
    private TextView logowanie_ek3;
    private ImageView image_1;

    com.example.project_iot.database.SQLiteDatabaseHelper SQLiteDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        activity = this;


        SQLiteDatabaseHelper = new SQLiteDatabaseHelper(this);

        _bg__logowanie_ek2 = (View) findViewById(R.id._bg__logowanie_ek2);
        rectangle_1 = (View) findViewById(R.id.rectangle_1);
        login_ek4 = (EditText) findViewById(R.id.login_ek4);
        rectangle_1_ek1 = (View) findViewById(R.id.rectangle_1_ek1);
        haslo_ek4 = (EditText) findViewById(R.id.haslo_ek4);
        _bg__button_zaloguj_si_ = (View) findViewById(R.id._bg__button_zaloguj_si_);
        rectangle_2 = (View) findViewById(R.id.rectangle_2);
        zaloguj = (TextView) findViewById(R.id.zaloguj);
        _bg__button_zarejestruj_sie = (View) findViewById(R.id._bg__button_zarejestruj_sie);
        rectangle_3 = (View) findViewById(R.id.rectangle_3);
        zarejestruj = (TextView) findViewById(R.id.zarejestruj);
        logowanie_ek3 = (TextView) findViewById(R.id.logowanie_ek3);
        image_1 = (ImageView) findViewById(R.id.image_1);

        _bg__button_zarejestruj_sie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Registration.class);
                startActivity(intent);
            }
        });

        _bg__button_zaloguj_si_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = login_ek4.getText().toString();
                String password = DigestUtils.sha256(haslo_ek4.getText().toString());

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

                        Boolean checkLogin = iDatabaseHelper.checkLogin(username, password);

                        if (checkLogin == true) {
                            iDatabaseHelper.close();
                            activity.runOnUiThread(() -> {
                                Toast.makeText(getApplicationContext(), "Zalogowano", Toast.LENGTH_SHORT).show();
                                openHome();
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
    public void openHome() {
        activity = null;
        Intent intent=new Intent(this, Home.class);
        startActivity(intent);
    }
}