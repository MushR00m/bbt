package models.postman;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.avaje.ebean.Model;

@Entity
@Table(name="postmanuser")
public class PostmanUser extends Model implements Serializable {

	private static final long serialVersionUID = -1619641047129597335L;
	@Id
	public Integer id;
	@Column(columnDefinition = " varchar(32) DEFAULT '' ")
	public String staffid;
	@Column(columnDefinition = " varchar(16) DEFAULT '' ")
	public String nickname;
	@Column(columnDefinition = " varchar(16) DEFAULT '' ")
	public String phone;
	@Column(columnDefinition = " varchar(256) DEFAULT '' ")
	public String headicon;
	@Column(columnDefinition = " varchar(32) DEFAULT '' ")
	public String companyname;
	@Column(columnDefinition = " varchar(32) DEFAULT '' ")
	public String cardidno;
	@Column(columnDefinition = " varchar(32) DEFAULT '' ")
	public String substation;
	@Column(columnDefinition = " int DEFAULT 0 ")
	public Integer companyid;
	@Column(name = "alipay_account", columnDefinition = " varchar(64) DEFAULT '' ")
	public String alipayAccount;
	@Column(columnDefinition = " varchar(32) DEFAULT '' ")
	public String token;//基础token
	@Column(columnDefinition = " varchar(32) DEFAULT '' ")
	public String bbttoken;
	@Column(columnDefinition = " numeric(9,4) ")
	public Double lat;// 纬度
	@Column(columnDefinition = " numeric(9,4) ")
	public Double lon;// 经度
	@Column(columnDefinition = " numeric(9,4) ")
	public Double height;// 高度
	@Column(columnDefinition = " varchar(256) DEFAULT '' ")
	public String addr;// 地址
	@Column(columnDefinition = " varchar(512) DEFAULT '' ")
	public String addrdes;// 地址描述
	@Column(columnDefinition = " varchar(128) DEFAULT '' ")
	public String shopurl;// 商铺描述
	@Column(columnDefinition = " varchar(2) DEFAULT '1' ")
	public String sta;//1正常
	@Column(columnDefinition = " int DEFAULT 0 ")
	public Integer poststatus;//1开启
	
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

	public String getStaffid() {
		return staffid;
	}

	public void setStaffid(String staffid) {
		this.staffid = staffid;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getHeadicon() {
		return headicon;
	}

	public void setHeadicon(String headicon) {
		this.headicon = headicon;
	}

	public String getCompanyname() {
		return companyname;
	}

	public void setCompanyname(String companyname) {
		this.companyname = companyname;
	}

	public Integer getCompanyid() {
		return companyid;
	}

	public void setCompanyid(Integer companyid) {
		this.companyid = companyid;
	}

	public String getAlipayAccount() {
		return alipayAccount;
	}

	public void setAlipayAccount(String alipayAccount) {
		this.alipayAccount = alipayAccount;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getBbttoken() {
		return bbttoken;
	}

	public void setBbttoken(String bbttoken) {
		this.bbttoken = bbttoken;
	}

	public String getSta() {
		return sta;
	}

	public void setSta(String sta) {
		this.sta = sta;
	}

	public Integer getPoststatus() {
		return poststatus;
	}

	public void setPoststatus(Integer poststatus) {
		this.poststatus = poststatus;
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

	public String getCardidno() {
		return cardidno;
	}

	public void setCardidno(String cardidno) {
		this.cardidno = cardidno;
	}

	public String getSubstation() {
		return substation;
	}

	public void setSubstation(String substation) {
		this.substation = substation;
	}

	

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public Double getLon() {
		return lon;
	}

	public void setLon(Double lon) {
		this.lon = lon;
	}

	public Double getHeight() {
		return height;
	}

	public void setHeight(Double height) {
		this.height = height;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public String getAddrdes() {
		return addrdes;
	}

	public void setAddrdes(String addrdes) {
		this.addrdes = addrdes;
	}

	public String getShopurl() {
		return shopurl;
	}

	public void setShopurl(String shopurl) {
		this.shopurl = shopurl;
	}

}
