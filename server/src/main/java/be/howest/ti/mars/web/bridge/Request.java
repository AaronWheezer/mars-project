package be.howest.ti.mars.web.bridge;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.validation.RequestParameters;
import io.vertx.ext.web.validation.ValidationHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * The Request class is responsible for translating information that is part of the
 * request into Java.
 *
 * For every piece of information that you need from the request, you should provide a method here.
 * You can find information in:
 * - the request path: params.pathParameter("some-param-name")
 * - the query-string: params.queryParameter("some-param-name")
 * Both return a `RequestParameter`, which can contain a string or an integer in our case.
 * The actual data can be retrieved using `getInteger()` or `getString()`, respectively.
 * You can check if it is an integer (or not) using `isNumber()`.
 *
 * Finally, some requests have a body. If present, the body will always be in the json format.
 * You can acces this body using: `params.body().getJsonObject()`.
 *
 * **TIP:** Make sure that al your methods have a unique name. For instance, there is a request
 * that consists of more than one "player name". You cannot use the method `getPlayerName()` for both,
 * you will need a second one with a different name.
 */
public class Request {

    public static final String SPEC_CONTAINER_SIZE = "size";
    public static final String SPEC_OWNER = "owner";
    public static final String SPEC_LOCATION = "location";
    public static final String SPEC_CONTAINER = "container";
    public static final String SPEC_PALLET = "pallet";

    private final RequestParameters params;

    public static Request from(RoutingContext ctx) {
        return new Request(ctx);
    }

    private Request(RoutingContext ctx) {
        this.params = ctx.get(ValidationHandler.REQUEST_CONTEXT_KEY);
    }

    public String getContainerSize(){
        return params.pathParameter(SPEC_CONTAINER_SIZE).getString();
    }

    public String getOwner() {return params.pathParameter(SPEC_OWNER).getString();}

    public String getLocation() {return params.pathParameter(SPEC_LOCATION).getString();}

    public int getContainerID() {
        return params.pathParameter(SPEC_CONTAINER).getInteger();
    }

    public int getPalletID(){
        return params.pathParameter(SPEC_PALLET).getInteger();
    }

    public Map<String, Double> getCoordinates(){
        JsonObject jsonObject = params.body().getJsonObject();
        HashMap<String, Double> map = new HashMap<>();
        map.put("longitude", jsonObject.getDouble("longitude"));
        map.put("latitude", jsonObject.getDouble("latitude"));
        return map;
    }

    public Map<String, Integer> getContent() {
        //get the body of the request
        JsonObject jsonObject = params.body().getJsonObject();
        HashMap<String, Integer> map = new HashMap<>();
        map.put(jsonObject.getString("content"), jsonObject.getInteger("amount"));
        return map;
    }

    public String getCommentFromJson(){
        JsonObject jsonObject = params.body().getJsonObject();
        return jsonObject.getString("comment");
    }
}
