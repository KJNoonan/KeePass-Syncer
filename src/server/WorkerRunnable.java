package server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Scanner;

import Utils.HttpUtils;

/**

 */
public class WorkerRunnable implements Runnable{

    protected Socket clientSocket = null;
    protected String serverText = null;
    protected File fileToManage = null;
    private OutputStream output = null;
    private InputStream input = null;
    private ClassLoader loader = null;
    private long sizeOfFile;

    public WorkerRunnable(Socket clientSocket, String serverText, String fileToManage) {
        this.clientSocket = clientSocket;
        this.serverText = serverText;
        loader = ClassLoader.getSystemClassLoader();
        try {
			this.fileToManage = new File(loader.getResource(fileToManage).toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
        if(fileToManage == null){
        	//Fatal Error Goes here we can't do anything without a file
        }
    }
    
    private String Sync() throws IOException{
    	String msg = HttpUtils.HEADER + " " + HttpUtils.STATUSES.OK + " " + HttpUtils.NOUNS.NUMBYTES + sizeOfFile;
    	output.write(msg.getBytes());
    	Scanner s = new Scanner(input).useDelimiter("\\A");
    	String request = s.hasNext() ? s.next() : "";
    	s.close();
    	String[] elements = request.split(" ");
    	if(elements[0].equals(HttpUtils.HEADER)) {
    		
    		return "";
    	} else {
    		return "CLOSE";
    	}
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
            	case HttpUtils.COMMANDS.SNYC: 
            		System.out.println("Sync Requested");
            		break;
            	case HttpUtils.COMMANDS.UPLOAD: 
            		System.out.println("Upload Requested");
            		break;
            	case HttpUtils.COMMANDS.CLOSE:
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
    	s.close();
    	String[] elements = request.split(" ");
    	if(elements[0].equals(HttpUtils.HEADER)){
    		sizeOfFile = fileToManage.length();
    		return elements[1];
    	} else  {
    		return "CLOSE";
    	}
    	
    }
}