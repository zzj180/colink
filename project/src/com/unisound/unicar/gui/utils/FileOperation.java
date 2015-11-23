package com.unisound.unicar.gui.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import android.os.Environment;
import android.util.Log;

public class FileOperation {

	private static final String TAG = FileOperation.class.getName();
	public static final String DEFAULT_FILENAME = "phonebook.cop";
	
	
	/**
	 * 创建文件
	 * 
	 * @param fileName
	 * @return
	 */
	public static boolean createFile(File fileName) {
		boolean flag = false;
		try {
			if (!fileName.exists()) {
				fileName.createNewFile();
				flag = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 * 读TXT文件内容
	 * 
	 * @param fileName
	 * @return
	 */
	public static String readTxtFile(File fileName) throws Exception {
		String result = null;
		FileReader fileReader = null;
		BufferedReader bufferedReader = null;
		try {
			fileReader = new FileReader(fileName);
			bufferedReader = new BufferedReader(fileReader);
			try {
				String read = null;
				while ((read = bufferedReader.readLine()) != null) {
					result = result + read + "\r\n";
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bufferedReader != null) {
				bufferedReader.close();
			}
			if (fileReader != null) {
				fileReader.close();
			}
		}
		System.out.println("读取出来的文件内容是：" + "\r\n" + result);
		return result;
	}

    private static FileOutputStream fos = null;
	
    public static boolean writeContacts(String content){
    	Log.d(TAG, "content:"+content);
    	if(!DeviceTool.isSdCardExist()){
    		Log.w(TAG, "!--->SDCard is not exist!");
    		return false;
    	}
    	File file = new File(Environment.getExternalStorageDirectory(),  
        		FileOperation.DEFAULT_FILENAME); 
    	return writeTxtFile(content, file);
    }
    
    
	public static boolean writeTxtFile(String content, File fileName){
		boolean flag = false;
		createFile(fileName);
		try {
			if(fos == null){
				fos = new FileOutputStream(fileName);
			}
			fos.write(content.getBytes("utf-8"));
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(fos != null){
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return flag;
	}
	
	public static void open(){
		
	}
	
	public static void close(){
		try {
			if(fos != null){
				fos.close();
				fos = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void contentToTxt(String filePath, String content) {
		String str = new String(); // 原有txt内容
		String s1 = new String();// 内容更新
		try {
			File f = new File(filePath);
			if (f.exists()) {
				System.out.print("文件存在");
			} else {
				System.out.print("文件不存在");
				f.createNewFile();// 不存在则创建
			}
			BufferedReader input = new BufferedReader(new FileReader(f));

			while ((str = input.readLine()) != null) {
				s1 += str + "\n";
			}
			System.out.println(s1);
			input.close();
			s1 += content;

			BufferedWriter output = new BufferedWriter(new FileWriter(f));
			output.write(s1);
			output.close();
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

}
