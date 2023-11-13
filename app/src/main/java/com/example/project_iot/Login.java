package com.example.project_iot;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends AppCompatActivity {
    Button btn_register, btn_login;
    EditText txt_username, txt_password;

    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        databaseHelper = new DatabaseHelper(this);

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
                String password = txt_password.getText().toString();

                Boolean checkLogin = databaseHelper.CheckLogin(username, password);

                if(checkLogin == true){
                    Toast.makeText(getApplicationContext(), "Zalogowano", Toast.LENGTH_SHORT).show();
                    openHome();

                }else{
                    Toast.makeText(getApplicationContext(), "Nieprawidłowe dane", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void openHome() {
        Intent intent=new Intent(this, Home.class);
        startActivity(intent);
    }
}