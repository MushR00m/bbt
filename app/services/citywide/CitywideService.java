package services.citywide;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Types;
import java.util.Date;
import java.util.List;
import models.OtherApp;
import models.citywide.order.PostmanUserTemp;
import org.apache.commons.lang3.StringUtils;
import models.DeleveryErrorMessage;
import models.Postdelivery;
import models.citywide.order.PostOrder;
import models.citywide.order.PostOrderUser;
import models.citywide.user.UserInfo;
import models.postman.PostmanUser;
import play.Configuration;
import play.Logger;
import play.libs.Json;
import utils.*;
import utils.Constants.PayModeStas;
import vo.api.ExpressInfoVO;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlRow;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;

public class CitywideService {
	private static final Logger.ALogger logger = Logger.of(CitywideService.class);

	public static List<PostOrderUser> findPostOrderUserListByPostmanid(int postmanid){
		return Ebean.getServer(Constants.getDB()).find(PostOrderUser.class).where().eq("postmanid", postmanid).findList();
	}
	public static PostOrder findPostOrderByOrderId(Integer orderid){
		return Ebean.getServer(Constants.getDB()).find(PostOrder.class,orderid);
	}
	public static UserInfo findUserInfoByUid(Integer uid){
		if(uid==null){
			return null;
		}
		return Ebean.getServer(Constants.getDB()).find(UserInfo.class,uid);
	}
	
	public static Integer getNoLookedCntByID(Integer uid){
		Integer noLookedCnt=0;
		String sql="SELECT count(id) as cnt FROM postdelivery WHERE islooked='0' and postman_id="+uid;
		// 调用存储过程
		JdbcOper oper = JdbcOper.getCalledbleDao(sql);
		if (oper == null) {
			return null;
		}
		try {
			oper.rs = oper.cst.executeQuery();
			while (oper.rs.next()) {
				noLookedCnt = oper.rs.getInt("cnt");
			}
		} catch (Exception e) {
			Logger.info(e.toString());
		} finally {
			oper.close();
		}
		return noLookedCnt;
	}
	
