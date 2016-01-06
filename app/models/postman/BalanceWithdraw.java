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
 * 用户提现实体类
 * @author luobotao
 *
 */
@Entity
@Table(name = "balance_withdraw")
public class BalanceWithdraw implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3532662456123636431L;
	@Id
	@GeneratedValue
	private Integer id;
	@Column(name="uid",columnDefinition = " int(11) DEFAULT 0 ")
	private Integer uid;

	@Column(name="amount",columnDefinition = " int(11) DEFAULT 0 ")
	private Integer amount;
	@Column(name="remark",columnDefinition = " varchar(256) '' ")
	private String remark;//
	@Column(name="oper",columnDefinition = " varchar(16) '' ")
	private String oper;//
	@Column(name="oper_remark",columnDefinition = " varchar(256) '' ")
	private String operRemark;//
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
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getOper() {
		return oper;
	}
	public void setOper(String oper) {
		this.oper = oper;
	}
	public String getOperRemark() {
		return operRemark;
	}
	public void setOperRemark(String operRemark) {
		this.operRemark = operRemark;
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
