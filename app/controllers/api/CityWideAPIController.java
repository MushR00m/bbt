package controllers.api;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import models.OtherApp;
import models.Versioninfo;
import org.apache.commons.lang3.StringUtils;

import models.DeleveryErrorMessage;
import models.citywide.order.PostOrder;
import models.citywide.order.PostOrderUser;
import models.citywide.user.UserInfo;
import models.postman.PostmanUser;
import play.Configuration;
import play.Logger;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import services.ServiceFactory;
import services.baidu.BaiduService;
import services.cache.ICacheService;
import services.citywide.CitywideService;
import services.express.ExpressService;
import services.user.UserService;
import utils.*;
import utils.Constants.PayModeStas;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;

import controllers.BaseController;

/**
 * 同城配送相关的API
 * @author luobotao
 * @Date 2015年11月10日
 */
public class CityWideAPIController extends BaseController {
	SimpleDateFormat CHINESE_DATE_TIME_FORMAT_NORMAL = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	SimpleDateFormat CHINESE_DATE_TIME_FORMAT_NORMAL_DAY = new SimpleDateFormat("yyyy-MM-dd");
	ICacheService cache = ServiceFactory.getCacheService();
	private static final Logger.ALogger logger = Logger.of(CityWideAPIController.class);


    /*
    * 新版首页(get)
    * */
	@PostmanAuthenticated
	public Result homepage_tab() {
		response().setContentType("application/json;charset=utf-8");
		PostmanUser postmanuser = getCurrentPostmanUser(request());
		ObjectNode result = Json.newObject();
		result=CitywideService.getHomePageInfo(result, postmanuser.id);
		return ok(Json.toJson(result));
	}

    /**
     * 订单列表接口(GET)order_list
     * @return
     */
	@PostmanAuthenticated
    public Result order_list() {
    	response().setContentType("application/json;charset=utf-8");
        String state = request().getQueryString("state");//快递状态（1：待接单，2：待揽收，3：待配送，4：已完成（已送达），5：已完成（有问题）
        String latitude = request().getQueryString("latitude");//快递员纬度
        String lontitude = request().getQueryString("lontitude");//快递员经度
        int lastindex = Numbers.parseInt(request().getQueryString("lastindex"),0);
		ObjectNode result = Json.newObject();
		PostmanUser postmanuser = getCurrentPostmanUser(request());
        Double postmanX = 0.0D;
        Double postmanY = 0.0D;


        if(StringUtils.isBlank(lontitude)||StringUtils.isBlank(lontitude)){
        	postmanX = postmanuser.getLat();
        	postmanY = postmanuser.getLon();
        }else{
        	postmanX = Numbers.parseDouble(latitude, 0.0D);
        	postmanY = Numbers.parseDouble(lontitude, 0.0D);
        }
    	result.put("status", "1");
		result.put("msg", "");
		result=CitywideService.getOrderList(result,postmanuser.getId(),state,lastindex,postmanX,postmanY);
        return ok(Json.toJson(result));
    }

