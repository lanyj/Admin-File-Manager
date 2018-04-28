package cn.lanyj.am.authentication;

import java.util.Hashtable;
import java.util.concurrent.CopyOnWriteArrayList;

public class Authentication {
	public static final String AUTHEN_USER = "AUTHEN_USER";
	private static CopyOnWriteArrayList<Authentication> authentications = new CopyOnWriteArrayList<>();
	private static Thread DEAMON;
	
	static {
		DEAMON = new Thread(new Runnable() {
			@Override
			public void run() {
				while(true) {
					try {
						for(int i = authentications.size() - 1; i >= 0; i--) {
							authentications.get(i).counters.next();
						}
						Thread.sleep(60 * 1000);
					} catch (Exception e) {
					}
				}
			}
		});
		DEAMON.start();
	}
	
	public static void main(String[] args) {
		new Authentication("1", null);
		new Authentication("2", null);
		new Authentication("3", null);
		new Authentication("4", null);
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				int step = 0;
				while(true) {
					System.out.println("1 " + Authentication.isAuthentication("1"));
					if(step % 2 == 0) {
						System.out.println("2 " + Authentication.isAuthentication("2"));
					}
					if(step % 6 == 0) {
						System.out.println("6 " + Authentication.isAuthentication("6"));
					}
					if(step % 8 == 0) {
						System.out.println("8 " + Authentication.isAuthentication("8"));
					}
					step++;
					try {
						Thread.sleep(1000 * 10);
					} catch (InterruptedException e) {
					}
				}
			}
		}).start();;
	}
	
	private String key = "";
	private Counter counters = new Counter(this);
	private Hashtable<String, Object> session = new Hashtable<String, Object>();
	private AuthenticationUser user;
	
	public Authentication(String key, AuthenticationUser user) {
		this.key = key;
		this.user = user;
		
		if(!Authentication.isAuthentication(key)) {
			authentications.add(this);
		}
	}
	
	public String getHost() {
		return key;
	}
	
	public AuthenticationUser getUser() {
		return user;
	}
	
	public static void removeAuthentication(String key) {
		for(int i = authentications.size() - 1; i >= 0; i--) {
			if(authentications.get(i).key.equals(key)) {
				authentications.remove(i);
			}
		}
	}

	public static synchronized Authentication getAuthentication(String key) {
		for(Authentication authentication : authentications) {
			if(authentication.key.equals(key)) {
				authentication.counters.reset();
				return authentication;
			}
		}
		return null;
	}
	
	public static synchronized boolean isAuthentication(String key) {
		for(Authentication authentication : authentications) {
			if(authentication.key.equals(key)) {
				authentication.counters.reset();
				return true;
			}
		}
		return false;
	}
	
	public void setKey(String key) {
		this.key = key;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Authentication) {
			return ((Authentication) obj).key.equals(key);
		}
		return false;
	}
	
	public Object put(String key, Object value) {
		return session.put(key, value);
	}
	
	public Hashtable<String, Object> getSession() {
		return session;
	}
	
	class Counter {
		static final long LIVE_TIME = 15 * 60 * 1000;
		long liveTime = 0;
		Authentication auth;
		
		public Counter(Authentication auth) {
			this.auth = auth;
			liveTime = System.currentTimeMillis();
		}
		
		public void next() {
			if(System.currentTimeMillis() - liveTime > LIVE_TIME) {
				Authentication.removeAuthentication(this.auth.key);
			}
		}
		
		protected void reset() {
			liveTime = System.currentTimeMillis();
		}
	}
	
}
