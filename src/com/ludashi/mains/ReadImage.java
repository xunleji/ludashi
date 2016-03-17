package com.ludashi.mains;

import org.w3c.dom.Text;

import com.ludashi.adapter.ImageAdapter;
import com.ludashi.tool.DetialGallery;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.TextView;
import android.widget.Toast;

public class ReadImage extends Activity{

	private DetialGallery gly;
	private ImageAdapter ia;
	private Context context;
	private TextView tx;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);// 全屏设置
		context = this;
		setContentView(R.layout.look_image);
		tx = (TextView)findViewById(R.id.lookimage_page);
		gly = (DetialGallery)findViewById(R.id.gallery_img);
		ia = new ImageAdapter(context,getIntent().getExtras().getString("file_name"));
		gly.setAdapter(ia);
		gly.setOnItemSelectedListener(new Gallery.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				    tx.setText((arg2+1) + " / " + arg0.getCount());
				    if(arg2 + 1 == arg0.getCount()){
				    	Toast.makeText(context, "阅读已完毕", Toast.LENGTH_SHORT).show();
				    }
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				LuDaShiActivity.outLog("DetialGallery go= " + arg0);
			}
		});
		
	}
	
	Dialog dialog;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			if(dialog != null && dialog.isShowing()){
				return true;
			}
			dialog = new AlertDialog.Builder(this).setMessage("是否退出阅读")
    			    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1){
							dialog.cancel();
							finish();
						}
					    }).setNegativeButton("取消", new DialogInterface.OnClickListener(){
						
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							dialog.cancel();
							return;
						}
					}).show();			
			}
			
		return true;
	} 

}
