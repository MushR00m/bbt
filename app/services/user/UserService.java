package services.user;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.avaje.ebean.CallableSql;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.PagedList;
import com.avaje.ebean.SqlRow;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;

import models.Deviceuser;
import models.Postcompany;
import models.Versioninfo;
import models.postcontent.PostcontentImg;
import models.postcontent.PostcontentUser;
//import models.postcontent.PostcontentUser;
import models.postman.Balance;
import models.postman.BalanceIncome;
import models.postman.BalanceWithdraw;
import models.postman.PostmanUser;
import models.postman.PostmanUserLocationLog;
import models.postman.Reddot;
import play.Configuration;
import play.Logger;
import play.libs.Json;
import utils.Constants;
import utils.Dates;
import utils.ErrorCode;
import utils.JdbcOper;
import utils.Numbers;
import utils.PinyinUtil;
import vo.api.HomePageVO;
import vo.api.PostcontentVO;
public class UserService {
	SimpleDateFormat CHINESE_DATE_TIME_FORMAT_NORMAL_DAY = new SimpleDateFormat("yyyy-MM-dd");
	private static final Logger.ALogger logger = Logger.of(UserService.class);

	/**
	 * 查找是否有新版本
	 * @param devicetype
	 * @param marketcode
	 * @param current_app_version
	 * @return
	 */
	public static Versioninfo getNewVersioninfo(String devicetype, String marketcode, String current_app_version) {
		List<Versioninfo> versioninfoList = Ebean.getServer(Constants.getDB()).find(Versioninfo.class).where()
				.eq("ostype", devicetype).eq("marketcode", marketcode).eq("sta", "3").orderBy("id desc").findList();
		if(versioninfoList.isEmpty()){
			versioninfoList = Ebean.getServer(Constants.getDB()).find(Versioninfo.class).where()
					.eq("ostype", devicetype).eq("marketcode", "0000").eq("sta", "3").orderBy("id desc").findList();//全局查找
			if(versioninfoList.isEmpty()){
				return null;
			}else{
				Versioninfo versioninfo = versioninfoList.get(0);
				if(current_app_version.equals(versioninfo.getLatestver())){
					return null;
				}else{
					return versioninfo;
				}
			}
			
		}else{
			Versioninfo versioninfo = versioninfoList.get(0);
			if(current_app_version.equals(versioninfo.getLatestver())){
				return null;
			}else{
				return versioninfo;
			}
		}
	}
	
	/**
	 * 根据快递员手机查询此快递员信息
	 * sta为1的，即正常
	 * @param phone
	 * @return
	 */
	public static PostmanUser getPostManUserByPhone(String phone){
		return Ebean.getServer(Constants.getDB()).find(PostmanUser.class).where().eq("phone", phone).ne("sta", "2").findUnique();
	}
	
	/**
	 * 根据快递员手机查询此快递员信息
	 * sta为1的，即正常
	 * @param phone
	 * @return
	 */
	public static PostmanUser getPostManUserByPhone_all(String phone){
		return Ebean.getServer(Constants.getDB()).find(PostmanUser.class).where().eq("phone", phone).findUnique();
	}
	
	/**
	 * 根据快递员ID查询此快递员信息
	 * @param id
	 * @return
	 */
	public static PostmanUser getPostManUserById(Integer id){
		return Ebean.getServer(Constants.getDB()).find(PostmanUser.class,id);
	}
	
	/**
	 * 根据快递员token查询此快递员信息
	 * @param token
	 * @return
	 */
	public static PostmanUser getPostManUserByToken(String token){
		return Ebean.getServer(Constants.getDB()).find(PostmanUser.class).where().eq("token",token).findUnique();
	}
	
	
	/**
	 * 保存快递员信息
	 * @param postmanUser
	 * @return
	 */
	public static PostmanUser savePostuserman(PostmanUser postmanUser){
		 Ebean.getServer(Constants.getDB()).save(postmanUser);
		 return postmanUser;
	}
	/**
	 * 根据设备ID获取该设备信息
	 * @param devid
	 * @return
	 */
	public static Deviceuser getDeviceuserByDevid(String devid){
		return  Ebean.getServer(Constants.getDB()).find(Deviceuser.class).where().eq("deviceid", devid).findUnique();
	}
	/**
	 * 保存设备信息
	 * @param deviceuser
	 * @return
	 */
	public static Deviceuser saveDeviceUser(Deviceuser deviceuser){
		 Ebean.getServer(Constants.getDB()).save(deviceuser);
		 return deviceuser;
	}
	/**
	 * 根据用户id去解除与设备之间的绑定
	 * @param uid
	 * @return
	 */
	public static int unbindDeviceByPostmanid(Integer uid){
		return Ebean.getServer(Constants.getDB()).createUpdate(Deviceuser.class, "update Deviceuser set uid=0 where uid = :uid").setParameter("uid", uid).execute();
	}
	/**
	 * 对用户与设备进行绑定
	 * @param uid
	 * @param devid
	 * @return
	 */
	public static int bindDevice(Integer uid,String devid){
		return Ebean.getServer(Constants.getDB())
				.createUpdate(Deviceuser.class, "update Deviceuser set uid=:uid where deviceid = :deviceid")
				.setParameter("uid", uid).setParameter("deviceid", devid).execute();
	}
	/**
	 * 根据公司ID获取公司信息
	 * @param id
	 * @return
	 */
	public static Postcompany getPostcompanyById(Integer id){
		return  Ebean.getServer(Constants.getDB()).find(Postcompany.class,id);
	}
	
