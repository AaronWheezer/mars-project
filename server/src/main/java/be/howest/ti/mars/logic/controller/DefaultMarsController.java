package be.howest.ti.mars.logic.controller;

import be.howest.ti.mars.logic.data.Repositories;
import be.howest.ti.mars.logic.domain.Container;
import be.howest.ti.mars.logic.domain.Location;
import be.howest.ti.mars.logic.domain.Pallet;
import be.howest.ti.mars.logic.domain.RentalControl;

import java.util.*;

/**
 * DefaultMarsController is the default implementation for the MarsController interface.
 * The controller shouldn't even know that it is used in an API context..
 * <p>
 * This class and all other classes in the logic-package (or future sub-packages)
 * should use 100% plain old Java Objects (POJOs). The use of Json, JsonObject or
 * Strings that contain encoded/json data should be avoided here.
 * Keep libraries and frameworks out of the logic packages as much as possible.
 * Do not be afraid to create your own Java classes if needed.
 */
public class DefaultMarsController implements MarsController {

    private static final String OWNER = "owner";

    private static final String LOCATION = "location";

    private static final String MSG_NO_CONTAINERS = "There are no containers present";
    private static final RentalControl rentalController = new RentalControl();

    @Override
    public Map<String, Object> getContentsByOwner(String businessOwner) {
        Map<String, Object> ownerInventory = new HashMap<>();

        Set<Container> containersOfOwner = Repositories.getH2Repo().getContainersByFilter(OWNER, businessOwner);
        Set<Pallet> palletsOfOwner = Repositories.getH2Repo().getPalletsByFilter(OWNER, businessOwner);

        ownerInventory.put("container", containersOfOwner);
        ownerInventory.put("pallet", palletsOfOwner);

        return ownerInventory;
    }

    @Override
    public Map<String, Object> getContentsByLocation(String location) {
        Map<String, Object> locationInventory = new HashMap<>();

        Set<Container> containersOfLocation = Repositories.getH2Repo().getContainersByFilter(LOCATION, location);
        Set<Pallet> palletsOfLocation = Repositories.getH2Repo().getPalletsByFilter(LOCATION, location);

        locationInventory.put("containers", containersOfLocation);
        locationInventory.put("pallets", palletsOfLocation);

        return locationInventory;
    }

    @Override
    public Container getContainer(int containerID){
        return Repositories.getH2Repo().getContainer(containerID);
    }

    @Override
    public Set<Container> getContainers() {
        Set<Container> containers = Repositories.getH2Repo().getContainers();
        if (containers.isEmpty()) {
            throw new NoSuchElementException(MSG_NO_CONTAINERS);
        }
        return containers;
    }

    @Override
    public Container createContainer(String size) {
        Container container = rentalController.createContainer(size);
        Repositories.getH2Repo().insertContainer(container);
        return container;
    }

    @Override
    public Set<Container> getContainersByOwner(String owner) {
        return Repositories.getH2Repo().getContainersByFilter(OWNER, owner);
    }

    @Override
    public Set<Container> getContainersByLocation(String location) {
        return Repositories.getH2Repo().getContainersByFilter(LOCATION, location);
    }

    @Override
    public Set<Pallet> getPalletsByOwner(String owner) {
        return Repositories.getH2Repo().getPalletsByFilter(OWNER, owner);
    }

    @Override
    public Set<Pallet> getPalletsByLocation(String location) {
        return Repositories.getH2Repo().getPalletsByFilter(LOCATION, location);
    }

    public Pallet createPalletUniversalSize() {
        Pallet pallet = rentalController.createPalletUniversalSize();
        Repositories.getH2Repo().insertPallet(pallet);
        return pallet;
    }

    @Override
    public Container changeContainerLocation(int containerID, Location location) {
        Repositories.getH2Repo().updateContainerLocation(containerID, location);
        return Repositories.getH2Repo().getContainer(containerID);
    }

    @Override
    public Container deleteContainer(int containerID) {
        Container container = Repositories.getH2Repo().getContainer(containerID);
        Repositories.getH2Repo().deleteContainer(containerID);
        return container;
    }

    @Override
    public Pallet deletePallet(int palletID) {
        Pallet pallet = Repositories.getH2Repo().getPallet(palletID);
        Repositories.getH2Repo().deletePallet(palletID);
        return pallet;
    }

    @Override
    public Pallet changePalletLocation(int palletID, Location location) {
        Repositories.getH2Repo().updatePalletLocation(palletID, location);
        return Repositories.getH2Repo().getPallet(palletID);
    }

    @Override
    public Pallet getPalletInfo(int palletID) {
        return  Repositories.getH2Repo().getPallet(palletID);
    }

    @Override
    public Set<Pallet> getPallets(){
        return Repositories.getH2Repo().getPallets();
    }

    @Override
    public Container changeContainerOwner(int containerID, String newOwner) {
        Repositories.getH2Repo().changeContainerOwner(containerID, newOwner);
        return Repositories.getH2Repo().getContainer(containerID);
    }

    @Override
    public Container addContainerContent(int containerID, Map<String, Integer> content) {
        Repositories.getH2Repo().addContainerContent(containerID, content);
        return Repositories.getH2Repo().getContainer(containerID);
    }

    @Override
    public Container removeContainerContent(int containerID, Map<String, Integer> content) {
        Repositories.getH2Repo().removeContainerContent(containerID, content);
        return Repositories.getH2Repo().getContainer(containerID);
    }

    @Override
    public Pallet addPalletContent(int id, Map<String, Integer> content) {
        Repositories.getH2Repo().addPalletContent(id, content);
        return Repositories.getH2Repo().getPallet(id);
    }

    @Override
    public Pallet removePalletContent(int palletID, Map<String, Integer> content) {
        Repositories.getH2Repo().removePalletContent(palletID, content);
        return Repositories.getH2Repo().getPallet(palletID);
    }

    @Override
    public void addContainerComment(int containerID, String comment) {
        Repositories.getH2Repo().addContainerComment(containerID, comment);
    }

    @Override
    public void addPalletComment(int palletID, String comment) {
        Repositories.getH2Repo().addPalletComment(palletID, comment);
    }

    @Override
    public void removeContainerComment(int containerID, String owner) {
        Repositories.getH2Repo().removeContainerComment(containerID, owner);
    }

    @Override
    public void removePalletComment(int palletID, String owner) {
        Repositories.getH2Repo().removePalletComment(palletID, owner);
    }

    @Override
    public Pallet changePalletOwner(int palletID, String newOwner) {
        Repositories.getH2Repo().changePalletOwner(palletID, newOwner);
        return Repositories.getH2Repo().getPallet(palletID);
    }
}