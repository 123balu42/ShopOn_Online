package com.salesmanager.shop.admin.model.message;

public class MessageInfo {
	private String messageTxt;
	private String sender;
	private String receiver;
	
	public MessageInfo() {
		
	}

	public MessageInfo(String messageTxt, String sender, String receiver) {
		super();
		this.messageTxt = messageTxt;
		this.sender = sender;
		this.receiver = receiver;
	}

	public String getMessageTxt() {
		return messageTxt;
	}

	public void setMessageTxt(String messageTxt) {
		this.messageTxt = messageTxt;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}
	
}
