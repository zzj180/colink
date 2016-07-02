/**
 * Copyright (c) 2012-2013 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * 
 * @FileName : ErrorUtil.java
 * @ProjectName : vui_common
 * @PakageName : cn.yunzhisheng.common.util
 * @Author : Dancindream
 * @CreateDate : 2013-10-12
 */
package com.unisound.unicar.gui.utils;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Dancindream
 * @CreateDate : 2013-10-12
 * @ModifiedBy : Dancindream
 * @ModifiedDate: 2013-10-12
 * @Modified: 2013-10-12: 实现基本功能
 */
public class ErrorUtil {
    public static final String TAG = "ErrorUtil";
    public int code = 0;
    public String message = "";

    public ErrorUtil(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * @Description : toString
     * @Author : Dancindream
     * @CreateDate : 2013-10-12
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "code:" + code + ";message:" + message;
    }
}
