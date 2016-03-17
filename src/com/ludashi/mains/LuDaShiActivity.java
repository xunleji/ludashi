package com.ludashi.mains;
import java.util.Arrays;
import java.util.LinkedList;

import net.youmi.android.AdManager;
import net.youmi.android.offers.OffersManager;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.waps.AppConnect;

import com.ludashi.adapter.BookAdapter;
import com.ludashi.adapter.DownAdapter;
import com.ludashi.adapter.PullToRefreshBase.OnRefreshListener;
import com.ludashi.adapter.PullToRefreshGridView;
import com.ludashi.tool.Diary;
import com.ludashi.tool.Downloader;
import com.ludashi.tool.HttpGetData;
import com.ludashi.tool.Rms;
import com.ludashi.tool.ToolUtil;
import com.ludashi.waps.LoadActivity;

import android.R.style;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

public class LuDaShiActivity extends Activity {
	
	private static int MENU = 0;
	private final static int MENU_INIT = 1;
	private final static int MENU_MAIN = 2;
	
	private static int MENU_NEW = 0;
	private final static int MENU_NEW_1 = 1;
	private final static int MENU_NEW_2 = 2;
	
	private static int MENU_CLASS = 0;
	private final static int MENU_CLASS_1 = 1;
	private final static int MENU_CLASS_2 = 2;
	private final static int MENU_CLASS_3 = 3;
	
	private final static int ty_class = 1;
	private final static int ty_book = 2;
	
	private Context context;
	private FlingGalleryView fgv_list_main;
	public static int screenIndex = 0;
	private ViewGroup[] main_tab_item = new ViewGroup[4];
	private ViewGroup[] main_view = new ViewGroup[4];
	private int[] screenitem = { R.drawable.menu01, R.drawable.menu02,
			R.drawable.menu03, R.drawable.management };
	private int[] screenitemed = { R.drawable.menu01_c, R.drawable.menu02_c,
			R.drawable.menu03_c, R.drawable.menu04_c };
	private GridView book_new,book_class,book_list;
	private BookAdapter bk_new,bk_class,bk_list;
	private View view_class;
	private FrameLayout book_content_new,book_content_class;
	
	private ImageView search;
	private ListView downlist;
	private DownAdapter dap;
	private Dialog management_dg;
	private View management_dg_content;
	
	private HttpGetData httpget;
	private JSONArray data_new,data_class,data_class_content;
	private View loading;
	private Rms rms;
	private TextView title;//标签
	private String titleing;//动态标签
	private Handler handler = null;
	public static Downloader dl;
	private boolean downing_look;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 全屏设置
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
                WindowManager.LayoutParams.FLAG_FULLSCREEN);  
        context = this;
        //有米
        AdManager.getInstance(this).init("b698cb1dcc6bb836","24a8f9eeed9edacb", false); 
        // 请务必调用以下代码，告诉SDK应用启动，可以让SDK进行一些初始化操作。
        OffersManager.getInstance(this).onAppLaunch(); 
        //万普
        AppConnect.getInstance(this);
