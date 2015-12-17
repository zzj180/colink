package com.colink.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class SerializableUtil {

/*	public static <E> String list2String(List<E> list) throws IOException {
		// 实例化一个ByteArrayOutputStream对象，用来装载压缩后的字节文件
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// 然后将得到的字符数据装载到ObjectOutputStream
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		// writeObject 方法负责写入特定类的对象的状态，以便相应的readObject可以还原它
		oos.writeObject(list);
		// 最后，用Base64.encode将字节文件转换成Base64编码，并以String形式保存
		String listString = new String(Base64.encode(baos.toByteArray(),
				Base64.DEFAULT));
		// 关闭oos
		oos.close();
		return listString;
	}

	public static String obj2Str(Object obj) throws IOException {
		if (obj == null) {
			return "";
		}
		// 实例化一个ByteArrayOutputStream对象，用来装载压缩后的字节文件
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		// 然后将得到的字符数据装载到ObjectOutputStream
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		// writeObject 方法负责写入特定类的对象的状态，以便相应的readObject可以还原它
		oos.writeObject(obj);
		// 最后，用Base64.encode将字节文件转换成Base64编码，并以String形式保存
		String listString = new String(Base64.encode(baos.toByteArray(),
				Base64.DEFAULT));
		// 关闭oos
		oos.close();
		return listString;
	}

	// 将序列化的数据还原成Object
	public static Object str2Obj(String str) throws StreamCorruptedException,
			IOException {
		byte[] mByte = Base64.decode(str.getBytes(), Base64.DEFAULT);
		ByteArrayInputStream bais = new ByteArrayInputStream(mByte);
		ObjectInputStream ois = new ObjectInputStream(bais);

		try {
			return ois.readObject();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	@SuppressWarnings("unchecked")
	public static <E> List<E> string2List(String str)
			throws StreamCorruptedException, IOException {
		byte[] mByte = Base64.decode(str.getBytes(), Base64.DEFAULT);
		ByteArrayInputStream bais = new ByteArrayInputStream(mByte);
		ObjectInputStream ois = new ObjectInputStream(bais);
		List<E> stringList = null;
		try {
			stringList = (List<E>) ois.readObject();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return stringList;
	}*/
	
	public static String getPinYin(String zhongwen)
			throws BadHanyuPinyinOutputFormatCombination {
		String zhongWenPinYin = "";
		char[] chars = zhongwen.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			if(chars[i] >= 'a' && chars[i] <= 'z'){
				chars[i]=(char) (chars[i] - 'a' + 'A');
			}
			String[] pinYin = PinyinHelper.toHanyuPinyinStringArray(chars[i],
					getDefaultOutputFormat());
			if (pinYin != null) {
				zhongWenPinYin += pinYin[0];
			} else {
				zhongWenPinYin += chars[i];
			}
		}
		return zhongWenPinYin;
	}
	private static HanyuPinyinOutputFormat getDefaultOutputFormat() {
		HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
		format.setCaseType(HanyuPinyinCaseType.UPPERCASE);
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		format.setVCharType(HanyuPinyinVCharType.WITH_U_AND_COLON);
		return format;
	}
	
}
