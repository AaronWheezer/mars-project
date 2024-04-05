package be.howest.ti.mars.logic.controller;

import be.howest.ti.mars.logic.domain.Container;
import be.howest.ti.mars.logic.domain.Location;
import be.howest.ti.mars.logic.domain.Pallet;

import java.util.Map;
import java.util.Set;

public interface MarsController {

    Map<String, Object> getContentsByOwner(String businessOwner);

    Map<String, Object> getContentsByLocation(String location);

    Container getContainer(int containerID);

    Set<Container> getContainers();

    Container createContainer(String size);

    Set<Container> getContainersByOwner(String owner);

    Set<Container> getContainersByLocation(String location);

    Set<Pallet> getPalletsByOwner(String owner);

    Set<Pallet> getPalletsByLocation(String location);

    Pallet createPalletUniversalSize();

    Container deleteContainer(int containerID);

    Pallet deletePallet(int palletID);

    Container changeContainerLocation(int id, Location location);

    Pallet changePalletLocation(int palletID, Location location);

    Pallet getPalletInfo(int palletID);

    Set<Pallet> getPallets();

    Container changeContainerOwner(int containerID, String newOwner);

    Pallet changePalletOwner(int palletID, String newOwner);

    Container addContainerContent(int containerID, Map<String, Integer> content);

    Container removeContainerContent(int containerID, Map<String, Integer> content);

    Pallet addPalletContent(int id, Map<String, Integer> content);

    Pallet removePalletContent(int palletID, Map<String, Integer> content);

    void addContainerComment(int containerID, String comment);

    void addPalletComment(int palletID, String comment);

    void removeContainerComment(int containerID, String owner);

    void removePalletComment(int palletID, String owner);
}
