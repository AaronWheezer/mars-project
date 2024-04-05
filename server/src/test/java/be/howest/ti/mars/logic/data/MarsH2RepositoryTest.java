package be.howest.ti.mars.logic.data;

import be.howest.ti.mars.logic.domain.Container;
import be.howest.ti.mars.logic.domain.Location;
import be.howest.ti.mars.logic.domain.Pallet;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class MarsH2RepositoryTest {
    private static final String URL = "jdbc:h2:./db-19";

    @BeforeEach
    void setupTestSuite() {
        Repositories.shutdown();
        JsonObject dbProperties = new JsonObject(Map.of("url",URL,
                "username", "",
                "password", "",
                "webconsole.port", 9000 ));
        Repositories.configure(dbProperties);
    }

    @Test
    void getContainers(){
        Set<Container> containers = Repositories.getH2Repo().getContainers();

        Assertions.assertTrue(containers != null && !containers.isEmpty());
    }

    @Test
    void createContainer(){
        Set<Container> containers = Repositories.getH2Repo().getContainers();
        int amountContainers = containers.size();
        Container container = new Container(15, 15, 15 ,15, new Location("test"));
        Repositories.getH2Repo().insertContainer(container);
        Assertions.assertEquals(amountContainers+1, Repositories.getH2Repo().getContainers().size());
    }

    @Test
    void getContainer(){
        Container container = Repositories.getH2Repo().getContainer(93459315);

        Assertions.assertNotNull(container);
    }

    @Test
    void getContainersByFilter() {
        Set<Container> containers = Repositories.getH2Repo().getContainersByFilter("owner", "Dusty Depot");
        Assertions.assertNotNull(containers);
        containers = Repositories.getH2Repo().getContainersByFilter("location", "Warehouse Space Station");
        Assertions.assertNotNull(containers);
    }

    @Test
    void createUniversalPallet(){
        Pallet pallet = new Pallet(85834521, 0.8, 0.14, 1.2, new Location("test"));
        Assertions.assertNotNull(pallet);
    }

    @Test
    void deleteContainer(){
        Set<Container> containers = Repositories.getH2Repo().getContainers();
        int amountContainers = containers.size();
        Repositories.getH2Repo().deleteContainer(93459315);
        Assertions.assertEquals(amountContainers-1, Repositories.getH2Repo().getContainers().size());
    }

    @Test
    void deletePallet(){
        Set<Pallet> pallets = new HashSet<>();
        pallets.add(new Pallet(85834521, 0.8, 0.14, 1.2, new Location("test")));
        pallets.add(new Pallet(35779620, 0.8, 0.14, 1.2, new Location("test")));

        int amountPallets = pallets.size();
        Repositories.getH2Repo().deletePallet(85834521);
        Assertions.assertEquals(1,amountPallets-1);
    }

    @Test
    void getPallet(){
        Pallet pallet = Repositories.getH2Repo().getPallet(85834521);
        Assertions.assertNotNull(pallet);
    }

    @Test
    void getPallets(){
        Set<Pallet> pallets = Repositories.getH2Repo().getPallets();
        Assertions.assertNotNull(pallets);
    }

    @Test
    void getPalletsByFilter(){
        Set<Pallet> pallets = Repositories.getH2Repo().getPalletsByFilter("owner", "Dusty Depot");
        Assertions.assertNotNull(pallets);
        pallets = Repositories.getH2Repo().getPalletsByFilter("location", "Warehouse Space Station");
        Assertions.assertNotNull(pallets);

    }
    @Test
    void changePalletLocation(){
        Pallet pallet = Repositories.getH2Repo().getPallet(85834521);
        String locationName = pallet.getLocation().getLocationName();
        Repositories.getH2Repo().updatePalletLocation(85834521, new Location("Warehouse Space Station 2"));
        Assertions.assertNotEquals(locationName, Repositories.getH2Repo().getPallet(85834521).getLocation().getLocationName());
    }


    //This needs to change probably

    @Test
    void updatePositivePalletContent(){
        Repositories.getH2Repo().updatePalletContent(85834521, "Boxes of bananas", 5, 20, "plus");
    }
    @Test
    void changeContainerOwner(){
        Container container = Repositories.getH2Repo().getContainer(93459315);
        Repositories.getH2Repo().changeContainerOwner(93459315, "Imposter");
        Assertions.assertNotEquals(container.getOwner(), Repositories.getH2Repo().getContainer(93459315).getOwner());
    }

    @Test
    void changePalletOwner(){
        Pallet pallet = Repositories.getH2Repo().getPallet(85834521);
        Repositories.getH2Repo().changePalletOwner(85834521, "Imposter");
        Assertions.assertNotEquals(pallet.getOwner(), Repositories.getH2Repo().getPallet(85834521).getOwner());
    }

    @Test
    void addContainerContent(){
        Container container = Repositories.getH2Repo().getContainer(93459315);
        Repositories.getH2Repo().addContainerContent(93459315, Map.of("Banana", 50));
        Assertions.assertNotEquals(container.getContents(), Repositories.getH2Repo().getContainer(93459315).getContents());
    }

    @Test
    void removeContainerContent(){
        Container container = Repositories.getH2Repo().getContainer(93459315);
        Repositories.getH2Repo().removeContainerContent(93459315, Map.of("Glass window", 10));
        Assertions.assertEquals(container.getContents().get("Glass window")-10,
                Repositories.getH2Repo().getContainer(93459315).getContents().get("Glass window"));
        Repositories.getH2Repo().removeContainerContent(93459315, Map.of("Glass window", 40));
        Assertions.assertEquals(0, Repositories.getH2Repo().getContainer(93459315).getContents().size());
    }

    @Test
    void insertPalletContent(){
        Map<String, Integer> newContent = new HashMap<>();
        newContent.put("rocks", 5000);
        Repositories.getH2Repo().addPalletContent(85834521, newContent);
        Assertions.assertEquals(2, Repositories.getH2Repo().getPallet(85834521).getContents().size());
        Repositories.getH2Repo().addPalletContent(85834521, newContent);
        Assertions.assertEquals(10000, Repositories.getH2Repo().getPallet(85834521).getContents().get("rocks"));
    }

    @Test
    void removePalletContent(){
        Repositories.getH2Repo().removePalletContent(85834521, Map.of("Boxes of bananas", 3));
        Assertions.assertEquals(2, Repositories.getH2Repo().getPallet(85834521).getContents().get("Boxes of bananas"));
        Repositories.getH2Repo().removePalletContent(85834521, Map.of("Boxes of bananas", 2));
        Assertions.assertEquals(0, Repositories.getH2Repo().getPallet(85834521).getContents().size());
    }

    @Test
    void addContainerComments(){
        Repositories.getH2Repo().addContainerComment(93459315, "This is a test");
        Assertions.assertTrue(Repositories.getH2Repo().getContainer(93459315).getComments().contains("This is a test"));
    }

    @Test
    void removeContainerComments(){
        Repositories.getH2Repo().removeContainerComment(93459315, "Container is fragile");
        Assertions.assertEquals(1, Repositories.getH2Repo().getContainer(93459315).getComments().size());
    }

    @Test
    void addPalletComments(){
        Repositories.getH2Repo().addPalletComment(85834521, "This is a test");
        Assertions.assertTrue(Repositories.getH2Repo().getPallet(85834521).getComments().contains("This is a test"));
    }

    @Test
    void removePalletComments(){
        Repositories.getH2Repo().removePalletComment(85834521, "Never open");
        Assertions.assertEquals(0, Repositories.getH2Repo().getPallet(85834521).getComments().size());
    }
}
