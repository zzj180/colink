package com.colink.bluetoothe;

public class CallLogBean {

	private int id;
	private String number;
	private String name;
	private int type;
	private long date;
	private long duration;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public long getDate() {
		return date;
	}
	public void setDate(long date) {
		this.date = date;
	}
	
	public long getDuration() {
		return duration;
	}
	public void setDuration(long duration) {
		this.duration = duration;
	}
	@Override
	public String toString() {
		return "CallLogBean [id=" + id + ", number=" + number + ", name="
				+ name + ", type=" + type + ", date=" + date + ", duration="
				+ duration + "]";
	}
}
