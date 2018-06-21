import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailNotifications {

	public EmailNotifications() {

	}
	public void sendmail(String to,String msg){
		final String username = "parkinglibhw3@gmail.com";
		final String password = "123321lib";

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props,
		  new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		  });

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("parkinglibhw3@gmail.com"));
			message.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse(to));
			message.setSubject("ParkingLib notification");
			message.setText(msg);

			Transport.send(message);

			System.out.println("Sent notification email to:\""+to+"\"");

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}
}
