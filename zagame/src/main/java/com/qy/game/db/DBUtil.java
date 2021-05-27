package com.qy.game.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 数据库基础操作类
 * @author lhp
 *
 */
public class DBUtil {

	
	public static void main(String args[]) throws Exception {  
		
		Object[] params = new Object[] {};
		String sql = "select * from za_users";
		JSONArray jsonArray = DBUtil.getObjectListBySQL(sql, params);
		System.out.println(jsonArray);
    }  
	
    
    /**
     * 返回多条记录
     * @param sql
     * @param params
     * @return
     */
    public static JSONArray getObjectListBySQL(String sql, Object[] params){
    	
    	// 从连接池获取数据库连接
    	Connection conn = DBConnectionPool.getConnection();
    	JSONArray jsonArray=new JSONArray();
    	PreparedStatement pstmt = null;
    	ResultSet rs = null;
    	try {
    		
    		// 预编译SQL语句
    		pstmt = conn.prepareStatement(sql);
			
    		if(params!=null){
    			for (int i=0; i<params.length; i++) {
    				pstmt.setObject(i+1, params[i]);
    			}
    		}
			
			rs = pstmt.executeQuery();
			
			// 获取结果集列数
			int columCount = rs.getMetaData().getColumnCount();
			
			while(rs.next()){
				
				JSONObject jsonObject=new JSONObject();
				
				for (int i = 0; i < columCount; i++) {
					String name = rs.getMetaData().getColumnName(i+1);
					Object val = rs.getObject(i+1);
					if(name.equals("createtime")){
						val=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(val);
					}
					jsonObject.put(name, val);
					
				}
				jsonArray.add(jsonObject);
			}
			return jsonArray;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			close(conn, pstmt, rs);
		}
    	return jsonArray;
    }

    /**
     * 返回多条记录2
     * @param sql
     * @param params
     * @param 去掉时间转换
     * @return
     */
    public static JSONArray getObjectListBySQL2(String sql, Object[] params){
    	
    	// 从连接池获取数据库连接
    	Connection conn = DBConnectionPool.getConnection();
    	JSONArray jsonArray=new JSONArray();
    	PreparedStatement pstmt = null;
    	ResultSet rs = null;
    	try {
    		
    		// 预编译SQL语句
    		pstmt = conn.prepareStatement(sql);
			
    		if(params!=null){
    			for (int i=0; i<params.length; i++) {
    				pstmt.setObject(i+1, params[i]);
    			}
    		}
			
			rs = pstmt.executeQuery();
			
			// 获取结果集列数
			int columCount = rs.getMetaData().getColumnCount();
			
			while(rs.next()){
				
				JSONObject jsonObject=new JSONObject();
				
				for (int i = 0; i < columCount; i++) {
					String name = rs.getMetaData().getColumnName(i+1);
					Object val = rs.getObject(i+1);
//					if(name.equals("createtime")){
//						val=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(val);
//					}
					jsonObject.put(name, val);
					
				}
				jsonArray.add(jsonObject);
			}
			return jsonArray;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			close(conn, pstmt, rs);
		}
    	return jsonArray;
    }
    
    /**
     * 返回查询结果的第一条数据
     * @param sql
     * @param params
     * @return
     */
    public static JSONObject getObjectBySQL(String sql, Object[] params){
    	
    	// 从连接池获取数据库连接
    	Connection conn = DBConnectionPool.getConnection();
    	PreparedStatement pstmt = null;
    	ResultSet rs = null;
    	try {
    		// 预编译SQL语句
    		pstmt = conn.prepareStatement(sql);
			
			for (int i=0; i<params.length; i++) {
				
				pstmt.setObject(i+1, params[i]);
			}
			
			rs = pstmt.executeQuery();
			
			// 获取结果集列数
			int columCount = rs.getMetaData().getColumnCount();
			//columCount
			if(rs.next()){
				JSONObject jsonObject=new JSONObject();
				
				for (int i = 0; i < columCount; i++) {
					
					jsonObject.put(rs.getMetaData().getColumnName(i+1), rs.getObject(i+1));
				}
				
				return jsonObject;
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			close(conn, pstmt, rs);
		}
    	return null;
    }
    
    
    /**
     * 更新数据，返回影响行数
     * @param sql
     * @param params
     * @return
     */
    public static int executeUpdateBySQL(String sql, Object[] params){
    	
    	// 从连接池获取数据库连接
    	Connection conn = DBConnectionPool.getConnection();
    	PreparedStatement pstmt = null;
    	try {
    		// 预编译SQL语句
    		pstmt = conn.prepareStatement(sql);
			
			for (int i=0; i<params.length; i++) {
				
				pstmt.setObject(i+1, params[i]);
			}
			
			return pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			close(conn, pstmt, null);
		}
    	return 0;
    }



	/**
	 * 释放资源
	 * @param conn
	 * @param pstmt
	 * @param rs
	 */
	private static void close(Connection conn, PreparedStatement pstmt, ResultSet rs) {
		
		try {
			if(rs != null){
				rs.close();
			}
			if(pstmt != null){
				pstmt.close();
			}
			if (conn != null){
				DBConnectionPool.closeConnection(conn);
			}
		}catch (SQLException e) {
			e.printStackTrace();
       }
	}
	 public static int executeBySQL(String sql, Object[] params){
	    	
	    	// 从连接池获取数据库连接
	    	Connection conn = DBConnectionPool.getConnection();
	    	PreparedStatement pstmt = null;
	    	int value=0;
	    	try {
	    		// 预编译SQL语句
	    		pstmt = conn.prepareStatement(sql);
				
				
				ResultSet rs=pstmt.executeQuery(sql);
				
				while(rs.next()){
					 value = rs.getInt(0);
				}
				
			} catch (SQLException e) {
				e.printStackTrace();
			}finally{
				close(conn, pstmt, null);
			}
			return value;
	    	
	    }
    
}
