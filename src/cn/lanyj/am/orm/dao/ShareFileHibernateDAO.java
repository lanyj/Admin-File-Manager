package cn.lanyj.am.orm.dao;

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
	
	
	
}
