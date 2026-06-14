
package com.mycompany.poepartthree;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

/**
 * Messages Class handles message creation, validation, sending, and JSON storage
 * @author kvmaq
 */

public class Messages {
    //Messaging variables 
    private String messageID;
    private int messageNumber;
    private String recipient;
    private String messageText;
    private String messageHash;
    private String messageStatus; // "Sent", "Stored", "Disregarded"

    //Static state messaging varaibles
    private static int MAX_MESSAGES = 100;

    //Main message object arrays
    private static Messages[] sentMessages = new Messages[MAX_MESSAGES];
    private static Messages[] storedMessages = new Messages[MAX_MESSAGES];
    private static Messages[] disregardedMessages = new Messages[MAX_MESSAGES];

    //Parallel string arrays 
    private static String[] messageHashes = new String[MAX_MESSAGES];
    private static String[] messageIDs = new String[MAX_MESSAGES];

    //Counters
    private static int sentCount = 0;
    private static int storedCount = 0;
    private static int disregardedCount = 0;
    private static int hashCount = 0;
    private static int idCount = 0;
    private static int totalMessages = 0;

    //+27 followed by exactly 9 digits (matches POE test data format)
    private static String RECIPIENT_PATTERN = "^\\+27\\d{9}$";
 
    //Full constructor used when all message data is known
    public Messages(String messageID, int messageNumber, String recipient, String messageText) {
        this.messageID     = messageID;
        this.messageNumber = messageNumber;
        this.recipient     = recipient;
        this.messageText   = messageText;
        this.messageHash   = createMessageHash();
    }

    //No argument constructor used for tempemorary validation of objects
    public Messages() { }

    //Returns true if messageID is non-null and ≤ 10 characters
    public boolean checkMessageID() {
        return messageID != null && messageID.length() <= 10;
    }

    //Validates recipient cell number
    public String checkRecipientCell() {
        if (recipient == null || recipient.isBlank()) {
            return "Cell phone number is incorrectly formatted or does not contain " + "an international code. Please correct the number and try again.";
        }
        if (recipient.matches(RECIPIENT_PATTERN)) {
            return "Cell phone number successfully captured.";
        }
        return "Cell phone number is incorrectly formatted or does not contain " + "an international code. Please correct the number and try again.";
    }

    //Validates message length
    public String checkMessageLength() {
        if (messageText == null) {
            return "Message exceeds 250 characters by 0; please reduce the size.";
        }
        int len = messageText.length();
        if (len <= 250) return "Message ready to send.";
        return "Message exceeds 250 characters by " + (len - 250) + "; please reduce the size.";
    }
    
    //Messag hash generation
    public String createMessageHash() {
        if (messageID == null || messageText == null || messageText.isBlank()) return "";

        String idPrefix  = messageID.substring(0, 2);
        String[] words   = messageText.trim().split("\\s+");
        String firstWord = words[0].replaceAll("[^a-zA-Z0-9]", "");
        String lastWord  = words[words.length - 1].replaceAll("[^a-zA-Z0-9]", "");

        this.messageHash = (idPrefix + ":" + messageNumber + ":" + firstWord + lastWord).toUpperCase();
        return this.messageHash;
    }

    //Handles the user's action for this message and slso populates the parallel arrays
    public String sentMessage(int choice) {
        // Always track hash and ID in the parallel arrays
        if (hashCount < MAX_MESSAGES) messageHashes[hashCount++] = this.messageHash;
        if (idCount   < MAX_MESSAGES) messageIDs[idCount++]      = this.messageID;

        switch (choice) {
            case 1:
                this.messageStatus = "Sent";
                totalMessages++;
                if (sentCount < MAX_MESSAGES) sentMessages[sentCount++] = this;
                return "Message successfully sent.";

            case 2:
                this.messageStatus = "Disregarded";
                if (disregardedCount < MAX_MESSAGES) disregardedMessages[disregardedCount++] = this;
                return "Press 0 to delete the message.";

            case 3:
                this.messageStatus = "Stored";
                if (storedCount < MAX_MESSAGES) storedMessages[storedCount++] = this;
                return "Message successfully stored.";

            default:
                return "Invalid option selected.";
        }
    }

    //Displays a  formatted report of all sent messages
    public String printMessages() {
        if (sentCount == 0) return "No messages have been sent.";
        StringBuilder sb = new StringBuilder();
        sb.append("\n======= SENT MESSAGES =======\n");
        for (int i = 0; i < sentCount; i++) {
            Messages m = sentMessages[i];
            sb.append("Message ID   : ").append(m.messageID).append("\n");
            sb.append("Message Hash : ").append(m.messageHash).append("\n");
            sb.append("Recipient    : ").append(m.recipient).append("\n");
            sb.append("Message      : ").append(m.messageText).append("\n");
            sb.append("-----------------------------\n");
        }
        return sb.toString();
    }

    // Returns total number of messages sent
    public int returnTotalMessages() { 
        return totalMessages; 
    }

