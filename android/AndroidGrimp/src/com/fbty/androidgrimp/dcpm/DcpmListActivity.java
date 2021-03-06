/**
 * 
 */
package com.fbty.androidgrimp.dcpm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.fbty.androidgrimp.MainActivity;
import com.fbty.androidgrimp.R;
import com.fbty.androidgrimp.dao.SectionDao;
import com.fbty.androidgrimp.domain.Section;
import com.fbty.androidgrimp.listener.MyListButtonOnClick;
import com.fbty.androidgrimp.util.GobalApplication;
import com.fbty.androidgrimp.util.GrimpConstants;
import com.fbty.base.activity.BaseActivity;
import com.fbty.base.adapter.DataListViewAdapter;
import com.fbty.base.util.FormFile;
import com.fbty.base.util.Util;

/**
 * 
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
 * @author lvbingfeng create 2011-1-25
 * 
 * @version 0.1
 * 
 */
public class DcpmListActivity extends BaseActivity {

	private Bundle testInstanceState;
	private static final int ADD = 1;
	private static final int OBSERVING = 2;
	private DataListViewAdapter sa;
	private int pageNo = 1;
	private int pageSize = 1000;
	private SectionDao sectionDao;
	private Button next;
	private Button previous;
	private long total;
	private ArrayList<HashMap<String, Object>> data;
	private HashMap<String, Object> datas;

	private ProgressDialog pDialog;
	private TextView tv;
	private int fileSize;
	private int downLoadFileSize;
	private String imgSrc;
	private String videoSrc;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dcpm_list);
		testInstanceState = savedInstanceState;
		// 下一页
		// next = (Button) findViewById(R.id.nextPage);
		// previous = (Button) findViewById(R.id.previousPage);
		// next.setOnClickListener(new OnClickListener() {
		//			
		// @Override
		// public void onClick(View v) {
		// sa.setData(getData(++pageNo, pageSize));
		// sa.notifyDataSetChanged();
		// }
		// });
		// previous.setOnClickListener(new OnClickListener() {
		//			
		// @Override
		// public void onClick(View v) {
		// sa.setData(getData(--pageNo, pageSize));
		// sa.notifyDataSetChanged();
		// }
		// });

		sectionDao = new SectionDao(this);
		data = getData(pageNo, pageSize);
		// 创建一个适配器
		sa = new DataListViewAdapter(this, data, R.layout.dcpm_data_item,
				new String[] { "id", "name", "searchTime", "backdrop", "del",
						"update", "submit", "observing" }, new int[] {
						R.id.did, R.id.name, R.id.time, R.id.struct,
						R.id.delBtn, R.id.updateBtn, R.id.submitBtn,
						R.id.observingBtn });
		ListView list = (ListView) findViewById(R.id.dataList);
		list.setAdapter(sa);
		list.setOnItemClickListener(new ItemClickListener());
		setTitle(R.string.dcpm_info);
		GridView bottom_menu = (GridView) findViewById(R.id.GridView_toolbar);
		bottom_menu.setAdapter(getBottomMenuAdapter());
		bottom_menu.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				// 返回主页
				case 0:
					Intent i = new Intent(DcpmListActivity.this,
							MainActivity.class);
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					DcpmListActivity.this.startActivity(i);
					break;
				// 添加新数据
				case 1:
					Intent intent = new Intent(DcpmListActivity.this,
							DcpmItemActivity.class);
					// 点击添加时候生成新的对象放入全局变量中
					gobal = (GobalApplication) DcpmListActivity.this
							.getApplication();
					Section section = new Section();
					section.setEnd(GrimpConstants.RELIC);
					section.setType(getIntent().getStringExtra("type"));
					gobal.setSection(section);
					startActivity(intent);
					break;
				// 刷新
				case 2:
					sa.setData(getData(pageNo, pageSize));
					sa.notifyDataSetChanged();
					break;
				// 返回
				case 3:
					DcpmListActivity.this.finish();
					break;
				// 退出应用
				case 4:
					 Intent i1 = new Intent(DcpmListActivity.this,MainActivity.class);
					 //表示退出
					 i1.putExtra("exit", true);
					 i1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					 DcpmListActivity.this.startActivity(i1);
