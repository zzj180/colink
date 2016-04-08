package com.aispeech.aios.adapter.model;

import com.aispeech.ailog.AILog;
import com.aispeech.aios.adapter.AdapterApplication;
import com.aispeech.aios.adapter.bean.LangGen;
import com.aispeech.aios.adapter.util.PropertyUtil;
import com.google.gson.Gson;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

/**
 * @desc 自定义自然语言生成文本Model，用户可以根据自己的需求
 * 更改进入模块，退出模块，选择超出范围的提示语
 * @auth AISPEECH
 * @date 2016-01-14
 * @copyright aispeech.com
 */
public class LangGenData {

    public static final String TAG = "LangGenData";

    public String getGeneration() {

        AILog.i(TAG, "开始获取各个领域自定义说法列表Json串");

        Document doc = null;

        try {
            doc = PropertyUtil.getDocument(AdapterApplication.getContext(), "custom_generate_language.xml");
        } catch (Exception e) {
            AILog.i(TAG, e);
        }

        LangGen langGen = new LangGen();

        if (null != doc) {

            LangGen.Navi navi = new LangGen.Navi();
            navi.setEnter(getTips(doc, "navi", "enter"));
            navi.setBye(getTips(doc, "navi", "bye"));
            navi.setOverflow(getTips(doc, "navi", "overflow"));

            LangGen.Music music = new LangGen.Music();
            music.setEnter(getTips(doc, "music", "enter"));
            music.setBye(getTips(doc, "music", "bye"));
            music.setOverflow(getTips(doc, "music", "overflow"));

            LangGen.Nearby nearby = new LangGen.Nearby();
            nearby.setEnter(getTips(doc, "nearby", "enter"));
            nearby.setBye(getTips(doc, "nearby", "bye"));
            nearby.setOverflow(getTips(doc, "nearby", "overflow"));
            nearby.setOfferdo(getTips(doc, "nearby", "offerdo"));

            LangGen.Phonecall phoneCall = new LangGen.Phonecall();
            phoneCall.setEnter(getTips(doc, "phonecall", "enter"));
            phoneCall.setBye(getTips(doc, "phonecall", "bye"));
            phoneCall.setOverflow(getTips(doc, "phonecall", "overflow"));

            langGen.setNavi(navi);
            langGen.setMusic(music);
            langGen.setNearby(nearby);
            langGen.setPhonecall(phoneCall);
        }


        String generation = new Gson().toJson(langGen);

        AILog.i(TAG, "各个领域自定义说法列表如下：");
        AILog.json(TAG, generation);
        return generation;
    }

    /**
     * 根据领域名称，场景名称，返回某个领域某个场景的反馈语列表
     *
     * @param document 通用反馈语XML生成的文档对象
     * @param domain   领域
     * @param scene    场景
     * @return 某个领域某个场景的反馈语列表
     */
    private List<String> getTips(Document document, String domain, String scene) {
        List<String> tipList = new ArrayList<String>();

        try {
            Element domainElement = document.getElementById(domain);

            NodeList tipsNodeList = domainElement.getElementsByTagName("tips");

            NodeList tipNodeList = null;

            for (int j = 0, len = tipsNodeList.getLength(); j < len; ++j) {
                Node tipsNode = tipsNodeList.item(j);

                if (tipsNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element tipElement = (Element) tipsNode;
                    if (scene.equals(tipElement.getAttribute("id"))) {
                        tipNodeList = tipElement.getChildNodes();
                    }
                }
            }


            if (null != tipNodeList) {

                for (int j = 0, len = tipNodeList.getLength(); j < len; ++j) {

                    Node tipNode = tipNodeList.item(j);

                    if (tipNode.getNodeType() == Node.ELEMENT_NODE) {

                        tipList.add(((Element) tipNode).getAttribute("name"));
                    }
                }
            } else {
                AILog.e(TAG, "没有找到对应节点");
            }


        } catch (Exception e) {
            AILog.e(TAG, e);
        }

        return tipList;
    }

}
