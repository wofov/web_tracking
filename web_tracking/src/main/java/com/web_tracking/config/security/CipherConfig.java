package com.web_tracking.config.security;

import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;

public class CipherConfig {

    public String encrypt(String str) throws Exception{
        byte[] messageBytes = str.getBytes(StandardCharsets.UTF_8);
        SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(), ALGORITHM);
        byte[] ivBytes = createRandomIv();
        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        byte[] inputBytes = new byte[BLOCK_SIZE + messageBytes.length];

        System.arraycopy(ivBytes,0,inputBytes,0,BLOCK_SIZE);
        System.arraycopy(messageBytes,0,inputBytes,BLOCK_SIZE,messageBytes.length);
        return Base64.encodeBase64String(cipher.doFinal(inputBytes));
    }

    public String decrypt(String str)  {
        try{
            byte[] msgBytes = Base64.decodeBase64(str);
            byte[] ivBytes = Arrays.copyOfRange(msgBytes,0,BLOCK_SIZE);
            byte[] inputBytes = Arrays.copyOfRange(msgBytes,BLOCK_SIZE,msgBytes.length);

            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            return new String(cipher.doFinal(inputBytes));
        }catch (Exception e){
            return WebConstants.ERROR;
        }

    }
    private byte[] createRandomIv(){
        SecureRandom random = new SecureRandom();
        byte[] ivBytes = new byte[BLOCK_SIZE];
        random.nextBytes(ivBytes);
        return ivBytes;
    }
}
