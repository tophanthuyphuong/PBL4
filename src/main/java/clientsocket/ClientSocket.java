package clientsocket;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import dto.ClientIden;

public class ClientSocket {
	private static Map<String, Socket> sessionSockets = new HashMap<String, Socket>();
	private static Map<String, DatagramSocket> sessionDataSockets = new HashMap<String, DatagramSocket>();
	private static Gson gson;
	private final static int timeoutInSeconds = 10 * 60 * 1000;
	
	public static synchronized Socket getInstance(String sessionId, String _email) {
		gson = new Gson();
		DatagramSocket datagramSocket;
		try {
			InetAddress address = InetAddress.getByName("localhost");
			if (!sessionSockets.containsKey(sessionId)) {
				Socket client = new Socket(address, 6969);
				client.setSoTimeout(timeoutInSeconds);
				DataOutputStream dos = new DataOutputStream(client.getOutputStream());
				datagramSocket = new DatagramSocket();
				
				String add = InetAddress.getLocalHost().getHostAddress();
				int port = datagramSocket.getLocalPort();
				ClientIden identity = new ClientIden(_email, add, port);
				identity.setEmail(_email);
				String json = gson.toJson(identity);
				dos.writeUTF(json);
				dos.flush();
				System.out.println("Tạo kết nối mới đến server");
				sessionSockets.put(sessionId, client);
				sessionDataSockets.put(sessionId, datagramSocket);
			} else {
				Socket client = sessionSockets.get(sessionId);
				try {
					DataOutputStream dos = new DataOutputStream(client.getOutputStream());
					dos.writeUTF("JOIN1");
					dos.flush();
					dos.writeUTF("JOIN2");
					dos.flush();
				} catch (IOException ex) {
					disconnectSocket(sessionId);
					client = new Socket(address, 6969);
					client.setSoTimeout(timeoutInSeconds);
					DataOutputStream dos = new DataOutputStream(client.getOutputStream());

					datagramSocket = new DatagramSocket();
					String add = InetAddress.getLocalHost().getHostAddress();
					int port = datagramSocket.getLocalPort();
					ClientIden identity = new ClientIden(_email, add, port);
					identity.setEmail(_email);
					String json = gson.toJson(identity);

					dos.writeUTF(json);
					System.out.println("Kết nối lại đến server");
					sessionSockets.put(sessionId, client);
					sessionDataSockets.put(sessionId, datagramSocket);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Không thể kết nối đến server: " + e.getMessage());
		}


		return sessionSockets.get(sessionId);
	}

	public static DatagramSocket getDatagramSocket(String sessionID) {
		DatagramSocket datagramSocket = sessionDataSockets.get(sessionID);
		return datagramSocket;
	}
	
	public static synchronized void disconnectSocket(String sessionId) {
        Socket socket = sessionSockets.get(sessionId);
        DatagramSocket datagramSocket = sessionDataSockets.get(sessionId);
        if (socket != null) {
            try {
                socket.close();
				datagramSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        sessionSockets.remove(sessionId);
        sessionDataSockets.remove(sessionId);
    }
}