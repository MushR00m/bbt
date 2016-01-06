package services.express;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Types;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import models.DeleveryErrorMessage;
import models.Postdelivery;
import models.postman.PostmanUser;
import play.Logger;
import play.libs.Json;
import utils.Constants;
import utils.Constants.PayModeStas;
import utils.Dates;
import utils.JdbcOper;
import utils.Numbers;
import vo.api.ExpressInfoVO;

import com.avaje.ebean.CallableSql;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlRow;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;

public class ExpressService {
	private static final Logger.ALogger logger = Logger.of(ExpressService.class);

	public static List<ExpressInfoVO> getExpressListObject(int uid,String tim,String sta,int index,int pageSize){
		List<ExpressInfoVO> expressListVOList = Lists.newArrayList();
		String sql="CALL `sp_postdelivery_list`(?,?,?,?,?,?,?)";
		// 调用存储过程
		JdbcOper oper = JdbcOper.getCalledbleDaoWithOutTran(sql);
		if (oper == null) {
			return null;
		}
		try {
			// 数据库存储过程操作
			oper.cst.setInt(1, uid);
			oper.cst.setString(2, tim);
			oper.cst.setString(3, sta);
			oper.cst.setInt(4, index);
			oper.cst.setInt(5, pageSize);
			oper.cst.registerOutParameter(6, Types.INTEGER);
			oper.cst.registerOutParameter(7, Types.INTEGER);
			oper.rs = oper.cst.executeQuery();
			while (oper.rs.next()) {
				ExpressInfoVO expressInfoVO=new ExpressInfoVO();
				expressInfoVO.expressid = oper.rs.getString("deliveryid");
				String state=oper.rs.getString("sta");
				String needpay = oper.rs.getString("need_pay");
				expressInfoVO.needpay=needpay;
				expressInfoVO.state = state;
				
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
				expressInfoVO.senderaddress = senderaddress;
				expressInfoVO.expressmoney = oper.rs.getBigDecimal("goods_fee").divide(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP)+"元";
				expressInfoVO.express_money =oper.rs.getBigDecimal("goods_fee").divide(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP)+"";
				expressInfoVO.expresstime =Dates.formatDate(oper.rs.getTimestamp("date_upd"));
				expressInfoVO.linkurl = "pExpressDetail://lid="+expressInfoVO.expressid;
				expressInfoVO.mailno = oper.rs.getString("mail_num");
				List<ExpressInfoVO.paytypeInfo> pList = Lists.newArrayList();
				if ("1".equals(needpay)){
			    	ExpressInfoVO.paytypeInfo p1 = new ExpressInfoVO.paytypeInfo();
			    	p1.paytypeid="0";
			    	p1.paytypetitle="现金支付";
			    	pList.add(p1);
			    	ExpressInfoVO.paytypeInfo p2 = new ExpressInfoVO.paytypeInfo();
			    	p2.paytypeid="1";
			    	p2.paytypetitle="刷卡";
			    	pList.add(p2);
				}
				expressInfoVO.expresstype = oper.rs.getString("typ");
				
				switch (expressInfoVO.expresstype) {
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
				if("4".equals(expressInfoVO.state) && ("WANBO".equals(oper.rs.getString("company_code"))||"EXPLINK".equals(oper.rs.getString("company_code")))){//不显示退货至站点按钮，需要将状态由4改成5
					expressInfoVO.state="5";//5已退货至站点
				}
				expressInfoVO.paytypes=pList;
				expressListVOList.add(expressInfoVO);
			}
		} catch (Exception e) {
			Logger.info(e.toString());
		} finally {
			oper.close();
		}
		return expressListVOList;
	}

	public static void setDeleiveryLooked(Integer uid){
		String sql="UPDATE `postdelivery` SET islooked=1 WHERE postman_id="+uid;
		Ebean.getServer(Constants.getDB()).createSqlUpdate(sql).execute();	
	    return;
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

	  
	public static ObjectNode getExpressSearchListObject(ObjectNode resultObject,int uid,String key,int index,int pageSize){
		resultObject.put("endflag", "1");
		List<ExpressInfoVO> expressListVOList = Lists.newArrayList();
		String sql="CALL `sp_postdelivery_searchlist`(?,?,?,?,?)";
		logger.info(sql+"=="+uid+"=="+index+"=="+pageSize);
		// 调用存储过程
		JdbcOper oper = JdbcOper.getCalledbleDao(sql);
		if (oper == null) {
			return null;
		}
		try {
			// 数据库存储过程操作
			oper.cst.setInt(1, uid);
			oper.cst.setString(2, key);
			oper.cst.setInt(3, index);
			oper.cst.setInt(4, pageSize);
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
				expressInfoVO.senderaddress = senderaddress;
				expressInfoVO.expresstype = oper.rs.getString("typ");
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
			int total =  oper.cst.getInt(5);//总条数
			if(total>Numbers.parseInt(lastindex, 0)){
				resultObject.put("endflag", "0");//判断结束标志
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
	
	
	public static ObjectNode getExpressListObject(ObjectNode resultObject, int uid,String sta,int index,int pageSize){
		resultObject.put("endflag", "1");
		List<ObjectNode> dayList = Lists.newArrayList();//数据库里查询出来的数据存储
		String sql="CALL `sp_postdeliveryday_list`(?,?,?,?,?)";
		// 调用存储过程
		JdbcOper oper = JdbcOper.getCalledbleDao(sql);
		if (oper == null) {
			return resultObject;
		}
		try {
			// 数据库存储过程操作
			oper.cst.setInt(1, uid);
			oper.cst.setString(2, sta);
			oper.cst.setInt(3, index);
			oper.cst.setInt(4, pageSize);
			oper.cst.registerOutParameter(5, Types.INTEGER);
			oper.rs = oper.cst.executeQuery();
			String lastindex = "9999";
			while (oper.rs.next()) {
				ObjectNode dayInfo = Json.newObject();
				lastindex = oper.rs.getString("id");
				String tim = oper.rs.getString("tim");
				Date d = new Date();
				
				if(tim.equals(Dates.formatDate2(new Date()))){
					dayInfo.put("daytitle","今天");	
				}else{
					if(tim.equals(Dates.formatDate2(new Date(d.getTime() - 1 * 24 * 60 * 60 * 1000L)))){
						dayInfo.put("daytitle","昨天");	
					}else{
						dayInfo.put("daytitle",tim);
					}
				}
				
				List<ExpressInfoVO> expresslist =  Lists.newArrayList();
				expresslist=getExpressListObject(uid,tim,sta,0,300);
				dayInfo.set("expresslist", Json.toJson(expresslist));
				dayList.add(dayInfo);
			}
			int total =  oper.cst.getInt(5);//总条数
			if(total>Numbers.parseInt(lastindex, 0)){
				resultObject.put("endflag", "0");//判断结束标志
			}
			resultObject.put("lastindex", lastindex);
			resultObject.put("tips", "");
			resultObject.set("daylist", Json.toJson(dayList));
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
	 * @param code
	 * @return
	 */
	public static Postdelivery getPostdeliveryByid(int id){
		return Ebean.getServer(Constants.getDB()).find(Postdelivery.class,id);
	}
	/**
	 * 签收返现
	 * @param expressid
	 */
	public static void saveIncome(int expressid) {
		String sql = "CALL `sp_balance_deliveryadd`(?)";
		logger.info(sql+"========"+expressid);
		CallableSql cs = Ebean.getServer(Constants.getDB()).createCallableSql(sql).setParameter(1, expressid);
		Ebean.getServer(Constants.getDB()).execute(cs);
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
	 * 根据异常类型寻找异常列表0落地配1同城
	 * @return
	 */
	public static List<DeleveryErrorMessage> getDeleveryErrorMessageByType(String type) {
		return Ebean.getServer(Constants.getDB()).find(DeleveryErrorMessage.class).where().eq("typ", type).findList();
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
	 * 保存快递配送
	 * @param postdelivery
	 * @return
	 */
	public static Postdelivery savePostdelivery(Postdelivery postdelivery){
		Ebean.getServer(Constants.getDB()).save(postdelivery);
		return postdelivery;
	}
	
	
}