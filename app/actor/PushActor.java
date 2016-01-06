package actor;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.avaje.ebean.Ebean;
import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.exceptions.RequestException;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.TransmissionTemplate;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import models.PushInfo;
import play.Logger;
import play.Logger.ALogger;
import play.libs.Akka;
import scala.concurrent.duration.Duration;
import utils.Constants;

/**
 * 个推定时
 * 
 * @author luobotao
 * @Date 2015年9月16日
 */
public class PushActor extends UntypedActor {
	private final ALogger logger = Logger.of(PushActor.class);
	private static String appId = "VARdujeTtQ8Qz6spdNBjQ1";
	private static String appkey = "cgVMkDLKJi5pUtPxc53at6";
	private static String master = "gMc9LqiYcTAh7l2vHhSSO6";
	private static String CID1 = "70fb1daa4f7491b80576f741efb7954c";
	private static String host = "http://sdk.open.api.igexin.com/serviceex";
	public static ActorRef myActor = Akka.system().actorOf(Props.create(PushActor.class));


	@Override
	public void onReceive(Object message) throws Exception {
		if ("ACT".equals(message)) {
			push();
		}
	}

	/**
	 * 查找棒棒糖返现表，进行返现操作
	 */
	private void push() {
		// 配置返回每个用户返回用户状态，可选
		System.setProperty("gexin.rp.sdk.pushlist.needDetails", "true");
		IGtPush push = new IGtPush(host, appkey, master);

		List<PushInfo> pushInfoList = Ebean.getServer(Constants.getDB()).find(PushInfo.class).where().eq("flg", "0")
				.findList();
		for (PushInfo pushInfo : pushInfoList) {
			logger.info(pushInfo.getTitle());
			Target target = new Target();
			target.setAppId(appId);
			target.setClientId(pushInfo.getPushtoken());

			TransmissionTemplate template = new TransmissionTemplate();
		    template.setAppId(appId);
		    template.setAppkey(appkey);
		    // 透传消息设置，1为强制启动应用，客户端接收到消息后就会立即启动应用；2为等待应用启动
		    template.setTransmissionType(2);
		    template.setTransmissionContent(pushInfo.getContent());
		    
			SingleMessage message = new SingleMessage();
			message.setOffline(true);
			// 离线有效时间，单位为毫秒，可选
			message.setOfflineExpireTime(24 * 3600 * 1000);
			message.setData(template);
			message.setPushNetWorkType(0); // 可选。判断是否客户端是否wifi环境下推送，1为在WIFI环境下，0为不限制网络环境。
			IPushResult ret = null;
			try {
				ret = push.pushMessageToSingle(message, target);
			} catch (RequestException e) {
				e.printStackTrace();
				ret = push.pushMessageToSingle(message, target, e.getRequestId());
			}
			if (ret != null) {
				String result = ret.getResponse().get("result")==null?"": ret.getResponse().get("result").toString();
				if("ok".equals(result)){
					pushInfo.setFlg("1");
					pushInfo.setDateUpd(new Date());
					Ebean.getServer(Constants.getDB()).save(pushInfo);
				}
				logger.info(ret.getResponse().toString());
			} else {
				logger.info("服务器响应异常");
			}
		}

	}
}
