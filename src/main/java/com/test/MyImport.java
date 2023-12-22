package com.test;
/////////////////////////////////////////WORKING Class///////////////////////

import SignLib.HSMCall;
import utils.MyUtil;

public class MyImport
{
	final static String fileVersion = "Sumardi Version";
    static final int READ_BUFFER = 50;
   
    public static void main( String[] args ) throws Exception
    {
    	HSMCall myHsm = new HSMCall();
    	myHsm.init();
    	
        String filePassword = "pus1ntek";
        String keyLabel = "MyKey";
        int slotNumber = 0;
    	String basePath = MyUtil.getProperty("driveName");
    	String certificateName = basePath + "testing_pusintek.p12";
    	
    	myHsm.importP12(certificateName, filePassword, slotNumber, keyLabel);
    }
}
