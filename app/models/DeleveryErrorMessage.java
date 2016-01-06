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

/**
 * 公司异常码表
 * 
 * @author luobotao
 *
 */
@Entity
@Table(name = "deleveryErrorMessage")
public class DeleveryErrorMessage implements Serializable {
	
	private static final long serialVersionUID = -8665663094004242874L;
	@Id
	@GeneratedValue
	private Integer id;
	@Column(name = "merchant_code", columnDefinition = " varchar(34) DEFAULT '' ")
	private String merchant_code;//电商编码
	@Column(name = "message", columnDefinition = " varchar(256) DEFAULT '' ")
	private String message;//异常信息
	@Column(name = "decode", columnDefinition = " varchar(34) DEFAULT '' ")
	private String decode;//异常码
	@Column(name = "state", columnDefinition = " varchar(24) DEFAULT '' ")
	private String state;//滞留或拒收等
	@Column(name = "typ", columnDefinition = " varchar(2) DEFAULT '0' ")
	private String typ;//0落地配1同城
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
	
	public String getMerchant_code() {
		return merchant_code;
	}
	public void setMerchant_code(String merchant_code) {
		this.merchant_code = merchant_code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getDecode() {
		return decode;
	}
	public void setDecode(String decode) {
		this.decode = decode;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
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
	public String getTyp() {
		return typ;
	}
	public void setTyp(String typ) {
		this.typ = typ;
	}
	
	

}