	/**
	 * 今日接单信息(GET)
	 */
	@PostmanAuthenticated
	public Result order_total_info() {
		response().setContentType("application/json;charset=utf-8");
		PostmanUser postmanuser = getCurrentPostmanUser(request());
		ObjectNode result = Json.newObject();
		result.put("status", "1");
		result.put("msg", "");
		result = CitywideService.getOrderTotalInfo(result,postmanuser.getId());
		return ok(Json.toJson(result));
	}
    /**
     * 订单详情(get)_order_detail
     * @return
     */
	@PostmanAuthenticated
	public Result order_detail() {
		response().setContentType("application/json;charset=utf-8");
		int id=Numbers.parseInt(request().getQueryString("id"), 0);//订单ID
		String type=request().getQueryString("type");
		String latitude = request().getQueryString("latitude");//快递员纬度
        String lontitude = request().getQueryString("lontitude");//快递员经度
		PostmanUser postmanuser = getCurrentPostmanUser(request());
		Double postmanX = 0.0D;
        Double postmanY = 0.0D;
        if(StringUtils.isBlank(lontitude)||StringUtils.isBlank(lontitude)){
        	postmanX = postmanuser.getLat();
        	postmanY = postmanuser.getLon();
        }else{
        	postmanX = Numbers.parseDouble(latitude, 0.0D);
        	postmanY = Numbers.parseDouble(lontitude, 0.0D);
        }
      //查两点间距离
  		Map<String,Double> begin=new HashMap<String,Double>();
  		Map<String,Double> end=new HashMap<String,Double>();
  		begin.put("x", postmanX);
  		begin.put("y", postmanY);
		PostOrder postOrder = CitywideService.findPostOrderByOrderId(id);
		ObjectNode result = Json.newObject();
		if(postOrder==null){
			result.put("status", ErrorCode.getErrorCode("global.cityWideNotExitError"));
			result.put("msg", ErrorCode.getErrorMsg("global.cityWideNotExitError"));
			return ok(Json.toJson(result));
		}
		result.put("status", "1");
		result.put("msg", "");
		result.put("state", postOrder.getStatus());
		result.put("type", type);
		
		result.put("orderid", postOrder.getId());
		result.put("ordercode",postOrder.getOrdercode());
		result.put("sendaddress", postOrder.getAddress());
		result.put("sendphone", postOrder.getPhone());
			end.put("x", postOrder.getUserlat());
			end.put("y", postOrder.getUserlong());
		result.put("senddistance", Numbers.doubleWithOne(StringUtil.Distance(postmanX,postmanY,postOrder.getUserlat(), postOrder.getUserlong()),1000,1)+"千米");

		result.put("receivphone", postOrder.getReceivephone());
		result.put("receiveaddress", postOrder.getReceiveaddress());
			end.put("x", postOrder.getReceivelat());
			end.put("y", postOrder.getReceivelong());
		result.put("receivedistance", Numbers.doubleWithOne(StringUtil.Distance(postmanX,postmanY,postOrder.getReceivelat(), postOrder.getReceivelong()),1000,1)+"千米");
		result.put("income", postOrder.getAward());//揽件费
		result.put("ordertime", Dates.formatDateTimeNew(postOrder.getGettime()));
		
		if("0".equals(type)){//商户
			result.put("remark", postOrder.getRemark());
			result.put("freight", postOrder.getFreight());
			result.put("platformreward", "平台奖励");
			result.put("payable",  Numbers.doubleWithOne(Double.valueOf(postOrder.getTotalfee()),100,2)+"元");
			if(postOrder.getRealgettime()==null){
				result.put("expectationdate", "");
			}else{
				result.put("expectationdate", Dates.formatDateTimeNew(postOrder.getGettime()));
			}
			if(postOrder.getRealgettime()==null){
				result.put("realitydate", "");
			}else{
				result.put("realitydate", Dates.formatDateTimeNew(postOrder.getRealgettime()));
			}
			if(postOrder.getOvertime()==null){
				result.put("completedate", "");
			}else{
				result.put("completedate", Dates.formatDateTimeNew(postOrder.getOvertime()));
			}
		}else{
			result.put("paytype", PayModeStas.status2Message(postOrder.getPaytyp()));
			result.put("goodstype", postOrder.getGettyp());
			result.put("goodsweight", postOrder.getWeight());
			result.put("payable", Numbers.doubleWithOne(Double.valueOf(postOrder.getTotalfee()),100,2)+"元");
		}

		return ok(Json.toJson(result));
	}

	/**
	 * 订单问题原因(get)
	 * @return
	 */
	@PostmanAuthenticated
	public Result order_problem_list() {
		response().setContentType("application/json;charset=utf-8");

		ObjectNode result = Json.newObject();
		result.put("status", "1");
		result.put("msg", "");
		List<ObjectNode> reasonlist = Lists.newArrayList();
		//0落地配1同城
		List<DeleveryErrorMessage> deleveryErrorMessageList =  ExpressService.getDeleveryErrorMessageByType("1");
		for(DeleveryErrorMessage deleveryErrorMessage :deleveryErrorMessageList){
			ObjectNode reason1 = Json.newObject();
			reason1.put("description", deleveryErrorMessage.getMessage());
			reason1.put("id", deleveryErrorMessage.getId());
			reasonlist.add(reason1);
		}
		result.set("reasonlist", Json.toJson(reasonlist));
		return ok(Json.toJson(result));
	}

