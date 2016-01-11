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
 * 内容实体类
 * @author luobotao
 *
 */
@Entity
@Table(name = "postcontent")
public class Postcontent implements Serializable {
	private static final long serialVersionUID = -5927506514702626993L;

	@Id
	@GeneratedValue
	private Integer id;
	
	@Column(name="typ",columnDefinition = " int(11)")
	private Integer typ;//DAILY(1, "日常任务"), AWARD(2, "黄金任务"), SIGNIN(3, "签到任务 "), SYSINFO(4, "系统消息"), NEW(5, "新闻");

	@Column(name="typname",columnDefinition = " varchar(32) '' ")
	private String typname;//
	
	@Column(name="typicon",columnDefinition = " varchar(256) '' ")
	private String typicon;//
	
	@Column(name="title",columnDefinition = " varchar(64) '' ")
	private String title;//
	
	@Column(name="subtitle",columnDefinition = " varchar(256) '' ")
	private String subtitle;//
	
	@Column(name="content",columnDefinition = " text  ")
	private String content;//
	
	@Column(name="amount",columnDefinition = " int(11) DEFAULT 0 ")
	private Integer amount;
	
	@Column(name="tips",columnDefinition = " varchar(64) '' ")
	private String tips;//
	
	@Column(name="linkurl",columnDefinition = " varchar(256) '' ")
	private String linkurl;//
	
	@Column(name="dateremark",columnDefinition = " varchar(64) '' ")
	private String dateremark;//
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date start_tim;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date end_tim;
	
	@Column(name="nsort",columnDefinition = " int(11) DEFAULT 0 ")
	private Integer nsort;

	@Column(name="sta",columnDefinition = " varchar(2) '0' ")
	private String sta;//
	
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

	public String getTypname() {
		return typname;
	}

	public void setTypname(String typname) {
		this.typname = typname;
	}

	public String getTypicon() {
		return typicon;
	}

	public void setTypicon(String typicon) {
		this.typicon = typicon;
	}

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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
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

	public String getDateremark() {
		return dateremark;
	}

	public void setDateremark(String dateremark) {
		this.dateremark = dateremark;
	}

	public Date getStart_tim() {
		return start_tim;
	}

	public void setStart_tim(Date start_tim) {
		this.start_tim = start_tim;
	}

	public Date getEnd_tim() {
		return end_tim;
	}

	public void setEnd_tim(Date end_tim) {
		this.end_tim = end_tim;
	}

	public Integer getNsort() {
		return nsort;
	}

	public void setNsort(Integer nsort) {
		this.nsort = nsort;
	}

	public String getSta() {
		return sta;
	}

	public void setSta(String sta) {
		this.sta = sta;
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
