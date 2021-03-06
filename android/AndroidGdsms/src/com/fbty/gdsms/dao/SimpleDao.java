package com.fbty.gdsms.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.fbty.gdsms.util.BeanTools;
import com.fbty.gdsms.util.DataBaseHelper;
import com.fbty.gdsms.util.DateTools;

/**
 * 实现了EntityDao接口,其他实体DAO只要继承它即可拥有所有强大功能。
 * 
 * @author EwinLive
 * 
 * @param <T>
 * @param <PK>
 */
public abstract class SimpleDao<T, PK extends Serializable> implements
		EntityDao<T, PK> {
	private String TAG = "SimpleDao";
	/**
	 * 实体的类型
	 */
	
	protected Class<T> entityClass;



	/**
	 * 表名
	 */
	protected String tableName;

	/**
	 * 数据库管理器
	 */
	protected DataBaseHelper dbHelper;

	/**
	 * 保存实体所要执行的SQL语句 只在创建对象时初始化。
	 */
	protected String saveSql;

	/**
	 * 更新实体所要执行的SQL语句 只在创建对象时初始化。
	 */
	protected String updateSql;

	/**
	 * 字段在数据表中所对应的列的索引 只在创建对象时初始化。
	 */
	protected int[] fieldPostion;
    
	private SQLiteDatabase sqLiteDatabase;

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public DataBaseHelper getDbHelper() {
		return dbHelper;
	}

	public void setDbHelper(DataBaseHelper dbHelper) {
		this.dbHelper = dbHelper;
	}

	public String getSaveSql() {
		return saveSql;
	}

	public void setSaveSql(String saveSql) {
		this.saveSql = saveSql;
	}

	public String getUpdateSql() {
		return updateSql;
	}

	public void setUpdateSql(String updateSql) {
		this.updateSql = updateSql;
	}

	/**
	 * 专属构造器 可通过子类的范型定义取得对象类型Class.
	 * 
	 * @param tableName
	 *            实体对应的表名
	 * @param context
	 *            设备上下文，通常是一个Activity对象
	 */
	// @SuppressWarnings("unchecked")
	// public SimpleDao(String tableName, Context context) {
	// this.entityClass = (Class<T>) BeanTools.getGenericClass(getClass());
	// this.tableName = tableName;
	// this.dbHelper = new
	// DataBaseHelper(context,entityClass.getSimpleName().toLowerCase()); //传入表名
	// this.saveSql = initSaveSql();
	// this.updateSql = initUpdateSql();
	// this.fieldPostion = initFieldPostion();
	// }

	@SuppressWarnings("unchecked")
	public SimpleDao(Context context) {
		this.entityClass = (Class<T>) BeanTools.getGenericClass(getClass());
		this.tableName = entityClass.getSimpleName().toLowerCase();
		this.dbHelper = new DataBaseHelper(context, tableName); // 传入表名
		this.saveSql = initSaveSql();
		this.updateSql = initUpdateSql();
		this.fieldPostion = initFieldPostion();
	}

	@Override
	public void save(T entity) {
		sqLiteDatabase = dbHelper.getReadableDatabase();
		sqLiteDatabase.execSQL(saveSql, getSaveValue(entity));
		sqLiteDatabase.close();
	}

	@SuppressWarnings("unused")
	@Override
	public void remove(PK... ids) {
		sqLiteDatabase = dbHelper.getReadableDatabase();
		if (ids.length > 0) {
			StringBuffer sb = new StringBuffer();
			for (PK id : ids) {
				sb.append('?').append(',');
			}
			sb.deleteCharAt(sb.length() - 1);
			sqLiteDatabase.execSQL(
					"delete from " + tableName + " where id in(" + sb + ")",
					(Object[]) ids);
			sqLiteDatabase.close();
		}
	}

	@Override
	public void upDate(T entity) {
		sqLiteDatabase = dbHelper.getReadableDatabase();
		sqLiteDatabase.execSQL(updateSql,getUpdateValue(entity));
		sqLiteDatabase.close();
	}

	@Override
	public T find(PK id) {
		sqLiteDatabase = dbHelper.getReadableDatabase();
		Cursor cursor = sqLiteDatabase.rawQuery(
				"select * from " + tableName + " where id=?",
				new String[] { String.valueOf(id) });
		cursor.moveToNext();
		T t = getEntityFromCursor(cursor);
		cursor.close();
		sqLiteDatabase.close();
		return t;
	}
	
	public List<T> findAll(String wildNumber) {
		List<T> list = new ArrayList<T>();
		Cursor cursor = dbHelper.getReadableDatabase().rawQuery(
				"select * from " + tableName + " where wildNumber=?",
				new String[] { wildNumber });
		while(cursor.moveToNext()) {
			cursor.getString(cursor.getColumnIndex("searchTime"));
			list.add(getEntityFromCursor(cursor));
		}
		cursor.close();
		return list;
	}
	
	public ArrayList<T> findList(PK[] ids){
		ArrayList<T> list = new ArrayList<T>();
		StringBuffer sql = new StringBuffer();
		String [] strIds = new String[]{};
		sql.append("select * from ").append(tableName).append(" where id in (");
		int num = ids.length;
		for(int i = 0 ; i < num ; i++){
			sql.append("?,");
		}
		sql.deleteCharAt(sql.length()).append(')');
		Log.d("SimpleDao", sql.toString());
		Cursor cursor = dbHelper.getReadableDatabase().rawQuery(sql.toString(),strIds);
		while (cursor.moveToNext()) {
			list.add(getEntityFromCursor(cursor));
		}
		return list;
	}
	@Override
	public ArrayList<T> getScroolData(Integer startResult, Integer maxResult) {
		ArrayList<T> list = new ArrayList<T>(0);
		Cursor cursor = dbHelper.getReadableDatabase().rawQuery(
				"select * from " + tableName + " limit ?, ?",
				new String[] { String.valueOf(startResult),
						String.valueOf(maxResult) });
		while (cursor.moveToNext()) {
			list.add(getEntityFromCursor(cursor));
		}
		return list;
	}

	
	/**
	 * @see com.fbty.base.dao.EntityDao#getMapData(java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public ArrayList<HashMap<String, Object>> getMapData(Integer startResult,
			Integer maxResult) {
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>(0);
		StringBuffer sb = new StringBuffer();
		sb.append("select * from ").append(tableName).append(" limit ?,?");
		Log.d(TAG, sb.toString());
		sqLiteDatabase = dbHelper.getReadableDatabase();
		Cursor cursor = sqLiteDatabase.rawQuery(sb.toString(),
				new String[] { String.valueOf(startResult),String.valueOf(maxResult) });
		while (cursor.moveToNext()) {
			list.add(getMapFromCursor(cursor));
		}
		cursor.close();
		sqLiteDatabase.close();
		return list;
		
	}

	/**
	 * @see com.fbty.base.dao.EntityDao#getMapData(java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public ArrayList<HashMap<String, Object>> getMapData(HashMap<String, String> filterMap ,Integer startResult, Integer maxResult) {
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>(0);
		StringBuffer sb = new StringBuffer();
		sb.append("select * from ").append(tableName).append(" where 1=1");
		Iterator<Entry<String, String>> itor = filterMap.entrySet().iterator();
		while(itor.hasNext()) {
			Entry<String, String> entry = itor.next();
		    Log.d(TAG,entry.getKey()+"     "+entry.getValue());
		    sb.append(" and ").append(entry.getKey()).append("='"+entry.getValue()+"' ");
		}
		sqLiteDatabase = dbHelper.getReadableDatabase();
		sb.append(" limit ?,?");
		Log.d(TAG, sb.toString());
		Cursor cursor = sqLiteDatabase.rawQuery(sb.toString(),
				new String[] { String.valueOf(startResult),String.valueOf(maxResult) });
		while (cursor.moveToNext()) {
			list.add(getMapFromCursor(cursor));
		}
		cursor.close();
		sqLiteDatabase.close();
		return list;
		
	}
	
	public ArrayList<HashMap<String, Object>> getAllMapData(HashMap<String, String> filterMap) {
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>(0);
		StringBuffer sb = new StringBuffer();
		sb.append("select * from ").append(tableName).append(" where 1=1");
		Iterator<Entry<String, String>> itor = filterMap.entrySet().iterator();
		while(itor.hasNext()) {
			Entry<String, String> entry = itor.next();
		    Log.d(TAG,entry.getKey()+"     "+entry.getValue());
		    if(entry.getValue()==null){
		    	 sb.append(" and ").append(entry.getKey()).append(" is "+entry.getValue()+" ");
		    }else{
		    	sb.append(" and ").append(entry.getKey()).append("='"+entry.getValue()+"' ");
		    }
		}
		Log.d(TAG, sb.toString());
		sqLiteDatabase = dbHelper.getReadableDatabase();
		Cursor cursor = sqLiteDatabase.rawQuery(sb.toString(),
				null);
		while (cursor.moveToNext()) {
			list.add(getMapFromCursor(cursor));
		}
		cursor.close();
		sqLiteDatabase.close();
		return list;
	}
	
	@Override
	public Long getCount() {
		Cursor cursor = dbHelper.getReadableDatabase().rawQuery(
				"select count(*) from " + tableName, null);
		if (cursor.moveToNext()) {
			return cursor.getLong(0);
		}
		cursor.close();
		return 0l;
	}

	/**
	 * 初始化保存实体所需的SQL语句
	 * 
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	protected String initSaveSql() {
		HashMap data = BeanTools.getAllFiled(entityClass);
		String[] fieldName = (String[]) data.get("fieldName");
		StringBuffer bufferName = new StringBuffer();
		StringBuffer bufferExpr = new StringBuffer();

		for (String tmp : fieldName) {
			bufferName.append(tmp).append(", ");
			bufferExpr.append("?,");
		}

		// 去除id字段及其属性值
		bufferName.delete(bufferName.indexOf(" id"),
				bufferName.indexOf(" id") + 4);
		bufferExpr.delete(0, 2);

		// 去除多余的分隔符
		bufferName.deleteCharAt(bufferName.length() - 2);
		bufferExpr.deleteCharAt(bufferExpr.length() - 1);

		String sql = "insert into " + tableName + "(" + bufferName.toString()
				+ ") values(" + bufferExpr.toString() + ")";
		return sql;
	}

	/**
	 * 初始化更新实体所需的SQL语句
	 * 
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	protected String initUpdateSql() {
		HashMap data = BeanTools.getAllFiled(entityClass);
		String[] fieldName = (String[]) data.get("fieldName");
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("update " + tableName + " set ");
		for (String tmp : fieldName) {
			sqlBuffer.append(tmp).append("=?, ");
		}

		// 去除id字段及其属性值
		int start = sqlBuffer.indexOf(" id=?");
		sqlBuffer.delete(start, start + 6);
		sqlBuffer.deleteCharAt(sqlBuffer.length() - 2);
		sqlBuffer.append("where id =?");

		return sqlBuffer.toString();
	}

	/**
	 * 获取保存实体所需的值
	 * 
	 * @param entity
	 * @return
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 */
	@SuppressWarnings("rawtypes")
	protected Object[] getSaveValue(T entity) {
		HashMap data = BeanTools.getAllFiled(entityClass);
		String[] fieldName = (String[]) data.get("fieldName");
		Object[] values;

		int length = fieldName.length;
		values = new Object[length - 1];
		int j = 0;
		for (int i = 0; i < length; i++) {
			if ("id".equals(fieldName[i].toString())) {
				continue;// 跳过ID字段
			}
				values[j++] = BeanTools.getPrivateProperty(entity, fieldName[i]);
		}
		return values;
	}

	/**
	 * 获取更新实体所需的值
	 * 
	 * @param entity
	 * @return
	 * @throws IllegalAccessException
	 * @throws NoSuchFieldException
	 */
	@SuppressWarnings("rawtypes")
	protected Object[] getUpdateValue(T entity) {
		HashMap data = BeanTools.getAllFiled(entityClass);
		String[] fieldName = (String[]) data.get("fieldName");
		Object[] values;

		int length = fieldName.length;
		values = new Object[length - 1];
		int j = 0;
		int id = 0;

		for (int i = 0; i < length; i++) {
			if ("id".equals(fieldName[i].toString())) {
				id = (Integer) BeanTools.getPrivateProperty(entity,
						fieldName[i]);
				continue;// 跳过ID字段
			}
			if("serialVersionUID".equals(fieldName[i].toString()))continue;
			values[j++] = BeanTools.getPrivateProperty(entity, fieldName[i]);
		}

		Object[] values2 = new Object[length];
		System.arraycopy(values, 0, values2, 0, values.length);
		values2[length - 1] = id;

		return values2;
	}

	/**
	 * 初始化字段在数据表中 对应的索引
	 * 
	 * @param cursor
	 */
	@SuppressWarnings("rawtypes")
	protected int[] initFieldPostion() {
		HashMap data = BeanTools.getAllFiled(entityClass);
		String[] fieldName = (String[]) data.get("fieldName");
		int length = fieldName.length;
		int[] postion = new int[length];
		sqLiteDatabase = dbHelper.getWritableDatabase();
		Cursor cursor = sqLiteDatabase.rawQuery(
				"select * from " + tableName + " limit ?, ?",
				new String[] { "0", "2" });
		for (int i = 0; i < length; i++) {
			postion[i] = cursor.getColumnIndex(fieldName[i]);
		}
		cursor.close();
		sqLiteDatabase.close();
		return postion;
	}

	/**
	 * 从游标中获取实体
	 * 
	 * @param cursor
	 *            游标
	 * @return T 实体对象
	 */
	@SuppressWarnings("rawtypes")
	public T getEntityFromCursor(Cursor cursor) {
		HashMap data = BeanTools.getAllFiled(entityClass);
		String[] fieldName = (String[]) data.get("fieldName");
		Class<?>[] fieldType = (Class<?>[]) data.get("fieldType");
		int length = fieldName.length;

		T entity = null;
		String db_data;
		String fieldTypeName;
		try {
			entity = entityClass.newInstance();
			for (int i = 0; i < length; i++) {
				fieldTypeName = fieldType[i].getSimpleName();
				db_data = cursor.getString(fieldPostion[i]);
				if (null != db_data) {
					Log.d("SimpleDao",fieldTypeName+""+db_data);
					if ("String".equals(fieldTypeName)) {
						BeanTools.setFieldValue(entity, fieldName[i], db_data);
					} else if ("int".equals(fieldTypeName)||"Integer".equals(fieldTypeName)) {
						BeanTools.setFieldValue(entity, fieldName[i], Integer
								.parseInt(db_data));
					} else if ("long".equals(fieldTypeName)||"Long".equals(fieldTypeName)) {
						BeanTools.setFieldValue(entity, fieldName[i], Long
								.getLong(db_data));
					} else if ("float".equals(fieldTypeName)||"Float".equals(fieldTypeName)) {
						BeanTools.setFieldValue(entity, fieldName[i], Float
								.parseFloat(db_data));
					} else if ("Date".equals(fieldTypeName)){
						BeanTools.setFieldValue(entity, fieldName[i],DateTools.parse(db_data,"EEE MMM dd hh:mm:ss z yyyy"));
						
					}
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return entity;
	}
	
	/**
	 * 从游标中获取map
	 * 
	 * @param cursor
	 *            游标
	 * @return T 实体对象
	 */
	@SuppressWarnings("rawtypes")
	public HashMap<String,Object> getMapFromCursor(Cursor cursor) {
		HashMap data = BeanTools.getAllFiled(entityClass);
		String[] fieldName = (String[]) data.get("fieldName");
		Class<?>[] fieldType = (Class<?>[]) data.get("fieldType");
		int length = fieldName.length;
		
		HashMap<String, Object> resultMap = new HashMap<String,Object>();
		String db_data;
		String fieldTypeName;
		try {
			for (int i = 0; i < length; i++) {
				fieldTypeName = fieldType[i].getSimpleName();
				db_data = cursor.getString(fieldPostion[i]);
				if (null != db_data) {
					resultMap.put(fieldName[i], db_data);
					}
				}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return resultMap;
	}
	public void saveOrUpdate(T entity){
		Object o = BeanTools.getPrivateProperty(entity, "id");
		if(o==null){
			save(entity);
		}else{
			upDate(entity);
		}
	}
	
}
