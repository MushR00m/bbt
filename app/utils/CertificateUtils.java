package utils;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * 姓名与身份证是否一致验证Util
 * 
 * @author luobotao
 *
 */
public class CertificateUtils {
	private static String APPKEY = "db6fb1f5ae343974296cece35e047218";
	private static String Url = "http://api.id98.cn/api/idcard";
	public static JsonNode checkNameWithCard(String name, String card) {
		String url = Url+"?appkey="+APPKEY+"&name="+name+"&cardno="+card;
		return  WSUtils.getResponseAsJson(url);
	}

	
}