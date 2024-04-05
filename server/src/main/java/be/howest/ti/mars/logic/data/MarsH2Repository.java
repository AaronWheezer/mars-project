package be.howest.ti.mars.logic.data;

import be.howest.ti.mars.logic.domain.Container;
import be.howest.ti.mars.logic.domain.Dimension;
import be.howest.ti.mars.logic.domain.Location;
import be.howest.ti.mars.logic.domain.Pallet;
import be.howest.ti.mars.logic.exceptions.RepositoryException;
import org.h2.tools.Server;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
This is only a starter class to use an H2 database.
In this start project there was no need for a Java interface MarsRepository.
Please always use interfaces when needed.

To make this class useful, please complete it with the topics seen in the module OOA & SD
 */

public class MarsH2Repository {
    private static final Logger LOGGER = Logger.getLogger(MarsH2Repository.class.getName());

    private static final String SQL_GET_CONTAINERS = "select * from containers;";
    private static final String SQL_GET_CONTAINER = "select * from containers where containerID = ?;";
    private static final String SQL_GET_CONTAINER_IDS = "SELECT containerID FROM containers";
    private static final String SQL_GET_PALLET_IDS = "SELECT palletID FROM pallets";
    private static final String SQL_INSERT_CONTAINER = "insert into containers (`containerID`,`rentprice`, `width`,`height`,`depth`) values (?,?,?,?,?);";
    private static final String SQL_GET_CONTAINERS_BY_OWNER = "select * from containers where owner = ?;";
    private static final String SQL_GET_CONTAINERS_BY_LOCATION = "select * from containers where locationName = ?;";
    private static final String SQL_GET_PALLETS_BY_OWNER = "select * from pallets where owner = ?;";
    private static final String SQL_GET_PALLETS_BY_LOCATION = "select * from pallets where locationName = ?;";
    private static final String SQL_DELETE_CONTAINER = "DELETE FROM CONTAINERS where containerID = ?;";
    private static final String SQL_DELETE_PALLET = "DELETE FROM PALLETS where palletID = ?;";
    private static final String SQL_INSERT_PALLET = "insert into pallets (`palletID`,`width`,`height`,`depth`) values (?,?,?,?);";

    private static final String SQL_GET_PALLET = "select * from pallets where palletID = ?;";

    private static final String SQL_UPDATE_PALLET_LOCATION= "UPDATE PALLETS SET locationName = ?, longitude = ?, latitude = ? WHERE palletID = ?;";
    private static final String OWNER = "owner";
    private static final String CONTAINERID = "containerID";
    private static final String PALLETID = "palletID";
    private static final String LOCATION = "location";

    private static final String LONGITUDE = "longitude";

    private static final String LATITUDE = "latitude";
    private static final String LOCATIONNAME = "locationName";

    private static final String SQL_GET_PALLETS = "select * from pallets;";
    private static final String SQL_UPDATE_CONTAINER_LOCATION = "UPDATE CONTAINERS SET locationName = ?, longitude = ?, latitude = ? WHERE containerID = ?";

    private static final String SQL_INSERT_CONTAINER_CONTENT = "insert into containerContents (`containerID`,`content`,`amount`) values (?,?,?);";
    private static final String SQL_INSERT_PALLET_CONTENT = "insert into palletContents (`palletID`,`content`,`amount`) values (?,?,?);";
    private static final String SQL_GET_CONTAINER_CONTENT = "select * from containerContents where containerID = ?";
    private static final String SQL_GET_PALLET_CONTENT = "select * from palletContents where palletID = ?";
    private static final String SQL_UPDATE_CONTAINER_CONTENT = "UPDATE containerContents SET amount= ? WHERE containerID = ? AND content = ?";
    private static final String SQL_UPDATE_PALLET_CONTENT = "UPDATE palletContents SET amount= ? WHERE palletID = ? AND content = ?";
    private static final String SQL_DELETE_CONTAINER_CONTENT = "DELETE FROM containerContents where containerID = ? AND content = ?";
    private static final String SQL_DELETE_PALLET_CONTENT = "DELETE FROM palletContents where palletID = ? AND content = ?";
    private static final String SQL_UPDATE_CONTAINER_OWNER = "UPDATE containers SET owner = ? WHERE containerID = ?";
    private static final String SQL_UPDATE_PALLET_OWNER = "UPDATE pallets SET owner = ? WHERE palletID = ?";
    private static final String SQL_GET_CONTAINER_COMMENTS = "SELECT comment FROM containerComments WHERE containerID = ?";
    private static final String SQL_GET_PALLET_COMMENTS = "SELECT comment FROM palletComments WHERE palletID = ?";
    private static final String SQL_INSERT_CONTAINER_COMMENT = "INSERT INTO containerComments (`containerID`,`comment`) values (?,?)";
    private static final String SQL_INSERT_PALLET_COMMENT = "INSERT INTO palletComments (`palletID`,`comment`) values (?,?)";
    private static final String SQL_DELETE_CONTAINER_COMMENT = "DELETE FROM containerComments where containerID = ? AND comment = ?";
    private static final String SQL_DELETE_PALLET_COMMENT = "DELETE FROM palletComments where palletID = ? AND comment = ?";
    private static final String GENERAL_ERROR_MSG_CONTAINERS = "Failed to get containers";
    private static final String GENERAL_ERROR_MSG_PALLETS = "Failed to get pallets";
    private static final String GENERAL_ERROR_MSG_CONTAINERS_NO_UPDATE = "Failed to update containers";
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

