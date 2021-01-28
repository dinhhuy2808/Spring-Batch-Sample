package com.batch.service;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SendEmail {

	@Value("${hostEmail}")
	private String hostEmail;

	@Value("${hostEmailPassword}")
	private String hostEmailPassword;

	@Value("${adminEmail}")
	private String adminEmail;
	
	@Value("${appPassword}")
	private String appPassword;

	public void sendEmail(String content) throws AddressException, MessagingException {
		Properties prop = new Properties();
		prop.put("mail.smtp.auth", true);
		prop.put("mail.smtp.starttls.enable", "true");
		prop.put("mail.smtp.host", "smtp.gmail.com");
		prop.put("mail.smtp.port", "587");

		Session session = getSession(prop);
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(hostEmail));
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(adminEmail));
		message.setSubject("Upload Successfully");

		String msg = content;

		MimeBodyPart mimeBodyPart = new MimeBodyPart();
		mimeBodyPart.setContent(msg, "text/html");

		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(mimeBodyPart);

		message.setContent(multipart);

		Transport.send(message);
	}
	
	private Session getSession(Properties prop) {
		return Session.getInstance(prop, new Authenticator() {
		    @Override
		    protected PasswordAuthentication getPasswordAuthentication() {
		        return new PasswordAuthentication(hostEmail, appPassword);
		    }
		});
	}
}
