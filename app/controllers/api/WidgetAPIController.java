package controllers.api;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import models.Deviceuser;
import models.postman.PostmanUser;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.ServiceFactory;
import services.cache.ICacheService;
import services.user.UserService;
import utils.AjaxHelper;
import utils.Constants;
import utils.Dates;
import utils.ErrorCode;
import utils.LogFileForStastic;
import utils.Numbers;
import vo.api.PostcontentVO;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

/**
 * widget相关的API
 * @author luobotao
 * @Date 2015年11月10日
 */
public class WidgetAPIController extends Controller {
	private static final Logger.ALogger logger = Logger.of(WidgetAPIController.class);
	SimpleDateFormat CHINESE_DATE_TIME_FORMAT_NORMAL_DAY = new SimpleDateFormat("yyyy-MM-dd");
	ICacheService cache = ServiceFactory.getCacheService();
    /**
     * widget列表接口(POST)bbt_widget
     * @return
     */
    public Result bbt_widget() {
    	response().setContentType("application/json;charset=utf-8");
    	JsonNode req = request().body().asJson();
    	List<String> client_ids =new ArrayList<String>();
    	List<String> server_ids =new ArrayList<String>();
    	List<String> all_ids =new ArrayList<String>();
    	String token="";
    	ObjectNode result = Json.newObject();
    	//ObjectNode linkurl = Json.newObject();
    	if (req.get("token")!= null){
    		token=	req.get("token").asText();
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
    	if(postmanuser==null){//用户信息不存在
    		result.put("status", ErrorCode.getErrorCode("global.postmanNotExit"));
    		result.put("msg", ErrorCode.getErrorMsg("global.postmanNotExit"));
    		return ok(Json.toJson(result));
    	}
    	JsonNode idandt=Json.newObject();
    	
    	if (req.get("idsandt")!= null){
    		idandt = req.get("idsandt");
    		Iterator<JsonNode> temp = idandt.elements();
    		while (temp.hasNext()) {
    			JsonNode id = temp.next();
    			if(id != null){
    				client_ids.add(id.get("id").asText());
    			}
			}
    	}
    
    	List<String>cancel_ids =new ArrayList<String>();
    	Integer uid=postmanuser.getId();
    	//Integer uid=2;
    	// 根据token获取用户ID
    	if (uid<=0){
    		result.put("status", "0");
	        result.put("msg",  "token串失效，获取用户失败");
	        return ok(Json.toJson(result));
    	}
    	
    	if(uid>0){
    		 List<PostcontentVO> returnwidgetlist = Lists.newArrayList();
    		 List<PostcontentVO> widgetlist = UserService.getPostcontentListByUid(postmanuser, 0, 0, 10000);
    		 for(PostcontentVO p:widgetlist){
    			 server_ids.add(p.id);
    			 if (!client_ids.contains(p.id)){
    				 returnwidgetlist.add(p);
    			 }else{
    				Iterator<JsonNode> temp = idandt.elements();
    				if(p.id.equals("-1")){
						returnwidgetlist.add(p);
					}
		    		while (temp.hasNext()) {
		    			JsonNode id = temp.next();
		    			if(id != null){
		    				if (id.get("t")!=null && id.get("t")!=null){
		    					if(id.get("id").asText().equals(p.id)&& !id.get("t").asText().equals(p.t)){
			    					 returnwidgetlist.add(p);
			    				}	
		    				}
		    			}
					}
    			 }
    		 }
    		 all_ids.addAll(client_ids);
    		 all_ids.addAll(server_ids);
    		 
    		 //通知客户端删除判断
    		 all_ids.removeAll(server_ids);
    		 cancel_ids=all_ids;
    		 
    		 result.put("status", "1");
    	     result.put("msg",  "成功");
    	     //linkurl.put("linkurl","http://bbtoms.ibbt.com/H5/newsList?ltitle=新闻列表");
    	     result.put("moreurl",  "http://bbtoms.ibbt.com/H5/newsList?ltitle=新闻列表");
    	        
    	     result.set("cancel_ids",Json.toJson(cancel_ids));
    	     result.set("widgetlist",Json.toJson(returnwidgetlist));
    	}
    	return ok(Json.toJson(result));
    }
    
    
    /**
     * 打点接口(POST)app_buriedpoint
     * @return
     */
    public Result app_buriedpoint() {
    	response().setContentType("application/json;charset=utf-8");
    	ObjectNode result = Json.newObject();
    	JsonNode req = request().body().asJson();
    	String token="";
    	String url="";
    	String src="";
    	String id="";
    	if (req.get("token")!= null){
    		token=req.get("token").asText();
    	}
    	if (req.get("url")!= null){
    		url=req.get("url").asText();
    	}
    	if (req.get("src")!= null){
    		src=req.get("src").asText();
    	}
    	String ip = request().remoteAddress();
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
    	Deviceuser deviceuser = UserService.getDeviceuserByUid(postmanid);
		if(deviceuser==null){
			deviceuser = new Deviceuser();
		}
    	LogFileForStastic logFileForStastic = new LogFileForStastic();
    	try {
    		logFileForStastic.creatTxtFile("logFileForStastic"+Dates.formatDateNew(new Date()));
    		if (url.equals("list")){
    			String text ="";
    			try {
    				text = Dates.formatDateTime_New(new Date())+","+ip+","+deviceuser.getModel()+","+deviceuser.getOstype()+","+deviceuser.getOsversion()+","+deviceuser.getDeviceid()+","+postmanid+",tasklist,"+src+",";	
				} catch (Exception e) {
					e.printStackTrace();
					text = "";
				}
    			logFileForStastic.writeTxtFile(text);
    		}else{
    			logFileForStastic.writeTxtFile(Dates.formatDateTime_New(new Date())+","+ip+","+deviceuser.getModel()+","+deviceuser.getOstype()+","+deviceuser.getOsversion()+","+deviceuser.getDeviceid()+","+postmanid+",taskinfo,"+src+","+url);
    		}	
        } catch (IOException e) {
            e.printStackTrace();
        }
    	result.put("status", "1");
    	result.put("msg",  "打点成功");
    	return ok(Json.toJson(result));
    }
    
    /**
     * widget详情页接口(GET)widget_info
     * @return
     */
    public Result bbt_widget_info() {
    	response().setContentType("application/json;charset=utf-8");
    	ObjectNode result = Json.newObject();
    	PostcontentVO widgetinfo = new PostcontentVO();
    	int postmanid = 0;
    	PostmanUser postmanuser = new PostmanUser();
    	
    	JsonNode req = request().body().asJson();
    	String token="";
    	String id="";
    	if (req.get("token")!= null){
    		token=	req.get("token").asText();
    		if (token.length()>0){
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
    		}
    	}
    	
    	//SELECT token,COUNT(token) FROM postmanuser GROUP BY token HAVING COUNT(token)>1
    	
    	Deviceuser deviceuser = UserService.getDeviceuserByUid(postmanid);
		if(deviceuser==null){
			deviceuser = new Deviceuser();
		}
    	if (req.get("id")!= null){
    		id=req.get("id").asText();
    		if(id.equals("-1"))
    		{
    			String tim=CHINESE_DATE_TIME_FORMAT_NORMAL_DAY.format(new Date());
    			widgetinfo = UserService.getDeliveryWidgetTotal(postmanid,tim);
    			result.put("status", "1");
	        	result.put("msg",  "成功");
	        	result.set("widgetinfo",Json.toJson(widgetinfo));
    		}else{
	    		if(!StringUtils.isBlank(id))
	    		{
		    		widgetinfo = UserService.getPostcontentInfoByid(Numbers.parseInt(id,0));
		    		result.put("status", "1");
		        	result.put("msg",  "成功");
		        	result.set("widgetinfo",Json.toJson(widgetinfo));
	    		}else{
	    			result.put("status", "0");
		        	result.put("msg",  "ID错误");
	    		}
    		}	
    	}
    	return ok(Json.toJson(result));
    }
    
    
    public Result bbt_widget_pushbind(){
    	response().setContentType("application/json;charset=utf-8");
    	ObjectNode result = Json.newObject();
    	
    	JsonNode req = request().body().asJson();
    	String token="";
    	String devid="";
    	String pushtoken="";
    	String version="";
    	if (req.get("token")!= null){
    		token=req.get("token").asText();
    	}
    	if (req.get("deviceid")!= null){
    		devid=req.get("deviceid").asText();
    	}
    	if (req.get("pushtoken")!= null){
    		pushtoken=req.get("pushtoken").asText();
    	}
    	if (req.get("version")!= null){
    		version=req.get("version").asText();
    	}
    	String postmanid = Numbers.parseInt(cache.get(Constants.cache_postmanid+token), 0).toString();
    	Integer uid= Integer.valueOf(postmanid);
    	
    	//根据token获取用户ID
    	if (uid<=0){
    		result.put("status", "0");
	        result.put("msg",  "token串失效，获取用户失败");
	        return ok(Json.toJson(result));
    	}
    	
    	Deviceuser deviceuser = UserService.getDeviceuserByDevid(devid);
		if(deviceuser==null){
			deviceuser = new Deviceuser();
		}
		deviceuser.setUid(uid);
		deviceuser.setDeviceid(devid);
		deviceuser.setOstype("1");
		deviceuser.setOsversion("1.0");
		deviceuser.setModel("X7");
		deviceuser.setAppversion("1.0.0");
		deviceuser.setPushToken(pushtoken);
		deviceuser.setVersion(version);
		deviceuser.setDateNew(new Date());
		deviceuser.setDateUpd(new Date());
		
		deviceuser = UserService.saveDeviceUser(deviceuser);
    	result.put("status", "1");
    	result.put("msg",  "成功");
    	return ok(Json.toJson(result));
    }
    
}
