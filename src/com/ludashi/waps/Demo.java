package com.ludashi.waps;

import cn.waps.AppConnect;
import cn.waps.UpdatePointsNotifier;
import com.ludashi.mains.R;
import com.ludashi.tool.Diary;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Demo extends Activity implements OnClickListener,UpdatePointsNotifier{
	
	private Button btn1,btn2,btn3,btn4,btn5,btn6;
	private Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.demo);
		context = this;
		//初始化统计器，并通过代码设置WAPS_ID, WAPS_PID
		AppConnect.getInstance("1674200a2858c3f72965155e039fef14", "WAPS", this);
		
		btn1 = (Button)findViewById(R.id.demobtn1);
		btn1.setOnClickListener(this);
		btn2 = (Button)findViewById(R.id.demobtn2);
		btn2.setOnClickListener(this);
		btn3 = (Button)findViewById(R.id.demobtn3);
		btn3.setOnClickListener(this);
		btn4 = (Button)findViewById(R.id.demobtn4);
		btn4.setOnClickListener(this);
		btn5 = (Button)findViewById(R.id.demobtn3);
		btn5.setOnClickListener(this);
		btn6 = (Button)findViewById(R.id.demobtn4);
		btn6.setOnClickListener(this);
		
	}
	@Override
	public void onClick(View v) {
		if(v==btn1){
			//获取货币
			AppConnect.getInstance(this).getPoints(this);
		}else if(v==btn2){
			//消费货币
			AppConnect.getInstance(this).spendPoints(1, this);
		}else if(v==btn3){
			//奖励货币
			AppConnect.getInstance(this).awardPoints(5, this);
		}else if(v==btn4){
			//推荐软件列表
			AppConnect.getInstance(this).showAppOffers(context);
		}else if(v==btn5){
			//推荐游戏列表
			AppConnect.getInstance(this).showGameOffers(context);
		}else if(v==btn6){
			//推荐列表
			AppConnect.getInstance(this).showOffers(context);
		}
		
	}
	@Override
	public void getUpdatePoints(String arg0, int arg1) {
		Diary.eLog("arg0="+arg0+"arg1="+arg1);
	}
	@Override
	public void getUpdatePointsFailed(String arg0) {
		Diary.eLog("arg0="+arg0);
	}
	@Override
	protected void onDestroy() {
		AppConnect.getInstance(context).finalize();
		super.onDestroy();
	}

}
