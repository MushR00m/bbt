package models.magazine;

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
 * 杂志实体类
 * @author luobotao
 *
 */
@Entity
@Table(name = "magazinelist")
public class Magazinelist implements Serializable {

	private static final long serialVersionUID = 2013100919302306006L;

	@Id
	@GeneratedValue
	private Integer id;
	
	@Column(name="title",columnDefinition = " varchar(64) DEFAULT '' ")
	private String title;
	@Column(name="remark",columnDefinition = " varchar(256) DEFAULT '' ")
	private String remark;
	@Column(name="girlname",columnDefinition = " varchar(16) DEFAULT '' ")
	private String girlname;
	@Column(name="girlage",columnDefinition = " varchar(16) DEFAULT '' ")
	private String girlage;//
	@Column(name="girlheight",columnDefinition = " varchar(16) DEFAULT '' ")
	private String girlheight;//
	@Column(name="imgurl",columnDefinition = " varchar(256) DEFAULT '' ")
	private String imgurl;//
	@Column(columnDefinition = " varchar(2) DEFAULT '0' ")
	private String typ;//
	private Integer nsort;//
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
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getGirlname() {
		return girlname;
	}
	public void setGirlname(String girlname) {
		this.girlname = girlname;
	}
	public String getGirlage() {
		return girlage;
	}
	public void setGirlage(String girlage) {
		this.girlage = girlage;
	}
	public String getGirlheight() {
		return girlheight;
	}
	public void setGirlheight(String girlheight) {
		this.girlheight = girlheight;
	}
	public String getImgurl() {
		return imgurl;
	}
	public void setImgurl(String imgurl) {
		this.imgurl = imgurl;
	}
	public String getTyp() {
		return typ;
	}
	public void setTyp(String typ) {
		this.typ = typ;
	}
	public Integer getNsort() {
		return nsort;
	}
	public void setNsort(Integer nsort) {
		this.nsort = nsort;
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
