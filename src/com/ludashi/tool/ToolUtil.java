package com.ludashi.tool;
import java.io.File;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

public class ToolUtil {


	/**
	 * 获取sd卡路径
	 * 
	 * @return
	 */
	public static String getSDPath() {
		File sdDir = null;
		try {
			boolean sdCardExist = Environment.getExternalStorageState().equals(
					android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
			if (sdCardExist) {
				sdDir = Environment.getExternalStorageDirectory();// 获取存储卡根目录
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sdDir == null?"null":sdDir.toString();
	}
	
	public static boolean isNet(Context context) {
		
		ConnectivityManager cManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cManager.getActiveNetworkInfo();
		if (info != null && info.isAvailable()) {
			return true;
		} else {
//			Diary.Out("isNet = false");
			return false;
		}
	}
}
