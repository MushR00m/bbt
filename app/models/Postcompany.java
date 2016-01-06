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
import javax.persistence.Transient;

/**
 * 公司实体类
 * 
 * @author luobotao
 *
 */
@Entity
@Table(name = "postcompany")
public class Postcompany implements Serializable {
	private static final long serialVersionUID = -3645677630568952615L;
	@Id
	@GeneratedValue
	private Integer id;
	@Column(name = "companyname", columnDefinition = " varchar(32) DEFAULT '' ")
	private String companyname;
	@Column(name = "companycode", columnDefinition = " varchar(34) DEFAULT '' ")
	private String companycode;
	@Column(name = "logo", columnDefinition = " varchar(256) DEFAULT '' ")
	private String logo;
	@Column(name = "sta", columnDefinition = " varchar(2) DEFAULT '1' ")
	private String sta;//状态
	@Column(name = "ishot", columnDefinition = " varchar(2) DEFAULT '0' ")
	private String ishot;//是否热门
	@Column(name = "nsort", columnDefinition = " int(11) default 0 ")
	private int nsort;//排序
	@Column(name = "deliveryflag", columnDefinition = " varchar(2) DEFAULT '0' ")
	private String deliveryflag;//是否可以进行派单 0不可派单 1可派单
	@Column(name = "date_new")
	@Temporal(TemporalType.TIMESTAMP)
	public Date dateNew;
	@Column(name = "date_upd")
	@Temporal(TemporalType.TIMESTAMP)
	public Date dateUpd;
	@Transient
	public String firstPinyin;//名字的拼音大写首字母
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCompanyname() {
		return companyname;
	}

	public void setCompanyname(String companyname) {
		this.companyname = companyname;
	}

	public String getCompanycode() {
		return companycode;
	}

	public void setCompanycode(String companycode) {
		this.companycode = companycode;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getSta() {
		return sta;
	}

	public void setSta(String sta) {
		this.sta = sta;
	}

	
	public String getIshot() {
		return ishot;
	}

	public void setIshot(String ishot) {
		this.ishot = ishot;
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

	public String getFirstPinyin() {
		return firstPinyin;
	}

	public void setFirstPinyin(String firstPinyin) {
		this.firstPinyin = firstPinyin;
	}

	public String getDeliveryflag() {
		return deliveryflag;
	}

	public void setDeliveryflag(String deliveryflag) {
		this.deliveryflag = deliveryflag;
	}

	public void setNsort(int nsort) {
		this.nsort = nsort;
	}

}
