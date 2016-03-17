package com.ludashi.mains;
import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import com.ludashi.tool.HttpGetData;
import com.ludashi.tool.ImageLoad;
import com.ludashi.tool.OnConnectionEndListener;
import com.ludashi.tool.Parameter;
import com.ludashi.tool.ToolUtil;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

//图书简介
public class BookContent extends FrameLayout implements OnConnectionEndListener{

	private View view;
	private Context context;
	private ImageView dbutten,icon,content_iamge;
	private View loading;
	private String data;
	private String book_id;
	private TextView title,author,time,desc;
	private JSONObject downUrl;

	
	
	public BookContent(Context context,String book_id) {
		super(context);
		this.context = context;
		this.book_id = book_id;
		this.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		loading = LayoutInflater.from(context).inflate(R.layout.loadview, null);
		setView();
	}
	
	/**
	 * 展现内容
	 */
	public void setView(){
		this.addView(loading);
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				data = new HttpGetData(context).getData("bookinfo?book_id=" + book_id);
				LuDaShiActivity.outLog("BookContent = " + data);
				((Activity)context).runOnUiThread(new Runnable() {
					@Override
					public void run() {
						view = LayoutInflater.from(context).inflate(R.layout.book, null);
						view.setOnClickListener(on);
						dbutten = (ImageView)view.findViewById(R.id.book_down);
						title = (TextView)view.findViewById(R.id.textView1);
						author = (TextView)view.findViewById(R.id.textView2);
						time = (TextView)view.findViewById(R.id.textView3);
						desc = (TextView)view.findViewById(R.id.textView4);
						icon = (ImageView)view.findViewById(R.id.image_icon);
						content_iamge = (ImageView)view.findViewById(R.id.image_content);
						dbutten.setOnClickListener(on);
						BookContent.this.removeAllViews();
						BookContent.this.addView(view);
						try {
							JSONObject json = new JSONObject(data);
							if(json.getInt("rescode") == 104){
								downUrl = json.getJSONObject("data");
								title.setText(downUrl.getString("book_name"));
								author.setText(downUrl.getString("book_auth"));
								time.setText(downUrl.getString("time"));
								desc.setText(downUrl.getString("book_desc"));
								Bitmap bt = Parameter.getImgae(downUrl.getString("pic_1"));
								
								String durl = downUrl.getString("book_file");
								String filename = durl.substring(durl.lastIndexOf("/") + 1).split("zip")[0] + "zip";
								if(new File(ToolUtil.getSDPath() + Parameter.download + filename).exists()){
									dbutten.setImageResource(R.drawable.open);
								}
								if(bt != null){
									icon.setImageBitmap(Parameter.getImgae(downUrl.getString("pic_1")));
								}else{
									icon.setImageResource(R.drawable.image_book);
								}
								if(downUrl.getString("pic_3").length() == 0){
									
									return;
								}
								Bitmap bmp = Parameter.getImgae(downUrl.getString("pic_3"));
								if(bmp != null){
									content_iamge.setImageBitmap(bmp);
									content_iamge.setScaleType(ImageView.ScaleType.CENTER_CROP);
								}
								
//								content_iamge.setScaleType(ImageView.ScaleType.FIT_XY);
								
								new Thread(new Runnable() {
									@Override
									public void run() {
											try {
												new ImageLoad().setImageLoad(downUrl.getString("pic_3"),BookContent.this);
											   } catch (Exception e) {
												e.printStackTrace();
											}
									}
								}).start();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		}).start();
	}
	
	private OnClickListener on = new  OnClickListener() {
		@Override
		public void onClick(View v) {
			if(v == dbutten){
				if(!LuDaShiActivity.dl.downloads){
					try {
						if(downUrl != null){
							downUrl.put("book_id", book_id);
						}
						LuDaShiActivity.dl.downFile(context, downUrl);
					} catch (Exception e) {
						e.printStackTrace();
					}
				
				}

			}
		}
	};
	
	protected boolean isFileExist(String fileName){
		File file = new File(fileName);
		return file.exists();
	}

	@Override
	public void onConnectionEndListener() {
		
		try {
			if(content_iamge != null && context != null){
				((Activity)context).runOnUiThread(new Runnable() {
					@Override
					public void run() {
						try {
							content_iamge.setImageBitmap(Parameter.getImgae(downUrl.getString("pic_3")));
							content_iamge.setScaleType(ImageView.ScaleType.CENTER_CROP);
						} catch (Exception e) {
							e.printStackTrace();
						}
						
					}
				});
			
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
