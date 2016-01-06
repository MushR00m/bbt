package models;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * app版本实体类
 * @author luobotao
 *
 */
@Entity
@Table(name = "other_app")
public class OtherApp implements Serializable {

	@Id
	@GeneratedValue
	private Integer id;
	
	@Column(name="title",columnDefinition = " varchar(256) DEFAULT '' ")
	private String title;
	@Column(name="subtitle",columnDefinition = " varchar(512) DEFAULT '' ")
	private String subtitle;
	@Column(name="tips",columnDefinition = " varchar(256) DEFAULT '' ")
	private String tips;
	@Column(name="linkurl",columnDefinition = " varchar(256) DEFAULT '' ")
	private String linkurl;
	@Column(name="icon",columnDefinition = " varchar(256) DEFAULT '' ")
	private String icon;
	@Column(name = "date_new")
	@Temporal(TemporalType.TIMESTAMP)
	public Date dateNew;
	@Column(name = "date_upd")
	@Temporal(TemporalType.TIMESTAMP)
	public Date dateUpd;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	public String getTips() {
		return tips;
	}

	public void setTips(String tips) {
		this.tips = tips;
	}

	public String getLinkurl() {
		return linkurl;
	}

	public void setLinkurl(String linkurl) {
		this.linkurl = linkurl;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
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

	public Integer getId() {

		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
}
