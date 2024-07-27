package application;

import com.github.javafaker.Faker;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Date;

public class JFakeProperties {

    public static void main(String[] args) {
        Faker faker = new Faker();

        // Specify the number of properties to generate
      int numberOfProperties = 5;

        for (int i = 0; i < numberOfProperties ; i++) {
            // Generate synthetic data for a property
            String street = faker.address().streetAddress();
            String city = faker.address().city();
            int listingNum = faker.number().numberBetween(1, 10);
            int styleId = faker.number().numberBetween(1, 11);
            int typeId = faker.number().numberBetween(1, 3);
            int bedrooms = faker.number().numberBetween(1, 10);
            int bathrooms = faker.number().numberBetween(1, 10);
            int squareFeet = faker.number().numberBetween(1, 10);
            String berRating = faker.lorem().characters(1, true, false);
            String description = faker.lorem().sentence();
            int lotSize = faker.number().numberBetween(1, 10);
            int garageSize = faker.number().numberBetween(1, 10);
            int garageId = faker.number().numberBetween(1, 3);
            int agentId = faker.number().numberBetween(1, 5);
            String photo = faker.numerify("20");
            BigDecimal price = BigDecimal.valueOf(faker.number().randomDouble(2, 1, 10000));
            Date dateAdded = new Date(faker.date().birthday().getTime());

            // Insert the generated data into the database
            insertPropertyData(street, city, listingNum, styleId, typeId, bedrooms, bathrooms,
                    squareFeet, berRating, description, lotSize, garageSize,
                    garageId, agentId, photo, price, dateAdded);
       }

        PropertyStatisticsJFaker.displayPropertyStatistics();
    }

    private static void insertPropertyData(String street, String city, int listingNum, int styleId,
                                           int typeId, int bedrooms, int bathrooms, int squareFeet,
                                           String berRating, String description, int lotSize, int garageSize,
                                           int garageId, int agentId, String photo, BigDecimal price, Date dateAdded) {
        try (Connection connection = DatabaseUtility.getConnection()) {
            String sql = "INSERT INTO properties (street, city, listingNum, styleId, typeId, bedrooms, bathrooms," +
                    "squareFeet, berRating, description, lotSize, garageSize, garageId, agentId, photo, " +
                    "price, dateAdded) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, street);
                preparedStatement.setString(2, city);
                preparedStatement.setInt(3, listingNum);
                preparedStatement.setInt(4, styleId);
                preparedStatement.setInt(5, typeId);
                preparedStatement.setInt(6, bedrooms);
                preparedStatement.setInt(7, bathrooms);
                preparedStatement.setInt(8, squareFeet);
                preparedStatement.setString(9, berRating);
                preparedStatement.setString(10, description);
                preparedStatement.setInt(11, lotSize);
                preparedStatement.setInt(12, garageSize);
                preparedStatement.setInt(13, garageId);
                preparedStatement.setInt(14, agentId);
                preparedStatement.setString(15, photo);
                preparedStatement.setBigDecimal(16, price);
                preparedStatement.setDate(17, dateAdded);
                preparedStatement.executeUpdate();
                System.out.println("Property data inserted successfully!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
