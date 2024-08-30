package kyrobi.cynagengpaddon;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Datastore {

    private String filePath;
    private String url;
    private Connection connection;

    public static void initialize() {
        File file = new File("");
        String claimNamesFile = String.valueOf(new File(file.getAbsolutePath() + File.separator + "plugins" + File.separator + "CynagenGPAddon" + File.separator + "data.db"));

        File dbFile = new File(claimNamesFile);

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

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:" + claimNamesFile)) {
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
    }

}
