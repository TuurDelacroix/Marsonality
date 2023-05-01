package be.howest.ti.mars.logic.data;

import be.howest.ti.mars.logic.domain.Profile;
import be.howest.ti.mars.logic.exceptions.RepositoryException;
import io.netty.util.internal.StringUtil;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MarsH2RepositoryExceptionsTest {

    private static final String URL = "jdbc:h2:./db-14";

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
        int id = 1;
        String traitType = "POSITIVE";
        String traitName = "Tough";
        Profile profile = new Profile("test", id, "0");
        JsonObject notification = new JsonObject();

        JsonObject dbProperties = new JsonObject(Map.of("url",URL,
                "username", "",
                "password", "",
                "webconsole.port", 9000 ));
        Repositories.shutdown();
        Repositories.configure(dbProperties);
        MarsH2Repository repo = Repositories.getH2Repo();
        repo.cleanUp();

        // Act + Assert
        assertThrows(RepositoryException.class, repo::getMartians);
        assertThrows(RepositoryException.class, () -> repo.getMartian(id));
        assertThrows(RepositoryException.class, repo::getProfiles);
        assertThrows(RepositoryException.class, () -> repo.getProfilesOfMartian(id));
        assertThrows(RepositoryException.class, () -> repo.getTraitsOfMartian(id));
        assertThrows(RepositoryException.class, () -> repo.buyTrait(id, traitName));
        assertThrows(RepositoryException.class, () -> repo.getTraitsOfMartianByTraitType(id, traitType));
        assertThrows(RepositoryException.class, () -> repo.getProfileOfMartian(id, profile.getName()));
        assertThrows(RepositoryException.class, () -> repo.storeProfileOfMartian(profile));
        assertThrows(RepositoryException.class, () -> repo.getDefaultProfileOfMartian(id));
        assertThrows(RepositoryException.class, () -> repo.getActiveProfileOfMartian(id));
        assertThrows(RepositoryException.class, repo::getTraits);
        assertThrows(RepositoryException.class, () -> repo.getTraits(traitType));
        assertThrows(RepositoryException.class, () -> repo.getTraitsOfMartiansProfile(id, profile.getName()));
        assertThrows(RepositoryException.class, () -> repo.addTraitToProfile(id, profile.getName(), traitName));
        assertThrows(RepositoryException.class, () -> repo.getTraitsOfMartiansProfile(id, profile.getName()));
        assertThrows(RepositoryException.class, () -> repo.getTrait(traitName));
        assertThrows(RepositoryException.class, () -> repo.addTraitToProfile(id, profile.getName(), traitName));
        assertThrows(RepositoryException.class, () -> repo.removeTraitFromProfile(id, profile.getName(), traitName));
        assertThrows(RepositoryException.class, repo::getChips);
        assertThrows(RepositoryException.class, repo::setTraitsOfMartians);
        assertThrows(RepositoryException.class, repo::setTraitsOfMartians);
        assertThrows(RepositoryException.class, repo::getNotifications);
        assertThrows(RepositoryException.class, () -> repo.insertNotification(notification));
        assertThrows(RepositoryException.class, () -> repo.activateProfile(id, profile.getName()));
    }


}
