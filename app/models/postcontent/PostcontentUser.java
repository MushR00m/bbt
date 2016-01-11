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
 * 内容用户实体类
 * @author luobotao
 *
 */
@Entity
@Table(name = "postcontent_user")
public class PostcontentUser implements Serializable {
	private static final long serialVersionUID = 8159800879690414052L;

	@Id
	@GeneratedValue
	private Integer id;
	
	@Column(name="pcid",columnDefinition = " int(11)")
	private Integer pcid;

	@Column(name="uid",columnDefinition = " int(11)")
	private Integer uid;

	@Column(name="sta",columnDefinition = " varchar(2)")
	private Integer sta;

	@Column(name="magid",columnDefinition = " int(11)")
	private Integer magid;

	@Column(name="rewardid",columnDefinition = " int(11)")
	private Integer rewardid;

	@Column(name="isnew",columnDefinition = " varchar(2)")
	private Integer isnew;

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



	public Integer getUid() {
		return uid;
	}



	public void setUid(Integer uid) {
		this.uid = uid;
	}



	public Integer getSta() {
		return sta;
	}



	public void setSta(Integer sta) {
		this.sta = sta;
	}



	public Integer getMagid() {
		return magid;
	}



	public void setMagid(Integer magid) {
		this.magid = magid;
	}



	public Integer getRewardid() {
		return rewardid;
	}



	public void setRewardid(Integer rewardid) {
		this.rewardid = rewardid;
	}



	public Integer getIsnew() {
		return isnew;
	}



	public void setIsnew(Integer isnew) {
		this.isnew = isnew;
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
