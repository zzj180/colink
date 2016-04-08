package com.aispeech.aios.adapter.ui.view;


public interface ITouchEventNotify {

	public void notifyEventAll(GridChartView chart);
	
	public void addNotify(ITouchEventResponse notify);
	
	public void removeNotify(int i);
	
	public void removeAllNotify();
}