	public static ObjectNode getHomePageInfo(ObjectNode resultObject,int postmanid){
		resultObject.put("status", "1");
		resultObject.put("msg", "");
		List<ObjectNode> tablist = Lists.newArrayList();
		String sql="CALL `sp_homepage_stat`(?)";
		// 调用存储过程
		JdbcOper oper = JdbcOper.getCalledbleDao(sql);
		if (oper == null) {
			return null;
		}
		try {
			// 数据库存储过程操作
			oper.cst.setInt(1, postmanid);
			oper.rs = oper.cst.executeQuery();
			while (oper.rs.next()) {
				if (oper.rs.getInt("msgcnt")>0){
					ObjectNode tab1 = Json.newObject();
					tab1.put("type", "6");
					tab1.put("statecolor", "#00AAE1");
					tab1.put("statename", oper.rs.getString("msgcnt")+"个未读消息");
					tab1.put("title", "消息中心");
					tab1.put("subtitle", "");
					tab1.put("tips", "");
					tab1.put("linkurl", "pMessageList://");
					tablist.add(tab1);	
				}
				ObjectNode tab2 = Json.newObject();
				tab2.put("type", "5");
				tab2.put("statecolor", "#00AAE1");
				tab2.put("statename", "");
				tab2.put("title", "配送");
				tab2.put("subtitle", "今日收入"+oper.rs.getString("deliveryfee")+"元");
				tab2.put("tips", "您有"+oper.rs.getString("deliverycnt")+"单未配送");
				tab2.put("linkurl", "pExpressList://");
				tablist.add(tab2);
				ObjectNode tab3 = Json.newObject();
				tab3.put("type", "5");
				tab3.put("statecolor", "#52c4fa");
				if (oper.rs.getInt("poststatus")==0){
					tab3.put("statename", "未接单");
				}else{
					tab3.put("statename", "接单");
				}
				tab3.put("title", "同城");
				tab3.put("subtitle", "今日收入"+oper.rs.getString("orderfee")+"元");
				
				tab3.put("tips", "今日已完成"+oper.rs.getString("ordercnt")+"单");
				tab3.put("linkurl", "pOrderList://");
				tablist.add(tab3);
				ObjectNode tab4 = Json.newObject();
				tab4.put("type", "5");
				tab4.put("statecolor", "#afd35b");
				if ( oper.rs.getInt("newcontentcnt")>0){
					tab4.put("statename", oper.rs.getString("newcontentcnt")+"个新任务");
				}else{
					tab4.put("statename", "");
				}
				tab4.put("title", "任务");
				tab4.put("subtitle", "今日收入"+oper.rs.getString("contentfee")+"元");
				tab4.put("tips", "今日已完成"+oper.rs.getString("contentcnt")+"项");
				tab4.put("linkurl", "pHomepage://");
				tablist.add(tab4);
				resultObject.set("tablist", Json.toJson(tablist));
			}
		} catch (Exception e) {
			Logger.info(e.toString());
		} finally {
			oper.close();
		}
		
		return resultObject;
	}


public static ObjectNode getOrderList(ObjectNode resultObject,int postmanid,String status,int index,Double postmanX,Double postmanY){
		int pageSize=10;
		resultObject.put("endflag", "1");
		List<ObjectNode> orderlist = Lists.newArrayList();
		String sql="CALL `sp_order_list`(?,?,?,?,?)";
		// 调用存储过程
		JdbcOper oper = JdbcOper.getCalledbleDao(sql);
		if (oper == null) {
			return null;
		}
		try {
			// 数据库存储过程操作
			oper.cst.setInt(1, postmanid);
			oper.cst.setString(2, status);
			oper.cst.setInt(3, index);
			oper.cst.setInt(4, pageSize);
			oper.cst.registerOutParameter(5, Types.INTEGER);
			oper.rs = oper.cst.executeQuery();
			String lastindex = "9999";
			while (oper.rs.next()) {
				ObjectNode express1 = Json.newObject();
        		express1.put("orderid", oper.rs.getString("orderid"));
        		express1.put("ordercode",oper.rs.getString("ordercode"));
        		express1.put("state",  oper.rs.getString("status"));//快递状态（1：待接单，2：待揽收，3：待配送，4：已完成（已送达），5：已完成（有问题）
        		express1.put("receivphone",  oper.rs.getString("receivephone"));
        		express1.put("receivedistance", Numbers.doubleWithOne(StringUtil.Distance(postmanX,postmanY,oper.rs.getDouble("receivelat"), oper.rs.getDouble("receivelong")),1000,1)+"千米");
        		express1.put("receiveaddress",  oper.rs.getString("receiveaddress"));
        		express1.put("sendaddress",  oper.rs.getString("address"));
        		express1.put("sendphone",oper.rs.getString("phone"));
        		express1.put("senddistance", Numbers.doubleWithOne(StringUtil.Distance(postmanX,postmanY,oper.rs.getDouble("userlat"), oper.rs.getDouble("userlong")),1000,1)+"千米");
        		express1.put("orderincome",oper.rs.getString("award"));
        		express1.put("ordertime", oper.rs.getString("gettime"));
        		UserInfo userInfo = CitywideService.findUserInfoByUid(postmanid);
        		if(userInfo!=null && userInfo.getTyp()==0){//0是商户
        			express1.put("linkurl", "pOrderDetail://oid="+oper.rs.getString("orderid")+"&type=0");
        			express1.put("ordertitle", "速递订单（商户）");
        		}else{
        			express1.put("linkurl", "pOrderDetail://oid="+oper.rs.getString("orderid")+"&type=1");
        			express1.put("ordertitle", "速递订单（个人）");
        		}
        		orderlist.add(express1);
				lastindex = oper.rs.getString("id");
			}
			int total =  oper.cst.getInt(5);//总条数
			if(total>Numbers.parseInt(lastindex, 0)){
				resultObject.put("endflag", "0");//判断结束标志
			}
			resultObject.put("tips", "您有"+total+"条新订单");
			resultObject.put("lastindex", lastindex);
			resultObject.set("orderlist", Json.toJson(orderlist));
		} catch (Exception e) {
			Logger.info(e.toString());
		} finally {
			oper.close();
		}
        return resultObject;
	}

	  
	public static ObjectNode getOrderTotalInfo(ObjectNode resultObject,int postmanid){
		resultObject.put("completednum", Ebean.getServer(Constants.getDB()).find(PostOrderUser.class).where().eq("postmanid", postmanid).findRowCount());
		resultObject.put("income", "15");
		return resultObject;
	}
	
	
	public static ObjectNode getExpressDoingListObject(ObjectNode resultObject,int uid,int index,int pageSize){
		resultObject.put("endflag", "1");
		List<ExpressInfoVO> expressListVOList = Lists.newArrayList();
		String sql="CALL `sp_postdelivery_doinglist`(?,?,?,?,?)";
		logger.info(sql+"=="+uid+"=="+index+"=="+pageSize);
		// 调用存储过程
		JdbcOper oper = JdbcOper.getCalledbleDao(sql);
		if (oper == null) {
			return null;
		}
		try {
			// 数据库存储过程操作
			oper.cst.setInt(1, uid);
			oper.cst.setInt(2, index);
			oper.cst.setInt(3, pageSize);
			oper.cst.registerOutParameter(4, Types.INTEGER);
			oper.cst.registerOutParameter(5, Types.INTEGER);
			oper.rs = oper.cst.executeQuery();
			String lastindex = "9999";
			while (oper.rs.next()) {
				ExpressInfoVO expressInfoVO=new ExpressInfoVO();
				expressInfoVO.expressid = oper.rs.getString("deliveryid");
				String paystate = oper.rs.getString("need_pay");
				expressInfoVO.needpay=paystate;
				expressInfoVO.state=oper.rs.getString("sta");
				expressInfoVO.receivername = oper.rs.getString("receiver_name");
				String receiverphone=oper.rs.getString("receiver_phone")==null?"":oper.rs.getString("receiver_phone");
				if(receiverphone.length()>12){
					receiverphone=receiverphone.substring(0,12);
				}
				expressInfoVO.receiverphone = receiverphone;
				String receiver_province=oper.rs.getString("receiver_province");
				String receiver_city=oper.rs.getString("receiver_city");
				String receiver_district=oper.rs.getString("receiver_district");
				String receiver_address=oper.rs.getString("receiver_address");
				String receiveraddress="";
				if(!StringUtils.isBlank(receiver_province)){
					receiveraddress =receiveraddress + receiver_province+" ";
				}
				if(!StringUtils.isBlank(receiver_city)){
					receiveraddress = receiveraddress + receiver_city+" " ;
				}
				if(!StringUtils.isBlank(receiver_district)){
					receiveraddress = receiveraddress + receiver_district+" " ;
				}
				if(!StringUtils.isBlank(receiver_address)){
					receiveraddress = receiveraddress + receiver_address;
				}
				expressInfoVO.receiveraddress = receiveraddress;
				expressInfoVO.sendername = oper.rs.getString("sender_name");

				String senderphone=oper.rs.getString("sender_phone")==null?"":oper.rs.getString("sender_phone");
				if(senderphone.length()>12){
					senderphone=senderphone.substring(0,12);
				}
				expressInfoVO.senderphone = senderphone;
				String sender_province=oper.rs.getString("sender_province");
				String sender_city=oper.rs.getString("sender_city");
				String sender_district=oper.rs.getString("sender_district");
				String sender_address=oper.rs.getString("sender_address");
				String senderaddress="";
				if(!StringUtils.isBlank(sender_province)){
					senderaddress =senderaddress + sender_province+" ";
				}
				if(!StringUtils.isBlank(sender_city)){
					senderaddress = senderaddress + sender_city+" " ;
				}
				if(!StringUtils.isBlank(sender_district)){
					senderaddress = senderaddress + sender_district+" " ;
				}
				if(!StringUtils.isBlank(sender_address)){
					senderaddress = senderaddress + sender_address;
				}
				expressInfoVO.expressmoney = oper.rs.getBigDecimal("goods_fee").divide(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP)+"元";
				expressInfoVO.express_money = oper.rs.getBigDecimal("goods_fee").divide(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP)+"";
				expressInfoVO.refusereason=oper.rs.getString("resultmsg");
				expressInfoVO.servicephone="";
				expressInfoVO.expresstime = Dates.formatDate(oper.rs.getTimestamp("date_upd"));
				expressInfoVO.linkurl = "pExpressDetail://lid="+expressInfoVO.expressid;
				expressInfoVO.mailno = oper.rs.getString("mail_num");
				if ("1".equals(paystate)){
					List<ExpressInfoVO.paytypeInfo> pList = Lists.newArrayList();
					ExpressInfoVO.paytypeInfo p1 = new ExpressInfoVO.paytypeInfo();
			    	p1.paytypeid="0";
			    	p1.paytypetitle="现金支付";
			    	pList.add(p1);
			    	ExpressInfoVO.paytypeInfo p2 = new ExpressInfoVO.paytypeInfo();
			    	p2.paytypeid="1";
			    	p2.paytypetitle="刷卡";
			    	pList.add(p2);
			    	expressInfoVO.paytypes=pList;
				}
				expressInfoVO.expresstype = oper.rs.getString("typ");
				switch (oper.rs.getString("typ")) {
					case "1":
						expressInfoVO.expresstypedesc = "签单返回";
						break;
					case "2":
						expressInfoVO.expresstypedesc = "退货";
						break;
					case "3":
						expressInfoVO.expresstypedesc = "换货";
						break;
					case "4":
						expressInfoVO.expresstypedesc = "普通件";
						break;
					case "5":
						expressInfoVO.expresstypedesc = "加急件";
						break;
					default:
						expressInfoVO.expresstypedesc = "普通件";
						break;
				}
				expressListVOList.add(expressInfoVO);
				lastindex = oper.rs.getString("id");
			}
			int total =  oper.cst.getInt(4);//总条数
			if(total>Numbers.parseInt(lastindex, 0)){
				resultObject.put("endflag", "0");//判断结束标志
			}
			int nolooked = oper.cst.getInt(5);//未看条数
			if (nolooked>0){
				resultObject.put("tips", "您有"+nolooked+"条新订单");
			}
			resultObject.put("lastindex", lastindex);
			resultObject.set("expresslist",Json.toJson(expressListVOList));
			
		} catch (Exception e) {
			Logger.info(e.toString());
		} finally {
			oper.close();
		}
		return resultObject;
	}
	
	
	
	
	public static ExpressInfoVO getExpressInfoByID(int id){
		ExpressInfoVO expressInfoVO=new ExpressInfoVO();
		String sql="SELECT *  FROM postdelivery WHERE id="+id;
		// 调用存储过程
		JdbcOper oper = JdbcOper.getCalledbleDao(sql);
		if (oper == null) {
			return null;
		}
		try {
			oper.rs = oper.cst.executeQuery();
			while (oper.rs.next()) {
				expressInfoVO.expressid = oper.rs.getString("id");
				String state=oper.rs.getString("sta")==null?"":oper.rs.getString("sta");
				expressInfoVO.state = state;
				String paystate = oper.rs.getString("pay_status");
				expressInfoVO.receivername = oper.rs.getString("receiver_name");
				String receiverphone=oper.rs.getString("receiver_phone")==null?"":oper.rs.getString("receiver_phone");
				if(receiverphone.length()>12){
					receiverphone=receiverphone.substring(0,12);
				}
				expressInfoVO.receiverphone = receiverphone;
				String receiver_province=oper.rs.getString("receiver_province");
				String receiver_city=oper.rs.getString("receiver_city");
				String receiver_district=oper.rs.getString("receiver_district");
				String receiver_address=oper.rs.getString("receiver_address");
				String receiveraddress="";
				if(!StringUtils.isBlank(receiver_province)){
					receiveraddress =receiveraddress + receiver_province+" ";
				}
				if(!StringUtils.isBlank(receiver_city)){
					receiveraddress = receiveraddress + receiver_city+" " ;
				}
				if(!StringUtils.isBlank(receiver_district)){
					receiveraddress = receiveraddress + receiver_district+" " ;
				}
				if(!StringUtils.isBlank(receiver_address)){
					receiveraddress = receiveraddress + receiver_address;
				}
				expressInfoVO.receiveraddress = receiveraddress;
				expressInfoVO.sendername = oper.rs.getString("sender_name");
				String senderphone=oper.rs.getString("sender_phone")==null?"":oper.rs.getString("sender_phone");
				if(senderphone.length()>12){
					senderphone=senderphone.substring(0,12);
				}
				expressInfoVO.senderphone = senderphone;
				expressInfoVO.expresstype = oper.rs.getString("typ");
				
				switch (oper.rs.getString("typ")) {
					case "1":
						expressInfoVO.expresstypedesc = "签单返回";
						break;
					case "2":
						expressInfoVO.expresstypedesc = "退货";
						break;
					case "3":
						expressInfoVO.expresstypedesc = "换货";
						break;
					case "4":
						expressInfoVO.expresstypedesc = "普通件";
						break;
					case "5":
						expressInfoVO.expresstypedesc = "加急件";
						break;
					default:
						expressInfoVO.expresstypedesc = "普通件";
						break;
				}
				String sender_province=oper.rs.getString("sender_province");
				String sender_city=oper.rs.getString("sender_city");
				String sender_district=oper.rs.getString("sender_district");
				String sender_address=oper.rs.getString("sender_address");
				String senderaddress="";
				if(!StringUtils.isBlank(sender_province)){
					senderaddress =senderaddress + sender_province+" ";
				}
				if(!StringUtils.isBlank(sender_city)){
					senderaddress = senderaddress + sender_city+" " ;
				}
				if(!StringUtils.isBlank(sender_district)){
					senderaddress = senderaddress + sender_district+" " ;
				}
				if(!StringUtils.isBlank(sender_address)){
					senderaddress = senderaddress + sender_address;
				}
				expressInfoVO.senderaddress = senderaddress;
				expressInfoVO.expressmoney = oper.rs.getBigDecimal("goods_fee").divide(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP)+"元";
				expressInfoVO.express_money = oper.rs.getBigDecimal("goods_fee").divide(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP)+"";
				
				expressInfoVO.expresstime = oper.rs.getString("date_upd");
				expressInfoVO.linkurl = "pExpressDetail://lid="+expressInfoVO.expressid;
				expressInfoVO.mailno = oper.rs.getString("mail_num");
				expressInfoVO.needpay = oper.rs.getString("need_pay");
				
				String typ=oper.rs.getString("typ");
				String paymodel =  oper.rs.getString("pay_mode");
				
				expressInfoVO.paymentstatus="";
				if (typ.equals("2")){
					expressInfoVO.paymentstatus="退款金额";
				}else{
					if(state.equals("2") && expressInfoVO.needpay.equals("1")){
						switch(paymodel){
						case "0":
							expressInfoVO.paymentstatus="已付金额（现金）";
							break;
						case "1":
							expressInfoVO.paymentstatus="已付金额（刷卡）";
							break;
						}
					}else{
						if ( expressInfoVO.needpay.equals("1")){
							expressInfoVO.paymentstatus="应付金额";
						}
					}
				}
				List<ExpressInfoVO.paytypeInfo> pList = Lists.newArrayList();
				if ("4".equals(expressInfoVO.expresstype)){//订单类型 1-签单返回 2-退货 3-换货 4-普通件 5-加急件
					ExpressInfoVO.paytypeInfo p1 = new ExpressInfoVO.paytypeInfo();
			    	p1.paytypeid=PayModeStas.Cash.getStatus();
			    	p1.paytypetitle=PayModeStas.Cash.getMessage();
			    	pList.add(p1);
			    	ExpressInfoVO.paytypeInfo p2 = new ExpressInfoVO.paytypeInfo();
			    	p2.paytypeid=PayModeStas.POS.getStatus();
			    	p2.paytypetitle=PayModeStas.POS.getMessage();
			    	pList.add(p2);
				}
				expressInfoVO.paytypes=pList;
				expressInfoVO.refusereason= oper.rs.getString("resultmsg")==null?"":oper.rs.getString("resultmsg");
				expressInfoVO.servicephone="";
			}
		} catch (Exception e) {
			Logger.info(e.toString());
		} finally {
			oper.close();
		}
		return expressInfoVO;
	}


