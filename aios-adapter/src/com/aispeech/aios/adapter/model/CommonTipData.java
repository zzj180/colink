package com.aispeech.aios.adapter.model;

import com.aispeech.ailog.AILog;
import com.aispeech.aios.adapter.AdapterApplication;
import com.aispeech.aios.adapter.bean.CommonTip;
import com.aispeech.aios.adapter.util.PropertyUtil;
import com.google.gson.Gson;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

/**
 * @desc 自定义aios通用反馈语数据源
 * @auth AISPEECH
 * @date 2016-01-14
 * @copyright aispeech.com
 */
public class CommonTipData {

    public static final String TAG = "CommonTipData";


    public String getCommonTips() {

        Document doc = null;

        try {
            doc = PropertyUtil.getDocument(AdapterApplication.getContext(), "custom_common_tip.xml");
        } catch (Exception e) {
            e.printStackTrace();
        }

        CommonTip commonTip = new CommonTip();

        if (null != doc) {
            commonTip.setHi(getTips(doc, "hi"));
            commonTip.setBye(getTips(doc, "bye"));
            commonTip.setSilence(getTips(doc, "silence"));
            commonTip.setWakeupAgain(getTips(doc, "wakeup_again"));
            commonTip.setRepeatAgain(getTips(doc, "repeat_again"));
            commonTip.setNetworkAbnomal(getTips(doc, "network_abnomal"));
            commonTip.setNetworkAbnomalNavi(getTips(doc, "network_abnomal_navi"));
            commonTip.setBluetoothDisconnect(getTips(doc, "bluetooth_disconnect"));
            commonTip.setContactsUnsync(getTips(doc, "contacts_unsync"));
        }

        String tips = new Gson().toJson(commonTip);

        AILog.i(TAG, "自定义反馈语列表如下：");
        AILog.json(TAG, tips);

        return tips;
    }

    /**
     * 根据不同的场景得到不同的反馈语列表
     *
     * @param document 通用反馈语XML生成的文档对象
     * @param scene    场景
     * @return 通用反馈语列表
     */
    private List<String> getTips(Document document, String scene) {
        List<String> tipList = new ArrayList<String>();

        try {
            Element tipsElement = document.getElementById(scene);
            NodeList tipNodeList = tipsElement.getChildNodes();

            for (int j = 0, len = tipNodeList.getLength(); j < len; ++j) {

                Node tipNode = tipNodeList.item(j);

                if (tipNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element tipElement = (Element) tipNode;
                    tipList.add(tipElement.getAttribute("name"));
                }
            }


        } catch (Exception e) {
            AILog.e(TAG, e);
        }

        return tipList;
    }

}
