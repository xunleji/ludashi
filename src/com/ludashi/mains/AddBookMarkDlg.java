package com.ludashi.mains;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

class AddBookMarkDlg extends Dialog implements android.view.View.OnClickListener{
	 public AddBookMarkDlg(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	Button btnConfirm;
	Button btnCancel;
	EditText editMarkName;
	String markName = "";
	
	public void setDisplay(){
		setContentView(R.layout.addbookmarkdlg);	// 设置对话框的布局
		btnConfirm = (Button)findViewById(R.id.addbookmark_dlg_btn_confirm);
		btnConfirm.setOnClickListener(this);
		btnCancel = (Button)findViewById(R.id.addbookmark_dlg_btn_cancel);
		btnCancel.setOnClickListener(this);
		editMarkName = (EditText)findViewById(R.id.addbookmark_dlg_edit_markname);
	    setProperty(); 
	    setTitle("书签名");							// 设定对话框的标题
	    show();										// 显示对话框   
	    }
	    
	public void setProperty(){
		Window window = getWindow();					// 得到对话框的窗口
	    WindowManager.LayoutParams wl = window.getAttributes();
//	    wl.x = 0;							
//	    wl.y = 0;
//	    wl.width = 240;
//	    wl.height = 180;
	    wl.alpha = 0.9f;
	    wl.gravity = Gravity.CENTER;         
	    window.setAttributes(wl); 
	    }

	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.addbookmark_dlg_btn_confirm:
			markName = editMarkName.getText().toString();
			dismiss();
			break;
		case R.id.addbookmark_dlg_btn_cancel:
			markName = "书签1";
			dismiss();
			break;
		}
	}
	
	public String getMarkName() {
		return markName;
	}
}