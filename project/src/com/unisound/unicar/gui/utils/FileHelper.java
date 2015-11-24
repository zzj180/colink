package com.unisound.unicar.gui.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;

import org.apache.http.util.EncodingUtils;

import android.graphics.Bitmap;
import android.os.Environment;
import android.text.TextUtils;

/**
 * @author Brant
 * @decription
 */
public class FileHelper {
	private String mPath;
	private File mFile;

	public FileHelper(String path) {
		path = path == null ? "" : path;
		mPath = path;
		mFile = new File(path);
	}

	public FileHelper(File dir, String fileName) {
		mPath = dir.getAbsolutePath() + File.separator + fileName;
		mFile = new File(dir, fileName);
	}

	public File getFileInstace() {
		if (mFile != null) return mFile;
		mFile = new File(mPath);
		return mFile;
	}

	public String getPath() {
		return mPath;
	}

	/**
	 * 获取指定目录下相应文件的内容
	 * 
	 * @param fileName
	 * @return
	 * @throws FileNotFoundException
	 */
	public String getFileContent() throws FileNotFoundException {
		int length = getFileLength(mFile);
		byte[] buffer = new byte[length];
		FileInputStream mFileInputStream = new FileInputStream(mFile);
		try {
			mFileInputStream.read(buffer, 0, length);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				mFileInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return byteArrayToString(buffer, "UTF-8");
	}

	/**
	 * 向指定的文件写入内容
	 * 
	 * @param fileName
	 * @param append
	 * @param outText
	 * @return
	 */
	public boolean writeToFile(String outText, boolean append) {

		// 判断是否存在,不存在则创建
		FileOutputStream mFileOutputStream = null;
		File dir = new File(getParentPath());
		try {
			if (!dir.exists()) {
				dir.mkdirs();
			}

			if (!mFile.exists()) {
				mFile.createNewFile();
			}
			mFileOutputStream = new FileOutputStream(mFile, append);
			mFileOutputStream.write(outText.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (mFileOutputStream != null) mFileOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	/**
	 * 将字节数组写入指定文件
	 * 
	 * @param data
	 * @param append
	 * @return
	 */
	public boolean writeToFile(byte[] data, boolean append) {
		FileOutputStream mFileOutputStream = null;
		// 判断是否存在,不存在则创建
		File dir = new File(getParentPath());
		if (!dir.exists()) {
			dir.mkdirs();
		}
		try {
			if (!mFile.exists()) {
				mFile.createNewFile();
			}
			mFileOutputStream = new FileOutputStream(new File(mPath), append);
			mFileOutputStream.write(data);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (mFileOutputStream != null) mFileOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	/**
	 * 将图片缓存到文件中
	 * 
	 * @param bitmap
	 * @param format
	 * @param quality
	 * @return
	 */
	public boolean saveImage(Bitmap bitmap, Bitmap.CompressFormat format, int quality) {
		if (bitmap == null) return false;
		FileOutputStream mFileOutputStream = null;
		try {
			createDirs(mPath);
			mFileOutputStream = new FileOutputStream(getFileInstace());
			bitmap.compress(format, quality, mFileOutputStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (mFileOutputStream != null) mFileOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	/**
	 * 根据路径创建文件夹
	 * 
	 * @param fullFileName
	 * @return
	 */
	public boolean createDirs(String fullFileName) {
		String dirPath = fullFileName.substring(0, fullFileName.lastIndexOf("/"));
		String fileName = fullFileName.substring(fullFileName.lastIndexOf("/") + 1);
		File fileDirs = new File(dirPath);
		fileDirs.mkdirs();
		File file = new File(fileDirs, fileName);
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 检查SD卡状态是否就绪
	 * 
	 * @return
	 */
	public static boolean checkExternalStorage() {
		String state = Environment.getExternalStorageState();
		boolean mExternalStorageAvailable, mExternalStorageWriteable;
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			mExternalStorageAvailable = true;
			mExternalStorageWriteable = false;
		} else {
			mExternalStorageAvailable = mExternalStorageWriteable = false;
		}
		return mExternalStorageAvailable && mExternalStorageWriteable;
	}

	/**
	 * 检查指定文件是否存在
	 * 
	 * @param fullFileName
	 * 包括路径的文件名
	 * @return
	 */
	public static boolean checkFileExist(String fullFileName) {
		File myFile = new File(fullFileName);
		return myFile.exists();
	}

	/**
	 * 检查基目录中相应文件是否存在
	 * 
	 * @param fileName
	 * 文件名，不包括路径
	 * @return
	 */
	public boolean checkFileExist() {
		return getFileInstace().exists();
	}

	/**
	 * 获取SD卡绝对路径
	 * 
	 * @return
	 */
	private static String getExternalStorageAbsolutePath() {
		return Environment.getExternalStorageDirectory().getAbsolutePath();
	}

	/**
	 * 获取分类缓存文件的路径
	 * 
	 * @return
	 */
	public static String getBaseCacheDirectoryPath(String packageName) {
		return getExternalStorageAbsolutePath() + "/Android/data/" + packageName + "/cache";
	}

	public static String getDownloadDirectoryPath(String appName) {
		return getExternalStorageAbsolutePath() + "/" + appName + "/Downloads";
	}

	/**
	 * 获取内容缓存路径
	 * 
	 * @return
	 */
	public static String getContentCacheDirectoryPath(String packageName) {
		return getBaseCacheDirectoryPath(packageName) + "/categorys";
	}

	/**
	 * 获取图片缓存路径
	 * 
	 * @return
	 */
	public static String getImageCacheDirectoryPath(String packageName) {
		return getBaseCacheDirectoryPath(packageName) + "/images";
	}

	/**
	 * 将字符数组转换成字符串
	 * 
	 * @param buffer
	 * @param charSet
	 * @return
	 */
	public String byteArrayToString(byte[] buffer, String charSet) {
		return EncodingUtils.getString(buffer, charSet);
	}

	/**
	 * 获取文件所在目录的路径
	 * 
	 * @return
	 */
	public String getParentPath() {
		int end = mPath.lastIndexOf(File.separator);
		if (end >= 0) {
			return mPath.substring(0, end);
		} else {
			return ".";
		}
	}

	/**
	 * 获取文件长度
	 * 
	 * @param file
	 * @return
	 */
	public int getFileLength(File file) {
		if (file != null) {
			return (int) file.length();
		}
		return -1;
	}

	/**
	 * 
	 * @return Returns the underlying file descriptor.
	 * @throws FileNotFoundException
	 */
	public FileDescriptor getFD() throws FileNotFoundException {
		FileInputStream mFileInputStream = new FileInputStream(mFile);
		try {
			return mFileInputStream.getFD();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				mFileInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 获取图片压缩格式
	 * 
	 * @param fullFilePathWithExtension
	 * @return
	 */
	public static Bitmap.CompressFormat getImageFileCompressFormat(String fullFilePathWithExtension) {
		if (TextUtils.isEmpty(fullFilePathWithExtension)) throw new RuntimeException(
			"fullFilePathWithExtension can't be null or empty.");
		String pathStr = fullFilePathWithExtension.toLowerCase();
		if (pathStr.endsWith(".jpeg") || pathStr.endsWith(".jpg")) {
			return Bitmap.CompressFormat.JPEG;
		} else {
			return Bitmap.CompressFormat.PNG;
		}
	}

	/**
	 * 获取合适的容量
	 * 
	 * @param sizeInBytes
	 * @return
	 */
	public static String getProperSize(long sizeInBytes) {
		if (sizeInBytes == 0) {
			return "0 K";
		} else if (sizeInBytes < 1024) {
			return "1 K";
		} else if (sizeInBytes < 1024 * 1024) {
			DecimalFormat df = new DecimalFormat("###.00");
			return df.format((float) sizeInBytes / 1024) + " K";
		} else {
			DecimalFormat df = new DecimalFormat("########.00");
			return df.format((float) sizeInBytes / 1024 / 1024) + " M";
		}
	}

	/**
	 * 拷贝文件
	 * 
	 * @param src
	 * @param tar
	 * @throws Exception
	 */
	public void copyFile(File src, File tar) throws Exception {
		if (src.isFile()) {
			InputStream is = new FileInputStream(src);
			OutputStream op = new FileOutputStream(tar);
			BufferedInputStream bis = new BufferedInputStream(is);
			BufferedOutputStream bos = new BufferedOutputStream(op);
			byte[] bt = new byte[8192];
			int len = bis.read(bt);
			while (len != -1) {
				bos.write(bt, 0, len);
				len = bis.read(bt);
			}
			bis.close();
			bos.close();
		} else if (src.isDirectory()) {
			File[] f = src.listFiles();
			tar.mkdirs();
			for (int i = 0; i < f.length; i++) {
				copyFile(f[i].getAbsoluteFile(), new File(tar.getAbsoluteFile() + File.separator + f[i].getName()));
			}
		}
	}

	/**
	 * 删除指定目录及下面的文件
	 * 
	 * @param dir
	 * @return
	 */
	public static boolean deleteDir(File dir) {
		// The directory is now empty so delete it
		if(dir != null) {
		    if (dir.isDirectory()) {
	            String[] children = dir.list();
	            for (int i = 0; i < children.length; i++) {
	                boolean success = deleteDir(new File(dir, children[i]));
	                if (!success) {
	                    return false;
	                }
	            }
	        }
		    return dir.delete();		    
		}
		return false;
	}

	/**
	 * 获得文件夹的大小
	 * 
	 * @param dir
	 * @return
	 */
	public static long getDirLength(File dir) {
		long length = 0;
		if(dir != null) {
		    if (dir.isDirectory()) {
	            String[] children = dir.list();
	            for (int i = 0; i < children.length; i++) {
	                length += getDirLength(new File(dir, children[i]));
	            }
	        } else {
	            // file
	            length += dir.length();
	        }
		}
		return length;
	}
}
