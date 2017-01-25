package org.nem.chat.protocol.service;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.nem.chat.protocol.model.Message;

/**
 *
 * @author nemanja.marjanovic
 */
public class ByteMessageTest {

    /**
     * Test of class ByteMessage.
     */
    @Test
    public void testFrom() throws Exception {

        String expected = "Server";
        Message message = MessageBuilder.builder().type(expected).build();

        ByteMessage bm1 = ByteMessage.fromMessage(message);
        ByteMessage bm2 = ByteMessage.fromBytes(bm1.getBytes());
        String result = bm2.getMessage().getType();

        assertEquals(expected, result);

    }

}
