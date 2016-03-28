package com.zzj.colink.fileupload;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpStatus;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

public class FileUpload {
	
	private Handler handler;
	
	@SuppressLint("HandlerLeak")
	public FileUpload(final IuploadState iuploadState) {
		this.handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case 0:
					// 连接超时
					iuploadState.uploadFail();
					break;
				case 1:
					// 上传成功，看服务返回的结果...........
					iuploadState.uploadSuccess();
					break;
					
				case 2:
					iuploadState.connOver();
					break;
				default:
					break;
				}
			}
		};
	}
	public FileUpload(Context context) {
	}
	/**
	 * 上传
	 * 
	 * @param urlStr
	 * @param textMap
	 * @param fileMap
	 * @return
	 */
	public void formUpload(final String urlStr, final String path,final String imei,final String text) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				
				HttpURLConnection conn = null;
				String BOUNDARY = "---------------------------137286239451234"; // boundary就是request头和上传文件内容的分隔符
				try {
					URL url = new URL(urlStr);
					conn = (HttpURLConnection) url.openConnection();
					conn.setConnectTimeout(60000);
					conn.setReadTimeout(20000);
					Log.v("uploadState", "setChunkedStreamingMode");
					conn.setChunkedStreamingMode(1024);
					Log.v("uploadState", "down");
					conn.setDoOutput(true);
					conn.setDoInput(true);
					conn.setUseCaches(false);
					conn.setRequestMethod("POST");
					conn.setRequestProperty("Connection", "Keep-Alive");
					conn.setRequestProperty("User-Agent","Mozilla/5.0 (Windows; U; Windows NT 6.1; zh-CN; rv:1.9.2.6)");
					conn.setRequestProperty("Content-Type","multipart/form-data; boundary=" + BOUNDARY);
					DataOutputStream out = new DataOutputStream(conn.getOutputStream());
					StringBuffer strBuf = new StringBuffer();
					strBuf.append("--" + BOUNDARY + "\r\n");  
					strBuf.append("Content-Disposition: form-data; name=\"imei\"" + "\r\n\r\n"+imei);  
					if(!TextUtils.isEmpty(text)){
						strBuf.append("\r\n--"+ BOUNDARY + "\r\n");
						strBuf.append("Content-Disposition: form-data; name=\"text\"\r\n\r\n"+text);
					}
					out.write(strBuf.toString().getBytes());
					strBuf = null;
					System.gc();
					// file
					if (!TextUtils.isEmpty(path)) {
						ArrayList <File> listFile = getFile(path);
						for (File file : listFile) {
							String filename = file.getName();
							strBuf = new StringBuffer();
							strBuf.append("\r\n--"+ BOUNDARY + "\r\n");
							strBuf.append("Content-Disposition: form-data; name=\"data\"; filename=\"" + filename + "\"\r\n");
							strBuf.append("Content-Type:text/plain\r\n\r\n");
							out.write(strBuf.toString().getBytes());
							strBuf = null;
							System.gc();
							InputStream in = new FileInputStream(file);
							int bytes = 0;
							byte[] bufferOut = new byte[1024];
							while ((bytes = in.read(bufferOut)) != -1) {
								out.write(bufferOut, 0, bytes);
							}
							in.close();
						}
					}
					byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
					out.write(endData);
					out.flush();
					out.close();
					// 读取返回数据
					if(conn.getResponseCode() == HttpStatus.SC_OK){
						handler.sendEmptyMessage(1);
					}else{
						handler.sendEmptyMessage(0);  
					}
					/*StringBuffer strBuf = new StringBuffer();
					BufferedReader reader = new BufferedReader(new InputStreamReader(
							conn.getInputStream()));
					String line = null;
					while ((line = reader.readLine()) != null) {
						strBuf.append(line).append("\n");
					}
					res = strBuf.toString();
					reader.close();
					reader = null;*/
				} catch (Exception e) {
					e.printStackTrace();
					handler.sendEmptyMessage(0);  
				} finally {
					if (conn != null) {
						conn.disconnect();
						conn = null;
					}
					handler.sendEmptyMessage(2);
				}
				
			}
		}).start();
		
	}

	 private ArrayList<File> getFile(String fileAbsolutePath) {
		ArrayList<File> listFile = new ArrayList<File>();
		File file = new File(fileAbsolutePath);
		File[] subFile = file.listFiles();

		for (int iFileLength = 0; iFileLength < subFile.length; iFileLength++) {
			// 判断是否为文件夹
			if (!subFile[iFileLength].isDirectory()) {
				listFile.add(subFile[iFileLength]);
			}
		}
		return listFile;
	}
	public interface IuploadState {
		/**
		 * @param success
		 *            上传成功...........
		 */
		public void uploadSuccess();

		/**
		 * 连接超时
		 */
		public void uploadFail();
		
		public void connOver();
	}

}
