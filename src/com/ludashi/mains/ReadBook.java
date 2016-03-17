package com.ludashi.mains;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.util.EncodingUtils;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.Layout;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;
import android.text.*;

public class ReadBook extends Activity implements OnClickListener{
	
	final int REQUST_CODE_GOTO_BOOKMARK = 1;
	
	CustomTxtView tvMain;			// 主看书控件，自定义
	String strSelection = "";		// 用户选择的字符串
	String strTxt = "";				// 用于显示的文本字符串
	String strPath = "";			// 完整的文件路径
	int position = 0;				// 当前阅读位置，取一行的行首
	int markPos = 0;				// 书签位置
	
	final int BUFFER_SIZE = 1024 * 3;		// 没时间了，暂时先不做大文件处理了-_-||
	final int SCROLL_STEP = 2;				// 自动滚动的步长
	final int BEGIN_SCROLL = 1; 			// 开始滚屏
	final int END_SCROLL = 2;				// 终止滚屏
	final int STOP_SCROLL = 3;				// 结束滚屏
	
	final int MENU_BOOKMARK = Menu.FIRST;
	final int MENU_SEARCH = Menu.FIRST+1;
	
	final int DIALOG_AFTER_SELECTION = 4;
	final int DIALOG_GET_SEARCH_KEY_WORD = 5;
	
	boolean isAutoScrolling = false;
	boolean isInSearching = false;
	boolean hasBookMark = false;
    
    private ProgressDialog pdg;
    private TextView tx;
	private DecimalFormat fnum;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 全屏设置
		
		setContentView(R.layout.look_book);
		tvMain = (CustomTxtView)this.findViewById(R.id.viewtxt_main_view);	
		tx = (TextView)findViewById(R.id.textView_book);
		if(fnum == null)fnum = new DecimalFormat("##0.00");
		// 设置一个触摸事件侦听器，拦截选择结束事件
		CustomTxtView.OnTouchListener viewTouch = new TextView.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
//				if (tvMain.getSelectionEnd() - tvMain.getSelectionStart() != 0
//						&& tvMain.isInSelectMode()
//						&& event.getAction() == MotionEvent.ACTION_UP) {
//					
//					char[] bufTmp = new char[128] ;	// 需做文字长度是否超出短信范围判断
//					try {
//						tvMain.getText().getChars(tvMain.getSelectionStart(), 
//								tvMain.getSelectionEnd(), bufTmp, 0);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//					//strSelection = String.copyValueOf(bufTmp);
//					strSelection = String.copyValueOf(bufTmp, 0, tvMain.getSelectionEnd() - tvMain.getSelectionStart());
//					showDialog(DIALOG_AFTER_SELECTION);
//				}
				upPage();
		        return false;   
		    }   
		};
		pdg = new ProgressDialog(this);  
		pdg.setMessage("正在读取请等待");  
		pdg.setIndeterminate(true);
		pdg.show(); 
		tvMain.setOnTouchListener(viewTouch);
		tvMain.setBackgroundResource(R.drawable.wenzhangbg);
		tvMain.setTextColor(Color.BLACK);
		tvMain.setTextSize(dip2px(12));
		tvMain.setCursorVisible(false);
		tvMain.setMovementMethod(ScrollingMovementMethod.getInstance());
		//亮度
