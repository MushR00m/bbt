package actor;

import org.joda.time.DateTime;
import org.joda.time.Seconds;

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
 * 整点执行的定时
 * 
 * @author luobotao
 * @Date 2015年9月16日
 */
public class ZeroActor extends UntypedActor {
	private final ALogger logger = Logger.of(ZeroActor.class);
	public static ActorRef myActor = Akka.system().actorOf(Props.create(ZeroActor.class));

	@Override
	public void onReceive(Object message) throws Exception {
		if ("ACT".equals(message)) {
			doAct();
		}
	}

	
	private void doAct() {
		String sql="call `sp_day_auto`() ";
		logger.info("start to act zeroActor and the time is"+new DateTime()+" and the sql is "+sql);
		CallableSql cs = Ebean.getServer(Constants.getDB()).createCallableSql(sql);
		Ebean.getServer(Constants.getDB()).execute(cs);
	}
	
	public static int nextExecutionInSeconds(int hour, int minute){
        return Seconds.secondsBetween(
                new DateTime(),
                nextExecution(hour, minute)
        ).getSeconds();
    }
	
	 public static DateTime nextExecution(int hour, int minute){
	        DateTime next = new DateTime()
	                .withHourOfDay(hour)
	                .withMinuteOfHour(minute)
	                .withSecondOfMinute(0)
	                .withMillisOfSecond(0);

	        return (next.isBeforeNow())
	                ? next.plusHours(24)
	                : next;
	    }
}
