package application;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class Update {

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    private static final String URL = "jdbc:mysql://localhost:3306/ihl_db";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            // Get user input for record ID
            System.out.print("Enter the record ID to update: ");
            int recordIdToUpdate = scanner.nextInt();

            // Update the record with confirmation for each field
            updateRecord(recordIdToUpdate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static final String SELECT_SQL = "SELECT * FROM properties WHERE id = ?";
    private static final String UPDATE_SQL = "UPDATE properties SET street = ?, city = ?, listingNum = ?, styleId = ?,"
            + "typeId = ?, bedrooms = ?, bathrooms = ?, squarefeet = ?, berRating= ?, description = ?, lotsize = ?,"
            + "garagesize= ?, garageId = ?, agentId = ?, photo = ?, price = ?, dateAdded = ?  WHERE id = ?";



    public static void displayCurrentValue(String fieldName, ResultSet resultSet, int columnIndex) throws SQLException {
        String currentValue = resultSet.getString(columnIndex);
        System.out.println("Current " + fieldName + ": " + currentValue);
    }

    public static void updateField(String fieldName, PreparedStatement updateStatement, int id, Scanner scanner) {
        try {
            // Ask for confirmation to update the field
            System.out.print("Do you want to update the " + fieldName + "? (yes/no): ");
            String confirmation = scanner.next().toLowerCase();
            if (confirmation.equals("yes")) {
                System.out.print("Enter the new " + fieldName + ": ");
                String newValue = scanner.nextLine(); // Consume the newline character
                newValue = scanner.nextLine(); // Read the actual input

                // Set the parameter based on the field name
                switch (fieldName) {
                    case "street", "city", "berRating", "description", "lotsize", "photo" ->
                            updateStatement.setString(1, newValue);
                    case "listingNum", "styleId", "typeId", "bedrooms", "squarefeet", "garagesize", "garageId", "agentId", "price" ->
                            updateStatement.setInt(2, Integer.parseInt(newValue));
                    case "dateAdded" -> {
                        System.out.print("Enter the new dateAdded (yyyy-MM-dd): ");
                        String dateString = scanner.next();
                        try {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            Date parsedDate = dateFormat.parse(dateString);
                            java.sql.Date sqlDate = new java.sql.Date(parsedDate.getTime());

                            updateStatement.setDate(3, sqlDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }

                updateStatement.setInt(1, id);

                int rowsAffected = updateStatement.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println(fieldName + " updated successfully.");
                } else {
                    System.out.println(fieldName + " couldn't be updated.");
                }
            } else {
                System.out.println(fieldName + " update canceled by the user.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateRecord(int id) {
        try (Connection connection = connect();
             PreparedStatement selectStatement = connection.prepareStatement(SELECT_SQL);
             PreparedStatement updateStatement = connection.prepareStatement(UPDATE_SQL)) {

            // Check if the record exists
            selectStatement.setInt(1, id);
            ResultSet resultSet = selectStatement.executeQuery();
            if (!resultSet.next()) {
                System.out.println("Record with ID " + id + " not found.");
                return;
            }

            // Display current values for each field
            String[] fieldNames = {"Street", "City", "Listing Num", "Style ID", "Type ID", "Bedrooms", "Bathrooms",
                    "Square Feet", "BerRating", "Description", "Lot Size", "Garage Size", "Garage ID", "Agent ID",
                    "Photo", "Price", "Date Added"};

            for (int i = 2; i <= 18; i++) {
                displayCurrentValue(fieldNames[i - 2], resultSet, i);
            }

            // Update each field
            for (String fieldName : fieldNames) {
                updateField(fieldName, updateStatement, id, new Scanner(System.in));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
