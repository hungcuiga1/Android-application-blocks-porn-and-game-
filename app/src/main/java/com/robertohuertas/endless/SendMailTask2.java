package com.robertohuertas.endless;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.Service;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

public class SendMailTask2 extends AsyncTask {

    private ProgressDialog statusDialog;
    private Service sendMailActivity;

    public SendMailTask2(Service activity) {
        sendMailActivity = activity;

    }

    protected void onPreExecute() {
//        statusDialog = new ProgressDialog(sendMailActivity);
//        statusDialog.setMessage("Getting ready...");
//        statusDialog.setIndeterminate(false);
//        statusDialog.setCancelable(false);
//        statusDialog.show();
    }

    @Override
    protected Object doInBackground(Object... args) {
        try {
            Log.i("SendMailTask", "About to instantiate GMail...");
//            publishProgress("Processing input....");
            GMail2 androidEmail = new GMail2(args[0].toString(),
                    args[1].toString(), (List) args[2], args[3].toString(),
                    args[4].toString());
//            publishProgress("Preparing mail message....");
            androidEmail.createEmailMessage();
//            publishProgress("Sending email....");
            androidEmail.sendEmail();
//            publishProgress("Email Sent.");
            Log.d("SendMailTask", "Mail Sentttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttt.");
        } catch (Exception e) {
//            publishProgress(e.getMessage());
            Log.e("SendMailTask", e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void onProgressUpdate(Object... values) {
//        statusDialog.setMessage(values[0].toString());

    }

    @Override
    public void onPostExecute(Object result) {
//        statusDialog.dismiss();
    }

}
