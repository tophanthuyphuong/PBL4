package utils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

public class AuthorizeMail {
	public void authorize(String toEmail, int passcode) {
		 final String fromEmail = "tophanthuyphuong@gmail.com";
	        // Mat khai email cua ban
	        final String password = "vfpe jvnf bkds frqg";
	        // dia chi email nguoi nhan
//	        final String toEmail = "tophanthuyphuong@gmail.com";
	        
	        final String subject = "Xác thực tài khoản MeowMail";
	        final String body = "Mã xác thực tài khoản của bạn là: " + passcode;

	        Properties props = new Properties();
	        props.put("mail.smtp.host", "smtp.gmail.com"); //SMTP Host
	        props.put("mail.smtp.port", "587"); //TLS Port
	        props.put("mail.smtp.auth", "true"); //enable authentication
	        props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS

	        Authenticator auth = new Authenticator() {
	            protected PasswordAuthentication getPasswordAuthentication() {
	                return new PasswordAuthentication(fromEmail, password);
	            }
	        };
	        Session session = Session.getInstance(props, auth);


	        try {
	        	MimeMessage msg = new MimeMessage(session);
		        //set message headers
		        msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
		        msg.addHeader("format", "flowed");
		        msg.addHeader("Content-Transfer-Encoding", "8bit");

		        msg.setFrom(new InternetAddress(fromEmail, "MeowMail"));

		        msg.setReplyTo(InternetAddress.parse(fromEmail, false));

		        msg.setSubject(subject, "UTF-8");

		        msg.setText(body, "UTF-8");

		        msg.setSentDate(new Date());

		        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
		        Transport.send(msg);
	        } catch (Exception e) {
	        	e.printStackTrace();
	        }
	        System.out.println("Gui mail thanh cong");
	}
}
