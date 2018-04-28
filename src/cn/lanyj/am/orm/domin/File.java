package cn.lanyj.am.orm.domin;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import cn.lanyj.am.orm.domin.jsonhelper.JsonTimestampDeserializer;
import cn.lanyj.am.orm.domin.jsonhelper.JsonTimestampSerializer;


@Entity
public class File extends UUIDDomin {
	private String name;
	private String path;
	private File parent;
	private boolean directory;
	private User uploader;
	private Timestamp uploadTime;
	private List<File> children;
	
	@Id
	@GeneratedValue(generator="UUID")
	@GenericGenerator(name="UUID", strategy="org.hibernate.id.UUIDGenerator")
	@Column(name="uuid", updatable=false, nullable=false, unique=true)
	public String getUUID() {
		return uuid;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@JsonIgnore
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@ManyToOne(targetEntity=File.class, cascade= {CascadeType.MERGE})
	@JoinTable(name="file_parent_map", joinColumns= {@JoinColumn(name="c_uuid", referencedColumnName="uuid")}
			, inverseJoinColumns= {@JoinColumn(name="p_uuid", referencedColumnName="uuid")})
	public File getParent() {
		return parent;
	}

	public void setParent(File parent) {
		this.parent = parent;
	}

	@ManyToOne(targetEntity=User.class, cascade= {CascadeType.MERGE})
	@JoinColumn(name="u_uuid", referencedColumnName="uuid")
	public User getUploader() {
		return uploader;
	}

	public void setUploader(User uploader) {
		this.uploader = uploader;
	}
	
	@JsonSerialize(using=JsonTimestampSerializer.class)
	@JsonDeserialize(using=JsonTimestampDeserializer.class)
	@Column(name="uploadTime", insertable=false, updatable=false, columnDefinition=" datetime default now() ")
	public Timestamp getUploadTime() {
		return uploadTime;
	}

	public void setUploadTime(Timestamp uploadTime) {
		this.uploadTime = uploadTime;
	}
	
	@JsonIgnore
	@OneToMany(targetEntity=File.class, mappedBy="parent", fetch=FetchType.LAZY, cascade= {CascadeType.MERGE})
	public List<File> getChildren() {
		return children;
	}

	public boolean isDirectory() {
		return directory;
	}

	public void setDirectory(boolean directory) {
		this.directory = directory;
	}

	public void setChildren(List<File> children) {
		this.children = children;
	}
	@Override
	public boolean equals(Object obj) {
		if(obj == null) {
			return false;
		}
		if(this.hashCode() == obj.hashCode()) {
			if(obj instanceof File) {
				return ((File) obj).getUUID().equals(uuid);
			}
		}
		return super.equals(obj);
	}
}