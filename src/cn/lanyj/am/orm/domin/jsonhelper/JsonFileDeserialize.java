package cn.lanyj.am.orm.domin.jsonhelper;

import java.io.IOException;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import cn.lanyj.am.orm.dao.FileHibernateDAO;
import cn.lanyj.am.orm.domin.File;

public class JsonFileDeserialize extends JsonDeserializer<File> {

	@Autowired
	FileHibernateDAO dao;
	
	@Override
	public File deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		String uuid = p.getValueAsString();
		if(uuid == null) {
			return null;
		}
		Session session = dao.beginTransaction();
		File file = dao.get(session, File.class, uuid);
		dao.commit(session);
		return file;
	}
	
}