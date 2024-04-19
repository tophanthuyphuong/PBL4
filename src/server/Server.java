package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

public class Server {
	private static List<ServerHandler> listClients;
	private static DatagramSocket datagramSocket;
	public Server(int port) {
		listClients = new ArrayList<ServerHandler>();
		
		try (ServerSocket server = new ServerSocket(port)) {
//			InetAddress address = InetAddress.getByName("192.168.225.190");
//			 ServerSocket server = new ServerSocket(6969,0,address);
//			System.out.println(server.getInetAddress() + " " + server.getLocalPort());
			datagramSocket = new DatagramSocket(9999);
			System.out.println("Wating for connection...");
			while (true) {
				Socket client = server.accept();

				ServerHandler handler = new ServerHandler(client);
				handler.start();
				listClients.add(handler);
				
				listClient();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void removeServerHandler(ServerHandler clientHandler) {
        listClients.remove(clientHandler);
    }
	
	public static void multicastEmail (int mailID, HashMap<String, Integer> mapCorrectMail) {
		if (mapCorrectMail != null) {
			Set<Entry<String, Integer>> entries = mapCorrectMail.entrySet();
			
			for (ServerHandler serverHandler : listClients) {
				for (Entry<String, Integer> entry : entries) {
					if (entry.getKey().equals(serverHandler.getClientEmail())) {
						serverHandler.alertForReceiver(mailID, entry.getValue());
					}
				}
			}
		}	
	}
	
	public static void sendPacket(DatagramPacket packet) {
		try {
			System.out.println(new String(packet.getData()).trim());
			datagramSocket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void listClient() {
			System.out.println("Danh sach client:" + listClients.size());
			for (ServerHandler serverHandler : listClients) {
				System.out.println(serverHandler.getClientEmail());
			}
			System.out.println("---------------");
	}
	
	public static void main(String[] args) {
		int port = 6969;
		new Server(port);
	}
}
