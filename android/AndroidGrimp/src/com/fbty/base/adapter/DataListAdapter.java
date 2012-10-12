/**
 * Copyright 2008-2010. 重庆富邦科技发展有限公司, Inc. All rights reserved.
 * <a>http://www.3gcq.cn</a>
 */
package com.fbty.base.adapter;

import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fbty.androidgrimp.R;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Company:重庆富邦科技发展有限责任公司
 * </p>
 * 
 * @author lvbingfeng create 2011-1-24
 * @version 0.1
 * 
 */
public class DataListAdapter extends BaseAdapter {
	private Context context;
	private List<Map<String, Object>> list;
	private int resource;
	private String[] from;
	private int[] to;
	private LayoutInflater layoutInflater;

	public DataListAdapter(Context context, List<Map<String, Object>> list,
			int resource, String[] from, int[] to) {
		this.context = context;
		this.list = list;
		this.resource = resource;
		this.from = from;
		this.to = to;
		this.layoutInflater = LayoutInflater.from(context);
	}

	/**
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		
		return list.size();
	}

	/**
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {

		return list.get(position);
	}

	/**
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {

		return position;
	}

	/**
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 *      android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		MyHolder holder = new MyHolder();
		if (convertView != null) {
			holder = (MyHolder) convertView.getTag();
		} else {
			convertView = layoutInflater.inflate(R.layout.dcpm_data_item, null);
			holder.id = (TextView) convertView.findViewById(to[0]);
			holder.name = (TextView) convertView.findViewById(to[1]);
			holder.category = (TextView) convertView.findViewById(to[2]);
			holder.time = (TextView) convertView.findViewById(to[3]);
			holder.struct = (TextView) convertView.findViewById(to[4]);
			holder.del_button = (Button) convertView.findViewById(R.id.delBtn);
			holder.update_button = (Button) convertView
					.findViewById(R.id.updateBtn);
			holder.submit_button = (Button) convertView
					.findViewById(R.id.submitBtn);
			convertView.setTag(holder);
		}
		Map<String, Object> map = list.get(position);
		if (map != null) {

			Log.i("dddd", (holder == null) + "");
			holder.id.setText(map.get(from[0]) + "");
			holder.name.setText(map.get(from[1]) + "");
			holder.category.setText(map.get(from[2]) + "");
			holder.time.setText(map.get(from[3]) + "");
			holder.struct.setText(map.get(from[4]) + "");
			holder.del_button
					.setOnClickListener(new MyDelBtnListener(position));
			holder.update_button.setOnClickListener(new MyUpdateBtnListener(
					position));
			holder.submit_button.setOnClickListener(new MyInfoBtnListener(
					position));
		}
		return convertView;
	}

	private class MyHolder {
		public TextView id;
		public TextView name;
		public TextView category;
		public TextView time;
		public TextView struct;
		public Button del_button;
		public Button update_button;
		public Button submit_button;
	}

	class MyDelBtnListener implements OnClickListener {
		private int position;

		public MyDelBtnListener(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setTitle("删除").setMessage("确定要删除?").setCancelable(true)
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									list.remove(position);
									DataListAdapter.this.notifyDataSetChanged();
									Toast.makeText(context,
											R.string.do_success, 2000).show();
								}
							}).setNegativeButton("取消",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();

								}
							}).show();

		}

	}

	class MyUpdateBtnListener implements OnClickListener {
		private int position;

		public MyUpdateBtnListener(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			Toast.makeText(context, "现在暂时无法修改", 2000).show();
		}

	}

	class MyInfoBtnListener implements OnClickListener {
		private int position;

		public MyInfoBtnListener(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View v) {

		}

	}

}
