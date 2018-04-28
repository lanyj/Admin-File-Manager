package cn.lanyj.am.orm.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import cn.lanyj.am.orm.domin.File;
import cn.lanyj.am.orm.domin.User;
import cn.lanyj.am.web.api.service.FileService;


@Transactional
@Repository
@PropertySource({ "classpath:sql.properties" })
public class FileHibernateDAO extends BaseHibernateDAO<File> {

	@Autowired
	Environment env;
	
	final static String FIND_FILE_ROOT = "FIND_FILE_ROOT";
	final static String FIND_TO_USER_FILE = "FIND_TO_USER_FILE";
	final static String FIND_TO_USER_FILE_COUNT = "FIND_TO_USER_FILE_COUNT";
	
	public File root(Session session, User user) {
		List<File> list = find(session, env.getProperty(FIND_FILE_ROOT), user);
		if(list == null || list.size() == 0) {
			File file = new File();
			FileService.initUserDir(user, file);
			Serializable id = save(session, file);
			file.setUUID(id.toString());
			return file;
		}
		return list.get(0);
	}
	
	public List<File> list(File file) {
		File parent = file.getParent();
		if(parent == null) {
			List<File> files = new ArrayList<>();
			files.add(file);
			return files;
		}
		return null;
	}
	
	public List<File> listFileByParent(Session session, String parent) {
		File file = get(session, File.class, parent);
		if(file != null && file.isDirectory()) {
			return file.getChildren();
		}
		return null;
	}
	
	public long countForCurrentUser(Session session, User user) {
		List<?> l = find(session, env.getProperty(FIND_TO_USER_FILE_COUNT), user);
		if(l != null && l.size() == 1) {
			return (Long) l.get(0);
		}
		return 0;
	}
	
	public List<File> list(Session session, User user, int pageNo, int pageSize) {
		return findByPage(session, env.getProperty(FIND_TO_USER_FILE), pageNo, pageSize, user);
	}
	
}
