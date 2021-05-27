package com.qy.game.model;

/**
 * 
 * 惠福天下支付实体类
 *
 */
public class KFTSMPayMode {
	private String Account;
	private String NotifyUrl;
	private String TradeNo;
	private int Price;
	private String PayType;
	private String ZfbAccount;
	private int ReturnAmount;
	private String ProductID;
	private String ProductName;
	
	public KFTSMPayMode(){}
	
	public KFTSMPayMode(String account, String notifyUrl, String tradeNo,
			int price, String payType, String zfbAccount, int returnAmount,
			String productID, String productName) {
		super();
		Account = account;
		NotifyUrl = notifyUrl;
		TradeNo = tradeNo;
		Price = price;
		PayType = payType;
		ZfbAccount = zfbAccount;
		ReturnAmount = returnAmount;
		ProductID = productID;
		ProductName = productName;
	}
	
	public String getAccount() {
		return Account;
	}
	public void setAccount(String account) {
		Account = account;
	}
	public String getNotifyUrl() {
		return NotifyUrl;
	}
	public void setNotifyUrl(String notifyUrl) {
		NotifyUrl = notifyUrl;
	}
	public String getTradeNo() {
		return TradeNo;
	}
	public void setTradeNo(String tradeNo) {
		TradeNo = tradeNo;
	}
	public int getPrice() {
		return Price;
	}
	public void setPrice(int price) {
		Price = price;
	}
	public String getPayType() {
		return PayType;
	}
	public void setPayType(String payType) {
		PayType = payType;
	}
	public String getZfbAccount() {
		return ZfbAccount;
	}
	public void setZfbAccount(String zfbAccount) {
		ZfbAccount = zfbAccount;
	}
	public int getReturnAmount() {
		return ReturnAmount;
	}
	public void setReturnAmount(int returnAmount) {
		ReturnAmount = returnAmount;
	}
	public String getProductID() {
		return ProductID;
	}
	public void setProductID(String productID) {
		ProductID = productID;
	}
	public String getProductName() {
		return ProductName;
	}
	public void setProductName(String productName) {
		ProductName = productName;
	}
	
	
}
