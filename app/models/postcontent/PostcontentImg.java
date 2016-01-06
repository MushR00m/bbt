package models.postcontent;

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
 * 内容图片实体类
 * @author luobotao
 *
 */
@Entity
@Table(name = "postcontent_img")
public class PostcontentImg implements Serializable {
	private static final long serialVersionUID = 8159800879690414051L;

	@Id
	@GeneratedValue
	private Integer id;
	
	@Column(name="pcid",columnDefinition = " int(11)")
	private Integer pcid;

	
	@Column(name="imgurl",columnDefinition = " varchar(256) '0' ")
	private String imgurl;//

	
	@Column(name = "date_new")
	@Temporal(TemporalType.TIMESTAMP)
	public Date dateNew;


	public Integer getId() {
		return id;
	}


	public void setId(Integer id) {
		this.id = id;
	}


	public Integer getPcid() {
		return pcid;
	}


	public void setPcid(Integer pcid) {
		this.pcid = pcid;
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


	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	
	
}
