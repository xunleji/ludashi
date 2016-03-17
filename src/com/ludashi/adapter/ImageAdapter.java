package com.ludashi.adapter;
import java.io.File;
import java.util.zip.ZipFile;

import com.ludashi.mains.R;
import com.ludashi.tool.MyZipUtil;
import com.ludashi.tool.OnConnectionEndListener;
import com.ludashi.tool.Parameter;
import com.ludashi.tool.ToolUtil;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

/**
 * 广告栏的显示
 * @author xujuan
 *
 */
public class ImageAdapter extends BaseAdapter implements OnConnectionEndListener{

	private Context context;
	private String name;
	private File dir;

	public ImageAdapter(Context context,String string){
		this.context = context;
//		if(string == null)return;
		try {
			name = ToolUtil.getSDPath() + Parameter.download +  string.substring(0, string.length() - 4);
			createSDDir(name);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private void createSDDir(String dirName){
		dir = new File(dirName);
		if (!dir.exists()) {// 目录存在返回false
			 dir.mkdirs();// 创建一个目录
		}
	}
	
	@Override
	public int getCount() {
		return dir.list().length;
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

		ImageView iv = new ImageView(context);
		iv.setImageBitmap(Parameter.getImgaes(dir.getPath() + "/" + dir.list()[position]));
		iv.setAdjustViewBounds(true);  
//		iv.setImageResource(R.drawable.image06);
		iv.setLayoutParams(new Gallery.LayoutParams(   
				LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));   
		return iv;
		
	}
	
	public class ViewHolder{
		public ImageView icn;
	}

	@Override
	public void onConnectionEndListener() {
		
	}
	
	
	

}
