package com.fbty.gdsms.activity;

import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;

import com.fbty.base.activity.BaseActivity;
import com.fbty.base.adapter.TreeAdapter;
import com.fbty.gdsms.R;
import com.fbty.gdsms.domain.Node;
import com.fbty.gdsms.service.RemoteService;

public class UpMessageConditionActivity extends BaseActivity {
	private EditText down_person_name;
	private EditText down_start_date;
	private EditText down_end_date;
	private ListView conditionTree;
	TreeAdapter tad;
	@Override
	protected void onCreate(Bundle paramBundle) {
		setContentView(R.layout.message_down_condition);
		super.onCreate(paramBundle);
		//开始时间组件
		final Calendar cd = Calendar.getInstance();
		down_end_date.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				new DatePickerDialog(UpMessageConditionActivity.this,
						new OnDateSetListener() {
							public void onDateSet(DatePicker view, int year,
									int monthOfYear, int dayOfMonth) {
								down_end_date.setText(year + "-" + (monthOfYear + 1) + "-"
										+ dayOfMonth);
								Calendar c = Calendar.getInstance();
								c.set(year, monthOfYear, dayOfMonth);
								down_end_date.setTag(c.getTime().getTime());
							}
						}, cd.get(Calendar.YEAR), cd.get(Calendar.MONTH), cd
								.get(Calendar.DAY_OF_MONTH)).show();
			}
		});
		//结束时间组件
		down_start_date.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				new DatePickerDialog(UpMessageConditionActivity.this,
						new OnDateSetListener() {
							public void onDateSet(DatePicker view, int year,
									int monthOfYear, int dayOfMonth) {
								down_start_date.setText(year + "-" + (monthOfYear + 1) + "-"
										+ dayOfMonth);
								Calendar c = Calendar.getInstance();
								c.set(year, monthOfYear, dayOfMonth);
								down_start_date.setTag(c.getTime().getTime());
							}
						}, cd.get(Calendar.YEAR), cd.get(Calendar.MONTH), cd
								.get(Calendar.DAY_OF_MONTH)).show();
			}
		});
		
	}
	@Override                                                                                                                                                                  
	protected void onPause() {
		//当前条件下，上传记录请求参数
		Map<String,String> params = new HashMap<String,String>();
		params.put("menuid", tad.getSelectItemId());
		params.put("name", down_person_name.getText().toString());
		params.put("startTime", down_start_date.getTag()==null?"":down_start_date.getTag().toString());
		params.put("endTime", down_end_date.getTag()==null?"":down_end_date.getTag().toString());
		setAttribute("params", params);
		super.onPause();
	}
	/**
	 * @throws Exception 
	 * @see com.fbty.base.activity.BaseActivity#initAsyncData()
	 */
	@Override
	protected Object initAsyncData() throws Exception {
		RemoteService rs = new RemoteService();
		LinkedList<Node> nodelist = null ;
		Object obj = getAttribute("nodeList");
		if(obj == null){
			nodelist = rs.queryRelations();
			setAttribute("nodeList", nodelist);
		}else{
			nodelist = (LinkedList<Node>)obj;
		}
		tad = new TreeAdapter(this, R.layout.outline, nodelist,TreeAdapter.SINGLE);
		return null;
	}
	/**
	 * @see com.fbty.base.activity.BaseActivity#refreshUI(java.lang.Object)
	 */
	@Override
	protected void refreshUI(Object result) {
		super.refreshUI(result);
		conditionTree.setAdapter(tad);
	}
	
	
	

}
