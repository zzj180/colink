package com.aispeech.aios.adapter.node;

import com.aispeech.ailog.AILog;
import com.aispeech.aios.BaseNode;
import com.aispeech.aios.BusClient;
import com.aispeech.aios.adapter.AdapterApplication;
import com.aispeech.aios.adapter.R;
import com.aispeech.aios.adapter.bean.VehicleRestrictionBean;
import com.aispeech.aios.adapter.config.AiosApi;
import com.aispeech.aios.adapter.control.UIType;
import com.aispeech.aios.adapter.control.UiEventDispatcher;
import com.aispeech.aios.adapter.util.StringUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @desc 车辆限行领域对应节点
 * @auth AISPEECH
 * @date 2016-01-13
 * @copyright aispeech.com
 */
public class VehicleRestrictionNode extends BaseNode {

    private static VehicleRestrictionNode mInstance;
    private static final String TAG = "AIOS-Adapter-VehiclerestrictionNode";

    public static synchronized VehicleRestrictionNode getInstance() {
        if (mInstance == null) {
            mInstance = new VehicleRestrictionNode();
        }
        return mInstance;
    }

    @Override
    public void onJoin() {
        super.onJoin();
        bc.subscribe("wakeup.result");
        bc.subscribe("data.vehiclerestriction.query.result");
    }

    @Override
    public String getName() {
        return "vehiclerestriction";
    }

    @Override
    public void onMessage(String topic, byte[]... parts) throws Exception {
        super.onMessage(topic, parts);
        AILog.i(TAG, topic, parts);

        if (topic.equals("wakeup.result")) {
            UiEventDispatcher.notifyUpdateUI(UIType.VehiclerestrictionLargeImage, null, null);
        } else if (AiosApi.Player.STATE.equals(topic) && "wait".equals(StringUtil.getEncodedString(parts[0]))) {
            bc.unsubscribe(AiosApi.Player.STATE);
        } else if (topic.equals("data.vehiclerestriction.query.result")) {
            UiEventDispatcher.notifyUpdateUI(UIType.CancelLoadingUI);
            int errId = Integer.valueOf(new String(parts[1]));
            if (errId == 0) {
                onResponse(new String(parts[0], "utf-8"));
            } else {
                onErrorResponse(errId);
            }
        }
    }

    @Override
    public BusClient.RPCResult onCall(String s, byte[]... bytes) throws Exception {
        AILog.i(TAG, s, bytes);

        if (s.equals("/vehiclerestriction/query")) {
            bc.call("/data/vehiclerestriction/query", StringUtil.getEncodedString(bytes[0]));
            UiEventDispatcher.notifyUpdateUI(UIType.LoadingUI);
        }
        return null;
    }

    private void parserJson(String jsons) {
        AILog.json(jsons);
        try {
            JSONObject jsonObject = new JSONObject(jsons);
            if (jsonObject != null) {
                if (jsonObject.optJSONObject("result") != null) {
                    JSONObject resultJson = jsonObject.optJSONObject("result");
                    if (resultJson.optJSONArray("image") != null) {
                        if (resultJson.optString("abstract") != null) {
                            VehicleRestrictionBean bean = new VehicleRestrictionBean();
                            bean.setOutPut(resultJson.optString("abstract"));
                            bean.setTitle(resultJson.optString("city"));
                            bean.setDate(resultJson.optString("date"));
                            JSONArray jsonArray = resultJson.optJSONArray("image");
                            List<VehicleRestrictionBean.ImageInfo> list = new ArrayList<VehicleRestrictionBean.ImageInfo>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                VehicleRestrictionBean.ImageInfo imageInfo = new VehicleRestrictionBean.ImageInfo();
                                imageInfo.setName(jsonArray.getJSONObject(i).getString("title"));
                                imageInfo.setUrl(jsonArray.getJSONObject(i).getString("url"));
                                list.add(imageInfo);
                            }
                            bean.setRules(list);
                            bc.subscribe(AiosApi.Player.STATE);
                            UiEventDispatcher.notifyUpdateUI(UIType.CancelLoadingUI, null, null);
                            TTSNode.getInstance().playRpc("vehiclerestriction.query.result", bean.getOutPut());
                            UiEventDispatcher.notifyUpdateUI(UIType.VehiclerestrictionUI, bean);

                        }
                    } else {
                        if (resultJson.optString("abstract") != null) {
                            String abstract_txt = resultJson.optString("abstract");
                            if (abstract_txt.contains("已超出官方公布范围") ||
                                    abstract_txt.contains("无常规限行") || abstract_txt.contains("不是限行城市")) {
                                UiEventDispatcher.notifyUpdateUI(UIType.CancelLoadingUI, null, null);
                                TTSNode.getInstance().playRpc("vehiclerestriction.query.result", abstract_txt);
                                UiEventDispatcher.notifyUpdateUI(UIType.DismissWindow, 2000);
                            }
                        }
                    }

                } else if (jsonObject.optString("errId") != null) {
                    UiEventDispatcher.notifyUpdateUI(UIType.CancelLoadingUI, null, null);
                    TTSNode.getInstance().playRpc("vehiclerestriction.query.result", "网络条件不佳，请重试");
                    UiEventDispatcher.notifyUpdateUI(UIType.DismissWindow, 2000);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
            UiEventDispatcher.notifyUpdateUI(UIType.CancelLoadingUI, null, null);
            TTSNode.getInstance().playRpc("vehiclerestriction.query.result", "网络条件不佳，请重试");
            UiEventDispatcher.notifyUpdateUI(UIType.DismissWindow, 2000);
        }
    }

    public void onResponse(String response) {
        parserJson(response);
    }

    public void onErrorResponse(int errorId) {
        if (errorId == 1) {
            TTSNode.getInstance().playRpc("vehiclerestriction.query.result", AdapterApplication.getContext().getString(R.string.error_loc));
            UiEventDispatcher.notifyUpdateUI(UIType.CancelLoadingUI, null, null);
            UiEventDispatcher.notifyUpdateUI(UIType.DismissWindow, 2000);
        } else if (errorId == 4) {
            UiEventDispatcher.notifyUpdateUI(UIType.CancelLoadingUI);
            TTSNode.getInstance().playRpc("vehiclerestriction.query.result", "网络条件不佳，请重试");
            UiEventDispatcher.notifyUpdateUI(UIType.DismissWindow, 2000);
        }
    }
}
