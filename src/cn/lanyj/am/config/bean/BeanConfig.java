package cn.lanyj.am.config.bean;

import java.sql.Date;
import java.sql.Timestamp;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.SqlDateConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cn.lanyj.am.authentication.CurrentUserMethodArgumentResolver;
import cn.lanyj.am.orm.dao.FileHibernateDAO;
import cn.lanyj.am.orm.dao.UserHibernateDAO;


@Configuration
public class BeanConfig {
	static {
		ConvertUtils.register(new SqlDateConverter(new Date(System.currentTimeMillis())), java.util.Date.class);
        ConvertUtils.register(new SqlTimestampConverter(new Timestamp(System.currentTimeMillis())), java.sql.Timestamp.class);
	}
	
	@Bean
	public CurrentUserMethodArgumentResolver currentUserMethodArgumentResolver() {
		CurrentUserMethodArgumentResolver argumentResolver = new CurrentUserMethodArgumentResolver();
		return argumentResolver;
	}
	
	@Bean(name="userHibernateDAO")
	public UserHibernateDAO userHibernateDAO() {
		return new UserHibernateDAO();
	}
	
	@Bean(name="fileHibernateDAO")
	public FileHibernateDAO fileHibernateDAO() {
		return new FileHibernateDAO();
	}
	
}
