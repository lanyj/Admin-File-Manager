package cn.lanyj.am.orm.domin.jsonhelper;

import java.io.IOException;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import cn.lanyj.am.orm.dao.UserHibernateDAO;
import cn.lanyj.am.orm.domin.User;

public class JsonUserDeserialize extends JsonDeserializer<User> {

	@Autowired
	UserHibernateDAO dao;
	
	@Override
	public User deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		String uuid = p.getValueAsString();
		if(uuid == null) {
			return null;
		}
		Session session = dao.beginTransaction();
		User user = dao.get(session, User.class, uuid);
		dao.commit(session);
		return user;
	}

}
