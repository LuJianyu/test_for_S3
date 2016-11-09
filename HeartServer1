import java.io.*;
import java.net.*;
import java.util.*;


public class HeartServer {
    
    List<Socket> clients = new ArrayList<Socket>();
    
    
    public static void main(String[] args) {
        new HeartServer().start();
    }
    
    
    public void start(){
    	ListenThread lt = new ListenThread(clients);
    	new Thread(lt).start();
        try {
            boolean iConnect = false;
            ServerSocket ss = new ServerSocket(7720);
            iConnect = true;
            while(iConnect){
System.out.println("setup successful!");
                Socket s = ss.accept();
                
System.out.println("client found!");
                clients.add(s);
                
System.out.println("client is started!");
            }
        } catch (IOException e) {
            System.out.println("IOException");
            e.printStackTrace();
        }
    }
    
   
    class ListenThread implements Runnable {
        
        
        private DataInputStream dis;
        private DataOutputStream dos;
        private BufferedReader stdIn;
                
        private String str;
		private List<Socket> clients;
        
       
        ListenThread(List<Socket> clients){
            this.clients = clients;
        }
        
        public void run(){
System.out.println("run！");
            try {
                
               
System.out.println("waiting...");

			//dos = new DataOutputStream(this.s.getOutputStream());
			//dis = new DataInputStream(s.getInputStream());
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
	                	str = sArray[2];
	                	System.out.println(i);
	                	System.out.println(str);
	                	Socket s = clients.get(i);
	                	dos = new DataOutputStream(s.getOutputStream());
	        			dis = new DataInputStream(s.getInputStream());
	        			dos.writeUTF(str);
	        			dos.flush();
   
	                }
                }
                    
            }    
            } catch (IOException e) {
                e.printStackTrace();
            }
            
        }
        
        
    }
    
}