	/**
	 * 根据运单号查询是否存在此配送订单信息
	 * @param id
	 * @return
	 */
	public static Postdelivery getPostdeliveryByid(int id){
		return Ebean.getServer(Constants.getDB()).find(Postdelivery.class,id);
	}


	/**
	 * 根据商户code和异常状态寻找异常列表
	 * @param merchant_code
	 * @param state
	 * @return
	 */
	public static List<DeleveryErrorMessage> getDeleveryErrorMessageList(
			String merchant_code, String state) {
		return Ebean.getServer(Constants.getDB()).find(DeleveryErrorMessage.class).where().eq("merchant_code", merchant_code).eq("state", state).findList();
	}
	
	/**
	 * 根据异常列表ID返回
	 * @param merchant_code
	 * @param state
	 * @return
	 */
	public static DeleveryErrorMessage getDeleveryErrorMessageInfo(Integer reasonid) {
		return Ebean.getServer(Constants.getDB()).find(DeleveryErrorMessage.class).where().eq("id", reasonid).findUnique();
	}


	/**
	 * 数据详情
	 * @param tim
	 * @return
	 */
	public static List<ObjectNode>  getPayModeDataByTim(Integer postmanid,String tim){
		List<ObjectNode> itemsList = Lists.newArrayList();
		String sql="SELECT IFNULL(pay_mode,'') AS pay_mode,COUNT(id) as cnt,IFNULL(SUM(goods_fee),0) as fee FROM postdelivery WHERE postman_id="+postmanid
				+ "	AND DATE_FORMAT(date_upd,'%Y-%m-%d')='"+tim+"' AND flg='1' and sta='2' GROUP BY pay_mode";
		List<SqlRow> rslist=Ebean.getServer(Constants.getDB()).createSqlQuery(sql).findList();
		for(SqlRow rs:rslist){
			ObjectNode item = Json.newObject();
			String pay_mode=rs.getString("pay_mode");
			String receivingname="无需收款";
			//支付方式 （0：现金，1：刷卡，2：微信，3：支付宝，4：其它）
			switch(pay_mode){
			case "4":
				receivingname="无需收款";
				break;
			case "0":
				receivingname="现金收款";
				break;
			case "1":
				receivingname="POS机刷卡收款";
				break;
			case "2":
				receivingname="微信收款";
				break;
			case "3":
				receivingname="支付宝收款";
				break;
			case "5":
				receivingname="退款";
				break;
			}
			item.put("receivingname", receivingname);
			item.put("receivingcount", "("+rs.getInteger("cnt")+")");
			item.put("receivingmoney", "¥"+rs.getBigDecimal("fee").divide(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP));
			itemsList.add(item);
		}
		return itemsList;
	}
	
