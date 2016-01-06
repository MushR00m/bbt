package actor;

import play.Logger;
import play.Logger.ALogger;
import play.libs.Akka;
import utils.Constants;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

import com.avaje.ebean.CallableSql;
import com.avaje.ebean.Ebean;

/**
 * 循环执行的定时
 * 
 * @author luobotao
 * @Date 2015年9月16日
 */
public class ScheduleActor extends UntypedActor {
	private final ALogger logger = Logger.of(ScheduleActor.class);
	public static ActorRef myActor = Akka.system().actorOf(Props.create(ScheduleActor.class));

	@Override
	public void onReceive(Object message) throws Exception {
		if ("ACT".equals(message)) {
			doAct();
		}
	}

	private void doAct() {
		String sql="call `sp_postcontent_auto`() ";
		CallableSql cs = Ebean.getServer(Constants.getDB()).createCallableSql(sql);
		Ebean.getServer(Constants.getDB()).execute(cs);
	}
}
