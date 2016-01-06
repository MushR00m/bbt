package controllers.api;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Deviceuser;
import models.Postcompany;
import models.Versioninfo;
import models.postman.Balance;
import models.postman.BalanceIncome;
import models.postman.BalanceWithdraw;
import models.postman.PostmanUser;
import models.postman.PostmanUserLocationLog;
import models.postman.Reddot;

import org.apache.commons.lang3.StringUtils;

import play.Configuration;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import services.CertificationService;
import services.ServiceFactory;
import services.SmsService;
import services.cache.ICacheService;
import services.citywide.CitywideService;
import services.user.UserService;
import utils.AjaxHelper;
import utils.Constants;
import utils.Constants.PostmanStatus;
import utils.Dates;
import utils.ErrorCode;
import utils.LogFileForStastic;
import utils.Numbers;
import utils.StringUtil;
import utils.WSUtils;
import vo.api.CompanyInfoVO;

import com.avaje.ebean.PagedList;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;

import controllers.BaseController;

/**
 * 用户相关的API
 * @author luobotao
 * @Date 2015年11月10日
 */
public class UserAPIController extends BaseController {
	SimpleDateFormat CHINESE_DATE_TIME_FORMAT_NORMAL = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final java.util.regex.Pattern USERNAME_PATTERN = java.util.regex.Pattern.compile("[\u4E00-\u9FA5]{2,10}(?:·[\u4E00-\u9FA5]{2,10})*");
	ICacheService cache = ServiceFactory.getCacheService();
	private static final Logger.ALogger logger = Logger.of(UserAPIController.class);
    /**
     * 首页接口(GET)homepage_list
     * @return
     */
	@PostmanAuthenticated
    public Result homepage_list() {
    	response().setContentType("application/json;charset=utf-8");
        ObjectNode result = Json.newObject();
        PostmanUser postmanuser = getCurrentPostmanUser(request());
    	result.put("status", "1");
    	int lastindex = Numbers.parseInt(request().getQueryString("lastindex"), 0);
    	result = UserService.getHomePageVOListByUid(result,postmanuser.getId(),0,lastindex,10);
    	String ip = request().remoteAddress();
    	Deviceuser deviceuser = UserService.getDeviceuserByUid(postmanuser.getId());
		if(deviceuser==null){
			deviceuser = new Deviceuser();
		}
    	LogFileForStastic logFileForStastic = new LogFileForStastic();
    	try {
    		logFileForStastic.creatTxtFile("logFileForStastic"+Dates.formatDateNew(new Date()));
        	logFileForStastic.writeTxtFile(Dates.formatDateTime_New(new Date())+","+ip+","+deviceuser.getModel()+","+deviceuser.getOstype()+","+deviceuser.getOsversion()+","+deviceuser.getDeviceid()+","+postmanuser.getId()+",tasklist,App,");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ok(Json.toJson(result));
    }
    /**
     * 设备注册接口(POST方式)dev_login
     * @return
     */
    public Result dev_login() {
    	response().setContentType("application/json;charset=utf-8");
    	ObjectNode result = Json.newObject();
    	result.put("user_agreement",  "http://ht.neolix.cn/www/wap/protocal.html");
    	result.put("about",  "http://ht.neolix.cn/www/wap/protocal.html");
    	result.put("service",  "http://ht.neolix.cn/www/wap/protocal.html");
    	result.put("loading",  "http://omspic.higegou.com/bbt/loading1242x2208.jpg");
		String devid = AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "devid");// 设备ID，iOS为FCUUID
		String marketcode = AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "marketcode");// 渠道号，iOS为[1，999]，但"王叔叔海淘"的渠道号是"wang_1"，Android为[1000，正无穷]
		String appversion = AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "appversion");// App版本号，如"2.0.0"
		String osversion = AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "osversion");// 手机操作系统版本号，如iOS为“8.3”
		String model = AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "model");// 手机型号，如iOS为“iPhone 6 Plus”
		String devicetype = AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "devicetype");// 手机平台类型，0表示iOS，1表示Android
		String wdhjy = AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "wdhjy");// devid校验参数
		String token = AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "token");
		String imei = AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "imei");
		
		if(!StringUtils.isBlank(model) && model.indexOf("4S")>=0){
			result.put("loading",  "http://omspic.higegou.com/bbt/loading1242x2208.jpg@640w");
		}
		if(StringUtils.isBlank(token)){
			result.put("status", ErrorCode.getErrorCode("global.tokenError"));
			result.put("msg", "");
			return ok(Json.toJson(result));
		}

    	int postmanid = 0;
    	PostmanUser postmanuser = new PostmanUser();
    	if(StringUtils.isBlank(cache.get(token))){
    		postmanuser=UserService.getPostManUserByToken(token);
    		if(postmanuser==null){//用户信息不存在
    			result.put("status", ErrorCode.getErrorCode("global.tokenError"));
    			result.put("msg", ErrorCode.getErrorMsg("global.tokenError"));
    			return ok(Json.toJson(result));
    		}else{
    			postmanid=postmanuser.getId();
    			cache.set(token, token);//写入token
        		cache.set(Constants.cache_postmanid+token, String.valueOf(postmanuser.getId()));//根据token找ID
        		cache.set(Constants.cache_tokenBypostmanid+postmanuser.getId(), token);//根据ID找token
    		}
    	}else{
    		postmanid = Numbers.parseInt(cache.get(Constants.cache_postmanid+token), 0);
    		postmanuser=UserService.getPostManUserById(postmanid);
    	}

		if (StringUtils.isBlank(devid) || StringUtils.isBlank(marketcode) || StringUtils.isBlank(appversion)
				|| StringUtils.isBlank(osversion) || StringUtils.isBlank(model) || StringUtils.isBlank(devicetype)
				|| StringUtils.isBlank(wdhjy)) {//参数不正确
			result.put("status", ErrorCode.getErrorCode("global.parameterError"));
			result.put("msg", ErrorCode.getErrorMsg("global.parameterError"));
			return ok(Json.toJson(result));
		}
		if (!StringUtil.checkMd5(devid, wdhjy))
		{
			result.put("status", ErrorCode.getErrorCode("global.operatorError"));
			result.put("msg", ErrorCode.getErrorMsg("global.operatorError"));
			return ok(Json.toJson(result));
		}
		Deviceuser deviceuser = UserService.getDeviceuserByDevid(devid);
		if(deviceuser==null){
			deviceuser = new Deviceuser();
			deviceuser.setDateNew(new Date());
		}
		deviceuser.setDeviceid(devid);
		deviceuser.setOstype(devicetype);
		deviceuser.setOsversion(osversion);
		deviceuser.setModel(model);
		deviceuser.setAppversion(appversion);
		deviceuser.setMarketcode(marketcode);
		deviceuser.setDateUpd(new Date());
		deviceuser.setImei(imei);
		deviceuser = UserService.saveDeviceUser(deviceuser);
		String ip = request().remoteAddress();
    	LogFileForStastic logFileForStastic = new LogFileForStastic();
    	try {
    		logFileForStastic.creatTxtFile("logFileForStastic"+Dates.formatDateNew(new Date()));
        	logFileForStastic.writeTxtFile(Dates.formatDateTime_New(new Date())+","+ip+","+deviceuser.getModel()+","+deviceuser.getOstype()+","+deviceuser.getOsversion()+","+deviceuser.getDeviceid()+","+postmanid+",start,App,");
        } catch (IOException e) {
            e.printStackTrace();
        }

    	ObjectNode userinfo = Json.newObject();
    	if(postmanuser!=null){
    		UserService.unbindDeviceByPostmanid(postmanuser.getId());//根据用户id去解除与设备之间的绑定
    		UserService.bindDevice(postmanuser.getId(),devid);//对用户与设备进行绑定
        	userinfo.put("staffid", postmanuser.getStaffid());
        	userinfo.put("nickname", postmanuser.getNickname());
        	userinfo.put("phone", postmanuser.getPhone());
        	userinfo.put("headicon", Configuration.root().getString("oss.image.url", "http://omspic.higegou.com")+postmanuser.getHeadicon());
        	userinfo.put("companyname", postmanuser.getCompanyname());
        	userinfo.put("stationname", postmanuser.getSubstation());
        	userinfo.put("token", token);
    	}
    	ObjectNode version  = Json.newObject();
    	Versioninfo versioninfo = UserService.getNewVersioninfo(devicetype, marketcode, appversion);
    	Reddot reddot = UserService.getReddotByUid(postmanid);
    	if(reddot==null){
    		reddot = new Reddot();
    		reddot.setDateNew(new Date());
    		reddot.setDateUpd(new Date());
    		reddot.setUpgrade("0");
    		reddot.setUid(postmanid);
    		reddot.setWallet_incoming("0");
    		reddot.setWallet_withdraw("0");
    	}
		if(versioninfo!=null){
			//增加红点
	    	reddot.setUpgrade("1");
	    	UserService.saveReddot(reddot);
			version.put("has_new", "1");
	    	version.put("is_forced", versioninfo.getIsforced());
	    	version.put("remind_once", versioninfo.getRemindTime());
	    	version.put("upgrade_msg", versioninfo.getMessage());
	    	version.put("install_file_url", versioninfo.getUrl());
		}else{
			version.put("has_new", "0");
			// 取消红点
			reddot.setUpgrade("0");
		}
		UserService.saveReddot(reddot);

    	result.put("status", "1");
    	result.set("userinfo", userinfo);
    	result.set("version", version);
    	return ok(Json.toJson(result));
    }
    /**
     * 登录接口(POST) user_login
     * @return
     */
    public Result user_login() {
    	response().setContentType("application/json;charset=utf-8");
    	String phone=AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "phone");
    	String verifycode=AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "verifycode");
    	String devid = AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "devid");// 设备ID，iOS为FCUUID
		String appversion = AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "appversion");// App版本号，如"2.0.0"
		String osversion = AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "osversion");// 手机操作系统版本号，如iOS为“8.3”
		String model = AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "model");// 手机型号，如iOS为“iPhone 6 Plus”
		String devicetype = AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "devicetype");// 手机平台类型，0表示iOS，1表示Android
		String marketcode = AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "marketcode");// 渠道号
		String wdhjy = AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "wdhjy");// devid校验参数
    	ObjectNode result = Json.newObject();
    	if (!StringUtil.checkMd5(devid, wdhjy))
		{
			result.put("status", ErrorCode.getErrorCode("global.operatorError"));
			result.put("msg", ErrorCode.getErrorMsg("global.operatorError"));
			return ok(Json.toJson(result));
		}
    	if (StringUtils.isBlank(devid)) {//参数不正确
			result.put("status", ErrorCode.getErrorCode("global.parameterError"));
			result.put("msg", ErrorCode.getErrorMsg("global.parameterError"));
			return ok(Json.toJson(result));
		}
    	if(StringUtils.isBlank(phone)||!StringUtil.checkPhone(phone)){//手机号码不正确
			result.put("status", ErrorCode.getErrorCode("global.phoneError"));
			result.put("msg", ErrorCode.getErrorMsg("global.phoneError"));
			return ok(Json.toJson(result));
		}
    	if(StringUtil.checkVerifyCode(phone, verifycode)){//验证检验码是否正确
			
    	}else{
    		result.put("status", ErrorCode.getErrorCode("global.verifyCodeError"));
			result.put("msg", ErrorCode.getErrorMsg("global.verifyCodeError"));
			return ok(Json.toJson(result));
    	}
    	
    	Deviceuser deviceuser = UserService.getDeviceuserByDevid(devid);
		if(deviceuser==null){
			deviceuser = new Deviceuser();
			deviceuser.setDeviceid(devid);
			deviceuser.setOstype(devicetype);
			deviceuser.setOsversion(osversion);
			deviceuser.setModel(model);
			deviceuser.setAppversion(appversion);
			deviceuser.setDateNew(new Date());
			deviceuser.setDateUpd(new Date());
			deviceuser = UserService.saveDeviceUser(deviceuser);
		}
    	PostmanUser postmanUser = UserService.getPostManUserByPhone(phone);
    	if(postmanUser!=null){
    		UserService.unbindDeviceByPostmanid(postmanUser.getId());//根据用户id去解除与设备之间的绑定
    		UserService.bindDevice(postmanUser.getId(),devid);//对用户与设备进行绑定
    		ObjectNode userinfo = Json.newObject();
        	userinfo.put("staffid", postmanUser.getStaffid());
        	userinfo.put("nickname", postmanUser.getNickname());
        	userinfo.put("phone", phone);
        	userinfo.put("headicon", Configuration.root().getString("oss.image.url", "http://omspic.higegou.com")+postmanUser.getHeadicon());
        	userinfo.put("companyname", postmanUser.getCompanyname());
        	userinfo.put("stationname", postmanUser.getSubstation());
        	StringBuffer sb = new StringBuffer();
    		sb.append(phone);
    		sb.append(postmanUser.getId());
    		sb.append(postmanUser.getDateNew().getTime());
    		sb.append(System.currentTimeMillis());
    		String token = "P" +StringUtil.getMD5(sb.toString());
    		String tokenOld = cache.get(Constants.cache_tokenBypostmanid+postmanUser.getId());//之前存储的token信息
    		if(!StringUtils.isBlank(tokenOld)){//将旧token设置为失效
    			cache.clear(tokenOld);
    			cache.clear(Constants.cache_postmanid+token);
    		}
    		cache.set(token, token);//写入token
    		cache.set(Constants.cache_postmanid+token, String.valueOf(postmanUser.getId()));//根据token找ID
    		cache.set(Constants.cache_tokenBypostmanid+postmanUser.getId(), token);//根据ID找token
    		cache.clear(Constants.cache_verifyCode+phone);//验证码失效
    		postmanUser.setToken(token);
    		UserService.savePostuserman(postmanUser);
    		ObjectNode version  = Json.newObject();
        	Versioninfo versioninfo = UserService.getNewVersioninfo(devicetype, marketcode, appversion);
        	//红点逻辑
        	Reddot reddot = UserService.getReddotByUid(postmanUser.getId());
        	if(reddot==null){
        		reddot = new Reddot();
        		reddot.setDateNew(new Date());
        		reddot.setDateUpd(new Date());
        		reddot.setUpgrade("0");
        		reddot.setUid(postmanUser.getId());
        		reddot.setWallet_incoming("0");
        		reddot.setWallet_withdraw("0");
        	}
    		if(versioninfo!=null){
    			//增加红点
    	    	reddot.setUpgrade("1");
    	    	UserService.saveReddot(reddot);
    			version.put("has_new", "1");
    	    	version.put("is_forced", versioninfo.getIsforced());
    	    	version.put("remind_once", versioninfo.getRemindTime());
    	    	version.put("upgrade_msg", versioninfo.getMessage());
    	    	version.put("install_file_url", versioninfo.getUrl());
    		}else{
    			version.put("has_new", "0");
    			// 取消红点
    			reddot.setUpgrade("0");
    		}
    		UserService.saveReddot(reddot);//红点逻辑结束
    		
    		UserService.updateVersionRefund(postmanUser.getId());//升级返现
        	userinfo.put("token", token);
        	result.put("status", "1");
        	result.set("userinfo", userinfo);
        	result.set("version", version);
    	}else{
    		result.put("status", ErrorCode.getErrorCode("global.postmanNotExit"));
    	}
    	return ok(Json.toJson(result));
    }
    /**
     * 用户注册接口(POST)user_register
     * @return
     */
    public Result user_register() {
    	response().setContentType("application/json;charset=utf-8");
    	ObjectNode result = Json.newObject();
    	ObjectNode userinfo = Json.newObject();
    	String phone=AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "phone");
    	String verifycode=AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "verifycode");
    	String companyid=AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "companyid");
    	String substation=AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "substation");//用户分站名称
    	String username=AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "username");
    	String cardid=AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "cardid");
    	String staffid=AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "staffid");
    	String devid = AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "devid");// 设备ID，iOS为FCUUID
    	String appversion = AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "appversion");// App版本号，如"2.0.0"
		String osversion = AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "osversion");// 手机操作系统版本号，如iOS为“8.3”
		String model = AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "model");// 手机型号，如iOS为“iPhone 6 Plus”
		String devicetype = AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "devicetype");// 手机平台类型，0表示iOS，1表示Android
		String marketcode = AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "marketcode");// 渠道号
    	
    	
		if (StringUtils.isBlank(phone) || StringUtils.isBlank(verifycode) || StringUtils.isBlank(companyid)
				|| StringUtils.isBlank(substation) || StringUtils.isBlank(username) || StringUtils.isBlank(cardid)
				|| StringUtils.isBlank(staffid)) {//参数不正确
			result.put("status", ErrorCode.getErrorCode("global.parameterError"));
			result.put("msg", ErrorCode.getErrorMsg("global.parameterError"));
			return ok(Json.toJson(result));
		}
    	
    	if(!StringUtil.checkPhone(phone)){//手机号码不正确
			result.put("status", ErrorCode.getErrorCode("global.phoneError"));
			result.put("msg", ErrorCode.getErrorMsg("global.phoneError"));
			return ok(Json.toJson(result));
		}
    	if(!StringUtil.checkVerifyCode(phone, verifycode)){//验证检验码是否正确
			result.put("status", ErrorCode.getErrorCode("global.verifyCodeError"));
			result.put("msg", ErrorCode.getErrorMsg("global.verifyCodeError"));
			return ok(Json.toJson(result));
    	}
    	if (!USERNAME_PATTERN.matcher(username).matches()) {//验证姓名是否正确
    		result.put("status", ErrorCode.getErrorCode("global.nameError"));
    		result.put("msg", ErrorCode.getErrorMsg("global.nameError"));
    		return ok(Json.toJson(result));
    	}
    	
    	PostmanUser postmanUser = UserService.getPostManUserByPhone(phone);
    	if(postmanUser!=null){
    		result.put("status", ErrorCode.getErrorCode("global.registerError"));
			result.put("msg", ErrorCode.getErrorMsg("global.registerError"));
			return ok(Json.toJson(result));
    	}
    	
    	Postcompany postcompany = UserService.getPostcompanyById(Numbers.parseInt(companyid, 0));
    	if(postcompany==null){
    		result.put("status", ErrorCode.getErrorCode("global.postmanCompanyNotExit"));
			result.put("msg", ErrorCode.getErrorMsg("global.postmanCompanyNotExit"));
			return ok(Json.toJson(result));
    	}
    	postmanUser = UserService.getPostManUserByComidAndStafid(Numbers.parseInt(companyid, 0),staffid);//根据公司ID与工号去获取是否存在快递员
    	if(postmanUser!=null){
    		result.put("status", ErrorCode.getErrorCode("global.registerWithStafError"));
			result.put("msg", ErrorCode.getErrorMsg("global.registerWithStafError"));
			return ok(Json.toJson(result));
    	}
    	
    	postmanUser = UserService.getPostManUserByPhone_all(phone);
    	if (postmanUser== null){
    		postmanUser = new PostmanUser();
    	}
    	
    	postmanUser.setPhone(phone);
    	postmanUser.setCompanyid(Numbers.parseInt(companyid, 0));
    	postmanUser.setStaffid(staffid);
    	postmanUser.setNickname(username);
    	postmanUser.setSubstation(substation);
    	postmanUser.setCompanyname(postcompany.getCompanyname());
    	postmanUser.setDateNew(new Date());
    	postmanUser.setDateUpd(new Date());
    	postmanUser.setHeadicon("/bbt/magzine/224/224_07.jpg@200w");
    	if(Numbers.parseInt(companyid, 0)==1){
    		String job_num=postmanUser.getStaffid();
    		String name=postmanUser.getNickname();
    		String id_card=postmanUser.getCardidno();//身份证号
    		
    		Map<String,String> getmap=new HashMap<String,String>();
    		getmap.put("job_num",job_num);
    		String timstr= CHINESE_DATE_TIME_FORMAT_NORMAL.format(new Date());
    		getmap.put("name", name);
    		getmap.put("id_card", id_card);
    		String sign=StringUtil.makeSig(getmap);
    		
    		String url = StringUtil.getBbtOldUrl();
    		ObjectNode re=Json.newObject();
    		re.put("sign", sign);
    		re.put("time_stamp", timstr);
    		re.put("id_card",id_card);
    		re.put("name",name);
    		re.put("job_num", job_num);
    		JsonNode jsonnode = WSUtils.postByJSON(url, re);
    		Logger.info(Json.toJson(jsonnode)+"");
    		if(jsonnode!=null){
    			String err=jsonnode.get("result")==null?"":jsonnode.get("result").textValue();
    			if(err.equals("ok")){
    				postmanUser.setSta(String.valueOf(Constants.PostmanStatus.COMMON.getStatus()));//已审核
    			}else{
    				postmanUser.setSta(String.valueOf(Constants.PostmanStatus.FAILED.getStatus()));//审核失败
    				//发送审核失败短信
    				SmsService.saveSFBack(phone, "1");
    				result.put("status", ErrorCode.getErrorCode("global.postmanStatError"));
    				result.put("msg", ErrorCode.getErrorMsg("global.postmanStatError"));
    				return ok(Json.toJson(result));
    			}
    		}
    	}else{
    		postmanUser.setSta(String.valueOf(Constants.PostmanStatus.NOCHECK.getStatus()));//默认未审核
    	}
    	postmanUser = UserService.savePostuserman(postmanUser);
    	int flag = CertificationService.checkNameWithCard(""+postmanUser.getId(), username, cardid);
    	if (flag == 4) {
    		result.put("status", ErrorCode.getErrorCode("global.timeTooMuchError"));
			result.put("msg", ErrorCode.getErrorMsg("global.timeTooMuchError"));
			return ok(Json.toJson(result));
		}
		if (flag == 0) {
			postmanUser.setCardidno(cardid);
			postmanUser = UserService.savePostuserman(postmanUser);
		}
    	UserService.initUser(postmanUser.getId());//需要初始化数据
    	userinfo.put("staffid", postmanUser.getStaffid());
    	userinfo.put("nickname", postmanUser.getNickname());
    	userinfo.put("phone", postmanUser.getPhone());
    	userinfo.put("headicon", Configuration.root().getString("oss.image.url", "http://omspic.higegou.com")+postmanUser.getHeadicon());
    	userinfo.put("companyname", postcompany.getCompanyname());
    	userinfo.put("stationname", postmanUser.getSubstation());
    	StringBuffer sb = new StringBuffer();
		sb.append(phone);
		sb.append(postmanUser.getId());
		sb.append(postmanUser.getDateNew().getTime());
		sb.append(System.currentTimeMillis());
		String token = "P" +StringUtil.getMD5(sb.toString());//新token
		String tokenOld = cache.get(Constants.cache_tokenBypostmanid+postmanUser.getId());//之前存储的token信息
		if(!StringUtils.isBlank(tokenOld)){//将旧token设置为失效
			cache.clear(tokenOld);
			cache.clear(Constants.cache_postmanid+token);
		}
		cache.set(token, token);//写入token
		cache.set(Constants.cache_postmanid+token, String.valueOf(postmanUser.getId()));//根据token找ID
		cache.set(Constants.cache_tokenBypostmanid+postmanUser.getId(), token);//根据ID找token
		postmanUser.setToken(token);
		UserService.savePostuserman(postmanUser);
		
		UserService.unbindDeviceByPostmanid(postmanUser.getId());//根据用户id去解除与设备之间的绑定
		UserService.bindDevice(postmanUser.getId(),devid);//对用户与设备进行绑定
		
		String ip = request().remoteAddress();
    	
    	Deviceuser deviceuser = UserService.getDeviceuserByUid(postmanUser.getId());
		if(deviceuser==null){
			deviceuser = new Deviceuser();
		}
    	LogFileForStastic logFileForStastic = new LogFileForStastic();
    	try {
    		logFileForStastic.creatTxtFile("logFileForStastic"+Dates.formatDateNew(new Date()));
        	logFileForStastic.writeTxtFile(Dates.formatDateTime_New(new Date())+","+ip+","+deviceuser.getModel()+","+deviceuser.getOstype()+","+deviceuser.getOsversion()+","+deviceuser.getDeviceid()+","+postmanUser.getId()+",reg,App,");
        } catch (IOException e) {
            e.printStackTrace();
        }
    	
    	ObjectNode version  = Json.newObject();
    	Versioninfo versioninfo = UserService.getNewVersioninfo(devicetype, marketcode, appversion);
    	Reddot reddot = UserService.getReddotByUid(postmanUser.getId());
    	if(reddot==null){
    		reddot = new Reddot();
    		reddot.setDateNew(new Date());
    		reddot.setDateUpd(new Date());
    		reddot.setUpgrade("0");
    		reddot.setUid(postmanUser.getId());
    		reddot.setWallet_incoming("0");
    		reddot.setWallet_withdraw("0");
    	}
		if(versioninfo!=null){
			//增加红点
	    	reddot.setUpgrade("1");
	    	UserService.saveReddot(reddot);
			version.put("has_new", "1");
	    	version.put("is_forced", versioninfo.getIsforced());
	    	version.put("remind_once", versioninfo.getRemindTime());
	    	version.put("upgrade_msg", versioninfo.getMessage());
	    	version.put("install_file_url", versioninfo.getUrl());
		}else{
			version.put("has_new", "0");
			// 取消红点
			reddot.setUpgrade("0");
		}
		UserService.saveReddot(reddot);
		
    	userinfo.put("token", token);
    	result.put("status", "1");
    	result.put("msg",  "注册成功");
    	result.put("loading",  "http://d.hiphotos.baidu.com/image/pic/item/267f9e2f07082838e44bf5d5ba99a9014d08f1a9.jpg");
    	result.set("userinfo", userinfo);
    	result.set("version", version);
    	return ok(Json.toJson(result));
    }
    
    /**
     * 用户位置定位接口(POST)app_location
     * @return
     */
    @PostmanAuthenticated
    public Result app_location() {
    	response().setContentType("application/json;charset=utf-8");
    	ObjectNode result = Json.newObject();
    	String lat =AjaxHelper.getHttpParamOfFormUrlEncoded(request(),"latitude");
    	String lon =AjaxHelper.getHttpParamOfFormUrlEncoded(request(),"lontitude");
    	String height =AjaxHelper.getHttpParamOfFormUrlEncoded(request(),"height")==null?"0":AjaxHelper.getHttpParamOfFormUrlEncoded(request(),"height");
    	String error_code =AjaxHelper.getHttpParamOfFormUrlEncoded(request(),"error_code");
    	String addr =AjaxHelper.getHttpParamOfFormUrlEncoded(request(),"addr")==null?"":AjaxHelper.getHttpParamOfFormUrlEncoded(request(),"addr");
    	String addrdes =AjaxHelper.getHttpParamOfFormUrlEncoded(request(),"locationdescribe")==null?"":AjaxHelper.getHttpParamOfFormUrlEncoded(request(),"locationdescribe");
    	
    	PostmanUser postmanuser = getCurrentPostmanUser(request());
    	
    	if (!"61".equals(error_code) &&!"161".equals(error_code)&&!"66".equals(error_code)){
    		result.put("status", "0");
        	result.put("msg",  "定位失败");
        	return ok(Json.toJson(result));
    	}
    	
    	logger.info("location========lat:"+lat+"~~~~~lon:"+lon);
    	if(postmanuser==null){
    		result.put("status", ErrorCode.getErrorCode("global.postmanNotExit"));
			result.put("msg", ErrorCode.getErrorMsg("global.postmanNotExit"));
			return ok(Json.toJson(result));
    	}
    	if(Constants.PostmanStatus.FAILED.getStatus()==Numbers.parseInt(postmanuser.getSta(), 0)){//用户信息审核失败
    		result.put("status", ErrorCode.getErrorCode("global.postmanStatError"));
			result.put("msg", ErrorCode.getErrorMsg("global.postmanStatError"));
			return ok(Json.toJson(result));
    	}
    	postmanuser.setLat(Double.valueOf(lat));
    	postmanuser.setLon(Double.valueOf(lon));
    	postmanuser.setHeight(Double.valueOf(height));
    	postmanuser.setAddr(addr);
    	postmanuser.setAddrdes(addrdes);
    	postmanuser.setDateUpd(new Date());
    	UserService.savePostuserman(postmanuser);
    	PostmanUserLocationLog postmanUserLocationLog = new PostmanUserLocationLog();
    	postmanUserLocationLog.setCompanyid(postmanuser.getCompanyid());
    	postmanUserLocationLog.setCompanyname(postmanuser.getCompanyname());
    	postmanUserLocationLog.setPhone(postmanuser.getPhone());
    	postmanUserLocationLog.setDateNew(new Date());
    	postmanUserLocationLog.setDateUpd(new Date());
    	postmanUserLocationLog.setStaffid(postmanuser.getStaffid());
    	postmanUserLocationLog.setLatitude(postmanuser.getLat());
    	postmanUserLocationLog.setLontitude(postmanuser.getLon());
    	postmanUserLocationLog.setNickname(postmanuser.getNickname());
    	postmanUserLocationLog.setPostmanid(postmanuser.getId());
    	postmanUserLocationLog.setHeight(postmanuser.getHeight());
    	postmanUserLocationLog.setSubstation(postmanuser.getSubstation());
    	UserService.savePostmanUserLocationLog(postmanUserLocationLog);
    	result.put("status", "1");
    	result.put("msg",  "定位成功");
    	return ok(Json.toJson(result));
    }
    
    /**
     * 设置已读接口(POST)update_state
     * @return
     */
    public Result update_state() {
    	response().setContentType("application/json;charset=utf-8");
    	ObjectNode result = Json.newObject();
    	String token =AjaxHelper.getHttpParamOfFormUrlEncoded(request(),"token");
    	String id =AjaxHelper.getHttpParamOfFormUrlEncoded(request(),"lid")==null?"0":AjaxHelper.getHttpParamOfFormUrlEncoded(request(),"lid");
    	String state =AjaxHelper.getHttpParamOfFormUrlEncoded(request(),"state")==null?"1":AjaxHelper.getHttpParamOfFormUrlEncoded(request(),"state");
    	int postmanid = 0;
    	PostmanUser postmanuser = new PostmanUser();
    	if(StringUtils.isBlank(cache.get(token))){
    		postmanuser=UserService.getPostManUserByToken(token);
    		if(postmanuser!=null){
    			postmanid=postmanuser.getId();
    			cache.set(token, token);//写入token
        		cache.set(Constants.cache_postmanid+token, String.valueOf(postmanuser.getId()));//根据token找ID
        		cache.set(Constants.cache_tokenBypostmanid+postmanuser.getId(), token);//根据ID找token
    		}
    	}else{
    		postmanid = Numbers.parseInt(cache.get(Constants.cache_postmanid+token), 0);
    		postmanuser=UserService.getPostManUserById(postmanid);
    	}
    	
    	if(postmanuser==null){//用户信息不存在
    		result.put("status", ErrorCode.getErrorCode("global.postmanNotExit"));
			result.put("msg", ErrorCode.getErrorMsg("global.postmanNotExit"));
			return ok(Json.toJson(result));
    	}
    	
    	if (!id.equals("0")&& state.equals("1")){
    		 UserService.setPostContentUserLooked(postmanid,Numbers.parseInt(id, 0));
    	}
    	UserService.widgetPush(postmanid, Numbers.parseInt(id, 0));
    	result.put("status", "1");
    	result.put("msg",  "成功");
    	return ok(Json.toJson(result));
    }
    

    /**
     * 打点接口(POST)app_buriedpoint
     * @return
     */
    public Result app_buriedpoint() {
    	response().setContentType("application/json;charset=utf-8");
    	ObjectNode result = Json.newObject();
    	String token =AjaxHelper.getHttpParamOfFormUrlEncoded(request(),"token");
    	String url =AjaxHelper.getHttpParamOfFormUrlEncoded(request(),"url");
    	String action_code =AjaxHelper.getHttpParamOfFormUrlEncoded(request(),"action_code");
    	String src =AjaxHelper.getHttpParamOfFormUrlEncoded(request(),"src")==null?"App":AjaxHelper.getHttpParamOfFormUrlEncoded(request(),"src");
    	String ip = request().remoteAddress();
    	int postmanid = 0;
    	PostmanUser postmanuser = new PostmanUser();
    	if(StringUtils.isBlank(cache.get(token))){
    		postmanuser=UserService.getPostManUserByToken(token);
    		if(postmanuser!=null){
    			postmanid=postmanuser.getId();
    			cache.set(token, token);//写入token
        		cache.set(Constants.cache_postmanid+token, String.valueOf(postmanuser.getId()));//根据token找ID
        		cache.set(Constants.cache_tokenBypostmanid+postmanuser.getId(), token);//根据ID找token
    		}
    	}else{
    		postmanid = Numbers.parseInt(cache.get(Constants.cache_postmanid+token), 0);
    		postmanuser=UserService.getPostManUserById(postmanid);
    	}
    	
    	if(postmanuser==null){//用户信息不存在
    		result.put("status", ErrorCode.getErrorCode("global.postmanNotExit"));
			result.put("msg", ErrorCode.getErrorMsg("global.postmanNotExit"));
			return ok(Json.toJson(result));
    	}
    	
    	Deviceuser deviceuser = UserService.getDeviceuserByUid(postmanid);
		if(deviceuser==null){
			deviceuser = new Deviceuser();
		}
		if(StringUtils.isBlank(action_code)){
			action_code = "taskinfo";
			if("pExpressScanCode".equals(url)){
				action_code="deliveryscan";
			}
			if("pExpressSearch".equals(url)){
				action_code="deliverysearch";
			}
			if("pExpressList://".equals(url)){
				action_code="deliverylist";
			}
			if("pExpressDetail".equals(url)){
				action_code="deliveryinfo";
			}
			if("pExpressList".equals(url)){
				action_code="deliverylist";
			}
			if("list".equals(url)){
				action_code="tasklist";
			}
		}

    	LogFileForStastic logFileForStastic = new LogFileForStastic();
    	try {
    		logFileForStastic.creatTxtFile("logFileForStastic"+Dates.formatDateNew(new Date()));
        	logFileForStastic.writeTxtFile(Dates.formatDateTime_New(new Date())+","+ip+","+deviceuser.getModel()+","+deviceuser.getOstype()+","+deviceuser.getOsversion()+","+deviceuser.getDeviceid()+","+postmanid+","+action_code+","+src+","+url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    	result.put("status", "1");
    	result.put("msg",  "打点成功");
    	return ok(Json.toJson(result));
    }
    
    /**
     * 用户登出接口(GET)user_logout
     * @return
     */
    public Result user_logout() {
    	response().setContentType("application/json;charset=utf-8");
    	String token = request().getQueryString("token");
    	int postmanid = 0;
    	PostmanUser postmanuser = new PostmanUser();
    	if(StringUtils.isBlank(cache.get(token))){
    		postmanuser=UserService.getPostManUserByToken(token);
    	}else{
    		postmanid = Numbers.parseInt(cache.get(Constants.cache_postmanid+token), 0);
    		postmanuser=UserService.getPostManUserById(postmanid);
    	}
    	if(postmanuser!=null){
    		postmanuser.setToken("");
    		postmanuser=UserService.savePostuserman(postmanuser);
    	}
    	cache.clear(token);
    	cache.clear(Constants.cache_postmanid+token);
    	ObjectNode result = Json.newObject();
    	result.put("status", "1");
    	return ok(Json.toJson(result));
    }
    /**
     * 获取验证码接口(GET)user_verifycode
     * @return
     */
    public Result user_verifycode() {
    	response().setContentType("application/json;charset=utf-8");
    	String devid = request().getQueryString("devid");// 设备ID，iOS为FCUUID
		String wdhjy =  request().getQueryString("wdhjy");// devid校验参数
		String phone=request().getQueryString("phone");
    	String type=request().getQueryString("type");//1普通短信 2营销短信 3语音短信
    	String flag=request().getQueryString("flag");//0:登录，1：注册
    	
    	ObjectNode result = Json.newObject();
		if (!StringUtil.checkMd5(devid, wdhjy)){
			result.put("status", ErrorCode.getErrorCode("global.operatorError"));
			result.put("msg", ErrorCode.getErrorMsg("global.operatorError"));
			return ok(Json.toJson(result));
		}
    	
    	if(StringUtils.isBlank(phone)||!StringUtil.checkPhone(phone)){//手机号码不正确
			result.put("status", ErrorCode.getErrorCode("global.phoneError"));
			result.put("msg", ErrorCode.getErrorMsg("global.phoneError"));
			return ok(Json.toJson(result));
		}
    	if("1".equals(flag)){//注册
    		PostmanUser postmanUser = UserService.getPostManUserByPhone(phone);
        	if(postmanUser!=null){
        		result.put("status", ErrorCode.getErrorCode("global.registerError"));
    			result.put("msg", ErrorCode.getErrorMsg("global.registerError"));
    			return ok(Json.toJson(result));
        	}
    	}/*else{
    		PostmanUser postmanUser = UserService.getPostManUserByPhone(phone);
        	if(postmanUser==null){
        		result.put("status", ErrorCode.getErrorCode("global.postmanNotExit"));
    			result.put("msg", ErrorCode.getErrorMsg("global.postmanNotExit"));
    			return ok(Json.toJson(result));
        	}
    	}*/
    	String code = cache.get(Constants.cache_verifyCode+phone);
    	if(StringUtils.isBlank(code)){
    		code = StringUtil.genRandomCode(4);//生成四位随机数
    	}
		Logger.info("generate code is :"+code);
    	SmsService.saveVerify(phone, code,type);
    	cache.setWithOutTime(Constants.cache_verifyCode+phone, code,60*30);
    	result.put("status", "1");
    	result.put("msg",  "发送成功");
    	return ok(Json.toJson(result));
    }
    /**
     * 验证验证码接口(GET)user_check_verifycode
     * 注册时下一步专用
     * @return
     */
    public Result user_check_verifycode() {
    	response().setContentType("application/json;charset=utf-8");
		ObjectNode result = Json.newObject();
		String devid = request().getQueryString("devid");// 设备ID，iOS为FCUUID
		String wdhjy =  request().getQueryString("wdhjy");// devid校验参数
    	String phone=request().getQueryString("phone");
    	String verifycode=request().getQueryString("verifycode");
    	if (!StringUtil.checkMd5(devid, wdhjy)){
			result.put("status", ErrorCode.getErrorCode("global.operatorError"));
			result.put("msg", ErrorCode.getErrorMsg("global.operatorError"));
			return ok(Json.toJson(result));
		}
    	if(StringUtils.isBlank(phone)||!StringUtil.checkPhone(phone)){//手机号码不正确
			result.put("status", ErrorCode.getErrorCode("global.phoneError"));
			result.put("msg", ErrorCode.getErrorMsg("global.phoneError"));
			return ok(Json.toJson(result));
		}
    	if(!StringUtil.checkVerifyCode(phone, verifycode)){//验证检验码是否正确
			result.put("status", ErrorCode.getErrorCode("global.verifyCodeError"));
			result.put("msg", ErrorCode.getErrorMsg("global.verifyCodeError"));
			return ok(Json.toJson(result));
    	}
    	
    	PostmanUser postmanUser = UserService.getPostManUserByPhone(phone);
    	if(postmanUser!=null){
    		result.put("status", ErrorCode.getErrorCode("global.registerError"));
			result.put("msg", ErrorCode.getErrorMsg("global.registerError"));
			return ok(Json.toJson(result));
    	}
    	result.put("status", "1");
    	return ok(Json.toJson(result));
    }
    /**
     * 获取所属公司接口(GET)user_company
     * @return
     */
    public Result user_company() {
    	response().setContentType("application/json;charset=utf-8");
    	List<ObjectNode> companyJsonlist = Lists.newArrayList();
    	
    	
    	ObjectNode keyHotJson = Json.newObject();
    	List<Postcompany> companyHotList = UserService.getHotPostcompany();//热门公司
    	List<CompanyInfoVO> companyHotKeyList = Lists.newArrayList();
    	for(Postcompany postcompany:companyHotList){
    		CompanyInfoVO companyInfoVO = new CompanyInfoVO();
			companyInfoVO.id = String.valueOf(postcompany.getId());
			companyInfoVO.name = postcompany.getCompanyname();
			companyHotKeyList.add(companyInfoVO);
    	}
    	if(companyHotKeyList.size()>0){
    		keyHotJson.put("key", "热门");
        	keyHotJson.set("companyinfo", Json.toJson(companyHotKeyList));
        	companyJsonlist.add(keyHotJson);
    	}
    	
    	List<Postcompany> companyList = UserService.getAllPostcompany();//所有公司列表
    	Map<String,String> keys = new HashMap<>();
    	for(Postcompany postcompany:companyList){
    		String key = postcompany.getFirstPinyin();
    		if(keys.containsKey(key)){
    			continue;
    		}
    		keys.put(key, key);
    		List<CompanyInfoVO> companyKeyList = Lists.newArrayList();
    		for(Postcompany postcompanyTemp:companyList){
        		if(postcompanyTemp.getFirstPinyin().equals(key)){
        			CompanyInfoVO companyInfoVO = new CompanyInfoVO();
        			companyInfoVO.id = String.valueOf(postcompanyTemp.getId());
        			companyInfoVO.name = postcompanyTemp.getCompanyname();
        			companyKeyList.add(companyInfoVO);
        		}
        	}
    		ObjectNode keyJson = Json.newObject();
    		keyJson.put("key", key);
    		keyJson.set("companyinfo", Json.toJson(companyKeyList));
    		companyJsonlist.add(keyJson);
    	}
    	ObjectNode result = Json.newObject();
    	result.put("status", "1");
    	result.set("companylist", Json.toJson(companyJsonlist));
    	return ok(Json.toJson(result));
    }
    
    
    /**
     * 更多接口(GET)more_list
     * @return
     */
    public Result more_list() {
    	response().setContentType("application/json;charset=utf-8");
    	ObjectNode result = Json.newObject();
        String token = request().getQueryString("token");
        if(StringUtils.isBlank(token)){
			result.put("status", ErrorCode.getErrorCode("global.tokenError"));
			result.put("msg", ErrorCode.getErrorMsg("global.tokenError"));
			return ok(Json.toJson(result));
		}
    	
    	int postmanid = 0;
    	PostmanUser postmanuser = new PostmanUser();
    	if(StringUtils.isBlank(cache.get(token))){
    		postmanuser=UserService.getPostManUserByToken(token);
    		if(postmanuser==null){//用户信息不存在
    			result.put("status", ErrorCode.getErrorCode("global.tokenError"));
    			result.put("msg", ErrorCode.getErrorMsg("global.tokenError"));
    			return ok(Json.toJson(result));
    		}else{
    			postmanid=postmanuser.getId();
    			cache.set(token, token);//写入token
        		cache.set(Constants.cache_postmanid+token, String.valueOf(postmanuser.getId()));//根据token找ID
        		cache.set(Constants.cache_tokenBypostmanid+postmanuser.getId(), token);//根据ID找token
    		}
    	}else{
    		postmanid = Numbers.parseInt(cache.get(Constants.cache_postmanid+token), 0);
    		postmanuser=UserService.getPostManUserById(postmanid);
    	}
    	
    	if(postmanuser==null){//用户信息不存在
    		result.put("status", ErrorCode.getErrorCode("global.postmanNotExit"));
			result.put("msg", ErrorCode.getErrorMsg("global.postmanNotExit"));
			return ok(Json.toJson(result));
    	}
    	if(Constants.PostmanStatus.FAILED.getStatus()==Numbers.parseInt(postmanuser.getSta(), 0)){//用户信息审核失败
    		result.put("status", ErrorCode.getErrorCode("global.postmanStatError"));
			result.put("msg", ErrorCode.getErrorMsg("global.postmanStatError"));
			return ok(Json.toJson(result));
    	}
        List<ObjectNode> moreInfoList = Lists.newArrayList();
        ObjectNode moreInfo1 = Json.newObject();
        moreInfo1.put("left_title", "个人中心");
        moreInfo1.put("left_icon", "http://higou-api.oss-cn-beijing.aliyuncs.com/images/home_personal_center%403x.png");
        moreInfo1.put("left_count", "3");
        moreInfo1.put("left_linkurl", "uCenter://");
        moreInfo1.put("right_title", "我的店铺");
        moreInfo1.put("right_icon", "http://higou-oms.oss-cn-beijing.aliyuncs.com/upload/bbt/shop-icon.png?1=1");
        moreInfo1.put("right_count", "3");
        if(StringUtils.isBlank(postmanuser.getShopurl())){
        	moreInfo1.put("right_linkurl", Configuration.root().getString("bbt.oms")+"/H5/expressMall");
        }else{
        	moreInfo1.put("right_linkurl", postmanuser.getShopurl());
        }
        ObjectNode moreInfo2 = Json.newObject();
        moreInfo2.put("left_title", "钱包");
        moreInfo2.put("left_icon", "http://higou-api.oss-cn-beijing.aliyuncs.com/images/home_wallet%403x.png");
        moreInfo2.put("left_count", "2");
        moreInfo2.put("left_linkurl", "userWallet://");
        moreInfoList.add(moreInfo1);
        moreInfoList.add(moreInfo2);
        result.put("status", "1");
        result.set("morelist", Json.toJson(moreInfoList));
        return ok(Json.toJson(result));
    }
    /**
     * 用户钱包接口：user_wallet
     * @return
     */
    public Result user_wallet() {
    	response().setContentType("application/json;charset=utf-8");
    	ObjectNode result = Json.newObject();
    	String token = request().getQueryString("token");
    	if(StringUtils.isBlank(token)){
			result.put("status", ErrorCode.getErrorCode("global.tokenError"));
			result.put("msg", ErrorCode.getErrorMsg("global.tokenError"));
			return ok(Json.toJson(result));
		}
    	
    	int postmanid = 0;
    	PostmanUser postmanuser = new PostmanUser();
    	if(StringUtils.isBlank(cache.get(token))){
    		postmanuser=UserService.getPostManUserByToken(token);
    		if(postmanuser==null){//用户信息不存在
    			result.put("status", ErrorCode.getErrorCode("global.tokenError"));
    			result.put("msg", ErrorCode.getErrorMsg("global.tokenError"));
    			return ok(Json.toJson(result));
    		}else{
    			postmanid=postmanuser.getId();
    			cache.set(token, token);//写入token
        		cache.set(Constants.cache_postmanid+token, String.valueOf(postmanuser.getId()));//根据token找ID
        		cache.set(Constants.cache_tokenBypostmanid+postmanuser.getId(), token);//根据ID找token
    		}
    	}else{
    		postmanid = Numbers.parseInt(cache.get(Constants.cache_postmanid+token), 0);
    		postmanuser=UserService.getPostManUserById(postmanid);
    	}
    	
    	if(postmanuser==null){
    		result.put("status", ErrorCode.getErrorCode("global.postmanNotExit"));
			result.put("msg", ErrorCode.getErrorMsg("global.postmanNotExit"));
			return ok(Json.toJson(result));
    	}
    	if(Constants.PostmanStatus.FAILED.getStatus()==Numbers.parseInt(postmanuser.getSta(), 0)){//用户信息审核失败
    		result.put("status", ErrorCode.getErrorCode("global.postmanStatError"));
			result.put("msg", ErrorCode.getErrorMsg("global.postmanStatError"));
			return ok(Json.toJson(result));
    	}
    	if(Numbers.parseInt(postmanuser.getSta(), 0)!=PostmanStatus.COMMON.getStatus()){//状态非正常
			result.put("status", ErrorCode.getErrorCode("global.postmanNotCheck"));
			result.put("msg", ErrorCode.getErrorMsg("global.postmanNotCheck"));
			return ok(Json.toJson(result));
		}
    	Balance balance = UserService.getBalanceByUid(postmanuser.getId());
    	if(balance==null){
    		result.put("status", ErrorCode.getErrorCode("global.postmanBalanceNotExit"));
			result.put("msg", ErrorCode.getErrorMsg("global.postmanBalanceNotExit"));
			return ok(Json.toJson(result));
    	}
    	result.put("status", "1");
    	result.put("capital_details_url",  Configuration.root().getString("bbt.oms")+"/H5/balanceList");
    	result.put("total_count",  Numbers.intToStringWithDiv(balance.getBalance(), 100));
    	result.put("can_withdraw_count",  Numbers.intToStringWithDiv(balance.getCanuse(), 100));
    	result.put("daily_withdraw_limit_count",  Numbers.intToStringWithDiv(Configuration.root().getInt("daily_withdraw_limit_count"), 100));
    	if(StringUtils.isBlank(postmanuser.getCardidno())){
    		result.put("withdraw_state", "1");//1 ：需要身份认证。
    		result.put("withdraw_btn_title", "提现到支付宝");
    	}else{
    		if(balance.getCanuse()>=10000){
//    			result.put("withdraw_state", "2");//可提现
//	    		result.put("withdraw_btn_title", "提现到支付宝");
	    		//暂时随时都可提现
        		if("星期二".equals(Dates.format2Week(new Date()))){
    	    		result.put("withdraw_state", "2");//可提现
    	    		result.put("withdraw_btn_title", "提现到支付宝");
            	}else{
            		result.put("withdraw_state", "0");
            		result.put("withdraw_btn_title", "每周二可提现");
            	}
        	}else{
        		result.put("withdraw_state", "0");
        		result.put("withdraw_btn_title", "100元以上支持提现");
        	}
    	}
    	Reddot reddot = UserService.getReddotByUid(postmanid);
    	if(reddot==null){
    		reddot = new Reddot();
    		reddot.setDateNew(new Date());
    		reddot.setDateUpd(new Date());
    		reddot.setUpgrade("0");
    		reddot.setMyfav("0");
    		reddot.setUid(postmanid);
    	}
    	reddot.setWallet_incoming("0");
		reddot.setWallet_withdraw("0");
		UserService.saveReddot(reddot);
    	return ok(Json.toJson(result));
    }
    /**
     * 用户认证接口(POST)user_authentication
     * @return
     */
	public Result user_authentication() {
		response().setContentType("application/json;charset=utf-8");
		ObjectNode result = Json.newObject();
		String username = AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "username");
		String cardid = AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "cardid");
		String account_no = AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "account_no");
		String token = AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "token");
		
		if ( StringUtils.isBlank(username) || StringUtils.isBlank(cardid)) {//参数不正确
			result.put("status", ErrorCode.getErrorCode("global.parameterError"));
			result.put("msg", ErrorCode.getErrorMsg("global.parameterError"));
			return ok(Json.toJson(result));
		}
		if(StringUtils.isBlank(token)){
			result.put("status", ErrorCode.getErrorCode("global.tokenError"));
			result.put("msg", ErrorCode.getErrorMsg("global.tokenError"));
			return ok(Json.toJson(result));
		}
    	
    	int postmanid = 0;
    	PostmanUser postmanuser = new PostmanUser();
    	if(StringUtils.isBlank(cache.get(token))){
    		postmanuser=UserService.getPostManUserByToken(token);
    		if(postmanuser==null){//用户信息不存在
    			result.put("status", ErrorCode.getErrorCode("global.tokenError"));
    			result.put("msg", ErrorCode.getErrorMsg("global.tokenError"));
    			return ok(Json.toJson(result));
    		}else{
    			postmanid=postmanuser.getId();
    			cache.set(token, token);//写入token
        		cache.set(Constants.cache_postmanid+token, String.valueOf(postmanuser.getId()));//根据token找ID
        		cache.set(Constants.cache_tokenBypostmanid+postmanuser.getId(), token);//根据ID找token
    		}
    	}else{
    		postmanid = Numbers.parseInt(cache.get(Constants.cache_postmanid+token), 0);
    		postmanuser=UserService.getPostManUserById(postmanid);
    	}
    	if(postmanuser==null){//判断用户是否存在
    		result.put("status", ErrorCode.getErrorCode("global.postmanNotExit"));
			result.put("msg", ErrorCode.getErrorMsg("global.postmanNotExit"));
			return ok(Json.toJson(result));
    	}
    	if(Constants.PostmanStatus.FAILED.getStatus()==Numbers.parseInt(postmanuser.getSta(), 0)){//用户信息审核失败
    		result.put("status", ErrorCode.getErrorCode("global.postmanStatError"));
			result.put("msg", ErrorCode.getErrorMsg("global.postmanStatError"));
			return ok(Json.toJson(result));
    	}
    	postmanuser.setAlipayAccount(account_no);
    	postmanuser.setNickname(username);
    	postmanuser.setCardidno(cardid);
    	
    	String status="";
    	String toast="";
		int flag = CertificationService.checkNameWithCard(String.valueOf(postmanid), username, cardid);
		if (flag == 0) {
			result.put("status", "1");
		} else {
			result.put("status", ErrorCode.getErrorCode("global.postmanBalanceNotExit"));
			switch (flag) {
			case -1:
				toast = "请输入真实姓名，身份证信息。";
				break;
			case 1:
				toast = "请登录。";
				break;
			case 2:
				toast = "请输入真实姓名。";
				break;
			case 3:
				toast = "请输入真实身份证号。";
				break;
			case 4:
				toast = "验证次数太多，请明天再试。";
				break;
			default:
				toast = "需要您完善真实的身份信息。";
			}
			result.put("msg", toast);
			return ok(Json.toJson(result)); 
		}
    	
    	postmanuser = UserService.savePostuserman(postmanuser);
		
		result.put("username", postmanuser.getNickname());
		result.put("idcard", postmanuser.getCardidno());
		result.put("account_no", postmanuser.getAlipayAccount());
		return ok(Json.toJson(result));
	}
    /**
     * 用户提现接口(POST)user_withdraw
     * @return
     */
    public Result user_withdraw() {
    	response().setContentType("application/json;charset=utf-8");
    	ObjectNode result = Json.newObject();
    	String token = AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "token");
    	String account_no =AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "account_no");
    	if(StringUtils.isBlank(token)){
			result.put("status", ErrorCode.getErrorCode("global.tokenError"));
			result.put("msg", ErrorCode.getErrorMsg("global.tokenError"));
			return ok(Json.toJson(result));
		}
    	
    	int postmanid = 0;
    	PostmanUser postmanuser = new PostmanUser();
    	if(StringUtils.isBlank(cache.get(token))){
    		postmanuser=UserService.getPostManUserByToken(token);
    		if(postmanuser==null){//用户信息不存在
    			result.put("status", ErrorCode.getErrorCode("global.tokenError"));
    			result.put("msg", ErrorCode.getErrorMsg("global.tokenError"));
    			return ok(Json.toJson(result));
    		}else{
    			postmanid=postmanuser.getId();
    			cache.set(token, token);//写入token
        		cache.set(Constants.cache_postmanid+token, String.valueOf(postmanuser.getId()));//根据token找ID
        		cache.set(Constants.cache_tokenBypostmanid+postmanuser.getId(), token);//根据ID找token
    		}
    	}else{
    		postmanid = Numbers.parseInt(cache.get(Constants.cache_postmanid+token), 0);
    		postmanuser=UserService.getPostManUserById(postmanid);
    	}
    	if(postmanuser==null){//用户信息不存在
    		result.put("status", ErrorCode.getErrorCode("global.postmanNotExit"));
			result.put("msg", ErrorCode.getErrorMsg("global.postmanNotExit"));
			return ok(Json.toJson(result));
    	}else{
    		if(Constants.PostmanStatus.FAILED.getStatus()==Numbers.parseInt(postmanuser.getSta(), 0)){//用户信息审核失败
        		result.put("status", ErrorCode.getErrorCode("global.postmanStatError"));
    			result.put("msg", ErrorCode.getErrorMsg("global.postmanStatError"));
    			return ok(Json.toJson(result));
        	}
    		postmanuser.setAlipayAccount(account_no);
    		postmanuser=UserService.savePostuserman(postmanuser);
    	}
    	String moneystr = UserService.withDraw(postmanuser.getId());
    	if("-1".equals(moneystr)){//用户资金信息不存在
    		result.put("status", ErrorCode.getErrorCode("global.postmanBalanceNotExit"));
			result.put("msg", ErrorCode.getErrorMsg("global.postmanBalanceNotExit"));
			return ok(Json.toJson(result));
    	}
    	result.put("withdraw_type",  "支付宝（*"+StringUtil.getSecretAliacount(postmanuser.getAlipayAccount())+"）");
    	result.put("withdraw_money",  moneystr);
    	
    	if("0".equals(moneystr)){
    		result.put("status", "0");
    		result.put("msg",  "提现失败");
    	}else{
    		result.put("status", "1");
    		result.put("msg",  "提现成功");
    	}
    	
    	return ok(Json.toJson(result));
    }
    /**
     * 提现明细接口
     * @return
     */
    public Result user_withdrawPage() {
    	response().setContentType("application/json;charset=utf-8");
    	ObjectNode result = Json.newObject();
    	String token = request().getQueryString("token");
    	String page = request().getQueryString("page");
    	String pagesize = request().getQueryString("pagesize");
    	if(StringUtils.isBlank(token)){
			result.put("status", ErrorCode.getErrorCode("global.tokenError"));
			result.put("msg", ErrorCode.getErrorMsg("global.tokenError"));
			return ok(Json.toJson(result));
		}
    	
    	int postmanid = 0;
    	PostmanUser postmanuser = new PostmanUser();
    	if(StringUtils.isBlank(cache.get(token))){
    		postmanuser=UserService.getPostManUserByToken(token);
    		if(postmanuser==null){//用户信息不存在
    			result.put("status", ErrorCode.getErrorCode("global.tokenError"));
    			result.put("msg", ErrorCode.getErrorMsg("global.tokenError"));
    			return ok(Json.toJson(result));
    		}else{
    			postmanid=postmanuser.getId();
    			cache.set(token, token);//写入token
        		cache.set(Constants.cache_postmanid+token, String.valueOf(postmanuser.getId()));//根据token找ID
        		cache.set(Constants.cache_tokenBypostmanid+postmanuser.getId(), token);//根据ID找token
    		}
    	}else{
    		postmanid = Numbers.parseInt(cache.get(Constants.cache_postmanid+token), 0);
    		postmanuser=UserService.getPostManUserById(postmanid);
    	}
    	if(postmanuser==null){//用户信息不存在
    		result.put("status", ErrorCode.getErrorCode("global.postmanNotExit"));
    		result.put("msg", ErrorCode.getErrorMsg("global.postmanNotExit"));
    		return ok(Json.toJson(result));
    	}
    	if(Constants.PostmanStatus.FAILED.getStatus()==Numbers.parseInt(postmanuser.getSta(), 0)){//用户信息审核失败
    		result.put("status", ErrorCode.getErrorCode("global.postmanStatError"));
			result.put("msg", ErrorCode.getErrorMsg("global.postmanStatError"));
			return ok(Json.toJson(result));
    	}
    	PagedList<BalanceWithdraw> balanceWithdrawPage = UserService.getBalanceWithdrawPageByUid(postmanuser.getId(),Numbers.parseInt(page, 0),Numbers.parseInt(pagesize, 10));
    	
    	result.put("status", "1");
    	result.set("balanceWithdrawList", Json.toJson(balanceWithdrawPage.getList()));
    	result.set("pageTotal", Json.toJson(balanceWithdrawPage.getTotalPageCount()));
    	return ok(Json.toJson(result));
    }
    /**
     * 收入明细接口
     * @return
     */
    public Result user_incomePage() {
    	response().setContentType("application/json;charset=utf-8");
    	ObjectNode result = Json.newObject();
    	String token = request().getQueryString("token");
    	String page = request().getQueryString("page");
    	String pagesize = request().getQueryString("pagesize");
    	if(StringUtils.isBlank(token)){
			result.put("status", ErrorCode.getErrorCode("global.tokenError"));
			result.put("msg", ErrorCode.getErrorMsg("global.tokenError"));
			return ok(Json.toJson(result));
		}
    	
    	int postmanid = 0;
    	PostmanUser postmanuser = new PostmanUser();
    	if(StringUtils.isBlank(cache.get(token))){
    		postmanuser=UserService.getPostManUserByToken(token);
    		if(postmanuser==null){//用户信息不存在
    			result.put("status", ErrorCode.getErrorCode("global.tokenError"));
    			result.put("msg", ErrorCode.getErrorMsg("global.tokenError"));
    			return ok(Json.toJson(result));
    		}else{
    			postmanid=postmanuser.getId();
    			cache.set(token, token);//写入token
        		cache.set(Constants.cache_postmanid+token, String.valueOf(postmanuser.getId()));//根据token找ID
        		cache.set(Constants.cache_tokenBypostmanid+postmanuser.getId(), token);//根据ID找token
    		}
    	}else{
    		postmanid = Numbers.parseInt(cache.get(Constants.cache_postmanid+token), 0);
    		postmanuser=UserService.getPostManUserById(postmanid);
    	}
    	if(postmanuser==null){//用户信息不存在
    		result.put("status", ErrorCode.getErrorCode("global.postmanNotExit"));
    		result.put("msg", ErrorCode.getErrorMsg("global.postmanNotExit"));
    		return ok(Json.toJson(result));
    	}
    	if(Constants.PostmanStatus.FAILED.getStatus()==Numbers.parseInt(postmanuser.getSta(), 0)){//用户信息审核失败
    		result.put("status", ErrorCode.getErrorCode("global.postmanStatError"));
			result.put("msg", ErrorCode.getErrorMsg("global.postmanStatError"));
			return ok(Json.toJson(result));
    	}
    	Reddot reddot = UserService.getReddotByUid(postmanid);
    	if(reddot==null){
    		reddot = new Reddot();
    		reddot.setDateNew(new Date());
    		reddot.setDateUpd(new Date());
    		reddot.setUpgrade("0");
    		reddot.setMyfav("0");
    		reddot.setUid(postmanid);
    	}
    	reddot.setWallet_incoming("0");
		reddot.setWallet_withdraw("0");
		UserService.saveReddot(reddot);
    	
    	PagedList<BalanceIncome> balanceIncomePage = UserService.getBalanceIncomePageByUid(postmanuser.getId(),Numbers.parseInt(page, 0),Numbers.parseInt(pagesize, 10));
    	
    	result.put("status", "1");
    	result.set("balanceIncomeList", Json.toJson(balanceIncomePage.getList()));
    	result.set("pageTotal", Json.toJson(balanceIncomePage.getTotalPageCount()));
    	return ok(Json.toJson(result));
    }
    /**
     * 提现信息接口(GET)withdrawal_info
     * @return
     */
    public Result withdrawal_info() {
    	response().setContentType("application/json;charset=utf-8");
    	ObjectNode result = Json.newObject();
    	String token = request().getQueryString("token");
    	if(StringUtils.isBlank(token)){
			result.put("status", ErrorCode.getErrorCode("global.tokenError"));
			result.put("msg", ErrorCode.getErrorMsg("global.tokenError"));
			return ok(Json.toJson(result));
		}
    	
    	int postmanid = 0;
    	PostmanUser postmanuser = new PostmanUser();
    	if(StringUtils.isBlank(cache.get(token))){
    		postmanuser=UserService.getPostManUserByToken(token);
    		if(postmanuser==null){//用户信息不存在
    			result.put("status", ErrorCode.getErrorCode("global.tokenError"));
    			result.put("msg", ErrorCode.getErrorMsg("global.tokenError"));
    			return ok(Json.toJson(result));
    		}else{
    			postmanid=postmanuser.getId();
    			cache.set(token, token);//写入token
        		cache.set(Constants.cache_postmanid+token, String.valueOf(postmanuser.getId()));//根据token找ID
        		cache.set(Constants.cache_tokenBypostmanid+postmanuser.getId(), token);//根据ID找token
    		}
    	}else{
    		postmanid = Numbers.parseInt(cache.get(Constants.cache_postmanid+token), 0);
    		postmanuser=UserService.getPostManUserById(postmanid);
    	}
    
    	if(postmanuser==null){//用户信息不存在
    		result.put("status", ErrorCode.getErrorCode("global.postmanNotExit"));
    		result.put("msg", ErrorCode.getErrorMsg("global.postmanNotExit"));
    		return ok(Json.toJson(result));
    	}
    	if(Constants.PostmanStatus.FAILED.getStatus()==Numbers.parseInt(postmanuser.getSta(), 0)){//用户信息审核失败
    		result.put("status", ErrorCode.getErrorCode("global.postmanStatError"));
			result.put("msg", ErrorCode.getErrorMsg("global.postmanStatError"));
			return ok(Json.toJson(result));
    	}
    	Balance balance = UserService.getBalanceByUid(postmanuser.getId());
    	
    	String moneystr=Numbers.intToStringWithDiv(balance.getCanuse(), 100);
    	result.put("status", "1");
    	result.put("name", "姓名："+postmanuser.getNickname());
    	result.put("withdrawal_type", "支付宝账号：");
    	if(postmanuser.getAlipayAccount()!=null){
    		result.put("account", ""+postmanuser.getAlipayAccount());
    	}else{
    		result.put("account", "");
    	}
    	result.put("highlight_money", moneystr+"元");
    	result.put("money_description", "本次可提现金额为"+moneystr+"元");
    	result.put("time_instructions", "本周收入下周提现，可提现金额为周日24点前到账收入");
    	result.put("toast", "为确保您的资金安全，该支付宝姓名需与实名认证一致！\n如个人信息与实际不符，请联系400616090进行修改");
    	return ok(Json.toJson(result));
    }
    
    /**
     * 升级检查接口(GET)app_version
     * @return
     */
    public Result app_version() {
    	response().setContentType("application/json;charset=utf-8");
    	ObjectNode result = Json.newObject();
    	ObjectNode version = Json.newObject();
    	result.put("status", "1");
    	String devicetype = request().getQueryString("devicetype");// 手机平台类型，0表示iOS，1表示Android
    	String marketcode = request().getQueryString("marketcode");//渠道号
		String appversion =  request().getQueryString("appversion");//当前app版本号
		if(StringUtils.isBlank(devicetype)||StringUtils.isBlank(marketcode)||StringUtils.isBlank(appversion)){//参数不正确
			result.put("status", ErrorCode.getErrorCode("global.parameterError"));
			result.put("msg", ErrorCode.getErrorMsg("global.parameterError"));
			return ok(Json.toJson(result));
		}
		Versioninfo versioninfo = UserService.getNewVersioninfo(devicetype, marketcode, appversion);
		if(versioninfo!=null){
			version.put("has_new", "1");
	    	version.put("is_forced", versioninfo.getIsforced());
	    	version.put("remind_once", versioninfo.getRemindTime());
	    	version.put("upgrade_title", "新版本发布");
	    	version.put("upgrade_msg", versioninfo.getMessage());
	    	version.put("install_file_url", versioninfo.getUrl());
		}else{
			version.put("has_new", "0");
		}
    	result.set("version", version);
    	return ok(Json.toJson(result));
    }
    /**
     * 个人中心列表接口(GET)ucenter_list
     * @return
     */
	@PostmanAuthenticated
    public Result ucenter_list() {
    	response().setContentType("application/json;charset=utf-8");
    	ObjectNode result = Json.newObject();
    	ObjectNode userinfo = Json.newObject();
    	String token = request().getQueryString("token");
		PostmanUser postmanuser = getCurrentPostmanUser(request());
		userinfo.put("staffid", postmanuser.staffid);
		userinfo.put("nickname", postmanuser.nickname);
		userinfo.put("phone", postmanuser.phone);
		userinfo.put("headicon", Configuration.root().getString("oss.image.url", "http://omspic.higegou.com")+postmanuser.headicon);
		userinfo.put("companyname", postmanuser.companyname);
		userinfo.put("stationname", postmanuser.substation);
		userinfo.put("token", token);
		result.set("userinfo", Json.toJson(userinfo));

		Reddot reddot = UserService.getReddotByUid(postmanuser.getId());
    	List<ObjectNode> itemsList = Lists.newArrayList();
    	result.put("status", "1");
    	result.put("msg",  "成功");

		ObjectNode item0 = Json.newObject();
		item0.put("name", "签到");
		item0.put("icon", "http://apitestpic.higegou.com/upload/endorsement/headicon55be389e-8438-4cb0-b6e0-afdf10ba1e49.png");
		Integer taskid = UserService.getSigninIdByUid(postmanuser.getId());
		item0.put("linkurl", "pH5SignIn://bbtoms.ibbt.com/H5/fanpai?taskid="+taskid);
		if(reddot!=null){
			//""count"：数量(v1.2.0，【-3：显示“新版本”】，【-2:显示小红点】，【-1：不显示】，【大于等于0：显示数值】）
			if("1".equals(reddot.getSignin())){//签到红点；0：不显示红点；1：显示红点；
				item0.put("count", "-2");
			}else{
				item0.put("count", "-1");
			}
		}
		itemsList.add(item0);

		ObjectNode item1 = Json.newObject();
    	item1.put("name", "我的收藏");
    	item1.put("icon", "http://test-higou-api.oss-cn-beijing.aliyuncs.com/upload/endorsement/headicon55be389e-8438-4cb0-b6e0-afdf10ba1e49.png");
    	item1.put("linkurl", "magazineList://");
		if(reddot!=null){
			//""count"：数量(v1.2.0，【-3：显示“新版本”】，【-2:显示小红点】，【-1：不显示】，【大于等于0：显示数值】）
			if("1".equals(reddot.getMyfav())){//我的收藏红点；0：不显示红点；1：显示红点；
				item1.put("count", "-2");
			}else{
				item1.put("count", "-1");
			}
		}
    	itemsList.add(item1);

		List<ObjectNode> messageList = Lists.newArrayList();
		messageList = CitywideService.getMessageListByUid(messageList,postmanuser.getId(),4,0,Integer.MAX_VALUE);//4为系统消息
		ObjectNode tab1 = Json.newObject();
		tab1.put("name", "消息中心");
		tab1.put("icon", "http://test-higou-api.oss-cn-beijing.aliyuncs.com/upload/endorsement/headicon55be389e-8438-4cb0-b6e0-afdf10ba1e49.png");
		tab1.put("linkurl", "pMessageList://");
		itemsList.add(tab1);

		Postcompany postcompany = UserService.getPostcompanyById(postmanuser.getCompanyid());
		if(postcompany!=null){
			if("1".equals(postcompany.getDeliveryflag())){//只有可派单的公司才展示
				ObjectNode item2 = Json.newObject();
		    	item2.put("name", "当日派件结算");
		    	item2.put("linkurl", "pSettlement://");
				item2.put("icon", "http://test-higou-api.oss-cn-beijing.aliyuncs.com/upload/endorsement/headicon55be389e-8438-4cb0-b6e0-afdf10ba1e49.png");
				item2.put("count", "-1");
				itemsList.add(item2);
			}
		}
    	result.set("items", Json.toJson(itemsList));
    	return ok(Json.toJson(result));
    }
    /**
     * 红点接口(GET)reddot_check
     * @return
     */
	@PostmanAuthenticated
    public Result reddot_check() {
    	response().setContentType("application/json;charset=utf-8");
    	ObjectNode result = Json.newObject();
		PostmanUser postmanuser = getCurrentPostmanUser(request());
    	Reddot reddot = UserService.getReddotByUid(postmanuser.getId());
    	if(reddot==null){
    		reddot = new Reddot();
    		reddot.setDateNew(new Date());
    		reddot.setDateUpd(new Date());
    		reddot.setMyfav("0");
    		reddot.setUpgrade("0");
    		reddot.setUid(postmanuser.getId());
    		reddot.setWallet_incoming("0");
    		reddot.setWallet_withdraw("0");
    		UserService.saveReddot(reddot);
    	}
    	ObjectNode data = Json.newObject();
    	data.put("myfav", reddot.getMyfav());
    	data.put("upgrade", reddot.getUpgrade());
    	data.put("wallet_withdraw", reddot.getWallet_withdraw());
    	data.put("wallet_incoming", reddot.getWallet_incoming());
    	data.put("message",UserService.getMessageCnt(postmanuser.getId())+"");
    	data.put("signin", UserService.getpH5SignIn(postmanuser.getId())+"");
    	
    	result.put("status", "1");
    	result.put("msg", "");
    	result.set("data", Json.toJson(data));
    	return ok(Json.toJson(result));
    }
}
