package be.howest.ti.mars.logic.controller;

import be.howest.ti.mars.logic.domain.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MockMarsController implements MarsController {

    private static final Set<String> comments = new HashSet<>();
    private static final Map<String, Integer> contents = new HashMap<>();
    private static final Container EXAMPLE_CONTAINER = new Container(0, 300,
            new Dimension(4, 2.5, 6), "Dusty Depot", new Location("Warehouse Space Station"),
            contents, comments);
    private static final Pallet EXAMPLE_PALLET = new Pallet(0, 4, 2.5, 6, "Dusty Depot",
            new Location("Warehouse Space Station"));

    @Override
    public Map<String, Object> getContentsByOwner(String businessOwner) {
        Map<String, Object> ownerInventory = new HashMap<>();
        ownerInventory.put("container", EXAMPLE_CONTAINER);
        ownerInventory.put("pallet", EXAMPLE_PALLET);
        return ownerInventory;
    }

    @Override
    public Map<String, Object> getContentsByLocation(String location) {
        Map<String, Object> locationInventory = new HashMap<>();
        locationInventory.put("container", EXAMPLE_CONTAINER);
        locationInventory.put("pallet", EXAMPLE_PALLET);
        return locationInventory;
    }

    @Override
    public Container getContainer(int containerID){
        return EXAMPLE_CONTAINER;
    }

    @Override
    public Set<Container> getContainers() {
        Set<Container> containers = new HashSet<>();
        containers.add(EXAMPLE_CONTAINER);
        return containers;
    }

    @Override
    public Container createContainer(String size){
        return new RentalControl().createContainer(size);
    }

    @Override
    public Set<Container> getContainersByOwner(String owner) {
        Set<Container> containers = new HashSet<>();
        containers.add(EXAMPLE_CONTAINER);
        return containers;
    }

    public Set<Container> getContainersByLocation(String location){
        Set<Container> containers = new HashSet<>();
        containers.add(EXAMPLE_CONTAINER);
        return containers;
    }

    @Override
    public Set<Pallet> getPalletsByOwner(String owner) {
        Set<Pallet> pallets = new HashSet<>();
        pallets.add(EXAMPLE_PALLET);
        return pallets;
    }

    @Override
    public Set<Pallet> getPalletsByLocation(String location) {
        Set<Pallet> pallets = new HashSet<>();
        pallets.add(EXAMPLE_PALLET);
        return pallets;
    }

    @Override
    public Pallet createPalletUniversalSize() {
        return new RentalControl().createPalletUniversalSize();
    }

    @Override
    public Container deleteContainer(int containerID) {
        return EXAMPLE_CONTAINER;
    }

    @Override
    public Pallet deletePallet(int palletID) {
        return EXAMPLE_PALLET;
    }

    @Override
    public Container changeContainerLocation(int id, Location location) {
        return EXAMPLE_CONTAINER;
    }

    @Override
    public Pallet changePalletLocation(int palletID, Location location) {
        return EXAMPLE_PALLET;
    }

    @Override
    public Pallet getPalletInfo(int palletID) {
        return EXAMPLE_PALLET;
    }

    @Override
    public Set<Pallet> getPallets() {
        Set<Pallet> pallets = new HashSet<>();
        pallets.add(EXAMPLE_PALLET);
        return pallets;
    }

    @Override
    public Pallet addPalletContent(int id, Map<String, Integer> content) {
        return EXAMPLE_PALLET;
    }

    @Override
    public Pallet removePalletContent(int palletID, Map<String, Integer> content) {
        return EXAMPLE_PALLET;
    }

    @Override
    public Container changeContainerOwner(int containerID, String newOwner) {
        return EXAMPLE_CONTAINER;
    }

    @Override
    public void addContainerComment(int containerID, String comment) {}

    @Override
    public void addPalletComment(int palletID, String comment) {}

    @Override
    public void removeContainerComment(int containerID, String owner) {}

    @Override
    public void removePalletComment(int palletID, String owner) {}

    @Override
    public Pallet changePalletOwner(int palletID, String newOwner) {
        return EXAMPLE_PALLET;
    }

    @Override
    public Container addContainerContent(int containerID, Map<String, Integer> content) {
        return EXAMPLE_CONTAINER;
    }

    @Override
    public Container removeContainerContent(int containerID, Map<String, Integer> content) {
        return EXAMPLE_CONTAINER;
    }
}
