import java.io.*;
import java.net.*;
import java.util.*;


public class HeartServer {
    
    List<ClientThread> clients = new ArrayList<ClientThread>();
    
    
    public static void main(String[] args) {
        new HeartServer().start();
    }
    
    
    public void start(){
        try {
            boolean iConnect = false;
            ServerSocket ss = new ServerSocket(7720);
            iConnect = true;
            while(iConnect){
System.out.println("setup successful!");
                Socket s = ss.accept();
                ClientThread currentClient = new ClientThread(s);
System.out.println("client found!");
                clients.add(currentClient);
                new Thread(currentClient).start();
System.out.println("client is started!");
            }
        } catch (IOException e) {
            System.out.println("IOException");
            e.printStackTrace();
        }
    }
    
   
    class ClientThread implements Runnable {
        
        private Socket s;
        private DataInputStream dis;
        private DataOutputStream dos;
        private BufferedReader stdIn;
                
        private String str;
        
       
        ClientThread(Socket s){
            this.s = s;
        }
        
        public void run(){
System.out.println("run！");
            try {
                
               
System.out.println("waiting...");

			dos = new DataOutputStream(this.s.getOutputStream());
			dis = new DataInputStream(s.getInputStream());
			stdIn =new BufferedReader(new InputStreamReader(System.in));
                    
			String userInput;
            while (true) {
                if((userInput = stdIn.readLine()) != null){
	                System.out.println("Sever successfully sent the Command: " + userInput);
	                String[] sArray = userInput.split(" ");
	                if(sArray.length == 0) ;
	                else if(sArray[0].equals("exit")) System.exit(0);
	                else if(sArray[0].equalsIgnoreCase("client")){
	                	int i = Integer.parseInt(sArray[1]);
	                	System.out.println(i);
	                	ClientThread c = clients.get(i);
	                	str = sArray[2];
	                	System.out.println(str);
	                    c.sendMsg(str);
	                }
                }
                    
            }    
            } catch (IOException e) {
                e.printStackTrace();
            }
            
        }
        
        
        public void sendMsg(String str){
            try {
System.out.println("building...");
				dos = new DataOutputStream(this.s.getOutputStream());
System.out.println("sending...");
                dos.writeUTF(str);
System.out.println("successful...");  
				dos.flush();

            } catch (IOException e) {
                e.printStackTrace();
            }
            
        }
        
    }
    
}