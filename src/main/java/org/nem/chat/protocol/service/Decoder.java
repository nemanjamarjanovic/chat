package org.nem.chat.protocol.service;

import com.google.gson.Gson;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

/**
 *
 * @author nemanja.marjanovic
 */
public class Decoder {

    public String decode(final String source) {
        return baseDecode(source);
    }

    public Map decodeMap(final String source) {
        return new Gson().fromJson(decode(source), Map.class);
    }

    private String baseDecode(final String source) {
        return new String(Base64.getDecoder().decode(source.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
    }

}
