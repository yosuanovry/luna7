package SignLib;

import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Signature;

import com.itextpdf.text.pdf.security.DigestAlgorithms;
import com.itextpdf.text.pdf.security.ExternalSignature;
import utils.MyUtil;

public class MyHSMSignature implements ExternalSignature {

	private MyHSMFactory hsmFactory = null;
	private String keyLabel = null;
	private String providerName = null;
	
	public MyHSMSignature(MyHSMFactory hsmFactory, String keyLabel) throws Exception {
		this.hsmFactory = hsmFactory;
		this.keyLabel = keyLabel;
		this.providerName = MyUtil.getProperty("providerName");
	}
	  
        
	public String getHashAlgorithm() {		
		return DigestAlgorithms.SHA1;
	}

	public String getEncryptionAlgorithm() {
		return "RSA";
	}

	public byte[] sign(byte[] message) throws GeneralSecurityException {
		byte[] data = new byte[256];
		
		PrivateKey privateKey = hsmFactory.getPrivateKey(keyLabel);
		
		Signature sign = Signature.getInstance("SHA1withRSA", providerName);
		sign.initSign(privateKey);
		sign.update(message);
		data = sign.sign();
		
		return data;
	}

}
