package com.unisound.unicar.gui.view;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.coogo.inet.vui.assistant.car.R;
import com.unisound.unicar.gui.model.PoiInfo;
import com.unisound.unicar.gui.session.LocalSearchShowSession;
import com.unisound.unicar.gui.session.LocalSearchShowSession.CallbakeLocalSearch;
import com.unisound.unicar.gui.utils.Logger;
/**
 * 附近的“XXX” View
 * @author 
 *
 */
public class LocalSearchView extends PickBaseView {
	private static final String TAG = LocalSearchView.class.getSimpleName();
	
	private Context mContext;
	private boolean btn_more_use = false;
	private List<PoiInfo> infosData = null; 

	private int mLocalItemNo = 1;

	private CallbakeLocalSearch mCallback;

	public LocalSearchView(Context context) {
		super(context);
		mContext = context;
		infosData = new ArrayList<PoiInfo>();
	}
	
	
	public void initView(List<PoiInfo> infos,String category) {
		infosData = infos;
		Logger.d(TAG, "!--->initView-----category = "+category);
		View header = mLayoutInflater.inflate(
				R.layout.pickview_header_muti_numbers, this, false);
		TextView tvHead = (TextView) header
				.findViewById(R.id.tv_header_muti_number);
		tvHead.setText(mContext.getString(R.string.local_out_title_muti,
				mContext.getString(R.string.title_format_arg_info)));
		setHeader(header);
		
		if (infos.size() >= 5) {
			for (int i = 0; i < 5; i++) {
				btn_more_use = true;
				addItem(getLocalItemView(infos.get(i)));
			}
		}else if (infos.size() < 5) {
			for (int i = 0; i < infos.size(); i++) {
				btn_more_use = false;
				addItem(getLocalItemView(infos.get(i)));
			}
		}
		
		final View btn_more = mLayoutInflater.inflate(R.layout.list_item_load_more, null,false);
		LinearLayout layout = (LinearLayout)btn_more.findViewById(R.id.btn_more_local);
		final View dataformView = mLayoutInflater.inflate(R.layout.local_search_dataform_view, null,false);
		layout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Logger.d(TAG, "load more btn clicked");
				btn_more.setVisibility(View.GONE);
				dataformView.setVisibility(View.GONE);
				addMoreView();
			}
		});
		addButtom(btn_more);
		
		addItem(dataformView);
	}
	
	private void addMoreView(){
		if(null == infosData){
			Logger.w(TAG,"!--->addMoreView---infosData is null");
			return;
		}
		if (btn_more_use) {
			for (int i = 5; i < infosData.size(); i++) {
				btn_more_use = true;
				addItem(getLocalItemView(infosData.get(i)));
			}
			View dataformView = mLayoutInflater.inflate(R.layout.local_search_dataform_view, null, false);
			addItem(dataformView);
		}
	}

	@SuppressLint("NewApi")
	public View getLocalItemView(final PoiInfo infos) {
		View view = LayoutInflater.from(mContext).inflate(
				R.layout.list_item_localsearch, null, false);
		
		LinearLayout phone_layout =(LinearLayout) view.findViewById(R.id.phone_layout);
		LinearLayout distance_layout =(LinearLayout) view.findViewById(R.id.distance_layout);
		
		TextView tvLocalItemNo = (TextView) view.findViewById(R.id.textViewNo);
		tvLocalItemNo.setText(mLocalItemNo+"");
		mLocalItemNo++;
		
		TextView name = (TextView) view.findViewById(R.id.name);
		TextView address = (TextView) view.findViewById(R.id.mLocationInfo);
		TextView has_deal = (TextView) view.findViewById(R.id.has_deal);//是否有团购
		has_deal.setVisibility(View.GONE);
		RatingBar rating = (RatingBar) view.findViewById(R.id.rating);
		final TextView phone = (TextView) view.findViewById(R.id.phone);
		final TextView distance = (TextView) view.findViewById(R.id.distance);
		TextView text_rating = (TextView) view.findViewById(R.id.text_rating);
		TextView pice = (TextView) view.findViewById(R.id.pice);
		TextView leixin = (TextView) view.findViewById(R.id.leixin);
		TextView has_online_reservation = (TextView) view.findViewById(R.id.has_online_reservation);////是否在线订购
		has_online_reservation.setVisibility(View.GONE);
		TextView has_coupon = (TextView) view.findViewById(R.id.has_coupon);//是否有优惠
		has_coupon.setVisibility(View.GONE);
		String[] str = infos.getCategories();
		if (str != null) {
			StringBuffer buffer = new StringBuffer();
			for (int i = 0; i < str.length; i++) {
				buffer.append(str[i]);
			}
			leixin.setText(buffer.toString());
		}
		
		has_online_reservation.setText(infos.isHas_online_reservation()+"");
		has_coupon.setText(infos.isHas_coupon()+"");
		Logger.d(TAG, "!--->getLocalItemView---price = "+infos.getAvg_price());
		if (infos.getAvg_price() > 0) {
			pice.setText(mContext.getString(R.string.local_search_food_price) + infos.getAvg_price());
		}else{
			pice.setVisibility(View.INVISIBLE);
		}
		
		String branchName = infos.getBranchName();
		if (branchName != null && branchName.length() > 0) {
			name.setText(infos.getName() + "(" + branchName+ ")");
		}else{
			name.setText(infos.getName());
		}

		if (infos.getRegions() != null && infos.getRegions().length > 0) {
			address.setText(infos.getRegions()[0]);
		}
		has_deal.setText(infos.isHas_deal() + "");
		rating.setRating(infos.getRating()+0.07f);
		
		String phoneNum = infos.getTel();
		if (phoneNum != null && phoneNum.length() > 0) {
			phone.setText(infos.getTel());
		}else{
			Drawable phoneD = getResources().getDrawable(R.drawable.icon_near_unablecall);
			phoneD.setBounds(0, 0, phoneD.getMinimumWidth(), phoneD.getMinimumHeight());
			phone.setCompoundDrawables(phoneD, null, null, null);
			phone.setText(mContext.getString(R.string.local_search_no_number));
			phone.setTextColor(Color.argb(76, 246, 246, 246));  
			phone_layout.setEnabled(false);
		}

		double dist =  infos.getDistance()/1000.00;
		Logger.d(TAG, "!--->getLocalItemView--dist = "+dist+"KM; getDistance = "+infos.getDistance());
		distance.setText(mContext.getString(R.string.local_search_distance, dist));
		text_rating.setText(infos.getRating() + "");

		phone_layout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Logger.d(TAG, "phone_layout OnClick phone = "+phone.getText().toString());
				if (mCallback != null) {
					String selectedItem = infos.getSeletItem();
					String callselectedItem = infos.getCallSelectItem();
					Logger.d(TAG, "--189--selectedItem = "+selectedItem +";callselectedItem = "+callselectedItem);
					mCallback.dissMissLocalSessionView(LocalSearchShowSession.CALL_BACK_CALL,callselectedItem);
				}
				
//				Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone.getText().toString()));
//				callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				mContext.startActivity(callIntent);
			}
		});
		
		distance_layout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Logger.d(TAG, "distance_layout OnClick distance"+distance.getText().toString());
				if (mCallback != null) {
					String selectedItem = infos.getSeletItem();
					String callselectedItem = infos.getCallSelectItem();
					Logger.d(TAG, "--205--selectedItem = "+selectedItem +";callselectedItem = "+callselectedItem);
					
					mCallback.dissMissLocalSessionView(LocalSearchShowSession.CALL_BACK_NAVI,selectedItem);
				}
				
//				if (GaodeMap.hasGaodeMapClient(mContext)) {
//					GaodeUriApi.startNavi(mContext, infos.getLocationInfo().getLatitude(), infos.getLocationInfo().getLongitude(), infos.getRegions()[0], 2, 0);
//				}else {
//					Toast.makeText(mContext, R.string.gaode_nofind_map, Toast.LENGTH_LONG).show();
//				}
			}
		});
		return view;
	}

	public void setListener(CallbakeLocalSearch callback){
		mCallback = callback;
	}
	
	/**
	 * 
	 */
	public void release(){
		Logger.d(TAG, "!--->release----");
		infosData = null;
	}
}
