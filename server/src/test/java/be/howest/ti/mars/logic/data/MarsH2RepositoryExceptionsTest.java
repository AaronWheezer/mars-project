package be.howest.ti.mars.logic.data;

import be.howest.ti.mars.logic.domain.Container;
import be.howest.ti.mars.logic.domain.Dimension;
import be.howest.ti.mars.logic.domain.Location;
import be.howest.ti.mars.logic.domain.Pallet;
import be.howest.ti.mars.logic.exceptions.RepositoryException;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
class MarsH2RepositoryExceptionsTest {

    private static final String URL = "jdbc:h2:./db-19";

    @Test
    void getH2RepoWithNoDbFails() {
        // Arrange
        Repositories.shutdown();

        // Act + Assert
        Assertions.assertThrows(RepositoryException.class, Repositories::getH2Repo);
    }

    @Test
    void functionsWithSQLExceptionFailsNicely() {
        // Arrange
        Container container = new Container(93459315,300, new Dimension(5,3,25), "Dusty Depot",
                new Location("Warehouse Space Station"), new HashMap<>(), new HashSet<>());
        Pallet pallet = new Pallet(85834521,300,5,3, new Location("dusty"));
        Map<String, Integer> content = new HashMap<>();
        content.put("boxes of rocks", 20);
        content.put("tons of iron", 5);
        JsonObject dbProperties = new JsonObject(Map.of("url",URL,
                "username", "",
                "password", "",
                "webconsole.port", 9000 ));
        Repositories.shutdown();
        Repositories.configure(dbProperties);
        MarsH2Repository repo = Repositories.getH2Repo();
        repo.cleanUp();

        // Act + Assert
        Assertions.assertThrows(RepositoryException.class, repo::getContainers);
        Assertions.assertThrows(RepositoryException.class, repo::getPallets);
        Assertions.assertThrows(RepositoryException.class, () -> repo.getContainer(container.getId()));
        Assertions.assertThrows(RepositoryException.class, () -> repo.getContainersByFilter("owner", "Dusty Depot"));
        Assertions.assertThrows(RepositoryException.class, () -> repo.getContainersByFilter("location", "Warehouse Space Station"));
        Assertions.assertThrows(RepositoryException.class, () -> repo.getPallet(pallet.getId()));
        Assertions.assertThrows(RepositoryException.class, () -> repo.getPalletsByFilter("owner", "Dusty Depot"));
        Assertions.assertThrows(RepositoryException.class, () -> repo.getPalletsByFilter("location", "Warehouse Space Station"));
        Assertions.assertThrows(RepositoryException.class, () -> repo.insertContainer(container));
        Assertions.assertThrows(RepositoryException.class, () -> repo.insertPallet(pallet));
        Assertions.assertThrows(RepositoryException.class, () -> repo.addPalletContent(0, content));
        Assertions.assertThrows(RepositoryException.class, () -> repo.updatePalletContent(0, "boxes of rocks", 100, 20, "plus"));
        Assertions.assertThrows(RepositoryException.class, () -> repo.updateContainerLocation(container.getId(), new Location("Dust")));
        Assertions.assertThrows(RepositoryException.class, () -> repo.updatePalletLocation(pallet.getId(), new Location("Dust")));
        Assertions.assertThrows(RepositoryException.class, () -> repo.deleteContainer(container.getId()));
        Assertions.assertThrows(RepositoryException.class, () -> repo.deletePallet(pallet.getId()));
        Assertions.assertThrows(RepositoryException.class, () -> repo.changeContainerOwner(container.getId(), "Imposter"));
        Assertions.assertThrows(RepositoryException.class, () -> repo.changePalletOwner(pallet.getId(), "Imposter"));
        Assertions.assertThrows(RepositoryException.class, () -> repo.addContainerContent(container.getId(), content));
        Assertions.assertThrows(RepositoryException.class, () -> repo.updateContainerContent(container.getId(), "banana", 10, 10, "plus"));
        Assertions.assertThrows(RepositoryException.class, () -> repo.deleteContainerContent(container.getId(), "banana"));
        Assertions.assertThrows(RepositoryException.class, () -> repo.deletePalletContent(container.getId(), "banana"));
        Assertions.assertThrows(RepositoryException.class, () -> repo.addContainerComment(container.getId(), "fragile"));
        Assertions.assertThrows(RepositoryException.class, () -> repo.removeContainerComment(container.getId(), "fragile"));
        Assertions.assertThrows(RepositoryException.class, () -> repo.addPalletComment(pallet.getId(), "fragile"));
        Assertions.assertThrows(RepositoryException.class, () -> repo.removePalletComment(pallet.getId(), "fragile"));
    }
}
