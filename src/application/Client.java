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
import java.net.Socket;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Client extends Application {

	DataInputStream fromServer;
	DataOutputStream toServer;
	String name;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		BorderPane mainPane = new BorderPane();
		BorderPane textPane = new BorderPane();
		BorderPane msgPane = new BorderPane();

		// setting up textPane
		Label labelForName = new Label("Enter your name");
		TextField fieldForName = new TextField();
		textPane.setLeft(labelForName);
		textPane.setRight(fieldForName);

		// setting up msgPane
		TextField fieldForMsg = new TextField();
		Button button = new Button("SEND");
		msgPane.setLeft(fieldForMsg);
		msgPane.setRight(button);

		// setting up the mainPane
		mainPane.setTop(textPane);
		TextArea chat = new TextArea();
		mainPane.setCenter(new ScrollPane(chat));
		mainPane.setBottom(msgPane);

		// create a scene and set it on the primary stage
		Scene scene = new Scene(mainPane, 450, 300);
		primaryStage.setTitle("Client");
		primaryStage.setScene(scene);
		primaryStage.show();

		// create a new thread to establish a connection to the server
		new Thread(() -> {
			try {
				// create a socket to connect to the server
				Socket socket = new Socket("localhost", 8000);

				// create input and output streams for the socket
				fromServer = new DataInputStream(socket.getInputStream());
				toServer = new DataOutputStream(socket.getOutputStream());

				// get the user's name
				name = fieldForName.getText();

				// send the user's name to the server
				toServer.writeUTF(name);

				// receive messages from the server and display them in the chat area
				while (true) {
					String message = fromServer.readUTF();
					Platform.runLater(() -> chat.appendText(message + "\n\n"));
				}
			} catch (IOException ex) {
				Platform.runLater(() -> chat.appendText("Error: " + ex.getMessage() + "\n"));
			}
		}).start();

		// send messages to the server when the "SEND" button is clicked
		button.setOnAction(event -> {
			try {
				String message = fieldForName.getText() + ": " + fieldForMsg.getText();
				toServer.writeUTF(message);
				Platform.runLater(() -> chat.appendText(message + "\n\n"));
				fieldForMsg.clear();
			} catch (IOException ex) {
				Platform.runLater(() -> chat.appendText("Error: " + ex.getMessage() + "\n"));
			}
		});
	}
}
