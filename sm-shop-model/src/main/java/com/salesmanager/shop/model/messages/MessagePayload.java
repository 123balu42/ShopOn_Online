package com.salesmanager.shop.model.messages;

import java.io.Serializable;

import com.salesmanager.core.model.message.Message;

public class MessagePayload implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    
	private Long msgId;
	
	//private HashMap<ParticipantType,com.salesmanager.core.model.user.User> users=new HashMap<>();
	//List<com.salesmanager.core.model.user.User> users=new ArrayList<>();
	private Long senderId;
	private Long recieverId;
		
	private String Msg;
	
	private int ack;
	
	private int type;
	
	public MessagePayload()
	{
		
	}
	public MessagePayload(Message message)
	
	{
this.msgId=message.getMsgId();
		
		//private HashMap<ParticipantType,com.salesmanager.core.model.user.User> users=new HashMap<>();
		//List<com.salesmanager.core.model.user.User> users=new ArrayList<>();
		this.senderId=message.getSender().getId();
		this.recieverId=message.getReciever().getId();
			
		this.Msg=message.getMessage();
		
		this.ack=message.getAck();
		
		this.type=message.getType();
	}

	public Long getMsgId() {
		return msgId;
	}

	public void setMsgId(Long msgId) {
		this.msgId = msgId;
	}

	

	public Long getSenderId() {
		return senderId;
	}

	public void setSenderId(Long senderId) {
		this.senderId = senderId;
	}

	public Long getRecieverId() {
		return recieverId;
	}

	public void setRecieverId(Long recieverId) {
		this.recieverId = recieverId;
	}

	public String getMsg() {
		return Msg;
	}

	public void setMsg(String msg) {
		Msg = msg;
	}

	public int getAck() {
		return ack;
	}

	public void setAck(int ack) {
		this.ack = ack;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	public void setByMessage(Message message)
	{
		this.msgId=message.getMsgId();
		
			
		this.senderId=message.getSender().getId();
		this.recieverId=message.getReciever().getId();
			
		this.Msg=message.getMessage();
		
		this.ack=message.getAck();
		
		this.type=message.getType();

	}
	

}
