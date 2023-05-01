package be.howest.ti.mars.logic.data;

import be.howest.ti.mars.logic.domain.*;
import be.howest.ti.mars.logic.exceptions.RepositoryException;
import io.vertx.core.json.JsonObject;
import org.h2.tools.Server;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/*
This is only a starter class to use an H2 database.
In this start project there was no need for a Java interface MarsRepository.
Please always use interfaces when needed.

To make this class useful, please complete it with the topics seen in the module OOA & SD
 */

public class MarsH2Repository {
    private static final Logger LOGGER = Logger.getLogger(MarsH2Repository.class.getName());

    // Column names
    private static final String MARS_ID = "marsId";
    private static final String PRICE = "price";
    private static final String CREATION_DATE = "creation_date";
    public static final String COUNTER = "counter";
    public static final String NAME = "name";
    public static final String IN_USE = "in_use";
    public static final String IS_LOCKED = "is_locked";
    private static final String TITLE = "title";
    private static final String OPTIONS = "options";
    private static final String DESCRIPTION = "description";
    private static final String TYPE = "type";

    // Getters
    private static final String SQL_ALL_MARTIANS = "select * from martian;";
    private static final String SQL_MARTIAN_BY_MARSID = "select * from martian where marsId = ?;";
    private static final String SQL_GET_ALL_PROFILES = "select * from profile";
    private static final String SQL_GET_ALL_TRAITS_OF_MARTIAN = "SELECT T.* FROM MARTIAN_TRAIT MT JOIN TRAITS T ON MT.NAME = T.NAME WHERE MT.MARSID = ?";
    private static final String SQL_GET_ALL_TRAITS_OF_MARTIAN_BY_TYPE = "SELECT T.* FROM MARTIAN_TRAIT MT JOIN TRAITS T ON MT.NAME = T.NAME WHERE MT.MARSID = ? AND T.TYPE = ?";
    private static final String SQL_GET_PROFILE_BY_MARSID = "select * from profile p join martian m on m.marsId = p.marsId where p.marsId = ?;";
    private static final String SQL_GET_PROFILE_BY_MARSID_AND_PROFILENAME = "select * from profile p join martian m on m.marsId = p.marsId where p.marsId = ? AND p.name LIKE ?;";
    private static final String SQL_GET_TRAITS_BY_PROFILE_NAME = "SELECT * FROM TRAITS T JOIN PROFILE_TRAIT PT ON PT.TRAITNAME = T.NAME JOIN PROFILE P ON PT.PROFILENAME = P.NAME WHERE P.NAME = ?;";
    private static final String SQL_GET_DEFAULT_PROFILE_BY_MARSID = "SELECT * FROM PROFILE WHERE NAME = ? AND MARSID = ?";
    private static final String SQL_GET_TRAITS = "select * from traits";
    private static final String SQL_GET_TRAITS_BY_TYPE = "select * from traits where type = ?";
    private static final String SQL_GET_ALL_CHIPS = "SELECT * FROM chip";
    private static final String SQL_GET_TRAI_BY_NAME = "SELECT * FROM traits WHERE NAME = ?";
    private static final String SQL_GET_ALL_NOTIFICATIONS = "select * from notification";

    // Inserts
    private static final String SQL_INSERT_NEW_PROFILE_OF_MARTIAN = "insert into profile (name, marsid, price, creation_date, counter, in_use) VALUES (?,?,?,?,?,?)";
    private static final String SQL_BUY_TRAIT = "INSERT INTO martian_trait VALUES (?, ?)";
    private static final String SQL_INSERT_NOTIFICATION = "insert into notification (title,options) VALUES (?,?)";

    // Updaters
    private static final String SQL_INSERT_MARTIAN_TRAITS = "INSERT INTO martian_trait (marsId, name) VALUES (?,?);";
    private static final String SQL_ADD_TRAIT_TO_PROFILE = "INSERT INTO PROFILE_TRAIT VALUES (?, ?)";
    private static final String SQL_DEACTIVATE_PROFILE = "UPDATE profile SET IN_USE = FALSE WHERE MARSID = ? AND NAME = ?";
    private static final String SQL_ACTIVATE_PROFILE = "UPDATE profile SET IN_USE = TRUE WHERE MARSID = ? AND NAME = ?";
    private static final String SQL_UPDATE_PROFILE_COUNTER = "UPDATE profile SET counter = COUNTER + 1 WHERE MARSID = ? AND NAME = ?";

