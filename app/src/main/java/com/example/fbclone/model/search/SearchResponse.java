package com.example.fbclone.model.search;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class SearchResponse{

	@SerializedName("message")
	private String message;

	@SerializedName("user")
	private List<User> user;

	@SerializedName("status")
	private int status;

	public SearchResponse(String message, int status) {
		this.message = message;
		this.status = status;
	}

	public void setMessage(String message){
		this.message = message;
	}

	public String getMessage(){
		return message;
	}

	public void setUser(List<User> user){
		this.user = user;
	}

	public List<User> getUser(){
		return user;
	}

	public void setStatus(int status){
		this.status = status;
	}

	public int getStatus(){
		return status;
	}
}