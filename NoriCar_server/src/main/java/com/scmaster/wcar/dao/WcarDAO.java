package com.scmaster.wcar.dao;

import java.util.ArrayList;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.scmaster.wcar.provide.Provide;
import com.scmaster.wcar.user.User;

@Repository
public class WcarDAO {
	
	@Autowired
	SqlSession sqlSession;
	
	public int insertUser(User user){
		WcarMapper mapper = sqlSession.getMapper(WcarMapper.class);
		
		int result = 0;
		try {
			result = mapper.insertUser(user);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public User loginUser(User user){
		WcarMapper mapper = sqlSession.getMapper(WcarMapper.class);
		User find_user = null;
		
		try {
			find_user = mapper.loginUser(user);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return find_user;
	}
	
	public int updateToken(User user){
		WcarMapper mapper = sqlSession.getMapper(WcarMapper.class);
		int result = 0;
		
		try {
			result = mapper.updateToken(user);
		}
		
		catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	
	public User selectUser(User user){
		User find_user = null;
		WcarMapper mapper = sqlSession.getMapper(WcarMapper.class);
		
		try {
			find_user = mapper.selectUser(user);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return find_user;
	}
	
	
	public int insertProvide(Provide provide){
		WcarMapper mapper = sqlSession.getMapper(WcarMapper.class);
		
		int result = 0;
		try {
			result = mapper.insertProvide(provide);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public ArrayList<Provide> selectAll(){
		ArrayList<Provide> list = null;
		WcarMapper mapper = sqlSession.getMapper(WcarMapper.class);
		
		try {
			list = mapper.selectAll();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public int updateProvide(Provide provide){
		WcarMapper mapper = sqlSession.getMapper(WcarMapper.class);
		
		int result = 0;
		try {
			result = mapper.updateProvide(provide);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public Provide selectPush(Provide provide){
		WcarMapper mapper = sqlSession.getMapper(WcarMapper.class);
		Provide f_provide = null;
		
		try {
			f_provide = mapper.selectPush(provide);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return f_provide;
	}
	
	
	public Provide checkRequest(int usernum){
		WcarMapper mapper = sqlSession.getMapper(WcarMapper.class);
		Provide f_provide = null;
		
		try {
			f_provide = mapper.checkRequest(usernum);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return f_provide;
	}
	
	public int deleteProvide(Provide provide){
		WcarMapper mapper = sqlSession.getMapper(WcarMapper.class);
		
		int result = 0;
		try {
			result = mapper.deleteProvide(provide);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
}
