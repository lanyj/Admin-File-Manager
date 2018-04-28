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

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import cn.lanyj.am.orm.domin.jsonhelper.JsonTimestampDeserializer;
import cn.lanyj.am.orm.domin.jsonhelper.JsonTimestampSerializer;

@Entity
public class Message extends UUIDDomin {
	
	private User from;
	private User to;
	private Timestamp createTime;
	private Timestamp readTime;
	private String content;
	
	@Id
	@GeneratedValue(generator="UUID")
	@GenericGenerator(name="UUID", strategy="org.hibernate.id.UUIDGenerator")
	@Column(name="uuid", updatable=false, nullable=false, unique=true)
	public String getUUID() {
		return uuid;
	}
	@ManyToOne(targetEntity=User.class, cascade= {CascadeType.MERGE})
	@JoinColumn(name="f_uuid")
	public User getFrom() {
		return from;
	}
	public void setFrom(User from) {
		this.from = from;
	}
	@ManyToOne(targetEntity=User.class, cascade= {CascadeType.MERGE})
	@JoinColumn(name="t_uuid")
	public User getTo() {
		return to;
	}
	public void setTo(User to) {
		this.to = to;
	}

	@JsonSerialize(using=JsonTimestampSerializer.class)
	@JsonDeserialize(using=JsonTimestampDeserializer.class)
	@Column(name="createTime", insertable=false, updatable=false, columnDefinition=" datetime default now() ")
	public Timestamp getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	
	@JsonSerialize(using=JsonTimestampSerializer.class)
	@JsonDeserialize(using=JsonTimestampDeserializer.class)
	public Timestamp getReadTime() {
		return readTime;
	}
	public void setReadTime(Timestamp readTime) {
		this.readTime = readTime;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	@Override
	public boolean equals(Object obj) {
		if(obj == null) {
			return false;
		}
		if(this.hashCode() == obj.hashCode()) {
			if(obj instanceof Message) {
				return ((Message) obj).getUUID().equals(uuid);
			}
		}
		return false;
	}
	@Override
	public int hashCode() {
		return uuid.hashCode();
	}
}
