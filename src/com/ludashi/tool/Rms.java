package com.ludashi.tool;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Rms {

	private String record = "modaokantu";
	private Context context;
	
	public Rms(Context context){
		this.context = context;
	}
	
	//下载数据记录
	private String loadDown() {
		SharedPreferences data = context.getSharedPreferences(record,
				Context.MODE_WORLD_READABLE);
		return data.getString("down", null);
    }
	
	public boolean inquiryDown(JSONObject json){
		if(loadDown()==null){
			return false;
		}else{
			try {
				JSONArray jsons = new JSONArray(loadDown());
				for (int i = 0; i < jsons.length(); i++) {
					if(jsons.getJSONObject(i).getString("book_id").equals(json.getString("book_id"))){
						return true;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public JSONObject getDown(String pk){
		if(loadDown()==null){
			return null;
		}else{
			try {
				JSONArray jsons = new JSONArray(loadDown());
				for (int i = 0; i < jsons.length(); i++) {
					if(jsons.getJSONObject(i).getString("book_id").equals(pk)){
						return jsons.getJSONObject(i);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public JSONArray getDownJson(){
		if(loadDown()==null){
			return null;
		}else{
			try {
				JSONArray jsons = new JSONArray(loadDown());
				return jsons;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	
	
	/**
	 * 记录Down
	 * @param str
	 */
	public void saveDown(JSONObject str) {
		if(inquiryDown(str))return;
		if(loadDown()==null){
			JSONArray json = new JSONArray();
			json.put(str);
			sDown(json.toString());
		}else{
			try {
				JSONArray json = new JSONArray(loadDown());
				json.put(str);
				sDown(json.toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 移除Down
	 * @param json
	 * @return
	 */
	public boolean deleteDown(JSONObject json){
		if(loadDown()==null){
			return false;
		}else{
			try {
				JSONArray jsons = new JSONArray(loadDown());
				JSONArray njson = new JSONArray();
				for (int i = 0; i < jsons.length(); i++) {
					if(jsons.getJSONObject(i).getString("book_id").equals(json.getString("book_id"))){
					}else{
						njson.put(jsons.getJSONObject(i));
					}
				}
				sDown(njson.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}
    private void sDown(String str) {
		Editor data = context.getSharedPreferences(record,
				Context.MODE_WORLD_READABLE).edit();
		data.remove("down");
		data.putString("down", str);
		data.commit();
    }
    
	
}
