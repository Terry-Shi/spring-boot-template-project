package com.service.webapp.security.token;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.crypto.Cipher;


/**
 * RSA 公钥，私钥文件生成
 * 公钥加密，私钥解密；私钥加密，公钥解密
 * 数字签名+验证签名 （数字签名由私钥拥有方生成，公钥拥有方验证）
 * 
 * 参考：https://blog.csdn.net/wangqiuyun/article/details/42143957
 * @author Terry
 *
 */
public class RSAKeyPairsGenerator {
    private static final String RSA_ALGORITHM = "RSA";
    public static final Charset UTF8 = Charset.forName("UTF-8");

    private static final String PRIVATE_KEY_PATH = "rsa_private.key";
    private static final String PUBLIC_KEY_PATH = "rsa_public.key";
    
    private final static Random random = new Random();

    public static void main(String[] args) throws Exception {
        // 生成公钥和私钥并存储到文件中
        Map<String, byte[]> map = generateRSAKeyPairs();
        System.out.println("publicKey:===>"+ Base64.getEncoder().encodeToString(map.get("publicKey")));
        System.out.println("privateKey:===>"+ Base64.getEncoder().encodeToString(map.get("privateKey")));
        Files.write(Paths.get(PRIVATE_KEY_PATH), (byte[])map.get("privateKey")); // Check a private RSA key file: openssl rsa -in rsa_private.isa -inform der -text -noout
        Files.write(Paths.get(PUBLIC_KEY_PATH), (byte[])map.get("publicKey")); // Check a public RSA key file: openssl rsa  -in rsa_public.isa  -inform der -text -noout -pubin
        
        RSAKeyPairsGenerator generator = new RSAKeyPairsGenerator();
        String str = RSAKeyPairsGenerator.KeyCreate(245); // Data must not be longer than 245 bytes
        // 私钥加密
        byte[] encrypted = generator.encrypt(str, RSAKeyPairsGenerator.loadPrivateKey(PRIVATE_KEY_PATH));
        // 公钥解密
        byte[] decrypted = generator.testDecrypt(encrypted, RSAKeyPairsGenerator.loadPublicKey(PUBLIC_KEY_PATH));
        System.out.println("before decrypt = " + str);
        System.out.println("after decrypt = " + new String(decrypted));
    }
    
    /**
     * 生成RSA公、私钥对
     * 
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static Map<String, byte[]> generateRSAKeyPairs() throws NoSuchAlgorithmException {
        Map<String, byte[]> keyPairMap = new HashMap<String, byte[]>();
        KeyPairGenerator generator = KeyPairGenerator.getInstance(RSA_ALGORITHM);
        generator.initialize(2048);
        KeyPair keyPair = generator.genKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();
        //keyPairMap.put("publicKey", Base64.encodeBase64String(publicKey.getEncoded()));
        //keyPairMap.put("privateKey", Base64.encodeBase64String(privateKey.getEncoded()));
        keyPairMap.put("publicKey", publicKey.getEncoded());
        keyPairMap.put("privateKey", privateKey.getEncoded());
        return keyPairMap;
    }

    /**
     * 加密
     * @param value （Data must not be longer than 245 bytes）
     * @return 加密后的byte[]
     * @throws Exception
     */
    private byte[] encrypt(String value, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
//        //读取公钥对应的字节数组
//        byte[] publicKeyCode = Files.readAllBytes(Paths.get(keyFile));
//        //构造公钥，存储起来的公钥需要使用X509EncodedKeySpec进行读取
//        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyCode);
//        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
//        //根据已有的KeySpec生成对应的公钥
//        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] result = cipher.doFinal(value.getBytes());
        //System.out.println(Base64.encodeBase64String(result));
        return result;
    }
    
    /**
     * 解密
     * @throws Exception
     */
    public byte[] testDecrypt(byte[] input, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
//        byte[] privateKeyCode = Files.readAllBytes(Paths.get(keyFile));
//        //私钥需要通过PKCS8EncodedKeySpec来读取
//        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyCode);
//        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
//        //生成私钥
//        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] result = cipher.doFinal(input);
        return result;
    }
    
    /**
     * 从文件中加载Private key
     * @param privateKeyFile 私钥文件
     * @return
     * @throws Exception
     */
    public static RSAPrivateKey loadPrivateKey(String privateKeyFile)
            throws Exception {
        try {
            byte[] buffer = Files.readAllBytes(Paths.get(privateKeyFile));
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此算法");
        } catch (InvalidKeySpecException e) {
            throw new Exception("私钥非法");
        }
    }
    
    /**
     * 从文件中加载公钥
     * 
     * @param publicKeyFile
     *            公钥文件
     * @throws Exception
     *             加载公钥时产生的异常
     */
    public static RSAPublicKey loadPublicKey(String publicKeyFile)
            throws Exception {
        try {
            byte[] buffer = Files.readAllBytes(Paths.get(publicKeyFile));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此算法");
        } catch (InvalidKeySpecException e) {
            throw new Exception("公钥非法");
        }
    }
    
    /**
     * 根据私钥（密钥）对信息进行签名
     * @param data 被签名的数据
     * @param privateKey 私钥
     * @return 签名后数据
     * @throws Exception
     */
    public static byte[] signByPrivateKey(byte[] data, PrivateKey privateKey)
            throws Exception {
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initSign(privateKey);
        sig.update(data);
        byte[] ret = sig.sign();
        return ret;
    }
    
    /**
     * 公钥验证签名
     * @param publicKey 公钥
     * @param infomation 私钥产生的签名
     * @param publicInfo 原始公开的字符串（被私钥签名的数据）
     * @return 验证通过与否
     */  
    public static boolean verifySignature(PublicKey publicKey,String infomation, byte[] publicInfo) {  
        boolean verify = false;  
        try {
            Signature mySig = Signature.getInstance("SHA256withRSA");
            mySig.initVerify(publicKey);  // 使用公钥初始化签名对象,用于验证签名  
            mySig.update(infomation.getBytes()); // 更新签名内容  
            verify= mySig.verify(publicInfo); // 得到验证结果  
        } catch (Exception e) {
            e.printStackTrace();
        }
        return verify;  
    }  
    
    /**
     * 生成16位AES随机密钥
     * @return
     */
    public static String getAESRandomKey(){
        long longValue = random.nextLong();
        return String.format("%016x", longValue);
    }

    public static String KeyCreate(int KeyLength) {
        String base = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%&*:_+<>?~#$@";
        Random random = new Random();
        StringBuffer Keysb = new StringBuffer();
        // 生成指定位数的随机秘钥字符串
        for (int i = 0; i < KeyLength; i++) {
            int number = random.nextInt(base.length());
            Keysb.append(base.charAt(number));
        }
        return Keysb.toString();
    }
}
