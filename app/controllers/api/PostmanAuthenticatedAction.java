package controllers.api;

import javax.inject.Named;

import models.postman.PostmanUser;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;

import play.Logger;
import play.data.Form;
import play.libs.F;
import play.libs.F.Function0;
import play.libs.F.Promise;
import play.libs.Json;
import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.Result;
import services.ServiceFactory;
import services.cache.ICacheService;
import services.user.UserService;
import utils.AjaxHelper;
import utils.Constants;
import utils.ErrorCode;
import utils.Numbers;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author luobotao
 * Date: 2015年4月15日 上午10:49:39
 */
@Named
@Scope("prototype")
public class PostmanAuthenticatedAction extends Action<PostmanAuthenticated> {
	private static final Logger.ALogger logger = Logger.of(PostmanAuthenticatedAction.class);
	ICacheService cache = ServiceFactory.getCacheService();
	@Override
	public Promise<Result> call(final Context ctx) throws Throwable {
		F.Promise<Result> resultAction;
		
		String token = ctx.request().getQueryString("token");
		if(StringUtils.isBlank(token)){
			token = AjaxHelper.getHttpParamOfFormUrlEncoded(ctx.request(), "token");
		}
		if(StringUtils.isBlank(token)){
			token = Form.form().bindFromRequest().get("token");//
		}
		if(StringUtils.isBlank(token)){
			return Promise.promise(new Function0<Result>() {
				@Override
				public Result apply() throws Throwable {
					ObjectNode result = Json.newObject();
					result.put("status", ErrorCode.getErrorCode("global.tokenError"));
					result.put("msg", ErrorCode.getErrorMsg("global.tokenError"));
					return ok(Json.toJson(result));
				}
			});
		}else{
			int postmanid = 0;
			PostmanUser postmanuser = new PostmanUser();
	    	if(StringUtils.isBlank(cache.get(token))){
	    		postmanuser=UserService.getPostManUserByToken(token);
	    		if(postmanuser==null){//用户信息不存在
	    			return Promise.promise(new Function0<Result>() {
	    				@Override
	    				public Result apply() throws Throwable {
	    					ObjectNode result = Json.newObject();
	    					result.put("status", ErrorCode.getErrorCode("global.tokenError"));
	    					result.put("msg", ErrorCode.getErrorMsg("global.tokenError"));
	    					return ok(Json.toJson(result));
	    				}
	    			});
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
	    		return Promise.promise(new Function0<Result>() {
    				@Override
    				public Result apply() throws Throwable {
    					ObjectNode result = Json.newObject();
    					result.put("status", ErrorCode.getErrorCode("global.postmanNotExit"));
    					result.put("msg", ErrorCode.getErrorMsg("global.postmanNotExit"));
    					return ok(Json.toJson(result));
    				}
    			});
	    		
	    	}
	    	if(Constants.PostmanStatus.FAILED.getStatus()==Numbers.parseInt(postmanuser.getSta(), 0)){//用户信息审核失败
	    		return Promise.promise(new Function0<Result>() {
    				@Override
    				public Result apply() throws Throwable {
    					ObjectNode result = Json.newObject();
    					result.put("status", ErrorCode.getErrorCode("global.postmanStatError"));
    					result.put("msg", ErrorCode.getErrorMsg("global.postmanStatError"));
    					return ok(Json.toJson(result));
    				}
    			});
	    	}
			resultAction = delegate.call(ctx);
		}
		return resultAction;
	}

	

}
