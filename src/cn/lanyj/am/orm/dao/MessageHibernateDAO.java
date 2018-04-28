package cn.lanyj.am.orm.dao;

import java.util.List;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import cn.lanyj.am.orm.domin.Message;
import cn.lanyj.am.orm.domin.User;


@Transactional
@Repository
@PropertySource({ "classpath:sql.properties" })
public class MessageHibernateDAO extends BaseHibernateDAO<Message> {
	
	static final String FIND_TO_USER_MESSAGE = "FIND_TO_USER_MESSAGE";
	static final String FIND_FROM_USER_MESSAGE = "FIND_FROM_USER_MESSAGE";
	static final String FIND_TO_USER_UNREAD_MESSAGE = "FIND_TO_USER_UNREAD_MESSAGE";
	static final String FIND_FROM_USER_UNREAD_MESSAGE = "FIND_FROM_USER_UNREAD_MESSAGE";
	
	static final String FIND_TO_USER_MESSAGE_COUNT = "FIND_TO_USER_MESSAGE_COUNT";
	static final String FIND_FROM_USER_MESSAGE_COUNT = "FIND_FROM_USER_MESSAGE_COUNT";
	static final String FIND_TO_USER_UNREAD_MESSAGE_COUNT = "FIND_TO_USER_UNREAD_MESSAGE_COUNT";
	static final String FIND_FROM_USER_UNREAD_MESSAGE_COUNT = "FIND_FROM_USER_UNREAD_MESSAGE_COUNT";
	
	@Autowired
	Environment env;
	
	public List<Message> toMe(Session session, User user) {
		return find(session, env.getProperty(FIND_TO_USER_MESSAGE), user);
	}
	
	public List<Message> fromMe(Session session, User user) {
		return find(session, env.getProperty(FIND_FROM_USER_MESSAGE), user);
	}
	
	public List<Message> toMeUnread(Session session, User user) {
		return find(session, env.getProperty(FIND_TO_USER_UNREAD_MESSAGE), user);
	}
	
	public List<Message> fromMeUnread(Session session, User user) {
		return find(session, env.getProperty(FIND_FROM_USER_UNREAD_MESSAGE), user);
	}
	
	public List<Message> toMe(Session session, User user, int pageNo, int pageSize) {
		return findByPage(session, env.getProperty(FIND_TO_USER_MESSAGE), pageNo * pageSize, pageSize, user);
	}
	
	public List<Message> fromMe(Session session, User user, int pageNo, int pageSize) {
		return findByPage(session, env.getProperty(FIND_FROM_USER_MESSAGE), pageNo * pageSize, pageSize, user);
	}
	
	public List<Message> toMeUnread(Session session, User user, int pageNo, int pageSize) {
		return findByPage(session, env.getProperty(FIND_TO_USER_UNREAD_MESSAGE), pageNo * pageSize, pageSize, user);
	}
	
	public List<Message> fromMeUnread(Session session, User user, int pageNo, int pageSize) {
		return findByPage(session, env.getProperty(FIND_FROM_USER_UNREAD_MESSAGE), pageNo * pageSize, pageSize, user);
	}
	
	public long countToMe(Session session, User user) {
		List<?> l = find(session, env.getProperty(FIND_TO_USER_MESSAGE_COUNT), user);
		if(l != null && l.size() == 1) {
			return (Long) l.get(0);
		}
		return 0;
	}
	
	public long countFromMe(Session session, User user) {
		List<?> l = find(session, env.getProperty(FIND_FROM_USER_MESSAGE_COUNT), user);
		if(l != null && l.size() == 1) {
			return (Long) l.get(0);
		}
		return 0;
	}
	
	public long countToMeUnread(Session session, User user) {
		List<?> l = find(session, env.getProperty(FIND_TO_USER_UNREAD_MESSAGE_COUNT), user);
		if(l != null && l.size() == 1) {
			return (Long) l.get(0);
		}
		return 0;
	}
	
	public long countFromMeUnread(Session session, User user) {
		List<?> l = find(session, env.getProperty(FIND_FROM_USER_UNREAD_MESSAGE_COUNT), user);
		if(l != null && l.size() == 1) {
			return (Long) l.get(0);
		}
		return 0;
	}
}
