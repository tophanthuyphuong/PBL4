package controller;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import clientsocket.ClientSocket;

@ServerEndpoint("/websocketendpoint")
public class WebSocketEndpoint {
	private boolean isConnectionOpen = false;

	@OnOpen
	public void onOpen(Session session) {
		System.out.println("Open Connection ...");
		isConnectionOpen = true;
	}

	@OnClose
	public void onClose(Session session) {
		System.out.println("Close Connection ...");
		isConnectionOpen = false;
	}

	@OnMessage
	public void onMessage(String sessionId, Session session) {
		System.out.println("Received session ID from client: " + sessionId);
		handleConnection(sessionId, session);
	}

	public void onError(Throwable t) {
		// Handle error
	}

	private void handleConnection(String sessionId, final Session session) {
		int count = 0;
		final DatagramSocket datagramSocket = ClientSocket.getDatagramSocket(sessionId);
		if (datagramSocket != null) {
			try {
				datagramSocket.setSoTimeout(1000); // 1 seconds timeout
			} catch (Exception e) {
				e.printStackTrace();
			}

			Runnable task = new Runnable() {
				@Override
				public void run() {
					while (isConnectionOpen && datagramSocket != null) {
						try {
							byte[] receive = new byte[1024];
							DatagramPacket packet = new DatagramPacket(receive, receive.length);
							// Đợi và nhận dữ liệu từ DatagramSocket
							datagramSocket.receive(packet);
							String receivedData = new String(packet.getData(), 0, packet.getLength(),
									StandardCharsets.UTF_8).trim();
							session.getBasicRemote().sendText(receivedData);
						} catch (SocketTimeoutException e) {
							// Khong lam gi ca, de cho no chay sang vong lap khac
						} catch (IOException e) {
//							e.printStackTrace();
							break;
						}
					}
				}
			};

			Thread newThread = new Thread(task); // Tạo một luồng mới với Runnable đã xác định
			newThread.start(); // Khởi động luồng mới

		}
	}
}