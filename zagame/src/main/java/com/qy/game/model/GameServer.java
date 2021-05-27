package com.qy.game.model;

public class GameServer {

	private long id;
	private String name;
	private String ip;
	private int port;
	private int reconnCount;
	private boolean isConnection;
	private int roomNums;
	
	public GameServer(String name, String ip, int port) {
		super();
		this.name = name;
		this.ip = ip;
		this.port = port;
		this.reconnCount = 3;
		this.isConnection = true;
	}
	
	
	public int getRoomNums() {
		return roomNums;
	}

	public void setRoomNums(int roomNums) {
		this.roomNums = roomNums;
	}

	public void setId(long id) {
		this.id = id;
	}
	public long getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public int getReconnCount() {
		return reconnCount;
	}
	public void setReconnCount(int reconnCount) {
		this.reconnCount = reconnCount;
	}
	public boolean isConnection() {
		return isConnection;
	}
	public void setConnection(boolean isConnection) {
		this.isConnection = isConnection;
	}
	
}
