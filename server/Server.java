package com.andrei.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.andrei.communication.Communication;

public class Server implements Runnable{

	//connection details
	private final int PORT;
	
	//connection objects
	private ServerSocket serverSocket;
	private Socket clientSocket;
	
	//communication with the client
	private List<Communication> communications;
	
	public Server(final int PORT) {
		this.PORT = PORT;
		this.communications = new ArrayList<Communication>();
		
		Thread thr = new Thread(this);
		thr.start();
	}
	
	public List<Communication> getCommunications() {
		return this.communications;
	}
	
	@Override
	public void run() {
		
		try {
			//create the server socket
			serverSocket = new ServerSocket(this.PORT);
			
			System.out.println("The server is up and waiting!");
			
			while(true) {
				clientSocket = serverSocket.accept();
				
				//a connection has been established
				System.out.println("New user connected!");
				Communication communicationChannel = new Communication(this.clientSocket, this.communications, this);
				this.communications.add(communicationChannel);
			}
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if(this.serverSocket != null && !this.serverSocket.isClosed()) {
					this.serverSocket.close();	
				}
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) { 
		
		final int PORT = 1234;
		new Server(PORT);
	}
}

