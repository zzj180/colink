
package android_serialport_api;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SerialPort {
	private final static String TAG = "serial_app";
	
	private FileDescriptor mFd;
	private FileInputStream mFileInputStream;
	private FileOutputStream mFileOutputStream;
	public SerialPort(String device, int baudrate, int flags)
			throws SecurityException, IOException {
		
		mFd = open(device, baudrate, flags);
		if (mFd == null) {
			throw new IOException();
		}
		mFileInputStream = new FileInputStream(mFd);
		mFileOutputStream = new FileOutputStream(mFd);
	}
	
	// Getters and setters
	public InputStream getInputStream() {
		return mFileInputStream;
	}

	public OutputStream getOutputStream() {
		return mFileOutputStream;
	}
	
	// JNI
	private native static FileDescriptor open(String path, int baudrate,
			int flags);

	public native void close();

	static {
		System.loadLibrary("serial_port");
	}
}
