package com.jet.cloud.deepmind.common.util;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public class AES {

    private static SecretKeySpec secretKey;
    private static byte[] key;

    static {
        setKey("beef56ad");
    }

    public static void setKey(String myKey) {
        MessageDigest sha = null;
        try {
            key = myKey.getBytes("UTF-8");
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKey = new SecretKeySpec(key, "AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static String encrypt(String strToEncrypt) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
        } catch (Exception e) {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }



    public static String decrypt(String strToDecrypt) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)), "UTF-8");
        } catch (Exception e) {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }

    public static void main(String[] args) {
      String root = "root";
        String encrypt = encrypt(root);
        System.out.println(encrypt);
        String decrypt = decrypt("hoKmXZDH8kFvKEbVCWNKHo8HAxtYuyNvKWHW0lw3gto=");
        System.out.println(decrypt);

    }

    /**
     * 初始化 AES Cipher
     *
     * @param sKey
     * @param cipherMode
     * @return
     */
    public static Cipher initAESCipher(String sKey, int cipherMode) {
        //创建Key gen
        KeyGenerator keyGenerator = null;
        Cipher cipher = null;
        try {
            keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128, new SecureRandom(sKey.getBytes()));
            SecretKey secretKey = keyGenerator.generateKey();
            byte[] codeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(codeFormat, "AES");
            cipher = Cipher.getInstance("AES");
            //初始化
            cipher.init(cipherMode, key);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();  //To change body success catch statement use File | Settings | File Templates.
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();  //To change body success catch statement use File | Settings | File Templates.
        } catch (InvalidKeyException e) {
            e.printStackTrace();  //To change body success catch statement use File | Settings | File Templates.
        }
        return cipher;
    }

    /**
     * 对文件进行AES加密
     *
     * @param sourceFile
     * @param fileType
     * @param sKey
     * @return
     */
    public static File encryptFile(File sourceFile, String fileType, String sKey) {
        //新建临时加密文件
        File encrypfile = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = new FileInputStream(sourceFile);
            encrypfile = File.createTempFile(sourceFile.getName(), fileType);
            outputStream = new FileOutputStream(encrypfile);
            Cipher cipher = initAESCipher(sKey, Cipher.ENCRYPT_MODE);
            //以加密流写入文件
            CipherInputStream cipherInputStream = new CipherInputStream(inputStream, cipher);
            byte[] cache = new byte[1024];
            int nRead = 0;
            while ((nRead = cipherInputStream.read(cache)) != -1) {
                outputStream.write(cache, 0, nRead);
                outputStream.flush();
            }
            cipherInputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body success catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body success catch statement use File | Settings | File Templates.
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();  //To change body success catch statement use File | Settings | File Templates.
            }
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();  //To change body success catch statement use File | Settings | File Templates.
            }
        }
        return encrypfile;
    }

    /**
     * AES方式解密文件
     *
     * @param sourceFile
     * @return
     */
    public static File decryptFile(File sourceFile, String fileType, String sKey) {
        File decryptFile = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            decryptFile = File.createTempFile(sourceFile.getName(), fileType);
            Cipher cipher = initAESCipher(sKey, Cipher.DECRYPT_MODE);
            inputStream = new FileInputStream(sourceFile);
            outputStream = new FileOutputStream(decryptFile);
            CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, cipher);
            byte[] buffer = new byte[1024];
            int r;
            while ((r = inputStream.read(buffer)) >= 0) {
                cipherOutputStream.write(buffer, 0, r);
            }
            cipherOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();  //To change body success catch statement use File | Settings | File Templates.
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();  //To change body success catch statement use File | Settings | File Templates.
            }
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();  //To change body success catch statement use File | Settings | File Templates.
            }
        }
        return decryptFile;
    }
}
