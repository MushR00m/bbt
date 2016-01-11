package models.citywide.user;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
/*
 * 同城用户表
 */
@Entity
@Table(name = "user_info")
public class UserInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2483896093420151207L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer uid;
	@Column(name="unionid")
	private String Unionid;//微信ＵＮＩＯＮＩＤ
	@Column(name="nickname")
	private String NickName;//妮称
	private Integer typ ;//类型 0商户 1个人
	@Column(name="date_new")
	private Date date_new;//创建时间
	@Column(name="date_upd")
	private Date date_upd;//更新时间
	
	public Integer getUid() {
		return uid;
	}
	public void setUid(Integer uid) {
		this.uid = uid;
	}
	public String getUnionid() {
		return Unionid;
	}
	public void setUnionid(String unionid) {
		Unionid = unionid;
	}
	public String getNickName() {
		return NickName;
	}
	public void setNickName(String nickName) {
		NickName = nickName;
	}
	public Integer getTyp() {
		return typ;
	}
	public void setTyp(Integer typ) {
		this.typ = typ;
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
