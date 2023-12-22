package SignLib;

import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.security.BouncyCastleDigest;
import com.itextpdf.text.pdf.security.ExternalDigest;
import com.itextpdf.text.pdf.security.ExternalSignature;
import com.itextpdf.text.pdf.security.MakeSignature;
import com.safenetinc.luna.provider.LunaProvider;
import utils.MyUtil;
import java.security.KeyStore;
import java.security.Provider;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream; 


/**
 *
 * @author slee
 */
public class HSMCall {
	static String basePath = null;
    String  SlotPin = null ; 
    int slotNumber = 0;
    MyHSMFactory  hsmFactory = null ; 
    public PdfSignatureAppearance appearance  = null ; 
    String LOGO_PATH = null;
    public ExternalDigest digest = null ; 
    public Certificate[] certificateChain  = null ; 
    public ExternalSignature signature = null ; 
    
    public HSMCall() throws Exception {
       SlotPin = MyUtil.getProperty("slotPin");
       basePath = MyUtil.getProperty("driveName") + MyUtil.getProperty("basePath");
       LOGO_PATH = basePath + "logo\\logo.png";
       slotNumber = Integer.parseInt(MyUtil.getProperty("slotNumber")); 
    }
    
    public void init() {
        try {
            hsmFactory = new MyHSMFactory(); 
        	hsmFactory.connect(slotNumber, SlotPin);        	
        }
        catch (Exception e) {
        	e.printStackTrace();
        }
    } 
    
    public void createAppearance(PdfReader reader, ByteArrayOutputStream outfilestream, int certId, int multi) throws Exception {
    
                PdfStamper stamper;
		
		if (multi == 0)
			stamper = PdfStamper.createSignature(reader, outfilestream, '\0');
		else
			stamper = PdfStamper.createSignature(reader, outfilestream, '\0', null, true);
		
                appearance = stamper.getSignatureAppearance();
		
		appearance.setReason("Signature " + certId);
		appearance.setLocation("Exclusive Networks Signature");
		appearance.setSignatureGraphic(Image.getInstance(LOGO_PATH));
		appearance.setRenderingMode(PdfSignatureAppearance.RenderingMode.GRAPHIC_AND_DESCRIPTION);
                
                if (certId == 0) {
			appearance.setVisibleSignature(new Rectangle(72, 732, 380, 780), 1, "Signature" + certId);
		} else {
			appearance.setVisibleSignature(new Rectangle(72, 632, 380, 680), 1, "Signature" + certId);
		}
					
    } 
    
    public void createDigest() throws Exception { 
        
    	digest = new BouncyCastleDigest();
        
    }
            
    public void createCertificateChain(String signerCertLabel) throws Exception {
        
    	certificateChain = hsmFactory.getCertificateChain(signerCertLabel);
        
    }
    
    public void createSignature(String signerPKeyLabel) throws Exception { 
        
    	signature = new MyHSMSignature(hsmFactory, signerPKeyLabel);   
        
    }
            
    
    public void sign(PdfSignatureAppearance iappearance, ExternalDigest idigest, ExternalSignature isignature, Certificate[] icertificateChain) throws Exception {
    	
    	MakeSignature.signDetached(iappearance, idigest, isignature, icertificateChain, null, null, null, 0, null);
    	
    }
    
    //Import p12 file - keypair set to HSM
    public boolean importP12(String certLocation, String filePassword, int slot, String keyLabel) {
    	ByteArrayInputStream slotNumber = new ByteArrayInputStream(("slot:" + slot).getBytes());
    	
    	try {
	    	System.out.println("Access P12 File");
	    	String p12PasswordIn = filePassword;
			String p12PasswordOut = filePassword;
			
			KeyStore p12Keystore = KeyStore.getInstance("PKCS12");
			InputStream inputFile = new FileInputStream(certLocation);
			p12Keystore.load(inputFile, p12PasswordIn.toCharArray());
			
			//Get required attributes of private key
			String p12Alias = p12Keystore.aliases().nextElement();
			Certificate cert = p12Keystore.getCertificate(p12Alias);
			//PublicKey publicKey = cert.getPublicKey();
			Certificate[] chain = p12Keystore.getCertificateChain(p12Alias);
			RSAPrivateKey privateKey = (RSAPrivateKey) p12Keystore.getKey(p12Alias, p12PasswordIn.toCharArray());
	    	
	        System.out.println( "Initialize Safenet Keystore" );
	        
	        /* make sure that we have access to the safenet provider */
			Provider p = new LunaProvider();
			Security.addProvider(p);
			
			/* get the safenet keystore - access to the adapter */
			KeyStore keyStore = KeyStore.getInstance("Luna", p.getName());
			
			/* LOAD the keystore from the adapter - presenting the password if required */
			if (SlotPin == null)
			{
				keyStore.load(slotNumber, null);
			}
			else
			{
				keyStore.load(slotNumber, SlotPin.toCharArray());
			}
	        
			System.out.println( "Successfully Logged in to Keystore" );
			
			System.out.println( "Check if alias name already Exist in keystore" );
			if (keyStore.containsAlias(keyLabel))
			{
				System.out.println("");
				System.out.println("Key name already exists");
				System.out.println("");
				
				System.exit(1);
			}
			
			System.out.println( "Try to save key to HSM" );
			keyStore.setKeyEntry(keyLabel, privateKey, p12PasswordOut.toCharArray(), chain);
			System.out.println( "Private Key saved successfully" );
			
			return true;
    	} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
    }
    
}