	/**
	 * 获取热门公司列表
	 * @return
	 */
	public static List<Postcompany> getHotPostcompany(){
		List<Postcompany> companyList =  Ebean.getServer(Constants.getDB()).find(Postcompany.class).where().eq("ishot", "1").orderBy("nsort desc").findList();
		return companyList;
	}
	/**
	 * 获取所有公司列表
	 * @return
	 */
	public static List<Postcompany> getAllPostcompany(){
		List<Postcompany> companyList =  Ebean.getServer(Constants.getDB()).find(Postcompany.class).findList();
		for(Postcompany postcompany:companyList){
			postcompany.setFirstPinyin(PinyinUtil.getFirstPingYin(postcompany.getCompanyname()));
		}
		Comparator<Postcompany> comparator = new Comparator<Postcompany>() {
			public int compare(Postcompany o1, Postcompany o2) {
				return o1.getFirstPinyin().compareTo(o2.getFirstPinyin());
			}
		};
		Collections.sort(companyList, comparator);//按照拼音进行排序
		return companyList;
	}
	
	
	/**
	 * 根据用户ID获取用户余额信息
	 * @param uid
	 * @return
	 */
	public static Balance getBalanceByUid(Integer uid){
		List<Balance> balanceList = Ebean.getServer(Constants.getDB()).find(Balance.class).where().eq("uid", uid).findList();
		if(balanceList.isEmpty()){
			return null;
		}else{
			return balanceList.get(0);
		}
	}
	
	
	/**
	 * 根据用户id/任务ID修改该用户的任务已读情况
	 * @param uid
	 * @param pcid
	 * @return
	 */
	
	public static int setPostContentUserLooked(Integer uid,Integer pcid){
		return Ebean.getServer(Constants.getDB())
				.createUpdate(PostcontentUser.class, "UPDATE postcontent_user SET isnew='0' WHERE uid= :uid AND pcid= :pcid")
				.setParameter("uid", uid).setParameter("pcid", pcid).execute();
	}
	
