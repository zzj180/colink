/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: F:\\vui36\\gui_seria\\src\\com\\unisound\\unicar\\fm\\service\\IUniDriveFmService.aidl
 */
package com.unisound.unicar.fm.service;
public interface IUniDriveFmService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.unisound.unicar.fm.service.IUniDriveFmService
{
private static final java.lang.String DESCRIPTOR = "com.unisound.unicar.fm.service.IUniDriveFmService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.unisound.unicar.fm.service.IUniDriveFmService interface,
 * generating a proxy if needed.
 */
public static com.unisound.unicar.fm.service.IUniDriveFmService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.unisound.unicar.fm.service.IUniDriveFmService))) {
return ((com.unisound.unicar.fm.service.IUniDriveFmService)iin);
}
return new com.unisound.unicar.fm.service.IUniDriveFmService.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_playVoiceByKeyword:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.playVoiceByKeyword(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_playVoice:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
java.lang.String _arg2;
_arg2 = data.readString();
this.playVoice(_arg0, _arg1, _arg2);
reply.writeNoException();
return true;
}
case TRANSACTION_playControl:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.playControl(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_playHistory:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.playHistory(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_playSeek:
{
data.enforceInterface(DESCRIPTOR);
long _arg0;
_arg0 = data.readLong();
this.playSeek(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getPlayerStatus:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getPlayerStatus();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_registerCallback:
{
data.enforceInterface(DESCRIPTOR);
com.unisound.unicar.fm.service.IUniDriveFmCallback _arg0;
_arg0 = com.unisound.unicar.fm.service.IUniDriveFmCallback.Stub.asInterface(data.readStrongBinder());
this.registerCallback(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.unisound.unicar.fm.service.IUniDriveFmService
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public void playVoiceByKeyword(java.lang.String keyword) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(keyword);
mRemote.transact(Stub.TRANSACTION_playVoiceByKeyword, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void playVoice(java.lang.String artist, java.lang.String category, java.lang.String keyword) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(artist);
_data.writeString(category);
_data.writeString(keyword);
mRemote.transact(Stub.TRANSACTION_playVoice, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/** 1-up; 2-next; 3-play; 4-pause; 5-stop; 6-exit;*/
@Override public void playControl(int status) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(status);
mRemote.transact(Stub.TRANSACTION_playControl, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**1-continue play last voice; 2-play next voice of last play list */
@Override public void playHistory(int status) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(status);
mRemote.transact(Stub.TRANSACTION_playHistory, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void playSeek(long milliseconds) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeLong(milliseconds);
mRemote.transact(Stub.TRANSACTION_playSeek, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/** 
     STATE_IDLE = 0;
     STATE_INITIALIZED = 1;
     STATE_PREPARED = 2;
     STATE_STARTED = 3;
     STATE_STOPPED = 4;
     STATE_PAUSED = 5;
     STATE_COMPLETED = 6;
     STATE_ERROR = 7;
     STATE_END = 8;
     STATE_PREPARING = 9;
     STATE_ADS_BUFFERING = 10;
     STATE_PLAYING_ADS = 11;
     */
@Override public int getPlayerStatus() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getPlayerStatus, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void registerCallback(com.unisound.unicar.fm.service.IUniDriveFmCallback callback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_registerCallback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_playVoiceByKeyword = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_playVoice = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_playControl = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_playHistory = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_playSeek = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_getPlayerStatus = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_registerCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
}
public void playVoiceByKeyword(java.lang.String keyword) throws android.os.RemoteException;
public void playVoice(java.lang.String artist, java.lang.String category, java.lang.String keyword) throws android.os.RemoteException;
/** 1-up; 2-next; 3-play; 4-pause; 5-stop; 6-exit;*/
public void playControl(int status) throws android.os.RemoteException;
/**1-continue play last voice; 2-play next voice of last play list */
public void playHistory(int status) throws android.os.RemoteException;
public void playSeek(long milliseconds) throws android.os.RemoteException;
/** 
     STATE_IDLE = 0;
     STATE_INITIALIZED = 1;
     STATE_PREPARED = 2;
     STATE_STARTED = 3;
     STATE_STOPPED = 4;
     STATE_PAUSED = 5;
     STATE_COMPLETED = 6;
     STATE_ERROR = 7;
     STATE_END = 8;
     STATE_PREPARING = 9;
     STATE_ADS_BUFFERING = 10;
     STATE_PLAYING_ADS = 11;
     */
public int getPlayerStatus() throws android.os.RemoteException;
public void registerCallback(com.unisound.unicar.fm.service.IUniDriveFmCallback callback) throws android.os.RemoteException;
}
