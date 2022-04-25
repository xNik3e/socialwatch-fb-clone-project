package com.example.fbclone.model;

import com.google.gson.annotations.SerializedName;

public class GeneralResponse{
	public GeneralResponse(String message, int status) {
		this.message = message;
		this.status = status;
	}

	@SerializedName("message")
	private String message;

	@SerializedName("status")
	private int status;

	@SerializedName("extra")
	private String extra;

	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}

	public void setMessage(String message){
		this.message = message;
	}

	public String getMessage(){
		return message;
	}

	public void setStatus(int status){
		this.status = status;
	}

	public int getStatus(){
		return status;
	}
}