package cn.lanyj.am.web.api;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.validator.routines.EmailValidator;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.lanyj.am.authentication.Authentication;
import cn.lanyj.am.authentication.AuthenticationUser;
import cn.lanyj.am.authentication.Authorization;
import cn.lanyj.am.authentication.CurrentUser;
import cn.lanyj.am.authentication.Level;
import cn.lanyj.am.orm.dao.FileHibernateDAO;
import cn.lanyj.am.orm.dao.UserHibernateDAO;
import cn.lanyj.am.orm.domin.User;
import cn.lanyj.am.web.api.domin.RegisterUserDomin;
import cn.lanyj.am.web.api.domin.RestRetDomin;

@RestController
@RequestMapping(value="/api/user")
public class UserAPI {

	@Autowired
	UserHibernateDAO dao;
	
	@Autowired
	FileHibernateDAO fileDAO;
	
	@RequestMapping(value="/me")
	public RestRetDomin me(HttpServletRequest request) {
		RestRetDomin ret = new RestRetDomin();
		Authentication authentication = Authentication.getAuthentication(request.getRemoteHost());
		if(authentication == null) {
			return ret.setSuccess(false);
		}
		return ret.setSuccess(true).setValue(authentication.getUser().getObject());
	}
	
	@RequestMapping(value="/register")
	public RestRetDomin register(@RequestBody RegisterUserDomin _user) {
		Session session = dao.beginTransaction();
		
		RestRetDomin ret = testForRegister(_user);
		if(ret.isSuccess()) {
			User user = new User();
			user.setUsername(_user.getUsername());
			user.setPassword(_user.getPassword());
			user.setEmail(_user.getEmail());
			
			Serializable id = dao.save(session, user);
			user.setUUID(id.toString());
			ret.setSuccess(true).setMsg("注册成功！").setValue(user);
		}
		dao.commit(session);
		return ret;
	}
	
	@RequestMapping(value="/register/test")
	public RestRetDomin testForRegister(@RequestBody RegisterUserDomin user) {
		Session session = dao.beginTransaction();
		
		RestRetDomin ret = new RestRetDomin();
		String username = user.getUsername();
		String password = user.getPassword();
		String email = user.getEmail();
		if(username == null || username.trim().length() < 6) {
			dao.commit(session);
			return ret.setSuccess(false).setMsg("用户名长度至少六位！");
		}

		if(dao.findUserByUsername(session, username) != null) {
			dao.commit(session);
			return ret.setSuccess(false).setMsg("用户名已存在！");
		}
		if(password == null || password.trim().length() < 6) {
			dao.commit(session);
			return ret.setSuccess(false).setMsg("密码长度至少六位！");
		}
		if(email == null || email.trim().length() < 1 || !EmailValidator.getInstance(true).isValid(email)) {
			dao.commit(session);
			return ret.setSuccess(false).setMsg("邮件格式不对！");
		}
		dao.commit(session);
		return ret.setSuccess(true);
	}
	
	@RequestMapping(value="/login")
	public RestRetDomin login(@RequestParam("username") String username, @RequestParam("password") String password, HttpServletRequest request) {
		Session session = dao.beginTransaction();
		RestRetDomin ret = new RestRetDomin();
		User u = dao.findUserByUsernameAndPassword(session, username, password);
		if(u != null) {
			u.setLdate(new Timestamp(System.currentTimeMillis()));
			dao.update(session, u);
			AuthenticationUser authenticationUser = new AuthenticationUser(Level.USER, u);
			new Authentication(request.getRemoteHost(), authenticationUser);
			ret.setSuccess(true).setMsg("登录成功！").setValue(u);
			
			dao.commit(session);
			return ret;
		}
		ret.setSuccess(false).setMsg("用户名或密码错误！");

		dao.commit(session);
		return ret;
	}
	
	@Authorization(level=Level.USER)
	@RequestMapping(value="/logout")
	public RestRetDomin logout(@CurrentUser User user, HttpServletRequest request) {
		Authentication.removeAuthentication(request.getRemoteHost());
		return new RestRetDomin().setSuccess(true);
	}
	
	@RequestMapping(value="/count")
	public RestRetDomin count() {
		Session session = dao.beginTransaction();
		RestRetDomin ret = new RestRetDomin();
		long total = dao.findCount(session, User.class);
		long join = dao.findLatelyJoinUserCount(session);
		dao.commit(session);
		return ret.setSuccess(true).setValue(new long[] {total, join});
	}
	
}
