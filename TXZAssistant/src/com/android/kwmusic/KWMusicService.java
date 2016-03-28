/**
 * @FileName : KWMusicService.java
 * @ProjectName :
 * @PakageName : com.android.kwmusic
 * @Author : zzj
 * @CreateDate : 2016-3-17
 */
package com.android.kwmusic;

import java.util.List;

import com.colink.zzj.txzassistant.R;
import com.colink.zzj.txzassistant.util.Logger;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;
import cn.kuwo.autosdk.api.KWAPI;
import cn.kuwo.autosdk.api.OnPlayerStatusListener;
import cn.kuwo.autosdk.api.OnSearchListener;
import cn.kuwo.autosdk.api.PlayMode;
import cn.kuwo.autosdk.api.PlayState;
import cn.kuwo.autosdk.api.PlayerStatus;
import cn.kuwo.autosdk.api.SearchStatus;
import cn.kuwo.autosdk.bean.Music;

@SuppressLint("HandlerLeak")
public class KWMusicService extends Service implements OnSearchListener {
	private static final String SCRRENOFF_ACTIVITY = "com.zzj.coogo.screenoff.ScrrenoffActivity";
	private static final int MUSIC_SET_DELAYTIME = 500;
	private static final int WHAT_PLAY_PAUSE = 1001;
	private ComponentName remComponenName;

	private KWAPI mKwapi;
	private Context mContext;

	// 由于单曲循环的设置和暂停都不能和播放指令同时发出，所以设置一个延时时间
	private Handler musicHandler;

	Runnable r = new Runnable() {
		@Override
		public void run() {
			if (remComponenName != null) {
				if (remComponenName.getClassName().equals(SCRRENOFF_ACTIVITY)) {
					openDeskLyric();
				} else {
					closeDeskLyric();
				}
				Intent intent = new Intent();
				intent.setComponent(remComponenName);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				try {
					startActivity(intent);
				} catch (Exception e) {
				}
			}
		}
	};

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Logger.i("music = " + action);
			String cmd = intent.getStringExtra(CommandPreference.CMDNAME);

