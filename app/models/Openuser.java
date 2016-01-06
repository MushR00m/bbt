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
 * 第三方公司实体类
 * @author luobotao
 *
 */
@Entity
@Table(name = "openuser")
public class Openuser implements Serializable {
	
	private static final long serialVersionUID = 265387835607651593L;
	@Id
	@GeneratedValue
	private Integer id;
	@Column(name="typ",columnDefinition = " int(11) ")
	private Integer typ;
	@Column(name="token",columnDefinition = " varchar(64)")
	private String token;
	@Column(name="code",columnDefinition = " varchar(32) ")
	private String code;//
	@Column(name="name",columnDefinition = " varchar(32) ")
	private String name;//
	@Column(name="address",columnDefinition = " varchar(255) ")
	private String address;//
	@Column(name="cust_id",columnDefinition = " varchar(32) ")
	private String custId;//
	@Column(name="region_code",columnDefinition = " varchar(32) ")
	private String regionCode;//
	@Column(name="contact",columnDefinition = " varchar(32) ")
	private String contact;//
	@Column(name="phone",columnDefinition = " varchar(32) ")
	private String phone;//
	@Column(name="email",columnDefinition = " varchar(32) ")
	private String email;//
	@Column(name="dev_url",columnDefinition = " varchar(128) ")
	private String devUrl;//
	@Column(name="prod_url",columnDefinition = " varchar(128) ")
	private String prodUrl;//
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
	public Integer getTyp() {
		return typ;
	}
	public void setTyp(Integer typ) {
		this.typ = typ;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getCustId() {
		return custId;
	}
	public void setCustId(String custId) {
		this.custId = custId;
	}
	public String getRegionCode() {
		return regionCode;
	}
	public void setRegionCode(String regionCode) {
		this.regionCode = regionCode;
	}
	public String getContact() {
		return contact;
	}
	public void setContact(String contact) {
		this.contact = contact;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getDevUrl() {
		return devUrl;
	}
	public void setDevUrl(String devUrl) {
		this.devUrl = devUrl;
	}
	public String getProdUrl() {
		return prodUrl;
	}
	public void setProdUrl(String prodUrl) {
		this.prodUrl = prodUrl;
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
