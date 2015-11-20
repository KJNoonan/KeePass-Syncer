package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class TestClient {
	public static void main(String[] args) {
		try {
			Socket me = new Socket("localhost", 8080);
			PrintWriter out  =  new PrintWriter(me.getOutputStream(), true);
			BufferedReader in =  new BufferedReader(new InputStreamReader(me.getInputStream()));
			
			String userInput = null;
			System.out.println("Sending SYNC command");
			out.print("SYNC");
			System.out.println("Sending UPLOAD command");
			out.println("UPLOAD");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
