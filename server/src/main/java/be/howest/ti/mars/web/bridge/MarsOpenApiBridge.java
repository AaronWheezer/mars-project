package be.howest.ti.mars.web.bridge;

import be.howest.ti.mars.logic.controller.DefaultMarsController;
import be.howest.ti.mars.logic.controller.MarsController;
import be.howest.ti.mars.logic.domain.Container;
import be.howest.ti.mars.logic.domain.Location;
import be.howest.ti.mars.logic.domain.Pallet;
import be.howest.ti.mars.web.exceptions.MalformedRequestException;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.openapi.RouterBuilder;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * In the MarsOpenApiBridge class you will create one handler-method per API operation.
 * The job of the "bridge" is to bridge between JSON (request and response) and Java (the controller).
 * <p>
 * For each API operation you should get the required data from the `Request` class.
 * The Request class will turn the HTTP request data into the desired Java types (int, String, Custom class,...)
 * This desired type is then passed to the controller.
 * The return value of the controller is turned to Json or another Web data type in the `Response` class.
 */
public class MarsOpenApiBridge {
    private static final Logger LOGGER = Logger.getLogger(MarsOpenApiBridge.class.getName());
    private final MarsController controller;

    public Router buildRouter(RouterBuilder routerBuilder) {


        routerBuilder.rootHandler(createCorsHandler());


        LOGGER.log(Level.INFO, "Installing Failure handlers");
        routerBuilder.operations().forEach(op -> op.failureHandler(this::onFailedRequest));


        LOGGER.log(Level.INFO, "Installing handler for: getInventoryOfOwner");
        routerBuilder.operation("getContentsByOwner").handler(this::getContentsByOwner);

        LOGGER.log(Level.INFO, "Installing handler for: getInventoryByLocation");
        routerBuilder.operation("getContentsByLocation").handler(this::getContentsByLocation);

        LOGGER.log(Level.INFO, "Installing handler for: getContainer");
        routerBuilder.operation("getContainer").handler(this::getContainer);

        LOGGER.log(Level.INFO, "Installing handler for: getContainers");
        routerBuilder.operation("getContainers").handler(this::getContainers);

        LOGGER.log(Level.INFO, "Installing handler for: getContainersByOwner");
        routerBuilder.operation("getContainersByOwner").handler(this::getContainersByOwner);

        LOGGER.log(Level.INFO, "Installing handler for: getContainersByLocation");
        routerBuilder.operation("getContainersByLocation").handler(this::getContainersByLocation);

        LOGGER.log(Level.INFO, "Installing handler for: getPallet");
        routerBuilder.operation("getPallet").handler(this::getPalletInfo);

        LOGGER.log(Level.INFO, "Installing handler for: getPallets");
        routerBuilder.operation("getPallets").handler(this::getPallets);

        LOGGER.log(Level.INFO, "Installing handler for: getPalletsByOwner");
        routerBuilder.operation("getPalletsByOwner").handler(this::getPalletsByOwner);

        LOGGER.log(Level.INFO, "Installing handler for: getPalletsByLocation");
        routerBuilder.operation("getPalletsByLocation").handler(this::getPalletsByLocation);

        LOGGER.log(Level.INFO, "Installing handler for: createContainer");
        routerBuilder.operation("createContainer").handler(this::createContainer);

        LOGGER.log(Level.INFO, "Installing handler for: createPalletUniversalSize");
        routerBuilder.operation("createUniversalPallet").handler(this::createPalletUniversalSize);

        LOGGER.log(Level.INFO, "Installing handler for: changeContainerLocation");
        routerBuilder.operation("changeContainerLocation").handler(this::changeContainerLocation);

        LOGGER.log(Level.INFO, "Installing handler for: changePalletLocation");
        routerBuilder.operation("changePalletLocation").handler(this::changePalletLocation);

        LOGGER.log(Level.INFO, "Installing handler for: deleteContainer");
        routerBuilder.operation("deleteContainer").handler(this::deleteContainer);

        LOGGER.log(Level.INFO, "Installing handler for: deletePallet");
        routerBuilder.operation("deletePallet").handler(this::deletePallet);

        LOGGER.log(Level.INFO, "Installing handler for: changeContainerOwner");
        routerBuilder.operation("changeOwnerContainer").handler(this::changeContainerOwner);

        LOGGER.log(Level.INFO, "Installing handler for: changePalletOwner");
        routerBuilder.operation("changeOwnerPallet").handler(this::changePalletOwner);

        LOGGER.log(Level.INFO, "Installing handler for: addContainerContent");
        routerBuilder.operation("addContainerContent").handler(this::addContainerContent);

        LOGGER.log(Level.INFO, "Installing handler for: removeContainerContent");
        routerBuilder.operation("removeContainerContent").handler(this::removeContainerContent);

        LOGGER.log(Level.INFO, "Installing handler for: addPalletContent");
        routerBuilder.operation("addPalletContent").handler(this::addPalletContent);

        LOGGER.log(Level.INFO, "Installing handler for: removePalletContent");
        routerBuilder.operation("removePalletContent").handler(this::removePalletContent);

        LOGGER.log(Level.INFO, "Installing handler for: addCommentContainer");
        routerBuilder.operation("addCommentContainer").handler(this::addCommentContainer);

        LOGGER.log(Level.INFO, "Installing handler for: addCommentPallet");
        routerBuilder.operation("addCommentPallet").handler(this::addCommentPallet);

        LOGGER.log(Level.INFO, "Installing handler for: removeCommentContainer");
        routerBuilder.operation("removeCommentContainer").handler(this::removeCommentContainer);

        LOGGER.log(Level.INFO, "Installing handler for: removeCommentPallet");
        routerBuilder.operation("removeCommentPallet").handler(this::removeCommentPallet);

        LOGGER.log(Level.INFO, "All handlers are installed, creating router.");
        return routerBuilder.createRouter();
    }


