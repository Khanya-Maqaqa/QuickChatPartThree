/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */

import com.mycompany.poepartthree.Messages;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author kvmaq
 */
public class MessagesTest {
    
    public MessagesTest() {
    }
     // Message IDs for testing hashes
    private static  String ID_MSG1 = "1100000001";
    private static  String ID_MSG2 = "2200000002";
    private static  String ID_MSG3 = "3300000003";
    private static  String ID_MSG4 = "4400000004";
    private static  String ID_MSG5 = "5500000005";

    @BeforeEach
    void setUp() {
        Messages.resetAll();

        // Populate arrays with the 5 test messages
        Messages m1 = new Messages(ID_MSG1, 1, "+27834557896", "Did you get the cake?");
        m1.sentMessage(1);   // Sent

        Messages m2 = new Messages(ID_MSG2, 2, "+27838884567","Where are you? You are late! I have asked you to be on time.");
        m2.sentMessage(3);   // Stored
        m2.storeMessage();   // Write to JSON

        Messages m3 = new Messages(ID_MSG3, 3, "+27834484567", "Yohoooo, I am at your gate.");
        m3.sentMessage(2);   // Disregard

        // Message 4 recipient is invalid (no +) – we still build the object so we can
        // test the array population; the checkRecipientCell test uses a temporay object.
        Messages m4 = new Messages(ID_MSG4, 4, "0838884567", "It is dinner time !");
        m4.sentMessage(1);   // Sent

        Messages m5 = new Messages(ID_MSG5, 5, "+27838884567", "Ok, I am leaving without you.");
        m5.sentMessage(3);   // Stored
        m5.storeMessage();   // Write to JSON
    }

    //Check message length
    @Test
    void testCheckMessageLength_Valid() {
        Messages msg = new Messages();
        msg.setMessageText("Did you get the cake?");
        assertEquals("Message ready to send.", msg.checkMessageLength());
    }

    @Test
    void testCheckMessageLength_Exceeds() {
        Messages msg = new Messages();
        msg.setMessageText("A".repeat(251));
        assertEquals("Message exceeds 250 characters by 1; please reduce the size.",
                msg.checkMessageLength());
    }

   //Check recipient cell
    @Test
    void testCheckRecipientCell_Valid() {
        Messages msg = new Messages();
        msg.setRecipient("+27834557896");
        assertEquals("Cell phone number successfully captured.", msg.checkRecipientCell());
    }

    @Test
    void testCheckRecipientCell_Invalid_NoCode() {
        Messages msg = new Messages();
        msg.setRecipient("0838884567");   // Message 4 recipient – no +27 code
        assertEquals("Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.",msg.checkRecipientCell());
    }

    
    //Create message hash
    @Test
    void testCreateMessageHash_Message1() {
        Messages msg = new Messages(ID_MSG1, 1, "+27834557896", "Did you get the cake?");
        assertEquals("11:1:DIDCAKE", msg.getMessageHash());
    }

    @Test
    void testCreateMessageHash_Message2() {
        Messages msg = new Messages(ID_MSG2, 2, "+27838884567","Where are you? You are late! I have asked you to be on time.");
        assertEquals("22:2:WHERETIME", msg.getMessageHash());
    }

    //Sent messages correctly populated
    @Test
    void testSentMessagesArray_ContainsExpectedMessages() {
        Messages[] sent = Messages.getSentMessages();
        assertEquals(2, sent.length, "Two messages should have been sent");
        assertEquals("Did you get the cake?", sent[0].getMessageText());
        assertEquals("It is dinner time !", sent[1].getMessageText());
    }

    
   //  sentMessage return value
    @Test
    void testSentMessage_Send() {
        Messages.resetAll();
        Messages msg = new Messages(ID_MSG1, 1, "+27834557896", "Did you get the cake?");
        assertEquals("Message successfully sent.", msg.sentMessage(1));
    }

    @Test
    void testSentMessage_Discard() {
        Messages.resetAll();
        Messages msg = new Messages(ID_MSG3, 3, "+27834484567", "Yohoooo, I am at your gate.");
        assertEquals("Press 0 to delete the message.", msg.sentMessage(2));
    }

