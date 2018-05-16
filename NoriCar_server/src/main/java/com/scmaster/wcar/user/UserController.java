package com.scmaster.wcar.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.scmaster.wcar.dao.WcarDAO;


@Controller
public class UserController {
	
	@Autowired
	WcarDAO dao;
	
	@ResponseBody
	@RequestMapping(value = "insertUser", method = RequestMethod.POST)
	public String insertUser(User user) {
		
		int result = dao.insertUser(user);
		String msg = "";
		if(result != 1){
			msg = "User Insert Fail";
		}else{
			msg = "User Insert Success";
		}
		
		return msg;
	}
	
	@ResponseBody
	@RequestMapping(value = "loginUser", method = RequestMethod.POST)
	public String loginUser(User user) {

		User sUser = null;

		sUser = dao.loginUser(user);
		
		if(sUser != null){
			dao.updateToken(user);
		}
		
		Gson gson = new Gson();
		String result = gson.toJson(sUser);
		
		return result;
	}
	
	@ResponseBody
	@RequestMapping(value = "selectUser", method = RequestMethod.POST)
	public String selectUser(User user) {

		User sUser = null;
		
		sUser = dao.selectUser(user);
		
		Gson gson = new Gson();
		String result = gson.toJson(sUser);
		
		return result;
	}
	
}
