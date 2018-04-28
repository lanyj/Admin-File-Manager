package cn.lanyj.am.web.api.domin;

public class RestRetDomin {
	private boolean success;
	private String msg;
	private Object value;

	public boolean isSuccess() {
		return success;
	}
	public RestRetDomin setSuccess(boolean success) {
		this.success = success;
		return this;
	}
	public String getMsg() {
		return msg;
	}
	public RestRetDomin setMsg(String msg) {
		this.msg = msg;
		return this;
	}
	public Object getValue() {
		return value;
	}
	public RestRetDomin setValue(Object value) {
		this.value = value;
		return this;
	}
}
