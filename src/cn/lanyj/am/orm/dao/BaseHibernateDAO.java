package cn.lanyj.am.orm.dao;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;


public class BaseHibernateDAO<T> {

	@Autowired
	private SessionFactory sessionFactory;
		
	protected Session openSession() {
		return sessionFactory.openSession();
	}
	
	public Session beginTransaction() {
		Session session = openSession();
		session.beginTransaction();
		return session;
	}
	
	public void commit(Session session) {
		try {
			session.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			session.close();
		}
	}
	
	public T get(Session session, Class<T> clazz, Serializable id) {
		T tmp = session.get(clazz, id);
		return tmp;
	}

	public Serializable save(Session session, T entity) {
		Serializable id = session.save(entity);
		return id;
	}

	public void update(Session session, T entity) {
		session.update(entity);
	}
	
	public void refresh(Session session, T entity) {
		session.refresh(entity);
	}

	public void delete(Session session, T entity) {
		session.delete(entity);
	}

	public int delete(Session session, Class<T> clazz, Serializable id) {
		int tmp = session.createQuery("delete " + clazz.getSimpleName() + " en where en.id = ?0")
			.setParameter("0", id).executeUpdate();	
		return tmp;
	}
	
	public void saveOrUpdate(Session session, T entity) {
		session.saveOrUpdate(entity);
	}

	public List<T> findAll(Session session, Class<T> clazz) {
		return find(session, "select en from " + clazz.getSimpleName() + " en");
	}

	public long findCount(Session session, Class<T> clazz) {
		List<?> l = find(session, "select count(*) from " + clazz.getSimpleName());
		if(l != null && l.size() == 1) {
			return (Long) l.get(0);
		}
		return 0;
	}
	
	@SuppressWarnings("unchecked")
	public List<T> find(Session session, String hql) {
		List<T> list = session.createQuery(hql).list();
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public List<T> find(Session session, String hql, Object...params) {
		Query<T> query = session.createQuery(hql);
		for(int i = 0; i < params.length; i++) {
			query.setParameter(i + "", params[i]);
		}
		List<T> list = query.getResultList();
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public List<T> findByPage(Session session, String hql, int pageNo, int pageSize) {
		List<T> list = session.createQuery(hql).setFirstResult((pageNo) * pageSize).setMaxResults(pageSize).list();
		return list;
	}

	@SuppressWarnings("unchecked")
	public List<T> findByPage(Session session, String hql, int pageNo, int pageSize, Object...params) {
		Query<T> query = session.createQuery(hql);
		for(int i = 0; i < params.length; i++) {
			query.setParameter(i + "", params[i]);
		}
		List<T> list = query.setFirstResult((pageNo) * pageSize).setMaxResults(pageSize).list();
		return list;
	}
	
}

//package cn.lanyj.am.orm.dao;
//
//import java.io.Serializable;
//import java.util.List;
//
//import org.hibernate.Session;
//import org.hibernate.SessionFactory;
//import org.hibernate.query.Query;
//import org.springframework.beans.factory.annotation.Autowired;
//
//public class BaseHibernateDAO implements BaseDAO {
//
//	@Autowired
//	private SessionFactory sessionFactory;
//	
//	public void setSessionFactory(SessionFactory sessionFactory) {
//		this.sessionFactory = sessionFactory;
//	}
//	
//	public SessionFactory getSessionFactory() {
//		return sessionFactory;
//	}
//	
//	@Override
//	public <T> T get(Class<T> clazz, Serializable id) {
//		Session session = getSessionFactory().getCurrentSession();
//		session.beginTransaction();
//		T tmp = session.get(clazz, id);
//		session.getTransaction().commit();
//		return tmp;
//	}
//
//	@Override
//	public <T> Serializable save(T entity) {
//		Session session = getSessionFactory().getCurrentSession();
//		session.beginTransaction();
//		Serializable id = session.save(entity);
//		session.getTransaction().commit();
//		return id;
//	}
//
//	@Override
//	public <T> void update(T entity) {
//		Session session = getSessionFactory().getCurrentSession();
//		session.beginTransaction();
//		session.update(entity);
//		session.getTransaction().commit();
//	}
//
//	@Override
//	public <T> void delete(T entity) {
//		Session session = getSessionFactory().getCurrentSession();
//		session.beginTransaction();
//		session.delete(entity);
//		session.getTransaction().commit();
//	}
//
//	@Override
//	public <T> int delete(Class<T> clazz, Serializable id) {
//		Session session = getSessionFactory().getCurrentSession();
//		session.beginTransaction();
//		int tmp = session.createQuery("delete " + clazz.getSimpleName() + " en where en.id = ?0")
//			.setParameter("0", id).executeUpdate();	
//		session.getTransaction().commit();
//		return tmp;
//	}
//
//	@Override
//	public <T> List<T> findAll(Class<T> clazz) {
//		return find("select en from " + clazz.getSimpleName() + " en");
//	}
//
//	@Override
//	public <T> long findCount(Class<T> clazz) {
//		List<?> l = find("select count(*) from " + clazz.getSimpleName());
//		if(l != null && l.size() == 1) {
//			return (Long) l.get(0);
//		}
//		return 0;
//	}
//	
//	@SuppressWarnings("unchecked")
//	public <T> List<T> find(String hql) {
//		Session session = getSessionFactory().getCurrentSession();
//		session.beginTransaction();
//		List<T> list = session.createQuery(hql).list();
//		session.getTransaction().commit();
//		return list;
//	}
//	
//	@SuppressWarnings("unchecked")
//	public <T> List<T> find(String hql, Object...params) {
//		Session session = getSessionFactory().getCurrentSession();
//		session.beginTransaction();
//		Query<T> query = session.createQuery(hql);
//		for(int i = 0; i < params.length; i++) {
//			query.setParameter(i + "", params[i]);
//		}
//		List<T> list = query.getResultList();
//		session.getTransaction().commit();
//		return list;
//	}
//	
//	@SuppressWarnings("unchecked")
//	public <T> List<T> findByPage(String hql, int pageNo, int pageSize) {
//		Session session = getSessionFactory().getCurrentSession();
//		session.beginTransaction();
//		List<T> list = session.createQuery(hql).setFirstResult((pageNo) * pageSize).setMaxResults(pageSize).list();
//		session.getTransaction().commit();
//		return list;
//	}
//
//	@SuppressWarnings("unchecked")
//	public <T> List<T> findByPage(String hql, int pageNo, int pageSize, Object...params) {
//		Session session = getSessionFactory().getCurrentSession();
//		session.beginTransaction();
//		Query<T> query = session.createQuery(hql);
//		for(int i = 0; i < params.length; i++) {
//			query.setParameter(i + "", params[i]);
//		}
//		List<T> list = query.setFirstResult((pageNo) * pageSize).setMaxResults(pageSize).list();
//		session.getTransaction().commit();
//		return list;
//	}
//	
//}