package com.ludashi.adapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ludashi.mains.R;
import com.ludashi.tool.ImageLoad;
import com.ludashi.tool.OnConnectionEndListener;
import com.ludashi.tool.Parameter;
import com.ludashi.tool.ToolUtil;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 广告栏的显示
 * @author xujuan
 *
 */

public class BookAdapter extends BaseAdapter implements OnConnectionEndListener{

	private Context context;
	private JSONArray data;
	private int ty;
	private Bitmap[] icon;

	public BookAdapter(Context context, JSONArray datas,int ty){
		this.context = context;
		this.data = datas;
		this.ty = ty;
		if(this.data != null){
			icon = new Bitmap[data.length()];
			new Thread(new Runnable() {
				@Override
				public void run() {
					for (int i = 0; i < data.length(); i++) {
						try {
							new ImageLoad().setImageLoad(data.getJSONObject(i).getString("pic_1"),BookAdapter.this);
						   } catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}).start();
		}else{
		   Toast.makeText(context, "! 请检查网络是否正常", Toast.LENGTH_SHORT).show(); 
		}
	}
	
	@Override
	public int getCount() {
		if(data != null){
			return data.length();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int id) {
		return id;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;
		if(convertView==null){
			convertView = LayoutInflater.from(context).inflate(R.layout.item_main, null);
			viewHolder = new ViewHolder();
			viewHolder.icn = (ImageView)convertView.findViewById(R.id.image_icon);
			viewHolder.tv = (TextView)convertView.findViewById(R.id.book_name);
			convertView.setTag(viewHolder);
		}else{
			viewHolder=(ViewHolder)convertView.getTag();
		}
		try {
			JSONObject json = data.getJSONObject(position);
			if(ty == 2){
				viewHolder.tv.setText(json.getString("book_name"));
			}else{
				viewHolder.tv.setText(json.getString("ca_name"));
			}
			
			if(icon[position] == null){
				icon[position] = Parameter.getImgae(json.getString("pic_1"));
			}
			if(icon[position] == null){
				viewHolder.icn.setImageResource(R.drawable.image_book);
			}else{
				viewHolder.icn.setImageBitmap(icon[position]);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return convertView;
	}
	
	public class ViewHolder{
		public ImageView icn;
		public TextView tv;
	}

	@Override
	public void onConnectionEndListener() {
		
		((Activity)context).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				BookAdapter.this.notifyDataSetChanged();
			}
		});
		
	}
	
	public JSONArray upData(JSONArray json){
		try {
			if(json == null)return data;
			for (int i = 0; i < json.length(); i++) {
				data.put(json.getJSONObject(i));
			}
			
			if(this.data != null){
				icon = new Bitmap[data.length()];
				new Thread(new Runnable() {
					@Override
					public void run() {
						for (int i = 0; i < data.length(); i++) {
							try {
								new ImageLoad().setImageLoad(data.getJSONObject(i).getString("pic_1"),BookAdapter.this);
							   } catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}).start();
			}else{
			   Toast.makeText(context, "! 请检查网络是否正常", Toast.LENGTH_SHORT).show(); 
			}
			
			this.notifyDataSetChanged();
			System.out.println("json = " + json.toString());
			System.out.println("data = " + json.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}
	
	

}
