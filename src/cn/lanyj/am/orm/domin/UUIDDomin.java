package cn.lanyj.am.orm.domin;

public class UUIDDomin {
	protected String uuid;

	public String getUUID() {
		return uuid;
	}

	public void setUUID(String uuid) {
		this.uuid = uuid;
	}
	
	@Override
	public int hashCode() {
		if(uuid == null) {
			return super.hashCode();
		}
		return uuid.hashCode();
	}
}
