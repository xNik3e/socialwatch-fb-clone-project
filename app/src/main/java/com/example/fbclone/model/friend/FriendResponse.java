package com.example.fbclone.model.friend;

import com.google.gson.annotations.SerializedName;

public class FriendResponse{
	public FriendResponse(String message, int status) {
		this.message = message;
		this.status = status;
	}

	@SerializedName("result")
	private Result result;

	@SerializedName("message")
	private String message;

	@SerializedName("status")
	private int status;

	public void setResult(Result result){
		this.result = result;
	}

	public Result getResult(){
		return result;
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