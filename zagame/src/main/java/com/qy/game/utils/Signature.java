package com.qy.game.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;


/**
 * User: rizenguo
 * Date: 2014/10/29
 * Time: 15:23
 */
public class Signature {
    /**
     * 签名算法
     * @param o 要参与签名的数据对象
     * @return 签名
     * @throws IllegalAccessException
     */
	
	public static String getSign(String str,String key){
		if(key!=null)
			str+=key;
		 System.out.println("-----------Sign Before MD5----:" + str);
		str = MD5.MD5Encode(str);
		 System.out.println("-----------Sign Before MD5----:" + str);
        return str;
	}
	
    public static String getSign(Object o,String key) throws IllegalAccessException {
        ArrayList<String> list = new ArrayList<String>();
        Class<?> cls = o.getClass();
        Field[] fields = cls.getDeclaredFields();
        for (Field f : fields) {
            f.setAccessible(true);
            if (f.get(o) != null && f.get(o) != "") {
                list.add(f.getName() + "=" + f.get(o) + "&");
            }
        }
        int size = list.size();
        String [] arrayToSort = list.toArray(new String[size]);
        Arrays.sort(arrayToSort, String.CASE_INSENSITIVE_ORDER);
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < size; i ++) {
            sb.append(arrayToSort[i]);
        }
        String result = sb.toString();
        if(key!=null)
        	result += "key=" +key;
        else
        	result=result.substring(0, result.length() - 1);
        Util.log("Sign Before MD5:" + result);
        result = MD5.MD5Encode(result).toUpperCase();
        Util.log("Sign Result:" + result);
        return result;
    }

    public static String getSign(Map<String,String> map,String key){
    	
        ArrayList<String> list = new ArrayList<String>();
        for (String in : map.keySet()) {
            //map.keySet()返回的是所有key的值
	      //String str = map.get(in);//得到每个key多对用value的值
	      if(map.get(in)!=""){
	          list.add(map.get(in) + "+");
	      }
     // System.out.println(in + "     " + str);
}
      /*  for(Entry<String, String> entry:map.keySet()){
        	
            if(entry.getValue()!=""){
                list.add(entry.getValue() + "+");
            }
        }*/
        int size = list.size();
        String [] arrayToSort = list.toArray(new String[size]);
       // Arrays.sort(arrayToSort, String.CASE_INSENSITIVE_ORDER);
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < size; i ++) {
            sb.append(arrayToSort[i]);
        }
        String result = sb.toString();
      /*  if(key!=null)
        	result += "key=" +key;
        else*/
        	result=result+key;
        System.out.println("-----------Sign Before MD5----:" + result);
        result = MD5.MD5Encode(result);
        System.out.println("-----------Sign Result--------:" + result);
        return result;
    }

    /**
     * 按照字母顺序
     * @param map
     * @param key
     * @return
     */
    public static String getSign1(Map<String,String> map,String key){
        ArrayList<String> list = new ArrayList<String>();
        for(Entry<String, String> entry:map.entrySet()){
            if(entry.getValue()!=""){
                list.add(entry.getKey() + "=" + entry.getValue() + "&");
            }
        }
        int size = list.size();
        String [] arrayToSort = list.toArray(new String[size]);
        Arrays.sort(arrayToSort, String.CASE_INSENSITIVE_ORDER);
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < size; i ++) {
            sb.append(arrayToSort[i]);
        }
        String result = sb.toString();
        if(key!=null)
        	result = result.substring(0, result.length() - 1)+key;
        else
        	result=result.substring(0, result.length() - 1);
        System.out.println("-----------Sign Before MD5----:" + result);
        result = MD5.MD5Encode(result).toUpperCase();
        System.out.println("-----------Sign Result--------:" + result);
        return result;
    }
    /**
     * 按照传入顺序
     * @param map
     * @param key
     * @return
     */
    public static String getSign2(Map<String,String> map,String key){
    	ArrayList<String> list = new ArrayList<String>();
    	for(String in:map.keySet()){
    		if(map.get(in)!=""){
    			list.add(in + "=" + map.get(in) + "&");
    		}
    	}
    	int size = list.size();
    	String [] arrayToSort = list.toArray(new String[size]);
    	//Arrays.sort(arrayToSort, String.CASE_INSENSITIVE_ORDER);
    	StringBuilder sb = new StringBuilder();
    	for(int i = 0; i < size; i ++) {
    		sb.append(arrayToSort[i]);
    	}
    	String result = sb.toString();
    	if(key!=null)
    		result = result.substring(0, result.length() - 1)+key;
    	else
    		result=result.substring(0, result.length() - 1);
    	System.out.println("-----------Sign Before MD5----:" + result);
    	result = MD5.MD5Encode(result).toUpperCase();
    	System.out.println("-----------Sign Result--------:" + result);
    	return result;
    }
    
   public static String sortParam(Map<String,String> map){
	   ArrayList<String> list = new ArrayList<String>();
       for(Entry<String, String> entry:map.entrySet()){
           if(entry.getValue()!=""){
               list.add(entry.getKey() + "=" + entry.getValue() + "&");
           }
       }
       int size = list.size();
       String [] arrayToSort = list.toArray(new String[size]);
       Arrays.sort(arrayToSort, String.CASE_INSENSITIVE_ORDER);
       StringBuilder sb = new StringBuilder();
       for(int i = 0; i < size; i ++) {
           sb.append(arrayToSort[i]);
       }
       String result = sb.toString();
       return result.substring(0, result.length() - 1);
   }

}