    public Set<Container> getContainers() {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_GET_CONTAINERS);
             ResultSet rs = stmt.executeQuery()) {

            Set<Container> containers = new HashSet<>();

            while (rs.next()) {
                Container container = rs2Container(rs);
                containers.add(container);
            }

            return containers;

        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, GENERAL_ERROR_MSG_CONTAINERS, ex);
            throw new RepositoryException(GENERAL_ERROR_MSG_CONTAINERS);
        }
    }

    public Container getContainer(int containerID) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_GET_CONTAINER)
        ) {
            stmt.setInt(1, containerID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs2Container(rs);
                } else {
                    return null;
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Failed to get container.", ex);
            throw new RepositoryException("Could not get container.");
        }
    }

    public Set<Pallet> getPallets() {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_GET_PALLETS);
             ResultSet rs = stmt.executeQuery()) {

        Set<Pallet> pallets = new HashSet<>();

        while (rs.next()) {
            Pallet pallet = rs2Pallet(rs);
            pallets.add(pallet);
        }

        return pallets;

    } catch (SQLException ex) {
        LOGGER.log(Level.SEVERE, GENERAL_ERROR_MSG_PALLETS, ex);
        throw new RepositoryException(GENERAL_ERROR_MSG_PALLETS);
    }
    }

    public Pallet getPallet(int palletID) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_GET_PALLET)) {

            stmt.setInt(1, palletID);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs2Pallet(rs);
            } else {
                return null;
            }

        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Failed to get pallet.", ex);
            throw new RepositoryException("Failed to get pallet.");
        }
    }

    public void insertContainer(Container container) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT_CONTAINER, Statement.RETURN_GENERATED_KEYS)) {

            int containerID = generateNonExistingID();

            stmt.setInt(1, containerID);
            stmt.setDouble(2, container.getRentprice());
            stmt.setDouble(3, container.getDimensions().getWidth());
            stmt.setDouble(4, container.getDimensions().getHeight());
            stmt.setDouble(5, container.getDimensions().getDepth());

            int affectedRows = stmt.executeUpdate();

            container.setId(containerID);

            if (affectedRows == 0) {
                throw new SQLException("Creating container failed, no rows affected.");
            }


        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Failed to create container in database.", ex);
            throw new RepositoryException("Could not create container in database.");
        }
    }

    public void insertPallet(Pallet pallet) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT_PALLET, Statement.RETURN_GENERATED_KEYS)) {

            int palletID = generateNonExistingID();

            stmt.setInt(1, palletID);
            stmt.setDouble(2, pallet.getDimensions().getWidth());
            stmt.setDouble(3, pallet.getDimensions().getHeight());
            stmt.setDouble(4, pallet.getDimensions().getDepth());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating pallet failed, no rows affected.");
            }

            pallet.setId(palletID);

        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Failed to create pallet in database.", ex);
            throw new RepositoryException("Could not create pallet in database.");
        }
    }

    public Set<Container> getContainersByFilter(String filter, String value) {
        String statement;
        if (filter.equals(OWNER)) {
            statement = SQL_GET_CONTAINERS_BY_OWNER;
        } else if (filter.equals(LOCATION)) {
            statement = SQL_GET_CONTAINERS_BY_LOCATION;
        } else {
            throw new IllegalArgumentException("This filter doesn't exist");
        }

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(statement)) {

            stmt.setString(1, value);

            ResultSet rs = stmt.executeQuery();

            Set<Container> containers = new HashSet<>();

            while (rs.next()) {
                Container container = rs2Container(rs);
                containers.add(container);
            }

            return containers;

        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, GENERAL_ERROR_MSG_CONTAINERS, ex);
            throw new RepositoryException(GENERAL_ERROR_MSG_CONTAINERS);
        }
    }


    public Set<Pallet> getPalletsByFilter(String filter, String value) {
        String statement;
        if (filter.equals(OWNER)) {
            statement = SQL_GET_PALLETS_BY_OWNER;
        } else if (filter.equals(LOCATION)) {
            statement = SQL_GET_PALLETS_BY_LOCATION;
        } else {
            throw new IllegalArgumentException("This filter doesn't exist");
        }

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(statement)) {

            stmt.setString(1, value);

            ResultSet rs = stmt.executeQuery();

            Set<Pallet> pallets = new HashSet<>();

            while (rs.next()) {
                Pallet pallet = rs2Pallet(rs);
                pallets.add(pallet);
            }

            return pallets;

        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, GENERAL_ERROR_MSG_PALLETS, ex);
            throw new RepositoryException(GENERAL_ERROR_MSG_PALLETS);
        }
    }

    public void updateContainerLocation(int containerID, Location location) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE_CONTAINER_LOCATION)) {

            stmt.setString(1, location.getLocationName());
            stmt.setDouble(2, location.getLongitude());
            stmt.setDouble(3, location.getLatitude());
            stmt.setInt(4, containerID);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Updating container location failed, no rows affected.");
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, GENERAL_ERROR_MSG_CONTAINERS_NO_UPDATE, ex);
            throw new RepositoryException(GENERAL_ERROR_MSG_CONTAINERS_NO_UPDATE);
        }
    }


    public void deleteContainer(int containerID) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE_CONTAINER)) {

            stmt.setInt(1, containerID);
            stmt.execute();

        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Failed to delete container.", ex);
            throw new RepositoryException("Failed to delete container.");
        }
    }

    public void deletePallet(int palletID) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE_PALLET)) {

            stmt.setInt(1, palletID);

            stmt.execute();

        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Failed to delete pallet.", ex);
            throw new RepositoryException("Failed to delete pallet.");
        }
    }

    public void updatePalletLocation(int palletID, Location location) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE_PALLET_LOCATION)) {

            stmt.setString(1, location.getLocationName());
            stmt.setDouble(2, location.getLongitude());
            stmt.setDouble(3, location.getLatitude());
            stmt.setInt(4, palletID);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Updating pallet location failed, no rows affected.");
            }

        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Failed to update pallet location in database.", ex);
            throw new RepositoryException("Could not update pallet location in database.");
        }
    }


    public void changeContainerOwner(int containerID, String newOwner) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE_CONTAINER_OWNER)) {

            stmt.setString(1, newOwner);
            stmt.setInt(2, containerID);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Updating container owner failed, no rows affected.");
            }

        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, GENERAL_ERROR_MSG_CONTAINERS_NO_UPDATE, ex);
            throw new RepositoryException(GENERAL_ERROR_MSG_CONTAINERS_NO_UPDATE);
        }
    }

    public void changePalletOwner(int palletID, String newOwner) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE_PALLET_OWNER)) {

            stmt.setString(1, newOwner);
            stmt.setInt(2, palletID);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Updating pallet owner failed, no rows affected.");
            }

        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Failed to update pallet owner in database.", ex);
            throw new RepositoryException("Could not update pallet owner in database.");
        }
    }

    public void addContainerContent(int containerID, Map<String, Integer> contents){
        for(Map.Entry<String, Integer> entry : contents.entrySet()) {
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(SQL_INSERT_CONTAINER_CONTENT)) {
                stmt.setInt(1, containerID);
                stmt.setString(2, entry.getKey());
                Map<String,Integer> currentContent = getContainerContent(containerID);
                if(currentContent.containsKey(entry.getKey())){
                    updateContainerContent(containerID, entry.getKey(), currentContent.get(entry.getKey()),
                            entry.getValue(), "plus");
                }else{
                    stmt.setInt(3, entry.getValue());
                    stmt.execute();
                }
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, GENERAL_ERROR_MSG_CONTAINERS_NO_UPDATE, ex);
                throw new RepositoryException(GENERAL_ERROR_MSG_CONTAINERS_NO_UPDATE);
            }
        }
    }

    public void removeContainerContent(int containerID, Map<String, Integer> contents){
        Map<String, Integer> contentsOfTargetContainer = getContainerContent(containerID);
        for(Map.Entry<String, Integer> entry : contentsOfTargetContainer.entrySet()) {
            if(contents.containsKey(entry.getKey())){
                if(entry.getValue() > contents.get(entry.getKey())){
                    updateContainerContent(containerID, entry.getKey(), entry.getValue(),
                            contents.get(entry.getKey()), "minus");
                }
                else{
                    deleteContainerContent(containerID, entry.getKey());
                }
            }
        }
    }

    public void updateContainerContent(int containerID, String key, Integer oldValue , Integer value, String operator) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE_CONTAINER_CONTENT)) {
            if(operator.equals("plus")){stmt.setInt(1, oldValue + value);}
            else{stmt.setInt(1, oldValue - value);}
            stmt.setInt(2, containerID);
            stmt.setString(3, key);
            stmt.execute();
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Updating container contents failed, no rows affected.");
            }

        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Failed to update container contents in database.", ex);
            throw new RepositoryException("Could not update container contents in database.");
        }
    }

    public void deleteContainerContent(int containerID, String content){
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE_CONTAINER_CONTENT)) {

            stmt.setInt(1, containerID);
            stmt.setString(2, content);
            stmt.execute();

        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Failed to delete container contents.", ex);
            throw new RepositoryException("Failed to delete container contents.");
        }
    }

    public void addPalletContent(int palletID, Map<String, Integer> contents) {
        for(Map.Entry<String, Integer> entry : contents.entrySet()) {
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(SQL_INSERT_PALLET_CONTENT)) {
                stmt.setInt(1, palletID);
                stmt.setString(2, entry.getKey());
                Map<String,Integer> currentContent = getPalletContent(palletID);
                if(currentContent.containsKey(entry.getKey())){
                    updatePalletContent(palletID, entry.getKey(),entry.getValue(),
                            currentContent.get(entry.getKey()), "plus");
                }else{
                    stmt.setInt(3, entry.getValue());
                    stmt.execute();
                }
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "Failed to update pallet contents in database.", ex);
                throw new RepositoryException("Could not update pallet contents in database.");
            }
        }
    }

    public void removePalletContent(int palletID, Map<String, Integer> contents){
        Map<String, Integer> contentsOfTargetPallet = getPalletContent(palletID);
        for(Map.Entry<String, Integer> entry : contentsOfTargetPallet.entrySet()) {
            if(contents.containsKey(entry.getKey())){
                if(entry.getValue() > contents.get(entry.getKey())){
                    updatePalletContent(palletID, entry.getKey(), entry.getValue(),
                            contents.get(entry.getKey()), "minus");
                }
                else{
                    deletePalletContent(palletID, entry.getKey());
                }
            }
        }
    }

    public void updatePalletContent(int palletID, String key, Integer oldValue , Integer value, String operator) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE_PALLET_CONTENT)) {
            if(operator.equals("plus")){stmt.setInt(1, oldValue + value);}
            else{stmt.setInt(1, oldValue - value);}
            stmt.setInt(2, palletID);
            stmt.setString(3, key);
            stmt.execute();
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Updating pallet contents failed, no rows affected.");
            }

        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Failed to update pallet contents in database.", ex);
            throw new RepositoryException("Could not update pallet contents in database.");
        }
    }

    public void deletePalletContent(int palletID, String content){
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE_PALLET_CONTENT)) {

            stmt.setInt(1, palletID);
            stmt.setString(2, content);
            stmt.execute();

        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Failed to delete pallet contents.", ex);
            throw new RepositoryException("Failed to delete pallet contents.");
        }
    }

    private Map<String, Integer> getContainerContent(int containerID){
        Map<String,Integer> contents = new HashMap<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_GET_CONTAINER_CONTENT)) {

            stmt.setInt(1, containerID);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                contents.put(rs.getString("content"), rs.getInt("amount"));
            }

            return contents;

        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Failed to get container contents.", ex);
            throw new RepositoryException("Could not get container contents.");
        }
    }

    private Map<String,Integer> getPalletContent(int palletID) {
        Map<String,Integer> contents = new HashMap<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_GET_PALLET_CONTENT)) {

            stmt.setInt(1, palletID);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                contents.put(rs.getString("content"), rs.getInt("amount"));
            }

            return contents;

        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Failed to get pallet contents.", ex);
            throw new RepositoryException("Could not get pallet contents.");
        }
    }

    private Set<String> getContainerComments(int containerID){
        Set<String> comments = new HashSet<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_GET_CONTAINER_COMMENTS)) {

            stmt.setInt(1, containerID);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                comments.add(rs.getString("comment"));
            }

            return comments;

        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Failed to get container comments.", ex);
            throw new RepositoryException("Could not get container comments.");
        }
    }

    public void addContainerComment(int containerID, String comment){
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT_CONTAINER_COMMENT)) {

            stmt.setInt(1, containerID);
            stmt.setString(2, comment);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating container comment failed, no rows affected.");
            }

        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Failed to create container comment in database.", ex);
            throw new RepositoryException("Could not create container comment in database.");
        }
    }

    public void removeContainerComment(int containerID, String comment){
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE_CONTAINER_COMMENT)) {

            stmt.setInt(1, containerID);
            stmt.setString(2, comment);
            stmt.execute();

        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Failed to delete container comment.", ex);
            throw new RepositoryException("Failed to delete container comment.");
        }
    }

    private Set<String> getPalletComments(int palletID){
        Set<String> comments = new HashSet<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_GET_PALLET_COMMENTS)) {

            stmt.setInt(1, palletID);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                comments.add(rs.getString("comment"));
            }

            return comments;

        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Failed to get pallet comments.", ex);
            throw new RepositoryException("Could not get pallet comments.");
        }
    }

    public void addPalletComment(int palletID, String comment){
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT_PALLET_COMMENT)) {

            stmt.setInt(1, palletID);
            stmt.setString(2, comment);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating pallet comment failed, no rows affected.");
            }

        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Failed to create pallet comment in database.", ex);
            throw new RepositoryException("Could not create pallet comment in database.");
        }
    }

    public void removePalletComment(int palletID, String comment){
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE_PALLET_COMMENT)) {

            stmt.setInt(1, palletID);
            stmt.setString(2, comment);
            stmt.execute();

        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Failed to delete pallet comment.", ex);
            throw new RepositoryException("Failed to delete pallet comment.");
        }
    }

    private int generateNonExistingID(){
        List<Integer> existingIDs = getAllExistingIds();
        int randomInt = idGenerator();
        while(existingIDs.contains(randomInt)){
            randomInt = idGenerator();
        }
        return randomInt;
    }

    private int idGenerator() {
        int desiredDigits = 8; //don't go past 8, since this will result in errors (should be a long after approx. 8 digits)
        SecureRandom random = new SecureRandom(); // Compliant for security-sensitive use cases
        double randomNumber = random.nextDouble() * random.nextDouble();
        double scale = Math.pow(10, desiredDigits);
        int idGenerated = (int) Math.round(randomNumber * scale);
        String idGeneratedStringify = Long.toString(idGenerated);
        if(idGeneratedStringify.length() < desiredDigits){
            String filler = "";
            for (int i = 0; i < desiredDigits - idGeneratedStringify.length(); i++) {
                filler = filler.concat("0");
            }
            return Integer.parseInt(idGeneratedStringify.concat(filler));
        }
        return idGenerated;
    }

    private List<Integer> getAllExistingIds(){
        List<Integer> ids = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_GET_CONTAINER_IDS);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                ids.add(rs.getInt(CONTAINERID));
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Failed to get containerIDs.", ex);
            throw new RepositoryException("Could not get containerIDs.");
        }

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_GET_PALLET_IDS);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                ids.add(rs.getInt(PALLETID));
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Failed to get palletIDs.", ex);
            throw new RepositoryException("Could not get palletIDs.");
        }

        return ids;
    }

    private Container rs2Container(ResultSet rs) throws SQLException {
        return new Container(rs.getInt(CONTAINERID), rs.getDouble("rentprice"),
                new Dimension(rs.getDouble("width"), rs.getDouble("height"), rs.getDouble("depth")),
                rs.getString(OWNER), new Location(rs.getString(LOCATIONNAME),
                rs.getDouble(LONGITUDE), rs.getDouble(LATITUDE)), getContainerContent(rs.getInt(CONTAINERID)),
                getContainerComments(rs.getInt(CONTAINERID)));
    }


    private Pallet rs2Pallet(ResultSet rs) throws SQLException {
        return new Pallet(rs.getInt(PALLETID), new Dimension(rs.getDouble("width"),
                rs.getDouble("height"), rs.getDouble("depth")),
                rs.getString(OWNER), new Location(rs.getString(LOCATIONNAME),
                rs.getDouble(LONGITUDE), rs.getDouble(LATITUDE)),getPalletContent(rs.getInt(PALLETID)),
                getPalletComments(rs.getInt(PALLETID)));
    }

    public void cleanUp() {
        if (dbWebConsole != null && dbWebConsole.isRunning(false))
            dbWebConsole.stop();

        try {
            Files.deleteIfExists(Path.of("./db-19.mv.db"));
            Files.deleteIfExists(Path.of("./db-19.trace.db"));
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
}


