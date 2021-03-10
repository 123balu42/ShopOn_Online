package com.salesmanager.shop.admin.model.authtoken;

// Class to store authentication token
public class AuthToken {
	private String auth_token;

	public AuthToken() {
		
	}
	
	public AuthToken(String auth_token) {
		super();
		this.auth_token = auth_token;
	}


	public String getAuth_token() {
		return auth_token;
	}

	public void setAuth_token(String auth_token) {
		this.auth_token = auth_token;
	}
	
}