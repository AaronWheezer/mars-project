package be.howest.ti.mars.web.bridge;

import be.howest.ti.mars.logic.domain.Container;
import be.howest.ti.mars.logic.domain.Pallet;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.Map;
import java.util.Set;

/**
 * The Response class is responsible for translating the result of the controller into
 * JSON responses with an appropriate HTTP code.
 */
public class Response {

    private Response() { }

    public static void sendOwnerInventory(RoutingContext ctx, Map<String, Object> ownerInventory) { sendOkJsonResponse(ctx, ownerInventory);}

    public static void sendLocationInventory(RoutingContext ctx, Map<String, Object> locationInventory) {sendOkJsonResponse(ctx, locationInventory);}

    public static void sendContainer(RoutingContext ctx, Container container) {
        sendOkJsonResponse(ctx, container);
    }

    public static void sendContainers(RoutingContext ctx, Set<Container> containers) {
        sendOkJsonResponse(ctx, containers);
    }
    public static void sendContainerCreated(RoutingContext ctx, Container container) {
        sendOkJsonResponse(ctx, container);
    }

    public static void sendPalletUniversalSize(RoutingContext ctx, Pallet pallet) {
        sendOkJsonResponse(ctx, pallet);
    }
    public static void sendPalletLocationChanged(RoutingContext ctx, Pallet pallet) {
        sendOkJsonResponse(ctx, pallet);
    }

    public static void sendContainerUpdated(RoutingContext ctx, Container container) {
        sendOkJsonResponse(ctx, container);
    }

    public static void sendContainersByOwner(RoutingContext ctx, Set<Container> containers) {
        sendOkJsonResponse(ctx, containers);
    }

    public static void sendContainersByLocation(RoutingContext ctx, Set<Container> containers) {
        sendOkJsonResponse(ctx, containers);
    }
    public static void sendPalletInfo(RoutingContext ctx, Pallet pallet) {
        sendOkJsonResponse(ctx, pallet);
    }

    public static void sendPallets(RoutingContext ctx, Set<Pallet> pallets) {sendOkJsonResponse(ctx, pallets);}

    public static void sendPalletsByOwner(RoutingContext ctx, Set<Pallet> pallets) {
        sendOkJsonResponse(ctx, pallets);
    }

    public static void sendPalletsByLocation(RoutingContext ctx, Set<Pallet> pallets) {sendOkJsonResponse(ctx, pallets);
    }

    public static void sendContainerContentAdded(RoutingContext ctx, Container container) {sendOkJsonResponse(ctx, container);
    }

    public static void sendContainerContentRemoved(RoutingContext ctx, Container container){sendOkJsonResponse(ctx, container);}

    public static void sendPalletContentAdded(RoutingContext ctx, Pallet pallet) {
        sendOkJsonResponse(ctx, pallet);
    }

    public static void sendPalletContentRemoved(RoutingContext ctx, Pallet pallet){sendOkJsonResponse(ctx, pallet);}

    public static void sendContainerOwnerChanged(RoutingContext ctx, Container container) {
        sendOkJsonResponse(ctx, container);
    }

    public static void sendPalletOwnerChanged(RoutingContext ctx, Pallet pallet) {
        sendOkJsonResponse(ctx, pallet);
    }

    public static void sendContainerDeleted(RoutingContext ctx, Container container) {
        sendOkJsonResponse(ctx, container);
    }

    public static void sendPalletDeleted(RoutingContext ctx, Pallet pallet) {
        sendOkJsonResponse(ctx, pallet);
    }
    public static void sendCommentAddedContainer(RoutingContext ctx){
        sendEmptyResponse(ctx, 202);
    }

    public static void sendCommentAddedPallet(RoutingContext ctx){
        sendEmptyResponse(ctx, 202);
    }

    public static void sendCommentDeletedContainer(RoutingContext ctx){
        sendEmptyResponse(ctx, 202);
    }

    public static void sendCommentDeletedPallet(RoutingContext ctx){
        sendEmptyResponse(ctx, 202);
    }

    private static void sendOkJsonResponse(RoutingContext ctx, Object response) {
        sendJsonResponse(ctx, 200, response);
    }

    private static void sendEmptyResponse(RoutingContext ctx, int statusCode) {
        ctx.response()
                .setStatusCode(statusCode)
                .end();
    }

    private static void sendJsonResponse(RoutingContext ctx, int statusCode, Object response) {
        ctx.response()
                .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .setStatusCode(statusCode)
                .end(Json.encodePrettily(response));
    }

    public static void sendFailure(RoutingContext ctx, int code, String quote) {
        sendJsonResponse(ctx, code, new JsonObject()
                .put("failure", code)
                .put("cause", quote));
    }
}
