package controllers.api;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import models.Openuser;
import models.Postcompany;
import models.Postdelivery;
import models.Postdelivery_goods;
import models.postman.PostmanUser;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.ServiceFactory;
import services.cache.ICacheService;
import services.express.ExpressService;
import services.user.OpenService;
import services.user.UserService;
import utils.Constants.DeliverStas;
import utils.Constants.NeedPayStas;
import utils.Constants.PayModeStas;
import utils.Dates;
import utils.ErrorCode;
import utils.Numbers;
import utils.StringUtil;
import utils.WSUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;

/**
 * 向第三方提供的API
 * 
 * @author luobotao
 * @Date 2015年11月10日
 */
public class OpenAPIController extends Controller {
	ICacheService cache = ServiceFactory.getCacheService();

	/**
	 * 派件通知
	 * @return
	 */
	public Result order() {
		response().setContentType("application/json;charset=utf-8");
		ObjectNode result = Json.newObject();
		JsonNode req = request().body().asJson();
		if(req==null){
			result.put("result", "error");
			result.put("code", "51000");
			result.put("message", "参数格式错误");
			return ok(Json.toJson(result));
		}
		Logger.info("派件通知，第三方请求数据："+req);
		try {
			String a = req.get("sign")==null?"":req.get("sign").asText();
			Map<Object,Object> getmap=new HashMap<Object,Object>();
			String sign = req.get("sign").asText();
			String code = req.get("code").asText();//接入方公司代码(不参与加密)
			String outer_trade_no = req.get("outer_trade_no").asText();//接入方订单唯一 ID
			String unid = req.get("unid").asText();//派送员唯一 ID
			String merchant_code = req.get("merchant_code").asText();//商户代码
			String delivery_company_code = req.get("delivery_company_code").asText();//落地配企业代码
			String mail_num = req.get("mail_num").asText();//运单号
			String delivery_type = req.get("delivery_type").asText();//订单类型 1-签单返回 2-退货 3-换货 4-普通件 5-加急件
			String s_company = req.get("s_company").asText();//寄件方公司名称 
			String d_company = req.get("d_company").asText();//收件方公司名称
			String d_contact = req.get("d_contact").asText();//收件方联系人 
			String d_address = req.get("d_address").asText();//收件方地址 
				
			Openuser openUser = OpenService.getOpenuserByCode(code);
			if(openUser==null){
				result.put("result", "error");
				result.put("code", "51001");
				result.put("message", "开放平台接入码不存在");
				return ok(Json.toJson(result));
			}
			String s_contact = req.get("s_contact")==null?"":req.get("s_contact").asText();//寄件方联系人
			String s_tel = req.get("s_tel")==null?"":req.get("s_tel").asText();//寄件方固定电话
			String s_mobile = req.get("s_mobile")==null?"":req.get("s_mobile").asText();//寄件方手机
			String s_address = req.get("s_address")==null?"":req.get("s_address").asText();//寄件方地址
			String s_province = req.get("s_province")==null?"":req.get("s_province").asText();//寄件方所在省 直辖市直接传北京市，上海市等
			String s_city = req.get("s_city")==null?"":req.get("s_city").asText();//寄件方所在市  直辖市直接传北京市，上海市等
			String s_district = req.get("s_district")==null?"":req.get("s_district").asText();//寄件方所在区
			String s_code = req.get("s_code")==null?"":req.get("s_code").asText();//寄件方所在区域码
			String d_tel = req.get("d_tel")==null?"":req.get("d_tel").asText();//收件方固定电话 固定电话和手机二选一
			String d_mobile = req.get("d_mobile")==null?"":req.get("d_mobile").asText();//收件方手机 固定电话和手机二选一
			String d_province = req.get("d_province")==null?"":req.get("d_province").asText();//收件方所在省
			String d_city = req.get("d_city")==null?"":req.get("d_city").asText();//收件方所在市
			String d_district = req.get("d_district")==null?"":req.get("d_district").asText();//收件方所在区
			String d_code = req.get("d_code")==null?"":req.get("d_code").asText();//收件方所在区域码
			String mch_order_num = req.get("mch_order_num")==null?"":req.get("mch_order_num").asText();//电商订单号 
			String mch_package_num = req.get("mch_package_num")==null?"":req.get("mch_package_num").asText();//电商包裹号
			int goods_num = req.get("goods_num")==null?0:req.get("goods_num").asInt();//邮寄物总数量  单位为个  
			int goods_fee = req.get("goods_fee")==null?0:req.get("goods_fee").asInt();//收款金额 单位为分 退货、换货时，金额为负值，无付款、已付款时，金额为 0，其他情况，金额为正值。
			String remark = req.get("remark")==null?"":req.get("remark").asText();//备注 
			
			getmap.put("outer_trade_no", outer_trade_no);
			getmap.put("unid", unid);
			getmap.put("merchant_code", merchant_code);
			getmap.put("delivery_company_code", delivery_company_code);
			getmap.put("mail_num", mail_num);
			getmap.put("delivery_type", delivery_type);
			getmap.put("s_company", s_company);
			getmap.put("d_company", d_company);
			getmap.put("d_contact", d_contact);
			getmap.put("d_address", d_address);
			getmap.put("goods_num", goods_num);
			getmap.put("s_contact", s_contact);
			getmap.put("goods_fee", goods_fee);
			getmap.put("remark", remark);
			getmap.put("s_tel", s_tel);
			getmap.put("s_mobile", s_mobile);
			getmap.put("s_address", s_address);
			getmap.put("s_province", s_province);
			getmap.put("s_city", s_city);
			getmap.put("s_district", s_district);
			getmap.put("s_code", s_code);
			getmap.put("d_tel", d_tel);
			getmap.put("d_mobile", d_mobile);
			getmap.put("d_province", d_province);
			getmap.put("d_city", d_city);
			getmap.put("d_district", d_district);
			getmap.put("d_code", d_code);
			getmap.put("mch_order_num", mch_order_num);
			getmap.put("mch_package_num", mch_package_num);
			String signstr=StringUtil.makeOpenSig(getmap,openUser.getToken());//验证sign
			Logger.info("user send sign is :"+sign+"=========");
			if(!signstr.equals(sign)){
				result.put("result", "error");
				result.put("code", "51002");
				result.put("message", "签名错误");
				return ok(Json.toJson(result));
			}
			Postdelivery postdelivery = OpenService.getPostdeliveryByMailNum(mail_num);
			if(postdelivery==null){
				postdelivery = new Postdelivery();
				postdelivery.setDateNew(new Date());
			}
			postdelivery.setFlg("1");//展示
			postdelivery.setStaffid(unid);//写入工号
			postdelivery.setCompany_code(delivery_company_code);//落地配企业代码
			Postcompany postcompany = UserService.getPostcompanyByCompanycode(delivery_company_code);
			if(postcompany==null){
				postdelivery.setPostman_id(0);
	    	}else{
	    		PostmanUser postmanUser = UserService.getPostManUserByComidAndStafid(postcompany.getId(),unid);//根据公司ID与工号去获取是否存在快递员
	    		if(postmanUser!=null){
	    			postdelivery.setPostman_id(postmanUser.getId());
	    			OpenService.pushWidgetpush(postmanUser.getId());
	    		}else{
	    			postdelivery.setPostman_id(0);
	    		}
	    	}
			postdelivery.setOut_trade_no(outer_trade_no);//接入方订单唯一 ID
			postdelivery.setMail_num(mail_num);
			if(goods_fee>0){
				postdelivery.setNeed_pay(NeedPayStas.NEEDYES.getStatus());
				postdelivery.setPay_mode("4");//（0：现金，1：刷卡，2：微信，3：支付宝，4：其它）
				//快递状态（[0:全部，服务器查询逻辑],1：未完成，2：已签收，3：已滞留，4：已拒绝，5：已退单
				postdelivery.setSta(String.valueOf(DeliverStas.WAIT.getStatus()));//待配送
			}else{
				postdelivery.setNeed_pay(NeedPayStas.NEEDNO.getStatus());
				postdelivery.setPay_mode("4");//（0：现金，1：刷卡，2：微信，3：支付宝，4：其它）
				postdelivery.setSta(String.valueOf(DeliverStas.WAIT.getStatus()));//待配送
			}
			postdelivery.setSender_name(s_contact );
			postdelivery.setSender_phone(s_tel);
			postdelivery.setSender_telphone(s_mobile);
			postdelivery.setSender_province(s_province);
			postdelivery.setSender_city(s_city);
			postdelivery.setSender_district(s_district);
			postdelivery.setSender_address(s_address);
			postdelivery.setSender_company_name(s_company);
			postdelivery.setSender_region_code(s_code);
			postdelivery.setReceiver_name(d_contact);
			postdelivery.setReceiver_phone(d_tel);
			postdelivery.setReceiver_telphone(d_mobile);
			postdelivery.setReceiver_province(d_province);
			postdelivery.setReceiver_city(d_city);
			postdelivery.setReceiver_district(d_district);
			postdelivery.setReceiver_address(d_address);
			postdelivery.setReceiver_company_name(d_company);
			postdelivery.setReceiver_region_code(d_code);
			postdelivery.setGoods_fee(goods_fee);
			postdelivery.setGoods_number(goods_num);
			postdelivery.setRemark(remark);
			postdelivery.setTyp(delivery_type);
			postdelivery.setDateUpd(new Date());
			postdelivery = OpenService.savePostdelivery(postdelivery);
			
			JsonNode goods_info = req.get("goods_info");//货物信息
			if(goods_info!=null){
				OpenService.deletePostdelivery_goodsByDid(postdelivery.getId());
				Iterator<JsonNode> goods_infoIt = goods_info.elements();
				while(goods_infoIt.hasNext()){
					JsonNode good = goods_infoIt.next();
					int num = good.get("num")==null?0:good.get("num").asInt();
					String name = good.get("name")==null?"":good.get("name").asText();
					Postdelivery_goods postdelivery_goods = new Postdelivery_goods();
					postdelivery_goods.setDateNew(new Date());
					postdelivery_goods.setDateUpd(new Date());
					postdelivery_goods.setDid(postdelivery.getId());
					postdelivery_goods.setGoodsname(name);
					postdelivery_goods.setGoodsnum(num);
					OpenService.savePostdelivery_goods(postdelivery_goods);
				}
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", "error");
			result.put("code", "51000");
			result.put("message", "参数格式错误");
			return ok(Json.toJson(result));
		}
		result.put("result", "ok");
		return ok(Json.toJson(result));
	}
	/**
	 * POS机支付结果通知接口
	 * @return
	 */
	public Result pos() {
		response().setContentType("application/json;charset=utf-8");
		ObjectNode result = Json.newObject();
		JsonNode req = request().body().asJson();
		if(req==null){
			result.put("result", "error");
			result.put("code", "51000");
			result.put("message", "参数格式错误");
			return ok(Json.toJson(result));
		}
		Logger.info("POS机支付结果通知接口，第三方请求数据："+req);
		try {
			Map<Object,Object> getmap=new HashMap<Object,Object>();
			String sign = req.get("sign").asText();
			String code = req.get("code").asText();//接入方公司代码(不参与加密)
			String delivery_company_code = req.get("delivery_company_code").asText();//落地配企业代码 
			String mail_num = req.get("mail_num").asText();//接入方运单号
			Openuser openUser = OpenService.getOpenuserByCode(code);
			if(openUser==null){
				result.put("result", "error");
				result.put("code", "51001");
				result.put("message", "开放平台接入码不存在");
				return ok(Json.toJson(result));
			}
			getmap.put("delivery_company_code",delivery_company_code);
			getmap.put("mail_num", mail_num);
			String signstr=StringUtil.makeOpenSig(getmap,openUser.getToken());//验证sign
			if(!signstr.equals(sign)){
				result.put("result", "error");
				result.put("code", "51002");
				result.put("message", "签名错误");
				return ok(Json.toJson(result));
			}
			Postdelivery postdelivery = OpenService.getPostdeliveryByMail_numAndComanyCode(delivery_company_code,mail_num);
			if(postdelivery!=null){
				postdelivery.setPay_status("1");//已支付
				postdelivery.setSta(String.valueOf(DeliverStas.SUCCESS.getStatus()));//已签收
				postdelivery.setPay_mode(PayModeStas.POS.getStatus());//（0：现金，1：刷卡，2：微信，3：支付宝，4：其它）
				OpenService.savePostdelivery(postdelivery);
				ExpressService.saveIncome(postdelivery.getId());
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", "error");
			result.put("code", "51000");
			result.put("message", "参数格式错误");
			return ok(Json.toJson(result));
		}
		result.put("result", "ok");
		
		return ok(Json.toJson(result));
	}
	/**
	 * 派件结果接口（调用易普联科）
	 * @return
	 */
	public Result orderResu() {
		response().setContentType("application/json;charset=utf-8");
		String code="WANBO";
		String outer_trade_no="178AVG664TRJ";
		String mail_num="15032039343825-T1";
		String payment_means="1";
		String delivery_result="7";//1  派送成功 2  收件人拒收 6  恢复归班前状态 7  滞留
		String sign_code="0";
		String fail_code="11";
		String message="超出配送范围";
		String postrace="32423455562";
		String cod="";
		String d_address="北京市朝阳区百子湾";
		String d_time="2015-12-03";
		String unid="test";
		String time_stamp=Dates.formatDateTime_New(new Date());//"2015-12-14 07:35:27";//
		ObjectNode result = Json.newObject();
		
		Map<Object,Object> getmap=new HashMap<Object,Object>();
		getmap.put("outer_trade_no",outer_trade_no);
		getmap.put("mail_num", mail_num);
		getmap.put("payment_means", payment_means);
		getmap.put("delivery_result", delivery_result);
		getmap.put("sign_code", sign_code);
		getmap.put("fail_code", fail_code);
		getmap.put("message", message);
		getmap.put("postrace", postrace);
		getmap.put("cod", cod);
		getmap.put("d_address", d_address);
		getmap.put("d_time", d_time);
		getmap.put("unid", unid);
		getmap.put("time_stamp", time_stamp);
		Openuser openUser = OpenService.getOpenuserByCode(code);
		if(openUser==null){
			result.put("result", "error");
			result.put("code", "51001");
			result.put("message", "开放平台接入码不存在");
			return ok(Json.toJson(result));
		}
		String sign=StringUtil.makeOpenSig(getmap,openUser.getToken());//生成sign
		getmap.put("sign", sign);
		getmap.put("code", code);
		String url="http://222.129.192.242:6001/api.pl/ibbt/build";
//		String url="http://103.229.214.164/tjpj/deliverServer/result";
		Logger.info("向第三方请求的数据："+Json.toJson(getmap));
		JsonNode jsonResult = WSUtils.postByJSON(url, Json.toJson(getmap));
		Logger.info(jsonResult+"====the data from result=======");
		return ok(Json.toJson(result));
	}
}
