package com.aispeech.aios.adapter.localScanService.service;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.text.TextUtils;

import com.aispeech.ailog.AILog;
import com.aispeech.aios.adapter.localScanService.bean.LocalMusicBean;
import com.aispeech.aios.adapter.localScanService.db.MusicLocalDaoImpl;
import com.aispeech.aios.adapter.localScanService.util.CharUtil;
import com.aispeech.aios.client.AIOSMusicDataNode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @desc 本地音乐扫描线程
 * @auth AISPEECH
 * @date 2016-02-19
 * @copyright aispeech.com
 */
public class LocalMusicScanner implements Runnable {

    private static final String TAG = "LocalMusicScanner";

    private List<String> mfolders;
    private Context mContext;

    public LocalMusicScanner(Context context, List<String> folders) {

        this.mContext = context;
        this.mfolders = folders;
    }

    @Override
    public void run() {
        // 1 拿到歌曲完整路径 list
        // 2 解析list
        // 3 post AIOS
        // 4 存数据库

        List<LocalMusicBean> list = parseMusicInfo(getMusicList(mfolders));

        updataLocalMusicDB(list);//更新数据库

        try {
            postKernel();//POST 给AIOS
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    /**
     * 遍历文件夹下所有音乐文件
     *
     * @param folders
     * @return
     */
    private List<LocalMusicBean> getMusicList(List<String> folders) {

        List<LocalMusicBean> dataList = new ArrayList<LocalMusicBean>();
        LocalMusicBean info = null;
        for (int i = 0; i < folders.size(); i++) {
            String path = folders.get(i);
            //遍历文件
            File dirFile = new File(path);

            if (!dirFile.exists() || dirFile.listFiles() == null) {
                continue;
            }
            for (File file : dirFile.listFiles()) {

                String name = file.getName();
                if (name.toLowerCase().endsWith(".mp3") || name.toLowerCase().endsWith(".ogg")) {
                    info = new LocalMusicBean();
                    info.setPath(folders.get(i) + "/" + name);
                    info.setFileName(name);
                    dataList.add(info);
                }
            }
        }
        return dataList;
    }


    /**
     * 解析音频文件
     * @param dataList 音频文件信息
     * @return
     */
    private List<LocalMusicBean>  parseMusicInfo(List<LocalMusicBean> dataList) {

        List<LocalMusicBean> musicList = new ArrayList<LocalMusicBean>();

        if (dataList == null || dataList.size() == 0) {
            AILog.d(TAG, "未找到歌曲！");
        }

        MediaMetadataRetriever mmr = new MediaMetadataRetriever();

        File file = null;

        for (LocalMusicBean info : dataList) {

            file = new File(info.getPath());

            try {
                mmr.setDataSource(info.getPath());

                AILog.d(TAG, "bean.path :" + info.getPath());

                AILog.d(TAG, "bean.Name :" + info.getFileName());

                if (TextUtils.isEmpty(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)) || CharUtil.isMessyCode(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE))) {
                    info.setMusicName("unknown");
                } else {
                    info.setMusicName(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));
                }
                AILog.d(TAG, "bean.MusicName :" + info.getMusicName());

                if (TextUtils.isEmpty(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)) || CharUtil.isMessyCode(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM))) {
                    info.setAlbum("unknown");
                } else {
                    info.setAlbum(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));
                }
                AILog.d(TAG, "bean.album :" + info.getAlbum());

                if (TextUtils.isEmpty(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)) || CharUtil.isMessyCode(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)) ) {
                    info.setArtist("unknown");
                } else {
                    info.setArtist(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
                }
                AILog.d(TAG, "bean.artist :" + info.getArtist());

                info.setCloud(false);//是否云端音乐

                if (TextUtils.isEmpty(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE))) {
                    info.setData("unknown");
                } else {
                    info.setData(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE));
                }
                AILog.d(TAG, "bean.data :" + info.getData());

                if (TextUtils.isEmpty(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION))) {
                    info.setDuration("unknown");
                } else {
                    info.setDuration(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
                }
                AILog.d(TAG, "info.duration :" + info.getDuration());

                info.setSize(String.valueOf(file.length()));
                AILog.d(TAG, "info.size :" + info.getSize());

                if (TextUtils.isEmpty(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE))) {
                    info.setMime("unknown");
                } else {
                    info.setMime(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE));
                }
                AILog.d(TAG, "info.mime :" + info.getMime());

                if (!musicList.contains(info)) {//去重复
                    musicList.add(info);
                }

            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        return musicList;

    }

    /**
     * 写数据库
     */
    private void updataLocalMusicDB(List<LocalMusicBean> list) {

        MusicLocalDaoImpl db = new MusicLocalDaoImpl(mContext);

        db.deleteAll();

        for (LocalMusicBean info : list) {
            db.add(info);
        }

    }

    /**
     * 丢给内核
     */
    private void postKernel() throws JSONException {

        MusicLocalDaoImpl db = new MusicLocalDaoImpl(mContext);

        List<LocalMusicBean> list = db.findAll();

        JSONObject jb = new JSONObject();
        JSONArray jrr = new JSONArray();
        JSONObject job = null;
        for (LocalMusicBean info : list) {
            job = new JSONObject();

            job.put("id", String.valueOf(info.getId()));

            if ("unknown".equals(info.getMusicName())) {
                job.put("title", "");
            } else {
                job.put("title", info.getMusicName());
            }

            if ("unknown".equals(info.getArtist())) {
                job.put("artist", "");
            } else {
                job.put("artist", info.getArtist());
            }

            if ("unknown".equals(info.getAlbum())) {
                job.put("album", "");
            } else {
                job.put("album", info.getAlbum());
            }

            if ("unknown".equals(info.getFileName())) {
                job.put("name", "");
            } else {
                job.put("name", info.getFileName().substring(0,(info.getFileName().lastIndexOf("."))));//去掉后缀名
            }

            if ("unknown".equals(info.getDuration())) {
                job.put("duration", null);
            } else {
                job.put("duration", Integer.getInteger(info.getDuration()));
            }
            job.put("size", Integer.parseInt(info.getSize()));
            job.put("url", info.getPath());
            jrr.put(job);
        }

        jb.put("songs", jrr);
        AIOSMusicDataNode.getInstance().postData(jb.toString());//发给AIOS
        AILog.d(TAG, "JRR:" + jrr);

    }

}
