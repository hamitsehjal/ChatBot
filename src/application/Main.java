/**********************************************
Workshop #
Course:JAC 444
Last Name:SEHJAL
First Name:HAMIT
ID:139238208
Section:ZAA
This assignment represents my own work in accordance with Seneca Academic Policy.
Signature
Date:2023-04-14
**********************************************/

package application;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {
	static ArrayList<ClientHandler> clients = new ArrayList<>();
	TextArea ta = new TextArea();

	@Override
	public void start(Stage primaryStage) {
		try {
			BorderPane root = new BorderPane(ta);
			Scene scene = new Scene(root, 400, 400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setTitle("Server");

			primaryStage.show();

			new Thread(() -> {
				try {

					// create a server socket
					ServerSocket serverSocket = new ServerSocket(8000);
					Platform.runLater(() -> ta.appendText("MultiThreadServer started at " + new Date() + "\n\n"));

					while (true) {
						// connect to client1
						Socket socket = serverSocket.accept();

						InetAddress address = socket.getInetAddress();
						Platform.runLater(() -> {
							ta.appendText("Connection from socket[address: " + address.getHostAddress() + " Port: "
									+ socket.getPort() + " LocalPort: " + socket.getLocalPort() + "] at " + new Date()
									+ "\n\n");
						});

						DataInputStream input = new DataInputStream(socket.getInputStream());
						DataOutputStream output = new DataOutputStream(socket.getOutputStream());

						ClientHandler client = new ClientHandler(socket, input, output);
						clients.add(client);

						Thread t = new Thread(client);
						t.start();
					}

				} catch (IOException err) {
					Platform.runLater(() -> ta.appendText(err.toString()));

				}
			}).start();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public class ClientHandler implements Runnable {
		Socket socket;
		DataInputStream input;
		DataOutputStream output;

		public ClientHandler(Socket socket, DataInputStream input, DataOutputStream output) {
			this.socket = socket;
			this.input = input;
			this.output = output;
		}

		@Override
		public void run() {

			try {
				while (true) {
					String msg = input.readUTF();
					System.out.println(msg);

					for (ClientHandler client : Main.clients) {
						if (client != this) {
							client.output.writeUTF(msg);
						}
					}
				}
			} catch (IOException err) {
				Platform.runLater(() -> ta.appendText("Error from server: " + err.toString() + "\n\n"));

			}

		}

	}

	public static void main(String[] args) {
		launch(args);
	}
}
