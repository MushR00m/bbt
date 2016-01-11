package controllers.api;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.node.ObjectNode;

import models.magazine.MagazineInfo;
import models.magazine.Magazinelist;
import models.postman.PostmanUser;
import models.postman.Reddot;
import play.Configuration;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.ServiceFactory;
import services.cache.ICacheService;
import services.magazine.MagazineService;
import services.user.UserService;
import utils.Constants;
import utils.ErrorCode;
import utils.Numbers;
import vo.api.MagezineInfoVO;

/**
 * 杂志相关的API
 * 
 * @author luobotao
 * @Date 2015年11月10日
 */
public class MagazineAPIController extends Controller {
	ICacheService cache = ServiceFactory.getCacheService();

	/**
	 * 杂志列表接口(GET)user_magazine_list
	 * 
	 * @return
	 */
	public Result user_magazine_list() {
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
		if (postmanuser == null) {// 用户信息不存在
			result.put("status", ErrorCode.getErrorCode("global.postmanNotExit"));
			result.put("msg", ErrorCode.getErrorMsg("global.postmanNotExit"));
			return ok(Json.toJson(result));
		}
		//红点逻辑---开始
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
    	reddot.setMyfav("0");
    	UserService.saveReddot(reddot);
    	//红点逻辑---结束
		String lastindex = request().getQueryString("lastindex");
		String pagesize = request().getQueryString("pagesize");
		result.put("status", "1");
		result = MagazineService.getMagezineListObject(result, postmanid, Numbers.parseInt(lastindex, 0),
				Numbers.parseInt(pagesize, 10));
		return ok(Json.toJson(result));
	}

	/**
	 * 杂志详情页接口(GET)user_magazine
	 * 
	 * @return
	 */
	public Result user_magazine() {
		response().setContentType("application/json;charset=utf-8");
		ObjectNode result = Json.newObject();
		String token = request().getQueryString("token");
		String mgid = request().getQueryString("id");
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
		Magazinelist magazinelist = MagazineService.getMagazinelistById(Numbers.parseInt(mgid, 0));//根据id获取杂志列表的详情
		if (magazinelist==null) {
			result.put("status", ErrorCode.getErrorCode("global.magazineListNotExit"));
			result.put("msg", ErrorCode.getErrorMsg("global.magazineListNotExit"));
			return ok(Json.toJson(result));
		}
		MagezineInfoVO magezineInfoVO = new MagezineInfoVO();
		magezineInfoVO.id = mgid;
		magezineInfoVO.title = magazinelist.getTitle();
		List<MagazineInfo> magazineInfoList = MagazineService.getMagazineImg(Integer.valueOf(mgid));
		for (MagazineInfo magazineInfo : magazineInfoList) {
			magezineInfoVO.imglist.add(Configuration.root().getString("oss.image.url") + magazineInfo.getImgurl());
		}
		result.put("status", "1");
		result.set("magazineinfo", Json.toJson(magezineInfoVO));
		return ok(Json.toJson(result));
	}

}
