package org.admin.pkg.ShoppingPointServer.persistence;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


public class AesEncryption {
	
	private static SecretKeySpec secretKey ;
    private static byte[] key ;
    private static String decryptedString;
    private static String encryptedString;
    
    public static void setKey(String myKey){ 
        MessageDigest sha = null;
        try {
            key = myKey.getBytes("UTF-8");
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16); // use only first 128 bit
            secretKey = new SecretKeySpec(key, "AES");      
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }          
    }
    
    public static String getDecryptedString() {
        return decryptedString;
    }
    public static void setDecryptedString(String decryptedString) {
        AesEncryption.decryptedString = decryptedString;
    }
    public static String getEncryptedString() {
        return encryptedString;
    }
    public static void setEncryptedString(String encryptedString) {
    	AesEncryption.encryptedString = encryptedString;
    }
    public static String encrypt(String strToEncrypt)
    {
        try {
        	byte[] textByte = strToEncrypt.getBytes();
        	Base64.Encoder encoder = Base64.getEncoder();
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            cipher.doFinal(textByte);
            String encryptedStr = encoder.encodeToString(cipher.doFinal(textByte));
            setEncryptedString(encryptedStr);
        }catch (Exception e) { 
        	System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }
    public static String decrypt(String strToDecrypt)
    {
        try {
        	Base64.Decoder decoder = Base64.getDecoder();
        	byte[] encryptedTextByte = decoder.decode(strToDecrypt);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
    		String decryptedStr = new String(cipher.doFinal(encryptedTextByte));
            setDecryptedString(decryptedStr);   
        } catch (Exception e) {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }
}
