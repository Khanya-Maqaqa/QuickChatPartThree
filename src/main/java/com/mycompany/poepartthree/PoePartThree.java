
package com.mycompany.poepartthree;

import java.util.Scanner;
/**
 * Main Class handles all method execution and calling
 * @author kvmaq
 */
public class PoePartThree {

    public static void main(String[] args) {
        
        Scanner s = new Scanner(System.in);
        
        //User Registration
        System.out.println("===============================");
        System.out.println(" CREATE ACCOUNT - USER DETAILS");
        System.out.println("===============================");

        System.out.println("Enter Your Name:");
        String name = s.nextLine();

        System.out.println("Enter Your Surname:");
        String surname = s.nextLine();

        System.out.println("Enter a Username (max 5 chars, must contain '_'):");
        String username = s.nextLine();

        System.out.println("Enter a Password (min 8 chars, 1 uppercase, 1 digit, 1 special char):");
        String password = s.nextLine();

        System.out.println("Enter Your Phone Number (USE +27XXXXXXXXX format):");
        String phonenumber = s.nextLine();

        System.out.println("*******************************************");
        System.out.println("  QUICK CHAT - CONNECTING WITH THE WORLD  ");
        System.out.println("*******************************************");

        Login loginObj = new Login(username, password, phonenumber, name, surname);
        String registrationResult = loginObj.registerUser(username, password, phonenumber);
        System.out.println(registrationResult);

        if (!registrationResult.equals("User successfully registered.")) {
            System.out.println("Please restart the application and enter valid details.");
            s.close();
            return;
        }

        //User Login
        System.out.println("\n======= LOGIN =======");

        System.out.println("Enter Your Username:");
        username = s.nextLine();

        System.out.println("Enter Your Password:");
        password = s.nextLine();

        boolean loggedIn = loginObj.loginUser(username, password);
        System.out.println(loginObj.returnLoginStatus(username, password));

        if (!loggedIn) {
            System.out.println("Login failed. Exiting application.");
            s.close();
            return;
        }

        //Messaging Setup/Interface
        System.out.println("-------------------------------");
        System.out.println("    Welcome to QuickChat.");
        System.out.println("-------------------------------");

        // Load any previously stored messages from JSON file into the storedMessages array
        Messages.loadStoredMessagesFromJSON();

        int numMessages = 0;
        while (numMessages <= 0) {
            System.out.println("How many messages would you like to send?");
            try {
                numMessages = Integer.parseInt(s.nextLine().trim());
                if (numMessages <= 0) System.out.println("Please enter a number greater than 0.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a whole number.");
            }
        }

        int messagesSentSoFar = 0;

        //Main menu loop
        boolean running = true;
        while (running) {

            System.out.println("\n--- QuickChat Menu ---");
            System.out.println("1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Stored Messages");
            System.out.println("4) Quit");
            System.out.print("Choose an option: ");

            String menuInput = s.nextLine().trim();

            switch (menuInput) {

                //Option 1 Compose and send messages
                case "1":
                    if (messagesSentSoFar >= numMessages) {
                        System.out.println("You have already composed all " + numMessages + " message(s).");
                        break;
                    }

                    while (messagesSentSoFar < numMessages) {

                        System.out.println("\n--- Message " + (messagesSentSoFar + 1) + " of " + numMessages + " ---");

                        //Generate Message ID
                        String msgID = Messages.generateMessageID();
                        System.out.println("Message ID generated: " + msgID);

                        //Recipient 
                        String recipient      = "";
                        String recipientStatus = "";
                        while (!recipientStatus.equals("Cell phone number successfully captured.")) {
                            System.out.println("Enter recipient cell number " + "(international format, e.g. +27718693002):");
                            recipient = s.nextLine().trim();
                            Messages tempMsg = new Messages();
                            tempMsg.setRecipient(recipient);
                            recipientStatus = tempMsg.checkRecipientCell();
                            System.out.println(recipientStatus);
                        }

                        // --- Message Body ---
                        String messageText = "";
                        String lengthStatus = "";
                        while (!lengthStatus.equals("Message ready to send.")) {
                            System.out.println("Enter your message (max 250 characters):");
                            messageText = s.nextLine();
                            Messages tempMsg = new Messages();
                            tempMsg.setMessageText(messageText);
                            lengthStatus = tempMsg.checkMessageLength();
                            System.out.println(lengthStatus);
                        }

                        //Build full Message object
                        int msgNumber = messagesSentSoFar + 1;
                        Messages msg = new Messages(msgID, msgNumber, recipient, messageText);
                        System.out.println("Message Hash: " + msg.getMessageHash());

                        //Message Action Menu
                        System.out.println("\nWhat would you like to do with this message?");
                        System.out.println("1) Send Message");
                        System.out.println("2) Disregard Message");
                        System.out.println("3) Store Message to send later");
                        System.out.print("Choose an option: ");

                        int actionChoice = 0;
                        try {
                            actionChoice = Integer.parseInt(s.nextLine().trim());
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid choice. Defaulting to Disregard.");
                            actionChoice = 2;
                        }

                        String actionResult = msg.sentMessage(actionChoice);
                        System.out.println(actionResult);

                        //Show full details after sending
                        if (actionChoice == 1) {
                            System.out.println("\n--- Message Details ---");
                            System.out.println("Message ID  : " + msg.getMessageID());
                            System.out.println("Message Hash: " + msg.getMessageHash());
                            System.out.println("Recipient   : " + msg.getRecipient());
                            System.out.println("Message     : " + msg.getMessageText());
                        }

                        //Store messages to JSON
                        if (actionChoice == 3) {
                            msg.storeMessage();
                        }

                        messagesSentSoFar++;
                    }

                    // Summary
                    Messages summary = new Messages();
                    System.out.println("\nTotal messages sent: " + summary.returnTotalMessages());
                    break;

                //Option 2 Show recently sent messages
                case "2":
                    Messages view = new Messages();
                    System.out.println(view.printMessages());
                    break;

               //Option 3 Stored messages sub-menu
                case "3":
                    boolean storedMenuRunning = true;
                    while (storedMenuRunning) {
                        System.out.println("\n--- Stored Messages Menu ---");
                        System.out.println("a) Display sender and recipient of all stored messages");
                        System.out.println("b) Display longest stored message");
                        System.out.println("c) Search for a message by ID");
                        System.out.println("d) Search all messages for a particular recipient");
                        System.out.println("e) Delete a message using the message hash");
                        System.out.println("f) Display full report of all stored messages");
                        System.out.println("0) Back to main menu");
                        System.out.print("Choose an option: ");

                        String subChoice = s.nextLine().trim().toLowerCase();
                        Messages helper = new Messages();

                        switch (subChoice) {
                            case "a":
                                System.out.println(helper.displayStoredSenderRecipient());
                                break;

                            case "b":
                                System.out.println("Longest stored message:\n\""
                                        + helper.longestStoredMessage() + "\"");
                                break;

                            case "c":
                                System.out.print("Enter Message ID to search: ");
                                String searchID = s.nextLine().trim();
                                System.out.println(helper.searchByID(searchID));
                                break;

                            case "d":
                                System.out.print("Enter recipient number to search: ");
                                String searchRecipient = s.nextLine().trim();
                                System.out.println(helper.searchByRecipient(searchRecipient));
                                break;

                            case "e":
                                System.out.print("Enter Message Hash to delete: ");
                                String delHash = s.nextLine().trim();
                                System.out.println(helper.deleteByHash(delHash));
                                break;

                            case "f":
                                System.out.println(helper.displayStoredReport());
                                break;

                            case "0":
                                storedMenuRunning = false;
                                break;

                            default:
                                System.out.println("Invalid option.");
                        }
                    }
                    break;

                //Option 4 Quit
                case "4":
                    System.out.println("Thank you for using QuickChat. Goodbye!");
                    running = false;
                    break;

                default:
                    System.out.println("Invalid option. Please enter 1, 2, 3, or 4.");
            }
        }

        s.close();
        
    }
}
