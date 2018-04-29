package cn.lanyj.am.orm.domin;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import cn.lanyj.am.orm.domin.jsonhelper.JsonTimestampDeserializer;
import cn.lanyj.am.orm.domin.jsonhelper.JsonTimestampSerializer;


@Entity
public class User extends UUIDDomin {
	private String username;
	private String password;
	private String email;
	private Timestamp ldate;
	private Timestamp rdate;
	private List<Message> fromMessage = new ArrayList<>();
	private List<Message> toMessage = new ArrayList<>();
	private List<File> files = new ArrayList<>();
	
	@Id
	@GeneratedValue(generator="UUID")
	@GenericGenerator(name="UUID", strategy="org.hibernate.id.UUIDGenerator")
	@Column(name="uuid", updatable=false, nullable=false, unique=true)
	public String getUUID() {
		return uuid;
	}
	@Column(unique=true)
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	@JsonIgnore
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	@JsonSerialize(using=JsonTimestampSerializer.class)
	@JsonDeserialize(using=JsonTimestampDeserializer.class)
	@Column(name="rdate", insertable=false, updatable=false, columnDefinition=" datetime default now() ")
	public Timestamp getRdate() {
		return rdate;
	}
	public void setRdate(Timestamp rdate) {
		this.rdate = rdate;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	@JsonSerialize(using=JsonTimestampSerializer.class)
	@JsonDeserialize(using=JsonTimestampDeserializer.class)
	public Timestamp getLdate() {
		return ldate;
	}
	public void setLdate(Timestamp ldate) {
		this.ldate = ldate;
	}
	@JsonIgnore
	@OneToMany(targetEntity=Message.class, mappedBy="from", fetch=FetchType.LAZY, cascade= {CascadeType.MERGE})
	public List<Message> getFromMessage() {
		return fromMessage;
	}
	public void setFromMessage(List<Message> fromMessage) {
		this.fromMessage = fromMessage;
	}
	@JsonIgnore
	@OneToMany(targetEntity=Message.class, mappedBy="to", fetch=FetchType.LAZY, cascade= {CascadeType.MERGE})
	public List<Message> getToMessage() {
		return toMessage;
	}
	public void setToMessage(List<Message> toMessage) {
		this.toMessage = toMessage;
	}
	@JsonIgnore
	@OneToMany(targetEntity=File.class, mappedBy="uploader", fetch=FetchType.LAZY, cascade= {CascadeType.MERGE})
	public List<File> getFiles() {
		return files;
	}
	public void setFiles(List<File> files) {
		this.files = files;
	}
	@Override
	public boolean equals(Object obj) {
		if(obj == null) {
			return false;
		}
		if(this.hashCode() == obj.hashCode()) {
			if(obj instanceof User) {
				if(uuid != null) {
					return uuid.equals(((User) obj).uuid);
				} else {
					return false;
				}
			}
		}
		return super.equals(obj);
	}
//	public static void main(String[] args) {
//		try {
//			StandardServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
//			SessionFactory factory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
//			
//			Session session = factory.openSession();
//			session.beginTransaction();
//			
////			Query<User> query = session.createQuery("from User where username = ?0 and password = ?1", User.class);
////			query.setParameter("0", "u1").setParameter("1", "p1");
////			User u = query.getSingleResult();
////			System.out.println(u.getFiles().size());
////			User u = new User();
////			u.setUsername("u5");
////			u.setPassword("p5");
////			session.save(u);
////			
////			File f1 = new File();
////			f1.setUploader(u);
////			f1.setParent(null);
////			f1.setName("f1");
////			f1.setPath("path1");
////			session.save(f1);
////			
////			File f2 = new File();
////			f2.setUploader(u);
////			f2.setParent(f1);
////			f2.setName("f2");
////			f2.setPath("path2");
////			session.save(f2);
////			
////			System.out.println("File UUID: " + f1.getUUID());
//			Query<File> query = session.createQuery("from File f", File.class);
//			File file = query.list().get(0);
//			System.out.println("Child UUID: " + file.getParent());
//			System.out.println(file.getParent().getParent());
//			
//			session.close();
//			factory.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//			System.exit(-1);
//		}
//	}
}
