

import java.io.*;
import java.net.*;

public class HeartClient implements Runnable {
	private static DataOutputStream dos;
	private static DataInputStream dis;
	private static Socket s;
	private static boolean closed = false;
    public static void main(String[] args) throws IOException {

        if (args.length != 2) {
            System.err.println(
                "Usage: java HeartClient <host name> <port number>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);
        
        /*
        try {
                Socket s = new Socket("localhost", portNumber);
                    dos = new DataOutputStream(s.getOutputStream());
            		dis = new DataInputStream(s.getInputStream());
            		
        }catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
        } catch (IOException e) {
          System.err.println("Couldn't get I/O for the connection to the host "
              + hostName);
        } 
        
        if (s != null && dis != null && dos != null) {
            try {

             
              new Thread(new HeartClient()).start();
              while (!closed) {
                System.out.println(dis.readUTF());
              }
              
              dos.close();
              dis.close();
              s.close();
            } catch (IOException e) {
              System.err.println("IOException:  " + e);
            }}
          }
        
        
        public void run() {
            
            String userInput;
            try {
              while ((userInput = dis.readUTF()) != null) {
            	  //userInput = dis.readUTF();
                  System.out.println("Sever sent the String: " + userInput);
                  if (userInput.equals("exit")){ System.exit(0);}
                  else if (userInput.equals("run")){ Process p = Runtime.getRuntime().exec("javac hello.java");}
                  else {
                  System.out.println("There is no such command!(pls type 'HELP' to check the available command)");
                  }
              }
              closed = true;
            } catch (IOException e) {
            	System.err.println("IOException:  " + e);
            
            }
        */
        try (
            Socket s = new Socket("localhost", portNumber);
        		DataOutputStream dos = new DataOutputStream(s.getOutputStream());
        		DataInputStream dis = new DataInputStream(s.getInputStream());
        		
        ) {
        	//System.out.println("Sever sent the String: " + dis.readUTF());
            String userInput;
            //while ((userInput = dis.readUTF()) != null) {
            while(true){
            	userInput = dis.readUTF();
                System.out.println("Sever sent the String: " + userInput);
                if (userInput.equals("exit")){ System.exit(0);}
                else if (userInput.equals("run")){
                    File dir = new File("/home/ubuntu/incubator-singa/examples/cifar10/");
                    Process p = Runtime.getRuntime().exec("./run.sh",null,dir);}

                else {
                System.out.println("There is no such command!(pls type 'HELP' to check the available command)");
                }

            }

        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                hostName);
            System.exit(1);
        }
        
    }
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
}                                