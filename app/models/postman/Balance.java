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
 * 用户余额实体类
 * @author luobotao
 *
 */
@Entity
@Table(name = "balance")
public class Balance implements Serializable {

	
	private static final long serialVersionUID = -7630120504563671474L;
	@Id
	@GeneratedValue
	private Integer id;
	@Column(name="uid",columnDefinition = " int(11) DEFAULT 0 ")
	private Integer uid;

	@Column(name="balance",columnDefinition = " int(11) DEFAULT 0 ")
	private Integer balance;
	@Column(name="canuse",columnDefinition = " int(11) DEFAULT 0 ")
	private Integer canuse;//可提现金额
	@Column(name="withdraw",columnDefinition = " int(11) DEFAULT 0 ")
	private Integer withdraw;//已提现金额
	@Column(name="remark",columnDefinition = " varchar(32) '' ")
	private String remark;//
	@Column(name="phone",columnDefinition = " varchar(16) '' ")
	private String phone;//
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
	public Integer getBalance() {
		return balance;
	}
	public void setBalance(Integer balance) {
		this.balance = balance;
	}
	public Integer getCanuse() {
		return canuse;
	}
	public void setCanuse(Integer canuse) {
		this.canuse = canuse;
	}
	public Integer getWithdraw() {
		return withdraw;
	}
	public void setWithdraw(Integer withdraw) {
		this.withdraw = withdraw;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
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
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	
}
