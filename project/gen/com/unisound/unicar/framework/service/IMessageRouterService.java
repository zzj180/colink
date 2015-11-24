/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: F:\\vui36\\project\\src\\com\\unisound\\unicar\\framework\\service\\IMessageRouterService.aidl
 */
package com.unisound.unicar.framework.service;
public interface IMessageRouterService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.unisound.unicar.framework.service.IMessageRouterService
{
private static final java.lang.String DESCRIPTOR = "com.unisound.unicar.framework.service.IMessageRouterService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.unisound.unicar.framework.service.IMessageRouterService interface,
 * generating a proxy if needed.
 */
public static com.unisound.unicar.framework.service.IMessageRouterService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.unisound.unicar.framework.service.IMessageRouterService))) {
return ((com.unisound.unicar.framework.service.IMessageRouterService)iin);
}
return new com.unisound.unicar.framework.service.IMessageRouterService.Stub.Proxy(obj);
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
case TRANSACTION_sendMessage:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.sendMessage(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_registerCallback:
{
data.enforceInterface(DESCRIPTOR);
com.unisound.unicar.framework.service.IMessageRouterCallback _arg0;
_arg0 = com.unisound.unicar.framework.service.IMessageRouterCallback.Stub.asInterface(data.readStrongBinder());
this.registerCallback(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_getCallback:
{
data.enforceInterface(DESCRIPTOR);
com.unisound.unicar.framework.service.IMessageRouterCallback _result = this.getCallback();
reply.writeNoException();
reply.writeStrongBinder((((_result!=null))?(_result.asBinder()):(null)));
return true;
}
case TRANSACTION_isServiceStart:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.isServiceStart();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_startMessageRoute:
{
data.enforceInterface(DESCRIPTOR);
this.startMessageRoute();
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.unisound.unicar.framework.service.IMessageRouterService
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
@Override public void sendMessage(java.lang.String messageJson) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(messageJson);
mRemote.transact(Stub.TRANSACTION_sendMessage, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void registerCallback(com.unisound.unicar.framework.service.IMessageRouterCallback callback) throws android.os.RemoteException
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
@Override public com.unisound.unicar.framework.service.IMessageRouterCallback getCallback() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
com.unisound.unicar.framework.service.IMessageRouterCallback _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getCallback, _data, _reply, 0);
_reply.readException();
_result = com.unisound.unicar.framework.service.IMessageRouterCallback.Stub.asInterface(_reply.readStrongBinder());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public boolean isServiceStart() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_isServiceStart, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void startMessageRoute() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_startMessageRoute, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_sendMessage = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_registerCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_getCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_isServiceStart = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_startMessageRoute = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
}
public void sendMessage(java.lang.String messageJson) throws android.os.RemoteException;
public void registerCallback(com.unisound.unicar.framework.service.IMessageRouterCallback callback) throws android.os.RemoteException;
public com.unisound.unicar.framework.service.IMessageRouterCallback getCallback() throws android.os.RemoteException;
public boolean isServiceStart() throws android.os.RemoteException;
public void startMessageRoute() throws android.os.RemoteException;
}
