package data.session;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

public class RSAKeyStore {

    private final RSAPublicKey rsaPublicKey;
    private final RSAPrivateKey rsaPrivateKey;

    public RSAKeyStore() {
        KeyPairGenerator kpg = null;
        try {
            kpg = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        kpg.initialize(2048);
        KeyPair keyPair = kpg.generateKeyPair();
        rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
        rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();
    }

    public RSAPublicKey getPublicKey() {
        return rsaPublicKey;
    }

    public RSAPrivateKey getPrivateKey() {
        return rsaPrivateKey;
    }

}
