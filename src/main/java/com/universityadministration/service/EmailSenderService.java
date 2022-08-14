package com.universityadministration.service;


import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Properties;

public class EmailSenderService {

    public void sendEmail(String confirmationToken, String sendTo) {
        final String username = "khalilkhalaf404@gmail.com";
        final String password = "Asasasas";

        Properties props = new Properties();
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            String contentMessage = "To confirm your account, please click here : "
                    + "http://localhost:8081/auth/confirm-account?token="
                    + confirmationToken;
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(sendTo));
            message.setSubject("Complete Registration!");
            message.setText(contentMessage);

            //Transport.send(message);
            System.out.println(contentMessage);

        } catch (MessagingException e) {
            System.out.println("Exception : " + e);
            throw new RuntimeException(e);
        }
    }
}