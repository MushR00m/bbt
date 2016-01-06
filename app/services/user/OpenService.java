package services.user;

import java.util.List;

import models.Openuser;
import models.Postdelivery;
import models.Postdelivery_goods;
import play.Logger;
import utils.Constants;

import com.avaje.ebean.CallableSql;
import com.avaje.ebean.Ebean;
public class OpenService {
	private static final Logger.ALogger logger = Logger.of(OpenService.class);


	
	
	
	
	/**
	 * 根据第三方公司code获取第三方公司信息
	 * @param code
	 * @return
	 */
	public static Openuser getOpenuserByCode(String code){
		List<Openuser> openuserList = Ebean.getServer(Constants.getDB()).find(Openuser.class).where().eq("code", code).findList();
		if(openuserList.isEmpty()){
			return null;
		}else{
			return openuserList.get(0);
		}
	}
	/**
	 * 根据运单号查询是否存在此配送订单信息
	 * @param code
	 * @return
	 */
	public static Postdelivery getPostdeliveryByMailNum(String mail_num){
		List<Postdelivery> postdeliveryList = Ebean.getServer(Constants.getDB()).find(Postdelivery.class).where().eq("mail_num", mail_num).findList();
		if(postdeliveryList.isEmpty()){
			return null;
		}else{
			return postdeliveryList.get(0);
		}
	}
	/**
	 * 根据公司编码与公司订单号查询是否存在此配送订单信息
	 * @param code
	 * @return
	 */
	public static Postdelivery getPostdeliveryByMail_numAndComanyCode(String delivery_company_code,String mail_num){
		List<Postdelivery> postdeliveryList = Ebean.getServer(Constants.getDB()).find(Postdelivery.class).where().eq("company_code", delivery_company_code).eq("mail_num", mail_num).findList();
		if(postdeliveryList.isEmpty()){
			return null;
		}else{
			return postdeliveryList.get(0);
		}
	}
	/**
	 * 保存快递配送
	 * @param postdelivery
	 * @return
	 */
	public static Postdelivery savePostdelivery(Postdelivery postdelivery){
		Ebean.getServer(Constants.getDB()).save(postdelivery);
		return postdelivery;
	}
	
	public static void savePostdelivery_goods(Object obj){
		Ebean.getServer(Constants.getDB()).save(obj);
	}
	
	public static void deletePostdelivery_goodsByDid(int did){
		Ebean.getServer(Constants.getDB()).createUpdate(Postdelivery_goods.class, "DELETE from Postdelivery_goods WHERE did=:did").setParameter("did", did).execute();
	}
	public static void pushWidgetpush(Integer id) {
		String sql="CALL `sp_postman_widgetpush`(?,?)";
		CallableSql cs = Ebean.getServer(Constants.getDB()).createCallableSql(sql).setParameter(1, id)
				.setParameter(2, -1);
		Ebean.getServer(Constants.getDB()).execute(cs);
	}
	
}