    //Displays the sender/recipient of all stored messages.
    public String displayStoredSenderRecipient() {
        if (storedCount == 0) return "No stored messages found.";
        StringBuilder sb = new StringBuilder();
        sb.append("\n=== Stored Messages – Sender & Recipient ===\n");
        for (int i = 0; i < storedCount; i++) {
            Messages m = storedMessages[i];
            sb.append("Message ").append(i + 1).append(":\n");
            sb.append("  Sender    : You\n");
            sb.append("  Recipient : ").append(m.recipient).append("\n");
        }
        return sb.toString();
    }

    //Returns the longest stored message (by character count).
    public String longestStoredMessage() {
        if (storedCount == 0) return "No stored messages found.";
        Messages longest = storedMessages[0];
        for (int i = 1; i < storedCount; i++) {
            if (storedMessages[i].messageText.length() > longest.messageText.length()) {
                longest = storedMessages[i];
            }
        }
        return longest.messageText;
    }

    //Searches storedMessages array for a given messageID and returns the corresponding recipient and message text.
    public String searchByID(String id) {
        for (int i = 0; i < storedCount; i++) {
            if (storedMessages[i].messageID.equals(id)) {
                return "Recipient : " + storedMessages[i].recipient
                     + "\nMessage   : " + storedMessages[i].messageText;
            }
        }
        // Also search sentMessages array
        for (int i = 0; i < sentCount; i++) {
            if (sentMessages[i].messageID.equals(id)) {
                return "Recipient : " + sentMessages[i].recipient
                     + "\nMessage   : " + sentMessages[i].messageText;
            }
        }
        return "Message with ID \"" + id + "\" not found.";
    }
    
    //Returns all sent or stored messages for a particular recipient.
    public String searchByRecipient(String recipientNumber) {
        StringBuilder sb = new StringBuilder();
        boolean found = false;

        for (int i = 0; i < sentCount; i++) {
            if (sentMessages[i].recipient.equals(recipientNumber)) {
                sb.append("\"").append(sentMessages[i].messageText).append("\"\n");
                found = true;
            }
        }
        for (int i = 0; i < storedCount; i++) {
            if (storedMessages[i].recipient.equals(recipientNumber)) {
                sb.append("\"").append(storedMessages[i].messageText).append("\"\n");
                found = true;
            }
        }

        if (!found) return "No messages found for recipient: " + recipientNumber;
        return sb.toString().trim();
    }

    //Deletes a stored message by its hash.
    public String deleteByHash(String hash) {
        for (int i = 0; i < storedCount; i++) {
            if (storedMessages[i].messageHash.equalsIgnoreCase(hash)) {
                String deletedText = storedMessages[i].messageText;
                // Shift left
                for (int j = i; j < storedCount - 1; j++) {
                    storedMessages[j] = storedMessages[j + 1];
                }
                storedMessages[--storedCount] = null;
                // Rewrite JSON after deletion
                storeMessage();
                return "Message: \"" + deletedText + "\" successfully deleted.";
            }
        }
        return "Message with hash \"" + hash + "\" not found.";
    }

    //Displays a full report of all stored messages showing Message Hash, Recipient, and Message.
    public String displayStoredReport() {
        if (storedCount == 0) return "No stored messages to display.";
        StringBuilder sb = new StringBuilder();
        sb.append("\n======= STORED MESSAGES REPORT =======\n");
        for (int i = 0; i < storedCount; i++) {
            Messages m = storedMessages[i];
            sb.append("Message Hash : ").append(m.messageHash).append("\n");
            sb.append("Recipient    : ").append(m.recipient).append("\n");
            sb.append("Message      : ").append(m.messageText).append("\n");
            sb.append("--------------------------------------\n");
        }
        return sb.toString();
    }

    //Writes all stored messages to JSON file. Uses manual JSON building 
    public void storeMessage() {
        StringBuilder json = new StringBuilder();
        json.append("[\n");
        for (int i = 0; i < storedCount; i++) {
            Messages m = storedMessages[i];
            json.append("  {\n");
            json.append("    \"messageID\": \"").append(escape(m.messageID)).append("\",\n");
            json.append("    \"messageNumber\": ").append(m.messageNumber).append(",\n");
            json.append("    \"recipient\": \"").append(escape(m.recipient)).append("\",\n");
            json.append("    \"message\": \"").append(escape(m.messageText)).append("\",\n");
            json.append("    \"messageHash\": \"").append(escape(m.messageHash)).append("\",\n");
            json.append("    \"status\": \"").append(escape(m.messageStatus)).append("\"\n");
            json.append("  }");
            if (i < storedCount - 1) json.append(",");
            json.append("\n");
        }
        json.append("]");

        try (FileWriter file = new FileWriter("storedMessages.json")) {
            file.write(json.toString());
            file.flush();
            System.out.println("Messages Successfully stored to storedMessages.json");
        } catch (IOException e) {
            System.out.println("Error storing messages: " + e.getMessage());
        }
    }
    
