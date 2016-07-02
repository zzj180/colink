package com.unisound.unicar.framework.service;
import com.unisound.unicar.framework.service.IMessageRouterCallback;  

interface IMessageRouterService {
	void sendMessage(String messageJson);
	void registerCallback(IMessageRouterCallback callback);
	IMessageRouterCallback getCallback();
	boolean isServiceStart();
	void startMessageRoute();
}