//		AppConnect.getInstance("1674200a2858c3f72965155e039fef14", "WAPS", this);

        httpget = new HttpGetData(context);
        if(dl == null)dl = new Downloader();
        if(rms == null)rms = new Rms(context);
        downing_look = true;
        initHandler();
        handler.sendEmptyMessage(1);
        setState(MENU_INIT);
        
      
    }
    
	// 状态调整
	private void setState(int st) {
		Message msg = new Message();
		msg.what = st;
		mRefreshHandler.sendMessage(msg);
	}
	
	private RefreshHandler mRefreshHandler = new RefreshHandler();
	private class RefreshHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.what) {
			case MENU_INIT:
				 setContentView(R.layout.logo);
				 new Thread(new Runnable() {
					@Override
					public void run() {
						data_new = getDataJson(httpget.getData("recombooklist?page=0"));
					    outLog("data_new = " + data_new);
					    data_class = getDataJson(httpget.getData("categoryinfo"));
					    outLog("data_class = " + data_class);
					    try {
							Thread.sleep(1500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					    setState(MENU_MAIN);
					}
				  }).start();
			
				 break;
			case MENU_MAIN:
				 if(!ToolUtil.isNet(context)){
						Toast.makeText(context, "! 请检查网络是否正常", Toast.LENGTH_LONG).show(); 
					 }
				 setContentView(R.layout.main);
			     init();
				 break;
			default:
				 break;
			}

		}
	}
    
    
    /**
     * 菜单状态
     * @param in
     */
    private void setMenuNew(int in){
    	MENU_NEW = in;
    }
    private void setMenuClass(int in){
    	MENU_CLASS = in;
    }
    
    /**
     * 初始化
     */
    public void init(){
    	if(bk_new == null){
    		Toast.makeText(context, "列表获取失败", Toast.LENGTH_SHORT);
    	}
    	search = (ImageView)findViewById(R.id.search);
    	search.setOnClickListener(on);
    	//初始化标签
    	title = (TextView)findViewById(R.id.title);
    	
    	if(fgv_list_main == null)fgv_list_main = (FlingGalleryView) findViewById(R.id.fgv_list_main);
		fgv_list_main.setDefaultScreen(screenIndex);
		// 导航栏选项卡数组 实例化
		main_tab_item[0] = (ViewGroup) this.findViewById(R.id.list_tab_item_new);
		main_tab_item[1] = (ViewGroup) this.findViewById(R.id.list_tab_item_class);
		main_tab_item[2] = (ViewGroup) this.findViewById(R.id.list_tab_item_down);
		main_tab_item[3] = (ViewGroup) this.findViewById(R.id.list_tab_item_management);
		
		main_view[0] = (ViewGroup) this.findViewById(R.id.main_new);
		main_view[1] = (ViewGroup) this.findViewById(R.id.main_calss);
		main_view[2] = (ViewGroup) this.findViewById(R.id.main_down);
		main_view[3] = (ViewGroup) this.findViewById(R.id.main_management);
		
		initTabItem();
		book_new = (GridView)findViewById(R.id.listViewBOOK);
		bk_new = new BookAdapter(context,data_new,ty_book);
		book_new.setAdapter(bk_new);
		book_new.setOnItemClickListener(oni);
		setMenuNew(MENU_NEW_1);
		
		book_class = (GridView)findViewById(R.id.listViewCLASS);
		bk_class = new BookAdapter(context,data_class,ty_class);
		book_class.setAdapter(bk_class);
		book_class.setOnItemClickListener(oni);
		setMenuClass(MENU_CLASS_1);
		
		downlist = (ListView)findViewById(R.id.listView_down);
		dap = new DownAdapter(context);
		downlist.setAdapter(dap);
		
		loading = LayoutInflater.from(context).inflate(R.layout.loadview, null);
    }
    
	/**
	 * 初始化导航栏
	 * */
	private void initTabItem() {
		for (int i = 0; i < main_tab_item.length; i++) {
			main_tab_item[i].setOnClickListener(tabClickListener);
			main_tab_item[i].setBackgroundResource(screenitem[i]);
			if (screenIndex == i) {
				main_tab_item[i].setBackgroundResource(screenitemed[i]);
			}
		}
	}
    /**
     * 导航栏选项卡切换事件
     */
 	private OnClickListener tabClickListener = new OnClickListener() {
 		public void onClick(View v) {
 			switch (v.getId()) {
 			case R.id.list_tab_item_new:
 				if (screenIndex == 0) {
 					return;
 				}
 				main_tab_item[screenIndex]
 						.setBackgroundResource(screenitem[screenIndex]);
 				screenIndex = 0;
 				title.setText(R.string.title_new);
 				break;
 			case R.id.list_tab_item_class:
 				if (screenIndex == 1) {
 					return;
 				}
 				main_tab_item[screenIndex]
 						.setBackgroundResource(screenitem[screenIndex]);
 				screenIndex = 1;
 				if(MENU_CLASS == MENU_CLASS_1){
 					title.setText(R.string.title_calss);
 				}else{
 					title.setText(titleing);
 				}
 				
 				break;
 			case R.id.list_tab_item_down:
 				if (screenIndex == 2) {
 					return;
 				}
 				main_tab_item[screenIndex].setBackgroundResource(screenitem[screenIndex]);
 				screenIndex = 2;
 				title.setText(R.string.title_down);
 				break;
 			case R.id.list_tab_item_management:
// 				if (screenIndex == 3) {
// 					return;
// 				}
// 				main_tab_item[screenIndex].setBackgroundResource(screenitem[screenIndex]);
// 				screenIndex = 3;
 				if(management_dg != null&&management_dg.isShowing()){
 					management_dg.dismiss();
 					return;
 				}
 				management_dg = new Dialog(context, style.Theme_Translucent_NoTitleBar_Fullscreen);	
 				management_dg_content = LayoutInflater.from(context).inflate(R.layout.management_dialog, null);
 				TextView about = (TextView)management_dg_content.findViewById(R.id.management_about);
 				about.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						management_dg.dismiss();
						String[] item = {"万普","有米"};
						new AlertDialog.Builder(context)
			                .setTitle("选择广告墙")
			                .setItems(item, new DialogInterface.OnClickListener() {
			                    public void onClick(DialogInterface dialog, int which) {
			                    	if(which==0){
//			                    		AppConnect.getInstance(context).showOffers(context);
			                    		Intent intent = new Intent(context, LoadActivity.class);
			                    		startActivity(intent);
			                    	}else if(which ==1){
			                    		//调用showOffersWall显示全屏的积分墙界面
			                    		OffersManager.getInstance(context).showOffersWall();
			                    	}
			                    }
			                }).create().show();
						
					}
				});
 				
 				TextView tx = (TextView)management_dg_content.findViewById(R.id.management_out);
 				tx.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						management_dg.dismiss();
						finish();
					}
				});
 				management_dg.setContentView(management_dg_content);
				Window window = management_dg.getWindow();
				window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
				window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL|WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
						WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL|WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
				window.setGravity(Gravity.RIGHT|Gravity.BOTTOM);
				WindowManager.LayoutParams lp = window.getAttributes();
				lp.x = 0;
				lp.y = dip2px(50);
				window.setAttributes(lp);
				management_dg.getWindow().setLayout(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
				management_dg.show();
//				new Thread(new Runnable() {
//					public void run() {
//						try {
//							Thread.sleep(2000);
//							if(management_dg != null&&management_dg.isShowing()){
//								    management_dg.dismiss();
//							}
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//						}
//					}
//				}).start();
 				break;
 			}
 			main_tab_item[screenIndex].setBackgroundResource(screenitemed[screenIndex]);
 			fgv_list_main.setToScreen(screenIndex, true);
 		}
 	};
	private PullToRefreshGridView mPullRefreshGridView;
 	private int class_id;
 	private final int maxbook = 21;
 
 	/**
 	 * 控制台
 	 */
 	private OnItemClickListener oni = new OnItemClickListener(){
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			if(arg0 == book_new){
				try {
					outLog("int>>>>book_new");
					book_content_new = new BookContent(context,data_new.getJSONObject(arg2).getString("book_id"));
					main_view[0].addView(book_content_new);
					setMenuNew(MENU_NEW_2);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}else if(arg0 == book_class){
				outLog("int>>>>book_class");
				try {
					class_id = data_class.getJSONObject(arg2).getInt("ca_id");
					titleing = data_class.getJSONObject(arg2).getString("ca_name");
					title.setText(titleing);
					main_view[1].addView(loading);
					new Thread(new Runnable() {
						@Override
						public void run() {
							data_class_content = getDataJson(httpget.getData("booklist?ca_id=" + class_id + "&" + "page=0"));
						    outLog("data_class_content = " + data_class_content);
							((Activity)context).runOnUiThread(new Runnable() {
								@Override
								public void run() {
									
									view_class = LayoutInflater.from(context).inflate(R.layout.calss_content, null);
									view_class.setOnClickListener(on);
									mPullRefreshGridView = (PullToRefreshGridView) view_class.findViewById(R.id.listViewCLASS);
									book_list = mPullRefreshGridView.getRefreshableView();

									// Set a listener to be invoked when the list should be refreshed.
									mPullRefreshGridView.setOnRefreshListener(new OnRefreshListener() {
										@Override
										public void onRefresh() {
											System.out.println("onRefresh");
											int clong = data_class_content.length();
											if(clong >= maxbook && clong % maxbook == 0){
												new GetDataTask().execute();
											}else{
												mPullRefreshGridView.onRefreshComplete();
											}
										}
									});
									
//									book_list = (GridView)view_class.findViewById(R.id.listViewCLASS);
									bk_list = new BookAdapter(context,data_class_content,ty_book);
									book_list.setAdapter(bk_list);
									book_list.setOnItemClickListener(oni);
//									book_list.setOnScrollListener(l)
									main_view[1].removeView(loading);
									main_view[1].addView(view_class);
									setMenuClass(MENU_CLASS_2);
								}
							});
						}
					}).start();
			
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else if(arg0 == book_list){
				try {
					outLog("int>>>>book_list");
					System.out.println("book_id = " + arg2);
					book_content_class = new BookContent(context,data_class_content.getJSONObject(arg2).getString("book_id"));
					main_view[1].addView(book_content_class);
					setMenuClass(MENU_CLASS_3);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	};
 	
	Dialog dialog;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		if(keyCode == KeyEvent.KEYCODE_BACK){
			if(screenIndex == 0){
				if(MENU_NEW == MENU_NEW_1){
				}else{
					setMenuNew(MENU_NEW_1);
					if(main_view[0] != null)
					main_view[0].removeView(book_content_new);
					return true;
				}
			}else if(screenIndex == 1){
				if(MENU_CLASS == MENU_CLASS_2){
					setMenuClass(MENU_CLASS_1);
					if(main_view[1] != null)
					main_view[1].removeView(view_class);
					title.setText(R.string.title_calss);
					return true;
				}else if(MENU_CLASS == MENU_CLASS_3){
					setMenuClass(MENU_CLASS_2);
					if(main_view[1] != null)
					main_view[1].removeView(book_content_class);
					return true;
				}
			}
		}
	
		if(dialog != null && dialog.isShowing()){
			return true;
		}
		dialog = new AlertDialog.Builder(this).setMessage(R.string.app_names)
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
		
		return true;
		
	}

	public static void outLog(String str){
//		Log.e("MoDaoKanTu", str);
	}
	
	private OnClickListener on = new  OnClickListener() {
		@Override
		public void onClick(View v) {
		 if(search == v){
				Intent intent = new Intent();
				intent.setClass(context, SearchActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
			}
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(management_dg != null&&management_dg.isShowing()){
			management_dg.dismiss();
		}
		downing_look = false;
		AppConnect.getInstance(this).finalize();
		// 请务必在应用退出的时候调用以下代码，告诉SDK应用已经关闭，可以让SDK进行一些资源的释放和清理。
        OffersManager.getInstance(this).onAppExit(); 

	}
	
	//像素转换
	private int dip2px(float dipValue){   
        final float scale = context.getResources().getDisplayMetrics().density;   
        return (int)(dipValue * scale + 0.5f);   
    } 
	
	private JSONArray getDataJson(String data){
		if(data != null){
			try {
				JSONObject json = new JSONObject(data);
				if("104".equals(json.getString("rescode"))){
					JSONArray js = json.getJSONObject("data").getJSONArray("data");
					return js;
				}else{
					outLog("resoed === " + json.getString("resoed"));
				}
			 } catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	private void initHandler(){
		if (handler==null){
			handler = new Handler(){
				@Override
				public void handleMessage(Message msg) {
					super.handleMessage(msg);
					if (msg.what==1){
						if(handler != null){
							if(dap != null){
								dap.toUpdata();
							}
							if(downing_look)handler.sendEmptyMessageDelayed(1, 2000);
						}
					}
				}
			};
		}
	}
	private int page = 0;
	private class GetDataTask extends AsyncTask<Void, Void, String[]> {

		@Override
		protected String[] doInBackground(Void... params) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			return null;
		}

		@Override
		protected void onPostExecute(String[] result) {
			int clong = data_class_content.length();
			if(clong >= maxbook && clong % maxbook == 0){
				JSONArray content = getDataJson(httpget.getData("booklist?ca_id=" + class_id + "&" + "page=" + (clong/maxbook+1) ));
				data_class_content = bk_list.upData(content);
			}
			mPullRefreshGridView.onRefreshComplete();
			super.onPostExecute(result);
		}
	}
	
 
}