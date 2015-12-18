package com.colink.util;

import com.colink.bluetoolthe.R;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

public class CustomDialog extends Dialog {
    private Button positiveButton, negativeButton;
    private TextView title;
 
    public CustomDialog(Context context) {
        super(context,R.style.Dialog);
        setCustomDialog();
    }
 
    private void setCustomDialog() {
        View mView = LayoutInflater.from(getContext()).inflate(R.layout.bluetoothe_alert, null);
        title = (TextView) mView.findViewById(R.id.textView1);
        positiveButton = (Button) mView.findViewById(R.id.button1);
        positiveButton.setText("确定");
        negativeButton = (Button) mView.findViewById(R.id.button2);
        negativeButton.setText("取消");
        super.setContentView(mView);
    }
     
    public TextView getTextView(){
        return title;
    }
     
     @Override
    public void setContentView(int layoutResID) {
    }
 
    @Override
    public void setContentView(View view, LayoutParams params) {
    }
 
    @Override
    public void setContentView(View view) {
    }
 
    /**
     * 确定键监听器
     * @param listener
     */ 
    public void setOnPositiveListener(View.OnClickListener listener){ 
        positiveButton.setOnClickListener(listener); 
    } 
    /**
     * 取消键监听器
     * @param listener
     */ 
    public void setOnNegativeListener(View.OnClickListener listener){ 
        negativeButton.setOnClickListener(listener); 
    }
}
