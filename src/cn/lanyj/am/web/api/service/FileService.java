package cn.lanyj.am.web.api.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

import cn.lanyj.am.orm.domin.File;
import cn.lanyj.am.orm.domin.User;
import cn.lanyj.am.web.api.domin.RestRetDomin;

public class FileService {
	
//	final static String ROOT = System.getProperty("user.dir") + "/";
	final static String ROOT = "/tomcat/am/";
	
	public static RestRetDomin save(RestRetDomin ret, File file, MultipartFile mf) {
		User user = file.getUploader();
		String root = ROOT + user.getUUID() + "/" + file.getUploader().getUUID() + "/";
		try {
			java.io.File parent = new java.io.File(root);
			if(!parent.exists()) {
				parent.mkdirs();
			}
			file.setPath(root +  file.getUUID());
			mf.transferTo(new java.io.File(file.getPath()));
			return ret.setSuccess(true);
		} catch (IllegalStateException | IOException e) {
			e.printStackTrace();
			return ret.setSuccess(false).setMsg("服务器存储失败！");
		}
	}
	
	public static RestRetDomin initUserDir(User user, File file) {
		RestRetDomin ret = new RestRetDomin();
		String root = ROOT + user.getUUID() + "/";
		
		file.setUploader(user);
		file.setDirectory(true);
		file.setName("ROOT");
		file.setPath(root + user.getUUID() + "/");
		
		try {
			java.io.File parent = new java.io.File(root);
			parent.deleteOnExit();
			parent.mkdirs();
		} catch (Exception e) {
			ret.setSuccess(false).setMsg("初始化文件失败！");
			e.printStackTrace();
		}
		return ret.setSuccess(true);
	}
	
	public static void delete(File file) {
		try {
			new java.io.File(file.getPath()).deleteOnExit();
		} catch (Exception e) {
		}
	}
	
}
