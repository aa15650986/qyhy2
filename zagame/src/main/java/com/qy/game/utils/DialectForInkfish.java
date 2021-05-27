package com.qy.game.utils;

import java.sql.Types;

import org.hibernate.dialect.MySQL5Dialect;


/**
 * 为了让mysql能够支持text的sql查询
 * @author CEC
 * @date:2017-3-10
 */
public class DialectForInkfish extends MySQL5Dialect {
	 public DialectForInkfish() {  
	        super();  
	        registerHibernateType(Types.LONGVARCHAR, 65535, "text");  
	    } 
}
