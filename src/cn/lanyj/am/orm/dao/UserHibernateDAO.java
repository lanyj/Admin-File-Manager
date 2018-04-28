package cn.lanyj.am.orm.dao;

import java.util.List;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import cn.lanyj.am.orm.domin.User;


@Transactional
@Repository
@PropertySource({ "classpath:sql.properties" })
public class UserHibernateDAO extends BaseHibernateDAO<User> {
	
	@Autowired
	Environment env;
	
	final static String FIND_USER_BY_USERNAME = "FIND_USER_BY_USERNAME";
	final static String FIND_USER_BY_USERNAME_AND_PASSWORD = "FIND_USER_BY_USERNAME_AND_PASSWORD";
	final static String FIND_USER_LATELY_JOIN_COUNT = "FIND_USER_LATELY_JOIN_COUNT";
	
	public User findUserByUsername(Session session, String username) {
		List<User> list = find(session, env.getProperty(FIND_USER_BY_USERNAME), username);
		if(list != null && list.size() == 1) {
			return list.get(0);
		}
		return null;
	}
	
	public User findUserByUsernameAndPassword(Session session, String username, String password) {
		List<User> list = find(session, env.getProperty(FIND_USER_BY_USERNAME_AND_PASSWORD), username, password);
		if(list != null && list.size() == 1) {
			return list.get(0);
		}
		return null;
	}
	
	public long findLatelyJoinUserCount(Session session) {
		List<?> l = find(session, env.getProperty(FIND_USER_LATELY_JOIN_COUNT));
		if(l != null && l.size() == 1) {
			return (Long) l.get(0);
		}
		return 0;
	}
	
}
