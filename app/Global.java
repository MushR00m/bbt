import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Seconds;

import actor.PushActor;
import actor.ScheduleActor;
import actor.ZeroActor;
import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.Play;
import play.data.Form;
import play.libs.Akka;
import play.libs.F;
import play.libs.F.Promise;
import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.Http.Request;
import play.mvc.Result;
import utils.AjaxHelper;
import scala.concurrent.duration.Duration;

public class Global extends GlobalSettings {
	private static Logger.ALogger logger = Logger.of(Global.class);

	@Override
	public void onStart(Application paramApplication) {
		super.onStart(paramApplication);
		boolean doactor = Play.application().configuration().getBoolean("doactor", false);
		if (doactor) {
			logger.info("start to doactor====================");
			Akka.system().scheduler().schedule(Duration.create(0, TimeUnit.MILLISECONDS), 
					Duration.create(1, TimeUnit.MINUTES), 
					PushActor.myActor, "ACT", Akka.system().dispatcher(), null);//push
			Akka.system().scheduler().schedule(Duration.create(nextExecutionInSeconds(0, 5), TimeUnit.SECONDS),
					Duration.create(24, TimeUnit.HOURS), ZeroActor.myActor, "ACT", Akka.system().dispatcher(), null);//每天 
			Akka.system().scheduler().schedule(Duration.create(0, TimeUnit.MILLISECONDS), 
					Duration.create(15, TimeUnit.MINUTES), 
					ScheduleActor.myActor, "ACT", Akka.system().dispatcher(), null);//每15分钟
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Action onRequest(Request request, Method actionMethod) {
		logger.info("request {} and request method is {}", request.uri(), actionMethod);
		if (request.uri().indexOf("/tokenError") > -1 || request.uri().indexOf("/api/user_verifycode") > -1
				|| request.uri().indexOf("/api/user_check_verifycode") > -1
				|| request.uri().indexOf("/api/app_version") > -1 || request.uri().indexOf("/api/user_login") > -1
				|| request.uri().indexOf("/api/user_register") > -1 || request.uri().indexOf("/api/user_company") > -1
				|| request.uri().indexOf("/widget/bbt_widget_info") > -1
				|| request.uri().indexOf("/widget/bbt_widget") > -1 || request.uri().indexOf("/open/") > -1
				|| request.uri().indexOf("/widget/app_buriedpoint") > -1 || request.uri().indexOf("/api/dev_login") > -1
				|| request.uri().indexOf("/api/app_buriedpoint") > -1 || request.uri().indexOf("/authapp/login") > -1
				|| request.uri().indexOf("/authapp/send_verify_code") > -1
				|| request.uri().indexOf("/authapp/get_company_list") > -1 || request.uri().indexOf("/authapp/reg") > -1
				|| request.uri().indexOf("/authapp/get_last") > -1|| request.uri().indexOf("/api/user_image_upload") > -1
				|| request.uri().indexOf("/authapp/unbind_device") > -1 || request.uri().indexOf("/authapp/get") > -1) {

			return super.onRequest(request, actionMethod);
		}
		String token = request.getQueryString("token");
		if ("POST".equals(request.method())) {
			token = AjaxHelper.getHttpParamOfFormUrlEncoded(request, "token");
		}
		if (StringUtils.isBlank(token)) {
			return new Action.Simple() {
				public Promise<Result> call(Context paramContext) throws Throwable {
					return F.Promise.pure(Action.redirect("/tokenError"));
				}
			};
		} else {
			return super.onRequest(request, actionMethod);
		}
	}

	/*
	 * @Override public Promise<Result> onError(RequestHeader
	 * paramRequestHeader, Throwable paramThrowable) {
	 * System.out.println(paramThrowable+""); return
	 * F.Promise.pure(Action.redirect("/mall/error?url="+paramRequestHeader.path
	 * ())); }
	 * 
	 * @Override public Promise<Result> onHandlerNotFound(RequestHeader
	 * paramRequestHeader) { return
	 * F.Promise.pure(Action.redirect("/mall/error?url="+paramRequestHeader.path
	 * ())); }
	 * 
	 * @Override public Promise<Result> onBadRequest(RequestHeader
	 * paramRequestHeader, String paramString) { return
	 * F.Promise.pure(Action.redirect("/mall/error?url="+paramRequestHeader.path
	 * ())); }
	 */
	public static int nextExecutionInSeconds(int hour, int minute) {
		return Seconds.secondsBetween(new DateTime(), nextExecution(hour, minute)).getSeconds();
	}

	public static DateTime nextExecution(int hour, int minute) {
		DateTime next = new DateTime().withHourOfDay(hour).withMinuteOfHour(minute).withSecondOfMinute(0)
				.withMillisOfSecond(0);

		return (next.isBeforeNow()) ? next.plusHours(24) : next;
	}
}
