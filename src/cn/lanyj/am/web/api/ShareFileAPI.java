package cn.lanyj.am.web.api;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.lanyj.am.authentication.Authorization;
import cn.lanyj.am.authentication.CurrentUser;
import cn.lanyj.am.authentication.Level;
import cn.lanyj.am.orm.dao.ShareFileHibernateDAO;
import cn.lanyj.am.orm.domin.User;
import cn.lanyj.am.web.api.domin.RestRetDomin;

/**
 * 暂时没有写
 * @author Aidi
 *
 */
@RestController
@RequestMapping(value="/share-file/")
public class ShareFileAPI {
	
	@Autowired
	ShareFileHibernateDAO dao;
	
	/**
	 * 
	 * @param user
	 * @param uuid 文件的uuid
	 * @param exipre 过期时间，以天记
	 * @param password 分享密码
	 * @return
	 */
	@Authorization(level=Level.USER)
	@RequestMapping(value="/share")
	public RestRetDomin share(@CurrentUser User user,
			@RequestParam(name="uuid", required=true) String uuid, 
			@RequestParam(name="expire", required=false, defaultValue="7") int exipre, 
			@RequestParam(name="password", required=false, defaultValue="") String password) {
		RestRetDomin ret = new RestRetDomin();
		
		return ret;
	}
	
	/**
	 * 取消分享
	 * @param user
	 * @param uuid 分享的uuid
	 * @return
	 */
	@Authorization(level=Level.USER)
	@RequestMapping(value="/unshare/{uuid}")
	public RestRetDomin unShare(@CurrentUser User user, @PathVariable(name="uuid", required=true) String uuid) {
		RestRetDomin ret = new RestRetDomin();
		
		return ret;
	}
	
	/**
	 * 获得uuid的对应条目
	 * @param uuid
	 * @return
	 */
	@RequestMapping(value="/get/{uuid}")
	public RestRetDomin get(@PathVariable(name="uuid", required=true) String uuid) {
		RestRetDomin ret = new RestRetDomin();
		
		return ret;
	}

	/**
	 * 下载
	 * @param uuid 分享条目的uuid
	 * @param password 分享条目的密码
	 * @return
	 */
	@RequestMapping(value="/download")
	public RestRetDomin download(@RequestParam(name="uuid", required=true) String uuid, 
			@RequestParam(name="password", required=false, defaultValue="") String password,
			HttpServletResponse response) {
		RestRetDomin ret = new RestRetDomin();
		
		return ret;
	}
}
