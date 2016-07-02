package com.unisound.unicar.gui.view;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.coogo.inet.vui.assistant.car.R;
import com.unisound.unicar.gui.model.ContactInfo;
import com.unisound.unicar.gui.utils.Logger;

/**
 * Multiple Contacts View
 * 
 * @author xiaodong
 * 
 */
public class PickPersonView extends PickBaseView {

    public PickPersonView(Context context) {
        super(context);
    }

    public void initView(ArrayList<ContactInfo> contactInfos) {
        Logger.d("PickPersonView", "!--->---initView()--------");
        Context context = getContext();

        View header = mLayoutInflater.inflate(R.layout.pickview_header_muti_numbers, this, false);
        TextView tvHead = (TextView) header.findViewById(R.id.tv_header_muti_number);
        tvHead.setText(context.getString(R.string.call_out_title_muti_contacts, contactInfos.size()));
        setHeader(header);

        for (int i = 0; i < contactInfos.size(); i++) {
            ContactInfo contactInfo = contactInfos.get(i);

            View view = mLayoutInflater.inflate(R.layout.pickview_item_contact, mContainer, false);

            TextView tvName = (TextView) view.findViewById(R.id.textViewName);
            tvName.setText(contactInfo.getDisplayName());

            TextView noText = (TextView) view.findViewById(R.id.textViewNo);
            noText.setText((i + 1) + "");

            // ImageView imageViewAvatar = (ImageView) view.findViewById(R.id.imageViewAvatar);
            // Drawable drawable = RomContact.loadContactDrawable(context,
            // contactInfo.getPhotoId());
            // if (drawable != null) {
            // imageViewAvatar.setImageDrawable(drawable);
            // } else {
            // imageViewAvatar.setImageResource(R.drawable.ic_contact_avatar_new);
            // }

            addItem(view);
        }
    }



}