//		WindowManager.LayoutParams lp = getWindow().getAttributes();  
//		lp.screenBrightness = scrBrightness;  
//		getWindow().setAttributes(lp); 
		
		new Thread(new Runnable() {
			@Override
			public void run() {
			       try {
//			           Bundle b = getIntent().getExtras();
//			           String str = b.getString("FILE_PATH");  
//			           strPath = str;	// 保存一份副本
			    	   
//			    	   strTxt = openFile(getIntent().getExtras().getString("file_name"));
			    	   strTxt = openbook(getIntent().getExtras().getString("file_name"));
//			      	   MoDaoKanTuActivity.outLog("strTxt = " + strTxt.length());
			    	   ReadBook.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							tvMain.setText(strTxt);
							pdg.dismiss();
						}
					 });
			        } catch (Exception e) {

			        }
			}
		}).start();
		// 读文件
 
        
        // 设置按钮事件监听器
        Button BtnPrePage = (Button)this.findViewById(R.id.viewtxt_pre_button);
        BtnPrePage.setOnClickListener(this);
        Button BtnNextPage = (Button)this.findViewById(R.id.viewtxt_next_button);
        BtnNextPage.setOnClickListener(this);
	}

	// 重新回到看书界面
	@Override
	public void onResume() {
		super.onResume();
		Layout l = tvMain.getLayout();
		if (null != l) {
			// 回到上次观看的位置
			if (hasBookMark) {
				hasBookMark = false;
				position = markPos;
			} else {
				int line = l.getLineForOffset(position);
				float sy = l.getLineBottom(line);
				tvMain.scrollTo(0, (int) sy);
			}	
			Log.e("REALPOS_RESUME", "REAL_POS " + position);
		}
	}
	
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.viewtxt_pre_button:
			if (tvMain.getScrollY() <= tvMain.getHeight())
				tvMain.scrollTo(0, 0);
			else
				tvMain.scrollTo(0, tvMain.getScrollY() - tvMain.getHeight());
			Log.e("", "LINEHEIGHT = "+tvMain.getLineHeight());
			upPage();
			break;
		case R.id.viewtxt_next_button:
			if (tvMain.getScrollY() >= tvMain.getLineCount() * tvMain.getLineHeight() - tvMain.getHeight()*2)
				tvMain.scrollTo(0, tvMain.getLineCount() * tvMain.getLineHeight() - tvMain.getHeight());
			else
				tvMain.scrollTo(0, tvMain.getScrollY() + tvMain.getHeight());
				
			Log.e("", "LINECOUNT*LINEHEIGHT = "+(tvMain.getLineCount()*tvMain.getLineHeight()-tvMain.getHeight()));
			Log.e("", "SCROLLY = "+tvMain.getScrollY());
			Log.e("", "TVHEIGHT = "+tvMain.getSelectionEnd());
			upPage();
			break;

		default:
			break;
		}
	}
	
	private void upPage(){
		float page = ((float)tvMain.getScrollY())/((float)(tvMain.getLineCount()*tvMain.getLineHeight()-tvMain.getHeight()));
		tx.setText("" + fnum.format(page*100) + " %");
	}
	
	// 主菜单
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_BOOKMARK, 0, "书签");
//		menu.add(0, MENU_SEARCH, 0, "搜索");
		
		return true;
	}
	
	// 主菜单点击事件
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case MENU_BOOKMARK:
			// 去往书签管理Activity
			Intent i = new Intent(this, BookMarkActivity.class);
			Bundle b = new Bundle();
			b.putString("BOOKNAME", strPath);
			Layout l = tvMain.getLayout();
			int line = l.getLineForVertical(tvMain.getScrollY());
			int off = l.getOffsetForHorizontal(line, 0);
			position = off;
			b.putInt("POSITION", position);
			Log.e("REALPOS_BEFORE_GO", "REAL_POS " + position);
            i.putExtras(b);
            startActivityForResult(i, REQUST_CODE_GOTO_BOOKMARK);
			break;
			
		case MENU_SEARCH:
			if (isInSearching) {
				tvMain.setText(strTxt);
				isInSearching = false;
			} else {
//				searchDlg.setDisplay();
			}
			break;
		default:
			break;
		}
		return false;
	}
	
	@Override
    protected Dialog onCreateDialog (int id) {
        switch (id) {
    	// 短信电话对话框
        case DIALOG_AFTER_SELECTION:
            return new AlertDialog.Builder(ReadBook.this)
                .setIcon(android.R.drawable.ic_dialog_info)
                //.setTitle("欢迎")
                .setMessage("您想用选定的文本：")
                .setPositiveButton("发送短信", new android.content.DialogInterface.OnClickListener() {
                	public void onClick(DialogInterface a0, int a1) {
                		Uri smsToUri = Uri.parse("smsto://");
                	    Intent mIntent = new Intent(Intent.ACTION_SENDTO, smsToUri);
                	    // 复制到剪贴板，然后调用系统的短信程序
                	    ClipboardManager clipboard = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
                	    clipboard.setText(strSelection);
                	    startActivity(mIntent);
                        Toast.makeText(ReadBook.this, "短信内容已复制到剪贴板", 
                                Toast.LENGTH_LONG).show();
//                		SmsManager manager = SmsManager.getDefault();
//                		   manager.sendTextMessage("10086",null,"hi,this is sms",null,null);
                	    tvMain.clearSelection();
                	}
                }).setNeutralButton("拨打电话", new android.content.DialogInterface.OnClickListener() {
                	public void onClick(DialogInterface a0, int a1) {
                		// 是否合法号码？
						if (PhoneNumberUtils.isGlobalPhoneNumber(strSelection)){ 
							Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("tel://" + strSelection));
							startActivity(i);
						} else {
							Toast.makeText(ReadBook.this, "非法的电话号码",
				                    Toast.LENGTH_LONG).show();
						}
						Log.e("", "NUM = " + strSelection);
						tvMain.clearSelection();
                	}
                }).setNegativeButton("取消选择", new android.content.DialogInterface.OnClickListener() {
                	public void onClick(DialogInterface a0, int a1) {
                		tvMain.clearSelection();
                	}
                })
                .create();
            
        default:
            return null;
        }
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case RESULT_OK:
			if (requestCode == REQUST_CODE_GOTO_BOOKMARK) {
	        	Bundle b = data.getExtras();
	            int mark = b.getInt("POSITION");  
	    		Layout l = tvMain.getLayout();
	    		if (null != l) {
	    			// 去往书签位置
	    			int line = l.getLineForOffset(mark);
	    			float sy = l.getLineBottom(line);
	    			tvMain.scrollTo(0, (int) sy);
	    			markPos = mark;
	    			hasBookMark = true;
	    			Log.e("REALPOS_RES", "REAL_POS " + mark);
	    		}
			}
			upPage();
			break;
		}
	}
	
	public String openFile(String fileName){
		try {
			File file = new File(fileName);
			FileInputStream in = new FileInputStream(file);
			BufferedInputStream buff = new BufferedInputStream(in);
			int length = (int)file.length();
			byte[] temp = new byte[length];
			byte[] first3bytes = new byte[3];
			
			buff.read(temp, 0, length);
            first3bytes[0] = temp[0];
            first3bytes[1] = temp[1];
            first3bytes[2] = temp[2];
            
            in.close();
            buff.close();
            
            if (first3bytes[0] == (byte) 0xEF && first3bytes[1] == (byte) 0xBB&& first3bytes[2] == (byte) 0xBF) {
            	  return EncodingUtils.getString(temp, "utf-8");
               } else if (first3bytes[0] == (byte) 0xFF && first3bytes[1] == (byte) 0xFE) {
            	   return EncodingUtils.getString(temp, "unicode");
               } else if (first3bytes[0] == (byte) 0xFE && first3bytes[1] == (byte) 0xFF) {
            	   return EncodingUtils.getString(temp, "utf-16be");
               } else if (first3bytes[0] == (byte) 0xFF && first3bytes[1] == (byte) 0xFF) {
            	   return EncodingUtils.getString(temp, "utf-16le");
               } else {
            	   return EncodingUtils.getString(temp, "GBK");
             }
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
    public String openbook(String strFilePath)  {
		try {
			File file = new File(strFilePath);
			long lLen = file.length();
			int i = (int) lLen;
			
				MappedByteBuffer mbb= new RandomAccessFile(file, "r").getChannel().map(FileChannel.MapMode.READ_ONLY, 0, lLen);
				byte[]buf=new byte[i];
				for(int j =0; j < lLen; j++) 
				{
					  if(j>=0&&j<=lLen)
					  {
						   buf[j]=mbb.get(j); 
					  }
		 
				}
				byte[] first3bytes = new byte[3];
	            first3bytes[0] = buf[0];
	            first3bytes[1] = buf[1];
	            first3bytes[2] = buf[2];
	            
		      if (first3bytes[0] == (byte) 0xEF && first3bytes[1] == (byte) 0xBB&& first3bytes[2] == (byte) 0xBF) {
            	   return EncodingUtils.getString(buf, "utf-8");
               } else if (first3bytes[0] == (byte) 0xFF && first3bytes[1] == (byte) 0xFE) {
            	   return EncodingUtils.getString(buf, "unicode");
               } else if (first3bytes[0] == (byte) 0xFE && first3bytes[1] == (byte) 0xFF) {
            	   return EncodingUtils.getString(buf, "utf-16be");
               } else if (first3bytes[0] == (byte) 0xFF && first3bytes[1] == (byte) 0xFF) {
            	   return EncodingUtils.getString(buf, "utf-16le");
               } else {
            	   return EncodingUtils.getString(buf, "GBK");
             }     
					  	       
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}  
		return null;
   }   
    
	//像素转换
	private int dip2px(float dipValue){   
        final float scale = getResources().getDisplayMetrics().density;   
        return (int)(dipValue * scale + 0.5f);   
    }

	Dialog dialog;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			if(dialog != null && dialog.isShowing()){
				return true;
			}
			dialog = new AlertDialog.Builder(this).setTitle("是否退出阅读").setMessage("小贴士:按菜单键添加书签喔")
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
		if(keyCode == KeyEvent.KEYCODE_MENU){
			return super.onKeyDown(keyCode, event);
		}
			
		return true;
	} 
	
	
	/**
     * 去除特殊字符或将所有中文标号替换为英文标号
     * 
     * @param str
     * @return
*/
    private String stringFilter(String str) {
        str = str.replaceAll("【", "[").replaceAll("】", "]")
                .replaceAll("！", "!").replaceAll("：", ":");// 替换中文标号
        String regEx = "[『』]"; // 清除掉特殊字符
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }
	
    public static String ToDBC(String input) {
    	   char[] c = input.toCharArray();
    	   for (int i = 0; i< c.length; i++) {
    	       if (c[i] == 12288) {
    	         c[i] = (char) 32;
    	         continue;
    	       }if (c[i]> 65280&& c[i]< 65375)
    	          c[i] = (char) (c[i] - 65248);
    	       }
    	   return new String(c);
    	}
	
	
}