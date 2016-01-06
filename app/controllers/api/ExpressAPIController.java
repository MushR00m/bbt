package controllers.api;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import models.DeleveryErrorMessage;
import models.Deviceuser;
import models.Openuser;
import models.Postdelivery;
import models.postman.PostmanUser;

import org.apache.commons.lang3.StringUtils;

import play.Logger;
import play.Play;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.ServiceFactory;
import services.cache.ICacheService;
import services.express.ExpressService;
import services.user.OpenService;
import services.user.UserService;
import utils.*;
import utils.Constants.DeliverStas;
import utils.Constants.NeedPayStas;
import utils.Constants.OutApiPayModeStas;
import utils.Constants.OutDeliverStas;
import utils.Constants.PayModeStas;
import vo.api.ExpressInfoVO;
import vo.api.ExpressReasonInfoVO;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;

import controllers.BaseController;

/**
 * 用户相关的API
 * @author luobotao
 * @Date 2015年11月10日
 */
public class ExpressAPIController extends BaseController {
	SimpleDateFormat CHINESE_DATE_TIME_FORMAT_NORMAL = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	SimpleDateFormat CHINESE_DATE_TIME_FORMAT_NORMAL_DAY = new SimpleDateFormat("yyyy-MM-dd");
	private static final java.util.regex.Pattern USERNAME_PATTERN = java.util.regex.Pattern.compile("[\u4E00-\u9FA5]{2,10}(?:·[\u4E00-\u9FA5]{2,10})*");
	ICacheService cache = ServiceFactory.getCacheService();
	private static final Logger.ALogger logger = Logger.of(ExpressAPIController.class);
    /**
     * 快递首页列表接口(GET)handled_express_list
     * @return
     */
	@PostmanAuthenticated
    public Result handled_express_list() {
    	response().setContentType("application/json;charset=utf-8");
        ObjectNode result = Json.newObject();
        String lastindex = request().getQueryString("lastindex");
        String state = request().getQueryString("state");
        int lastindexInteger = Numbers.parseInt(lastindex,0);
    	result.put("status", "1");
		result.put("msg", "");
		PostmanUser postmanuser = getCurrentPostmanUser(request());
		result=ExpressService.getExpressListObject(result,postmanuser.getId(),state,lastindexInteger,4);
		// 设置为已看
		if(ExpressService.getNoLookedCntByID(postmanuser.getId())>0)
		{
			UserService.widgetPush(postmanuser.getId(),-1);
			ExpressService.setDeleiveryLooked(postmanuser.getId());
		}
        return ok(Json.toJson(result));
    }