	/**
	 * 更新订单状态(post)
	 * @return
	 */
	@PostmanAuthenticated
	public Result update_order_status() {
		response().setContentType("application/json;charset=utf-8");
		int orderid = Numbers.parseInt(AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "orderid"), 0);//快递单号
		String state = AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "state");//新的订单状态
		ObjectNode result = Json.newObject();
		if(StringUtils.isBlank(state)){
			result.put("status", ErrorCode.getErrorCode("global.cityWideStatusError"));
			result.put("msg", ErrorCode.getErrorMsg("global.cityWideStatusError"));
			return ok(Json.toJson(result));
		}
		String reasonid = AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "orderid");//原因id(有问题的订单上传，没有不上传)
		PostOrder postOrder = CitywideService.findPostOrderByOrderId(orderid);
		if(postOrder!=null){
			// 同城派送状态1：待接单，2：待揽收，3：配送中，4：已完成（已送达），5：已完成（有问题）6用户取消
			if(postOrder.getStatus().intValue()==Constants.CityWideDeliverStas.WaitToGet.getStatus() && Numbers.parseInt(state, 1)==Constants.CityWideDeliverStas.WaitToGet.getStatus()){
				result.put("status", ErrorCode.getErrorCode("global.cityWideGetedError"));
				result.put("msg", ErrorCode.getErrorMsg("global.cityWideGetedError"));
				return ok(Json.toJson(result));
			}
			postOrder.setReasonid(Numbers.parseInt(reasonid, 0));
			postOrder.setStatus(Numbers.parseInt(state, 1));
			postOrder.setDate_upd(new Date());
			CitywideService.savePostOrder(postOrder);
			if(postOrder.getStatus().intValue()!=Constants.CityWideDeliverStas.WaitToCatch.getStatus()){//只要不是待接单状态，则清除临时表数据
				CitywideService.deletePostmanUserTempByOrderid(orderid);
			}
			
		}
		result.put("status", "1");
		result.put("msg", "");
		result.put("orderid", orderid);
		result.put("state", state);
		return ok(Json.toJson(result));
	}
	/**
	 * 上传用户头像(post) user_image_upload 
	 * @return
	 */
	@PostmanAuthenticated
	public Result user_image_upload() {
		response().setContentType("application/json;charset=utf-8");
		ObjectNode result = Json.newObject();
		PostmanUser postmanuser = getCurrentPostmanUser(request());
		MultipartFormData body = request().body().asMultipartFormData();
		if(body==null){
			result.put("status", ErrorCode.getErrorCode("global.uploadTypeError"));
			result.put("msg", ErrorCode.getErrorMsg("global.uploadTypeError"));
			return ok(Json.toJson(result));
		}
		FilePart headicon = body.getFile("headicon");//头像数据（媒体数据）
		if (headicon != null && headicon.getFile() != null) {
			String path=Configuration.root().getString("oss.upload.postmanhead.image", "upload/bbt/postmanhead/");//上传路径
			String BUCKET_NAME=Configuration.root().getString("oss.bucket.name.higouOMSProduct ", "higou-oms");
			String fileName = headicon.getFilename();
			File file = headicon.getFile();//获取到该文件
			int p = fileName.lastIndexOf('.');
			String type = fileName.substring(p, fileName.length()).toLowerCase();

			if (".jpg".equals(type)||".gif".equals(type)||".png".equals(type)||".jpeg".equals(type)||".bmp".equals(type)) {
				// 检查文件后缀格式
				String fileNameLast = UUID.randomUUID().toString().replaceAll("-", "")+type;//最终的文件名称
				String endfilestr = OSSUtils.uploadFile(file,path,fileNameLast, type,BUCKET_NAME);
				postmanuser.setHeadicon(endfilestr);
				UserService.savePostuserman(postmanuser);
				result.put("status", "1");
				result.put("msg", "");
				ObjectNode userinfo = Json.newObject();
				userinfo.put("staffid", postmanuser.getStaffid());
				userinfo.put("nickname", postmanuser.getNickname());
				userinfo.put("phone", postmanuser.getPhone());
				userinfo.put("headicon", Configuration.root().getString("oss.image.url", "http://omspic.higegou.com")+postmanuser.getHeadicon());
				userinfo.put("companyname", postmanuser.getCompanyname());
				userinfo.put("stationname", postmanuser.getSubstation());
				userinfo.put("token", postmanuser.getToken());
				result.set("userinfo", Json.toJson(userinfo));
			}else{
				result.put("status", ErrorCode.getErrorCode("global.uploadFileError"));
				result.put("msg", ErrorCode.getErrorMsg("global.uploadFileError"));
				return ok(Json.toJson(result));
			}
		}else{
			result.put("status", ErrorCode.getErrorCode("global.uploadFileError"));
			result.put("msg", ErrorCode.getErrorMsg("global.uploadFileError"));
			return ok(Json.toJson(result));
		}
		return ok(Json.toJson(result));
	}

	/**
	 * 消息列表(get)_user_message_list
	 * @return
	 */
	@PostmanAuthenticated
	public Result user_message_list() {
		response().setContentType("application/json;charset=utf-8");
		PostmanUser postmanuser = getCurrentPostmanUser(request());
		int lastindex = Numbers.parseInt(request().getQueryString("lastindex"), 0);
		ObjectNode result = Json.newObject();
		//DAILY(1, "日常任务"), AWARD(2, "黄金任务"), SIGNIN(3, "签到任务 "), SYSINFO(4, "系统消息"), NEW(5, "新闻");
		result.put("status", "1");
		result.put("msg", "");
		List<ObjectNode> cardlist = Lists.newArrayList();
		cardlist = CitywideService.getMessageListByUid(cardlist,postmanuser.getId(),4,lastindex,10);//4为系统消息
		result.set("cardlist", Json.toJson(cardlist));
		return ok(Json.toJson(result));
	}

	
	/**
    * 接单状态改变(post) order_receive_status
    * @return
    */
	@PostmanAuthenticated
	public Result order_receive_status() {
		response().setContentType("application/json;charset=utf-8");
		PostmanUser postmanuser = getCurrentPostmanUser(request());
		String state = AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "state");//
		postmanuser.setPoststatus(Integer.parseInt(state));
		UserService.savePostuserman(postmanuser);
		ObjectNode result = Json.newObject();
		result.put("status", "1");
		result.put("msg", "");
		return ok(Json.toJson(result));
	}
		
	/**
	 * 用户反馈(post) user_feedback (post)
	 * @return
	 */
	@PostmanAuthenticated
	public Result user_feedback() {
		response().setContentType("application/json;charset=utf-8");
		String feedback = AjaxHelper.getHttpParamOfFormUrlEncoded(request(), "feedback");//反馈内容
		PostmanUser postmanuser = getCurrentPostmanUser(request());
		ObjectNode result = Json.newObject();
		result.put("status", "1");
		result.put("msg", "");
		return ok(Json.toJson(result));
	}
	/**
	 * 应用下载(get) app_list
	 * @return
	 */
	@PostmanAuthenticated
	public Result app_list() {
		response().setContentType("application/json;charset=utf-8");
		ObjectNode result = Json.newObject();
		List<ObjectNode> applist = Lists.newArrayList();
		result.put("status", "1");
		result.put("msg", "");
		result.put("endflag", "1");
		List<OtherApp> otherAppList = CitywideService.getOtherAppList();

		for(OtherApp otherApp : otherAppList){
			ObjectNode obj = Json.newObject();
			obj.put("id",otherApp.getId());
			obj.put("icon",otherApp.getIcon());
			obj.put("title",otherApp.getTitle());
			obj.put("subtitle",otherApp.getSubtitle());
			obj.put("tips",otherApp.getTips());
			obj.put("linkurl",otherApp.getLinkurl());
			applist.add(obj);
			result.put("lastindex", otherApp.getId());
		}
		result.set("applist", Json.toJson(applist));
		return ok(Json.toJson(result));
	}
}

