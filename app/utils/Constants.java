package utils;

import play.Play;

/**
 * @author luobotao
 * @Date 2015年10月21日
 */
public class Constants {
	/********百度地图帐号*******************************/
	public static final String BAIDU_AK="MI9t5CBdVKEiDsHhPx1jXysB";
	public static final String BAIDU_SK="SuLiG2G53vsAEsySH7stEYXZDfGvQuP6";
	/**
	 * 数据库名称
	 * 
	 * @return
	 */
	public static String getDB() {
		if (Play.application().configuration().getBoolean("production", false)) {
			return "product";
		} else {
			return "dev";
		}
	}

	public static final String cache_verifyCode = "bbt.verifyCode.";
	public static final String cache_postmanid = "bbt.postmanid.";// 根据token找ID使用
	public static final String cache_tokenBypostmanid = "bbt.token.";// 根据ID找token使用
	public static final String CERTIFICATION_TIME_CHECKED = "bbt.certification.times.";// 身份认证次数

	// 快递员状态
	public static enum PostmanStatus {
		NOCHECK(0, "未审核"), COMMON(1, "正常"), FAILED(2, "审核失败");

		private int status;
		private String message;

		private PostmanStatus(int status, String message) {
			this.message = message;
			this.status = status;
		}

		public int getStatus() {
			return status;
		}

		public String getMessage() {
			return message;
		}

		public static String stas2HTML(int value) {
			StringBuilder sb = new StringBuilder();
			PostmanStatus[] stas = PostmanStatus.values();
			sb.append(Htmls.generateOption(-1, "全部"));
			for (PostmanStatus s : stas) {
				if (value == s.status) {
					sb.append(Htmls.generateSelectedOption(s.status, s.message));
				} else {
					sb.append(Htmls.generateOption(s.status, s.message));
				}
			}
			return sb.toString();
		}

		public static String stas2Message(int status) {
			PostmanStatus[] stas = PostmanStatus.values();
			for (PostmanStatus s : stas) {
				if (status == s.status) {
					return s.getMessage();
				}
			}
			return "";
		}
	}

	// 任务类型
	public static enum PostcontentTyps {
		DAILY(1, "日常任务"), AWARD(2, "黄金任务"), SIGNIN(3, "签到任务 "), SYSINFO(4, "系统消息"), NEW(5, "新闻");

		private int status;
		private String message;

		private PostcontentTyps(int status, String message) {
			this.message = message;
			this.status = status;
		}

		public int getStatus() {
			return status;
		}

		public String getMessage() {
			return message;
		}

		public static String typs2HTML(int value) {
			StringBuilder sb = new StringBuilder();
			PostcontentTyps[] typs = PostcontentTyps.values();
			sb.append(Htmls.generateOption(-1, "全部"));
			for (PostcontentTyps s : typs) {
				if (value == s.status) {
					sb.append(Htmls.generateSelectedOption(s.status, s.message));
				} else {
					sb.append(Htmls.generateOption(s.status, s.message));
				}
			}
			return sb.toString();
		}

		public static String typs2Message(int status) {
			PostcontentTyps[] typs = PostcontentTyps.values();
			for (PostcontentTyps s : typs) {
				if (status == s.status) {
					return s.getMessage();
				}
			}
			return "";
		}
	}

	// 任务状态
	public static enum PostcontentStas {
		INVALID(0, "无效"), VALID(1, "有效");

		private int status;
		private String message;

		private PostcontentStas(int status, String message) {
			this.message = message;
			this.status = status;
		}

		public int getStatus() {
			return status;
		}

		public String getMessage() {
			return message;
		}

		public static String stas2HTML(int value) {
			StringBuilder sb = new StringBuilder();
			PostcontentStas[] stas = PostcontentStas.values();
			sb.append(Htmls.generateOption(-1, "全部"));
			for (PostcontentStas s : stas) {
				if (value == s.status) {
					sb.append(Htmls.generateSelectedOption(s.status, s.message));
				} else {
					sb.append(Htmls.generateOption(s.status, s.message));
				}
			}
			return sb.toString();
		}

		public static String stas2Message(int status) {
			PostcontentStas[] stas = PostcontentStas.values();
			for (PostcontentStas s : stas) {
				if (status == s.status) {
					return s.getMessage();
				}
			}
			return "";
		}
	}
	
	// 派送是否需要付款
	public static enum NeedPayStas {
		NEEDNO("0", "不需要"), NEEDYES("1", "需要"), Paied("2", "支付完成");
		
		private String status;
		private String message;
		
