package models;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.avaje.ebean.Model;

/**
 * 短信实体类
 * 从表中获取数据，依靠RabbitMQ进行消息发送
 * @author luobotao
 *
 */
@Entity
@Table(name = "smsinfo")
public class SmsInfo implements Serializable {

	private static final long serialVersionUID = -6881239945444620545L;

	@Id
	@GeneratedValue
	private Integer id;

	private String phone;

	@Column(name="tpl_id",columnDefinition = " varchar(8) DEFAULT '' ")
	private String tplId;

	private String args;
	
	private String flg;//标志是否已发送 0未发送 1已发送 2发送失败
	
	@Column(columnDefinition = " varchar(2) DEFAULT '1' ")
	private String typ;//1普通短信2营销短信
	
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
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getTplId() {
		return tplId;
	}
	public void setTplId(String tplId) {
		this.tplId = tplId;
	}
	public String getArgs() {
		return args;
	}
	public void setArgs(String args) {
		this.args = args;
	}
	public String getFlg() {
		return flg;
	}
	public void setFlg(String flg) {
		this.flg = flg;
	}
	public String getTyp() {
		return typ;
	}
	public void setTyp(String typ) {
		this.typ = typ;
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
