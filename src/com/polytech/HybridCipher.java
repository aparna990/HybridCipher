package com.polytech;

import java.security.*;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import java.util.*;
import java.io.*;

public class HybridCipher {	
	
	static public void main(String argv[]){
		

		try {
			// Alice generates her public and private keys		
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(1024);
			KeyPair keyPair = keyPairGenerator.genKeyPair();
			PrivateKey alicePrivateKey = keyPair.getPrivate();
			PublicKey alicePublicKey = keyPair.getPublic();
			System.out.println("Alice sends to Bob " + DatatypeConverter.printBase64Binary(alicePublicKey.getEncoded()));
			
			// Bob generates his DES session key
			KeyGenerator keyGen = KeyGenerator.getInstance("DES");
			SecretKey bobSessionKey = keyGen.generateKey();
			System.out.println("Bob has generated " + DatatypeConverter.printBase64Binary(bobSessionKey.getEncoded()));
				
			// Bob encrypts the session key with Alice's public key
			Cipher bobCipher = Cipher.getInstance("RSA");
			bobCipher.init(Cipher.ENCRYPT_MODE, alicePublicKey);
			byte[] ciphered = bobCipher.doFinal(bobSessionKey.getEncoded());
			System.out.println("Bob sends to Alice " + DatatypeConverter.printBase64Binary(ciphered));
			
			// Alice decrypts Bob's message with her private key
			Cipher aliceDecipher = Cipher.getInstance("RSA");
			aliceDecipher.init(Cipher.DECRYPT_MODE, alicePrivateKey);
			byte[] deciphered = aliceDecipher.doFinal(ciphered);
			System.out.println("Alice has received " + DatatypeConverter.printBase64Binary(deciphered));
			System.out.println("Are the Bob's session key and Alice's deciphered message equal? " + DatatypeConverter.printBase64Binary(deciphered).equals(DatatypeConverter.printBase64Binary(bobSessionKey.getEncoded())));
			
			//5) Alice sends a message to Bob with her session key
			String message = "I love you Bob";
			System.out.println("Alice's message: " + message);
			Cipher aliceCipher = Cipher.getInstance("DES");
			SecretKey decipheredBobSessionKey =  new SecretKeySpec(deciphered, "DES");
			aliceCipher.init(Cipher.ENCRYPT_MODE, decipheredBobSessionKey);
			String cryptedMessage = DatatypeConverter.printBase64Binary(aliceCipher.doFinal(message.getBytes()));
			System.out.println("Alice's crypted message: " + cryptedMessage);
			
			//6) Bob decrypts the message with the session key.
			Cipher bobDecipher = Cipher.getInstance("DES");
			bobDecipher.init(Cipher.DECRYPT_MODE, bobSessionKey);
			String decryptedMessage = new String(bobDecipher.doFinal(DatatypeConverter.parseBase64Binary(cryptedMessage)));
			System.out.println("Bob has received: " + cryptedMessage);
			System.out.println("Bob has decoded: " + decryptedMessage);
			
		}catch(Exception e){
			System.out.println("Error");
			e.printStackTrace();
		}
	}
	
}
