package com.qy.game.model;

public class GameCircleMember {
	
	private int id;
	private int circleId;
	private int userID;
	private String userCode;
	private double userHp;
	private String superUserCode;
	private int userRole;
	private int profitRatio;
	private double profitBalance;
	private double profitBalanceAll;
	private int isAdmin;
	private char isUse;
	private String platform;
	private long createUser;
	private long modifyUser;
	private String gmtCreate;
	private String gmtModified;
	private String isDelete;
	private String sqlMemberIds;
	
	
	
	
	
	public GameCircleMember(int id, int circleId, int userID, String userCode, double userHp, String superUserCode,
			int userRole, int profitRatio, double profitBalance, double profitBalanceAll, int isAdmin, char isUse,
			String platform, long createUser, long modifyUser, String gmtCreate, String gmtModified, String isDelete,
			String sqlMemberIds) {
		super();
		this.id = id;
		this.circleId = circleId;
		this.userID = userID;
		this.userCode = userCode;
		this.userHp = userHp;
		this.superUserCode = superUserCode;
		this.userRole = userRole;
		this.profitRatio = profitRatio;
		this.profitBalance = profitBalance;
		this.profitBalanceAll = profitBalanceAll;
		this.isAdmin = isAdmin;
		this.isUse = isUse;
		this.platform = platform;
		this.createUser = createUser;
		this.modifyUser = modifyUser;
		this.gmtCreate = gmtCreate;
		this.gmtModified = gmtModified;
		this.isDelete = isDelete;
		this.sqlMemberIds = sqlMemberIds;
	}
	public GameCircleMember() {
		super();
	}
	public GameCircleMember(int circleId, int userID, String userCode, double userHp, String superUserCode,
			int userRole, String platform, long createUser, long modifyUser, String gmtCreate, String gmtModified) {
		super();
		this.circleId = circleId;
		this.userID = userID;
		this.userCode = userCode;
		this.userHp = userHp;
		this.superUserCode = superUserCode;
		this.userRole = userRole;
		this.platform = platform;
		this.createUser = createUser;
		this.modifyUser = modifyUser;
		this.gmtCreate = gmtCreate;
		this.gmtModified = gmtModified;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getCircleId() {
		return circleId;
	}
	public void setCircleId(int circleId) {
		this.circleId = circleId;
	}
	public int getUserID() {
		return userID;
	}
	public void setUserID(int userID) {
		this.userID = userID;
	}
	public String getUserCode() {
		return userCode;
	}
	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}
	public double getUserHp() {
		return userHp;
	}
	public void setUserHp(double userHp) {
		this.userHp = userHp;
	}
	public String getSuperUserCode() {
		return superUserCode;
	}
	public void setSuperUserCode(String superUserCode) {
		this.superUserCode = superUserCode;
	}
	public int getUserRole() {
		return userRole;
	}
	public void setUserRole(int userRole) {
		this.userRole = userRole;
	}
	public int getProfitRatio() {
		return profitRatio;
	}
	public void setProfitRatio(int profitRatio) {
		this.profitRatio = profitRatio;
	}
	public double getProfitBalance() {
		return profitBalance;
	}
	public void setProfitBalance(double profitBalance) {
		this.profitBalance = profitBalance;
	}
	public double getProfitBalanceAll() {
		return profitBalanceAll;
	}
	public void setProfitBalanceAll(double profitBalanceAll) {
		this.profitBalanceAll = profitBalanceAll;
	}
	public int getIsAdmin() {
		return isAdmin;
	}
	public void setIsAdmin(int isAdmin) {
		this.isAdmin = isAdmin;
	}
	public char getIsUse() {
		return isUse;
	}
	public void setIsUse(char isUse) {
		this.isUse = isUse;
	}
	public String getPlatform() {
		return platform;
	}
	public void setPlatform(String platform) {
		this.platform = platform;
	}
	public long getCreateUser() {
		return createUser;
	}
	public void setCreateUser(long createUser) {
		this.createUser = createUser;
	}
	public long getModifyUser() {
		return modifyUser;
	}
	public void setModifyUser(long modifyUser) {
		this.modifyUser = modifyUser;
	}
	public String getGmtCreate() {
		return gmtCreate;
	}
	public void setGmtCreate(String gmtCreate) {
		this.gmtCreate = gmtCreate;
	}
	public String getGmtModified() {
		return gmtModified;
	}
	public void setGmtModified(String gmtModified) {
		this.gmtModified = gmtModified;
	}
	public String getIsDelete() {
		return isDelete;
	}
	public void setIsDelete(String isDelete) {
		this.isDelete = isDelete;
	}
	public String getSqlMemberIds() {
		return sqlMemberIds;
	}
	public void setSqlMemberIds(String sqlMemberIds) {
		this.sqlMemberIds = sqlMemberIds;
	}
	
	
	

}