    @Test
    void testSentMessage_Store() {
        Messages.resetAll();
        Messages msg = new Messages(ID_MSG2, 2, "+27838884567",
                "Where are you? You are late! I have asked you to be on time.");
        assertEquals("Message successfully stored.", msg.sentMessage(3));
    }

    //  returnTotalMessages
    @Test
    void testReturnTotalMessages() {
        // setUp sent 2 messages (m1 and m4); stored and disregarded don't count
        assertEquals(2, new Messages().returnTotalMessages());
    }

    //  longestStoredMessage  
    @Test
    void testLongestStoredMessage() {
        String longest = new Messages().longestStoredMessage();
        assertEquals(
            "Where are you? You are late! I have asked you to be on time.",
            longest,
            "Message 2 should be the longest stored message"
        );
    }

      //  searchByID
    @Test
    void testSearchByID_Found() {
        String result = new Messages().searchByID(ID_MSG2);
        assertTrue(result.contains("Where are you? You are late! I have asked you to be on time."),
                "searchByID should return Message 2's text");
        assertTrue(result.contains("+27838884567"),
                "searchByID should return Message 2's recipient");
    }

    @Test
    void testSearchByID_NotFound() {
        String result = new Messages().searchByID("0000000000");
        assertTrue(result.contains("not found"), "Non-existent ID should return 'not found'");
    }

    // searchByRecipient  (Part 3 – d)
    @Test
    void testSearchByRecipient_TwoMessages() {
        // +27838884567 is the recipient of Message 2 (stored) and Message 5 (stored)
        String result = new Messages().searchByRecipient("+27838884567");
        assertTrue(result.contains("Where are you? You are late! I have asked you to be on time."),
                "Result should include Message 2");
        assertTrue(result.contains("Ok, I am leaving without you."),
                "Result should include Message 5");
    }

    @Test
    void testSearchByRecipient_NotFound() {
        String result = new Messages().searchByRecipient("+27000000000");
        assertTrue(result.contains("No messages found"), "Unknown recipient should return 'no messages found'");
    }

    //  deleteByHash
    @Test
    void testDeleteByHash_Success() {
        // Message 2 hash: 22:2:WHERETIME
        String hash = new Messages(ID_MSG2, 2, "+27838884567",
                "Where are you? You are late! I have asked you to be on time.").getMessageHash();

        String result = new Messages().deleteByHash(hash);
        assertTrue(result.contains("successfully deleted"),
                "deleteByHash should confirm deletion");

        // storedCount should have decreased by 1 (was 2: m2 + m5)
        assertEquals(1, Messages.getStoredCount(),
                "Stored count should be 1 after deleting Message 2");
    }

    @Test
    void testDeleteByHash_NotFound() {
        String result = new Messages().deleteByHash("XX:0:FAKEHASH");
        assertTrue(result.contains("not found"), "Non-existent hash should return 'not found'");
    }

    //  displayStoredReport  
    @Test
    void testDisplayStoredReport_ContainsAllFields() {
        String report = new Messages().displayStoredReport();
        assertTrue(report.contains("Message Hash"), "Report should contain 'Message Hash' header");
        assertTrue(report.contains("Recipient"),    "Report should contain 'Recipient' header");
        assertTrue(report.contains("Message"),      "Report should contain 'Message' header");
        // At least one stored message should appear
        assertTrue(report.contains("+27838884567"), "Report should contain the stored recipient");
    }

    //generateMessageID
    @Test
    void testGenerateMessageID_Format() {
        String id = Messages.generateMessageID();
        assertEquals(10, id.length(), "Message ID must be exactly 10 digits");
        assertTrue(id.matches("\\d{10}"), "Message ID must contain only digits");
        assertNotEquals('0', id.charAt(0), "Message ID must not start with 0");
    }

    //  disregardedMessages[] array
    @Test
    void testDisregardedMessagesArray_Populated() {
        assertEquals(1, Messages.getDisregardedCount(),"Only Message 3 was disregarded");
        assertEquals("Yohoooo, I am at your gate.", Messages.getDisregardedMessages()[0].getMessageText());
    }
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    
    @AfterEach
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
}
