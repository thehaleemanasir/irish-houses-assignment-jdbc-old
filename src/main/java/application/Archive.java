package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Archive {

    private static final String URL = "jdbc:mysql://localhost:3306/ihl_db";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            // Take user input for the record ID to archive or unarchive
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter the ID of the record to archive or unarchive: ");
            int recordId = scanner.nextInt();

            // Check if the user wants to archive or unarchive
            System.out.print("Do you want to archive (A) or unarchive (U) the record? ");
            String action = scanner.next().toUpperCase();

            if (action.equals("A")) {
                // Archive the specified record
                archiveRecord(connection, recordId);
            } else if (action.equals("U")) {
                // Unarchive the specified record
                unarchiveRecord(connection, recordId);
            } else {
                System.out.println("Invalid action. Please enter 'A' for archive or 'U' for unarchive.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void archiveRecord(Connection connection, int id) {
        // Query to retrieve the record with the specified ID
        String selectQuery = "SELECT * FROM properties WHERE id = ?";
        try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
            selectStatement.setInt(1, id);

            try (ResultSet resultSet = selectStatement.executeQuery()) {
                // Check if the record with the specified ID exists
                if (resultSet.next()) {
                    // Retrieve data from the result set
                    int propertyID = resultSet.getInt("id");
                    String street = resultSet.getString("street");
                    String city = resultSet.getString("city");

                    // Your code to archive or process the record goes here
                    //E.g. you could insert it into an archive table
                    archiveRecord(connection, id, propertyID, street, city);
                } else {
                    System.out.println("Record with ID " + id + " not found.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void archiveRecord(Connection connection, int id, int propertyID, String street, String city) {

        // Your code to archive the record, for example, insert it into an archive table
        String archiveQuery = "INSERT INTO archiveproperty (id, propertyID, street, city) VALUES (?, ?, ?, ?)";
        try (PreparedStatement archiveStatement = connection.prepareStatement(archiveQuery)) {
            archiveStatement.setInt(1, id);
            archiveStatement.setInt(2, propertyID);
            archiveStatement.setString(3, street);
            archiveStatement.setString(4, city);

            archiveStatement.executeUpdate();
            System.out.println("Record with ID " + id + " archived into the table successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void unarchiveRecord(Connection connection, int id) {
        System.out.println("Trying to unarchive record with ID: " + id); // To check if it works.
        // Your code to unarchive the record, for example, move it back to the original table
        String unarchiveQuery = "DELETE FROM archiveproperty WHERE id = ?";
        try (PreparedStatement unarchiveStatement = connection.prepareStatement(unarchiveQuery)) {
            unarchiveStatement.setInt(1, id);

            int rowsAffected = unarchiveStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Record with ID " + id + " unarchived successfully.");
            } else {
                System.out.println("Record with ID " + id + " not found in the archive.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
