import java.io.*;
import java.net.*;
import java.util.Hashtable;
import java.util.Dictionary;
import java.util.Enumeration;

class TextServer {
    public static void main(String argv[]) throws Exception {
        String clientUsername;
        String clientPassword;
        Dictionary<String, String> logins = new Hashtable<>();
        logins.put("Lennart", "Password");
        logins.put("Alice", "1234");
        logins.put("Bob", "5678");
        String capitalizedSentence;
        ServerSocket welcomeSocket = new ServerSocket(8000);
        System.out.println("SERVER is running ... ");

        while (true) {
            Socket connectionSocket = welcomeSocket.accept();
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
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
                        } else {
                            outToClient.writeByte(0);
                        }
                        break;
                    case "1":
                        Integer numUsers = logins.size();
                        outToClient.writeByte(numUsers);
                        Enumeration<String> enu = logins.keys();
                        while(enu.hasMoreElements()){
                            System.out.println("In the loop");
                            outToClient.writeBytes(enu.nextElement() + '\n');
                        }
                        break;
                    case "4":
                        // connectionSocket.close();
                        break;
                }
                if (option.equals("4")) {
                    break;
                }
            }
        }
    }
}
