package com.qy.game.model;

public class User {

	private long id;
	private String account;
	private String name;
	private String sex;
	private String headimg;
	private String area;
	private String lv;
	private int roomcard;
	private int coins;
	private int score;
	private String openid;
	private String unionid;
	private String uuid;
	private String ip;
	private String platform;
	private Long gulidId;
	private int win;
	private int isAi;
	
	public User(long id, String account, String name, String sex,
			String headimg, String lv, int roomcard, int coins, int score,
			String openid,String unionid ,String uuid, String ip, String area,String platform, long gulidId) {
		super();
		this.id = id;
		this.account = account;
		this.name = name;
		this.sex = sex;
		this.headimg = headimg;
		this.lv = lv;
		this.roomcard = roomcard;
		this.coins = coins;
		this.score = score;
		this.openid = openid;
		this.unionid=unionid;
		this.uuid = uuid;
		this.ip = ip;
		this.area = area;
		this.platform=platform;
		this.gulidId=gulidId;
	}
	
	public User( String account, String name, String sex,
			String headimg, String lv, int roomcard, int coins, int score,
			String openid,String unionid ,String uuid, String ip, String area,String platform, long gulidId,int isAi) {
		super();
		this.account = account;
		this.name = name;
		this.sex = sex;
		this.headimg = headimg;
		this.lv = lv;
		this.roomcard = roomcard;
		this.coins = coins;
		this.score = score;
		this.openid = openid;
		this.unionid=unionid;
		this.uuid = uuid;
		this.ip = ip;
		this.area = area;
		this.platform=platform;
		this.gulidId=gulidId;
		this.isAi = isAi;
	}
	
	
	public int getIsAi() {
		return isAi;
	}

	public void setIsAi(int isAi) {
		this.isAi = isAi;
	}

	public String getUnionid() {
		return unionid;
	}

	public void setUnionid(String unionid) {
		this.unionid = unionid;
	}

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getHeadimg() {
		return headimg;
	}
	public void setHeadimg(String headimg) {
		this.headimg = headimg;
	}
	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getLv() {
		return lv;
	}
	public void setLv(String lv) {
		this.lv = lv;
	}
	public int getRoomcard() {
		return roomcard;
	}
	public void setRoomcard(int roomcard) {
		this.roomcard = roomcard;
	}
	public int getCoins() {
		return coins;
	}
	public void setCoins(int coins) {
		this.coins = coins;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public Long getGulidId() {
		return gulidId;
	}

	public void setGulidId(Long gulidId) {
		this.gulidId = gulidId;
	}

	public int getWin() {
		return win;
	}

	public void setWin(int win) {
		this.win = win;
	}
	
	
	
}
