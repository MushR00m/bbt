package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;

import play.*;
import play.libs.Json;
import play.mvc.*;
import utils.ErrorCode;
import views.html.*;

public class Application extends Controller {

    public Result index() {
        return ok(index.render("Your new application is ready."));
    }
    public Result tokenError() {
    	String status = ErrorCode.getErrorCode("global.tokenError");
		String msg = ErrorCode.getErrorMsg("global.tokenError");
		ObjectNode result = Json.newObject();
		result.put("status", status);
		result.put("msg", msg);
    	return ok(Json.toJson(result));
    }

}
