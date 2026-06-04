package com.lims.service;

import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.io.InputStream;
import java.util.Properties;

/**
 * EmailService.java
 *
 * PURPOSE: Sends emails using Jakarta Mail over SMTP.
 * All email-sending in the application goes through this one class.
 *
 * Member 3 will call sendResultReadyEmail().
 * Member 1 (you) calls sendVerificationEmail() during registration.
 *
 * The email settings are read from email.properties — never hardcoded here.
 */
public class EmailService {

    private final Properties mailProps;
    private final String fromAddress;
    private final String password;

    public EmailService() {
        try {
            // Load config from the properties file in resources/
            Properties config = new Properties();
            InputStream input = getClass().getResourceAsStream("/email.properties");
            config.load(input);

            fromAddress = config.getProperty("mail.smtp.username");
            password     = config.getProperty("mail.smtp.password");

            // These are the settings Jakarta Mail needs
            mailProps = new Properties();
            mailProps.put("mail.smtp.host",            config.getProperty("mail.smtp.host"));
            mailProps.put("mail.smtp.port",            config.getProperty("mail.smtp.port"));
            mailProps.put("mail.smtp.auth",            config.getProperty("mail.smtp.auth"));
            mailProps.put("mail.smtp.starttls.enable", config.getProperty("mail.smtp.starttls.enable"));

        } catch (Exception e) {
            throw new RuntimeException("Failed to load email config: " + e.getMessage(), e);
        }
    }

    /**
     * Sends a generic email. All specific email methods below call this.
     *
     * @param toAddress   recipient's email
     * @param subject     email subject line
     * @param htmlBody    the email body as HTML (supports bold, links, etc.)
     */
    public void sendEmail(String toAddress, String subject, String htmlBody) {
        // Authenticator provides credentials to the mail server
        Session session = Session.getInstance(mailProps, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromAddress, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromAddress));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toAddress));
            message.setSubject(subject);
            message.setContent(htmlBody, "text/html; charset=utf-8");

            Transport.send(message);

        } catch (MessagingException e) {
            // We log but don't crash the app if email fails
            System.err.println("Email send failed to " + toAddress + ": " + e.getMessage());
        }
    }

    /**
     * Sends the email verification link to a newly registered customer.
     * The token is a unique code tied to their account — clicking the link
     * will call markEmailVerified() in UserDAO.
     *
     * (For simplicity in this project, the "link" can just be a code the
     * user types in — full URL-based verification requires a web server.)
     */
    public void sendVerificationEmail(String toAddress, String name, String verificationCode) {
        String subject = "Verify your Santé Diagnostics account";
        String body = """
                <h2>Welcome to Santé Diagnostics, %s!</h2>
                <p>Your email verification code is:</p>
                <h1 style="letter-spacing: 4px;">%s</h1>
                <p>Enter this code in the app to activate your account.</p>
                """.formatted(name, verificationCode);

        sendEmail(toAddress, subject, body);
    }

    /**
     * Notifies a customer that their test result is ready to view.
     * Called by Member 3's result-validation flow after Lab Attendant validates.
     */
    public void sendResultReadyEmail(String toAddress, String name, String testName) {
        String subject = "Your " + testName + " result is ready";
        String body = """
                <h2>Hello %s,</h2>
                <p>Your <strong>%s</strong> test result has been validated and is now
                available in your dashboard.</p>
                <p>Log in to Santé Diagnostics to view and download your result.</p>
                """.formatted(name, testName);

        sendEmail(toAddress, subject, body);
    }
}
