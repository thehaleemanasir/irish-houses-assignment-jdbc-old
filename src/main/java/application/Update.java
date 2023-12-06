package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Update {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/ihl_db";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    public static void main(String[] args) {
        updateProperty();
    }

    public static void updateProperty() {
        try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
             Scanner scanner = new Scanner(System.in)) {

            System.out.print("Enter the ID of the property you wish to update: ");
            int propertyId = scanner.nextInt();
            scanner.nextLine();
///////////////////////////////////////
            String selectQuery = "SELECT * FROM properties WHERE id=?";
            String updateQuery = "UPDATE properties SET street=?, city=?, listingNum=?, styleId=?, typeId=?, bedrooms=?, " +
                    "bathrooms=?, squarefeet=?, berRating=?, description=?, lotsize=?, garagesize=?, garageId=?, " +
                    "agentId=?, photo=?, price=?, dateAdded=? WHERE id=?";

            try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery);
                 PreparedStatement updateStatement = connection.prepareStatement(updateQuery)) {

                selectStatement.setInt(1, propertyId);
                ResultSet resultSet = selectStatement.executeQuery();

                if (resultSet.next()) {
                    displayPDetails(resultSet);

                    updateStatement.setInt(18, propertyId);
                    promptUpdateAttribute("Street", resultSet.getString("street"), updateStatement, 1, scanner);
                    promptUpdateAttribute("City", resultSet.getString("city"), updateStatement, 2, scanner);

                    promptUpdateAttribute("ListingNum", String.valueOf(resultSet.getInt("listingNum")), updateStatement, 3, scanner);
                    promptUpdateAttribute("StyleId", String.valueOf(resultSet.getInt("styleId")), updateStatement, 4, scanner);
                    promptUpdateAttribute("TypeId", String.valueOf(resultSet.getInt("typeId")), updateStatement, 5, scanner);
                    promptUpdateAttribute("Bedrooms", String.valueOf(resultSet.getInt("bedrooms")), updateStatement, 6, scanner);
                    promptUpdateAttribute("Bathrooms", String.valueOf(resultSet.getFloat("bathrooms")), updateStatement, 7, scanner);
                    promptUpdateAttribute("Squarefeet", String.valueOf(resultSet.getInt("squarefeet")), updateStatement, 8, scanner);
                    promptUpdateAttribute("BerRating", resultSet.getString("berRating"), updateStatement, 9, scanner);
                    promptUpdateAttribute("Description", resultSet.getString("description"), updateStatement, 10, scanner);
                    promptUpdateAttribute("Lotsize", resultSet.getString("lotsize"), updateStatement, 11, scanner);
                    promptUpdateAttribute("Garagesize", String.valueOf(resultSet.getInt("garagesize")), updateStatement, 12, scanner);
                    promptUpdateAttribute("GarageId", String.valueOf(resultSet.getInt("garageId")), updateStatement, 13, scanner);
                    promptUpdateAttribute("AgentId", String.valueOf(resultSet.getInt("agentId")), updateStatement, 14, scanner);
                    promptUpdateAttribute("Photo", resultSet.getString("photo"), updateStatement, 15, scanner);
                    promptUpdateAttribute("Price", String.valueOf(resultSet.getDouble("price")), updateStatement, 16, scanner);
                    promptUpdateAttribute("DateAdded", resultSet.getDate("dateAdded").toString(), updateStatement, 17, scanner);



                    int rowsAffected = updateStatement.executeUpdate();
                    System.out.println(rowsAffected + " row(s) updated.");
                } else {
                    System.out.println("Property not found with ID: " + propertyId);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static PreparedStatement promptUpdateAttribute(String attributeName, String currentValue,
                                                              PreparedStatement updateStatement, int index, Scanner scanner) throws SQLException {
        System.out.print("Would you like to update " + attributeName + "? (Y/N): ");
        String updateAttribute = scanner.nextLine();
        if ("y".equalsIgnoreCase(updateAttribute)) {
            System.out.print("Enter new " + attributeName + ": ");
            String newValue = scanner.nextLine();
            updateStatement.setString(index, newValue);
        } else {
            System.out.println("Update of " + attributeName + " cancelled.");
            updateStatement.setString(index, currentValue);
        }
        return updateStatement;
    }


    private static void displayPDetails(ResultSet resultSet) throws SQLException {

        System.out.println("Current Street: " + resultSet.getString("street"));
        System.out.println("Current City: " + resultSet.getString("city"));
        System.out.println("Current ListingNum: " + resultSet.getInt("listingNum"));
        System.out.println("Current StyleId: " + resultSet.getInt("styleId"));
        System.out.println("Current TypeId: " + resultSet.getInt("typeId"));
        System.out.println("Current Bedrooms: " + resultSet.getInt("bedrooms"));
        System.out.println("Current Bathrooms: " + resultSet.getFloat("bathrooms"));
        System.out.println("Current Squarefeet: " + resultSet.getInt("squarefeet"));
        System.out.println("Current BerRating: " + resultSet.getString("berRating"));
        System.out.println("Current Description: " + resultSet.getString("description"));
        System.out.println("Current Lotsize: " + resultSet.getString("lotsize"));
        System.out.println("Current Garagesize: " + resultSet.getInt("garagesize"));
        System.out.println("Current GarageId: " + resultSet.getInt("garageId"));
        System.out.println("Current AgentId: " + resultSet.getInt("agentId"));
        System.out.println("Current Photo: " + resultSet.getString("photo"));
        System.out.println("Current Price: " + resultSet.getDouble("price"));
        System.out.println("Current DateAdded: " + resultSet.getDate("dateAdded"));
    }


}
