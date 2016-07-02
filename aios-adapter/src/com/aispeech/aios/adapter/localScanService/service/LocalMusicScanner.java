package com.aispeech.aios.adapter.localScanService.service;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.text.TextUtils;

import com.aispeech.ailog.AILog;
import com.aispeech.aios.adapter.localScanService.bean.LocalMusicBean;
import com.aispeech.aios.adapter.localScanService.db.MusicLocalDaoImpl;
import com.aispeech.aios.adapter.localScanService.util.CharUtil;
import com.aispeech.aios.adapter.localScanService.util.PathClusterUtil;
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

        List<String> musicFileList = new ArrayList<String>();

        for (String rootPath : PathClusterUtil.getClusteredList(mfolders)) {//拿到歌曲完整路径 list
            initMusicFileList(musicFileList, rootPath);
        }

        List<LocalMusicBean> localMusicBeanList = getMusicList(musicFileList);
        List<LocalMusicBean> list = parseMusicInfo(localMusicBeanList);// 解析list

        updataLocalMusicDB(list);//更新数据库

        try {
            postKernel();//POST 给AIOS
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    /**
     * 根据音频文件名列表获取List<LocalMusicBean>对象,Item已经设置好完整名和简单名
     *
     * @param musicFileList 音频文件完整名列表
     * @return List<LocalMusicBean>对象
     */
    private List<LocalMusicBean> getMusicList(List<String> musicFileList) {

        List<LocalMusicBean> dataList = new ArrayList<LocalMusicBean>();

        LocalMusicBean info;
        File musicFile;
        for (String musicFilePath : musicFileList) {

            info = new LocalMusicBean();
            musicFile = new File(musicFilePath);

            info.setPath(musicFilePath);
            info.setFileName(musicFile.getName());

            dataList.add(info);
        }
        return dataList;
    }

    /**
     * 根据根目录递归查询下面的音频文件，并添加到List<String>
     *
     * @param musicFileList 应该传递进来一个空的列表
     * @param rootPath      跟目录
     */
    private void initMusicFileList(List<String> musicFileList, String rootPath) {

        File rootFile = new File(rootPath);
        File[] files = rootFile.listFiles();

        if (files!=null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    initMusicFileList(musicFileList, file.getAbsolutePath());
                } else {
                    String name = file.getName().toLowerCase();
                    if (name.endsWith(".ogg") || name.endsWith(".mp3")) {
                        musicFileList.add(file.getAbsolutePath());
                    }
                }
            }
        }
    }


    /**
     * 解析音频文件
     *
     * @param dataList 音频文件信息
     * @return
     */
    private List<LocalMusicBean> parseMusicInfo(List<LocalMusicBean> dataList) {

        List<LocalMusicBean> musicList = new ArrayList<LocalMusicBean>();

        MediaMetadataRetriever mmr = new MediaMetadataRetriever();

        AILog.d(TAG, "解析之前的音乐信息：");
        AILog.d(TAG, dataList);
        File file;
        for (LocalMusicBean info : dataList) {
            file = new File(info.getPath());
            if (file.exists()) {
                try {
                    mmr.setDataSource(info.getPath());

                    info.setMusicName(getEmptyMessyCodeValidated(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)));
                    info.setArtist(getEmptyMessyCodeValidated(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)));

                    info.setSize(String.valueOf(file.length()));
                    info.setData(getEmptyMessyCodeValidated(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE)));
                    info.setAlbum(getEmptyMessyCodeValidated(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)));
                    info.setCloud(false);//是否云端音乐
                    info.setDuration(getEmptyMessyCodeValidated(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)));
                    info.setMime(getEmptyMessyCodeValidated(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE)));

                    if (!musicList.contains(info)) {//去重复
                        musicList.add(info);
                    }

                } catch (Exception e) {
                    AILog.e(TAG, e.toString());
                }
            }

        }


        AILog.d(TAG, "解析之后的音乐信息：");
        AILog.d(TAG, dataList);
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
        JSONObject job;
        for (LocalMusicBean info : list) {
            job = new JSONObject();

            job.put("id", String.valueOf(info.getId()));
            job.put("title", getValidated(info.getMusicName()));
            job.put("artist", getValidated(info.getArtist()));
            job.put("name", info.getFileName().substring(0, (info.getFileName().lastIndexOf("."))));//去掉后缀名
            job.put("album", getValidated(info.getAlbum()));
            job.put("duration", Integer.getInteger(info.getDuration()));
            job.put("size", Integer.parseInt(info.getSize()));
            job.put("url", info.getPath());

            jrr.put(job);
        }

        jb.put("songs", jrr);

        AILog.i("传给内核同步的Json：");
        AILog.json(TAG, jb.toString());

        AIOSMusicDataNode.getInstance().postData(jb.toString());
    }

    /**
     * 如果字符串等于"unknown"，就返回“”字符串，否则返回原字符串
     *
     * @param src 待验证字符串
     * @return 验证之后的字符串
     */
    private String getValidated(String src) {
        return "unknown".equals(src) ? "" : src;
    }

    /**
     * 如果是乱码或者为空，就返回"unknown"，否则返回原字符串
     *
     * @param src 待验证字符串
     * @return 验证之后的字符串
     */
    private String getEmptyMessyCodeValidated(String src) {
        return TextUtils.isEmpty(src) || CharUtil.isMessyCode(src) ? "unknown" : src;
    }

}
