package SignLib;

import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;
import java.security.cert.Certificate;

import com.safenetinc.luna.provider.LunaProvider;

public class MyHSMFactory {
		
	private KeyStore ks = null;
	
	public int connect(int slot, String password) throws Exception, NoSuchProviderException, KeyStoreException {
		Provider p = new LunaProvider();
		ByteArrayInputStream slotNumber = new ByteArrayInputStream(("slot:" + slot).getBytes());

        try { 
	        Security.addProvider(p);
        
	        // open key store                                                            
	        ks = KeyStore.getInstance("Luna", p.getName());
	        System.out.println(p.getName());
	        
	        if(password.isEmpty() || password == null) {
	        	ks.load(slotNumber, null);
	        }
	        else {
	        	ks.load(slotNumber, password.toCharArray());
	        }
	        
        } catch(Exception e) {
			throw new KeyStoreException("Failed to initialize keystore: " + e.getMessage());
		}		
		
		return 0;
	}
	
	public Certificate[] getCertificateChain(String label) {
		
		Certificate[] certificateChain = new Certificate[1];
		
		try {
			certificateChain[0] = ks.getCertificate(label);
			//certificateChain[1] = ks0.getCertificate("Sub CA");
			//certificateChain[2] = ks0.getCertificate("Root CA");
			return certificateChain;
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} finally {
			certificateChain = null;
		}
		
		return null;
	}
	
	public PrivateKey getPrivateKey(String label) {
		
		PrivateKey privateKey;
		
		try {			
			privateKey = (PrivateKey) ks.getKey(label, null);			
			if (privateKey == null) {
				System.out.println("Private key not found");
				new Exception("Private key not found");
			}			
			return privateKey;			
		} catch (Exception e) {			
			e.printStackTrace();
		}
		
		return null;
		 
	}
	
}
