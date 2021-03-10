package com.salesmanager.shop.model.messages;

import java.io.Serializable;

public class UserPayload implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private String email;
	
	UserPayload()
	{
		
	}
	
	public UserPayload(String[] arr)
	{
		name=arr[0];
		email=arr[1];
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	
	

}
