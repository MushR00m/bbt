package controllers.api;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import models.postman.Balance;
import models.postman.BalanceIncome;
import models.postman.PostmanUser;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.ServiceFactory;
import services.cache.ICacheService;
import services.user.UserService;
import utils.Numbers;
import utils.StringUtil;

/**
 * 向外部提供的API
 * 
 * @author luobotao
 * @Date 2015年11月10日
 */
public class OutAPIController extends Controller {
	ICacheService cache = ServiceFactory.getCacheService();

	/**
	 * 向嗨购OMS提供的返现接口
	 * @return
	 */
	public Result income() {
		response().setContentType("application/json;charset=utf-8");
		ObjectNode result = Json.newObject();
		JsonNode req = request().body().asJson();
		if(req==null){
			result.put("result", "false");
			result.put("code", "10000");
			result.put("message", "非法请求类型");
			return ok(Json.toJson(result));
		}
		try {
			String sign = req.get("sign").asText();
			String time_stamp = req.get("time_stamp").asText();
			Integer postman_id = Numbers.parseInt(req.get("postman_id").asText(), 0);
			String amount = req.get("amount").asText();
			String desc = req.get("desc").asText();
			String out_trade_no = req.get("out_trade_no").asText();
			String catalog = req.get("catalog").asText();
			String sub_catalog = req.get("sub_catalog").asText();
			String type = req.get("type").asText();
			Map<String,String> getmap=new HashMap<String,String>();
			getmap.put("out_trade_no",out_trade_no);
			getmap.put("time_stamp", time_stamp);
			String signstr=StringUtil.makeSig(getmap);
			if(!signstr.equals(sign)){
				result.put("result", "false");
				result.put("code", "10002");
				result.put("message", "签名不正确");
				return ok(Json.toJson(result));
			}
			PostmanUser postmanUser = UserService.getPostManUserById(postman_id);
			if(postmanUser==null){
				result.put("result", "false");
				result.put("code", "10003");
				result.put("message", "快递员不存在");
				return ok(Json.toJson(result));
			}
			UserService.saveIncome(postmanUser,amount,desc,out_trade_no,desc,sub_catalog,"higou",type);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", "false");
			result.put("code", "10001");
			result.put("message", "参数不合法");
			return ok(Json.toJson(result));
		}
		result.put("result", "ok");
		
		return ok(Json.toJson(result));
	}

}
