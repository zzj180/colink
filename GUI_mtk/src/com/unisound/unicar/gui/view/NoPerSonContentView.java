package com.unisound.unicar.gui.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.coogo.inet.vui.assistant.car.R;
import com.unisound.unicar.gui.utils.ContactsUtil;

/**
 * 
 * “打电话” --> “打电话给谁？” “发短信” --> “发短信给谁？”
 * 
 * @author xiaodong
 * @date 20150623
 */
public class NoPerSonContentView extends FrameLayout implements ISessionView {
    public static final String TAG = NoPerSonContentView.class.getSimpleName();

    private TextView mTvTitle;
    private TextView mTextViewName;
    private Context mContext;

    public NoPerSonContentView(Context context) {
        super(context);
        mContext = context;
        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_no_person, this, true);
        findViews();

    }

    private void findViews() {
        mTvTitle = (TextView) findViewById(R.id.tv_title_noperson);

        mTextViewName = (TextView) findViewById(R.id.tv_noperson_tips);
        /* < XD 20150715 modify for no contact with name find Begin */
        String randomName = ContactsUtil.getRandomContactName(mContext);
        if ("".equals(randomName) || null == randomName) {
            mTextViewName.setText(R.string.call_find_no_person);
        } else {
            mTextViewName.setText(mContext.getString(R.string.input_contact_tips, randomName));
        }
        /* XD 20150715 modify for no contact with name find End > */
    }

    /**
     * 
     * @param s
     */
    public void setShowTitle(String s) {
        mTvTitle.setText(s);
    }

    /**
     * @param id
     */
    public void setShowTitle(int id) {
        mTvTitle.setText(id);
    }

    // public void setShowText(String s) {
    // mTextViewName.setText(s);
    // }
    //
    // public void setShowText(int id) {
    // mTextViewName.setText(id);
    // }


    @Override
    public boolean isTemporary() {
        return false;
    }

    @Override
    public void release() {

    }


}