	/**
	 * 首页数据
	 * @param uid
	 * @param type
	 * @param index
	 * @param pageSize
	 * @return
	 */
	public static ObjectNode getHomePageVOListByUid(ObjectNode resultObject, int uid,int type,int index,int pageSize){
		resultObject.put("endflag", "1");
		List<HomePageVO> result = Lists.newArrayList();//拼装完成后的数据存储（先放总结，再放resultTemp）
		List<HomePageVO> resultTemp = Lists.newArrayList();//数据库里查询出来的数据存储
		String sql="CALL `sp_postcontent_list`(?,?,?,?,?,?)";
		// 调用存储过程
		JdbcOper oper = JdbcOper.getCalledbleDao(sql);
		if (oper == null) {
			return resultObject;
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
				HomePageVO homePageVO=new HomePageVO();
				homePageVO.id=oper.rs.getString("id");
				homePageVO.contentid=oper.rs.getString("contentid");
				homePageVO.typ=oper.rs.getInt("typ");
				homePageVO.typname=oper.rs.getString("typname");
				homePageVO.typicon=oper.rs.getString("typicon");
				if(StringUtils.isBlank(homePageVO.typicon)){
					homePageVO.typicon="";
				}else{
					homePageVO.typicon=Configuration.root().getString("oss.image.url")+homePageVO.typicon;
				}
				homePageVO.title=oper.rs.getString("title");
				homePageVO.content=oper.rs.getString("content");
				homePageVO.linkurl=oper.rs.getString("linkurl");
				homePageVO.expectamount=oper.rs.getInt("expectamount");
				homePageVO.amount=oper.rs.getString("amount");
				if(!homePageVO.amount.equals(""))
				{
					homePageVO.subtitle=homePageVO.amount;
					homePageVO.tips="额外奖励";
				}else{
					homePageVO.subtitle="";
					homePageVO.tips="";
				}	
				homePageVO.dateremark=oper.rs.getString("dateremark");
				homePageVO.start_tim=oper.rs.getString("start_tim");
				homePageVO.end_tim=oper.rs.getString("end_tim");
				homePageVO.nsort=oper.rs.getInt("nsort");
				homePageVO.sta=oper.rs.getString("sta");
				homePageVO.isnew = oper.rs.getString("isnew");
				homePageVO.date_new=oper.rs.getTimestamp("date_new");
				homePageVO.date_upd=oper.rs.getTimestamp("date_upd");
				lastindex = homePageVO.id;
				resultTemp.add(homePageVO);
			}
			int total =  oper.cst.getInt(5);//总条数
			int totalCount =  oper.cst.getInt(6);//总任务数
			if(total>Numbers.parseInt(lastindex, 0)){
				resultObject.put("endflag", "0");//判断结束标志
			}
			resultObject.put("lastindex", lastindex);
			//接下来处理type为1的数据，即总结的数据，同时此数据应放在首位，故list中先add
			if (index<=0)
			{
				HomePageVO homePageVO=new HomePageVO();
				homePageVO.typ=0;
				homePageVO.dateremark = Dates.formatMonthAndDay(new Date())+"\n"+ Dates.format2Week(new Date());
				
				Integer expectamount = 0;
				for(HomePageVO homePageVOTemp:resultTemp){
					expectamount += homePageVOTemp.expectamount;
				}
				homePageVO.title = "全部完成预计增加收入 "+new BigDecimal(expectamount/100).setScale(2,BigDecimal.ROUND_HALF_UP)+" 元";
				homePageVO.subtitle = String.valueOf(totalCount);
				homePageVO.tips = "个任务需要今天完成";
				homePageVO.linkurl = "";
				result.add(homePageVO);
				
				HomePageVO homePageVO1=getDeliveryTotal(uid,Dates.formatDate(new Date(), "yyyy-MM-dd"));
				if (homePageVO1!=null){
					result.add(homePageVO1);
				}
			}
			result.addAll(resultTemp);
		} catch (Exception e) {
			Logger.info(e.toString());
		} finally {
			oper.close();
		}
		//接下来对查询出来的数据进行组装，组装成APP需要的数据格式
		List<ObjectNode> cardlistList = Lists.newArrayList();
	 		
        for(HomePageVO homePageVO:result){
        	if(homePageVO.typ==null){
        		continue;
        	}
        	ObjectNode taskinfo = Json.newObject();
        	switch (homePageVO.typ.intValue()) {
        	case 0://总结
				taskinfo.put("id", "");
				taskinfo.put("type", "1");
				taskinfo.put("title", homePageVO.title);
				taskinfo.put("subtitle", homePageVO.subtitle);
				taskinfo.put("tips", homePageVO.tips);
				taskinfo.put("datetime", homePageVO.dateremark);
				taskinfo.put("linkurl", homePageVO.linkurl);
				cardlistList.add(taskinfo);
				break;
			case 1://DAILY(1, "日常任务"), AWARD(2, "黄金任务"), SIGNIN(3, "签到任务 "), SYSINFO(4, "系统消息"), NEW(5, "新闻");
				taskinfo.put("id", homePageVO.contentid);
				taskinfo.put("type", "2");
				taskinfo.put("typeicon",homePageVO.typicon);
				taskinfo.put("typename",homePageVO.typname);
				taskinfo.put("state",homePageVO.sta);
				if("1".equals(homePageVO.sta)){
					taskinfo.put("statename","已完成");
				}else{
					taskinfo.put("statename","进行中");
				}
				taskinfo.put("statecolor", "#b3d465");
				taskinfo.put("title", homePageVO.title);
				taskinfo.put("subtitle", homePageVO.subtitle);
				taskinfo.put("tips", homePageVO.tips);
				taskinfo.put("datetime", homePageVO.dateremark);
				taskinfo.put("linkurl", homePageVO.linkurl);
				taskinfo.put("isnew", homePageVO.isnew);
				cardlistList.add(taskinfo);
				break;
			case 2:
				taskinfo.put("id", homePageVO.contentid);
				taskinfo.put("type", "2");
				taskinfo.put("typeicon",homePageVO.typicon);
				taskinfo.put("typename",homePageVO.typname);
				taskinfo.put("state",homePageVO.sta);
				if("1".equals(homePageVO.sta)){
					taskinfo.put("statename","已完成");
				}else{
					taskinfo.put("statename","进行中");
				}
				taskinfo.put("statecolor", "#f39800");
				taskinfo.put("title", homePageVO.title);
				taskinfo.put("subtitle", homePageVO.subtitle);
				taskinfo.put("tips", homePageVO.tips);
				taskinfo.put("datetime", homePageVO.dateremark);
				taskinfo.put("linkurl", homePageVO.linkurl);
				taskinfo.put("isnew", homePageVO.isnew);
				cardlistList.add(taskinfo);
				break;
			case 3:
				taskinfo.put("id", homePageVO.contentid);
				taskinfo.put("type", "2");
				taskinfo.put("typeicon",homePageVO.typicon);
				taskinfo.put("typename",homePageVO.typname);
				taskinfo.put("state",homePageVO.sta);
				if("1".equals(homePageVO.sta)){
					taskinfo.put("statename","已完成");
				}else{
					taskinfo.put("statename","进行中");
				}
				taskinfo.put("statecolor", "#7ecef4");
				taskinfo.put("title", homePageVO.title);
				taskinfo.put("subtitle", homePageVO.subtitle);
				taskinfo.put("tips", homePageVO.tips);
				taskinfo.put("datetime", homePageVO.dateremark);
				taskinfo.put("linkurl", homePageVO.linkurl);
				taskinfo.put("isnew", homePageVO.isnew);
				cardlistList.add(taskinfo);
				break;
			case 4:
				taskinfo.put("id", homePageVO.contentid);
				taskinfo.put("type", "3");
				taskinfo.put("typeicon",homePageVO.typicon);
				taskinfo.put("typename",homePageVO.typname);
				taskinfo.put("title", homePageVO.title);
				taskinfo.put("subtitle", homePageVO.content);
				taskinfo.put("datetime", homePageVO.dateremark);
				taskinfo.put("linkurl", homePageVO.linkurl);
				taskinfo.put("isnew", homePageVO.isnew);
				taskinfo.put("statecolor", "#ff374d");
				cardlistList.add(taskinfo);
				break;	
			case 6:
				taskinfo.put("id", "");
				taskinfo.put("type", "4");
				taskinfo.put("typeicon","http://omspic.higegou.com/upload/homeImg/turn_the_task.png");
				taskinfo.put("typename",homePageVO.typname);
				taskinfo.put("title", homePageVO.title);
				taskinfo.put("subtitle", homePageVO.subtitle);
				taskinfo.put("linkurl", homePageVO.linkurl);
				taskinfo.put("statecolor", "#ff374d");
				cardlistList.add(taskinfo);
				break;	
			default:
				break;
			}
        }
      
