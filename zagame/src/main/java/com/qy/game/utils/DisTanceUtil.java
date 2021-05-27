package com.qy.game.utils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 物理距离工具类
 */
public class DisTanceUtil {

	private static final double EARTH_RADIUS = 6378137;
	private static final String BAIDUMAP_API_AK = "vGfFS9pGYx1KqoTVWGd7mtk8rehmaCTG";

	private static double rad(double d) {
		return d * Math.PI / 180.0;
	}

	/**
	 * 根据两点间经纬度坐标（double值），计算两点间距离，单位为米
	 * 
	 * @param lng1
	 * @param lat1
	 * @param lng2
	 * @param lat2
	 * @return
	 */
	public static long getDistance(double lng1, double lat1, double lng2,
			double lat2) {
		double radLat1 = rad(lat1);
		double radLat2 = rad(lat2);
		double a = radLat1 - radLat2;
		double b = rad(lng1) - rad(lng2);
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
				+ Math.cos(radLat1) * Math.cos(radLat2)
				* Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS;
		s = Math.round(s * 10000) / 10000;
		return (long) s;
	}
	
	/**
	 * 根据IP获取经纬度坐标
	 * @param ip
	 * @return
	 */
	public static double[] getLngAndLat(String ip){
		
		double[] points = new double[2];
		String url = "https://api.map.baidu.com/location/ip?ak="+BAIDUMAP_API_AK+"&coor=bd09ll&ip=";
		String result = IPAddressUtils.getResult(url+ip, "", "UTF-8");
		if(result!=null){
			JSONObject obj = JSONObject.fromObject(result);
			if(obj.getInt("status")==0){
				JSONObject point = obj.getJSONObject("content").getJSONObject("point");
				points[0]=point.getDouble("x");
				points[1]=point.getDouble("y");
			}
		}
		return points;
	}
	
	/**
	 * 获取两个IP的物理距离
	 * @param ip
	 * @param ip1
	 * @return
	 */
	public static long getDistanceByIP(String ip, String ip1){
		
		double[] pointA = getLngAndLat(ip);
		double[] pointB = getLngAndLat(ip1);
		System.out.println(JSONArray.fromObject(pointA)+" -- "+JSONArray.fromObject(pointB));
		long distance = getDistance(pointA[0], pointA[1], pointB[0], pointB[1]);
		return distance;
	}

	
	public static void main(String[] args) {

		System.out.println("距离为:" + getDistanceByIP("110.85.1.217", "120.33.216.52"));
	}

}
