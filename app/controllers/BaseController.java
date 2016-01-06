package controllers;

import models.postman.PostmanUser;

import org.apache.commons.lang3.StringUtils;

import play.data.Form;
import play.mvc.Controller;
import play.mvc.Http.Request;
import services.ServiceFactory;
import services.cache.ICacheService;
import services.user.UserService;
import utils.AjaxHelper;
import utils.Constants;
import utils.Numbers;

public class BaseController extends Controller {
	ICacheService cache = ServiceFactory.getCacheService();

	/**
	 * 获取当前登录的快递员信息
	 * 在调用此方法前必须进行PostmanAuthenticatedAction composition
	 * 否则会出现空异常
	 * @param request
	 * @return
	 */
	public PostmanUser getCurrentPostmanUser(Request request) {
		String token = request().getQueryString("token");
		if(StringUtils.isBlank(token)){
			token = AjaxHelper.getHttpParamOfFormUrlEncoded(request, "token");
		}
		if(StringUtils.isBlank(token)){
			token = Form.form().bindFromRequest().get("token");//
		}
		int postmanid = 0;
		PostmanUser postmanuser = new PostmanUser();
		if (StringUtils.isBlank(cache.get(token))) {
			return UserService.getPostManUserByToken(token);
		} else {
			postmanid = Numbers.parseInt(cache.get(Constants.cache_postmanid + token), 0);
			postmanuser = UserService.getPostManUserById(postmanid);
			return postmanuser;
		}
	}

}
