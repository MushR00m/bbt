package models;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * 推送实体类 从表中获取数据，依靠RabbitMQ进行消息发送
 * 
 * @author luobotao
 *
 */
@Entity
@Table(name = "pushinfo")
public class PushInfo implements Serializable {

	private static final long serialVersionUID = -4389795899568666091L;
	@Id
	@GeneratedValue
	private Integer id;
	@Column(name = "title", columnDefinition = " varchar(256) DEFAULT '' ")
	private String title;
	@Column(name = "content", columnDefinition = " varchar(512) DEFAULT '' ")
	private String content;
	@Column(name = "logo", columnDefinition = " varchar(64) DEFAULT '' ")
	private String logo;
	@Column(name = "logourl", columnDefinition = " varchar(128) DEFAULT '' ")
	private String logourl;
	@Column(name = "url", columnDefinition = " varchar(128) DEFAULT '' ")
	private String url;
	@Column(name = "pushtoken", columnDefinition = " varchar(128) DEFAULT '' ")
	private String pushtoken;
	@Column(name = "flg", columnDefinition = " varchar(2) DEFAULT '0' ")
	private String flg;// 标志是否已发送 0未发送 1已发送 2发送失败
	@Column(name = "date_new")
	@Temporal(TemporalType.TIMESTAMP)
	public Date dateNew;
	@Column(name = "date_upd")
	@Temporal(TemporalType.TIMESTAMP)
	public Date dateUpd;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPushtoken() {
		return pushtoken;
	}

	public void setPushtoken(String pushtoken) {
		this.pushtoken = pushtoken;
	}

	public String getFlg() {
		return flg;
	}

	public void setFlg(String flg) {
		this.flg = flg;
	}

	public Date getDateNew() {
		return dateNew;
	}

	public void setDateNew(Date dateNew) {
		this.dateNew = dateNew;
	}

	public Date getDateUpd() {
		return dateUpd;
	}

	public void setDateUpd(Date dateUpd) {
		this.dateUpd = dateUpd;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getLogourl() {
		return logourl;
	}

	public void setLogourl(String logourl) {
		this.logourl = logourl;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
