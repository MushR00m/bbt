package services;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.avaje.ebean.Ebean;

import models.SmsInfo;
import play.Logger;
import utils.Constants;

public class SmsService {
	private static final Logger.ALogger logger = Logger.of(SmsService.class);

	public static void saveVerify(String phone, String code, String type) {
		String args = code;
		String tpl_id = "";
		if (StringUtils.isBlank(type) || "1".equals(type)) {
			args = "#company#=棒棒糖&#code#=" + code;
			tpl_id = "1";
			type = "1";
		} else {
			type = "3";
		}
		saveSmsInfo(args, phone, tpl_id, type);
	}
	
	public static void saveSFBack(String phone, String type) {
		String args = "";
		String tpl_id = "";
		if (StringUtils.isBlank(type) || "1".equals(type)) {
			args = "#name#=棒棒糖";
			tpl_id = "1125699";
			type = "1";
		} else {
			type = "3";
		}
		saveSmsInfo(args, phone, tpl_id, type);
	}

	private static SmsInfo saveSmsInfo(String args, String phone, String tpl_id, String type) {
		logger.info("args is " + args + "; Phone is " + phone + ";tpl_id is " + tpl_id);
		SmsInfo smsInfo = new SmsInfo();
		smsInfo.setFlg("0");// 未发送
		smsInfo.setTplId(tpl_id);
		smsInfo.setPhone(phone);
		smsInfo.setArgs(args);
		smsInfo.setTyp(type);// 1普通短信 2营销短信 3语音短信
		smsInfo.setDateNew(new Date());
		smsInfo.setDateUpd(new Date());
		Ebean.getServer(Constants.getDB()).save(smsInfo);
		return smsInfo;

	}

}