	/**
	 * 数据详情
	 * @param tim
	 * @return
	 */
	public static List<ObjectNode>  getCompanyDataByTim(Integer postmanid,String tim){
		List<ObjectNode> itemsList = Lists.newArrayList();
		String sql="SELECT sender_company_name,COUNT(id) as cnt,IFNULL(SUM(goods_fee),0) as fee FROM postdelivery WHERE postman_id="+postmanid
				+ "	AND DATE_FORMAT(date_upd,'%Y-%m-%d')='"+tim+"' AND flg='1' and sta='2' GROUP BY sender_company_name";
		List<SqlRow> rslist=Ebean.getServer(Constants.getDB()).createSqlQuery(sql).findList();
		for(SqlRow rs:rslist){
			ObjectNode item = Json.newObject();
			item.put("companyname", rs.getString("sender_company_name"));
			item.put("companycount", "("+rs.getInteger("cnt")+")");
			item.put("companymoney", "¥"+rs.getBigDecimal("fee").divide(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP));
			itemsList.add(item);
		}
		return itemsList;
	}
	
	/**
	 * 数据详情
	 * @param tim
	 * @return
	 */
	public static ObjectNode  getTotalDataByTim(ObjectNode item,Integer postmanid,String tim){
		String sql="SELECT COUNT(id) as cnt,IFNULL(SUM(goods_fee),0) as fee FROM postdelivery WHERE postman_id="+postmanid
				+ "	AND DATE_FORMAT(date_upd,'%Y-%m-%d')='"+tim+"' AND flg='1' and sta='2' and typ<>'2' AND `sender_company_name`<> '聚美商品'";
		List<SqlRow> rslist=Ebean.getServer(Constants.getDB()).createSqlQuery(sql).findList();
		BigDecimal receiving = new BigDecimal(0);
		BigDecimal refund = new BigDecimal(0);
		Integer sendcount = 0;
		for(SqlRow rs:rslist){
			receiving = rs.getBigDecimal("fee").divide(new BigDecimal(100));
			sendcount=rs.getInteger("cnt");
		}
		
		String sql1="SELECT COUNT(id) as cnt,IFNULL(SUM(goods_fee),0) as fee FROM postdelivery WHERE postman_id="+postmanid
				+ "	AND DATE_FORMAT(date_upd,'%Y-%m-%d')='"+tim+"' AND flg='1' and sta='2' and typ='2' AND `sender_company_name`<> '聚美商品'";
		List<SqlRow> rslist1=Ebean.getServer(Constants.getDB()).createSqlQuery(sql1).findList();
		for(SqlRow rs:rslist1){
			refund = rs.getBigDecimal("fee").divide(new BigDecimal(100));
			sendcount+=rs.getInteger("cnt");
		}
		
		BigDecimal totalmoney =receiving.add(refund);
		
		item.put("datetime", tim);
		item.put("totalmoney",""+totalmoney.setScale(2, RoundingMode.HALF_UP));
		item.put("totalmoneystr","¥"+totalmoney.setScale(2, RoundingMode.HALF_UP));
		item.put("receiving", "¥"+receiving.setScale(2, RoundingMode.HALF_UP));
		item.put("refund", "¥"+refund.setScale(2, RoundingMode.HALF_UP));
		item.put("sendcount", ""+sendcount+"单");
		return item;
	}
	
