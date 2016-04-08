package com.aispeech.aios.adapter.util;

import android.content.Context;

import com.aispeech.aios.adapter.AdapterApplication;
import com.aispeech.aios.adapter.bean.PropertyBean;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * @desc AIOS设置帮助类
 * @auth AISPEECH
 * @date 2016-02-19
 * @copyright aispeech.com
 */
public class PropertyUtil {

    private static PropertyUtil mPropertyUtil;

    /**
     * @return PropertyUtil实例
     */
    public static synchronized PropertyUtil getInstance() {
        if (mPropertyUtil == null) {
            mPropertyUtil = new PropertyUtil();
        }
        return mPropertyUtil;
    }

    /**
     * 读取property文件
     *
     * @throws IOException
     */
    public PropertyBean loadProperty() throws IOException {

        Properties property = new Properties();
        property.load(AdapterApplication.getContext().getAssets().open("config.properties"));
        PropertyBean b = new PropertyBean();
        b.setDefaultMapType(Integer.parseInt(property.getProperty("DEFAULTMAPTYPE")));//默认地图类型
        b.setDefaultMusicType(Integer.parseInt(property.getProperty("DEFAULTMUSICTYPE")));//默认音乐APP类型
        return b;
    }

    /**
     * 根据文件名返回Assert目录下某个XML文件的Document对象
     *
     * @param context        上下文
     * @param arrestFileName 文件名
     * @return Document对象
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    public static Document getDocument(Context context, String arrestFileName) throws IOException, ParserConfigurationException, SAXException {
        InputStream is = context.getResources().getAssets().open(arrestFileName);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(is);
    }

}
