package com.ludashi.tool;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;

import com.ludashi.mains.LuDaShiActivity;

import android.content.Context;
import android.util.Log;

public class HttpGetData {
	
	private Context context;
	private String http = "http://app.m2c.mobi/bookinfointface/";
	
	public HttpGetData(Context context){
		this.context = context;
	}
	
	public String getData(String uriPic) {
		if(!ToolUtil.isNet(context))return null;
	    URL imageUrl = null;
		String responseContent = null;
		HttpURLConnection conn = null;
		try {
			imageUrl = new URL(http + uriPic);
			LuDaShiActivity.outLog("get = " + http + uriPic);
			conn = (HttpURLConnection)imageUrl.openConnection();
			conn.setReadTimeout(60000);
			conn.setConnectTimeout(5000);
			conn.connect();
			if(conn.getResponseCode() == 302){
				Log.i("out",conn.getHeaderField("Location"));
			}else{
				InputStream is = conn.getInputStream();
				ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
				int i = 0;
				try {
					while((i=is.read()) != -1){
						bytestream.write(i);
					}
				  } catch (Exception e) {
				}
				byte data_byte[] = bytestream.toByteArray();
				bytestream.close();
				responseContent = URLDecoder.decode(new String(data_byte));
				is.close();
			}
		} catch (Exception e) {
		}finally{
			if(conn != null)conn.disconnect();
	}
    return responseContent;
 }
	
}
