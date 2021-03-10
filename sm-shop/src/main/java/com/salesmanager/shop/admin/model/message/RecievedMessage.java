package com.salesmanager.shop.admin.model.message;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RecievedMessage{
	
	@JsonProperty
	private int Ack;
	@JsonProperty
	private long MsgId;
	@JsonProperty
	private String ToId;
	@JsonProperty
	private String FromId;
	@JsonProperty
	private String Msg;
	@JsonProperty
	private int Type;
	public long getMsgId() {
		return MsgId;
	}
	public void setMsgId(long MsgId) {
		this.MsgId = MsgId;
	}
	public String getToId() {
		return ToId;
	}
	public void setToId(String ToId) {
		this.ToId = ToId;
	}
	public String getFromId() {
		return FromId;
	}
	public void setFromId(String FromId) {
		this.FromId = FromId;
	}
	public String getMsg() {
		return Msg;
	}
	public void setMsg(String Msg) {
		this.Msg = Msg;
	}
	public int getAck() {
		return Ack;
	}
	public void setAck(int Ack) {
		this.Ack = Ack;
	}
	public int getType() {
		return Type;
	}
	public void setType(int Type) {
		this.Type = Type;
	}
	
	
}