    //Reads JSON file and populates the storedMessages array
    public static void loadStoredMessagesFromJSON() {
        try (BufferedReader reader = new BufferedReader(new FileReader("storedMessages.json"))) {
            String line;
            String id = null, recipient = null, text = null, hash = null, status = null;
            int number = 0;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("\"messageID\""))     id        = extractValue(line);
                else if (line.startsWith("\"messageNumber\"")) number = Integer.parseInt(extractValue(line));
                else if (line.startsWith("\"recipient\""))    recipient = extractValue(line);
                else if (line.startsWith("\"message\""))      text      = extractValue(line);
                else if (line.startsWith("\"messageHash\""))  hash      = extractValue(line);
                else if (line.startsWith("\"status\""))       status    = extractValue(line);

                //When all fields collected, build the Messages object
                if (id != null && recipient != null && text != null && hash != null && status != null) {
                    Messages m = new Messages(id, number, recipient, text);
                    m.messageHash   = hash;
                    m.messageStatus = status;
                    // Only add if not already in the array (avoid duplicates on reload)
                    boolean exists = false;
                    for (int i = 0; i < storedCount; i++) {
                        if (storedMessages[i].messageID.equals(id)) { exists = true; break; }
                    }
                    if (!exists && storedCount < MAX_MESSAGES) {
                        storedMessages[storedCount++] = m;
                        if (hashCount < MAX_MESSAGES) messageHashes[hashCount++] = hash;
                        if (idCount   < MAX_MESSAGES) messageIDs[idCount++]      = id;
                    }
                    id = null; recipient = null; text = null; hash = null; status = null;
                }
            }
            System.out.println("Loaded " + storedCount + " stored message(s) from JSON.");
        } catch (IOException e) {
            //No file yet – silently continue
        }
    }

    //Extracts the value part from a JSON line like  "key": "value"  or  "key": 123
    private static String extractValue(String line) {
        int colon = line.indexOf(':');
        if (colon < 0) return "";
        String val = line.substring(colon + 1).trim();
        //Remove trailing comma
        if (val.endsWith(",")) val = val.substring(0, val.length() - 1).trim();
        //Strip surrounding quotes if present
        if (val.startsWith("\"") && val.endsWith("\"")) {
            val = val.substring(1, val.length() - 1);
        }
        //Unescape basic JSON escapes
        val = val.replace("\\\"", "\"").replace("\\n", "\n").replace("\\\\", "\\");
        return val;
    }

    //Escapes special characters for safe JSON string embedding
    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    //Generates a random 10-digit message ID (no leading zero)
    public static String generateMessageID() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        sb.append(random.nextInt(9) + 1);
        for (int i = 0; i < 9; i++) sb.append(random.nextInt(10));
        return sb.toString();
    }

    //Resets all static state variables – used between test runs to ensure isolation
    public static void resetAll() {
        sentMessages = new Messages[MAX_MESSAGES];
        storedMessages = new Messages[MAX_MESSAGES];
        disregardedMessages = new Messages[MAX_MESSAGES];
        messageHashes = new String[MAX_MESSAGES];
        messageIDs = new String[MAX_MESSAGES];
        sentCount = 0;
        storedCount = 0;
        disregardedCount = 0;
        hashCount = 0;
        idCount = 0;
        totalMessages = 0;
    }

    //Getters
    public String getMessageID() { 
        return messageID; 
    }
    
    public int    getMessageNumber() { 
        return messageNumber;
    }
    
    public String getRecipient() { 
        return recipient; 
    }
    
    public String getMessageText() { 
        return messageText; 
    }
    
    public String getMessageHash() { 
        return messageHash;
    }
    
    public String getMessageStatus() { 
        return messageStatus; 
    }
    
    public static int getSentCount() { 
        return sentCount; 
    }
    
    public static int getStoredCount() { 
        return storedCount; 
    }
    
    public static int getDisregardedCount() { 
        return disregardedCount; 
    }
    
    public static Messages[] getSentMessages() {
        Messages[] r = new Messages[sentCount];
        System.arraycopy(sentMessages, 0, r, 0, sentCount);
        return r;
    }

    public static Messages[] getStoredMessages() {
        Messages[] r = new Messages[storedCount];
        System.arraycopy(storedMessages, 0, r, 0, storedCount);
        return r;
    }
    
    public static Messages[] getDisregardedMessages() {
        Messages[] r = new Messages[disregardedCount];
        System.arraycopy(disregardedMessages, 0, r, 0, disregardedCount);
        return r;
    }

    public static String[] getMessageHashes() {
        String[] r = new String[hashCount];
        System.arraycopy(messageHashes, 0, r, 0, hashCount);
        return r;
    }

    public static String[] getMessageIDs() {
        String[] r = new String[idCount];
        System.arraycopy(messageIDs, 0, r, 0, idCount);
        return r;
    }


    //Setters
    public void setMessageID(String messageID) { 
        this.messageID = messageID; 
    }
    
    public void setMessageNumber(int n) { 
        this.messageNumber = n; 
    }
    
    public void setRecipient(String recipient) { 
        this.recipient = recipient; 
    }
    
    public void setMessageText(String messageText) { 
        this.messageText = messageText; 
    }
    
    public void setMessageHash(String messageHash) { 
        this.messageHash = messageHash; 
    }
}
