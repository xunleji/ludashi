package com.ludashi.mains;
import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

public class BookMarkActivity extends Activity{
	
	Button btnAdd;
	Button btnClear;
	Button btnGo;
	
	AddBookMarkDlg addBookMarkDlg;
	OnClickListener oclClick;
	
	// 数据库相关
    public static final String DB_NAME = "bm1.db";
    public static final int VERSION = 1;
    
    BookMarkDBHelper dbHelper;
    SQLiteDatabase db;
    
    Cursor c;					// 查询结果的游标
    Spinner s;					// 显示数据库的控件
    
    // 书签
    String bookName = "";		// 书名
    int position = 0;			// 从书里传来的位置
    int markGo = 0;				// 书签位置
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bookmark);
    	
        // 取得控件
        btnAdd = (Button)this.findViewById(R.id.bookmark_btn_add);
        btnClear = (Button)this.findViewById(R.id.bookmark_btn_clear);
        btnGo = (Button)this.findViewById(R.id.bookmark_btn_go);
        s = (Spinner)findViewById(R.id.bookmark_spinner_list);
        
        // 从Bundle中取出书名和位置
        Bundle b = getIntent().getExtras();
        bookName = b.getString("BOOKNAME");
        position = b.getInt("POSITION");
        
        /// 创建数据库
        dbHelper = new BookMarkDBHelper(this, DB_NAME, null, VERSION);
        db = dbHelper.getWritableDatabase();  
        updateSpinner();
        
		// 注册一个添加书签对话框及其返回事件
        addBookMarkDlg = new AddBookMarkDlg(this);
        addBookMarkDlg.setOnDismissListener(new OnDismissListener() {

			public void onDismiss(DialogInterface dialog) {
				// TODO Auto-generated method stub
				String markName = addBookMarkDlg.getMarkName();
				if (markName.length() > 0) {
					// 书签名非空，加入数据库
			        ContentValues values = new ContentValues();
			        values.put(BookMarkDBHelper.POSITION, position);
			        values.put(BookMarkDBHelper.MARK_NAME, markName);
			        values.put(BookMarkDBHelper.BOOK_NAME, bookName);
			        db.insert(BookMarkDBHelper.TB_NAME,BookMarkDBHelper.POSITION, values);
		        
				} else {
					// 默认为”未命名“书签，加入数据库
			        ContentValues values = new ContentValues();
			        values.put(BookMarkDBHelper.POSITION, position);
			        values.put(BookMarkDBHelper.MARK_NAME, "unknown");
			        values.put(BookMarkDBHelper.BOOK_NAME, bookName);
			        db.insert(BookMarkDBHelper.TB_NAME,BookMarkDBHelper.POSITION, values);
				}
			    
				updateSpinner();
			}});
		
        
    	// 实现点击事件的监听接口
    	oclClick = new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				switch (v.getId()) {
				// 添加书签
				case R.id.bookmark_btn_add: {
					addBookMarkDlg.setDisplay();
				   }
					break;
					
				// 清空书签	
				case R.id.bookmark_btn_clear:
					db.delete(BookMarkDBHelper.TB_NAME,null,null);
					updateSpinner();
					break;
					
				// 去往书签	
				case R.id.bookmark_btn_go:
					Bundle bundle = new Bundle();
					bundle.putInt("POSITION", markGo);
					Intent mIntent = new Intent();
					mIntent.putExtras(bundle);
					setResult(RESULT_OK, mIntent);
					BookMarkActivity.this.finish();
					break;
				}
			}
    		
    	};
    	
    	// 事件监听器和按钮绑定
    	btnAdd.setOnClickListener(oclClick);
    	btnClear.setOnClickListener(oclClick);
    	btnGo.setOnClickListener(oclClick);
    }
    
	// 重新回到书签界面，处理用户换书的情况
	@Override
	public void onResume() {
		super.onResume();
		// 更新书签信息
        Bundle b = getIntent().getExtras();
        bookName = b.getString("BOOKNAME");
        position = b.getInt("POSITION");
	}
	
	// 程序退出时释放数据库
	@Override
	public void onDestroy() {
		super.onDestroy();
		db.close();
	}
	
	// 更新Spinner控件数据
	public void updateSpinner() {
		String sql = "SELECT * FROM "
				   + "'" + BookMarkDBHelper.TB_NAME + "'"
				   + " WHERE "
				   + BookMarkDBHelper.BOOK_NAME
				   + "=" + "'" + bookName + "'";

		c = db.rawQuery(sql, null);
        
        // 用游标查询
        final int markIdx = c.getColumnIndexOrThrow(BookMarkDBHelper.POSITION);
        
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(BookMarkActivity.this,
                android.R.layout.simple_spinner_item, 
                c,
                new String[] {BookMarkDBHelper.MARK_NAME}, 
                new int[] {android.R.id.text1});
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        
        s.setAdapter(adapter);
        s.setOnItemSelectedListener(new OnItemSelectedListener(){

            public void onItemSelected(AdapterView<?> adapter,View v,
                    int pos, long id) {
            	
                c.moveToPosition(pos);
                markGo = c.getInt(markIdx);
                Log.i("", "markGO = " + markGo);
            }
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
	}
}