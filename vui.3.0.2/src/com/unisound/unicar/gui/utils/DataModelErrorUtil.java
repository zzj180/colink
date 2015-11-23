package com.unisound.unicar.gui.utils;

public class DataModelErrorUtil extends ErrorUtil {
    public static final int RETURN_SUCCESS = 0;
    public static final int RETURN_NORESULT = -102000;
    public static final int RETURN_SEARCH_TIMEOUT = -102007;
    public static final int RETURN_EXCEPTION_RESULT = -102001;
    public static final int LOCATION_DISABLED = -102002;
    public static final int SEARCH_POI_TIMEOUT = -102003;
    public static final int LOCATE_TIMEOUT = -102004;
    public static final int LOCATE_NO_RESULT = -102005;
    public static final int SEARCH_MUSCI_TIMEOUT = -102006;
    public static final int SEARCH_STOCK_TIMEOUT = -102008;
    public static final int SEARCH_WEATHER_TIMEOUT = -102009;
    public static final int CITY_POI_LOCATION = -102010;
    public static final int VENDOR_ERROR = -102011;
    public static final int SEARCH_POI_BAIDU_CITY_NULL = -102012;
    public static final int NETWORK_EXCEPTION = -102013;
    public static final int RUNTIME_EXCEPTION = -102014;
    public static final int SEARCH_LOCATION_TIMEOUT = -102015;

    public DataModelErrorUtil(int code, String message) {
        super(code, message);
        // TODO Auto-generated constructor stub
    }

    public static DataModelErrorUtil getErrorUtil(int code) {
        switch (code) {
            case RETURN_SUCCESS:
                return new DataModelErrorUtil(RETURN_SUCCESS, "操作成功");
            case RETURN_NORESULT:
                return new DataModelErrorUtil(RETURN_NORESULT, "无相应结果");
            case RETURN_EXCEPTION_RESULT:
                return new DataModelErrorUtil(RETURN_EXCEPTION_RESULT, "获取信息失败");
            case SEARCH_POI_TIMEOUT:
                return new DataModelErrorUtil(SEARCH_POI_TIMEOUT, "搜索位置信息超时");
            case SEARCH_LOCATION_TIMEOUT:
                return new DataModelErrorUtil(SEARCH_LOCATION_TIMEOUT, "搜索位置超时");
            case LOCATION_DISABLED:
                return new DataModelErrorUtil(LOCATION_DISABLED, "定位服务被关闭");
            case LOCATE_TIMEOUT:
                return new DataModelErrorUtil(LOCATE_TIMEOUT, "定位超时");
            case LOCATE_NO_RESULT:
                return new DataModelErrorUtil(LOCATE_NO_RESULT, "定位失败");
            case SEARCH_MUSCI_TIMEOUT:
                return new DataModelErrorUtil(SEARCH_MUSCI_TIMEOUT, "音乐信息获取超时");
            case SEARCH_STOCK_TIMEOUT:
                return new DataModelErrorUtil(SEARCH_STOCK_TIMEOUT, "股票信息获取超时");
            case SEARCH_WEATHER_TIMEOUT:
                return new DataModelErrorUtil(SEARCH_WEATHER_TIMEOUT, "天气信息获取超时");
            case CITY_POI_LOCATION:
                return new DataModelErrorUtil(CITY_POI_LOCATION, "根据城市获取位置信息超时");
            case SEARCH_POI_BAIDU_CITY_NULL:
                return new DataModelErrorUtil(SEARCH_POI_BAIDU_CITY_NULL, "搜索城市结果为空");
            case NETWORK_EXCEPTION:
                return new DataModelErrorUtil(NETWORK_EXCEPTION, "网络异常");
            case RUNTIME_EXCEPTION:
                return new DataModelErrorUtil(RUNTIME_EXCEPTION, "系统错误");
            case RETURN_SEARCH_TIMEOUT:
                return new DataModelErrorUtil(RETURN_SEARCH_TIMEOUT, "搜索周边信息超时");
            default:
                return new DataModelErrorUtil(code, "未知错误");
        }
    }
}
