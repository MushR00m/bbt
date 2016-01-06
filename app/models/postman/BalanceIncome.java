package models.postman;

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
 * 用户收入实体类
 * @author luobotao
 *
 */
@Entity
@Table(name = "balance_income")
public class BalanceIncome implements Serializable {

	private static final long serialVersionUID = 4330631600545277722L;
	@Id
	@GeneratedValue
	private Integer id;
	@Column(name="uid",columnDefinition = " int(11) DEFAULT 0 ")
	private Integer uid;

	@Column(name="amount",columnDefinition = " int(11) DEFAULT 0 ")
	private Integer amount;
	@Column(name="title",columnDefinition = " varchar(32) '' ")
	private String title;//
	@Column(name="remark",columnDefinition = " varchar(256) '' ")
	private String remark;//
	@Column(name="out_trade_no",columnDefinition = " varchar(64) '' ")
	private String out_trade_no;//
	@Column(name="src",columnDefinition = " varchar(16) '' ")
	private String src;//
	@Column(name="phone",columnDefinition = " varchar(16) '' ")
	private String phone;//
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
	public Integer getUid() {
		return uid;
	}
	public void setUid(Integer uid) {
		this.uid = uid;
	}
	public Integer getAmount() {
		return amount;
	}
	public void setAmount(Integer amount) {
		this.amount = amount;
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
	public String getOut_trade_no() {
		return out_trade_no;
	}
	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}
	public String getSrc() {
		return src;
	}
	public void setSrc(String src) {
		this.src = src;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
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
	
	
	
}
