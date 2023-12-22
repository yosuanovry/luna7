
package SignLib;

import com.itextpdf.text.pdf.PdfReader;

import utils.MyUtil;

import java.io.FileOutputStream;
import java.io.ByteArrayOutputStream; 
import java.io.FileInputStream;
import java.io.OutputStream; 

/**
 *
 * @author slee
 */
public class TestHSMSigner {
    static final String SIGNER1_CERTIF_LABEL = "PusintekKey";
	static final String SIGNER1_PKEY_LABEL = "PusintekKey";
        
	static String basePath = "A:\\TEST\\";
	static String LOGO_PATH = basePath + "logo.png";
	static String OUTPUT_PATH = basePath + "output\\";
	static String OUTPUTFILE = basePath + "output\\result.pdf";
        
//	static public String PDF_35KB = basePath + "pdf\\35KB.pdf";
//	static public String PDF_150KB = basePath + "pdf\\150KB.pdf";
//	static public String PDF_200KB = basePath + "pdf\\200KB.pdf";
    static final int    FILESIZE =  300000; 
        
    /**
     * @param args the command line arguments
     */
        
   public static void main(String[] args) throws Exception {
	   
	   	basePath = MyUtil.getProperty("driveName") + MyUtil.getProperty("basePath");
		LOGO_PATH = basePath + "logo.png";
		OUTPUT_PATH = basePath + "output\\";
		OUTPUTFILE = basePath + "output\\result.pdf";
	        
		//String PDF_35KB = basePath + "pdf\\35KB.pdf";
		String myPDF = basePath + "pdf\\tester.pdf";
	   			
        HSMCall myHsm = new HSMCall(); 
        myHsm.init();
        
        FileInputStream  inputStream = new FileInputStream(myPDF) ;
        PdfReader reader = new PdfReader(inputStream);
        ByteArrayOutputStream bOutput = new ByteArrayOutputStream(FILESIZE);
        myHsm.createAppearance(reader, bOutput, 0, 0);
        myHsm.createDigest();
        myHsm.createCertificateChain(SIGNER1_CERTIF_LABEL);
        myHsm.createSignature(SIGNER1_PKEY_LABEL);
        myHsm.sign(myHsm.appearance, myHsm.digest, myHsm.signature, myHsm.certificateChain);
        
        OutputStream outputStream = new FileOutputStream (OUTPUTFILE); 
        bOutput.writeTo(outputStream);
   }
}