        resultObject.set("cardlist", Json.toJson(cardlistList));
        return resultObject;
	}
	
	
	public static HomePageVO getDeliveryTotal(int uid,String tim)
	{
		PostmanUser postmanUser=getPostManUserById(uid);
		if(postmanUser!=null){
			Postcompany postcompany = UserService.getPostcompanyById(postmanUser.getCompanyid());
			if(postcompany==null){
				return null;
			}else{
				if("1".equals(postcompany.getDeliveryflag())){//只有可派单的公司才展示
					Integer nolookedcnt=0;
					Integer newcnt=0;
					String sql="SELECT COUNT(id) as cnt FROM postdelivery WHERE postman_id="+uid+" AND islooked='0' AND sta='1' and `sender_company_name`<> '聚美商品' and flg='1' AND DATE_FORMAT(date_upd,'%Y-%m-%d')='"+tim+"'";
					List<SqlRow> rslist=Ebean.getServer(Constants.getDB()).createSqlQuery(sql).findList();
					for(SqlRow rs:rslist){
						nolookedcnt=rs.getInteger("cnt");
					}
					
					String sql1="SELECT COUNT(id) as cnt FROM postdelivery WHERE postman_id="+uid+" AND sta='1' and `sender_company_name`<> '聚美商品' and flg='1' AND DATE_FORMAT(date_upd,'%Y-%m-%d')='"+tim+"'";
					List<SqlRow> rslist1=Ebean.getServer(Constants.getDB()).createSqlQuery(sql1).findList();
					for(SqlRow rs:rslist1){
						newcnt=rs.getInteger("cnt");
					}
					HomePageVO homePageVO=new HomePageVO();
					homePageVO.typ=6;
					homePageVO.typname="配送订单";
					homePageVO.id="-1";
					homePageVO.dateremark = "";
					homePageVO.title = "您今天有"+newcnt+"个未配送订单";
					homePageVO.subtitle = ""+nolookedcnt;
					homePageVO.tips = "";
					homePageVO.linkurl = "pExpressList://";
					return homePageVO;
				}else{
					return null;
				}
			}
			
		}else{
			return null;
		}
		
	}
	
	public static int getMessageCnt(int uid){
		int msgcnt=0;
		String sql="SELECT COUNT(DISTINCT p.id) as cnt FROM postcontent p,postcontent_user pu WHERE p.id = pu.pcid AND pu.uid="+uid+" AND p.sta='1' AND p.typ=4";
		List<SqlRow> rslist=Ebean.getServer(Constants.getDB()).createSqlQuery(sql).findList();
		for(SqlRow rs:rslist){
			msgcnt=rs.getInteger("cnt");
		}
		if(msgcnt>0){
			return msgcnt;	
		}else{
			return -1;
		}
	}
	
	public static int getpH5SignIn(int uid){
		int msgcnt=0;
		String sql="SELECT COUNT(DISTINCT p.id) as cnt FROM postcontent p,postcontent_user pu WHERE p.id = pu.pcid AND pu.uid="+uid+" AND p.sta='1' AND p.typ=3 and pu.sta='1'";
		List<SqlRow> rslist=Ebean.getServer(Constants.getDB()).createSqlQuery(sql).findList();
		for(SqlRow rs:rslist){
			msgcnt=rs.getInteger("cnt");
		}
		
		if(msgcnt>0){
			return -2;	
		}else{
			return -1;
		}
	}
	
	
	public static PostcontentVO getDeliveryWidgetTotal(int uid,String tim)
	{
		PostmanUser uInfo=getPostManUserById(uid);
		Integer nolookedcnt=0;
		Integer newcnt=0;
		String sql="SELECT COUNT(id) as cnt FROM postdelivery WHERE postman_id="+uid+" AND islooked='0' AND sta='1' and `sender_company_name`<> '聚美商品' and flg='1' AND DATE_FORMAT(date_upd,'%Y-%m-%d')='"+tim+"'";
		List<SqlRow> rslist=Ebean.getServer(Constants.getDB()).createSqlQuery(sql).findList();
		for(SqlRow rs:rslist){
			nolookedcnt=rs.getInteger("cnt");
		}
		
		String sql1="SELECT COUNT(id) as cnt FROM postdelivery WHERE postman_id="+uid+" AND sta='1' and `sender_company_name`<> '聚美商品' and flg='1' AND DATE_FORMAT(date_upd,'%Y-%m-%d')='"+tim+"'";
		List<SqlRow> rslist1=Ebean.getServer(Constants.getDB()).createSqlQuery(sql1).findList();
		for(SqlRow rs:rslist1){
			newcnt=rs.getInteger("cnt");
		}
		PostcontentVO contentVO1=new PostcontentVO();
		contentVO1.id="-1";
		contentVO1.type="6";
		contentVO1.typename="配送订单";
		contentVO1.typeicon="";
		if (!StringUtils.isBlank(contentVO1.typeicon)){
			contentVO1.typeicon=Configuration.root().getString("oss.image.url")+contentVO1.typeicon;
		}
		contentVO1.title="配送订单";
		contentVO1.subtitle="您今天有"+newcnt+"单未配送订单";
		contentVO1.content="";
		contentVO1.statecolor="";
		contentVO1.linkurl="pExpressList://";
		contentVO1.amount="0";
		contentVO1.tips=""+nolookedcnt;
		contentVO1.datetime="";
		contentVO1.nsort="999999";
		contentVO1.state="1";
		contentVO1.statename="已完成";
		contentVO1.t="";
		return contentVO1;
	}
	
	/**
	 * 直接提现不输入金额，直接使用canuser
	 * @param postmanid
	 * @return
	 */
	public static String withDraw(Integer postmanid){
		String moneystr ="-2";
		Ebean.getServer(Constants.getDB()).beginTransaction();
		try {
			Balance balance = getBalanceByUid(postmanid);
	    	if(balance==null){//用户资金信息不存在
				return "-1";
	    	}
	    	if(balance.getBalance()<balance.getCanuse()){
				balance.setCanuse(balance.getBalance());
			}
	    	if(balance.getCanuse()<=0){
	    		return "0";
	    	}
	    	moneystr = Numbers.intToStringWithDiv(balance.getCanuse(), 100)+"";
			BalanceWithdraw balanceWithdraw = new BalanceWithdraw();
	    	balanceWithdraw.setAmount(balance.getCanuse());
	    	balanceWithdraw.setUid(balance.getUid());
	    	balanceWithdraw.setRemark("提现金额："+balance.getCanuse()+"元, 提现申请时间："+Dates.formatDateTime(new Date()));
	    	balanceWithdraw.setSta("1");//设置状态为申请中
	    	balanceWithdraw.setDateNew(new Date());
	    	balanceWithdraw.setDateUpd(new Date());
	    	Ebean.getServer(Constants.getDB()).save(balanceWithdraw);
			Integer balanceNow = balance.getBalance()-balance.getCanuse();
			Integer balanceCanuse = 0;
	    	Integer withdrawNow = balance.getWithdraw()+balance.getCanuse();//已 提现金额
	    	if(balanceNow<balance.getCanuse()){
	    		balance.setCanuse(balanceNow);
	    	}
	    	balance.setBalance(balanceNow);
	    	balance.setWithdraw(withdrawNow);
	    	balance.setCanuse(balanceCanuse);
	    	balance.setDateUpd(new Date());
	    	Ebean.getServer(Constants.getDB()).save(balance);
	    	
	    	Reddot reddot = getReddotByUid(balance.getUid());
	    	if(reddot==null){
	    		reddot = new Reddot();
	    		reddot.setDateNew(new Date());
	    		reddot.setDateUpd(new Date());
	    		reddot.setUpgrade("0");
	    		reddot.setMyfav("0");
	    		reddot.setUid(balance.getUid());
	    		reddot.setWallet_incoming("0");
	    	}
	    	reddot.setWallet_withdraw("1");
	    	saveReddot(reddot);
	    	Ebean.getServer(Constants.getDB()).commitTransaction();
		} catch(Exception e){
			moneystr = "0";
			Ebean.getServer(Constants.getDB()).rollbackTransaction();
		}finally {
			Ebean.getServer(Constants.getDB()).endTransaction();
		}
    	return moneystr;
	}
	/**
	 * 根据用户ID获取提现明细
	 * @param uid
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public static PagedList<BalanceWithdraw> getBalanceWithdrawPageByUid(int uid,int page,int pageSize){
		return Ebean.getServer(Constants.getDB()).find(BalanceWithdraw.class).where().eq("uid", uid).orderBy("id desc").findPagedList(page, pageSize);
	}
	/**
	 * 根据提现ID获取提现详情
	 * @param id
	 * @return
	 */
	public static BalanceWithdraw getBalanceWithdrawById(int id){
		return Ebean.getServer(Constants.getDB()).find(BalanceWithdraw.class,id);
	}
	/**
	 * 根据用户ID获取收支明细
	 * @param uid
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public static PagedList<BalanceIncome> getBalanceIncomePageByUid(int uid,int page,int pageSize){
		return Ebean.getServer(Constants.getDB()).find(BalanceIncome.class).where().eq("uid", uid).orderBy("id desc").findPagedList(page, pageSize);
	}
	/**
	 * widget数据
	 * @param postmanuser
	 * @param type
	 * @param index
	 * @param pageSize
	 * @return
	 */
	public static List<PostcontentVO> getPostcontentListByUid(PostmanUser postmanuser,int type,int index,int pageSize){
		List<PostcontentVO> result = Lists.newArrayList();
		String sql="CALL `sp_postcontentWidget_list`(?,?,?,?,?)";
		// 调用存储过程
		JdbcOper oper = JdbcOper.getCalledbleDao(sql);
		if (oper == null) {
			return result;
		}
		try {
			// 数据库存储过程操作
			oper.cst.setInt(1, postmanuser.getId());
			oper.cst.setInt(2, type);
			oper.cst.setInt(3, index);
			oper.cst.setInt(4, pageSize);
			oper.cst.registerOutParameter(5, Types.INTEGER);
			oper.rs = oper.cst.executeQuery();
			while (oper.rs.next()) {
				PostcontentVO contentVO=new PostcontentVO();
				contentVO.id=oper.rs.getString("contentid");
				contentVO.type=oper.rs.getString("typ");
				contentVO.typename=oper.rs.getString("typname");
				contentVO.typeicon=oper.rs.getString("typicon");
				if (!StringUtils.isBlank(contentVO.typeicon)){
					contentVO.typeicon=Configuration.root().getString("oss.image.url")+contentVO.typeicon;
				}
				contentVO.title=oper.rs.getString("title");
				contentVO.subtitle=oper.rs.getString("subtitle");
				contentVO.content=oper.rs.getString("content");
				String titlename = "";
				switch(contentVO.type){
					case "1":
						contentVO.statecolor="#b3d465";
						titlename="任务详情";
						break;
					case "2":
						titlename="任务详情";
						contentVO.statecolor="#f39800";
						break;
					case "3":
						contentVO.type="1";
						contentVO.statecolor="#b3d465";
						titlename="任务详情";
						break;
					case "4":
						titlename="消息详情";
						contentVO.statecolor="#ff374d";
						break;
					case "5":
						titlename="新闻详情";
						contentVO.statecolor="#ff374d";
						break;
				}
				contentVO.linkurl=oper.rs.getString("linkurl")+"&ltitle="+titlename;
				//contentVO.expectamount=oper.rs.getString("expectamount");
				contentVO.amount=oper.rs.getString("amount");
				//contentVO.amount= new BigDecimal(oper.rs.getDouble("amount")/100).setScale(2,BigDecimal.ROUND_HALF_UP)+"元";
				contentVO.tips=oper.rs.getString("tips");
				contentVO.datetime=oper.rs.getString("dateremark");
				contentVO.nsort=oper.rs.getString("nsort");
				contentVO.state=oper.rs.getString("sta");
				switch(contentVO.state){
					case "0":
						contentVO.statename="未完成";
						break;
					case "1":
						contentVO.statename="已完成";
						break;	
				}
				contentVO.t=String.valueOf(oper.rs.getDate("date_upd").getTime());
				if (contentVO.type.equals("5")){
					List<String> imglist =  Lists.newArrayList();
					List<PostcontentImg> PostcontentImgList=Ebean.getServer(Constants.getDB()).find(PostcontentImg.class).where().eq("pcid", contentVO.id).findList();
					for(PostcontentImg m:PostcontentImgList){
						imglist.add(Configuration.root().getString("oss.image.url")+m.getImgurl()+"@200w");
			    	}
					contentVO.imglist=imglist;
				}
				result.add(contentVO);
			}
			
			Postcompany postcompany = UserService.getPostcompanyById(postmanuser.getCompanyid());
			if(postcompany!=null){
				if("1".equals(postcompany.getDeliveryflag())){//只有可派单的公司才展示
					PostcontentVO contentVO1=getDeliveryWidgetTotal(postmanuser.getId(),Dates.formatDate(new Date(), "yyyy-MM-dd"));
					if (contentVO1!=null){
						result.add(contentVO1);
					}
				}
			}
			
			return result;
		} catch (Exception e) {
			Logger.info(e.toString());
			return result;
		} finally {
			oper.close();
		}
	}
	
	
	
	
	/**
	 * widget数据详情
	 * @param id
	 * @return
	 */
	public static PostcontentVO getPostcontentInfoByid(int id){
		PostcontentVO contentVO = new PostcontentVO();
		String sql="SELECT * FROM postcontent WHERE id="+id;
		List<SqlRow> rslist=Ebean.getServer(Constants.getDB()).createSqlQuery(sql).findList();
		for(SqlRow rs:rslist){
			contentVO.id=rs.getString("id");
			contentVO.type=rs.getString("typ");
			if (contentVO.type.equals("3")){
				contentVO.type="1";
			}
			contentVO.typename=rs.getString("typname");
			contentVO.typeicon=rs.getString("typicon");
			if (contentVO.typeicon!=null){
				contentVO.typeicon=Configuration.root().getString("oss.image.url")+contentVO.typeicon;
			}
			contentVO.title=rs.getString("title");
			contentVO.subtitle=rs.getString("subtitle");
			contentVO.content=rs.getString("content");
			contentVO.linkurl=rs.getString("linkurl");
			contentVO.amount= new BigDecimal(rs.getDouble("expectamount")/100).setScale(2,BigDecimal.ROUND_HALF_UP)+"元";
			contentVO.tips=rs.getString("tips");
			contentVO.datetime=rs.getString("dateremark");
			contentVO.nsort=rs.getString("nsort");
			contentVO.state=rs.getString("sta");
			switch(contentVO.state){
				case "0":
					contentVO.statename="未完成";
					break;
				case "1":
					contentVO.statename="已完成";
					break;	
			}
			contentVO.t=String.valueOf(rs.getDate("date_upd").getTime());
			if (contentVO.type.equals("5")){
				List<String> imglist =  Lists.newArrayList();
				List<PostcontentImg> PostcontentImgList=Ebean.getServer(Constants.getDB()).find(PostcontentImg.class).where().eq("pcid", contentVO.id).findList();
				for(PostcontentImg m:PostcontentImgList){
					imglist.add(Configuration.root().getString("oss.image.url")+m.getImgurl()+"@100w");
		    	}
				contentVO.imglist=imglist;
			}
		}
		return contentVO;
	}

	/**
	 * 收入
	 * @param postmanUser
	 * @param amount
	 * @param desc
	 * @param out_trade_no
	 * @param catalog
	 * @param sub_catalog
	 * @param src
	 * @param type 1首单返现 2全局返现 3销售返现 4关注返现 5签收返现
	 */
	public static void saveIncome(PostmanUser postmanUser, String amount, String desc, String out_trade_no, String catalog, String sub_catalog,String src,String type) {
		String sql = "CALL `sp_balance_change`(?,?,?,?,?,?,?)";
		CallableSql cs = Ebean.getServer(Constants.getDB()).createCallableSql(sql).setParameter(1, postmanUser.getId())
				.setParameter(2, amount).setParameter(3, out_trade_no).setParameter(4, src).setParameter(5, catalog).setParameter(6, "收入-"+desc+"-外部订单ID为"+out_trade_no).setParameter(7, type);
		Ebean.getServer(Constants.getDB()).execute(cs);
	}

	
	/**
	 * widgetpush
	 * @param uid
	 * @param pcid
	 */
	public static void widgetPush(Integer uid,Integer pcid) {
		String sql = "CALL `sp_postman_widgetpush`(?,?)";
		CallableSql cs = Ebean.getServer(Constants.getDB()).createCallableSql(sql).setParameter(1,uid)
				.setParameter(2, pcid);
		Ebean.getServer(Constants.getDB()).execute(cs);
	}
	
	/**
	 * 根据订单号获取该订单号下的返现记录
	 * @param out_trade_no
	 * @return
	 */
	private static BalanceIncome getBalanceIncomeBy(String out_trade_no) {
		List<BalanceIncome> balanceIncomeList = Ebean.getServer(Constants.getDB()).find(BalanceIncome.class).where().eq("out_trade_no", out_trade_no).findList();
		if(balanceIncomeList.isEmpty()){
			return null;
		}else{
			return balanceIncomeList.get(0);
		}
	}

	/**
	 * 初始化快递员的资金信息
	 * @param id
	 */
	public static void initUser(Integer id) {
		String sql = "CALL `sp_postmanuser_init`(?)";
		CallableSql cs = Ebean.getServer(Constants.getDB()).createCallableSql(sql).setParameter(1, id);
		Ebean.getServer(Constants.getDB()).execute(cs);
	}

	/**
	 * 根据公司ID与工号去获取是否存在快递员
	 * @param companyid
	 * @param staffid
	 * @return
	 */
	public static PostmanUser getPostManUserByComidAndStafid(Integer companyid, String staffid) {
		List<PostmanUser> postmanList = Ebean.getServer(Constants.getDB()).find(PostmanUser.class).where().eq("companyid", companyid).eq("staffid", staffid).ne("sta", "2").findList();
		if(postmanList.isEmpty()){
			return null;
		}else{
			return postmanList.get(0);
		}
	}
	/**
	 * 根据公司ID与工号去获取是否存在快递员
	 * @param postmanid
	 * @return
	 */
	public static Reddot getReddotByUid(Integer postmanid) {
		List<Reddot> reddotList = Ebean.getServer(Constants.getDB()).find(Reddot.class).where().eq("uid", postmanid).findList();
		if(reddotList.isEmpty()){
			return null;
		}else{
			return reddotList.get(0);
		}
	}
	/**
	 * 保存红点信息
	 * @param reddot
	 * @return
	 */
	public static Reddot saveReddot(Reddot reddot){
		 Ebean.getServer(Constants.getDB()).save(reddot);
		 return reddot;
	}

	/**
	 * 根据用户ID获取device
	 * @param postmanid
	 * @return
	 */
	public static Deviceuser getDeviceuserByUid(int postmanid) {
		List<Deviceuser> deviceuserList =  Ebean.getServer(Constants.getDB()).find(Deviceuser.class).where().eq("uid", postmanid).findList();
		if(deviceuserList.isEmpty()){
			return null;
		}else{
			return deviceuserList.get(0);
		}
	}

	/**
	 * 升级返现
	 * @param id
	 */
	public static void updateVersionRefund(Integer id) {
		String sql = "CALL `sp_balance_login`(?)";
		logger.info(sql+"==========");
		CallableSql cs = Ebean.getServer(Constants.getDB()).createCallableSql(sql).setParameter(1, id);
		Ebean.getServer(Constants.getDB()).execute(cs);
		
	}

	/**
	 * 根据公司编码获取该公司信息
	 * @param delivery_company_code
	 * @return
	 */
	public static Postcompany getPostcompanyByCompanycode(
			String delivery_company_code) {
		List<Postcompany> postcompanyList = Ebean.getServer(Constants.getDB()).find(Postcompany.class).where().eq("companycode",delivery_company_code).findList();
		if(postcompanyList.size()>0){
			return postcompanyList.get(0);
		}else{
			return null;
		}
	}

	/**
	 * 保存快递员的位置记录
	 * @param postmanUserLocationLog
	 */
	public static void savePostmanUserLocationLog(
			PostmanUserLocationLog postmanUserLocationLog) {
		Ebean.getServer(Constants.getDB()).save(postmanUserLocationLog);
	}

	public static Integer getSigninIdByUid(Integer uid) {
		Integer result = 0;
		String sql="SELECT c.id AS taskId FROM postcontent_user u,postcontent c WHERE u.uid=:uid AND c.id=u.`pcid` AND c.`typ`=3";//typ为3表示
		List<SqlRow> sqlRowList = Ebean.getServer(Constants.getDB()).createSqlQuery(sql).setParameter("uid",uid).findList();
		for(SqlRow sqlRow:sqlRowList){
			result = sqlRow.getInteger("taskId");
		}
		return result;
	}


}