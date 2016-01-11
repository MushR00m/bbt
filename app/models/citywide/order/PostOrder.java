package models.citywide.order;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "post_order")
public class PostOrder implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2483896093420151210L;
	/*
 * 派单表
 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	@Column(name="uid")
	private Integer uid;//用户编号
	@Column(name="ordercode")
	private String ordercode;//订单编号
	@Column(name="userlong")
	private Double userlong;//寄件人经度
	@Column(name="userlat")
	private Double userlat;//寄件人伟度
	@Column(name="username")
	private String username;//寄件人姓名
	@Column(name="phone")
	private String phone;//寄件人手机
	@Column(name="address")
	private String address;//寄件地址
	@Column(name="receivelong")
	private Double receivelong;//收件人经度
	@Column(name="receivelat")
	private Double receivelat;//收件人伟度
	@Column(name="receivename")
	private String receivename;//收件人姓名
	@Column(name="receivephone")
	private String receivephone;//收件人手机
	@Column(name="receiveaddress")
	private String receiveaddress;//收件人地址
	@Column(name="subjectname")
	private String subjectname;//物品名称
	@Column(name="subjecttyp")
	private Integer subjecttyp;//特别类型
	@Column(name="subjectremark")
	private String subjectremark;//物品备注
	@Column(name="distance")
	private Long distance;//距离 
	@Column(name="weight")
	private Double weight;//重量
	@Column(name="paytyp")
	private String paytyp;//付款方式
	@Column(name="goodsfee")
	private int goodsfee;//商品金额
	@Column(name="freight")
	private int freight;//运费
	@Column(name="totalfee")
	private Integer totalfee;//总价
	@Column(name="award")
	private Integer award;//奖励
	@Column(name="status")
	private Integer status;//0未支付，1：待接单，2：待揽收，3：待配送，4：已完成（已送达），5：已完成（有问题）
	@Column(name="gettyp")
	private String gettyp;	//取件类型 1立即取件 2其他时间
	@Column(name="gettime")
	private Date gettime;//取件时间
	@Column(name="realgettime")
	private Date realgettime;//实际取件时间
	@Column(name="overtime")
	private Date overtime;//完成时间
	@Column(name="date_new")
	private Date date_new;//创建时间
	@Column(name="date_upd")
	private Date date_upd;//更新时间
	@Column(name="islooked")
	private String islooked;//是否查看
	@Column(name="ordertyp")
	private String ordertyp;	//订单类型 1客户端下单 2渠道下单
	@Column(name="remark")
	private String remark;		//备注
	@Column(name="reasonid")
	private int reasonid;		//备注
	
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
	public String getIslooked() {
		return islooked;
	}
	public void setIslooked(String islooked) {
		this.islooked = islooked;
	}
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
	public Double getUserlong() {
		return userlong;
	}
	public void setUserlong(Double userlong) {
		this.userlong = userlong;
	}
	public Double getUserlat() {
		return userlat;
	}
	public void setUserlat(Double userlat) {
		this.userlat = userlat;
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
	public Double getReceivelong() {
		return receivelong;
	}
	public void setReceivelong(Double receivelong) {
		this.receivelong = receivelong;
	}
	public Double getReceivelat() {
		return receivelat;
	}
	public void setReceivelat(Double receivelat) {
		this.receivelat = receivelat;
	}
	public String getReceivename() {
		return receivename;
	}
	public void setReceivename(String receivename) {
		this.receivename = receivename;
	}
	public String getReceivephone() {
		return receivephone;
	}
	public void setReceivephone(String receivephone) {
		this.receivephone = receivephone;
	}
	public String getReceiveaddress() {
		return receiveaddress;
	}
	public void setReceiveaddress(String receiveaddress) {
		this.receiveaddress = receiveaddress;
	}
	public Long getDistance() {
		return distance;
	}
	public void setDistance(Long distance) {
		this.distance = distance;
	}
	public String getSubjectname() {
		return subjectname;
	}
	public void setSubjectname(String subjectname) {
		this.subjectname = subjectname;
	}
	public Integer getSubjecttyp() {
		return subjecttyp;
	}
	public void setSubjecttyp(Integer subjecttyp) {
		this.subjecttyp = subjecttyp;
	}
	public String getSubjectremark() {
		return subjectremark;
	}
	public void setSubjectremark(String subjectremark) {
		this.subjectremark = subjectremark;
	}
	public Double getWeight() {
		return weight;
	}
	public void setWeight(Double weight) {
		this.weight = weight;
	}
	public String getPaytyp() {
		return paytyp;
	}
	public void setPaytyp(String paytyp) {
		this.paytyp = paytyp;
	}
	public Integer getAward() {
		return award;
	}
	public void setAward(Integer award) {
		this.award = award;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Date getGettime() {
		return gettime;
	}
	public void setGettime(Date gettime) {
		this.gettime = gettime;
	}
	public Date getRealgettime() {
		return realgettime;
	}
	public void setRealgettime(Date realgettime) {
		this.realgettime = realgettime;
	}
	public Date getOvertime() {
		return overtime;
	}
	public void setOvertime(Date overtime) {
		this.overtime = overtime;
	}
	public String getGettyp() {
		return gettyp;
	}
	public void setGettyp(String gettyp) {
		this.gettyp = gettyp;
	}
	public String getOrdertyp() {
		return ordertyp;
	}
	public void setOrdertyp(String ordertyp) {
		this.ordertyp = ordertyp;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public int getGoodsfee() {
		return goodsfee;
	}
	public void setGoodsfee(int goodsfee) {
		this.goodsfee = goodsfee;
	}
	public int getFreight() {
		return freight;
	}
	public void setFreight(int freight) {
		this.freight = freight;
	}
	public Integer getTotalfee() {
		return totalfee;
	}
	public void setTotalfee(Integer totalfee) {
		this.totalfee = totalfee;
	}
	public int getReasonid() {
		return reasonid;
	}
	public void setReasonid(int reasonid) {
		this.reasonid = reasonid;
	}
	public String getOrdercode() {
		return ordercode;
	}
	public void setOrdercode(String ordercode) {
		this.ordercode = ordercode;
	}
	
}
