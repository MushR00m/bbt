package vo.api;

import java.util.Date;

/**
 * 首页接口信息
 * 
 * @author luobotao
 * @Date 2015年11月10日
 */
public class HomePageVO {
	public String id;
	public String contentid;
	public Integer typ;
	public String typname;
	public String typicon;
	public String title;
	public String subtitle;
	public String content;
	public String linkurl;
	public Integer expectamount;
	public String amount;
	public String tips;
	public String dateremark;
	public String start_tim;
	public String end_tim;
	public Integer nsort;
	public String sta;//1进行中2已完成
	public String isnew;//1:新消息 0非新消息
	public Date date_new;
	public Date date_upd;
}
