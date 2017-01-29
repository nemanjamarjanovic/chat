package org.nem.chat.crypto.model;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;

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
            keyGen.initialize(2046);
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

//    public static void main(String[] args) throws Exception {
////        AsymetricKey as = new AsymetricKey();
////        Files.write(new File("server-private.txt").toPath(), as.privateKey.getEncoded(), StandardOpenOption.CREATE);
////        Files.write(new File("server-public.txt").toPath(), as.publicKey.getEncoded(), StandardOpenOption.CREATE);
////    
//       // for(Provider p:Security.getProviders()){System.out.println(p);}
//    }

}
