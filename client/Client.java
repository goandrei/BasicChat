package com.andrei.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client implements Runnable {

	//connection details
	private final String IP;
	private final int PORT;
	
	//server endpoint
	private Socket socket;
	
	//user's details
	private String username;
	
	//console reader
	private BufferedReader scanner;
	
	//communication objects
	private PrintWriter out;
	private BufferedReader in;
	
	//buffer
	private String message;
	
	public Client(final String IP, final int PORT) {
		this.IP = IP;
		this.PORT = PORT;
		
		this.initializeConnections();
		
		Thread thr = new Thread(this);
		thr.start();
	}
	
	private void initializeConnections() {
		
		try {
			//connect to the server
			this.socket = new Socket(IP, PORT);
			
			//initialize the connection channels
			this.out = new PrintWriter(this.socket.getOutputStream(), true);
			this.in  = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void run() {
		
		try {
			//get the username from the console
			this.scanner = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Insert your username : ");
			this.username = this.scanner.readLine();			
			
			//send the username to the server
			this.out.println(this.username);

			//while the server is still up
			while(this.socket.isConnected()) {
				//if the users is sending a message
				if(this.scanner.ready() && (this.message = this.scanner.readLine()) != null) {
					//add the user's name to the message
					this.message = this.username + " : " + this.message;
					//send the message
					this.out.println(this.message);
				}
				//if we got a message -> show it
				if(this.in.ready() && (this.message = this.in.readLine()) != null) {
					if(this.message.startsWith(this.username)) {
						continue;
					}
					if(this.message.startsWith("!@!@")) {
						this.message = this.message.substring(4, this.message.length()) + " has connected!";
					}
					System.out.println(this.message);
				}
			}
		} catch (UnknownHostException e) {
			System.out.format("There is no host at IP %s or it is not listening to the PORT %d!", IP, PORT);
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Could not connect!");
			e.printStackTrace();
		}
		finally {
			this.close();
		}
	}

	private void close() {
		try {
			if(this.socket != null && this.socket.isConnected()) {
				this.socket.close();	
			}
			if(this.in != null) {
				this.in.close();	
			}
			if(this.out != null) {
				this.out.close();	
			}
			if(this.scanner != null) {
				this.scanner.close();	
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		
		final String IP = "127.0.0.1";
		final int PORT  = 1234;
		
		new Client(IP, PORT);
	}
}
