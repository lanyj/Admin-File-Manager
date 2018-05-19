package cn.lanyj.am.web.api;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import cn.lanyj.am.authentication.Authorization;
import cn.lanyj.am.authentication.CurrentUser;
import cn.lanyj.am.authentication.Level;
import cn.lanyj.am.orm.dao.FileHibernateDAO;
import cn.lanyj.am.orm.domin.File;
import cn.lanyj.am.orm.domin.User;
import cn.lanyj.am.web.api.domin.FileUpdateDomin;
import cn.lanyj.am.web.api.domin.RestRetDomin;
import cn.lanyj.am.web.api.service.FileService;

@RestController
@RequestMapping(value = "/api/file")
public class FileAPI {
	static final String NEW_DIRECTORY_NAME = "新建文件夹";
	static final String FILE_NOT_FOUND = "文件未找到！";
	static final String PARENT_NOT_DIRECTORY = "父目录不是文件夹！";
	
	@Autowired
	private FileHibernateDAO dao;

//	@Authorization(level = Level.USER)
//	@RequestMapping(value = "/list")
//	public RestRetDomin list(@CurrentUser User user,
//			@RequestParam(name = "parent", defaultValue = "", required = false) String parent) {
//		Session session = dao.beginTransaction();
//
//		RestRetDomin ret = new RestRetDomin();
//		if (parent.equals("")) {
//			File file = dao.root(session, user);
//			dao.commit(session);
//			return ret.setSuccess(true).setValue(file);
//		}
//		File file = dao.get(session, File.class, parent.trim());
//		if (file == null) {
//			dao.commit(session);
//			return ret.setSuccess(false).setMsg(FILE_NOT_FOUND);
//		}
//		if (!file.getUploader().equals(user)) {
//			dao.commit(session);
//			return ret.setSuccess(false).setMsg(FILE_NOT_FOUND);
//		}
//		if (!file.isDirectory()) {
//			dao.commit(session);
//			return ret.setSuccess(false).setMsg(PARENT_NOT_DIRECTORY);
//		}
//		List<File> files = file.getChildren();
//		dao.commit(session);
//		return ret.setSuccess(true).setValue(files);
//	}

	@Authorization(level = Level.USER)
	@RequestMapping(value = "/list")
	public RestRetDomin list(@CurrentUser User user,
			@RequestParam(name = "parent", defaultValue = "", required = false) String parent,
			@RequestParam(name="pageNo", defaultValue="0", required=false) int pageNo,
			@RequestParam(name="pageSize", defaultValue="10", required=false) int pageSize) {
		Session session = dao.beginTransaction();

		RestRetDomin ret = new RestRetDomin();
//		if (parent.equals("")) {
//			File file = dao.root(session, user);
//			dao.commit(session);
//			return ret.setSuccess(true).setValue(file);
//		}
//		File file = dao.get(session, File.class, parent.trim());
//		if (file == null) {
//			dao.commit(session);
//			return ret.setSuccess(false).setMsg(FILE_NOT_FOUND);
//		}
//		if (!file.getUploader().equals(user)) {
//			dao.commit(session);
//			return ret.setSuccess(false).setMsg(FILE_NOT_FOUND);
//		}
//		if (!file.isDirectory()) {
//			dao.commit(session);
//			return ret.setSuccess(false).setMsg(PARENT_NOT_DIRECTORY);
//		}
//		List<File> files = file.getChildren();
		List<File> files = dao.list(session, user, pageNo, pageSize);
		long totalSize = dao.countForCurrentUser(session, user);
		dao.commit(session);
		return ret.setSuccess(true).setValue(files).setMsg("" + totalSize);
	}
	
	@Authorization(level = Level.USER)
	@RequestMapping(value = "/upload")
	public RestRetDomin upload(@CurrentUser User user, @RequestParam(name = "parent", required = true) String _parent,
			@RequestParam(name = "file", required = true) MultipartFile file) {
		Session session = dao.beginTransaction();

		RestRetDomin ret = new RestRetDomin();
		File parent;
		if (_parent.trim().equals("")) {
			parent = dao.root(session, user);
		} else {
			parent = dao.get(session, File.class, _parent.trim());
		}
		if (parent == null) {
			dao.commit(session);
			return ret.setSuccess(false).setMsg(FILE_NOT_FOUND);
		}
		if (!parent.isDirectory()) {
			dao.commit(session);
			return ret.setSuccess(false).setMsg(PARENT_NOT_DIRECTORY);
		}

		String filename = file.getOriginalFilename();
		File tmp = new File();
		tmp.setName(filename);
		tmp.setUploader(user);
		tmp.setDirectory(false);
		tmp.setParent(parent);

		Serializable uuid = dao.save(session, tmp);
		tmp.setUUID(uuid.toString());
		tmp.setUploadTime(new Timestamp(System.currentTimeMillis()));

		ret = FileService.save(ret, tmp, file);
		dao.update(session, tmp);
		if (ret.isSuccess()) {
			dao.commit(session);
			return ret.setSuccess(true).setMsg("上传成功！").setValue(tmp);
		}

		dao.commit(session);
		return ret;
	}
	
