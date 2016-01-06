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
 * 用户红点实体类
 * @author luobotao
 *
 */
@Entity
@Table(name = "reddot")
public class Reddot implements Serializable {

	
	private static final long serialVersionUID = 7145091037264432542L;
	@Id
	@GeneratedValue
	private Integer id;
	@Column(name="uid",columnDefinition = " int(11) DEFAULT 0 ")
	private Integer uid;
	@Column(name="myfav",columnDefinition = " varchar(2) DEFAULT '0' ")
	private String myfav;//我的收藏红点；0：不显示红点；1：显示红点；
	@Column(name="signin",columnDefinition = " varchar(2) DEFAULT '0' ")
	private String signin;//签到红点；0：不显示红点；1：显示红点；
	@Column(name="upgrade",columnDefinition = "varchar(2) DEFAULT '0' ")
	private String upgrade;//升级提示红点；0：不显示红点；1：显示红点；
	@Column(name="wallet_withdraw",columnDefinition = " varchar(2) DEFAULT '0'  ")
	private String wallet_withdraw;//钱包提现明细红点；0：不显示红点；1：显示红点；
	@Column(name="wallet_incoming",columnDefinition = " varchar(2) DEFAULT '0'  ")
	private String wallet_incoming;//钱包收支明细红点；0：不显示红点；1：显示红点；
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
	public String getMyfav() {
		return myfav;
	}
	public void setMyfav(String myfav) {
		this.myfav = myfav;
	}
	public String getUpgrade() {
		return upgrade;
	}
	public void setUpgrade(String upgrade) {
		this.upgrade = upgrade;
	}
	public String getWallet_withdraw() {
		return wallet_withdraw;
	}
	public void setWallet_withdraw(String wallet_withdraw) {
		this.wallet_withdraw = wallet_withdraw;
	}
	public String getWallet_incoming() {
		return wallet_incoming;
	}
	public void setWallet_incoming(String wallet_incoming) {
		this.wallet_incoming = wallet_incoming;
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

	public String getSignin() {
		return signin;
	}
	public void setSignin(String signin) {
		this.signin = signin;
	}
}