	/**
	 * 数据详情
	 * @param tim
	 * @return
	 */
	public static List<ObjectNode>  getStaDataByTim(Integer postmanid,String tim){
		List<ObjectNode> itemsList = Lists.newArrayList();
		String sql="SELECT sta,COUNT(id) as cnt,IFNULL(SUM(goods_fee),0) as fee FROM postdelivery WHERE postman_id="+postmanid
				+ "	AND date_upd>DATE_ADD('"+tim+"',INTERVAL -7 DAY) AND flg='1' GROUP BY sta";
		List<SqlRow> rslist=Ebean.getServer(Constants.getDB()).createSqlQuery(sql).findList();
		int watiNo = 0; //未完成
		int successNo = 0; //已完成
		int rollBackNo = 0;//滞留 
		int denyNo = 0;//已拒绝 
		for(SqlRow rs:rslist){
			String stastr= rs.getString("sta");
			int cnt= rs.getInteger("cnt");
			if("1".equals(stastr)){
				watiNo +=cnt;
			}
			if("2".equals(stastr)){
				successNo +=cnt;
			}
			if("3".equals(stastr)){
				rollBackNo +=cnt;
			}
			if("4".equals(stastr)){
				denyNo +=cnt;
			}
			if("5".equals(stastr)){
				denyNo +=cnt;
			}
		}
		ObjectNode item2 = Json.newObject();
		item2.put("name", "已完成");
		item2.put("count", successNo);
		item2.put("linkurl", "pExpressSignedList://");
		itemsList.add(item2);
		ObjectNode item3 = Json.newObject();
		item3.put("name", "当天滞留");
		item3.put("count", rollBackNo);
		item3.put("linkurl", "pExpressRetentionList://");
		itemsList.add(item3);
		ObjectNode item4 = Json.newObject();
		item4.put("name", "拒收订单");
		item4.put("count", denyNo);
		item4.put("linkurl", "pExpressRejectList://");
		itemsList.add(item4);
		return itemsList;
	}

