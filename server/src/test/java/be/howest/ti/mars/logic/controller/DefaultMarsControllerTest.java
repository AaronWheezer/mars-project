package be.howest.ti.mars.logic.controller;

import be.howest.ti.mars.logic.data.Repositories;
import be.howest.ti.mars.logic.domain.Container;
import be.howest.ti.mars.logic.domain.Location;
import be.howest.ti.mars.logic.domain.Pallet;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DefaultMarsControllerTest {

    private static final String URL = "jdbc:h2:./db-19";

    MarsController sut;

    @BeforeAll
    void setupTestSuite() {
        Repositories.shutdown();
        JsonObject dbProperties = new JsonObject(Map.of("url", "jdbc:h2:./db-19",
                "username", "",
                "password", "",
                "webconsole.port", 9000));
        Repositories.configure(dbProperties);
        sut = new DefaultMarsController();
    }

    @BeforeEach
    void setupTest() {
        Repositories.getH2Repo().generateData();
    }

    @Test
    void getContentsByOwner(){
        Map<String, Object> ownerInventory = sut.getContentsByOwner("Dusty Depot");

        Assertions.assertNotNull(ownerInventory);
    }

    @Test
    void getContainer(){
        Container container = sut.getContainer(93459315);

        Assertions.assertNotNull(container);
    }

    @Test
    void getContainers(){
        Set<Container> containers =sut.getContainers();

        Assertions.assertTrue(containers != null && !containers.isEmpty());
    }

    @Test
    void createContainer(){
        Container container = sut.createContainer("small");

        assertNotNull(container);
    }

    @Test
    void getContainersByOwner() {
        Set<Container> containers = sut.getContainersByOwner("Dusty Depot");

        Assertions.assertTrue(containers != null && !containers.isEmpty());
    }

    @Test
    void getContainersByLocation(){
        Set<Container> containers = sut.getContainersByLocation("Warehouse Space Station");

        Assertions.assertTrue(containers != null && !containers.isEmpty());
    }

    @Test
    void getPallets(){
        Set<Pallet> pallets = sut.getPallets();
        Assertions.assertNotNull(pallets);
    }

    @Test
    void getPalletInfo(){
        Pallet pallet = sut.getPalletInfo(85834521);

        Assertions.assertNotNull(pallet);
    }

    @Test
    void getPalletsByOwner() {
        Set<Pallet> pallets = sut.getPalletsByOwner("Dusty Depot");

        Assertions.assertTrue(pallets != null && !pallets.isEmpty());
    }

    @Test
    void getPalletsByLocation() {
        Set<Pallet> pallets = sut.getPalletsByLocation("Warehouse Space Station");

        Assertions.assertTrue(pallets != null && !pallets.isEmpty());
    }
    @Test
    void createPalletUniversalSize(){
        Pallet pallet = sut.createPalletUniversalSize();
        Assertions.assertNotNull(pallet);
    }

    @Test
    void changeContainerLocation(){
        Container container = sut.changeContainerLocation(93459315, new Location("test"));

        Assertions.assertEquals("test", container.getLocation().getLocationName());
    }

    @Test
    void changePalletLocation(){
        Pallet pallet = sut.changePalletLocation(85834521, new Location("test"));

        Assertions.assertEquals("test", pallet.getLocation().getLocationName());
    }

    @Test
    void changeContainerOwner(){
        Container container = sut.changeContainerOwner(93459315, "ImposterOwner");

        Assertions.assertEquals("ImposterOwner", container.getOwner());
    }

    @Test
    void changePalletOwner(){
        Pallet pallet = sut.changePalletOwner(85834521, "ImposterOwner");

        Assertions.assertEquals("ImposterOwner", pallet.getOwner());
    }

    @Test
    void editPalletContent(){
        Map<String, Integer> content = new HashMap<>();
        content.put("bananen", 50);
        Pallet pallet = sut.addPalletContent(85834521, content);
        Assertions.assertNotNull(pallet);
    }

    @Test
    void deleteContainer(){
        Container container = sut.deleteContainer(93459315);
        Assertions.assertNotNull(container);
    }

    @Test
    void deletePallet(){
        Pallet pallet = sut.deletePallet(85834521);
        Assertions.assertNotNull(pallet);
    }

    @Test @AfterAll
    void getNoContainers(){
        Set<Container> containers = sut.getContainers();
        List<Integer> allIDs = new ArrayList<>();
        containers.forEach(container -> allIDs.add(container.getId()));

        for (int i = 0; i < containers.size(); i++) {
            sut.deleteContainer(allIDs.get(i))  ;
        }

        Assertions.assertThrows(NoSuchElementException.class, () -> sut.getContainers());
    }
}
