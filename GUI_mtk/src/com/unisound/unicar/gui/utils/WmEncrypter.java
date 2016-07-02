package com.unisound.unicar.gui.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.security.MessageDigest;

public class WmEncrypter {
	 public static String encryptByMD5(String paramString)
	  {
	    try
	    {
	      byte[] arrayOfByte = paramString.getBytes();
	      MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
	      localMessageDigest.update(arrayOfByte);
	      String str = WmMath.translateHex(localMessageDigest.digest());
	      return str;
	    }
	    catch (Exception localException)
	    {
	      localException.printStackTrace();
	    }
	    return "";
	  }
	 
	 public static boolean write(File paramFile, String paramString)
	  {
	    if (paramFile.exists())
	      return false;
	    try
	    {
	      BufferedWriter localBufferedWriter = new BufferedWriter(new FileWriter(paramFile));
	      localBufferedWriter.write(paramString);
	      localBufferedWriter.flush();
	      localBufferedWriter.close();
	      return true;
	    }
	    catch (Exception localException)
	    {
	      localException.printStackTrace();
	    }
	    return false;
	  }
}
