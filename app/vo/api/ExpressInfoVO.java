package vo.api;

import java.util.List;

/**
 * 获取派件信息
 * 
 * @author luobotao
 * @Date 2015年11月10日
 */
public class ExpressInfoVO {
	public String expressid;
	public String state;
	public String receivername;
	public String receiverphone;
	public String receiveraddress;
	public String sendername;
	public String senderphone;
	public String expresstype;
	public String expresstypedesc;
	public String senderaddress;
	public String expressmoney;
	public String express_money;
	public String refusereason;
	public String servicephone;
	public String expresstime;
	public String linkurl;
	public String mailno;
	public String needpay;
	public String paymentstatus;
	public String expressfrom;
	public List<paytypeInfo> paytypes;
	

	public static class paytypeInfo{
		public String paytypeid;
	    public String paytypetitle;
}
}

