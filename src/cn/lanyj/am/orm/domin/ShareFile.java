package cn.lanyj.am.orm.domin;

import java.sql.Timestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import cn.lanyj.am.orm.domin.jsonhelper.JsonTimestampDeserializer;
import cn.lanyj.am.orm.domin.jsonhelper.JsonTimestampSerializer;


@Entity
public class ShareFile extends UUIDDomin {
	private User owner;
	private String password;
	private File file;
	private Timestamp expire;
	
	@Id
	@GeneratedValue(generator="UUID")
	@GenericGenerator(name="UUID", strategy="org.hibernate.id.UUIDGenerator")
	@Column(name="uuid", updatable=false, nullable=false, unique=true)
	public String getUUID() {
		return uuid;
	}

	@ManyToOne(targetEntity=User.class, cascade= {CascadeType.MERGE})
	@JoinColumn(name="u_uuid", referencedColumnName="uuid")
	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	@JsonIgnore
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@ManyToOne(targetEntity=File.class, cascade= {CascadeType.MERGE})
	@JoinColumn(name="f_uuid", referencedColumnName="uuid")
	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}
	
	@JsonSerialize(using=JsonTimestampSerializer.class)
	@JsonDeserialize(using=JsonTimestampDeserializer.class)
	@Column(name="uploadTime", insertable=false, updatable=false)
	public Timestamp getExpire() {
		return expire;
	}

	public void setExpire(Timestamp expire) {
		this.expire = expire;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null) {
			return false;
		}
		if(obj instanceof ShareFile) {
			if(obj.hashCode() == this.hashCode()) {
				if(uuid != null) {
					return uuid.equals(((ShareFile) obj).uuid);
				} else {
					return false;
				}
			}
		}
		return super.equals(obj);
	}
}