			try {
				if (CommandPreference.CMDTOGGLEPAUSE.equals(cmd)
						|| CommandPreference.TOGGLEPAUSE_ACTION.equals(action)) {
					setPlayState(KWPlayState.STATE_PAUSE);
				} else if (CommandPreference.CMDPAUSE.equals(cmd)
						|| CommandPreference.PAUSE_ACTION.equals(action)) {
					musicHandler.sendEmptyMessageDelayed(WHAT_PLAY_PAUSE,
							MUSIC_SET_DELAYTIME);
				} else if (CommandPreference.CMDPLAY.equals(cmd)) {
					setPlayState(KWPlayState.STATE_PLAY);
				} else if (CommandPreference.CMDPREVIOUS.equals(cmd)
						|| CommandPreference.PREVIOUS_ACTION.equals(action)) {
					setPlayState(KWPlayState.STATE_PRE);
				} else if (CommandPreference.CMDNEXT.equals(cmd)
						|| CommandPreference.NEXT_ACTION.equals(action)) {
					setPlayState(KWPlayState.STATE_NEXT);
				} else if (CommandPreference.CMDSTOP.equals(cmd)) {
					exitKwApp();
				} else if (CommandPreference.CMDOPEN_MUSIC.equals(cmd)) {
					startKwApp();
					// contiuneKwApp();
				} else if (CommandPreference.CMDFULL_CYCLE.equals(cmd)) {
					setPlayModel(KWPlayMode.MODE_ALL_CIRCLE);
				} else if (CommandPreference.CMDSINGLE_CYCLE.equals(cmd)) {
					setPlayModel(KWPlayMode.MODE_SINGLE_CIRCLE);
				} else if (CommandPreference.CMDORDER_PLAYBACK.equals(cmd)) {
					setPlayModel(KWPlayMode.MODE_ALL_ORDER);
				} else if (CommandPreference.CMDSHUFFLE_PLAYBACK.equals(cmd)) {
					setPlayModel(KWPlayMode.MODE_ALL_RANDOM);
				} else if (CommandPreference.CMDOPEN_DESK_LYRIC.equals(cmd)) {
					openDeskLyric();
				} else if (CommandPreference.CMDCLOSE_DESK_LYRIC.equals(cmd)) {
					closeDeskLyric();
				} else if (CommandPreference.ACTION_MUSIC_DATA.equals(action)) {
					String key = intent
							.getStringExtra(CommandPreference.MUSIC_KEY);
					searchOnlineMusic(key);
				} else if (CommandPreference.ACTION_MUSIC_LOCAL.equals(action)) {
					String path = intent
							.getStringExtra(CommandPreference.MUSIC_PATH);
					playLocalMusic(path);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	@Override
	public void onCreate() {
		super.onCreate();
		mKwapi = KWAPI.createKWAPI(this, "auto");
		mContext = this;
		registerPlayListener();
		registReceiver();
		musicHandler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				if (msg.what == WHAT_PLAY_PAUSE) {
					setPlayState(KWPlayState.STATE_PAUSE);
				}
			};
		};
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			String action = intent.getAction();
			if (action != null) {
				String cmd = intent.getStringExtra(CommandPreference.CMDNAME);
				if (CommandPreference.CMDCLOSE_DESK_LYRIC.equals(action)) {
					closeDeskLyric();
				} else if (CommandPreference.CMDOPEN_DESK_LYRIC.equals(action)) {
					openDeskLyric();
				} else if (CommandPreference.CMDTOGGLEPAUSE.equals(cmd)
						|| CommandPreference.TOGGLEPAUSE_ACTION.equals(action)) {
					setPlayState(KWPlayState.STATE_PAUSE);
				} else if (CommandPreference.CMDPAUSE.equals(cmd)
						|| CommandPreference.PAUSE_ACTION.equals(action)) {
					musicHandler.sendEmptyMessageDelayed(WHAT_PLAY_PAUSE,
							MUSIC_SET_DELAYTIME);
				} else if (CommandPreference.CMDPLAY.equals(cmd)) {
					setPlayState(KWPlayState.STATE_PLAY);
				} else if (CommandPreference.CMDPREVIOUS.equals(cmd)
						|| CommandPreference.PREVIOUS_ACTION.equals(action)) {
					setPlayState(KWPlayState.STATE_PRE);
				} else if (CommandPreference.CMDNEXT.equals(cmd)
						|| CommandPreference.NEXT_ACTION.equals(action)) {
					setPlayState(KWPlayState.STATE_NEXT);
				} else if (CommandPreference.CMDSTOP.equals(cmd)) {
					exitKwApp();
				} else if (CommandPreference.CMDOPEN_MUSIC.equals(cmd)) {
					startKwApp();
					// contiuneKwApp();
				} else if (CommandPreference.CMDFULL_CYCLE.equals(cmd)) {
					setPlayModel(KWPlayMode.MODE_ALL_CIRCLE);
				} else if (CommandPreference.CMDSINGLE_CYCLE.equals(cmd)) {
					setPlayModel(KWPlayMode.MODE_SINGLE_CIRCLE);
				} else if (CommandPreference.CMDORDER_PLAYBACK.equals(cmd)) {
					setPlayModel(KWPlayMode.MODE_ALL_ORDER);
				} else if (CommandPreference.CMDSHUFFLE_PLAYBACK.equals(cmd)) {
					setPlayModel(KWPlayMode.MODE_ALL_RANDOM);
				} else if (CommandPreference.CMDOPEN_DESK_LYRIC.equals(cmd)) {
					openDeskLyric();
				} else if (CommandPreference.CMDCLOSE_DESK_LYRIC.equals(cmd)) {
					closeDeskLyric();
				} else if (CommandPreference.ACTION_MUSIC_DATA.equals(action)) {
					String key = intent
							.getStringExtra(CommandPreference.MUSIC_KEY);
					searchOnlineMusic(key);
				} else if (CommandPreference.ACTION_MUSIC_LOCAL.equals(action)) {
					String path = intent
							.getStringExtra(CommandPreference.MUSIC_PATH);
					playLocalMusic(path);
				}

			}
		}
		return START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mKwapi != null) {
			mKwapi.unRegisterPlayerStatusListener(this);
		}
		unregistReceiver();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void searchFinshed(SearchStatus arg0, boolean arg1, List arg2,
			boolean arg3) {
		List<Music> musics = (List<Music>) arg2;
		if (arg0 == SearchStatus.SUCCESS && musics!=null) {
			// 搜索成功，可以将歌曲列表展现出来选择性的播放
			if (musics.size() > 0) {
				mKwapi.playMusic(this, musics.get(0));
			}
		} else {
			Toast.makeText(this, getString(R.string.serch_music_fail),
					Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * 添加action，注册广播
	 */
	private void registReceiver() {
		IntentFilter commandFilter = new IntentFilter();
		commandFilter.setPriority(1100);
		commandFilter.addAction(CommandPreference.SERVICECMD);
		commandFilter.addAction(CommandPreference.TOGGLEPAUSE_ACTION);
		commandFilter.addAction(CommandPreference.PAUSE_ACTION);
		commandFilter.addAction(CommandPreference.NEXT_ACTION);
		commandFilter.addAction(CommandPreference.PREVIOUS_ACTION);
		commandFilter.addAction(CommandPreference.ACTION_MUSIC_DATA);
		commandFilter.addAction(CommandPreference.ACTION_MUSIC_LOCAL);
		commandFilter.addAction(CommandPreference.ACTION_CONTROL_KWMUSIC);
		mContext.registerReceiver(mReceiver, commandFilter);
	}

	/**
	 * 取消广播
	 */
	private void unregistReceiver() {
		try {
			mContext.unregisterReceiver(mReceiver);
		} catch (Exception e) {
		}
	}

	/*------------------kw接口封装----------------*/
	/**
	 * 在线搜索播放第一条
	 */
	private void searchOnlineMusic(String key) {
		remComponenName = getTop();
		if (mKwapi == null)
			reCreateKWAPI();
		mKwapi.searchOnlineMusic(key, this);
		musicHandler.postDelayed(r, 5000);
	}

	/**
	 * 启动kw，非自动播放
	 */
	private void startKwApp() {
		remComponenName = getTop();
		if (mKwapi == null)
			reCreateKWAPI();
		mKwapi.startAPP(mContext, true);// startApp()是启动程序并自动播放上次播放过的歌曲，你这种情况需要调用playClientMusics这个接口，歌曲的参数信息传空即可。
		musicHandler.postDelayed(r, 5000);
	}

	/**
	 * 启动kw，并自动播放
	 */
	public void contiuneKwApp() {
		remComponenName = getTop();
		if (mKwapi == null)
			reCreateKWAPI();
		mKwapi.playClientMusics(mContext, null, null, null);
		musicHandler.postDelayed(r, 5000);
	}

	/**
	 * 关闭kw
	 */
	private void exitKwApp() {
		if (mKwapi == null)
			reCreateKWAPI();
		mKwapi.exitAPP(mContext);
	}

	/**
	 * 打开桌面歌词
	 */
	private void openDeskLyric() {
		if (musicHandler != null)
			musicHandler.removeCallbacks(r);
		if (mKwapi == null)
			reCreateKWAPI();
		mKwapi.openDeskLyric(mContext);

	}

	/**
	 * 关闭桌面歌词
	 */
	private void closeDeskLyric() {
		if (mKwapi == null)
			reCreateKWAPI();
		mKwapi.closeDeskLyric(mContext);
	}

	/**
	 * 设置播放模式
	 * 
	 * @param mode
	 */
	private void setPlayModel(KWPlayMode mode) {
		if (mKwapi == null)
			reCreateKWAPI();
		switch (mode) {
		case MODE_ALL_CIRCLE:
			// 循环播放
			mKwapi.setPlayMode(this, PlayMode.MODE_ALL_CIRCLE);
			break;
		case MODE_SINGLE_CIRCLE:
			// 单曲循环
			mKwapi.setPlayMode(this, PlayMode.MODE_SINGLE_CIRCLE);
			break;
		case MODE_ALL_ORDER:
			// 顺序播放
			mKwapi.setPlayMode(this, PlayMode.MODE_ALL_ORDER);
			break;
		case MODE_ALL_RANDOM:
			// 随机播放
			mKwapi.setPlayMode(this, PlayMode.MODE_ALL_RANDOM);
		default:
			break;
		}
	}

	/**
	 * 设置模式
	 * 
	 * @param model
	 *            模式
	 */
	private void setPlayState(KWPlayState state) {
		if (mKwapi == null)
			reCreateKWAPI();
		switch (state) {
		case STATE_PLAY:// 播放
			mKwapi.setPlayState(mContext, PlayState.STATE_PLAY);
			break;
		case STATE_PAUSE:// 暂停
			mKwapi.setPlayState(mContext, PlayState.STATE_PAUSE);
			break;
		case STATE_PRE:// 上一个
			mKwapi.setPlayState(mContext, PlayState.STATE_PRE);
			break;
		case STATE_NEXT:// 下一个
			mKwapi.setPlayState(mContext, PlayState.STATE_NEXT);
			break;
		default:
			break;
		}
	};

	/**
	 * 播放用户搜索的歌曲
	 * 
	 * @param name
	 *            歌名
	 * @param singer
	 *            歌手
	 * @param album
	 *            专辑
	 */
	public void playClientMusics(String name, String singer, String album) {
		remComponenName = getTop();
		if (mKwapi == null)
			reCreateKWAPI();
		mKwapi.playClientMusics(mContext, name, singer, album);
		mKwapi.setPlayState(mContext, PlayState.STATE_PLAY);
		musicHandler.postDelayed(r, 5000);
	}

	/**
	 * 根据路径打开本地音乐
	 * 
	 * @param path
	 */
	private void playLocalMusic(String path) {
		remComponenName = getTop();
		if (mKwapi == null)
			reCreateKWAPI();
		mKwapi.playLocalMusic(mContext, path);
		musicHandler.postDelayed(r, 5000);
	}

	private ComponentName getTop() {
		ComponentName className = null;
		ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		@SuppressWarnings("deprecation")
		List<RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
		// 应用程序位于堆栈的顶层
		if (tasksInfo != null) {
			className = tasksInfo.get(0).topActivity;
		}
		return className;
	}

	/**
	 * 重新创建对象
	 * 
	 * @return
	 */
	private void reCreateKWAPI() {
		mKwapi = KWAPI.createKWAPI(this, "auto");
	}

	/**
	 * 播放模式
	 * 
	 * @author kevin
	 */
	private enum KWPlayMode {
		MODE_ALL_CIRCLE, MODE_SINGLE_CIRCLE, MODE_ALL_ORDER, MODE_ALL_RANDOM;
	}

	/**
	 * 播放状态
	 * 
	 * @author kevin
	 */
	private enum KWPlayState {
		STATE_PLAY, STATE_PAUSE, STATE_PRE, STATE_NEXT;
	}

	/**
	 * KW PlayerStatus INIT, //启动没播过歌 PLAYING, //正在播放 BUFFERING, //播放中，等待缓冲
	 * PAUSE, //暂停 STOP, //停止 XD added 20150929
	 */
	private static PlayerStatus mPlayerStatus = PlayerStatus.INIT;

	/**
	 * getPlayerStatus
	 * 
	 * @author xiaodong.he
	 * @return PlayerStatus
	 */
	public static PlayerStatus getPlayerStatus() {
		return mPlayerStatus;
	}

	/**
	 * is KWMusic Playing
	 * 
	 * @author xiaodong.he
	 * @return
	 */
	public static boolean isKWMusicPlaying() {
		if (PlayerStatus.PLAYING == mPlayerStatus
				|| PlayerStatus.BUFFERING == mPlayerStatus) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * register WK Play Listener XD added 20150929
	 */
	private void registerPlayListener() {
		if (null == mKwapi) {
			return;
		}
		mKwapi.registerPlayerStatusListener(this, mPlayerStatusListener);
	}

	private OnPlayerStatusListener mPlayerStatusListener = new OnPlayerStatusListener() {
		@Override
		public void onPlayerStatus(PlayerStatus status) {
			// do something by yourself
			mPlayerStatus = status;
		}
	};

}