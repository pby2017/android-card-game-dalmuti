package sce2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.rmi.ConnectException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class TcpIpMultichatClient {

	static Scanner sc;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		sc = new Scanner(System.in);
		StringBuilder serverIp = new StringBuilder();
		StringBuilder nickName = new StringBuilder();
		serverIp.delete(0, serverIp.length());
		nickName.delete(0, nickName.length());
		
		while(true)
		{
			System.out.print("input server IP : ");
			try {
				serverIp.append(sc.next());
				break;
			}catch(InputMismatchException ime) {
				sc = new Scanner(System.in);
			}
		}
		
		while(true)
		{
			System.out.print("input nickname : ");
			try {
				nickName.append(sc.next());
				break;
			}catch(InputMismatchException ime) {
				sc = new Scanner(System.in);
			}
		}
		
		try {
			Socket s = new Socket(serverIp.toString(), 7777);
			System.out.println("서버에 연결되었습니다.");
			Thread sender = new Thread(new ClientSender(s, nickName.toString()));
			Thread receiver = new Thread(new ClientReceiver(s));
			
			sender.start();
			receiver.start();
		}catch(ConnectException ce) {
			ce.printStackTrace();
		}catch(Exception e) {}
	}
	
	static class ClientSender extends Thread {
		Socket s;
		DataOutputStream out;
		String name;
		
		ClientSender(Socket s, String name)
		{
			this.s = s;
			try {
				out = new DataOutputStream(s.getOutputStream());
				this.name = name;
			} catch(Exception e) {}
		}
		
		public void run()
		{
			sc = new Scanner(System.in);
			try {
				if(out != null)
				{
					out.writeUTF(name);
				}
				
				while(out != null)
				{
					out.writeUTF("["+name+"]"+sc.nextLine());
				}
			}catch(IOException e) {}
		}
	}
	
	static class ClientReceiver extends Thread {
		Socket s;
		DataInputStream in;
		
		ClientReceiver(Socket s)
		{
			this.s= s;
			try {
				in = new DataInputStream(s.getInputStream());
			}catch(IOException e) {}
		}
		
		public void run()
		{
			while(in != null)
			{
				try {
					System.out.println(in.readUTF());
				}catch(IOException e) {}
			}
		}
	}

}
