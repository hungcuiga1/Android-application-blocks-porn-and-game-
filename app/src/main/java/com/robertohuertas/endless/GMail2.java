package com.robertohuertas.endless;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class GMail2 {

    final String emailPort = "587";// gmail's smtp port
    final String smtpAuth = "true";
    final String starttls = "true";
    final String emailHost = "smtp.gmail.com";


    private String mStoreDir;
    String fromEmail;
    String fromPassword;
    List<String> toEmailList;
    String emailSubject;
    String emailBody;
    String contents;
    Properties emailProperties;
    Session mailSession;
    MimeMessage emailMessage;

    public GMail2() {

    }

    public GMail2(String fromEmail, String fromPassword,
                  List<String> toEmailList, String emailSubject, String emailBody) {
        this.fromEmail = fromEmail;
        this.fromPassword = fromPassword;
        this.toEmailList = toEmailList;
        this.emailSubject = emailSubject;
        this.emailBody = emailBody;

        emailProperties = System.getProperties();
        emailProperties.put("mail.smtp.port", emailPort);
        emailProperties.put("mail.smtp.auth", smtpAuth);
        emailProperties.put("mail.smtp.starttls.enable", starttls);
        Log.i("GMail", "Mail server properties set.");
    }

    public MimeMessage createEmailMessage() throws AddressException,
            MessagingException, UnsupportedEncodingException, FileNotFoundException {

        mailSession = Session.getDefaultInstance(emailProperties, null);
        emailMessage = new MimeMessage(mailSession);


        emailMessage.setFrom(new InternetAddress(fromEmail, "CHẶN"));
        for (String toEmail : toEmailList) {
            Log.i("GMail", "toEmail: " + toEmail);
            emailMessage.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(toEmail));
        }

        emailMessage.setSubject(emailSubject);
        Multipart mp = getAttachments();
//        emailMessage.setContent(multipart);
        emailMessage.setContent(emailBody, "text/html");// for a html email
        // emailMessage.setText(emailBody);// for a text email
        Log.i("GMail", "Email Message created.");
        emailMessage.setContent(mp);
        return emailMessage;
    }

    public void sendEmail() throws AddressException, MessagingException {

        Transport transport = mailSession.getTransport("smtp");
        transport.connect(emailHost, fromEmail, fromPassword);
        Log.i("GMail", "allrecipients: " + emailMessage.getAllRecipients());
        transport.sendMessage(emailMessage, emailMessage.getAllRecipients());
//        transport.send(emailMessage);
        transport.close();
        Log.i("GMail", "Email sent successfully.");
    }
    private Multipart getAttachments() throws FileNotFoundException, MessagingException
    {
//        File externalFilesDir = Environment.getExternalStorageState();
//        mStoreDir = externalFilesDir.getAbsolutePath() + "/MyPDFFolder/"
//        Log.d("ddddddddddddddssssssss","ss"+System.getProperty("user.dir"));
        File folder = new File("/storage/emulated/0/Android/data/com.robertohuertas.endless/files/MyPDFFolder/");
        File[] fileList = folder.listFiles();

        Multipart mp = new MimeMultipart();

        for (File file : fileList)
        {
            Log.d("ddddddddddddddssssssss","sssssssssssss"+file.getName());
                MimeBodyPart messageBodyPart = new MimeBodyPart();
                DataSource datasource = new FileDataSource(file);
                messageBodyPart.setDataHandler(new DataHandler(datasource));
                messageBodyPart.setFileName(file.getName());
                mp.addBodyPart(messageBodyPart);
        }
        return mp;
    }

}