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
import com.example.project_iot.activities.home.Home;
import com.example.project_iot.database.DatabaseHelperFactory;
import com.example.project_iot.database.IDatabaseHelper;
import com.example.project_iot.database.SQLiteDatabaseHelper;
import com.example.project_iot.utils.DigestUtils;

public class Registration extends AppCompatActivity {
    private static Activity activity;

    EditText et_username, et_password, et_cpassword;
    Button btn_register, btn_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        activity = this;

        et_username = (EditText)findViewById(R.id.et_username);
        et_password = (EditText)findViewById(R.id.et_password);
        et_cpassword = (EditText)findViewById(R.id.et_cpassword);
        btn_register = (Button)findViewById(R.id.btn_register);
        btn_login = (Button)findViewById(R.id.btn_login);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Registration.this, Login.class);
                startActivity(intent);
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = et_username.getText().toString();
                String password = et_password.getText().toString();
                String confirm_password = et_cpassword.getText().toString();

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
                                            et_username.setText("");
                                            et_password.setText("");
                                            et_cpassword.setText("");
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
        activity = null;
        Intent intent=new Intent(getBaseContext(), Home.class);
        intent.putExtra("USER_ID", userId);
        startActivity(intent);
    }
}

