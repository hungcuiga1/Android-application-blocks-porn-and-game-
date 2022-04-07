package com.robertohuertas.endless;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

public class forgotpass extends Activity {
    private EditText editText;
    private EditText editText2;
    int xacnhan;
    String Emails;
    private static final String PREFS_KEY_email = "myPrefernces2";
    private static final String PASSWORD_KEY_email = "myPrefernces2";
    String email_ck;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);
        final Button send = (Button) this.findViewById(R.id.button);
        editText = findViewById(R.id.editText);

        final Button send2 = (Button) this.findViewById(R.id.button2);
        editText2 = findViewById(R.id.editText2);

        SharedPreferences sharedPreferences2 = getSharedPreferences(PREFS_KEY_email, MODE_PRIVATE);
        email_ck = sharedPreferences2.getString(PASSWORD_KEY_email, "");

        send.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Log.i("SendMailActivity", "Send Button Clicked.");
                String email = editText.getText().toString();
                String fromEmail = "h801633329596@gmail.com";
                String fromPassword = "hung19199827400";
                Emails = email;
                if (email.equals(email_ck)){
                    List<String> toEmailList = Arrays.asList(Emails
                            .split("\\s*,\\s*"));
                    Log.i("SendMailActivity", "To List: " + toEmailList);
                    String emailSubject = "Vertify Code";
                    int code = (int) Math.floor(((Math.random() * 899999) + 100000));
                    xacnhan = code;
                    String emailBody = "Mã xác nhận của bạn là: "+code;
                    new SendMailTask(forgotpass.this).execute(fromEmail,
                            fromPassword, toEmailList, emailSubject, emailBody);

                }else {
                    Toast.makeText(forgotpass.this, "Wrong email", Toast.LENGTH_SHORT).show();
                }
            }
        });
        send2.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                int text = Integer.parseInt(editText2.getText().toString());
                Log.d("text","="+text);
                Log.d("xacnhan","="+xacnhan);
                if (text == (xacnhan)){
                    SharedPreferences sharedPreferences2 = getSharedPreferences(PREFS_KEY_email,MODE_PRIVATE);
                    SharedPreferences.Editor editor2 = sharedPreferences2.edit();
                    editor2.putString(PASSWORD_KEY_email,Emails);
                    editor2.apply();
                    Intent intent = new Intent(forgotpass.this, CreatePasswordActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Toast.makeText(forgotpass.this, "Wrong email", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
