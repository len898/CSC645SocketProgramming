import java.io.*;
import java.net.*;

class TextClient {
    public static void main(String argv[]) throws Exception {
        String username;
        String password;
        String serverResponse;
        Integer loginResponse;
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        Socket clientSocket = new Socket("127.0.0.1", 8000);
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        while (true) {
            System.out.println("0. Connect to the Server");
            System.out.println("1. Get user list");
            System.out.println("2. Send a message");
            System.out.println("3. Get my messages");
            System.out.println("4. Exit");
            System.out.print("Enter a number: ");

            String option = inFromUser.readLine();
            outToServer.writeBytes(option + "\n");

            switch (option) {

                case "0":
                    while (true) {
                        System.out.print("Enter your username: ");
                        username = inFromUser.readLine();
                        System.out.print("Enter your Password: ");
                        password = inFromUser.readLine();
                        outToServer.writeBytes(username + '\n');
                        outToServer.writeBytes(password + '\n');
                        loginResponse = inFromServer.read();
                        if (loginResponse == 0) {
                            System.out.println("Access Denied - Username/Password Incorrect");
                            System.out.println("---------------------------------------------");
                            outToServer.writeBytes(option + '\n');
                        } else {
                            System.out.println("Access Granted");
                            break;
                        }
                    }
                    System.out.println("-----------------");
                    break;
                case "1":
                    System.out.println("System Users");
                    Integer numUsers = inFromServer.read();
                    String currName;
                    for (int i = 0; i < numUsers; i++) {
                        currName = inFromServer.readLine();
                        System.out.println(currName);
                    }
                    System.out.println("-----------------");
                    break;
                case "2":
                    
                    break;

                case "3":
                    clientSocket.close();
                    break;

                case "4":
                    clientSocket.close();
                    break;
            }
            if (option.equals("4")) {
                break;
            }
        }
    }
}
