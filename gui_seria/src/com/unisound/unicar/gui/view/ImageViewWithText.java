package com.unisound.unicar.gui.view;

import com.coogo.inet.vui.assistant.car.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 
 * @author xiaodong
 * @date 20150609
 */
public class ImageViewWithText extends LinearLayout {

    ImageView imageView;
    TextView textView;

    public ImageViewWithText(Context context) {
        this(context, null);
    }

    public ImageViewWithText(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.menu_item_layout, this, true);
        int textId = -1;
        int imageId = -1;
        imageView = (ImageView) findViewById(R.id.imageView);
        textView = (TextView) findViewById(R.id.textView);
        textId = attrs.getAttributeResourceValue(null, "Text", 0);
        imageId = attrs.getAttributeResourceValue(null, "ImageSrc", 0);
        if (textId > 0) {
            textView.setText(context.getResources().getText(textId));
        } else {}
        if (imageId > 0) {
            imageView.setImageDrawable(context.getResources().getDrawable(imageId));
        }
    }

    public ImageView getImageButton() {
        return imageView;
    }

    public CharSequence getShowText() {
        if (null == textView) {
            return "";
        } else {
            return textView.getText();
        }
    }

}
