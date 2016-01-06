package models.citywide.user;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 通讯录表
 * @author luobotao
 *
 */
@Entity
@Table(name = "user_address")
public class UserAddress implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2483896093420151209L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	private Integer uid;//用户编号
	private Double longs;//经度
	private Double lat;//纬度
	private String username;//姓名
	private String phone;//手机
	private String address;//地址
	private Integer typ ;//类型
	private Date date_new;//创建时间
	private Date date_upd ;//更新时间
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
	public Double getLongs() {
		return longs;
	}
	public void setLongs(Double longs) {
		this.longs = longs;
	}
	public Double getLat() {
		return lat;
	}
	public void setLat(Double lat) {
		this.lat = lat;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
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
