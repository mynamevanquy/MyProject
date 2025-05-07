package com.my.myproject.jwt;

import java.io.Serializable;

public class JWTAuthResponse implements Serializable {
	  /**
	 * 
	 */
	private static final long serialVersionUID = 837973411518731862L;
	private String accessToken;
	  private String typeToken;
	  

	    public String getTypeToken() {
		return typeToken;
	}

	public void setTypeToken(String typeToken) {
		this.typeToken = typeToken;
	}

		public String getAccessToken() {
	        return accessToken;
	    }

	    public void setAccessToken(String accessToken) {
	        this.accessToken = accessToken;
	    }
}
