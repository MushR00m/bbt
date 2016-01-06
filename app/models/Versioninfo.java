package models;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.avaje.ebean.Model;

/**
 * app版本实体类
 * @author luobotao
 *
 */
@Entity
@Table(name = "versioninfo")
public class Versioninfo implements Serializable {

	private static final long serialVersionUID = 4766489891101898900L;

	@Id
	@GeneratedValue
	private Integer id;
	
	@Column(name="ostype",columnDefinition = " varchar(2) DEFAULT '' ")
	private String ostype;
	@Column(name="latestver",columnDefinition = " varchar(16) DEFAULT '' ")
	private String latestver;
	@Column(name="isforced",columnDefinition = " varchar(2) DEFAULT '' ")
	private String isforced;
	@Column(name="remind_time",columnDefinition = " varchar(2) DEFAULT '' ")
	private String remindTime;//1只提醒一次 0提醒多次
	@Lob
	private String message;
	@Column(name="url",columnDefinition = " varchar(256) DEFAULT '' ")
	private String url;
	@Column(columnDefinition = " varchar(2) DEFAULT '1' ")
	private String sta;//1未发布2已发布3最新发布
	@Column(name="marketcode",columnDefinition = " varchar(256) DEFAULT '' ")
	private String marketcode;
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
	public String getOstype() {
		return ostype;
	}
	public void setOstype(String ostype) {
		this.ostype = ostype;
	}
	public String getLatestver() {
		return latestver;
	}
	public void setLatestver(String latestver) {
		this.latestver = latestver;
	}
	public String getIsforced() {
		return isforced;
	}
	public void setIsforced(String isforced) {
		this.isforced = isforced;
	}
	public String getRemindTime() {
		return remindTime;
	}
	public void setRemindTime(String remindTime) {
		this.remindTime = remindTime;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getSta() {
		return sta;
	}
	public void setSta(String sta) {
		this.sta = sta;
	}
	public String getMarketcode() {
		return marketcode;
	}
	public void setMarketcode(String marketcode) {
		this.marketcode = marketcode;
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
	
	
}
