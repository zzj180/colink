package com.unisound.unicar.gui.session;

import java.util.ArrayList;

import android.content.Context;
import android.os.Handler;

import com.unisound.unicar.gui.model.ContactInfo;
import com.unisound.unicar.gui.model.PhoneNumberInfo;

public class ContactSelectBaseSession extends SelectCommonSession {
    protected ArrayList<ContactInfo> mContactInfoList = new ArrayList<ContactInfo>();
    protected ArrayList<PhoneNumberInfo> mPhoneNumberInfoList = new ArrayList<PhoneNumberInfo>();
    /* 2014.01.23 added by SC for showing sms call icon properly */
    protected ArrayList<SideBannerInfo> mBannerInfoList = new ArrayList<SideBannerInfo>();

    /* end */

    public ContactSelectBaseSession(Context context, Handler sessionManagerHandler) {
        super(context, sessionManagerHandler);
    }

    /* 2014.01.23 added by SC for showing sms call icon properly */
    public class SideBannerInfo {
        public PhoneNumberInfo phoneInfo;
        public boolean callIcon;
        public boolean smsIcon;
    }
    /* end */

}
