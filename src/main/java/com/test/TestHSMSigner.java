package com.test;
import com.itextpdf.text.pdf.PdfReader;

import SignLib.HSMCall;
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
        
	static String basePath = null;
	static String LOGO_PATH = null;
	static String OUTPUT_PATH = null;
	static String OUTPUTFILE = null;
        
	static public String PDF_35KB = null;
	static public String PDF_150KB = null;
	static public String PDF_200KB = null;
    static final int    FILESIZE =  300000; 
        
    /**
     * @param args the command line arguments
     */
        
   public static void main(String[] args) throws Exception {
	   
	   	basePath = MyUtil.getProperty("driveName") + MyUtil.getProperty("basePath");
		LOGO_PATH = basePath + "logo.png";
		OUTPUT_PATH = basePath + "output\\";
		OUTPUTFILE = basePath + "output\\resultkemenkeu.pdf";
	        
		String PDF_35KB = basePath + "pdf\\test.pdf";
		
	   			
        HSMCall myHsm = new HSMCall(); 
        myHsm.init();
        
        FileInputStream  inputStream = new FileInputStream(PDF_35KB) ;
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
