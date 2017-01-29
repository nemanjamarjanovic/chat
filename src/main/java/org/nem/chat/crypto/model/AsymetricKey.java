package org.nem.chat.protocol.model;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 *
 * @author Nemanja Marjanović
 */
public class AsymetricKey {

    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    public AsymetricKey() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(1024);
            KeyPair pair = keyGen.generateKeyPair();
            this.privateKey = pair.getPrivate();
            this.publicKey = pair.getPublic();
        } catch (Exception exception) {
            throw new RuntimeException(exception.getMessage());
        }
    }

    public PrivateKey getPrivateKey() {
        return this.privateKey;
    }

    public PublicKey getPublicKey() {
        return this.publicKey;
    }

    public static void main(String[] args) throws Exception {
        AsymetricKey as = new AsymetricKey();
        System.out.println(new String(as.privateKey.getEncoded()));
        Files.write(new File("server-private.txt").toPath(), as.privateKey.getEncoded(), StandardOpenOption.CREATE);
        PrivateKey readpriv = getPrivate("server-private.txt");
        System.out.println(new String(readpriv.getEncoded()));
        
        
       // Files.write(new File("servar-public.txt").toPath(), as.publicKey.getEncoded(), StandardOpenOption.CREATE);
    }

    public static PrivateKey getPrivate(String filename) throws Exception {
        byte[] keyBytes = Files.readAllBytes(new File(filename).toPath());
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }

    public static PublicKey getPublic(String filename) throws Exception {
        byte[] keyBytes = Files.readAllBytes(new File(filename).toPath());
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }
}
