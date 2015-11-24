package com.unisound.unicar.gui.ui;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.coogo.inet.vui.assistant.car.R;

public class HelpActivity extends Activity {

    private int groupName = R.array.help_group_name;

    private int[] childContent = { R.array.help_child_content_1,
            R.array.help_child_content_2, R.array.help_child_content_3,
            R.array.help_child_content_4, R.array.help_child_content_5,
            R.array.help_child_content_6, R.array.help_child_content_7 };
    private ExpandableListAdapter mAdapter = new BaseExpandableListAdapter() {

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(
                    R.layout.help_listview_menu_title, null);
            TextView text = (TextView) convertView
                    .findViewById(R.id.menuTitleTxt);
            text.setText(getGroup(groupPosition).toString());
            ImageView imgIndicator = (ImageView) convertView
                    .findViewById(R.id.foldBtn);
            if (isExpanded) {
                imgIndicator.setBackgroundResource(R.drawable.btn_help_unfold);
            } else {
                imgIndicator.setBackgroundResource(R.drawable.btn_help_fold);
            }
            View hideView = convertView.findViewById(R.id.top_hide_view);
            if (groupPosition == 0) {
                hideView.setVisibility(View.VISIBLE);
            } else {
                hideView.setVisibility(View.GONE);
            }
            return convertView;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public int getGroupCount() {
            return getResources().getStringArray(groupName).length;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return getResources().getStringArray(groupName)[groupPosition];
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return getResources().getStringArray(childContent[groupPosition]).length;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition,
                boolean isLastChild, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(
                    R.layout.help_listview_submenu, null);
            TextView text = (TextView) convertView
                    .findViewById(R.id.subMenuTxt);
            text.setText(Html.fromHtml(getChild(groupPosition, childPosition)
                    .toString()));
            return convertView;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return getResources().getStringArray(childContent[groupPosition])[childPosition];
        }
    };
    private OnClickListener mReturnListerner = new OnClickListener() {
        @Override
        public void onClick(View v) {
            onBackPressed();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_help);
        ExpandableListView listview = (ExpandableListView) findViewById(R.id.helpListview);
        listview.setAdapter(mAdapter);
        ImageButton returnBtn = (ImageButton) findViewById(R.id.backBtn);
        returnBtn.setOnClickListener(mReturnListerner);
        int position = getIntent().getIntExtra("position", -1);
        if (position != -1) {
            listview.expandGroup(position);
            listview.setSelectedGroup(position);
        }

    }

}
