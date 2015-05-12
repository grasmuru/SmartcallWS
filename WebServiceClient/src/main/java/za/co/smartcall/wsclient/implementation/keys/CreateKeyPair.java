package za.co.smartcall.wsclient.implementation.keys;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import lombok.extern.log4j.Log4j;

import org.apache.commons.lang.RandomStringUtils;
import org.bouncycastle.asn1.x509.X509Name;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.x509.X509V3CertificateGenerator;

import za.co.smartcall.wsclient.implementation.KeyStoreImplementation;



@Log4j
public class CreateKeyPair {
	
	private long fortyFiveYears = TimeUnit.MILLISECONDS.convert(365*45, TimeUnit.DAYS);

	public void createKeyPair(String keyEntry,String password) throws InvalidKeyException, IllegalStateException, NoSuchAlgorithmException, SignatureException, CertificateException, IOException, KeyStoreException{

	        Security.addProvider(new BouncyCastleProvider()); // adding provider
	        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");    
	        SecureRandom random = createFixedRandom();
            generator.initialize(2048, random);// to
	        String pathtoSave = KeyStoreImplementation.CONFIG_HOME + KeyStoreImplementation.FILE_SEPARATOR;
	        log.info("Path to save " + pathtoSave);
	        KeyPair keyPair = generator.generateKeyPair();
	        PublicKey publicKey = keyPair.getPublic();
	        PrivateKey privateKey = keyPair.getPrivate();
	        X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();
	        X509Name x509Name = new X509Name("CN=" + keyEntry);
	        certGen.setIssuerDN(x509Name);
	        certGen.setSubjectDN(x509Name);                       // note: same as issuer
	        certGen.setPublicKey(publicKey);
	        certGen.setNotAfter(new Date(System.currentTimeMillis()+fortyFiveYears));
	        certGen.setNotBefore(new Date(System.currentTimeMillis()));
	        certGen.setSerialNumber(new BigInteger(RandomStringUtils.randomNumeric(5)));
	        certGen.setSignatureAlgorithm("SHA1withRSA");
	        X509Certificate cert = certGen.generate(keyPair.getPrivate());
	        
	        FileOutputStream out = new FileOutputStream(new File(pathtoSave+keyEntry + ".cer"));
	        out.write(cert.getEncoded());
            out.close();
	        
	      
	        java.security.cert.Certificate[] outChain = { cert, };
	        cert.checkValidity();
	        KeyStore outStore = KeyStore.getInstance("JKS");
	        outStore.load(null, null);
	        outStore.setKeyEntry(keyEntry, privateKey,
	                password.toCharArray(), outChain);
	        OutputStream outputStream = new FileOutputStream(pathtoSave+KeyStoreImplementation.clientKeyStore);
	        outStore.store(outputStream, password.toCharArray());
	        outputStream.flush();
	        outputStream.close();


	  
	}
	
	 public static SecureRandom createFixedRandom()
	    {
	        return new FixedRand();
	    }
	 
	    private static class FixedRand extends SecureRandom {
	 
	        MessageDigest sha;
	        byte[] state;
	 
	        FixedRand() {
	            try {
	                this.sha = MessageDigest.getInstance("SHA-1");
	                this.state = sha.digest();
	            }
	            catch (NoSuchAlgorithmException e) {
	                throw new RuntimeException("can't find SHA-1!");
	            }
	        }
	 
	        public void nextBytes(byte[] bytes){
	 
	            int off = 0;
	 
	            sha.update(state);
	 
	            while (off < bytes.length){               
	                state = sha.digest();
	 
	                if (bytes.length - off > state.length) {
	                    System.arraycopy(state, 0, bytes, off, state.length);
	                }
	                else {
	                    System.arraycopy(state, 0, bytes, off, bytes.length - off);
	                }
	 
	                off += state.length;
	 
	                sha.update(state);
	            }
	        }
	    }
	
}
