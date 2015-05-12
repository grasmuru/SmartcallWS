package za.co.smartcall.wsclient.implementation.keys;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import lombok.extern.log4j.Log4j;
import za.co.smartcall.wsclient.implementation.KeyStoreImplementation;

@Log4j
public class ImportKey {
	
	public  void addKey(String certfile, String keystoreFileName, String password) throws NoSuchAlgorithmException, CertificateException, IOException, KeyStoreException, URISyntaxException {

		String pathtoSave = KeyStoreImplementation.CONFIG_HOME + KeyStoreImplementation.FILE_SEPARATOR;
		

		File file = new File(pathtoSave+keystoreFileName);
		KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
		try (FileInputStream is = new FileInputStream(file)){
			keystore.load(is, password.toCharArray());
		}

		String alias = "smartcallservices";

		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		InputStream certstream = fullStream(certfile);
		Certificate certs = cf.generateCertificate(certstream);

		try (FileInputStream in = new FileInputStream(file)){
			keystore.load(in, password.toCharArray());
		}
				
		
		keystore.setCertificateEntry(alias, certs);

		try (FileOutputStream out = new FileOutputStream(file)){
			keystore.store(out, password.toCharArray());
		}
		log.info("Saved " + certfile +  " in " + file.getAbsolutePath());
	}

	private InputStream fullStream(String fname) throws IOException, URISyntaxException {
		InputStream resource = getClass().getClassLoader().getResourceAsStream(fname);
		DataInputStream dis = new DataInputStream(resource);
		byte[] bytes = new byte[dis.available()];
		dis.readFully(bytes);
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		return bais;
	}
}