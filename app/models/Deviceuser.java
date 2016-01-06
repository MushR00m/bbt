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
 * 设备实体类
 * @author luobotao
 *
 */
@Entity
@Table(name = "deviceuser")
public class Deviceuser implements Serializable {
	private static final long serialVersionUID = 1811772039259177880L;
	@Id
	@GeneratedValue
	private Integer id;
	@Column(name="deviceid",columnDefinition = " varchar(64) ")
	private String deviceid;
	@Column(name="uid",columnDefinition = "  int(11) DEFAULT 0 ")
	private Integer uid;
	@Column(name="ostype",columnDefinition = " varchar(2) ")
	private String ostype;//
	@Column(name="osversion",columnDefinition = " varchar(32) ")
	private String osversion;//
	@Column(name="model",columnDefinition = " varchar(32) ")
	private String model;//
	@Column(name="pushToken",columnDefinition = " varchar(32) ")
	private String pushToken;//
	@Column(name="solution",columnDefinition = " varchar(32) ")
	private String solution;//
	@Column(name="appversion",columnDefinition = " varchar(32) ")
	private String appversion;//
	@Column(name="marketcode",columnDefinition = " varchar(8) ")
	private String marketcode;//
	@Column(name="ver",columnDefinition = " varchar(8) DEFAULT ''")
	private String version;//
	@Column(name="imei",columnDefinition = " varchar(64) DEFAULT ''")
	private String imei;//
	@Column(name = "date_new")
	@Temporal(TemporalType.TIMESTAMP)
	public Date dateNew;

	@Column(name = "date_upd")
	@Temporal(TemporalType.TIMESTAMP)
	public Date dateUpd;
	
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getImei() {
		return imei;
	}
	public void setImei(String imei) {
		this.imei = imei;
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getDeviceid() {
		return deviceid;
	}
	public void setDeviceid(String deviceid) {
		this.deviceid = deviceid;
	}
	public Integer getUid() {
		return uid;
	}
	public void setUid(Integer uid) {
		this.uid = uid;
	}
	public String getOstype() {
		return ostype;
	}
	public void setOstype(String ostype) {
		this.ostype = ostype;
	}
	public String getOsversion() {
		return osversion;
	}
	public void setOsversion(String osversion) {
		this.osversion = osversion;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getPushToken() {
		return pushToken;
	}
	public void setPushToken(String pushToken) {
		this.pushToken = pushToken;
	}
	public String getSolution() {
		return solution;
	}
	public void setSolution(String solution) {
		this.solution = solution;
	}
	public String getAppversion() {
		return appversion;
	}
	public void setAppversion(String appversion) {
		this.appversion = appversion;
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
	public String getMarketcode() {
		return marketcode;
	}
	public void setMarketcode(String marketcode) {
		this.marketcode = marketcode;
	}
}
