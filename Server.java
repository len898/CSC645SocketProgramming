import java.io.*;
import java.net.*;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;

class TextServer {
    public static void main(String argv[]) throws Exception {
        Dictionary<String, String> logins = new Hashtable<>();
        Dictionary<String, List<String>> messages = new Hashtable<>();
        logins.put("Lennart", "Password");
        messages.put("Lennart", new ArrayList<String>());
        logins.put("Alice", "1234");
        messages.put("Alice", new ArrayList<String>());
        logins.put("Bob", "5678");
        messages.put("Bob", new ArrayList<String>());
        ServerSocket welcomeSocket = new ServerSocket(8000);
        System.out.println("SERVER is running ... ");

        while (true) {
            Socket connectionSocket = welcomeSocket.accept();
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
            Boolean loggedIn = false;
            String clientUsername = null;
            String clientPassword;
            String option;

            while (true) {
                System.out.println("waiting...");

                option = inFromClient.readLine();
                System.out.println("Option is: " + option);
                switch (option) {
                    case "0":
                        clientUsername = inFromClient.readLine();
                        clientPassword = inFromClient.readLine();
                        if (logins.get(clientUsername) != null
                                && logins.get(clientUsername).equals(clientPassword)) {
                            outToClient.writeByte(1);
                            loggedIn = true;
                        } else {
                            outToClient.writeByte(0);
                        }
                        break;
                    case "1":
                        Integer numUsers = logins.size();
                        outToClient.writeByte(numUsers);
                        Enumeration<String> enu = logins.keys();
                        while (enu.hasMoreElements()) {
                            System.out.println("In the loop");
                            outToClient.writeBytes(enu.nextElement() + '\n');
                        }
                        break;
                    case "2":
                        if (loggedIn) {
                            String messageRecipient = inFromClient.readLine();
                            // Need to check whether the recipient is valid
                            if (logins.get(messageRecipient) == null) {
                                outToClient.writeByte(0);
                                break;
                            } else {
                                outToClient.writeByte(1);
                            }
                            String messageContent = inFromClient.readLine();
                            messages.get(messageRecipient).add(clientUsername + ": " + messageContent);
                            System.out.println("Made it to the end");
                        }
                        break;
                    case "3":
                        if(loggedIn){
                            List<String> userMessages = messages.get(clientUsername);
                            //Let the client know how many messages to expect.
                            outToClient.writeByte(userMessages.size());
                            Iterator<String> iter = userMessages.listIterator();
                            while(iter.hasNext()){
                                outToClient.writeBytes(iter.next() + "\n");
                            }
                            break;
                        }
                        break;
                    case "4":
                        connectionSocket.close();
                        break;
                }
                if (option.equals("4")) {
                    break;
                }
            }
        }
    }
}