    /**
     * 未完成列表接口(GET)unhandled_express_list
     * @return
     */
	@PostmanAuthenticated
    public Result unhandled_express_list() {
    	response().setContentType("application/json;charset=utf-8");
        ObjectNode result = Json.newObject();
        String lastindex = request().getQueryString("lastindex");
        int lastindexInteger = Numbers.parseInt(lastindex,0);
       
    	result.put("status", "1");
		result.put("msg", "");
		PostmanUser postmanuser = getCurrentPostmanUser(request());
		result=ExpressService.getExpressDoingListObject(result,postmanuser.getId(),lastindexInteger,10);
		// 设置为已看
		if(ExpressService.getNoLookedCntByID(postmanuser.getId())>0)
		{
			UserService.widgetPush(postmanuser.getId(),-1);
			ExpressService.setDeleiveryLooked(postmanuser.getId());
		}	
        return ok(Json.toJson(result));
    }
    
    
    /**
     * 搜索列表接口(GET)search_express_list
     * @return
     */
	@PostmanAuthenticated
    public Result search_express_list() {
    	response().setContentType("application/json;charset=utf-8");
        ObjectNode result = Json.newObject();
        String lastindex = request().getQueryString("lastindex");
        String key = request().getQueryString("key")==null?"":request().getQueryString("key");
        
        int lastindexInteger = Numbers.parseInt(lastindex,0);
    	result.put("status", "1");
		result.put("msg", "");
		PostmanUser postmanuser = getCurrentPostmanUser(request());
		result=ExpressService.getExpressSearchListObject(result,postmanuser.getId(),key,lastindexInteger,10);
		// 设置为已看
		if(ExpressService.getNoLookedCntByID(postmanuser.getId())>0)
		{
			ExpressService.setDeleiveryLooked(postmanuser.getId());
			UserService.widgetPush(postmanuser.getId(),-1);
		}	
        return ok(Json.toJson(result));
    }
    
  
    /**
     * 快递订单详情接口(GET)express_detail
     * @return
     */
	@PostmanAuthenticated
    public Result express_detail() {
    	response().setContentType("application/json;charset=utf-8");
        ObjectNode result = Json.newObject();
    	result.put("status", "1");
    	String id=request().getQueryString("id");
    	int idInteger = Numbers.parseInt(id,0);
    	result.put("status", "1");
		result.put("msg", "");
		ExpressInfoVO eInfo = new ExpressInfoVO();
    	eInfo = ExpressService.getExpressInfoByID(idInteger);
		PostmanUser postmanuser = getCurrentPostmanUser(request());
		Deviceuser deviceuser = UserService.getDeviceuserByUid(postmanuser.getId());
		if(deviceuser==null){
			deviceuser = new Deviceuser();
		}
		LogFileForStastic logFileForStastic = new LogFileForStastic();
		try {
			logFileForStastic.creatTxtFile("logFileForStastic"+Dates.formatDateNew(new Date()));
			logFileForStastic.writeTxtFile(Dates.formatDateTime_New(new Date())+","+request().remoteAddress()+","+deviceuser.getModel()+","+deviceuser.getOstype()+","+deviceuser.getOsversion()+","+deviceuser.getDeviceid()+","+postmanuser.getId()+",deliveryinfo,App,");
		} catch (IOException e) {
			e.printStackTrace();
		}
    	result.set("expressinfo", Json.toJson(eInfo));
        return ok(Json.toJson(result));
    }
    
