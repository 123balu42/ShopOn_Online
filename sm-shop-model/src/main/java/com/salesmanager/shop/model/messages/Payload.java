package com.salesmanager.shop.model.messages;

import java.io.Serializable;

public class Payload implements Serializable {
	private Long id;
	private String email;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
}
