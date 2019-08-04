package com.andrei.communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import com.andrei.server.Server;

public class Communication implements Runnable{
	
	private Server server;
	
	//the client endpoint
	private Socket clientSocket;
	
	//communication objects
	private PrintWriter out;
	private BufferedReader in;
		
	//list of the users connected to the chat room
	private List<Communication> connectedUsers;
	
	//user details
	private String username;
	
	//buffer
	private String message;
	
	public Communication(final Socket clientSocket, final List<Communication> connectedUsers, final Server server) {
		this.clientSocket = clientSocket;
		this.connectedUsers = connectedUsers;
		this.server = server;
		this.createCommunicationChannels();
		
		//start the thread
		Thread thr = new Thread(this);
		thr.start();
	}

	private void createCommunicationChannels(){
		try {
			this.out = new PrintWriter(this.clientSocket.getOutputStream(), true);
			this.in  = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		
		try {			
			//read the username from the client
			this.username = this.in.readLine();
			
			//notify user's presence to other clients
			//pad it with some less used combination(not a good solution)
			this.spreadMessage("!@!@" + this.username);
			
			while(this.clientSocket.isConnected()) {
				if(this.in.ready() && (this.message = this.in.readLine()) != null) { 
					if(this.message.startsWith("!@!@")) {
						this.updateClientsList();
					} 
					
					this.spreadMessage(this.message);	
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			this.close();
		}
	}
	
	private void updateClientsList() {
		this.connectedUsers = this.server.getCommunications();
		this.connectedUsers.remove(this);
	}
	
	private void spreadMessage(final String message) {
		
		//send the message to each user
		for(Communication user : this.connectedUsers) {
			user.sendMessage(message);
		}
	}
	
	public void sendMessage(final String message) {
		this.out.println(message);
	}
	
	private void close() {
		try {
			if(this.out != null) {
				out.close();	
			}
			if(this.in != null) {
				in.close();	
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
