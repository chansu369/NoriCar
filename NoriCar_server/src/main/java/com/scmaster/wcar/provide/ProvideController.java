package com.scmaster.wcar.provide;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.google.gson.Gson;
import com.scmaster.wcar.dao.WcarDAO;

@Controller
public class ProvideController {

	@Autowired
	WcarDAO dao;
	
	
	
	@ResponseBody
	@RequestMapping(value = "insertProvide", method = RequestMethod.POST)
	public String insertProvide(Provide provide) {
		
		System.out.println(provide);
		
		int result = dao.insertProvide(provide);
		String msg = "";
		if(result != 1){
			msg = "Provide info Insert Fail";
		}else{
			msg = "Provide info Success";
		}
		
		return msg;
	}
	
	@ResponseBody
	@RequestMapping(value = "selectAll", method = RequestMethod.GET)
	public String selectMember() {
		
		ArrayList<Provide> list = dao.selectAll();
		Gson gson = new Gson();
		
		String result = gson.toJson(list);
		
		return result;
	}
	
	
	@ResponseBody
	@RequestMapping(value = "updateProvide", method = RequestMethod.POST)
	public String updateProvide(Provide provide) {
		
		System.out.println(provide);
		
		int result = dao.updateProvide(provide);
		String msg = "";
		if(result != 1){
			msg = "Request Fail";
		}else{
			msg = "Request Success";
		}
		
		System.out.println(result);
		System.out.println(msg);
		return msg;
	}
	
	@ResponseBody
	@RequestMapping(value = "checkRequest", method = RequestMethod.POST)
	public String checkRequest(Provide provide) {
		
		Provide f_provide = dao.checkRequest(provide.getUsernum());
		String msg = "";
		try{
			if(f_provide.getRequest() != 1){
				msg = "No Request";
			}else{
				msg = "Request select";
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		System.out.println(msg);
		return msg;
	}
	
	@ResponseBody
	@RequestMapping(value = "deleteProvide", method = RequestMethod.POST)
	public String deleteProvide(Provide provide) {
				
		int result = dao.deleteProvide(provide);
		String msg = "";
		if(result != 1){
			msg = "Delete Fail";
		}else{
			msg = "Delete Success";
		}
		
		return msg;
	}
	
}