	@Authorization(level=Level.USER)
	@RequestMapping(value="/mkdir")
	public RestRetDomin mkdir(@CurrentUser User user,
			@RequestParam(name="parent", defaultValue="", required=true) String _parent,
			@RequestParam(name="name", defaultValue=NEW_DIRECTORY_NAME, required=true) String name) {
		RestRetDomin ret = new RestRetDomin();
		if(name.trim().equals("")) {
			return ret.setSuccess(false).setMsg("请输入文件夹名称！");
		}
		Session session = dao.beginTransaction();
		File parent = dao.get(session, File.class, _parent);
		if(parent == null || (parent != null && !parent.getUploader().equals(user))) {
			dao.commit(session);
			return ret.setSuccess(false).setMsg(FILE_NOT_FOUND);
		}
		if(!parent.isDirectory()) {
			dao.commit(session);
			return ret.setSuccess(false).setMsg(PARENT_NOT_DIRECTORY);
		}
		for(File file : parent.getChildren()) {
			if(file.getName().equals(name)) {
				dao.commit(session);
				return ret.setSuccess(false).setMsg("文件夹已存在！");
			}
		}
		File file = new File();
		file.setName(name);
		file.setDirectory(true);
		file.setParent(parent);
		file.setUploader(user);
		Serializable id = dao.save(session, file);
		file.setUUID(id.toString());
		dao.commit(session);
		return ret.setSuccess(true).setValue(file);
	}
	
	@Authorization(level=Level.USER)
	@RequestMapping(value="/update")
	public RestRetDomin update(@CurrentUser User user, @RequestBody FileUpdateDomin fileUpdateDomin) {
		Session session = dao.beginTransaction();
		RestRetDomin ret = new RestRetDomin();
		String uuid = fileUpdateDomin.getUUID().trim();
		String parent = fileUpdateDomin.getParent().trim();
		
		File cur = dao.get(session, File.class, uuid);
		File par = dao.get(session, File.class, parent);
		
		if(cur == null || (cur != null && !cur.getUploader().equals(user)) || (par != null && !par.getUploader().equals(user))) {
			dao.commit(session);
			return ret.setSuccess(false).setMsg(FILE_NOT_FOUND);
		}
		if(cur.equals(par)) {
			dao.commit(session);
			return ret.setSuccess(false).setMsg("不能移动到当前文件下！");
		}
		if(cur.getParent() == null && par != null && !cur.equals(par)) {
			dao.commit(session);
			return ret.setSuccess(false).setMsg("根目录，不可移动！");
		}
		if(par != null && !par.isDirectory()) {
			dao.commit(session);
			return ret.setSuccess(false).setMsg(PARENT_NOT_DIRECTORY);
		}
		cur.setParent(par);
		dao.commit(session);
		return ret.setSuccess(true);
	}

	@Authorization(level = Level.USER)
	@RequestMapping(value = "/delete/{uuid}")
	public RestRetDomin delete(@CurrentUser User user, @PathVariable String uuid) {
		Session session = dao.beginTransaction();

		RestRetDomin ret = new RestRetDomin();
		File file = dao.get(session, File.class, uuid);
		if (file == null) {
			dao.commit(session);
			return ret.setSuccess(false).setMsg(FILE_NOT_FOUND);
		}
		if (!file.getUploader().equals(user)) {
			dao.commit(session);
			return ret.setSuccess(false).setMsg(FILE_NOT_FOUND);
		}
		FileService.delete(file);
		dao.delete(session, file);
		dao.commit(session);
		return ret.setSuccess(true);
	}

	@Authorization(level = Level.USER)
	@RequestMapping(value = "/download/{uuid}", produces = "application/zip")
	public void download(@CurrentUser User user, @PathVariable String uuid, HttpServletResponse response) {
		Session session = dao.beginTransaction();

		File file = dao.get(session, File.class, uuid);
		if (file == null) {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			dao.commit(session);
			return;
		}
		if (!file.getUploader().equals(user)) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			dao.commit(session);
			return;
		}
		if (file.isDirectory()) {
			response.setStatus(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
			dao.commit(session);
			return;
		}
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
	
	@Authorization(level=Level.USER)
	@RequestMapping(value="/count")
	public RestRetDomin count(@CurrentUser User user) {
		RestRetDomin ret = new RestRetDomin();
		Session session = dao.beginTransaction();
		long count = dao.countForCurrentUser(session, user);
		dao.commit(session);
		return ret.setSuccess(true).setValue(new long[] {count});
	}

}
