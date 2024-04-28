import java.io.*;
import java.net.*;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;

class ClientServerConnection extends Thread {
    Socket connectionSocket;
    BufferedReader inFromClient;
    DataOutputStream outToClient;
    Boolean loggedIn = false;
    String clientUsername = null;
    String clientPassword;
    String option;

    public ClientServerConnection(Socket passedSocket) throws Exception {
        connectionSocket = passedSocket;
        inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
        outToClient = new DataOutputStream(connectionSocket.getOutputStream());
    }

    public void run() {
        try {
            while (true) {
                System.out.println("waiting...");

                option = inFromClient.readLine();
                System.out.println("Option is: " + option);
                switch (option) {
                    case "0":
                        clientUsername = inFromClient.readLine();
                        clientPassword = inFromClient.readLine();
                        if (TextServer.logins.get(clientUsername) != null
                                && TextServer.logins.get(clientUsername).equals(clientPassword)) {
                            outToClient.writeByte(1);
                            loggedIn = true;
                        } else {
                            outToClient.writeByte(0);
                        }
                        break;
                    case "1":
                        Integer numUsers = TextServer.logins.size();
                        outToClient.writeByte(numUsers);
                        Enumeration<String> enu = TextServer.logins.keys();
                        while (enu.hasMoreElements()) {
                            System.out.println("In the loop");
                            outToClient.writeBytes(enu.nextElement() + '\n');
                        }
                        break;
                    case "2":
                        if (loggedIn) {
                            String messageRecipient = inFromClient.readLine();
                            // Need to check whether the recipient is valid
                            if (TextServer.logins.get(messageRecipient) == null) {
                                outToClient.writeByte(0);
                                break;
                            } else {
                                outToClient.writeByte(1);
                            }
                            String messageContent = inFromClient.readLine();
                            TextServer.messages.get(messageRecipient).add(clientUsername + ": " + messageContent);
                            System.out.println("Made it to the end");
                        }
                        break;
                    case "3":
                        if (loggedIn) {
                            List<String> userMessages = TextServer.messages.get(clientUsername);
                            // Let the client know how many messages to expect.
                            outToClient.writeByte(userMessages.size());
                            Iterator<String> iter = userMessages.listIterator();
                            while (iter.hasNext()) {
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
        } catch (Exception e) {
            System.out.println("Multithreading Exception");
        }
    }
}

class TextServer {
    public static Dictionary<String, String> logins = new Hashtable<>();
    public static Dictionary<String, List<String>> messages = new Hashtable<>();

    public static void main(String argv[]) throws Exception {

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
            ClientServerConnection conn = new ClientServerConnection(connectionSocket);
            conn.start();
        }
    }
}