	/**
	 * 保存同城快递
	 * @param postOrder
	 * @return
	 */
	public static PostOrder savePostOrder(PostOrder postOrder){
		Ebean.getServer(Constants.getDB()).save(postOrder);
		return postOrder;
	}

	/**
	 * 获取系统消息
	 * @param cardlist
	 * @param uid
	 * @param type
	 * @param index
	 * @param pageSize
     * @return
     */
	public static List<ObjectNode> getMessageListByUid(List<ObjectNode> cardlist, Integer uid,
			int type, int index, int pageSize) {
		String sql="CALL `sp_postcontent_list`(?,?,?,?,?,?)";
		// 调用存储过程
		JdbcOper oper = JdbcOper.getCalledbleDao(sql);
		if (oper == null) {
			return null;
		}
		try {
			// 数据库存储过程操作
			oper.cst.setInt(1, uid);
			oper.cst.setInt(2, type);
			oper.cst.setInt(3, index);
			oper.cst.setInt(4, pageSize);
			oper.cst.registerOutParameter(5, Types.INTEGER);
			oper.cst.registerOutParameter(6, Types.INTEGER);
			oper.rs = oper.cst.executeQuery();
			String lastindex = "0";
			while (oper.rs.next()) {
				lastindex = oper.rs.getString("id");
				ObjectNode card1 = Json.newObject();
				card1.put("id", lastindex);
				card1.put("type", 3);
				String typicon = oper.rs.getString("typicon");
				if(StringUtils.isBlank(typicon)){
					card1.put("typeicon", "");
				}else{
					card1.put("typeicon", Configuration.root().getString("oss.image.url")+typicon);
				}
				card1.put("typename", oper.rs.getString("typname"));
				card1.put("statecolor", "#FF00FF");
				card1.put("title", oper.rs.getString("title"));
				String amount=oper.rs.getString("amount");
				if(!"".equals(amount))
				{
					card1.put("subtitle", amount);
				}else{
					card1.put("subtitle", "");
				}	
				card1.put("datetime", oper.rs.getString("dateremark"));
				card1.put("isnew", oper.rs.getString("isnew"));
				card1.put("linkurl", oper.rs.getString("linkurl"));
				cardlist.add(card1);
			}
		} catch (Exception e) {
			Logger.info(e.toString());
		} finally {
			oper.close();
		}
		return cardlist;
	}


	public static List<OtherApp> getOtherAppList() {
		return  Ebean.getServer(Constants.getDB()).find(OtherApp.class).findList();
	}

	/**
	 * 根据订单ID删除订单用户推送的临时表
	 * @param orderid
     */
	public static void deletePostmanUserTempByOrderid(int orderid) {
		Ebean.getServer(Constants.getDB()).createUpdate(PostmanUserTemp.class,"INSERT INTO postmanuser_templog(postmanid,orderid) SELECT postmanid,orderid FROM postmanuser_temp WHERE orderid=:orderid").setParameter("orderid",orderid).execute();
		Ebean.getServer(Constants.getDB()).createUpdate(PostmanUserTemp.class,"delete from PostmanUserTemp where orderid=:orderid").setParameter("orderid",orderid).execute();
	}
}