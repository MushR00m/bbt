package models.citywide.user;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 同城余额变动表
 * @author luobotao
 *
 */
@Entity
@Table(name = "user_balance_log")
public class UserBalanceLog implements Serializable{
	private static final long serialVersionUID = 2483896093420151208L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	private Integer uid;//用户编号
	private Double changemoney;//变动金额
	private Double beforebalance;//变动前金额
	private Double endbalance;//变动后金额
	private String remark;//备注
	private Date date_new;//创建时间
	private Date date_upd;//更新时间
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Integer getUid() {
		return uid;
	}
	public void setUid(Integer uid) {
		this.uid = uid;
	}
	public Double getChangemoney() {
		return changemoney;
	}
	public void setChangemoney(Double changemoney) {
		this.changemoney = changemoney;
	}
	public Double getBeforebalance() {
		return beforebalance;
	}
	public void setBeforebalance(Double beforebalance) {
		this.beforebalance = beforebalance;
	}
	public Double getEndbalance() {
		return endbalance;
	}
	public void setEndbalance(Double endbalance) {
		this.endbalance = endbalance;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Date getDate_new() {
		return date_new;
	}
	public void setDate_new(Date date_new) {
		this.date_new = date_new;
	}
	public Date getDate_upd() {
		return date_upd;
	}
	public void setDate_upd(Date date_upd) {
		this.date_upd = date_upd;
	}
	
}
