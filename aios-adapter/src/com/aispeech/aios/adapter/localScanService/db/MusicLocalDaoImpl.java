package com.aispeech.aios.adapter.localScanService.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.aispeech.ailog.AILog;
import com.aispeech.aios.adapter.localScanService.bean.LocalMusicBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * @desc 数据库的实现层
 * @auth AISPEECH
 * @date 2016-02-19
 * @copyright aispeech.com
 */
public class MusicLocalDaoImpl implements MusicLocalDao {

    private final String TAG = "AIOS-MusicLocalDaoImpl";

    SQLiteDatabase mDb;

    public MusicLocalDaoImpl(Context context) {
        if (mDb == null) {
            mDb = MusicDBHelper.getInstance(context).getWritableDatabase();
        }
    }

    /**
     * 往数据库中添加一条
     *
     * @param o LocalMusicBean对象
     */
    @Override
    public void add(Object o) {

        if (!isExist(o)) {
            LocalMusicBean info = ((LocalMusicBean) o);
            ContentValues values = new ContentValues();
            values.put(MusicLocal._ID, generateID());//随机生成一个id
            values.put(MusicLocal.ARTIST, info.getArtist());
            values.put(MusicLocal.TITLE, info.getMusicName());
            values.put(MusicLocal.DURATION, info.getDuration());
            values.put(MusicLocal.SIZE, info.getSize());
            values.put(MusicLocal.DATA, info.getData());
            values.put(MusicLocal.MIME_TYPE, info.getMime());
            values.put(MusicLocal.PATH, info.getPath());
            values.put(MusicLocal.FILENAME, info.getFileName());
            values.put(MusicLocal.ALBUM, info.getAlbum());

            mDb.insert(MusicLocal.TABLE_NAME, null, values);
        }

    }

    /**
     * 从数据库中删除一条
     * @param o LocalMusicBean对象
     */
    @Override
    public void delete(Object o) {
        if (isExist(o)) {
            String where = "_id = ?";
            mDb.delete(MusicLocal.TABLE_NAME, where, new String[]{String.valueOf(((LocalMusicBean) o).getId())});
        }
    }

    /**
     * 根据id从数据库中找对象
     * @param id 数据库中的id的值
     * @return LocalMusicBean对象或者没找到返回null
     */
    @Override
    public Object findById(long id) {

        LocalMusicBean info = null;

        String selection = " _id = ? ";
        Cursor cursor = mDb.query(MusicLocal.TABLE_NAME, new String[]{MusicLocal._ID, MusicLocal.ARTIST, MusicLocal.TITLE, MusicLocal.DURATION, MusicLocal.DATA, MusicLocal.SIZE}, selection, new String[]{String.valueOf(id)}, null, null, null);
        if (cursor != null && cursor.moveToNext()) {
            info = new LocalMusicBean();
            initLocalMusicBean(cursor, info);
        }

        return info;
    }

    /**
     * 删除整张表
     */
    @Override
    public void deleteAll() {
        if (mDb != null) {
            mDb.delete(MusicLocal.TABLE_NAME, null, null);
        }
    }

    /**
     * 查找整张表的数据
     * @return LocalMusicBean List 或者 空的 list
     */
    @Override
    public List findAll() {

        List<LocalMusicBean> list = new ArrayList<LocalMusicBean>();

        Cursor cursor = mDb.query(MusicLocal.TABLE_NAME, null, null, null, null, null, null);

        LocalMusicBean info = null;
        while (cursor != null && cursor.moveToNext()) {
            info = new LocalMusicBean();
            initLocalMusicBean(cursor, info);
            list.add(info);
        }

        Collections.reverse(list);
        return list;
    }

    /**
     * 判断表中是否含有这个LocalMusicBean对象
     * @param o LocalMusicBean对象
     * @return true或者false
     */
    @Override
    public boolean isExist(Object o) {
        LocalMusicBean info = ((LocalMusicBean) o);

        String selection = "_id = ?";
        Cursor query = mDb.query(MusicLocal.TABLE_NAME, new String[]{MusicLocal._ID}, selection, new String[]{String.valueOf(info.getId())}, null, null, null);
        if (query != null && query.moveToNext()) {
            return true;
        }

        return false;
    }


    public List<Long> getIDs() {

        List<Long> ids = new ArrayList<Long>();

        Cursor cursor = mDb.query(MusicLocal.TABLE_NAME, new String[]{MusicLocal._ID}, null, null, null, null, null);
        while (cursor != null && cursor.moveToNext()) {
            Long id = Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(MusicLocal._ID)));
            ids.add(id);
        }

        return ids;
    }

    /**
     * 根据当前游标位置指向的数据库记录，设置需要初始化的LocalMusicBean对象
     *
     * @param cursor 当前游标
     * @param info   已经实例化的LocalMusicBean对象
     */
    public void initLocalMusicBean(Cursor cursor, LocalMusicBean info) {
        info.setId(Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(MusicLocal._ID))));
        info.setArtist(cursor.getString(cursor.getColumnIndexOrThrow(MusicLocal.ARTIST)));
        info.setMusicName(cursor.getString(cursor.getColumnIndexOrThrow(MusicLocal.TITLE)));
        info.setDuration(cursor.getString(cursor.getColumnIndexOrThrow(MusicLocal.DURATION)));
        info.setPath(cursor.getString(cursor.getColumnIndexOrThrow(MusicLocal.DATA)));
        info.setSize(cursor.getString(cursor.getColumnIndexOrThrow(MusicLocal.SIZE)));
        info.setFileName(cursor.getString(cursor.getColumnIndexOrThrow(MusicLocal.FILENAME)));
        info.setAlbum(cursor.getString(cursor.getColumnIndexOrThrow(MusicLocal.ALBUM)));
        info.setPath(cursor.getString(cursor.getColumnIndexOrThrow(MusicLocal.PATH)));
        info.setData(cursor.getString(cursor.getColumnIndexOrThrow(MusicLocal.DATA)));
        info.setMime(cursor.getString(cursor.getColumnIndexOrThrow(MusicLocal.MIME_TYPE)));
        info.setCloud(false);
    }

    public void updateDatabase(List<LocalMusicBean> list) {
        for (LocalMusicBean info : list) {
            if (!isExistByObject(info)) {
                info.setId(generateID());
                add(info);
            }
        }
    }

    private boolean isExistByObject(Object o) {
        LocalMusicBean info = ((LocalMusicBean) o);

        String selection = "artist = ? and title = ?";
        Cursor query = mDb.query(MusicLocal.TABLE_NAME, new String[]{MusicLocal.ARTIST, MusicLocal.TITLE}, selection, new String[]{info.getArtist(), info.getMusicName()}, null, null, null);
        if (query != null && query.moveToNext()) {
            return true;
        }

        return false;
    }

    private long generateID() {
        Set<Long> ids = new HashSet<Long>();

        Cursor cursor = mDb.query(MusicLocal.TABLE_NAME, new String[]{MusicLocal._ID}, null, null, null, null, null);


        while (cursor != null && cursor.moveToNext()) {
            Long id = Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(MusicLocal._ID)));
            ids.add(id);
        }


        return getRandomID(ids);
    }

    /**
     * 生成随机ID
     * @param ids
     * @return
     */
    private long getRandomID(Set<Long> ids) {

        Long id = (long) Math.abs(new Random().nextInt()) % 20000;
        if (ids.contains(id)) {
            AILog.i(TAG, "ID已经存在：" + id);
            return getRandomID(ids);
        } else {
            return id;
        }
    }
}