    public MarsOpenApiBridge() {
        this.controller = new DefaultMarsController();
    }

    public MarsOpenApiBridge(MarsController controller) {
        this.controller = controller;
    }


    public void getContentsByOwner(RoutingContext ctx) {
        String businessOwner = Request.from(ctx).getOwner();

        Map<String, Object> ownerInventory = controller.getContentsByOwner(businessOwner);

        Response.sendOwnerInventory(ctx, ownerInventory);
    }

    public void getContentsByLocation(RoutingContext ctx){
        String location = Request.from(ctx).getLocation();

        Map<String, Object> locationInventory = controller.getContentsByLocation(location);

        Response.sendLocationInventory(ctx, locationInventory);
    }

    public void getContainer(RoutingContext ctx) {
        int containerID = Request.from(ctx).getContainerID();

        Container container = controller.getContainer(containerID);

        Response.sendContainer(ctx, container);
    }

    public void getContainers(RoutingContext ctx) {
        Set<Container> containers = controller.getContainers();

        Response.sendContainers(ctx, containers);
    }

    public void getContainersByOwner(RoutingContext ctx) {
        String owner = Request.from(ctx).getOwner();

        Set<Container> containers = controller.getContainersByOwner(owner);

        Response.sendContainersByOwner(ctx, containers);
    }

    public void getContainersByLocation(RoutingContext ctx) {
        String location = Request.from(ctx).getLocation();

        Set<Container> containers = controller.getContainersByLocation(location);

        Response.sendContainersByLocation(ctx, containers);
    }

    public void createContainer(RoutingContext ctx) {
        String size = Request.from(ctx).getContainerSize();

        Container container = controller.createContainer(size.toLowerCase());

        Response.sendContainerCreated(ctx, container);
    }

    public void getPalletInfo(RoutingContext ctx) {
        int palletID = Request.from(ctx).getPalletID();

        Pallet pallet = controller.getPalletInfo(palletID);

        Response.sendPalletInfo(ctx, pallet);
    }
    public void getPallets(RoutingContext ctx){
        Set<Pallet> pallets = controller.getPallets();

        Response.sendPallets(ctx, pallets);
    }

    public void getPalletsByOwner(RoutingContext ctx){
        String owner = Request.from(ctx).getOwner();

        Set<Pallet> pallets = controller.getPalletsByOwner(owner);

        Response.sendPalletsByOwner(ctx, pallets);
    }

    public void getPalletsByLocation(RoutingContext ctx){
        String location = Request.from(ctx).getLocation();
        Set<Pallet> pallets = controller.getPalletsByLocation(location);
        Response.sendPalletsByLocation(ctx, pallets);
    }
    public void createPalletUniversalSize(RoutingContext ctx) {
        Pallet palletUniversalSize = controller.createPalletUniversalSize();
        Response.sendPalletUniversalSize(ctx, palletUniversalSize);
    }
    public void deleteContainer(RoutingContext ctx) {
        int containerID = Request.from(ctx).getContainerID();

        Container container = controller.deleteContainer(containerID);

        Response.sendContainerDeleted(ctx, container);
    }

    public void deletePallet(RoutingContext ctx) {
        int palletID = Request.from(ctx).getPalletID();

        Pallet pallet = controller.deletePallet(palletID);

        Response.sendPalletDeleted(ctx, pallet);
    }

    public void changeContainerLocation(RoutingContext ctx) {
        int id = Request.from(ctx).getContainerID();
        String locationName = Request.from(ctx).getLocation();
        Map<String, Double> coordinates = Request.from(ctx).getCoordinates();
        Location location = new Location(locationName, coordinates.get("longitude"), coordinates.get("latitude"));

        Container container = controller.changeContainerLocation(id, location);

        Response.sendContainerUpdated(ctx, container);
    }

