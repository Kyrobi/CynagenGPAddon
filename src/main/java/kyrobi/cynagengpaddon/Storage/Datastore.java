package kyrobi.cynagengpaddon.Storage;

import kyrobi.cynagengpaddon.CynagenGPAddon;
import org.bukkit.Bukkit;

import java.io.File;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class Datastore {

    private static String DB_PATH;

    public static HashMap<Integer, ClaimData> myDataStore = new HashMap<>();

    public static void initialize() {
        File file = new File("");
        DB_PATH = String.valueOf(new File(file.getAbsolutePath() + File.separator + "plugins" + File.separator + "CynagenGPAddon" + File.separator + "data.db"));

        File dbFile = new File(DB_PATH);

        String createTableSQL = "CREATE TABLE IF NOT EXISTS claims ("
                + "claimID INTEGER PRIMARY KEY NOT NULL, "
                + "creationTime INTEGER, "
                + "creator TEXT, "
                + "creatorUUID TEXT, "
                + "claimName TEXT, "
                + "allowPvP BOOLEAN, "
                + "noEnterPlayer TEXT, "
                + "enterMessage TEXT, "
                + "exitMessage TEXT"
                + ");";

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH)) {
            if (dbFile.exists()) {
                System.out.println("Database already exists. No action taken.");
            } else {
                System.out.println("A new database has been created.");
            }

            try (Statement stmt = connection.createStatement()) {
                stmt.execute(createTableSQL);
                System.out.println("Table 'claims' has been created (or already exists).");
            } catch (SQLException e) {
                System.out.println("Failed to create the table: " + e.getMessage());
            }

        } catch (SQLException e) {
            System.out.println("Failed to connect to the database: " + e.getMessage());
        }

        loadAllData();
    }

    private static void loadAllData() {
        String selectSQL = "SELECT * FROM claims";
        Bukkit.getLogger().info("Initializing CynagenGPAddon Datastore ");
        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
             PreparedStatement pstmt = connection.prepareStatement(selectSQL);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int claimID = rs.getInt("claimID");
                long creationTime = rs.getLong("creationTime");
                String creator = rs.getString("creator");
                String creatorUUID = rs.getString("creatorUUID");
                String claimName = rs.getString("claimName");
                boolean allowPvP = rs.getBoolean("allowPvP");
                String noEnterPlayer = rs.getString("noEnterPlayer");
                String enterMessage = rs.getString("enterMessage");
                String exitMessage = rs.getString("exitMessage");

                ClaimData claimData = new ClaimData(claimID, creationTime, creator, creatorUUID, claimName, allowPvP, noEnterPlayer, enterMessage, exitMessage);
                myDataStore.put(claimID, claimData);
            }

            System.out.println("All data has been loaded into myDataStore.");

        } catch (SQLException e) {
            System.out.println("Error loading data: " + e.getMessage());
        }
    }

    public static void uninitialize() {
        Bukkit.getLogger().info("Uninitializing CynagenGPAddon Datastore ");
        String insertOrReplaceSQL = "INSERT OR REPLACE INTO claims "
                + "(claimID, creationTime, creator, creatorUUID, claimName, allowPvP, noEnterPlayer, enterMessage, exitMessage) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        final int BATCH_SIZE = 1000;  // Adjust the batch size based on performance testing

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
             PreparedStatement pstmt = connection.prepareStatement(insertOrReplaceSQL)) {

            connection.setAutoCommit(false); // Start transaction

            int count = 0;
            for (Map.Entry<Integer, ClaimData> entry : myDataStore.entrySet()) {
                // Set parameters and add to batch
                pstmt.setInt(1, entry.getKey());
                pstmt.setLong(2, entry.getValue().getCreationDate());
                pstmt.setString(3, entry.getValue().getCreator());
                pstmt.setString(4, entry.getValue().getCreatorUUID());
                pstmt.setString(5, entry.getValue().getClaimName());
                pstmt.setBoolean(6, entry.getValue().isAllowPvP());
                pstmt.setString(7, entry.getValue().getNoEnterPlayerString());
                pstmt.setString(8, entry.getValue().getEnterMessage());
                pstmt.setString(9, entry.getValue().getExitMessage());
                pstmt.addBatch();

                if (++count % BATCH_SIZE == 0) {
                    pstmt.executeBatch(); // Execute batch
                }
            }

            pstmt.executeBatch(); // Execute remaining batch
            connection.commit(); // Commit transaction
            Bukkit.getLogger().info("[CynagenGPAddon] All data has been saved to the database.");

        } catch (SQLException e) {
            Bukkit.getLogger().severe("Error saving data: " + e.getMessage());
        }
    }

}
