package org.nem.chat.protocol.service;

import com.google.gson.Gson;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

/**
 *
 * @author nemanja.marjanovic
 */
public class Encoder {

    public String encode(final String source) {
        return baseEncode(source);
    }

    public String encodeMap(final Map source) {
        return encode(new Gson().toJson(source));
    }

    private String baseEncode(final String source) {
        return new String(Base64.getEncoder().encode(source.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
    }
}