    // Deletes
    private static final String SQL_TRUNCATE_MARTIAN_TRAITS_TABLE = "TRUNCATE TABLE martian_trait";
    private static final String SQL_REMOVE_TRAIT_FROM_PROFILE = "DELETE FROM PROFILE_TRAIT WHERE PROFILENAME = ? AND TRAITNAME = ?";

    private final Server dbWebConsole;
    private final String username;
    private final String password;
    private final String url;

    public MarsH2Repository(String url, String username, String password, int console) {
        try {
            this.username = username;
            this.password = password;
            this.url = url;
            this.dbWebConsole = Server.createWebServer(
                    "-ifNotExists",
                    "-webPort", String.valueOf(console)).start();
            LOGGER.log(Level.INFO, "Database web console started on port: {0}", console);
            this.generateData();
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "DB configuration failed", ex);
            throw new RepositoryException("Could not configure MarsH2repository");
        }
    }

    public void cleanUp() {
        if (dbWebConsole != null && dbWebConsole.isRunning(false))
            dbWebConsole.stop();

        try {
            Files.deleteIfExists(Path.of("./db-14.mv.db"));
            Files.deleteIfExists(Path.of("./db-14.trace.db"));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Database cleanup failed.", e);
            throw new RepositoryException("Database cleanup failed.");
        }
    }

    public void generateData() {
        try {
            executeScript("db-create.sql");
            executeScript("db-populate.sql");
        } catch (IOException | SQLException ex) {
            LOGGER.log(Level.SEVERE, "Execution of database scripts failed.", ex);
        }
    }

    private void executeScript(String fileName) throws IOException, SQLException {
        String createDbSql = readFile(fileName);
        try (
                Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(createDbSql)
        ) {
            stmt.executeUpdate();
        }
    }

    private String readFile(String fileName) throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
        if (inputStream == null)
            throw new RepositoryException("Could not read file: " + fileName);

        return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    public List<Martian> getMartians() {
        List<Martian> martians = new ArrayList<>();

        try (
                Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(SQL_ALL_MARTIANS)
        ) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    martians.add(new Martian(
                            rs.getInt(MARS_ID),
                            rs.getString("firstname"),
                            rs.getString("lastname"),
                            rs.getDate("birthdate").toLocalDate()
                    ));
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Failed to get martians.", ex);
            throw new RepositoryException("Could not get martians.");
        }

        martians.forEach(martian -> martian.addProfiles(getProfilesOfMartian(martian.getMarsId())));

        return martians;
    }

    public Martian getMartian(int marsId) {
        Martian martian;

        try (
                Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(SQL_MARTIAN_BY_MARSID)
        ) {
            stmt.setInt(1, marsId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    martian = new Martian(
                            marsId,
                            rs.getString("firstname"),
                            rs.getString("lastname"),
                            rs.getDate("birthdate").toLocalDate()
                    );
                } else {
                    martian = null;
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Failed to get martian.", ex);
            throw new RepositoryException("Could not get martian.");
        }

        if (martian != null)
            martian.addProfiles(getProfilesOfMartian(marsId));

        return martian;
    }

    public List<Profile> getProfiles() {
        Profile dbProfile = null;
        int profileCounter = 0;

        try (
                Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(SQL_GET_ALL_PROFILES)
        ) {
            try (ResultSet rs = stmt.executeQuery()) {
                List<Profile> profiles = new ArrayList<>();

                while (rs.next()) {
                    if (dbProfile != null && rs.getInt(MARS_ID) != dbProfile.getMarsId())
                        profileCounter = 0;


                    dbProfile = new Profile(
                            rs.getString(NAME),
                            rs.getInt(MARS_ID),
                            rs.getString(PRICE),
                            rs.getDate(CREATION_DATE).toLocalDate(),
                            rs.getInt(COUNTER),
                            rs.getBoolean(IN_USE)
                    );

                    profiles.add(dbProfile);
                    dbProfile.setNumber(profileCounter);

                    profileCounter++;
                }

                return profiles;
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Failed to get profiles.", ex);
            throw new RepositoryException("Could not get profiles.");
        }
    }

    public List<Profile> getProfilesOfMartian(int marsId) {
        List<Profile> profiles = new ArrayList<>();
        Profile dbProfile;
        int profileCounter = 0;

        try (
                Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(SQL_GET_PROFILE_BY_MARSID)
        ) {
            stmt.setInt(1, marsId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    dbProfile = new Profile(
                            rs.getString(NAME),
                            marsId,
                            rs.getString(PRICE),
                            rs.getDate(CREATION_DATE).toLocalDate(),
                            rs.getInt(COUNTER),
                            rs.getBoolean(IN_USE)
                    );

                    profiles.add(dbProfile);
                    dbProfile.setNumber(profileCounter);

                    profileCounter++;
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Failed to get profile.", ex);
            throw new RepositoryException("Could not get profile.");
        }

        profiles.forEach(this::addTraitsToProfile);

        return profiles;
    }


    public List<Trait> getTraitsOfMartian(int marsId) {
        // All traits WITH the default ones
        List<Trait> allTraits = new ArrayList<>();
        try (
                Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(SQL_GET_ALL_TRAITS_OF_MARTIAN);
        ) {
            stmt.setInt(1, marsId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    allTraits.add(new Trait(
                            rs.getString(NAME),
                            rs.getString(DESCRIPTION),
                            TraitType.valueOf(rs.getString("type"))
                    ));
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Failed to receive traits of martian", ex);
            throw new RepositoryException("Failed to receive traits of martian");
        }

        // Get the new traits only
        return allTraits.stream()
                .filter(trait -> !getDefaultProfileOfMartian(marsId).getTraits().contains(trait))
                .collect(Collectors.toList());
    }

    public Trait buyTrait(int marsId, String traitName) {
        if (getDefaultProfileOfMartian(marsId).getTraits().contains(getTrait(traitName)))
            throw new RepositoryException("You cannot buy a trait that is in your default profile");

        if (getTraitsOfMartian(marsId).contains(getTrait(traitName)))
            throw new RepositoryException("This martian already has this trait.");

        try (Connection con = getConnection();
             PreparedStatement stmt = con.prepareStatement(SQL_BUY_TRAIT, PreparedStatement.RETURN_GENERATED_KEYS);
        ) {
            stmt.setInt(1, marsId);
            stmt.setString(2, traitName);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Buying a new profile failed, no rows affected");
            }

            return getTrait(traitName);
        }
        catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Failed to buy a trait", ex);
            throw new RepositoryException("Failed to buy a trait");
        }
    }

    public List<Trait> getTraitsOfMartianByTraitType(int marsId, String traitType) {
        List<Trait> traits = new ArrayList<>();

        try (Connection con = getConnection();
             PreparedStatement stmt = con.prepareStatement(SQL_GET_ALL_TRAITS_OF_MARTIAN_BY_TYPE, Statement.RETURN_GENERATED_KEYS)
        ) {
            stmt.setInt(1, marsId);
            stmt.setString(2, traitType);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    traits.add(new Trait(
                            rs.getString(NAME),
                            rs.getString(DESCRIPTION),
                            TraitType.valueOf(rs.getString("type"))
                    ));
                }
            }
        }
        catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Failed to get traits of Martian", ex);
            throw new RepositoryException("Failed to get traits of Martian");
        }

        return traits;
    }

    private void addTraitsToProfile(Profile profile)
    {
        try (
                Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(SQL_GET_TRAITS_BY_PROFILE_NAME)
        ) {
            stmt.setString(1, profile.getName());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    profile.addTrait(new Trait(
                            rs.getString(NAME),
                            rs.getString(DESCRIPTION),
                            TraitType.valueOf(rs.getString("type"))
                    ));
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Failed to add traits to profile.", ex);
            throw new RepositoryException("Could not add traits to profile.");
        }
    }

    public Profile getProfileOfMartian(int marsId, String profileName) {
        Profile profile;

        try (
                Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(SQL_GET_PROFILE_BY_MARSID_AND_PROFILENAME);
        ) {
            stmt.setInt(1, marsId);
            stmt.setString(2, profileName);

            try (ResultSet rs = stmt.executeQuery())
            {
                if (rs.next())
                {
                    profile = new Profile(
                            profileName,
                            marsId,
                            rs.getString(PRICE),
                            rs.getDate(CREATION_DATE).toLocalDate(),
                            rs.getInt(COUNTER),
                            rs.getBoolean(IN_USE)
                    );

                    setProfileNumber(marsId, profile);
                    addTraitsToProfile(profile);
                }
                else profile = null;
            }
        }
        catch(SQLException ex)
        {
            LOGGER.log(Level.SEVERE, "Failed to receive profile of martian", ex);
            throw new RepositoryException("Failed to receive profile of martian");
        }

        if (profile != null)
            addTraitsToProfile(profile);

        return profile;
    }

    private void setProfileNumber(int marsId, Profile profile) {
        List<Profile> profiles = getProfilesOfMartian(marsId);

        for (int i = 0; i < profiles.size(); i++) {
            if (profiles.get(i).equals(profile)) {
                profile.setNumber(i);
            }
        }
    }

    public Trait getTrait(String traitName) {
        try (Connection con = getConnection();
             PreparedStatement stmt = con.prepareStatement(SQL_GET_TRAI_BY_NAME, PreparedStatement.RETURN_GENERATED_KEYS);
        ) {
            stmt.setString(1, traitName);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Trait(
                            rs.getString(NAME),
                            rs.getString(DESCRIPTION),
                            TraitType.valueOf(rs.getString(TYPE))
                    );
                }
            }
        }
        catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Failed to get trait by its name.", ex);
            throw new RepositoryException("Failed to get a trait by its name.");
        }

        return null;
    }

    public void storeProfileOfMartian(Profile newProfile) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT_NEW_PROFILE_OF_MARTIAN, Statement.RETURN_GENERATED_KEYS)
        )
        {
            stmt.setString(1, newProfile.getName());
            stmt.setInt(2, newProfile.getMarsId());
            stmt.setString(3, String.valueOf(newProfile.getPrice()));
            stmt.setDate(4, Date.valueOf(newProfile.getCreationDate()));
            stmt.setInt(5, newProfile.getUsages());
            stmt.setBoolean(6, newProfile.isInUse());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating the new profile failed, no rows affected");
            }

        } catch (SQLException ex)
        {
            LOGGER.log(Level.SEVERE, "Failed to create new profile", ex);
            throw new RepositoryException("Failed to create new profile");
        }
    }

    public Profile getDefaultProfileOfMartian(int marsId) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_GET_DEFAULT_PROFILE_BY_MARSID)
        ) {
            stmt.setString(1, "DEFAULT-" + marsId);
            stmt.setInt(2, marsId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Profile profile = new Profile(
                            rs.getString(NAME),
                            marsId,
                            rs.getString(PRICE),
                            rs.getDate(CREATION_DATE).toLocalDate(),
                            rs.getInt(COUNTER),
                            rs.getBoolean(IN_USE)
                    );

                    profile.setNumber(0);
                    addTraitsToProfile(profile);

                    return profile;
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Failed to get default profile.", ex);
            throw new RepositoryException("Could not get default profile.");
        }

        return null;
    }

    public Profile getActiveProfileOfMartian(int marsId) {
        List<Profile> profiles = getProfilesOfMartian(marsId);
        return profiles.stream().filter(Profile::isInUse).findFirst().orElse(null);
    }

    public List<Trait> getTraits() {
        try (
                Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(SQL_GET_TRAITS)
        ) {
            return getTraitsFromQuery(stmt);
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Failed to get traits.", ex);
            throw new RepositoryException("Could not get traits.");
        }
    }

    public List<Trait> getTraits(String traitType) {
        try (
                Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(SQL_GET_TRAITS_BY_TYPE)
        ) {
            stmt.setString(1, traitType);

            return getTraitsFromQuery(stmt);

        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, String.format("Failed to get traits with type %s", traitType), ex);
            throw new RepositoryException(String.format("Failed to get traits with type %s", traitType));
        }
    }

    private List<Trait> getTraitsFromQuery(PreparedStatement stmt) {
        List<Trait> traits = new ArrayList<>();

        try (ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                traits.add(new Trait(
                        rs.getString(NAME),
                        rs.getString(DESCRIPTION),
                        TraitType.valueOf(rs.getString("type"))
                ));
            }

            return traits;
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Failed to get traits.", ex);
            throw new RepositoryException("Could not get traits.");
        }
    }

    public List<Trait> getTraitsOfMartiansProfile(int marsId, String profileName)
    {
        Profile profile = getProfileOfMartian(marsId, profileName);
        List<Trait> traits = new ArrayList<>();

        try (
                Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(SQL_GET_TRAITS_BY_PROFILE_NAME)
        ) {
            stmt.setString(1, profile.getName());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    traits.add(new Trait(
                            rs.getString(NAME),
                            rs.getString(DESCRIPTION),
                            TraitType.valueOf(rs.getString("type"))
                    ));
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Failed to get traits of martian profile.", ex);
            throw new RepositoryException("Could not get traits of martian profile.");
        }

        return traits;
    }

    public Trait addTraitToProfile(int marsId, String profileName, String traitName) {
        if (!getTraits().contains(getTrait(traitName)))
            throw new RepositoryException("Congratulations, out of the 600+ traits we offer, you managed to pick one that's not in that list. Here's a cookie \uD83C\uDF6A");

        if (!getTraitsOfMartian(marsId).contains(getTrait(traitName)))
            throw new RepositoryException("Martian cannot add this trait to its profile, since it does not own this trait");

        if (getProfileOfMartian(marsId, profileName) == null)
            throw new RepositoryException("Cannot add a trait to a profile that does not exist.");

        if (getTraitsOfMartiansProfile(marsId, profileName).contains(getTrait(traitName)))
            throw new RepositoryException("Cannot add a trait to a profile that already has that trait.");

        if (getProfileOfMartian(marsId, profileName).getNumber() == getTraitsOfMartiansProfile(marsId, profileName).size())
            throw new RepositoryException("Cannot add more traits to profile (limit reached).");

        try (Connection con = getConnection();
             PreparedStatement stmt = con.prepareStatement(SQL_ADD_TRAIT_TO_PROFILE)
        ) {
            stmt.setString(1, profileName);
            stmt.setString(2, traitName);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0)
                throw new RepositoryException("Failed to add trait to profile, no rows affected");

            return getTrait(traitName);
        }
        catch (SQLException ex) {
            LOGGER.log(Level.INFO, "Failed to add trait to profile", ex);
            throw new RepositoryException("Failed to add trait to profile");
        }
    }

    public Trait removeTraitFromProfile(int marsId, String profileName, String traitName) {
        if (profileName.contains("DEFAULT"))
            throw new RepositoryException("Cannot remove a trait from a default profile.");

        if (getProfileOfMartian(marsId, profileName) == null)
            throw new RepositoryException("Cannot remove a trait from a profile that does not exist.");

        if (!getTraitsOfMartiansProfile(marsId, profileName).contains(getTrait(traitName)))
            throw new RepositoryException("Cannot remove this trait from this profile, since the profile does not have this trait.");

        try (Connection con = getConnection();
             PreparedStatement stmt = con.prepareStatement(SQL_REMOVE_TRAIT_FROM_PROFILE)
        ) {
            stmt.setString(1, profileName);
            stmt.setString(2, traitName);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0)
                throw new RepositoryException("Failed to remove trait from profile, no rows affected");

            return getTrait(traitName);
        }
        catch (SQLException ex) {
            LOGGER.log(Level.INFO, "Failed to remove trait from profile", ex);
            throw new RepositoryException("Failed to remove trait from profile");
        }
    }

    public List<Chip> getChips() {
        List<Chip> chips = new ArrayList<>();

        try (
                Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(SQL_GET_ALL_CHIPS)
        ) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    chips.add(new Chip(
                            rs.getInt("id"),
                            rs.getBoolean("activated")
                    ));
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Failed to get chips.", ex);
            throw new RepositoryException("Could not get chips.");
        }

        return chips;
    }

    public void setTraitsOfMartians() {
        Map<Integer, List<Trait>> allOwnedTraits = new HashMap<>();
        getMartians().forEach(martian -> {
            List<Trait> ownedTraits = new ArrayList<>();
            if (!getProfilesOfMartian(martian.getMarsId()).isEmpty())
            {
                getProfilesOfMartian(martian.getMarsId()).forEach(profile -> {
                    ownedTraits.addAll(getTraitsOfMartiansProfile(martian.getMarsId(), profile.getName()));
                    allOwnedTraits.put(martian.getMarsId(), ownedTraits);
                });
            }
        });

        emptyMartianTraits();

        allOwnedTraits.keySet().forEach(marsId ->
            allOwnedTraits.get(marsId).forEach(trait -> {
                try (
                        Connection conn = getConnection();
                        PreparedStatement stmt = conn.prepareStatement(SQL_INSERT_MARTIAN_TRAITS);
                ) {
                    stmt.setInt(1, marsId);
                    stmt.setString(2, trait.getName());

                    int affectedRows = stmt.executeUpdate();

                    if (affectedRows == 0) {
                        throw new SQLException("Adding the martian traits failed");
                    }
                } catch(SQLException ex) {
                    LOGGER.log(Level.SEVERE, "Failed to add mock data to martian trait table", ex);
                    throw new RepositoryException("Failed to add mock data to martian trait table");
                }
            })
        );
    }

    private void emptyMartianTraits()
    {
        try (
                Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(SQL_TRUNCATE_MARTIAN_TRAITS_TABLE);
        ) {
            stmt.executeUpdate();
        } catch(SQLException ex) {
            LOGGER.log(Level.SEVERE, "Failed to truncate table", ex);
            throw new RepositoryException("Failed to truncate table");
        }
    }

    public List<JsonObject> getNotifications() {
        List<JsonObject> notifications = new ArrayList<>();

        try (
                Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(SQL_GET_ALL_NOTIFICATIONS)
        ) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    notifications.add( new JsonObject( Map.of(
                            TITLE,
                            rs.getString(TITLE),
                            OPTIONS,
                            new JsonObject(rs.getString(OPTIONS))
                    )));
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Failed to get notifications.", ex);
            throw new RepositoryException("Could not get notifications.");
        }

        return notifications;
    }

    public void insertNotification(JsonObject notification) {
        try (
                Connection conn = getConnection();
                PreparedStatement stmt = conn.prepareStatement(SQL_INSERT_NOTIFICATION);
        ) {
            stmt.setString(1, notification.getString(TITLE));
            stmt.setString(2, notification.getJsonObject(OPTIONS).toString());


            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Adding the notification failed");
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Failed to add notification", ex);
            throw new RepositoryException("Failed to add notification");
        }
    }

    public Profile activateProfile(int marsId, String profileName) {
        Profile activeProfile = getActiveProfileOfMartian(marsId);

        if (activeProfile.getName().equals(profileName))
            throw new RepositoryException("Cannot activate this profile, since it's already active");

        deactivateCurrentlyActiveProfile(activeProfile);

        try (Connection con = getConnection();
             PreparedStatement stmt = con.prepareStatement(SQL_ACTIVATE_PROFILE, PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            stmt.setInt(1, marsId);
            stmt.setString(2, profileName);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) throw new RepositoryException("Failed to activate profile, no rows affected");

            increaseProfileCounter(marsId, profileName);

            return getProfileOfMartian(marsId, profileName);
        }
        catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Failed to activate profile", ex);
            throw new RepositoryException("Failed to activate profile");
        }
    }

    private void deactivateCurrentlyActiveProfile(Profile profile) {
        try (Connection con = getConnection();
             PreparedStatement stmt = con.prepareStatement(SQL_DEACTIVATE_PROFILE);
        ) {
            stmt.setInt(1, profile.getMarsId());
            stmt.setString(2, profile.getName());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) throw new SQLException("Failed to deactivate active profile, no rows affected");
        }
        catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Failed to deactivate currently active profile", ex);
            throw new RepositoryException("Failed to deactivate currently active profile");
        }
    }

    private void increaseProfileCounter(int marsId, String profileName) {
        try (Connection con = getConnection();
             PreparedStatement stmt = con.prepareStatement(SQL_UPDATE_PROFILE_COUNTER)
        ) {
            stmt.setInt(1, marsId);
            stmt.setString(2, profileName);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) throw new RepositoryException("Failed to activate profile, no rows affected");
        }
        catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Failed to update profile counter", ex);
            throw new RepositoryException("Failed to update profile counter");
        }
    }
}
