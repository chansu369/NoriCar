package com.scmaster.wcar.provide;

public class Provide {

	 private int pronum;
	 private int usernum;
	 private int pay;
	 private double startx;
	 private double starty;
	 private double destx;
	 private double desty;
	 private int request;
	public Provide() {
		super();
	}
	public Provide(int pronum, int usernum, int pay, double startx, double starty, double destx, double desty,
			int request) {
		super();
		this.pronum = pronum;
		this.usernum = usernum;
		this.pay = pay;
		this.startx = startx;
		this.starty = starty;
		this.destx = destx;
		this.desty = desty;
		this.request = request;
	}
	public int getPronum() {
		return pronum;
	}
	public void setPronum(int pronum) {
		this.pronum = pronum;
	}
	public int getUsernum() {
		return usernum;
	}
	public void setUsernum(int usernum) {
		this.usernum = usernum;
	}
	public int getPay() {
		return pay;
	}
	public void setPay(int pay) {
		this.pay = pay;
	}
	public double getStartx() {
		return startx;
	}
	public void setStartx(double startx) {
		this.startx = startx;
	}
	public double getStarty() {
		return starty;
	}
	public void setStarty(double starty) {
		this.starty = starty;
	}
	public double getDestx() {
		return destx;
	}
	public void setDestx(double destx) {
		this.destx = destx;
	}
	public double getDesty() {
		return desty;
	}
	public void setDesty(double desty) {
		this.desty = desty;
	}
	public int getRequest() {
		return request;
	}
	public void setRequest(int request) {
		this.request = request;
	}
	@Override
	public String toString() {
		return "Provide [pronum=" + pronum + ", usernum=" + usernum + ", pay=" + pay + ", startx=" + startx
				+ ", starty=" + starty + ", destx=" + destx + ", desty=" + desty + ", request=" + request + "]";
	}

	 
}