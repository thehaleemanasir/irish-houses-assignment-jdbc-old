package application;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class Update {

    private static final String URL = "jdbc:mysql://localhost:3306/ihl_db";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    private static final String SELECT_SQL = "SELECT * FROM properties WHERE id = ?";
    private static final String UPDATE_SQL = "UPDATE properties SET street = ?, city = ?, listingNum = ?, styleId = ?,"
            + "typeId = ?, bedrooms = ?, bathrooms = ?, squarefeet = ?, berRating = ?, description = ?, lotsize = ?,"
            + "garagesize = ?, garageId = ?, agentId = ?, photo = ?, price = ?, dateAdded = ?  WHERE id = ?";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            // Get user input for record ID
            System.out.print("Enter the record ID to update: ");
            int recordIdToUpdate = scanner.nextInt();

            // Update the record with confirmation for each field
            updatePropertyRecord(recordIdToUpdate, scanner);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void displayCurrentValue(String fieldName, ResultSet resultSet, int columnIndex) throws SQLException {
        String currentValue = resultSet.getString(columnIndex);
        System.out.println("Current " + fieldName + ": " + currentValue);
    }

    public static void updateField(String fieldName, String currentValue, PreparedStatement updateStatement, int parameterIndex, Scanner scanner) {
        try {
            System.out.print("Do you want to update the " + fieldName + "? (yes/no): ");
            String confirmation = scanner.next().toLowerCase();
            if (confirmation.equals("yes")) {
                System.out.print("Enter the new " + fieldName + ": ");
                String newValue = scanner.nextLine();
                newValue = scanner.nextLine();

                switch (fieldName) {
                    case "street", "city", "berRating", "description", "lotsize", "photo" ->
                            updateStatement.setString(parameterIndex, newValue);
                    case "listingNum", "styleId", "typeId", "bedrooms", "squarefeet", "garagesize", "garageId", "agentId", "price" -> {
                        try {
                            int intValue = Integer.parseInt(newValue);
                            updateStatement.setInt(parameterIndex, intValue);
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid input. Please enter a valid integer.");
                            return;
                        }
                    }
                    case "bathrooms" -> {
                        try {
                            float floatValue = Float.parseFloat(newValue);
                            updateStatement.setFloat(parameterIndex, floatValue);
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid input. Please enter a valid float.");
                            return;
                        }
                    }
                    case "dateAdded" -> {
                        System.out.print("Enter the new dateAdded (yyyy-MM-dd): ");
                        String dateString = scanner.next();
                        try {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            Date parsedDate = dateFormat.parse(dateString);
                            java.sql.Date sqlDate = new java.sql.Date(parsedDate.getTime());
                            updateStatement.setDate(parameterIndex, sqlDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }

                // Set the parameter for the record ID
                updateStatement.setInt(18, parameterIndex);

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

    public static void updatePropertyRecord(int id, Scanner scanner) {
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
            displayCurrentValue("Street", resultSet, 2);
            displayCurrentValue("City", resultSet, 3);
            displayCurrentValue("Listing Num", resultSet, 4);
            displayCurrentValue("Style ID", resultSet, 5);
            displayCurrentValue("Type ID", resultSet, 6);
            displayCurrentValue("Bedrooms", resultSet, 7);
            displayCurrentValue("Bathrooms", resultSet, 8);
            displayCurrentValue("Square Feet", resultSet, 9);
            displayCurrentValue("BerRating", resultSet, 10);
            displayCurrentValue("Description", resultSet, 11);
            displayCurrentValue("Lot Size", resultSet, 12);
            displayCurrentValue("Garage Size", resultSet, 13);
            displayCurrentValue("Garage ID", resultSet, 14);
            displayCurrentValue("Agent ID", resultSet, 15);
            displayCurrentValue("Photo", resultSet, 16);
            displayCurrentValue("Price", resultSet, 17);
            displayCurrentValue("Date Added", resultSet, 18);

            // Update each field
            updateField("Street", resultSet.getString("street"), updateStatement, 2, scanner);
            updateField("City", resultSet.getString("city"), updateStatement, 3, scanner);
            updateField("ListingNum", String.valueOf(resultSet.getInt("listingNum")), updateStatement, 4, scanner);
            updateField("StyleId", String.valueOf(resultSet.getInt("styleId")), updateStatement, 5, scanner);
            updateField("TypeId", String.valueOf(resultSet.getInt("typeId")), updateStatement, 6, scanner);
            updateField("Bedrooms", String.valueOf(resultSet.getInt("bedrooms")), updateStatement, 7, scanner);
            updateField("Bathrooms", String.valueOf(resultSet.getFloat("bathrooms")), updateStatement, 8, scanner);
            updateField("Squarefeet", String.valueOf(resultSet.getInt("squarefeet")), updateStatement, 9, scanner);
            updateField("BerRating", resultSet.getString("berRating"), updateStatement, 10, scanner);
            updateField("Description", resultSet.getString("description"), updateStatement, 11, scanner);
            updateField("Lotsize", resultSet.getString("lotsize"), updateStatement, 12, scanner);
            updateField("Garagesize", String.valueOf(resultSet.getInt("garagesize")), updateStatement, 13, scanner);
            updateField("GarageId", String.valueOf(resultSet.getInt("garageId")), updateStatement, 14, scanner);
            updateField("AgentId", String.valueOf(resultSet.getInt("agentId")), updateStatement, 15, scanner);
            updateField("Photo", resultSet.getString("photo"), updateStatement, 16, scanner);
            updateField("Price", String.valueOf(resultSet.getDouble("price")), updateStatement, 17, scanner);
            updateField("DateAdded", resultSet.getDate("dateAdded").toString(), updateStatement, 18, scanner);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
