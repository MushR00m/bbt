package models.postman;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="postmanuser_location_log")
public class PostmanUserLocationLog implements Serializable {

	private static final long serialVersionUID = -7275086968889530394L;
	@Id
	public Long id;
	public Integer postmanid;
	@Column(columnDefinition = " varchar(32) DEFAULT '' ")
	public String staffid;
	@Column(columnDefinition = " varchar(16) DEFAULT '' ")
	public String nickname;
	@Column(columnDefinition = " varchar(16) DEFAULT '' ")
	public String phone;
	@Column(columnDefinition = " varchar(32) DEFAULT '' ")
	public String companyname;
	@Column(columnDefinition = " varchar(32) DEFAULT '' ")
	public String substation;
	@Column(columnDefinition = " int DEFAULT 0 ")
	public Integer companyid;
	@Column(columnDefinition = " numeric(9,4) ")
	public Double latitude;// 纬度
	@Column(columnDefinition = " numeric(9,4) ")
	public Double lontitude;// 经度
	@Column(columnDefinition = " numeric(9,4) ")
	public Double height;// 高度

	@Column(name = "date_new")
	@Temporal(TemporalType.TIMESTAMP)
	public Date dateNew;
	@Column(name = "date_upd")
	@Temporal(TemporalType.TIMESTAMP)
	public Date dateUpd;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getPostmanid() {
		return postmanid;
	}
	public void setPostmanid(Integer postmanid) {
		this.postmanid = postmanid;
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
	public String getCompanyname() {
		return companyname;
	}
	public void setCompanyname(String companyname) {
		this.companyname = companyname;
	}
	public String getSubstation() {
		return substation;
	}
	public void setSubstation(String substation) {
		this.substation = substation;
	}
	public Integer getCompanyid() {
		return companyid;
	}
	public void setCompanyid(Integer companyid) {
		this.companyid = companyid;
	}
	
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public Double getLontitude() {
		return lontitude;
	}
	public void setLontitude(Double lontitude) {
		this.lontitude = lontitude;
	}
	public Double getHeight() {
		return height;
	}
	public void setHeight(Double height) {
		this.height = height;
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
