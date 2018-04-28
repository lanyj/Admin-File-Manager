package cn.lanyj.am.authentication;

public class AuthenticationUser {
	
	private Object object;
	private Level level;
	
	public AuthenticationUser() {
	}
	
	public AuthenticationUser(Level level, Object obj) {
		this.level = level;
		this.object = obj;
	}
	
	public void setLevel(Level level) {
		this.level = level;
	}
	
	public Level getLevel() {
		return level;
	}
	
	public void setObject(Object object) {
		this.object = object;
	}
	
	public Object getObject() {
		return object;
	}
	
}
