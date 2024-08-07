package top.zhaoqw.crypto.asymmetric;

import javax.crypto.Cipher;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @author zhaoqw
 * @date 2023/5/17
 */
public class RSAcrypto {
        public static void main(String[] args) throws Exception {
            // 明文:
            byte[] plain = "Hello, encrypt use RSA".getBytes("UTF-8");
            // 创建公钥／私钥对:
            Person alice = new Person("Alice");
            // 用Alice的公钥加密:
            byte[] pk = alice.getPublicKey();
            System.out.println(String.format("public key: %x", new BigInteger(1, pk)));
            byte[] encrypted = alice.encrypt(plain);
            System.out.println(String.format("encrypted: %x", new BigInteger(1, encrypted)));
            // 用Alice的私钥解密:
            byte[] sk = alice.getPrivateKey();
            System.out.println(String.format("private key: %x", new BigInteger(1, sk)));
            byte[] decrypted = alice.decrypt(encrypted);
            System.out.println(new String(decrypted, "UTF-8"));


            /*
                RSA的公钥和私钥都可以通过getEncoded()方法获得以byte[]表示的二进制数据，并根据需要保存到文件中。
                要从byte[]数组恢复公钥或私钥，可以这么写：
                byte[] pkData = ...
                byte[] skData = ...
                KeyFactory kf = KeyFactory.getInstance("RSA");
                // 恢复公钥:
                X509EncodedKeySpec pkSpec = new X509EncodedKeySpec(pkData);
                PublicKey pk = kf.generatePublic(pkSpec);
                // 恢复私钥:
                PKCS8EncodedKeySpec skSpec = new PKCS8EncodedKeySpec(skData);
                PrivateKey sk = kf.generatePrivate(skSpec);


             */
        }
}

class Person {
    String name;
    // 私钥:
    PrivateKey sk;
    // 公钥:
    PublicKey pk;

    public Person(String name) throws GeneralSecurityException {
        this.name = name;
        // 生成公钥／私钥对:
        KeyPairGenerator kpGen = KeyPairGenerator.getInstance("RSA");
        kpGen.initialize(1024);
        KeyPair kp = kpGen.generateKeyPair();
        this.sk = kp.getPrivate();
        this.pk = kp.getPublic();
    }

    // 把私钥导出为字节
    public byte[] getPrivateKey() {
        return this.sk.getEncoded();
    }

    // 把公钥导出为字节
    public byte[] getPublicKey() {
        return this.pk.getEncoded();
    }

    // 用公钥加密:
    public byte[] encrypt(byte[] message) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, this.pk);
        return cipher.doFinal(message);
    }

    // 用私钥解密:
    public byte[] decrypt(byte[] input) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, this.sk);
        return cipher.doFinal(input);
    }
}

