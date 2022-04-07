package com.robertohuertas.endless;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CreatePasswordActivity extends AppCompatActivity {

    EditText password, confirmPassword;
    Button buttonLogin;

    private static final String PREFS_KEY = "myPrefernces";
    private static final String PASSWORD_KEY = "myPrefernces";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_password);

        password = findViewById(R.id.edt_password);
        confirmPassword = findViewById(R.id.edt_con_password);
        buttonLogin = findViewById(R.id.btn_confirm);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text1 = password.getText().toString().trim();
                String text2 = confirmPassword.getText().toString().trim();

                if(text1.equals("") || text2.equals("")){
                    Toast.makeText(CreatePasswordActivity.this, "No password entered", Toast.LENGTH_SHORT).show();
                } else {
                    if(text1.equals(text2)){
                        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_KEY,MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(PASSWORD_KEY,text1);
                        editor.apply();

                        Intent intent = new Intent(CreatePasswordActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(CreatePasswordActivity.this, "Password not match", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }
}
