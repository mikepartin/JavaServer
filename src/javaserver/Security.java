/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaserver;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.KeyPair;
import java.security.SignatureException;
import java.util.ArrayList;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 *
 * @author Michael
 */

public class Security {
    
    int bit = 0;
    int BLOCK_SIZE = 117;
    
    Security (int bit) {
        if (bit < 512 || (bit % 512) > 0) {
            this.bit = 512;
        } else {
            this.bit = bit;
            this.BLOCK_SIZE = (bit/8) - 11;
        }
    }
    
    PublicKey myPublicKey;
    PrivateKey myPrivateKey;
    
    PublicKey encryptPublicKey;
    
    
    public boolean generateKeyPair() {
        boolean output = true;
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(bit);

            KeyPair newKeyPair = keyGen.genKeyPair();

            myPublicKey = newKeyPair.getPublic();
            myPrivateKey = newKeyPair.getPrivate();
        }
        catch (NoSuchAlgorithmException e) {
            output = false;
        }

        return output;
    }
    
    public void setEncryptPublicKey(PublicKey encryptPublicKey) {
        this.encryptPublicKey = encryptPublicKey;
    }
    
    public PublicKey getMyPublicKey() {
        return myPublicKey;
    }
    
    public ArrayList<byte[]> encrypt(String data) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, SignatureException {
        ArrayList<byte[]> output = new ArrayList<>();
        ArrayList<String> dataPartitions = new ArrayList<>();

        while (data.length() > BLOCK_SIZE) {
            String partition = data.substring(0, BLOCK_SIZE);
            dataPartitions.add(partition);
            data = data.substring(BLOCK_SIZE, data.length());
        }
        if (data.length() > 0) {
            dataPartitions.add(data);
        }
        byte[] cipherText = null;

        final Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, encryptPublicKey);
        
        System.out.println(dataPartitions.size());
        for (int i=0; i<dataPartitions.size(); i++) {
            cipherText = cipher.doFinal(dataPartitions.get(i).getBytes());
            output.add(cipherText);
        }
        return output;
    }
    
    public String encryptedToString(ArrayList<byte[]> input) throws UnsupportedEncodingException {
        String output = "";
        for (int i=0; i<input.size(); i++) {
            String decoded = new String(input.get(i), StandardCharsets.US_ASCII);
            output += decoded;
        }
        return output;
    }
    
    public String decrypt(ArrayList<byte[]> data) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        StringBuilder output = new StringBuilder();
        byte[] dectyptedText = null;
        final Cipher cipher = Cipher.getInstance("RSA");

        cipher.init(Cipher.DECRYPT_MODE, myPrivateKey);
        
        for (int q=0; q<data.size(); q++) {
            dectyptedText = cipher.doFinal(data.get(q));
            for (int i = 0; i < dectyptedText.length; ++i) {
                output.append((char)dectyptedText[i]);
            }
        }
        
        return output.toString();
    }
}
