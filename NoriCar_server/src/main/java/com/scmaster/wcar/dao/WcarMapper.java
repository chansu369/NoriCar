package com.scmaster.wcar.dao;

import java.util.ArrayList;

import com.scmaster.wcar.provide.Provide;
import com.scmaster.wcar.user.User;

public interface WcarMapper {

	public int insertUser(User user);
	
	public User loginUser(User user);
	
	public int updateToken(User user);
	
	public User selectUser(User user);
	
	public int insertProvide(Provide provide);
	
	public ArrayList<Provide> selectAll();
	
	public int updateProvide(Provide provide);
	
	public Provide selectPush(Provide provide);
	
	public Provide checkRequest(int usernum);
	
	public int deleteProvide(Provide provide);
}
