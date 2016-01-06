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
@Table(name = "magazineinfo")
public class MagazineInfo implements Serializable {

	private static final long serialVersionUID = -6468553568351462431L;

	@Id
	@GeneratedValue
	private Integer id;
	
	@Column(name="magid")
	private Integer magid;
	@Column(name="imgurl",columnDefinition = " varchar(256) DEFAULT '' ")
	private String imgurl;//
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
	public Integer getMagid() {
		return magid;
	}
	public void setMagid(Integer magid) {
		this.magid = magid;
	}
	public String getImgurl() {
		return imgurl;
	}
	public void setImgurl(String imgurl) {
		this.imgurl = imgurl;
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
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
	
}
