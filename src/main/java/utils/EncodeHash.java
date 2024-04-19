package utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.tomcat.util.codec.binary.Base64;

public class EncodeHash {
	public static String toSHA1(String str) {
		String res = null;
		String salt = "bafsfhskhfh@qwerqur!asdfjssdfjsljf@!!";
		str = str + salt;
		try {
			byte[] dataBytes = str.getBytes("UTF-8");
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			res = Base64.encodeBase64String(md.digest(dataBytes));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return res;
	}
}
