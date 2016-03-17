package com.ludashi.tool;
import java.io.File;

import com.ludashi.mains.LuDaShiActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class Parameter {
	
	public final static String cache = "/LuDaShi/";
	public final static String download = "/LuDaShiDownLoad/";
	
	public static Bitmap getImgae(String url){
		String[] str = url.split("/");
		String name = str[str.length-1];
		File paths = new File(ToolUtil.getSDPath()+ cache + name);
		if (!paths.exists()) {
			 return null;
		}else{
			BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 0;
            Bitmap bm = BitmapFactory.decodeFile(ToolUtil.getSDPath()+ cache + name, options);
            return bm;
		}
	}
	
	public static Drawable getDrawable(String url){
		String[] str = url.split("/");
		String name = str[str.length-1];
		File paths = new File(ToolUtil.getSDPath()+ cache + name);
		if (!paths.exists()) {
			 return null;
		}else{
			BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 0;
            Bitmap bm = BitmapFactory.decodeFile(ToolUtil.getSDPath()+ cache + name, options);
            Drawable drawable = new BitmapDrawable(bm);		
            return drawable;
		}
	}
	
	public static Bitmap getImgaes(String name){
		File paths = new File(name);
		if (!paths.exists()) {
			 return null;
		}else{
			BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 0;
            Bitmap bm = BitmapFactory.decodeFile(name ,options);
            return bm;
		}
		
	}
	
	
}