    /**
     * 快递状态回传接口(POST)update_express_status
     * @return
     */
	@PostmanAuthenticated
    public Result update_express_status() {
    	response().setContentType("application/json;charset=utf-8");
        ObjectNode result = Json.newObject();
        int expressid = Numbers.parseInt(AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "expressid"), 0);
        int state = Numbers.parseInt(AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "state"), 0);//快递状态（[0:全部，服务器查询逻辑],1：未完成，2：已签收，3：已滞留，4：已拒绝，5：已退单
        String paytypeid = AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "paytypeid");//(可选参数)支付ID（0：现金，1：刷卡，2：微信，3：支付宝，4：其它）
        String reasonid = AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "reasonid");//(可选参数) 滞留或拒绝原因ID
        String d_address = AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "reasonaddress");//(可选参数) 修改收货地址
        String d_time = AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "reasondatetime");//(可选参数) 修改收货时间
        PostmanUser postmanuser = getCurrentPostmanUser(request());
        String reasonmsg="订单状态：";
        if (state==1 || state==6){
        	state=2;
        }
        String msg ="";
        
    	/*设置配送订单状态修改成功*/
    	Postdelivery postdelivery = ExpressService.getPostdeliveryByid(expressid);//根据派送单I号D获取派送单
    	if(postdelivery!=null){
    		if("2".equals(postdelivery.getSta())){//已签收状态
    			result.put("status", "1");
    			result.put("msg", msg);
    			result.put("expressid", ""+expressid);
    			result.put("state", state);
    	        return ok(Json.toJson(result));
    		}
    		String payment_means=OutApiPayModeStas.Paid.getStatus();//付款方式(向第三方公司post数据使用) 1  现金 6  pos 机 7  支付宝 8  已付款
    		String delivery_result=OutDeliverStas.SUCCESS.getStatus();//派送状态 (向第三方公司post数据使用) 1  派送成功 2  收件人拒收 6  恢复归班前状态 7  滞留
    		String sign_code="1";//是否本人签收1 本人签收，0 他人代签收
    		String fail_code=null;//接口返回 派件 异常 编码 (向第三方公司post数据使用)
    		String message=null;//接口返回 派件 异常 信息(向第三方公司post数据使用)
    		postdelivery.setSta(String.valueOf(state));//
    		//快递状态（[0:全部，服务器查询逻辑],1：未完成，2：已签收，3：已滞留，4：已拒绝，5：已退单
    		if(DeliverStas.SUCCESS.getStatus()==state){//已签收
    			if(postmanuser.getCompanyid()!=null && postmanuser.getCompanyid().intValue()==4){//万博快递需要进行签收返现0.05元
    				ExpressService.saveIncome(expressid);
    			}
    			if(postdelivery.getTyp().equals("2")){
    				reasonmsg= reasonmsg+ "已退货";
    				postdelivery.setPay_mode("5");
    			}else{
	    			payment_means = OutApiPayModeStas.Paid.getStatus();
	    			logger.info(paytypeid+"===============支付方式+++++++++++++"+postdelivery.getNeed_pay()+"================"+NeedPayStas.NEEDYES.getStatus());
	    			if(NeedPayStas.NEEDYES.getStatus().equals(postdelivery.getNeed_pay())){//需要支付
	    				postdelivery.setPay_status("1");
	    				postdelivery.setPay_mode(paytypeid);//支付方式（0：现金，1：刷卡，2：微信，3：支付宝，4：其它）
	    				if(PayModeStas.Cash.getStatus().equals(paytypeid)){
	    					payment_means = OutApiPayModeStas.Cash.getStatus();
	    				}
	    				if(PayModeStas.POS.getStatus().equals(paytypeid)){
	    					payment_means = OutApiPayModeStas.POS.getStatus();
	    				}
	    				if(PayModeStas.Alipay.getStatus().equals(paytypeid)){
	    					payment_means = OutApiPayModeStas.Alipay.getStatus();
	    				}
	    				msg="您的订单已完成支付签收";
	    			}else{
	    				msg="您的订单已完成签收";
	    			}
	    			reasonmsg= reasonmsg+ "已签收";
	    			postdelivery.setRemark(reasonmsg);
	    			postdelivery.setResultmsg(reasonmsg);
    			}	
    		}
    		
    		if(DeliverStas.StopInSta.getStatus()==state){
    			reasonmsg= reasonmsg+ "已退单到站点";
    			postdelivery.setRemark(reasonmsg);
    			postdelivery.setResultmsg(reasonmsg);
    		}
    		
    		if(DeliverStas.DENY.getStatus()==state||DeliverStas.RollBack.getStatus()==state){//滞留或拒绝
    			DeleveryErrorMessage reason= new DeleveryErrorMessage(); 
    			sign_code = "0";
    			
    			if (!StringUtils.isBlank(reasonid)){
    				Integer rid =Numbers.parseInt(reasonid, 0);
    				if(rid>0){
		    			reason=ExpressService.getDeleveryErrorMessageInfo(rid);
		    			reasonmsg= reason.getMessage();
		    			fail_code = reason.getDecode();
		    			message= reason.getMessage();
    				}
    			}
    			if(DeliverStas.DENY.getStatus()==state){
    				delivery_result=OutDeliverStas.DENY.getStatus();
    				reasonmsg="拒收原因: "+reasonmsg;
    				msg="您的订单被拒收，请到已完成-已拒收查看";
    			}
    			
    			if(DeliverStas.RollBack.getStatus()==state){
    				delivery_result=OutDeliverStas.RollBack.getStatus();
    				reasonmsg="滞留原因： "+reasonmsg;
    				msg="您的订单已滞留，请到已完成-当日滞留查看";
    			}
    			
    			if(!StringUtils.isBlank(d_time)){
    				if(StringUtils.isBlank(fail_code)){//防止没查到原因
    					fail_code = "12";
        				message="修改收货时间为-"+d_time;
    				}
    				reasonmsg=reasonmsg+"  "+"修改收货时间为-"+d_time;
        		}
        		if(!StringUtils.isBlank(d_address)){
        			if(StringUtils.isBlank(fail_code)){//防止没查到地址
        				fail_code = "13";
            			message="修改收货地址为-"+d_address;
    				}
        			reasonmsg=reasonmsg+"  "+"修改收货地址为-"+d_address;
        		}
    		}
    		
    		postdelivery.setRemark(reasonmsg);
			postdelivery.setResultmsg(reasonmsg);
    		postdelivery.setDateUpd(new Date());
    		ExpressService.savePostdelivery(postdelivery);
			if (DeliverStas.SUCCESS.getStatus() == state && postmanuser.getCompanyid() != null && postmanuser.getCompanyid().intValue() == 4) {// 已签收 万博快递需要进行签收返现0.05元
				ExpressService.saveIncome(expressid);
			}
    		UserService.widgetPush(postmanuser.getId(),-1);
    		
    		//调用第三方落地配公司接口
    		String code=postdelivery.getCompany_code();
   
    		if(DeliverStas.StopInSta.getStatus()==state && ("HNJH".equals(code) ||"EXPLINK".equals(code)) ){//状态为退单到站点，且用的是易普则不再调用第三方
    			//
    		}else{
    	 		String outer_trade_no=postdelivery.getOut_trade_no();
        		String mail_num=postdelivery.getMail_num();
        		String postrace=StringUtil.genRandomCode(8);//流水号
        		String cod=String.valueOf(postdelivery.getGoods_fee());//收款金额
        		String unid=postdelivery.getStaffid();
        		String time_stamp=Dates.formatDateTime_New(new Date());
        		
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
        		getmap.put("unid", unid);
        		getmap.put("d_address", d_address);
        		getmap.put("d_time", d_time);
        		getmap.put("time_stamp", time_stamp);
        		Openuser openUser = OpenService.getOpenuserByCode(code);
        		OrderResu orderResu = new OrderResu(getmap,openUser);
    			(new Thread(orderResu)).start();//调用第三方接口
    		}
    		
			
    	}else{
    		result.put("status", "0");
    		result.put("msg", "运单不存在");
    	}
    	result.put("status", "1");
		result.put("msg", msg);
		result.put("expressid", ""+expressid);
		result.put("state", state);
        return ok(Json.toJson(result));
    }
    
    /**
     * 快递滞留原因列表接口(GET)express_reason_list
     * @return
     */
	@PostmanAuthenticated
    public Result express_reason_list() {
    	response().setContentType("application/json;charset=utf-8");
        ObjectNode result = Json.newObject();
        List<ExpressReasonInfoVO> returnReasonList = Lists.newArrayList();
        int expressid = Numbers.parseInt(request().getQueryString("expressid"), 6);
        String state = request().getQueryString("state");
    	
    	Postdelivery postdelivery = ExpressService.getPostdeliveryByid(expressid);//根据派送单I号D获取派送单
    	if(postdelivery!=null){
    		String company_code = postdelivery.getCompany_code();
    		List<DeleveryErrorMessage> deleveryErrorMessageList =  ExpressService.getDeleveryErrorMessageList(company_code,state);
    		for(DeleveryErrorMessage deleveryErrorMessage :deleveryErrorMessageList){
    			ExpressReasonInfoVO reason = new ExpressReasonInfoVO();
    			reason.id=String.valueOf(deleveryErrorMessage.getId());
    			reason.description=deleveryErrorMessage.getMessage();
    	    	returnReasonList.add(reason);
    		}
    	}
    	result.put("status", "1");
    	result.put("msg", "");
    	if (state.equals("3")){
    		List<String> datetimelist = Lists.newArrayList();
    		List<Date> dateList = Dates.findDates(new Date(), 7);
    		for(Date temp:dateList){
    			datetimelist.add(Dates.formatDate2(temp));
    		}
    		result.put("isshowmodify", "1");	
    		result.set("datetimelist", Json.toJson(datetimelist));	
    	}
    	result.set("reasonlist", Json.toJson(returnReasonList));
        return ok(Json.toJson(result));
    }
    
    /**
     * 我的当日派件结算接口(GET)user_settlement
     * @return
     */
	@PostmanAuthenticated
    public Result user_settlement(){
    	response().setContentType("application/json;charset=utf-8");
        ObjectNode result = Json.newObject();
    	List<ObjectNode> itemsList = Lists.newArrayList();
    	String tim=CHINESE_DATE_TIME_FORMAT_NORMAL_DAY.format(new Date());
    	PostmanUser postmanuser = getCurrentPostmanUser(request());
    	itemsList=ExpressService.getPayModeDataByTim(postmanuser.getId(),tim);
    	List<ObjectNode> companyList = Lists.newArrayList();
    	companyList=ExpressService.getCompanyDataByTim(postmanuser.getId(),tim);
    	result.put("status", "1");
    	result.put("msg", "");
    	result=ExpressService.getTotalDataByTim(result,postmanuser.getId(),tim);
    	result.set("receivinglist", Json.toJson(itemsList));
    	result.set("companylist", Json.toJson(companyList));
        return ok(Json.toJson(result));
    }
    
    /**
     * 快递状态接口(GET)express_processed
     * @return
     */
	@PostmanAuthenticated
    public Result express_processed() {
    	response().setContentType("application/json;charset=utf-8");
        ObjectNode result = Json.newObject();
    
    	List<ObjectNode> itemsList = Lists.newArrayList();
    	String tim=CHINESE_DATE_TIME_FORMAT_NORMAL_DAY.format(new Date());
    	PostmanUser postmanuser = getCurrentPostmanUser(request());
    	itemsList=ExpressService.getStaDataByTim(postmanuser.getId(), tim);
    	
    	result.put("status", "1");
    	result.put("msg", "");
    	result.put("toast",  "已完成、拒收订单保留7天，请尽快妥善处理遗留订单");
    	result.set("processeddata", Json.toJson(itemsList));
        return ok(Json.toJson(result));
    }

	/**
	 * 调用第三方接口
	 * @author luo
	 *
	 */
	class OrderResu extends Thread {
		Map<Object, Object> getmap;
		Openuser openUser;

		public OrderResu(Map<Object, Object> getmap, Openuser openUser) {
			this.getmap = getmap;
			this.openUser = openUser;
		}

		public void run() {
			if (openUser != null) {
				String sign = StringUtil.makeOpenSig(getmap,
						openUser.getToken());// 生成sign
				getmap.put("sign", sign);
				getmap.put("code", openUser.getCode());
				String url = openUser.getDevUrl();
				if (Play.application().configuration().getBoolean("production", false)) {
					url = openUser.getProdUrl();
				}
				Logger.info("向第三方请求的数据：" + Json.toJson(getmap) + "向第三方请求的url："+ url);
				int i = 0;
				while (i < 3) {
					JsonNode jsonResult = WSUtils.postByJSON(url,Json.toJson(getmap));
					Logger.info(jsonResult+ "====the data from result======= and the times is:"+ i);
					if (jsonResult != null) {
						if (jsonResult.get("result") != null && "ok".equals(jsonResult.get("result")
										.asText())) {// 成功，退出线程
							i = 3;
							break;
						} else {
							try {
								Thread.sleep(60000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							i++;
						}
					}
				}

			}
		}
	}
}

