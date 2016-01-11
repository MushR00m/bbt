package services.baidu;

import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

import play.Logger;
import play.libs.Json;
import utils.Constants;
import utils.StringUtil;
import utils.WSUtils;

import com.fasterxml.jackson.databind.JsonNode;

public class BaiduService{
	public static BaiduService instance=new BaiduService();
	private BaiduService(){}
	
	//计算两点间距离（百度经纬度查询)
	public Long getdistanceByPoints(Map<String,Double> begin,Map<String,Double> end,String region){
		String origin=begin.get("x")+","+begin.get("y");
		String destination=end.get("x")+","+end.get("y");
		String timestamp=StringUtil.getTimeStamp();
		String bdurl="http://api.map.baidu.com/direction/v1";
		Map<String,String> map=new LinkedHashMap<String,String>();
		map.put("ak", Constants.BAIDU_AK);
		map.put("origin",origin);
		map.put("destination", destination);
		map.put("region", region);
		map.put("output", "json");
		map.put("timestamp", timestamp);
		Long distinct=0L;
		try{
			String tmp=StringUtil.toQueryString(map);
			String sn=StringUtil.BaiduMD5(URLEncoder.encode("/direction/v1?"+tmp+Constants.BAIDU_SK));
			bdurl=bdurl+"?"+tmp+"&sn="+sn;
			String resContent=StringUtil.doHttpGet(bdurl);
			JsonNode jsn=Json.parse(resContent);
			String status=jsn.get("status").asText();
			if(status.equals("0")){
				JsonNode jst=jsn.get("result").get("routes");
				if(jst!=null){
					distinct=jst.findValue("distance").asLong();
					Logger.info("distance:"+distinct);
				}
			}
		//	Logger.info("百度查询两点间距离返回："+resContent);
		}catch(Exception e){}
		return distinct;
	}
	
}