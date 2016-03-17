package com.ludashi.mains;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ludashi.adapter.BookAdapter;
import com.ludashi.tool.HttpGetData;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.Toast;

public class SearchActivity extends Activity{

	private EditText text;
	private Button search;
	private FrameLayout main;
	private JSONArray content_search;
	private HttpGetData httpget;
	private Context context;
	private View loading;
	private static int MENU = 0;
	private final static int MENU_1 = 1;
	private final static int MENU_2 = 2;
	private View view_search, book_content_search;
	private GridView book_list;
	private BookAdapter bk_list;
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);// 全屏设置
	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
	                WindowManager.LayoutParams.FLAG_FULLSCREEN); 
	    context = this;
	    
		setContentView(R.layout.search);
		httpget = new HttpGetData(context);
		text = (EditText)findViewById(R.id.editText_search);
		search = (Button)findViewById(R.id.button_search);
		search.setOnClickListener(on);
		main = (FrameLayout)findViewById(R.id.frameLayout_search);
		loading = LayoutInflater.from(context).inflate(R.layout.loadview, null);
		
	}
	
	  private void setMenu(int in){
		  MENU = in;
	    }
	
	private OnClickListener on = new  OnClickListener() {
		@Override
		public void onClick(View v) {
			if(search == v){
				if(text.getText().length() == 0){
					Toast.makeText(context, "搜索内容不能为空", Toast.LENGTH_SHORT).show();
					return;
				}

				LuDaShiActivity.outLog("int>>>>Search");
				try {
					new Thread(new Runnable() {
						@Override
						public void run() {
							content_search = getDataJson(httpget.getData("booksearch?page=0&book_name="+ text.getText()));
							LuDaShiActivity.outLog("content_search = " + content_search);
							((Activity)context).runOnUiThread(new Runnable() {
								@Override
								public void run() {
									view_search = LayoutInflater.from(context).inflate(R.layout.main_calss, null);
									book_list = (GridView)view_search.findViewById(R.id.listViewCLASS);
									bk_list = new BookAdapter(context,content_search,2);
									book_list.setAdapter(bk_list);
									book_list.setOnItemClickListener(oni);
									main.removeView(loading);
									main.addView(view_search);
									setMenu(MENU_1);
								}
							});
						}
					}).start();
			
				} catch (Exception e) {
					e.printStackTrace();
				}
			
				
			}
		}
	};
	
	
	private OnItemClickListener oni = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			if(book_list == arg0){
				try {
					LuDaShiActivity.outLog("int>>>>book_list");
					book_content_search = new BookContent(context,content_search.getJSONObject(arg2).getString("book_id"));
					main.addView(book_content_search);
					setMenu(MENU_2);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	};
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if(keyCode == KeyEvent.KEYCODE_BACK){
			if(MENU == MENU_2){
				setMenu(MENU_1);
				if(main != null)
				main.removeView(book_content_search);
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	
	private JSONArray getDataJson(String data){
		if(data != null){
			try {
				JSONObject json = new JSONObject(data);
				if("104".equals(json.getString("rescode"))){
					JSONArray js = json.getJSONObject("data").getJSONArray("data");
					return js;
				}else{
					LuDaShiActivity.outLog("resoed === " + json.getString("resoed"));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	
	
}
