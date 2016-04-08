package com.aispeech.aios.adapter.localScanService.service;


/**
 * @desc 线程池管理类
 * @auth AISPEECH
 * @date 2016-02-19
 * @copyright aispeech.com
 */
public class ThreadPoolManager
{

	private static ThreadPoolProxy	mLongPool;					// 耗时操作的池子
	private static Object			mLongLock	= new Object();

	/**
	 * 获得耗时操作的池子
	 * 
	 * @return
	 */
	public static ThreadPoolProxy getLongPool()
	{
		if (mLongPool == null)
		{
			synchronized (mLongLock)
			{
				if (mLongPool == null)
				{
					mLongPool = new ThreadPoolProxy(1, 1, 0L);
				}
			}
		}
		return mLongPool;
	}

}
