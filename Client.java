import java.io.*;
import java.net.*;

class TextClient {
    public static void main(String argv[]) throws Exception {
        String username;
        String password;
        String messageRecipient;
        String messageBody;
        Boolean loggedIn = false;
        Integer loginResponse;
        Integer checkValue;
        Integer numberOfMessages;
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
                            loggedIn = true;
                            break;
                        }
                    }
                    System.out.println("-----------------" + '\n');
                    break;
                case "1":
                    System.out.println("System Users");
                    Integer numUsers = inFromServer.read();
                    String currName;
                    for (int i = 0; i < numUsers; i++) {
                        currName = inFromServer.readLine();
                        System.out.println(currName);
                    }
                    System.out.println("-----------------" + '\n');
                    break;
                case "2":
                    if (loggedIn) {
                        System.out.print("Who would you like to send a message to: ");
                        messageRecipient = inFromUser.readLine();
                        outToServer.writeBytes(messageRecipient + '\n');
                        checkValue = inFromServer.read();
                        if (checkValue == 0) {
                            System.out.println("Invalid Recipient Entered");
                            break;
                        }
                        System.out.print("Enter the message: ");
                        messageBody = inFromUser.readLine();
                        outToServer.writeBytes(messageBody + '\n');
                        System.out.println("Message sent succesfully");
                        System.out.println("-------------------");
                        break;
                    } else {
                        System.out.println("Please Connect to the Server before sending a message");
                        break;
                    }

                case "3":
                    if (loggedIn) {
                        numberOfMessages = inFromServer.read();
                        for (int i = 0; i < numberOfMessages; i++) {
                            System.out.println(inFromServer.readLine());
                        }
                        break;
                    } else {
                        System.out.println("Please Connect to the Server before Getting messages");
                        break;
                    }
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
