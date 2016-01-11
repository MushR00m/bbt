package controllers.api;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import models.Deviceuser;
import models.Postcompany;
import models.postman.PostmanUser;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.CertificationService;
import services.ServiceFactory;
import services.SmsService;
import services.cache.ICacheService;
import services.user.UserService;
import utils.Constants;
import utils.Dates;
import utils.ErrorCode;
import utils.LogFileForStastic;
import utils.Numbers;
import utils.StringUtil;
import utils.WSUtils;
import vo.api.CompanyInfoVO;
import vo.api.PostcontentVO;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;

/**
 * widget相关的API
 * @author luobotao
 * @Date 2015年11月10日
 */
public class AuthAppAPIController extends Controller {
	SimpleDateFormat CHINESE_DATE_TIME_FORMAT_NORMAL = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	ICacheService cache = ServiceFactory.getCacheService();
  
    public Result login() {
    	response().setContentType("application/json;charset=utf-8");
    	JsonNode req = request().body().asJson();
    	JsonNode params=Json.newObject();
    	ObjectNode all =Json.newObject();
		all.put("jsonrpc", "2.0");
		all.put("id",  "1448096464279");
    	ObjectNode result = Json.newObject();
    	if (req.get("params")!= null){
    		params = req.get("params");
    		String phone=params.get("phone").asText();
    		String verify= params.get("verify_code").asText();
    		String device_name= params.get("device_name").asText();
    		String imei= params.get("imei").asText();
    		String product= params.get("product").asText();
    		Logger.info("phone:================="+phone);
    		Logger.info("verify:================="+verify);
    		if(StringUtil.checkVerifyCode(phone, verify)){//验证检验码是否正确
    			cache.clear(Constants.cache_verifyCode+phone);
        	}else{
        		ObjectNode error = Json.newObject();
    	    	error.put("message", "验证码错误");
    	    	error.put("code",  -32702);
    	    	all.set("error",Json.toJson(error));
    	    	//result.set("error",Json.toJson(error));
    	    	return ok(Json.toJson(all));
        	}
    		PostmanUser postmanUser = UserService.getPostManUserByPhone(phone);
        	if(postmanUser!=null){
        		UserService.unbindDeviceByPostmanid(postmanUser.getId());//根据用户id去解除与设备之间的绑定
        		//UserService.bindDevice(postmanUser.getId(),devid);//对用户与设备进行绑定
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
        		postmanUser.setToken(token);
        		UserService.savePostuserman(postmanUser);
        		

        		String ip = request().remoteAddress();
            	
            	Deviceuser deviceuser = UserService.getDeviceuserByUid(postmanUser.getId());
        		if(deviceuser==null){
        			deviceuser = new Deviceuser();
        		}
            	LogFileForStastic logFileForStastic = new LogFileForStastic();
            	try {
            		logFileForStastic.creatTxtFile("logFileForStastic"+Dates.formatDateNew(new Date()));
                	logFileForStastic.writeTxtFile(Dates.formatDateTime_New(new Date())+","+ip+","+"X7"+","+"android17"+","+"1.0"+","+imei+","+postmanUser.getId()+",start,accountapp,");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            	
	    		ObjectNode obj= Json.newObject();
	    		obj.put("is_register", 1);
	    		obj.put("token",token);
	    		obj.put("job_num", postmanUser.staffid);
	    		//obj.put("status", "1");
	    		switch(postmanUser.sta){
	    		case "1":
	    			obj.put("status","1");
	    			break;
	    		case "0":
	    			obj.put("status","2");
	    			break;
	    		case "2":
	    			obj.put("status","3");
	    			obj.put("is_register", 0);
	    			break;
	    		}
	    		obj.put("is_audit", "1");
	    		obj.put("name", postmanUser.phone);
	    		obj.put("company_id", postmanUser.companyid);
	    		obj.put("company_name", postmanUser.companyname);
	    		obj.put("postman_working_status", "2");
	    		obj.put("company_icon_url", "");
	    		obj.put("header_url", "");
	    		obj.put("qr_code", "");
	    		obj.put("phone", postmanUser.phone);
	    		obj.put("id_card_no", postmanUser.cardidno);
	    		obj.put("liked", "0");
	    		obj.put("disliked", "0");
	    		obj.put("substation", postmanUser.substation);
	            //result.set("result", Json.toJson(obj));
	            all.set("result", obj);
	            UserService.updateVersionRefund(postmanUser.getId());//升级返现
        	}else{
        		ObjectNode obj= Json.newObject();
	    		obj.put("is_register", 0);
	    		obj.put("token","");
	    		obj.put("job_num", "");
	    		obj.put("status", "0");
	    		obj.put("is_audit", "1");
	    		obj.put("name",  "");
	    		obj.put("company_id", "");
	    		obj.put("company_name",  "");
	    		obj.put("postman_working_status", "2");
	    		obj.put("company_icon_url", "");
	    		obj.put("header_url", "");
	    		obj.put("qr_code", "");
	    		obj.put("phone", "");
	    		obj.put("id_card_no", "");
	    		obj.put("liked", "0");
	    		obj.put("disliked", "0");
	    		obj.put("substation", "");
	    		//result.set("result", Json.toJson(obj));
	    		all.set("result",  Json.toJson(obj));
        	}
    	}else{
	      	ObjectNode error = Json.newObject();
	    	error.put("message", "登录失败");
	    	error.put("code", -32702);
	    	all.set("error",Json.toJson(error));
    	}
    	return ok(Json.toJson(all));
    }
    
    public Result send_verify_code() {
    	response().setContentType("application/json;charset=utf-8");
    	JsonNode req = request().body().asJson();
    	JsonNode params=Json.newObject();
    	ObjectNode all =Json.newObject();
		all.put("jsonrpc", "2.0");
		all.put("id",  "1448096464279");
    	ObjectNode result = Json.newObject();
    	if (req.get("params")!= null){
    		params = req.get("params");
    		String phone=params.get("phone").asText();
    		if(StringUtils.isBlank(phone)||!StringUtil.checkPhone(phone)){//手机号码不正确
    			ObjectNode error = Json.newObject();
    	    	error.put("message", "手机号码非法");
    	    	error.put("code",  0);
    	    	all.set("error", error);
    			return ok(Json.toJson(all));
    		}
    		String code = StringUtil.genRandomCode(4);//生成四位随机数
    		Logger.info("generate code is :"+code);
        	SmsService.saveVerify(phone, code,"1");
        	cache.setWithOutTime(Constants.cache_verifyCode+phone, code,60*30);
    		//result.put("result", "OK");	
    		all.put("result", "OK");
    	}else{
	      	ObjectNode error = Json.newObject();
	    	error.put("message", "手机号码非法");
	    	error.put("code",  0);
	    	all.set("error", error);
    	}
    	return ok(Json.toJson(all));
    }
    
    public Result get_company_list() {
    	response().setContentType("application/json;charset=utf-8");
    	String token = request().getQueryString("token");
    	JsonNode req = request().body().asJson();
    	JsonNode params=Json.newObject();
    	ObjectNode all =Json.newObject();
		all.put("jsonrpc", "2.0");
		all.put("id",  "1448096464279");
    	ObjectNode company_list = Json.newObject();
    	if (req.get("params")!= null){
    		List<ObjectNode> objlist= Lists.newArrayList();
    		List<Postcompany> companyList = UserService.getAllPostcompany();//所有公司列表
        	for(Postcompany postcompany:companyList){
    			ObjectNode b=Json.newObject();
    			b.put("id",String.valueOf(postcompany.getId()));
        		b.put("url", "");
        		b.put("name", postcompany.getCompanyname());
        		b.put("need_id_card", "1");
        		objlist.add(b);
        	}
    		company_list.set("company_list",Json.toJson(objlist));
    		all.set("result", Json.toJson(company_list));
    	}else{
	      	ObjectNode error = Json.newObject();
	    	error.put("message", "失败");
	    	error.put("code",  0);
	    	//result.set("error",Json.toJson(error));
	    	all.set("error", error);
    	}
    	return ok(Json.toJson(all));
    }
    
    public Result reg() {
    	response().setContentType("application/json;charset=utf-8");
    	JsonNode req = request().body().asJson();
    	JsonNode params=Json.newObject();
    	ObjectNode all =Json.newObject();
		all.put("jsonrpc", "2.0");
		all.put("id",  "1448096464279");
    	ObjectNode result = Json.newObject();
    	if (req.get("params")!= null){
    		params = req.get("params");
    		String company_id=params.get("company_id").asText()==null ?"":params.get("company_id").asText();
    		String job_num=params.get("job_num").asText()==null ?"":params.get("job_num").asText();
    		String phone=params.get("phone").asText()==null ?"":params.get("phone").asText();
    		String name=params.get("name").asText()==null ?"":params.get("name").asText();
    		String id_card_no=params.get("id_card_no").asText()==null ?"":params.get("id_card_no").asText();
    		String substation=params.get("substation").asText()==null ?"":params.get("substation").asText();
    		
    		Postcompany postcompany = UserService.getPostcompanyById(Numbers.parseInt(company_id, 0));
        	if(postcompany==null){
        		ObjectNode error = Json.newObject();
    	    	error.put("message", "注册失败");
    	    	error.put("code",  0);
    	    	//result.set("error",Json.toJson(error));
    	    	all.set("error", error);
    	    	return ok(Json.toJson(all));
        	}
        	
        	PostmanUser postmanUser = UserService.getPostManUserByComidAndStafid(Numbers.parseInt(company_id, 0),job_num);//根据公司ID与工号去获取是否存在快递员
        	if(postmanUser!=null){
        		ObjectNode error = Json.newObject();
    	    	error.put("message", "该工号已被使用");
    	    	error.put("code",  0);
    	    	all.set("error", error);
    	    	return ok(Json.toJson(all));
        	}
        	postmanUser = UserService.getPostManUserByPhone_all(phone);
        	if (postmanUser== null){
        		postmanUser = new PostmanUser();
        	}
        	postmanUser.setPhone(phone);
        	postmanUser.setCompanyid(Numbers.parseInt(company_id, 0));
        	postmanUser.setStaffid(job_num);
        	
        	postmanUser.setNickname(name);
        	postmanUser.setSubstation(substation);
        	postmanUser.setAlipayAccount("");
        	postmanUser.setCompanyname(postcompany.getCompanyname());
        	postmanUser.setDateNew(new Date());
        	postmanUser.setDateUpd(new Date());
        	postmanUser.setHeadicon("http://apipic.higegou.com/upload/endorsement/97047278cef1432c980f1b78542a4daf.jpg");
        	//postmanUser.setSta(String.valueOf(Constants.PostmanStatus.NOCHECK.getStatus()));
        	if(Numbers.parseInt(company_id, 0)==1){
        		job_num=postmanUser.getStaffid();
        		name=postmanUser.getNickname();
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
        				postmanUser.setSta(String.valueOf(Constants.PostmanStatus.FAILED.getStatus()));//默认审核失败
        				//发送审核失败短信
        				SmsService.saveSFBack(phone, "1");
        				ObjectNode error = Json.newObject();
            	    	error.put("message", "审核失败");
            	    	error.put("code",  0);
            	    	all.set("error", error);
            	    	return ok(Json.toJson(all));
        			}
        		}
        	}else{
        		postmanUser.setSta(String.valueOf(Constants.PostmanStatus.NOCHECK.getStatus()));//默认未审核
        	}
        	postmanUser = UserService.savePostuserman(postmanUser);
        
			int flag = CertificationService.checkNameWithCard("" + postmanUser.getId(), name, id_card_no);
			if (flag == 0) {
				postmanUser.setCardidno(id_card_no);
				postmanUser = UserService.savePostuserman(postmanUser);
			}
    		
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
    		
    		
    		
    		String ip = request().remoteAddress();
        	
        	Deviceuser deviceuser = UserService.getDeviceuserByUid(postmanUser.getId());
    		if(deviceuser==null){
    			deviceuser = new Deviceuser();
    		}
        	LogFileForStastic logFileForStastic = new LogFileForStastic();
        	try {
        		logFileForStastic.creatTxtFile("logFileForStastic"+Dates.formatDateNew(new Date()));
            	logFileForStastic.writeTxtFile(Dates.formatDateTime_New(new Date())+","+ip+","+deviceuser.getModel()+","+deviceuser.getOstype()+","+deviceuser.getOsversion()+","+deviceuser.getDeviceid()+","+postmanUser.getId()+",reg,accountapp,");
            } catch (IOException e) {
                e.printStackTrace();
            }
        	
        	result.put("token",token);
        	result.put("status","2");
        	result.put("company_icon_url","");
        	result.put("is_audit","0");
    		all.set("result",Json.toJson(result));
    	}else{
	      	ObjectNode error = Json.newObject();
	    	error.put("message", "注册失败");
	    	error.put("code",  0);
	    	//result.set("error",Json.toJson(error));
	    	all.set("error", error);
    	}
    	return ok(Json.toJson(all));
    }
    
    public Result get_last() {
    	response().setContentType("application/json;charset=utf-8");
    	JsonNode req = request().body().asJson();
    	JsonNode params=Json.newObject();
    	ObjectNode all =Json.newObject();
		all.put("jsonrpc", "2.0");
		all.put("id",  "1448096464279");
    	ObjectNode result = Json.newObject();
    	if (req.get("params")!= null){
    		params = req.get("params");
    		//String version_name=params.get("version_name").asText();
    		String version_code=params.get("version_code").asText();
    		ObjectNode re=Json.newObject();
    		ObjectNode version=Json.newObject();
    		version.put("create_time", "");
    		version.put("version_name", "");
    		version.put("apk_url", "http://higou-oms.oss-cn-beijing.aliyuncs.com/upload/bbt/neolixBind_devApp_release_1.0.0.apk");
    		version.put("version_code", 1111111);
    		version.put("description", "账号系统升级文案测试");
    		version.put("md5", "");
    		re.set("version", Json.toJson(version));
    		re.put("must_upgrade", 0);
    		re.put("need_upgrade", 0);
    		//result.set("result", Json.toJson(re));
    		all.set("result", Json.toJson(re));
    	}else{
	      	ObjectNode error = Json.newObject();
	    	error.put("message", "1");
	    	error.put("code",  "成功");
	    	//result.set("error",Json.toJson(error));
	    	all.set("error", error);
    	}
    	return ok(Json.toJson(all));
    }
    
    public Result unbind_device() {
    	response().setContentType("application/json;charset=utf-8");
    	String token = request().getQueryString("token");
    	cache.clear(token);
    	cache.clear(Constants.cache_postmanid+token);
    	ObjectNode all =Json.newObject();
		all.put("jsonrpc", "2.0");
		all.put("id",  "1448096464279");
    	ObjectNode result = Json.newObject();
    	//result.put("result", "OK");
    	all.put("result", "OK");
    	return ok(Json.toJson(all));
    }
    
    public Result get() {
    	response().setContentType("application/json;charset=utf-8");
    	String token = request().getQueryString("token");
    	ObjectNode all =Json.newObject();
		all.put("jsonrpc", "2.0");
		all.put("id",  "1448096464279");
    	ObjectNode result = Json.newObject();
    	String postmanid = cache.get(Constants.cache_postmanid+token);
    	PostmanUser postmanUser = UserService.getPostManUserById(Numbers.parseInt(postmanid, 0));
    	if(postmanUser==null){
    		ObjectNode obj= Json.newObject();
    		obj.put("is_register", 0);
    		obj.put("token","");
    		obj.put("job_num", "");
    		obj.put("status", "0");
    		obj.put("is_audit", "0");
    		obj.put("name", "");
    		obj.put("company_id", "0");
    		obj.put("company_name","");
    		obj.put("postman_working_status", "2");
    		obj.put("company_icon_url", "");
    		obj.put("header_url", "");
    		obj.put("qr_code", "");
    		obj.put("phone", "");
    		obj.put("id_card_no", "");
    		obj.put("liked", "0");
    		obj.put("disliked", "0");
            all.set("result", Json.toJson(obj));
	    	return ok(Json.toJson(all));
    	}
    	JsonNode req = request().body().asJson();
    	if (req.get("params")!= null){
    		ObjectNode obj= Json.newObject();
    		obj.put("is_register", "1");
    		obj.put("token",token);
    		obj.put("job_num", postmanUser.staffid);
    		switch(postmanUser.sta){
	    		case "1":
	    			obj.put("status","1");
	    			break;
	    		case "0":
	    			obj.put("status","2");
	    			break;
	    		case "2":
	    			obj.put("status","3");
	    			obj.put("is_register", 0);
	    			break;
    		}
    		obj.put("is_audit", "1");
    		obj.put("name", postmanUser.phone);
    		obj.put("company_id", postmanUser.companyid);
    		obj.put("company_name", postmanUser.companyname);
    		obj.put("postman_working_status", "2");
    		obj.put("company_icon_url", "");
    		obj.put("header_url", "");
    		obj.put("qr_code", "");
    		obj.put("phone", postmanUser.phone);
    		obj.put("id_card_no", postmanUser.cardidno);
    		obj.put("substation", postmanUser.substation);
    		obj.put("liked", "0");
    		obj.put("disliked", "0");
            //result.set("result", Json.toJson(obj));
            all.set("result", Json.toJson(obj));
    	}else{
	      	ObjectNode error = Json.newObject();
	      	error.put("message", "获取用户失败");
	    	error.put("code", -32702);
	    	//result.set("error",Json.toJson(error));
	    	all.set("error", error);
    	}
    	return ok(Json.toJson(all));
    }
    
    
}
