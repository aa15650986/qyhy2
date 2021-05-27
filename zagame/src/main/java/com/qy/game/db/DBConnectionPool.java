package com.qy.game.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

/**
 * 数据库连接池管理类
 * 
 */
public class DBConnectionPool {
	
    static DataSource dataSource = new DataSource();

    private DBConnectionPool() {
        super();
    }
    
    static {
        PoolProperties poolProperties = new PoolProperties();
        Properties dbProperties = new Properties();
        try {
            dbProperties.load(DBConnectionPool.class.getClassLoader().getResourceAsStream("jdbcPool.properties"));
            //设置URL
            poolProperties.setUrl(dbProperties.getProperty("url"));
            //设置驱动名
            poolProperties.setDriverClassName(dbProperties.getProperty("driver"));
            //设置数据库用户名
            poolProperties.setUsername(dbProperties.getProperty("username"));
            //设置数据库密码
            poolProperties.setPassword(dbProperties.getProperty("password"));
            //设置初始化连接数
            poolProperties.setInitialSize(Integer.valueOf(dbProperties.getProperty("initialSize")));
            
            poolProperties.setMaxActive(Integer.valueOf(dbProperties.getProperty("maxActive")));
            poolProperties.setMaxIdle(Integer.valueOf(dbProperties.getProperty("maxIdle")));
            poolProperties.setMinIdle(Integer.valueOf(dbProperties.getProperty("minIdle")));
            poolProperties.setMaxWait(Integer.valueOf(dbProperties.getProperty("maxWait")));
            poolProperties.setRemoveAbandoned(Boolean.valueOf(dbProperties.getProperty("removeAbandoned")));
            poolProperties.setRemoveAbandonedTimeout(Integer.valueOf(dbProperties.getProperty("removeAbandonedTimeout")));
            
            dataSource.setPoolProperties(poolProperties);
        } catch (Exception e) {
            throw new RuntimeException("初始化数据库连接池失败");
        }
    }

    
    /**
     * 获取数据库连接
     * @return 数据库连接
     */
    public static final Connection getConnection() {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    
    /**
    * 关闭连接
    * @param conn 需要关闭的连接
    */
    public static void closeConnection(Connection conn) {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException("关闭数据库连接失败");
        }
    }
    
}
