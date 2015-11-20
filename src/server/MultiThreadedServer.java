package server;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.plaf.metal.MetalIconFactory.FolderIcon16;

import java.io.File;
import java.io.IOException;

public class MultiThreadedServer implements Runnable{

    protected int serverPort = 8888;
    protected InetAddress inetAddr;
    protected ServerSocket serverSocket = null;
    protected boolean isStopped = false;
    protected Thread runningThread = null;
    protected String fileToManage = null;
    
    public MultiThreadedServer(int port, String address, String fileToManage){
        this.serverPort = port;
        this.fileToManage = "files" + File.pathSeparator + fileToManage;
        try {
			inetAddr = InetAddress.getByName(address);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
        
    }
    
    public void run(){
        synchronized(this){
            this.runningThread = Thread.currentThread();
        }
        openServerSocket();
        while(! isStopped()){
            Socket clientSocket = null;
            try {
                clientSocket = this.serverSocket.accept();
                System.out.println("Gotta client");
            } catch (IOException e) {
                if(isStopped()) {
                    System.out.println("Server Stopped.") ;
                    return;
                }
                throw new RuntimeException(
                    "Error accepting client connection", e);
            }
            System.out.println("Starting Worker...");
            Thread t1 = new Thread(new WorkerRunnable(clientSocket, "Multithreaded Server", fileToManage));
            t1.start();
            try {
				this.runningThread.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
            this.stop();
            
            
        }
        System.out.println("Server Stopped.") ;
    }


    private synchronized boolean isStopped() {
        return this.isStopped;
    }

    public synchronized void stop(){
        this.isStopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }

    private void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.serverPort, 5, this.inetAddr);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port " + this.serverPort, e);
        }
    }
    
    public static void main(String[] args) {
		//MultiThreadedServer m = new MultiThreadedServer(8888, "localhost");
		//System.out.println("Server Setup");
		//m.run();
		//m.stop();
    	
    	//System.out.println(loader.getResource(""));
	}
}
