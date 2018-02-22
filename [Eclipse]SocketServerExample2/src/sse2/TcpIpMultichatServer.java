package sse2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

public class TcpIpMultichatServer {

	HashMap<String, DataOutputStream> clients;
	
	TcpIpMultichatServer()
	{
		clients = new HashMap<>();
		Collections.synchronizedMap(clients);
	}
	
	public void start()
	{
		ServerSocket ss = null;
		Socket s = null;
		
		try {
			ss = new ServerSocket(7777);
			System.out.println("서버가 시작되었습니다.");
			
			while(true)
			{
				s = ss.accept();
				System.out.println("["+s.getInetAddress()+":"+s.getPort()+"]"+"에서 접속하였습니다.");
				ServerReceiver thread = new ServerReceiver(s);
				thread.start();
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	void sendToAll(String msg)
	{
		Iterator<String> it = clients.keySet().iterator();
		while(it.hasNext())
		{
			try {
				DataOutputStream out = (DataOutputStream)clients.get((it.next()));
				out.writeUTF(msg);
			}catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		new TcpIpMultichatServer().start();
	}
	
	class ServerReceiver extends Thread {
		Socket s;
		DataInputStream in;
		DataOutputStream out;
		
		ServerReceiver(Socket s)
		{
			this.s=s;
			try {
				in = new DataInputStream(s.getInputStream());
				out = new DataOutputStream(s.getOutputStream());
			}catch(IOException e) {
				e.printStackTrace();
			}
		}
		
		public void run()
		{
			String name = "";
			try {
				name = in.readUTF();
				sendToAll("#"+name+"님이 들어오셨습니다.");
				sendToAll("CMD_JOIN");
				
				clients.put(name, out);
				System.out.println("현재 서버 접속자 수는 "+clients.size()+"입니다.");
				while(in!=null)
				{
					sendToAll(in.readUTF());
				}
			}catch(IOException e) {
				
			}finally {
				clients.remove(name);
				sendToAll("#"+name+"님이 나가셨습니다.");
				System.out.println("["+s.getInetAddress()+":"+s.getPort()+"]"+"에서 접속을 종료하였습니다.");
				System.out.println("현재 서버 접속자 수는 "+clients.size()+"입니다.");
			}
		}
	}
}
