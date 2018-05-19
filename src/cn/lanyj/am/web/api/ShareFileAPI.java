package cn.lanyj.am.web.api;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;

import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cn.lanyj.am.authentication.Authorization;
import cn.lanyj.am.authentication.CurrentUser;
import cn.lanyj.am.authentication.Level;
import cn.lanyj.am.orm.dao.FileHibernateDAO;
import cn.lanyj.am.orm.dao.ShareFileHibernateDAO;
import cn.lanyj.am.orm.domin.File;
import cn.lanyj.am.orm.domin.ShareFile;
import cn.lanyj.am.orm.domin.User;
import cn.lanyj.am.web.api.domin.RestRetDomin;
import cn.lanyj.am.web.api.service.FileService;

/**
 * 暂时没有写
 * @author Aidi
 *
 */
@RestController
@RequestMapping(value="/share/")
public class ShareFileAPI {
	
	@Autowired
	ShareFileHibernateDAO dao;
	
	@Autowired
	FileHibernateDAO fileDAO;
	
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
			@RequestParam(name="expire", required=false, defaultValue="7") int expire, 
			@RequestParam(name="password", required=false, defaultValue="") String password) {
		Session session = dao.beginTransaction();
		RestRetDomin ret = new RestRetDomin();
		File file = fileDAO.get(session, File.class, uuid);
		if(file == null) {
			dao.commit(session);
			return ret.setSuccess(false).setMsg("文件未找到！");
		}
		if(!file.getUploader().equals(user)) {
			dao.commit(session);
			return ret.setSuccess(false).setMsg("文件未找到！");
		}
		Timestamp timestamp = null;
		if(expire >= 3) {
			timestamp = new Timestamp(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * expire);
		}
		ShareFile shareFile = new ShareFile();
		shareFile.setExpire(timestamp);
		shareFile.setFile(file);
		shareFile.setOwner(user);
		shareFile.setPassword(password);
		Serializable id = dao.save(session, shareFile);
		shareFile.setUUID(id.toString());
		dao.commit(session);
		return ret.setSuccess(true).setValue(shareFile);
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
		Session session = dao.beginTransaction();
		ShareFile shareFile = dao.get(session, uuid);
		if(shareFile == null || !shareFile.getOwner().equals(user)) {
			dao.commit(session);
			return ret.setSuccess(false).setMsg("分享文件未找到！");
		}
		dao.delete(session, shareFile);
		dao.commit(session);
		return ret.setSuccess(true);
	}
	
	/**
	 * 获得uuid的对应条目
	 * @param uuid
	 * @return
	 */
	@RequestMapping(value="/get/{uuid}")
	public RestRetDomin get(@PathVariable(name="uuid", required=true) String uuid) {
		RestRetDomin ret = new RestRetDomin();
		Session session = dao.beginTransaction();
		ShareFile shareFile = dao.get(session, uuid);
		if(shareFile == null) {
			dao.commit(session);
			return ret.setSuccess(false).setMsg("分享文件未找到！");
		}
		dao.commit(session);
		return ret.setSuccess(true).setValue(shareFile);
	}
	
	@Authorization(level=Level.USER)
	@RequestMapping(value="/fork")
	public RestRetDomin fork(@CurrentUser User user, @RequestParam(name="uuid", required=true) String uuid, 
			@RequestParam(name="password", required=false, defaultValue="") String password) {
		RestRetDomin ret = new RestRetDomin();
		Session session = dao.beginTransaction();
		ShareFile shareFile = dao.get(session, uuid);
		if (shareFile == null) {
			dao.commit(session);
			return ret.setSuccess(false).setMsg("分享文件未找到！");
		}
		if(shareFile.getExpire().before(new Timestamp(System.currentTimeMillis()))) {
			dao.delete(session, shareFile);
			dao.commit(session);
			return ret.setSuccess(false).setMsg("分享文件未找到！");
		}
		if(shareFile.getPassword() != null && !shareFile.getPassword().equals(password)) {
			dao.commit(session);
			return ret.setSuccess(false).setMsg("密码不正确！");
		}
		File parent = fileDAO.root(session, user);

		String filename = shareFile.getFile().getName();
		File tmp = new File();
		tmp.setName(filename);
		tmp.setUploader(user);
		tmp.setDirectory(false);
		tmp.setParent(parent);

		Serializable id = fileDAO.save(session, tmp);
		tmp.setUUID(id.toString());
		tmp.setUploadTime(new Timestamp(System.currentTimeMillis()));

		ret = FileService.save(ret, tmp, shareFile.getFile());
		fileDAO.update(session, tmp);
		if (ret.isSuccess()) {
			dao.commit(session);
			return ret.setSuccess(true).setMsg("上传成功！").setValue(tmp);
		}
		dao.commit(session);
		return ret;
	}

	/**
	 * 下载
	 * @param uuid 分享条目的uuid
	 * @param password 分享条目的密码
	 * @return
	 */
	@RequestMapping(value="/download")
	public void download(@RequestParam(name="uuid", required=true) String uuid, 
			@RequestParam(name="password", required=false, defaultValue="") String password,
			HttpServletResponse response) {
		Session session = dao.beginTransaction();
		ShareFile shareFile = dao.get(session, uuid);
		if (shareFile == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			dao.commit(session);
			return;
		}
		if(shareFile.getExpire().before(new Timestamp(System.currentTimeMillis()))) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			dao.delete(session, shareFile);
			dao.commit(session);
			return;
		}
		if(shareFile.getPassword() != null && !shareFile.getPassword().equals(password)) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			dao.commit(session);
			return;
		}
		File file = shareFile.getFile();
		String filename = file.getName();
		try {
			filename = URLEncoder.encode(file.getName(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
		}
		response.setHeader("Content-Disposition", "attachment; filename=" + filename);
		response.setContentType("Content-Type: application/octet-stream");
		byte[] buf = new byte[1024 * 64];
		BufferedInputStream bis = null;
		try {
			bis = new BufferedInputStream(new FileInputStream(new java.io.File(file.getPath())));
			int length = 0;
			while ((length = bis.read(buf)) != -1) {
				response.getOutputStream().write(buf, 0, length);
			}
			response.flushBuffer();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			response.setStatus(HttpServletResponse.SC_EXPECTATION_FAILED);
			try {
				bis.close();
				response.flushBuffer();
			} catch (IOException e) {
			}
		}
	}
}
