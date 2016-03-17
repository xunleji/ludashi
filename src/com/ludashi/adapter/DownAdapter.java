package com.ludashi.adapter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ludashi.mains.LuDaShiActivity;
import com.ludashi.mains.R;
import com.ludashi.mains.ReadBook;
import com.ludashi.mains.ReadImage;
import com.ludashi.tool.MyZipUtil;
import com.ludashi.tool.OnConnectionEndListener;
import com.ludashi.tool.Parameter;
import com.ludashi.tool.Rms;
import com.ludashi.tool.ToolUtil;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 广告栏的显示
 * @author xujuan
 *
 */
public class DownAdapter extends BaseAdapter implements OnConnectionEndListener{

	private Context context;
	private JSONArray data;
	private File f;
	private File dir;
	private Bitmap[] icon;
	private Rms rms;
	private String path = ToolUtil.getSDPath() + Parameter.download;
	private int completem = 0;
	private DecimalFormat fnum;

	public DownAdapter(Context context){
		if(fnum == null)fnum = new DecimalFormat("##0.00");
		this.context = context;
		if(rms == null)rms = new Rms(context);
		data = rms.getDownJson();
		LuDaShiActivity.outLog("DownAdapter_data = " + data);
		if(this.data != null){
			icon = new Bitmap[data.length()];
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
	public int getItemViewType(int position) {
		return completem;
	}

	public void toUpdata(){
		data = rms.getDownJson();
		if(data != null){
			icon = new Bitmap[data.length()];
		}
		this.notifyDataSetChanged();
	}
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewIng down_ing = null;
		ViewIng down_complete = null;
		final JSONObject json;
		try {
			json = data.getJSONObject(position);
			File get = new File(path + json.getString("file_name"));
			if(!get.exists()){
				f = new File(path + json.getString("file_name") + ".temp");
			}else{
				f = get;
			}
			
			int Max = json.getInt("se");
			int Progress = (int)f.length();
		    
		    if(Max == Progress){
		    	completem = 1;
		    }else{
		    	completem = 0;
		    }
		    
//			if(convertView == null){
				if(completem == 1){
					convertView = LayoutInflater.from(context).inflate(R.layout.down_item_complete, null);
					down_complete = new ViewIng();
					down_complete.icn = (ImageView)convertView.findViewById(R.id.downitemiv);
					down_complete.name = (TextView)convertView.findViewById(R.id.down_name);
					down_complete.play = (Button)convertView.findViewById(R.id.pause);
					down_complete.delete = (Button)convertView.findViewById(R.id.delete);
//					convertView.setTag(down_complete);
				}else{
					convertView = LayoutInflater.from(context).inflate(R.layout.down_item_ing, null);
					down_ing = new ViewIng();
					down_ing.icn = (ImageView)convertView.findViewById(R.id.downitemiv);
					down_ing.name = (TextView)convertView.findViewById(R.id.down_name);
					down_ing.pg = (ProgressBar)convertView.findViewById(R.id.down_ProgressBar);
					down_ing.play = (Button)convertView.findViewById(R.id.pause);
					down_ing.delete = (Button)convertView.findViewById(R.id.delete);
					down_ing.se = (TextView)convertView.findViewById(R.id.downtv_se);
//					convertView.setTag(down_ing);
				}
//			}else{
				    if(completem == 1){
//				    	down_complete=(ViewIng)convertView.getTag();
						if(icon[position] == null){
							icon[position] = Parameter.getImgae(json.getString("pic_1"));
						}
						if(icon[position] == null){
							down_complete.icn.setImageResource(R.drawable.image_book);
						}else{
							down_complete.icn.setImageBitmap(icon[position]);
						}
						down_complete.name.setText(json.getString("book_name"));
						down_complete.play.setOnClickListener(new View.OnClickListener() {
							
							@Override
							public void onClick(View v) {
								try {
									 String string = json.getString("file_name");
							    	 String name = string.substring(0, string.length() - 4);
							    	 try {
								   		   MyZipUtil.upZipSelectedFile(new File(ToolUtil.getSDPath() + Parameter.download + string), 
								   					ToolUtil.getSDPath() + Parameter.download + name);
								   		   } catch (Exception e) {
								   			e.printStackTrace();
								   		   }
							    	 
							    	File ff = new File(ToolUtil.getSDPath() + Parameter.download +  name).listFiles()[0];
							    	if(!ff.exists())return;
						    		if(ff.getName().contains(".txt")){
										LuDaShiActivity.outLog("int>>>>ReadBook");
										Intent intent = new Intent(context, ReadBook.class);
										intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
										intent.putExtra("file_name", ff.getPath());
										context.startActivity(intent);
									}else {
										LuDaShiActivity.outLog("int>>>>ReadImage");
										Intent intent = new Intent(context, ReadImage.class);
										intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
										intent.putExtra("file_name", json.getString("file_name"));
										context.startActivity(intent);
									}
									
								} catch (Exception e) {
									e.printStackTrace();
								}
								
							}
						});
						
                        down_complete.delete.setOnClickListener(new View.OnClickListener() {
							
							@Override
							public void onClick(View v) {
								rms.deleteDown(json);
								 try {
									new File(path + json.getString("file_name")).delete();
									new File(path + json.getString("file_name")+".temp").delete();
									delAllFile(path + json.getString("file_name").substring(0, json.getString("file_name").length()-4));
								   } catch (Exception e) {
									e.printStackTrace();
								}
								toUpdata();
							}
						});
						
						
				    	
				    }else{
//				    	down_ing=(ViewIng)convertView.getTag();
						if(icon[position] == null){
							icon[position] = Parameter.getImgae(json.getString("pic_1"));
						}
						if(icon[position] == null){
							down_ing.icn.setImageResource(R.drawable.image_book);
						}else{
							down_ing.icn.setImageBitmap(icon[position]);
						}
						down_ing.name.setText(json.getString("book_name"));
						//进度条
						if(Max >= Progress){
							down_ing.pg.setMax(Max);
							down_ing.pg.setProgress(Progress);
						}else{
							down_ing.pg.setMax(100);
							down_ing.pg.setProgress(0);
						}
						
						down_ing.se.setText(""+ fnum.format((float)Max/1048576) + "M");
						
						down_ing.play.setOnClickListener(new Button.OnClickListener() {
						
							@Override
							public void onClick(View v) {
								try {
									if(LuDaShiActivity.dl.downloads && 
									    LuDaShiActivity.dl.downing_id.equals(json.getString("book_id"))){
										  LuDaShiActivity.dl.stopDown();
										  ((Button)v).setText("继续");
										  return;
									  }else {
										  if(LuDaShiActivity.dl.downloads){
											  LuDaShiActivity.dl.stopDown();
											  LuDaShiActivity.dl.downFile(context, json);
											  ((Button)v).setText("暂停");
										  }else{
											  LuDaShiActivity.dl.downFile(context, json);
											  ((Button)v).setText("暂停");
										  }
										  return;
									}
									
								} catch (JSONException e) {
									e.printStackTrace();
								}
								
							}
						});
						
						if(LuDaShiActivity.dl.downloads && 
							    LuDaShiActivity.dl.downing_id.equals(json.getString("book_id"))){
							    down_ing.play.setText("暂停");
							  }else {
								  down_ing.play.setText("继续");
							  }
						down_ing.delete.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								rms.deleteDown(json);
							    try {
									new File(path + json.getString("file_name")).delete();
									new File(path + json.getString("file_name")+".temp").delete();
									delAllFile(path + json.getString("file_name").substring(0, json.getString("file_name").length()-4));
								   } catch (Exception e) {
									e.printStackTrace();
								}
								toUpdata();
							}
						});
						
				    }
//			}
			
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return convertView;
	}
	
	public class ViewIng{
		public ImageView icn;
		public TextView name,se;
		public ProgressBar pg;
		public Button play,delete;
	}

	@Override
	public void onConnectionEndListener() {
		
	}
	
	private boolean delAllFile(String path) {
	       boolean flag = false;
	       File file = new File(path);
	       if (!file.exists()) {
	         return flag;
	       }
	       if (!file.isDirectory()) {
	         return flag;
	       }
	       String[] tempList = file.list();
	       File temp = null;
	       for (int i = 0; i < tempList.length; i++) {
	          if (path.endsWith(File.separator)) {
	             temp = new File(path + tempList[i]);
	          } else {
	              temp = new File(path + File.separator + tempList[i]);
	          }
	          if (temp.isFile()) {
	             temp.delete();
	          }
	          if (temp.isDirectory()) {
	             delAllFile(path + "/" + tempList[i]);//先删除文件夹里面的文件
	             flag = true;
	          }
	       }
	       file.delete();
	       return flag;
	   }

	

}