    public void changePalletLocation(RoutingContext ctx){
        int palletID = Request.from(ctx).getPalletID();
        String locationName = Request.from(ctx).getLocation();
        Map<String, Double> coordinates = Request.from(ctx).getCoordinates();
        Location location = new Location(locationName, coordinates.get("longitude"), coordinates.get("latitude"));

        Pallet pallet = controller.changePalletLocation(palletID, location);
        Response.sendPalletLocationChanged(ctx, pallet);
    }

    public void changeContainerOwner(RoutingContext ctx){
        int containerID = Request.from(ctx).getContainerID();
        String newOwner = Request.from(ctx).getOwner();
        Container container = controller.changeContainerOwner(containerID, newOwner);
        Response.sendContainerOwnerChanged(ctx, container);
    }

    public void changePalletOwner(RoutingContext ctx){
        int palletID = Request.from(ctx).getPalletID();
        String newOwner = Request.from(ctx).getOwner();
        Pallet pallet = controller.changePalletOwner(palletID, newOwner);
        Response.sendPalletOwnerChanged(ctx, pallet);
    }

    public void addContainerContent(RoutingContext ctx){
        int containerID = Request.from(ctx).getContainerID();
        Map<String,Integer> content = Request.from(ctx).getContent();

        Container container = controller.addContainerContent(containerID, content);

        Response.sendContainerContentAdded(ctx, container);
    }

    public void addPalletContent(RoutingContext ctx) {
        int palletID = Request.from(ctx).getPalletID();

        Map<String,Integer> content = Request.from(ctx).getContent();

        Pallet pallet = controller.addPalletContent(palletID, content);

        Response.sendPalletContentAdded(ctx, pallet);
    }

    public void removeContainerContent(RoutingContext ctx){
        int containerID = Request.from(ctx).getContainerID();
        Map<String,Integer> content = Request.from(ctx).getContent();

        Container container = controller.removeContainerContent(containerID, content);

        Response.sendContainerContentRemoved(ctx, container);
    }

    public void removePalletContent(RoutingContext ctx){
        int palletID = Request.from(ctx).getPalletID();
        Map<String,Integer> content = Request.from(ctx).getContent();

        Pallet pallet = controller.removePalletContent(palletID, content);

        Response.sendPalletContentRemoved(ctx, pallet);
    }

    private void addCommentContainer(RoutingContext ctx) {
        int containerID = Request.from(ctx).getContainerID();
        String comment = Request.from(ctx).getCommentFromJson();

        controller.addContainerComment(containerID, comment);

        Response.sendCommentAddedContainer(ctx);
    }

    private void addCommentPallet(RoutingContext ctx) {
        int palletID = Request.from(ctx).getPalletID();
        String comment = Request.from(ctx).getCommentFromJson();

        controller.addPalletComment(palletID, comment);

        Response.sendCommentAddedPallet(ctx);
    }

    private void removeCommentContainer(RoutingContext ctx) {
        int containerID = Request.from(ctx).getContainerID();
        String comment = Request.from(ctx).getCommentFromJson();

        controller.removeContainerComment(containerID, comment);

        Response.sendCommentDeletedContainer(ctx);
    }

    private void removeCommentPallet(RoutingContext ctx) {
        int palletID = Request.from(ctx).getPalletID();
        String comment = Request.from(ctx).getCommentFromJson();

        controller.removePalletComment(palletID, comment);

        Response.sendCommentDeletedPallet(ctx);
    }

    private void onFailedRequest(RoutingContext ctx) {
        Throwable cause = ctx.failure();
        int code = ctx.statusCode();
        String quote = Objects.isNull(cause) ? "" + code : cause.getMessage();

        // Map custom runtime exceptions to a HTTP status code.
        LOGGER.log(Level.INFO, "Failed request", cause);
        if (cause instanceof IllegalArgumentException) {
            code = 400;
        } else if (cause instanceof MalformedRequestException) {
            code = 400;
        } else if (cause instanceof NoSuchElementException) {
            code = 404;
        } else {
            LOGGER.log(Level.WARNING, "Failed request", cause);
        }

        Response.sendFailure(ctx, code, quote);
    }

    private CorsHandler createCorsHandler() {
        return CorsHandler.create(".*.")
                .allowedHeader("x-requested-with")
                .allowedHeader("Access-Control-Allow-Origin")
                .allowedHeader("Access-Control-Allow-Credentials")
                .allowCredentials(true)
                .allowedHeader("origin")
                .allowedHeader("Content-Type")
                .allowedHeader("Authorization")
                .allowedHeader("accept")
                .allowedMethod(HttpMethod.HEAD)
                .allowedMethod(HttpMethod.GET)
                .allowedMethod(HttpMethod.POST)
                .allowedMethod(HttpMethod.OPTIONS)
                .allowedMethod(HttpMethod.PATCH)
                .allowedMethod(HttpMethod.DELETE)
                .allowedMethod(HttpMethod.PUT);
    }
}
