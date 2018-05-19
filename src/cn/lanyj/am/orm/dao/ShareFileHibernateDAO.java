package cn.lanyj.am.orm.dao;

import java.io.Serializable;
import java.sql.Timestamp;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import cn.lanyj.am.orm.domin.ShareFile;

@Transactional
@Repository
@PropertySource({ "classpath:sql.properties" })
public class ShareFileHibernateDAO extends BaseHibernateDAO<ShareFile> {
	@Autowired
	Environment env;
	
	public ShareFile get(Session session, Serializable uuid) {
		ShareFile file = get(session, ShareFile.class, uuid);
		if(file != null) {
			if(file.getExpire().before(new Timestamp(System.currentTimeMillis()))) {
				session.delete(file);
				return null;
			} else {
				return file;
			}
		}
		return null;
	}
	
}
