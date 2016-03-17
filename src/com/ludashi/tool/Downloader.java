package com.ludashi.tool;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import org.json.JSONArray;
import org.json.JSONObject;

import com.ludashi.mains.LuDaShiActivity;
import com.ludashi.mains.ReadBook;
import com.ludashi.mains.ReadImage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.Toast;

public class Downloader{
	
	private URL url = null; 
	protected Context context;
	public static boolean downloads = false;
	private long nFileLength;
	private int loads;
	Intent localIntent;
	public String done;
	private String fileName;
	public String fileNameApk;
	private long time;
	String juzilist = "0";
	int mainlist;
	protected boolean actapk = false;
	private String downProgress = "0/100";
	private int id = 0;
	private DecimalFormat fnum;
	private JSONObject downData;
	private String durl;//下载地址
	private String filename;//文件名
	private String address = ToolUtil.getSDPath() + Parameter.download;
	private JSONArray downing;
	private Rms rms;
	public static String downing_id;
	
	public Downloader(){
		if(fnum == null)fnum = new DecimalFormat("##0.00");
		if(downing == null)downing = new JSONArray();
	}
	
	public void downFile(Context context,JSONObject json){
	   this.context = context;
	   this.downData = json;
	   LuDaShiActivity.outLog("downFile_json = " + json);
	   if(rms == null)rms = new Rms(context);
	   
       if (ToolUtil.getSDPath() == "null") {
			Toast.makeText(context.getApplicationContext(), "请插入SD卡",1000).show();
	      }else{
	    	createSDDir(address);
	    	try {
				durl = json.getString("book_file");
				filename = durl.substring(durl.lastIndexOf("/") + 1).split("zip")[0] + "zip";
				if(isFileExist(address + filename)){
					
				    downData.put("se", new File(address + filename).length());
				    downData.put("file_name", filename);
				    rms.saveDown(downData);
					
			    	 String name = filename.substring(0, filename.length() - 4);
			    	 try {
				   		   MyZipUtil.upZipSelectedFile(new File(ToolUtil.getSDPath() + Parameter.download + filename), 
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
					
				}else{
					downData.put("se", 0);
					downData.put("file_name", filename);
					rms.saveDown(downData);
					if(!downloads){
						downing_id = downData.getString("book_id");
						savedFile(address + filename, durl, downData);
						Toast.makeText(context, "已添加到下载列表", Toast.LENGTH_SHORT).show();
					}
				}
			  } catch (Exception e) {
				e.printStackTrace();
			}
	      }
	}
	
	private int FILESIZE = 1024 * 30; 
	private void createSDDir(String dirName){
		File dir = new File(dirName);
		if (!dir.exists()) {// 目录存在返回false
			 dir.mkdirs();// 创建一个目录
		}
	}
	
	protected boolean isFileExist(String fileName){
		File file = new File(fileName);
		return file.exists();
	}
	
	//暂停下载
	public void stopDown(){
		downloads = false;
		if(conn != null)conn.disconnect();
	}
	
	//获取下载进度
	protected String downProgress(){
		return downProgress;
	}
	
	private Handler mh = new Handler();
	private RandomAccessFile savedFile;
	private  HttpURLConnection conn = null;
	protected void savedFile(final String str ,final String urlStr, final JSONObject json){
		
//		localIntent = new Intent();
//		localIntent.setClass(context, MoDaoKanTuActivity.class);
//		localIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
//		showNotification("读取数据","正在获取下载数据","");
		
		downloads = true;
		
		new Thread(new Runnable() {
			private boolean head;
			@Override
			public void run() {
			    conn = null;
			    head = false;
				try {
		            savedFile = new RandomAccessFile(str + ".temp", "rwd");		            
		            long start = savedFile.length();
		            
		            String urlStred = ((String)urlStr).replaceAll(" ", "%20");
					url = new URL(urlStred);
				    conn = (HttpURLConnection) url.openConnection();
					conn.setRequestMethod("GET");
		            conn.setConnectTimeout(20000);
		            nFileLength = conn.getContentLength();
		            
		            rms.deleteDown(json);
		            json.put("se", nFileLength);
					rms.saveDown(json);
					LuDaShiActivity.outLog("nFileLength = " + nFileLength);
					  
		            conn.disconnect();
		            conn = (HttpURLConnection) url.openConnection();
		        	conn.setRequestMethod("GET");
		            conn.setConnectTimeout(20000);
	            	try {
						conn.setRequestProperty("RANGE", "bytes=" + start + "-" + nFileLength);
					} catch (Exception e) {
						e.printStackTrace();
					}
	            	
		            InputStream in = conn.getInputStream();
		            
		            byte[] buffer = new byte[FILESIZE];
		            int len = 0; 
		            savedFile.seek(start);
		            time = System.currentTimeMillis();
		            while (downloads && (len = in.read(buffer)) != -1 ) {
		               savedFile.write(buffer, 0, len);
		               loads = (int)savedFile.length();
//		               if(System.currentTimeMillis()- time > 2000){
//		            	   mh.post(new Runnable() {
//					       	      @Override
//					       	      public void run() {
//					       	         try {
//				       	        		  time = System.currentTimeMillis();
//				       	        		  done = fnum.format((float) loads / 1048576)
//												+ "M/"
//												+ fnum.format((float) nFileLength / 1048576)
//												+ "M";
//				       	        		  downProgress = loads + "/" + nFileLength;
//									      showNotification("正在下载",fileName,done);								    
//									      }catch (Exception e) {
//									      }
//					       	          }
//					       	   });
//		               }
		            }
		            in.close();
		            
		            if(downloads){
			           	downProgress = "100/100";
			            File file = new File(str +".temp");
			            file.renameTo(new File(str));
			            downloads = false;
			           	showNotification("正在下载",fileName,"已完成下载");	
						head = true;
		            }else{
//		            	showNotification("暂停",fileName,done + "(暂停中)");
		            }
		         } catch (Exception e) {
		        	 downloads = false;
		        	 e.printStackTrace();
		             showNotification("下载失败",fileName,"网络环境不佳 下载停止");
		        }finally{
		        	if(savedFile != null){
		        		try {
							savedFile.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
		        	}
		        	if(conn != null)conn.disconnect();
		        	if(!downloads&!head){
		        		if(done==null)done = "";
		    		}else{
		    			
		    		}
		        }
		    }
		}).start();
	}
	
//    NotificationManager m_NotificationManager;   
//    Intent              m_Intent;   
//    PendingIntent       m_PendingIntent;   
//    Notification        m_Notification;   

    private void showNotification(String str,String name,final String jindu){  
    	
    	if(context != null){
    	((Activity)context).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try {
					 Toast.makeText(context, jindu, Toast.LENGTH_SHORT).show();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
    	}
    	
//    	if(jindu == null)jindu = "";
//    	if(m_NotificationManager == null)
//        m_NotificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);   
//        //主要是设置点击通知时显示内容的类   
//    	if(m_PendingIntent == null)
//        m_PendingIntent = PendingIntent.getActivity(context, 0, localIntent, PendingIntent.FLAG_CANCEL_CURRENT); //如果轉移內容則用m_Intent(); 
//        //构造Notification对象   
//    	if(m_Notification == null)
//        m_Notification = new Notification();   
//        //设置通知在状态栏显示的图标   
//    	if(jindu.equals("已完成")||jindu.contains("暂停中")){
//    		   m_Notification.icon = android.R.drawable.stat_sys_download_done; 
//    	}else{
//    		   m_Notification.icon = android.R.drawable.stat_sys_download; 
//    	}
//        //当我们点击通知时显示的内容   
//        m_Notification.tickerText = str;           
//        m_Notification.flags |= Notification.FLAG_AUTO_CANCEL;
//        //设置通知显示的参数   
//        m_Notification.setLatestEventInfo(context, name, jindu, m_PendingIntent);   
//        //可以理解为执行这个通知   
//        m_NotificationManager.notify(id, m_Notification); 
    	
    }

}
