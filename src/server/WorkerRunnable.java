package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

import Utils.HttpCodes;

/**

 */
public class WorkerRunnable implements Runnable{

    protected Socket clientSocket = null;
    protected String serverText = null;
    protected String fileToManage = null;
    private OutputStream output = null;
    private InputStream input = null;

    public WorkerRunnable(Socket clientSocket, String serverText, String fileToManage) {
        this.clientSocket = clientSocket;
        this.serverText = serverText;
        this.fileToManage = fileToManage;
    }
    
    public void Sync(){
    	
    }

    public void run() {
        try {
            input  = clientSocket.getInputStream();
            output = clientSocket.getOutputStream();
            long time = System.currentTimeMillis();
            
            output.write(("HTTP/1.1 200 OK\n\nWorkerRunnable: " +
this.serverText + " - " +
time +
": ").getBytes());
            System.out.println("Thingy Sent");
            String cmd = null;
            while(cmd.equals("") || cmd.equalsIgnoreCase("quit") || cmd.equalsIgnoreCase("q")){
            	cmd = cmd.toUpperCase();
            	switch(cmd){
            	case HttpCodes.COMMANDS.SNYC: 
            		System.out.println("Sync Requested");
            		break;
            	case HttpCodes.COMMANDS.UPLOAD: 
            		System.out.println("Upload Requested");
            		break;
            	case HttpCodes.COMMANDS.CLOSE:
            		System.out.println("Kill the connection");
            	}
            }
            
            output.close();
            input.close();
            System.out.println("Request processed: " + time);
        } catch (IOException e) {
            //report exception somewhere.
            e.printStackTrace();
        }
    }
    
    private String getClientCommandRequest(){
    	Scanner s = new Scanner(input).useDelimiter("\\A");
    	String request = s.hasNext() ? s.next() : "";
    	String[] elements = request.split(" ");
    	if(elements[0].equals(HttpCodes.HEADER)){
    		return elements[1];
    	} else  {
    		return "CLOSE";
    	}
    	
    }
}