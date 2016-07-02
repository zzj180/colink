package com.unisound.unicar.gui.utils;

public class WmMath {
	public static String translateHex(byte[] digest) {
		return translateHex(digest, 0, digest.length);
	}
	 public static String translateHex(byte[] paramArrayOfByte, int paramInt)
	  {
	    return translateHex(paramArrayOfByte, 0, paramInt);
	  }
	 public static String translateHex(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
	  {
	    String str = "";
	    for (int i = 0; i < paramInt2; i++)
	      str = str + translateHex(0xFF & paramArrayOfByte[(paramInt1 + i)]);
	    return str;
	  }
	 public static String translateHex(int paramInt)
	  {
	    String str = "" + translateHexEx(paramInt >>> 4);
	    return str + translateHexEx(paramInt & 0xF);
	  }
	 public static char translateHexEx(int paramInt)
	  {
	    if (paramInt < 10)
	      return (char)(paramInt + 48);
	    if ((paramInt >= 10) && (paramInt <= 16))
	      return (char)(65 + (paramInt - 10));
	    return '0';
	  }
}