		private NeedPayStas(String status, String message) {
			this.message = message;
			this.status = status;
		}
		
		public String getStatus() {
			return status;
		}
		
		public String getMessage() {
			return message;
		}
	}
	//支付方式 （0：现金，1：刷卡，2：微信，3：支付宝，4：其它）
	public static enum PayModeStas {
		Cash("0", "现金支付"), POS("1", "刷卡"), Weixin("2", "微信"),Alipay("3","支付宝"),Other("4","其他");
		
		private String status;
		private String message;
		
		private PayModeStas(String status, String message) {
			this.message = message;
			this.status = status;
		}
		
		public String getStatus() {
			return status;
		}
		
		public String getMessage() {
			return message;
		}
		public static String status2Message(String status) {
			PayModeStas[] stas = PayModeStas.values();
			for (PayModeStas s : stas) {
				if (s.status.equals(status)) {
					return s.getMessage();
				}
			}
			return "";
		}
	}
	//向第三方提供的支付方式 1  现金 6  pos 机 7  支付宝 8  已付款
	public static enum OutApiPayModeStas {
		Cash("1", "现金支付"), POS("6", "pos机"), Alipay("7","支付宝"),Paid("8","已付款");
		
		private String status;
		private String message;
		
		private OutApiPayModeStas(String status, String message) {
			this.message = message;
			this.status = status;
		}
		
		public String getStatus() {
			return status;
		}
		
		public String getMessage() {
			return message;
		}
		
	}
	//向第三方提供的派送状态 1  现金 6  pos 机 7  支付宝 8  已付款
	public static enum OutDeliverStas {
		SUCCESS("1", "派送成功"), DENY("2", "收件人拒收"), Restore("6","恢复归班前状态"),RollBack("7","滞留");
		
		private String status;
		private String message;
		
		private OutDeliverStas(String status, String message) {
			this.message = message;
			this.status = status;
		}
		
		public String getStatus() {
			return status;
		}
		
		public String getMessage() {
			return message;
		}
	}
	// 派送状态快递状态（[0:全部，服务器查询逻辑],1：未完成，2：已签收，3：已滞留，4：已拒绝，5：已退单
	public static enum DeliverStas {
		WAIT(1, "未完成"), SUCCESS(2, "已签收"),RollBack(3,"已滞留"),DENY(4,"已拒绝"),StopInSta(5,"已退单")
		//异常件
		,OutOfRegion(11,"超出配送范围")
		,WeatherBad(12,"异常天气")
		,Transit(14,"货运中转延误")
		,DelayPost(15,"客户要求推迟时间")
		,ConnectNot(16,"联系不上送无人")
		,DeliverErro(17,"配送商分拣错误")
		,AddressModify(18,"客户更改配送地址")
		,Weekend(19,"客户要求周末送货")
		,Dailly(20,"客户要求工作日送货")
		,OurResponsibility(37,"我方责任服务事故")
		//拒收件
		,CancelOrder(25,"客户取消订单")
		,ConnectNotTow(28,"联系不上送无人")
		,NotTheOne(32,"与客户期望不符")
		,LimitPro(33,"商品少配件")
		,CancelPost(41,"取消配送")
		,Other(46,"其他拒收原因")
		,BillingWrong(50,"顾客反映发票有问题")
		,QualityWrong(51,"顾客反映质量有问题")
		,OrderWrong(52,"顾客重复订购或订购错误")
		,NoOrder(53,"顾客表示未曾订购")
		;

		private int status;
		private String message;

		private DeliverStas(int status, String message) {
			this.message = message;
			this.status = status;
		}

		public int getStatus() {
			return status;
		}

		public String getMessage() {
			return message;
		}
	}
	
	// 同城派送状态1：待接单，2：待揽收，3：待配送，4：已完成（已送达），5：已完成（有问题）
	public static enum CityWideDeliverStas {
		WaitToCatch(1, "待接单"), WaitToGet(2, "待揽收"), WaitToSend(3, "待配送"), Success(
				4, "已完成"), SuccessWithProblom(5, "已完成");
		private int status;
		private String message;

		private CityWideDeliverStas(int status, String message) {
			this.message = message;
			this.status = status;
		}

		public int getStatus() {
			return status;
		}

		public String getMessage() {
			return message;
		}
		public static String status2Message(int status) {
			CityWideDeliverStas[] stas = CityWideDeliverStas.values();
			for (CityWideDeliverStas s : stas) {
				if (status == s.status) {
					return s.getMessage();
				}
			}
			return "";
		}
	}
}
