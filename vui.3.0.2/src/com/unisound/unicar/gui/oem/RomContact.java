/**
 * Copyright (c) 2012-2013 YunZhiSheng(Shanghai) Co.Ltd. All right reserved.
 * 
 * @FileName : RomContact.java
 * @ProjectName : vui_assistant
 * @PakageName : cn.yunzhisheng.vui.assistant.oem
 * @Author : Dancindream
 * @CreateDate : 2013-9-9
 */
package com.unisound.unicar.gui.oem;

import java.io.IOException;
import java.io.InputStream;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.ContactsContract;

/**
 * @Module : 隶属模块名
 * @Comments : 描述
 * @Author : Dancindream
 * @CreateDate : 2013-9-9
 * @ModifiedBy : Dancindream
 * @ModifiedDate: 2013-9-9
 * @Modified: 2013-9-9: 实现基本功能
 */
public class RomContact {
    public static final String TAG = "RomContact";

    public static Drawable loadContactDrawable(Context context, long contactPhotoId) {
        Drawable photoDrawable = null;

        if (contactPhotoId > 0) {
            Uri photoUri =
                    ContentUris.withAppendedId(ContactsContract.Data.CONTENT_URI, contactPhotoId);

            photoDrawable = loadPhotoDrawable(context, photoUri);
        }

        return photoDrawable;
    }

    private static Drawable loadPhotoDrawable(Context context, Uri photoUri) {
        Drawable photoDrawable = null;
        InputStream inputStream = null;

        try {
            inputStream = context.getContentResolver().openInputStream(photoUri);

            photoDrawable = Drawable.createFromStream(inputStream, null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            inputStream = null;
        }

        return photoDrawable;
    }
}
