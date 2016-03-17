package com.ludashi.tool;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/***
 * 获取网络图片
 * @author yuhongbing
 *
 */
public class ImageLoad{

	private String url;
	private String name;
	private String path;
	private int FILESIZE = 1024 << 6; 
	private OnConnectionEndListener li;
	public ImageLoad(){
		
	}

	public void setImageLoad(String url,OnConnectionEndListener li) {
		this.url = url;
		this.li = li;
		String[] str = url.split("/");
		name = str[str.length-1];
		path = ToolUtil.getSDPath()+ Parameter.cache + name;
		createSDDir();
		if(getImgae()){
			return;
		}else{
			run();
		}
	}
	
	protected File createSDDir(){
		
		File path = new File(ToolUtil.getSDPath() + Parameter.cache);// 创建目录
		if (!path.exists()) {// 目录存在返回false
			path.mkdirs();// 创建一个目录
		}
		return path;
	}
	
	public boolean getImgae(){
		File paths = new File(path);
		if(paths.length() == 0){
			paths.delete();
			return false;
		}
		if (!paths.exists()) {
			return false;
		}else{
            return true;
		}
	}
	
	public void run(){
		HttpURLConnection conn = null;
			try {
	            RandomAccessFile savedFile = new RandomAccessFile(path + ".temp", "rwd");		
	            URL urls = new URL(url);
				conn = (HttpURLConnection) urls.openConnection();
	            conn.setConnectTimeout(20000);
	            conn.disconnect();
	            InputStream in = conn.getInputStream();
	            byte[] buffer = new byte[FILESIZE];
	            int len = 0; 
	            while ((len = in.read(buffer)) != -1) {
	               savedFile.write(buffer, 0, len);
	            }
	            savedFile.close();
	            in.close();
	            File file = new File(path +".temp");
	            file.renameTo(new File(path));
	            if(li  != null)li.onConnectionEndListener();
	         }catch(Exception e){
	        	 e.printStackTrace();
	         }finally{
	        	if(conn != null)conn.disconnect();
	        }
	}
	
	
}