//					Intent i1 = new Intent(Intent.ACTION_MAIN);
//					i1.addCategory(Intent.CATEGORY_HOME);
//					DcpmListActivity.this.startActivity(i1);
					// android.os.Process.killProcess(android.os.Process.myPid());
					break;
				}
			}
		});

	}

	/**
	 * 创建菜单
	 * 
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		menu.add(0, ADD, 0, R.string.add).setIcon(
				android.R.drawable.ic_menu_add);
		return super.onCreateOptionsMenu(menu);

	}

	/**
	 * 为菜单添加监听
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case ADD:
			Intent intent = new Intent(DcpmListActivity.this,
					DcpmItemActivity.class);
			// 点击添加时候生成新的对象放入全局变量中
			gobal = (GobalApplication) DcpmListActivity.this.getApplication();
			Section section = new Section();
			section.setEnd(GrimpConstants.RELIC);
			section.setType(getIntent().getStringExtra("type"));
			gobal.setSection(section);
			startActivity(intent);
			return true;
		case OBSERVING:
			Intent intent2 = new Intent(DcpmListActivity.this,
					DcpmObservationPointActivity.class);
			startActivity(intent2);
			return true;
		}
		return false;
	}

	class ItemClickListener implements OnItemClickListener {
		@SuppressWarnings("unchecked")
		public void onItemClick(AdapterView<?> arg0,// The AdapterView where the
				// click happened
				View arg1,// The view within the AdapterView that was clicked
				int arg2,// The position of the view in the adapter
				long arg3// The row id of the item that was clicked
		) {

			HashMap<String, Object> item = (HashMap<String, Object>) arg0
					.getItemAtPosition(arg2);
			Toast.makeText(DcpmListActivity.this, "" + arg2, 1000);

		}

	}

	/**
	 * 内部类：监听删除按钮
	 * 
	 */
	class MyDelBtnListener extends MyListButtonOnClick {
		@Override
		public void onClick(View v) {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					DcpmListActivity.this);
			builder.setTitle("删除").setMessage("确定要删除?").setCancelable(true)
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Map<String, Object> item = (Map<String, Object>) list
											.get(position);
									// 通过id删除记录
									new SectionDao(DcpmListActivity.this)
											.removeRelic(Integer.parseInt(item
													.get("id")
													+ ""));
									// 获取最新记录
									sa.setData(getData(pageNo, pageSize));
									sa.notifyDataSetChanged();
									Toast.makeText(DcpmListActivity.this,
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

	/**
	 * 内部类：监听更新按钮
	 * 
	 */
	class MyUpdateBtnListener extends MyListButtonOnClick {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(DcpmListActivity.this, DcpmItemActivity.class);
			intent.putExtra("section", sectionDao.find(Integer.parseInt(list
					.get(position).get("id")
					+ "")));
			startActivity(intent);
		}
	}

	/**
	 * 
	 * <p>
	 * Description:
	 * </p>
	 * 内部类：监听提交按钮
	 */
	class MySubmitBtnListener extends MyListButtonOnClick {

		@Override
		public void onClick(View v) {
			pDialog = new ProgressDialog(DcpmListActivity.this);

			if (isHaveWifi()) {
				showDialog_Layout(DcpmListActivity.this);
				GobalApplication gobal = (GobalApplication) DcpmListActivity.this.getApplication();
				Log.i("ddddd", gobal.getActionUrl() + "");
			} else {
				Toast.makeText(DcpmListActivity.this, "无网络",Toast.LENGTH_SHORT).show();
				pDialog.cancel();
			}

		}

		private void showDialog_Layout(Context context) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setCancelable(false);
			builder.setIcon(R.drawable.icon);
			builder.setTitle("确认提交数据?");
			builder.setPositiveButton("提交",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							int id = Integer.parseInt(list.get(position).get("id")+ "");
							SectionDao sectionDao = new SectionDao(DcpmListActivity.this);
							Section section = sectionDao.find(id);
							HashMap filterMap = new HashMap();
							filterMap.put("id", id + "");
							ArrayList<HashMap<String, Object>> d = sectionDao.getAllMapData(filterMap);
							datas = d.get(0);
							if (datas != null) {
								section.setIsData(1);
							}
							sectionDao.upDate(section);
							pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
							pDialog.setTitle("正在提交,请稍后!");
							pDialog.setMessage("确认或者返回可取消提交!");
							pDialog.setIcon(R.drawable.icon);
							pDialog.setIndeterminate(false);
							pDialog.setCancelable(true);
							pDialog.setButton("确认",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog,int which) {
											dialog.cancel();
											Toast.makeText(DcpmListActivity.this,"取消成功!", 1000);
											
										}
									});
							// pDialog.show();
							if (datas != null) {
								FormFile[] files = {};
								try {
									gobal = (GobalApplication) DcpmListActivity.this.getApplication();
									String actionUrl = gobal.getActionUrl();
									String result = Util.post(actionUrl, datas,files);
									Toast.makeText(DcpmListActivity.this,"提交成功", 2000).show();
								} catch (Exception e) {
									Toast.makeText(DcpmListActivity.this,"网络故障请稍后再试", 2000).show();
								}
							}
							pDialog.cancel();

						}

					});
			builder.setNegativeButton("取消",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {

						}
					});
			builder.show();
		}

	}

	/**
	 * 
	 * <p>
	 * Description:
	 * </p>
	 * 内部类：监听观测点按钮
	 */
	class MyObservingBtnListener extends MyListButtonOnClick {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(DcpmListActivity.this,
					DcpmObservationPointActivity.class);
			intent.putExtra("wildNumber", (String) list.get(position).get(
					"wildNumber"));
			intent.putExtra("type", (String) list.get(position).get("" +"type"));
			startActivity(intent);
		}

	}

	/**
	 * 
	 * Description: <br>
	 * 获取数据
	 * 
	 * @return
	 */
	public ArrayList<HashMap<String, Object>> getData(int pageNo, int pageSize) {
		HashMap<String, String> filter = new HashMap<String, String>();
		// 类型
		filter.put("type", getIntent().getStringExtra("type"));
		// 是遗迹不是观测点
		filter.put("isEnd", GrimpConstants.RELIC + "");
		ArrayList<HashMap<String, Object>> data = new SectionDao(this)
				.getMapData(filter, (pageNo - 1) * pageSize, (pageNo)
						* pageSize);
		int size = data.size();
		for (int i = 0; i < size; i++) {
			HashMap<String, Object> hashMap = data.get(i);
			// Date d = null;
			// try {
			// if(hashMap.get("searchTime") != null){
			// d = new
			// SimpleDateFormat(GrimpConstants.DATE_FORMAT_STRING,Locale.US).parse((String)
			// hashMap.get("searchTime"));
			// }
			// } catch (ParseException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }if(d != null){
			// hashMap.put("searchTime",new
			// SimpleDateFormat("yyyy-MM-dd").format(d));
			// }
			hashMap.put("searchTime", (String) hashMap.get("searchTime"));
			hashMap.put("del", new MyDelBtnListener());// 为删除按钮添加事件
			hashMap.put("update", new MyUpdateBtnListener());// 为更新按钮添加事件
			hashMap.put("submit", new MySubmitBtnListener());// 为提交按钮添加事件
			hashMap.put("observing", new MyObservingBtnListener());// 为观测点按钮添加事件
		}
		// setTotalPage(data);
		// if(pageNo >= total){
		// next.setEnabled(false);
		// }else{
		// next.setEnabled(true);
		// }
		// if (pageNo == 1) {
		// previous.setEnabled(false);
		// }else{
		// previous.setEnabled(true);
		// }
		// TextView t = (TextView) findViewById(R.id.totalPage);
		// t.setText("共"+total+"页");
		// TextView p = (TextView) findViewById(R.id.pageNo);
		// p.setText("第"+pageNo+"页");
		return data;
	}

	@Override
	protected void onStart() {

		sa.setData(getData(pageNo, pageSize));
		sa.notifyDataSetChanged();
		super.onStart();
	}

	// @Override
	// protected void onRestart() {
	// onCreate(testInstanceState);
	//
	//		
	// super.onRestart();
	// }

	private void setTotalPage(ArrayList<HashMap<String, Object>> data) {
		int count = (Integer) data.get(data.size() - 1).get("count");
		total = count / pageSize;
		if (count % pageSize != 0) {
			total++;
		}
		if (total == 0) {
			total = 1;
		}
		data.remove(data.size() - 1);
	}

	/*
	 * private Handler handler = new Handler() { public void
	 * handleMessage(Message msg) {// 定义一个Handler，用于处理下载线程与UI间通讯 if
	 * (!Thread.currentThread().isInterrupted()) { switch (msg.what) { case 0:
	 * pb.setMax(fileSize); case 1: int result = downLoadFileSize * 100 /
	 * fileSize; try { Thread.sleep(2000); } catch (InterruptedException e) { //
	 * TODO Auto-generated catch block e.printStackTrace(); }
	 * pb.setProgress(result); tv.setText(result + "%"); break; case 2:
	 * Toast.makeText(DcpmListActivity.this, "数据提交完成", 1).show(); break;
	 * 
	 * case -1: String error = msg.getData().getString("error");
	 * Toast.makeText(DcpmListActivity.this, error, 1).show(); break; } }
	 * super.handleMessage(msg); } };
	 */
	// public void uploadFile(String name) throws IOException {
	// FormFile[] files={};
	// if(name!=null){
	// File[] fs = new File(name).listFiles(new FileFilter() {
	// @Override
	// public boolean accept(File pathname) {
	// if (pathname.isFile()) {
	// return true;
	// } else {
	// return false;
	// }
	// }
	// });
	// int size = fs.length;
	//
	// files = new FormFile[size];
	// for (int i = 0; i < size; i++) {
	// File fi = fs[i];
	// if (fi.isFile()) {
	// FormFile file = new FormFile(name + File.separator
	// + fi.getName(), new byte[8192], "file",
	// "application/octet-stream");
	// files[i] = file;
	// }
	// }
	// }
	// String result = Util.post(GrimpConstants.actionUrl, datas, files);
	// Log.d("xx", "xxxx"+result);
	// pDialog.cancel();
	// }
	//
	// private void sendMsg(int flag) {
	// Message msg = new Message();
	// msg.what = flag;
	// handler.sendMessage(msg);
	// }
	// 是否有网络
	public boolean isHaveWifi() {
		return true;
	}

	public SimpleAdapter getBottomMenuAdapter() {
		ArrayList<HashMap<String, Object>> data1 = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("menu_image", R.drawable.ic_menu_home);
		map.put("menu_text", getString(R.string.home));
		data1.add(map);
		map = new HashMap<String, Object>();
		map.put("menu_image", R.drawable.ic_menu_add);
		map.put("menu_text", getString(R.string.add_new));
		data1.add(map);
		map = new HashMap<String, Object>();
		map.put("menu_image", R.drawable.ic_menu_refresh);
		map.put("menu_text", getString(R.string.refresh));
		data1.add(map);
		map = new HashMap<String, Object>();
		map.put("menu_image", R.drawable.ic_menu_back);
		map.put("menu_text", getString(R.string.back_old));
		data1.add(map);
		map = new HashMap<String, Object>();
		map.put("menu_image", R.drawable.ic_menu_close);
		map.put("menu_text", getString(R.string.close));
		data1.add(map);
		SimpleAdapter sa = new SimpleAdapter(this, data1, R.layout.item_menu,
				new String[] { "menu_image", "menu_text" }, new int[] {
						R.id.item_image, R.id.item_text });
		return sa;
    }

}
