/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package SignLib;

import java.util.*;
import com.itextpdf.text.pdf.PdfReader;
//import hk.com.toplevel.trustsafe.pdf.HSMSigner; 
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 *
 * @author scng
 */
public class testMT extends Thread{

    /**
     * @param args the command line arguments
     */

   int threadNo  ;
   public static int returnCount;
   int counter ;
   public PdfReader reader ; 
   public HSMCall signer ;
   
   public String basePath = "A:\\TEST\\";
   
   public String SIGNER1_CERTIF_LABEL = "Testing eCheque"; 
   public String SIGNER1_PKEY_LABEL = "PrivateKey";
   public String LOGO_PATH = basePath + "logo\\logo.png";
   public String OUTPUT_PATH = basePath + "output\\";
   public String OUTPUTFILE = basePath + "input\\";
   public String PDF_35KB = basePath + "pdf\\35KB.pdf";
   //public String PDF_100KB = "C:/SigningClientSample/pdf/100KB.pdf";
   public String PDF_150KB = basePath + "pdf\\150KB.pdf";
   public String PDF_200KB = basePath + "pdf\\200KB.pdf";
   public byte fileContent[] = null ; 
   public ByteArrayOutputStream bOutput = null ; 
   
   public testMT(int num, int loop) throws Exception
   {
          threadNo = num ;
          counter = loop ;
          
          signer = new HSMCall(); 
          signer.init();
          FileOutputStream outputfile = new FileOutputStream(OUTPUTFILE);
          FileInputStream inputStream = new FileInputStream(PDF_35KB) ;
          fileContent = new byte[(int)inputStream.available()];
          inputStream.read(fileContent);
          signer.createDigest();
          signer.createCertificateChain(SIGNER1_CERTIF_LABEL);
          signer.createSignature(SIGNER1_PKEY_LABEL);
          
   }

   public static void main(String[] args) {
        // TODO code application logic here
	       
        testMT   mt[]  ;
       // int numThread = Integer.parseInt(args[0]) ;
       // int loop = Integer.parseInt(args[1]) ;
       
        int numThread = 1 ;
        int loop = 20 ;
        
        try {

            
        //HSMSigner signer = new  HSMSigner() ;
        // init() should only be called once in a Java code
        //signer.init(); 
             
            
        mt  = new testMT[numThread] ;

        for (int i=0 ; i < numThread ; i++)
        {
         mt[i] = new testMT(i,loop) ;
        }

         long starttime, endtime ;

          starttime = System.currentTimeMillis();

         returnCount = 0 ;

         
         for(int i=0 ; i < numThread ; i++)
         {
            mt[i].start() ;
            Thread.sleep(20) ;
         }

         while (returnCount < numThread)
         {
   	  Thread.sleep(100);
         }

          endtime = System.currentTimeMillis();

          //sps.close(); 
          
          System.out.println("Total time : " + (endtime-starttime) + " ms") ;
          System.out.println("Total transaction : "  +  numThread*loop) ;
          System.out.println("Transaction per sec : " + 1000*numThread*((double)loop)/((double)(endtime-starttime)));

         } catch (Throwable th) {
         th.printStackTrace();
         }

   }


 public void run() {

        try {

        System.out.println("Thread Start :  " + threadNo ) ;

        int    FILESIZE =  300000; 
        
        for (int i=0 ; i < counter ; i++)
        { 
        reader = new PdfReader(fileContent);    
        bOutput = new ByteArrayOutputStream(FILESIZE);
        signer.createAppearance(reader, bOutput, 0, 0);     
        signer.sign(signer.appearance, signer.digest, signer.signature, signer.certificateChain);
        } 
        
        //OutputStream outputStream = new FileOutputStream (OUTPUTFILE); 
        //bOutput.writeTo(outputStream);
       
         returnCount++ ;

        } catch (Exception e)
        {
           System.out.println(e.getMessage());
        }

           
    }


    private static String BinToHex(byte[] bArray) {

        String lookup = "0123456789ABCDEF";
        StringBuffer s = new StringBuffer(bArray.length * 2);

        for (int i = 0; i < bArray.length; i++)
        {
            s.append(lookup.charAt((bArray[i] >>> 4) & 0x0f));
            s.append(lookup.charAt(bArray[i] & 0x0f));
        }

        return s.toString();

    }

   private static byte[] HexToBin(String input) {

         byte[]    bytes;

        bytes = new byte[input.length() / 2];

        for (int i = 0; i < bytes.length; i++)
        {
            bytes[i] = (byte)Integer.parseInt(input.substring(2 * i, 2 * i + 2), 16);
        }

        return bytes;
    }

  
}
