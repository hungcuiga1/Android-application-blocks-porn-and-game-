package com.robertohuertas.endless;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class SendMailActivity2 extends Service {
    private EditText editText;
    private EditText editText2;
    int xacnhan;
    String Emails;
    private static final String PREFS_KEY_email = "myPrefernces2";
    private static final String PASSWORD_KEY_email = "myPrefernces2";
    String email_ckx;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Calendar calendar = Calendar.getInstance();
        String myFormat = "dd/MM/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        String current_time=sdf.format(calendar.getTime());
        Log.i("SendMailActivity", "Send Button Clicked.");
        SharedPreferences sharedPreferences2 = getSharedPreferences(PREFS_KEY_email, MODE_PRIVATE);
        email_ckx = sharedPreferences2.getString(PASSWORD_KEY_email, "");
        String fromEmail = "h801633329596@gmail.com";
        String fromPassword = "hung19199827400";
        Emails = email_ckx;
        if (!email_ckx.equals("")){
            List<String> toEmailList = Arrays.asList(Emails
                    .split("\\s*,\\s*"));
            Log.i("SendMailActivity", "To List: " + toEmailList);
            String emailSubject = "Lịch sử ngày"+current_time;
            String emailBody = "Lịch sử ngày hôm nay!";
            new SendMailTask2(SendMailActivity2.this).execute(fromEmail,
                    fromPassword, toEmailList, emailSubject, emailBody);

        }else {
            Toast.makeText(SendMailActivity2.this, "Wrong email",
                    Toast.LENGTH_SHORT).show();

        }
        };
}
