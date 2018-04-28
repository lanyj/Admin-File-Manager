package cn.lanyj.am.web.api;

import java.sql.Timestamp;
import java.util.List;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.lanyj.am.authentication.Authorization;
import cn.lanyj.am.authentication.CurrentUser;
import cn.lanyj.am.authentication.Level;
import cn.lanyj.am.orm.dao.MessageHibernateDAO;
import cn.lanyj.am.orm.dao.UserHibernateDAO;
import cn.lanyj.am.orm.domin.Message;
import cn.lanyj.am.orm.domin.User;
import cn.lanyj.am.web.api.domin.RestRetDomin;
import cn.lanyj.am.web.api.domin.WriteMessageDomin;

@RestController
@RequestMapping(value="/api/message")
public class MessageAPI {
	
	@Autowired
	MessageHibernateDAO dao;
	
	@Autowired
	UserHibernateDAO userDao;

	@Authorization(level=Level.USER)
	@RequestMapping(value="/inbox")
	public RestRetDomin inbox(@CurrentUser User user,
			@RequestParam(name="pageNo", defaultValue="0", required=false) int pageNo,
			@RequestParam(name="pageSize", defaultValue="10", required=false) int pageSize) {
		Session session = dao.beginTransaction();
		RestRetDomin ret = new RestRetDomin();
		List<Message> messages = dao.toMe(session, user, pageNo, pageSize);
		long total = dao.countToMe(session, user);
		dao.commit(session);
		return ret.setSuccess(true).setValue(messages).setMsg("" + total);
	}
	
	@Authorization(level=Level.USER)
	@RequestMapping(value="/outbox")
	public RestRetDomin outbox(@CurrentUser User user,
			@RequestParam(name="pageNo", defaultValue="0", required=false) int pageNo,
			@RequestParam(name="pageSize", defaultValue="10", required=false) int pageSize) {
		Session session = dao.beginTransaction();
		RestRetDomin ret = new RestRetDomin();
		List<Message> messages = dao.fromMe(session, user, pageNo, pageSize);
		long total = dao.countFromMe(session, user);
		dao.commit(session);
		return ret.setSuccess(true).setValue(messages).setMsg("" + total);
	}
	
	@Authorization(level=Level.USER)
	@RequestMapping(value="/inbox/unread")
	public RestRetDomin inboxUnread(@CurrentUser User user,
			@RequestParam(name="pageNo", defaultValue="0", required=false) int pageNo,
			@RequestParam(name="pageSize", defaultValue="10", required=false) int pageSize) {
		Session session = dao.beginTransaction();
		RestRetDomin ret = new RestRetDomin();
		List<Message> messages = dao.toMeUnread(session, user, pageNo, pageSize);
		long total = dao.countToMeUnread(session, user);
		dao.commit(session);
		return ret.setSuccess(true).setValue(messages).setMsg("" + total);
	}
	
	@Authorization(level=Level.USER)
	@RequestMapping(value="/outbox/unread")
	public RestRetDomin outboxUnread(@CurrentUser User user,
			@RequestParam(name="pageNo", defaultValue="0", required=false) int pageNo,
			@RequestParam(name="pageSize", defaultValue="10", required=false) int pageSize) {
		Session session = dao.beginTransaction();
		RestRetDomin ret = new RestRetDomin();
		List<Message> messages = dao.fromMeUnread(session, user, pageNo, pageSize);
		long total = dao.countFromMeUnread(session, user);
		dao.commit(session);
		return ret.setSuccess(true).setValue(messages).setMsg("" + total);
	}
	
	@Authorization(level=Level.USER)
	@RequestMapping(value="/inbox/unread-count")
	public RestRetDomin inboxUnreadCount(@CurrentUser User user) {
		Session session = dao.beginTransaction();
		RestRetDomin ret = new RestRetDomin();
		long count = dao.countToMeUnread(session, user);
		dao.commit(session);
		return ret.setSuccess(true).setValue(count);
	}
	
	@Authorization(level=Level.USER)
	@RequestMapping(value="/outbox/unread-count")
	public RestRetDomin outboxUnreadCount(@CurrentUser User user) {
		Session session = dao.beginTransaction();
		RestRetDomin ret = new RestRetDomin();
		long count = dao.countFromMeUnread(session, user);
		dao.commit(session);
		return ret.setSuccess(true).setValue(count);
	}
	
	@Authorization(level=Level.USER)
	@RequestMapping(value="/write")
	public RestRetDomin write(@CurrentUser User user, @RequestBody WriteMessageDomin message) {
		Session session = dao.beginTransaction();
		RestRetDomin ret = new RestRetDomin();
		String toUsername = message.getTo().trim();
		User to = userDao.findUserByUsername(session, toUsername);
		if(to == null) {
			dao.commit(session);
			return ret.setSuccess(false).setMsg("用户名不存在！");
		}
		Message m = new Message();
		m.setContent(message.getContent());
		m.setFrom(user);
		m.setTo(to);
		dao.save(session, m);
		dao.commit(session);
		return ret.setSuccess(true);
	}
	
	
	@Authorization(level=Level.USER)
	@RequestMapping(value="/write/test")
	public RestRetDomin testUsername(@RequestParam(name="username") String username) {
		RestRetDomin ret = new RestRetDomin();
		Session session = userDao.beginTransaction();
		if(userDao.findUserByUsername(session, username) != null) {
			userDao.commit(session);
			return ret.setSuccess(true);
		}
		userDao.commit(session);
		return ret.setSuccess(false);
	}
	
	
	@Authorization(level=Level.USER)
	@RequestMapping(value="/read/{uuid}")
	public RestRetDomin read(@CurrentUser User user, @PathVariable(name="uuid") String uuid) {
		RestRetDomin ret = new RestRetDomin();
		Session session = dao.beginTransaction();
		Message message = dao.get(session, Message.class, uuid);
		if(message == null || !message.getTo().equals(user)) {
			dao.commit(session);
			return ret.setSuccess(false).setMsg("信息未找到！");
		}
		message.setReadTime(new Timestamp(System.currentTimeMillis()));
		dao.commit(session);
		return ret.setSuccess(true);
	}
	
	@Authorization(level=Level.USER)
	@RequestMapping(value="/delete/{uuid}")
	public RestRetDomin delete(@CurrentUser User user, @PathVariable(name="uuid") String uuid) {
		RestRetDomin ret = new RestRetDomin();
		Session session = dao.beginTransaction();
		Message message = dao.get(session, Message.class, uuid);
		if(message == null || !message.getTo().equals(user)) {
			dao.commit(session);
			return ret.setSuccess(false).setMsg("信息未找到！");
		}
		dao.delete(session, message);
		dao.commit(session);
		return ret.setSuccess(true);
	}
	
	@Authorization(level=Level.USER)
	@RequestMapping(value="/count")
	public RestRetDomin count(@CurrentUser User user) {
		Session session = dao.beginTransaction();
		RestRetDomin ret = new RestRetDomin();
		long total = dao.countToMe(session, user);
		long count = dao.countToMeUnread(session, user);
		dao.commit(session);
		return ret.setSuccess(true).setValue(new long[] {total, count});
	}
}
