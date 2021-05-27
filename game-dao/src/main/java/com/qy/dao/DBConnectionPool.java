package com.qy.dao;


import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;

@Component
public class DBConnectionPool {
    private static final Logger logger = LoggerFactory.getLogger(DBConnectionPool.class);

    private static DruidDataSource dataSource;

    @Autowired
    private void setDruidDateSource(DruidDataSource dataSource){
        DBConnectionPool.dataSource = dataSource;
    }

    public static final Connection getConnection(){
        DruidPooledConnection conn = null;
        try {
            conn=dataSource.getConnection();
        } catch (SQLException throwables) {
            logger.error("",throwables);
        }
        return conn;
    }

    public  static void closeConnection(Connection conn){
        try {
            if (conn!=null && !conn.isClosed()){
                conn.close();
            }
        } catch (SQLException throwables) {
            throw new RuntimeException("关闭数据库连接失败");
        }
    }
}
