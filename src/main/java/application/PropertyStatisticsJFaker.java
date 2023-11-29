package application;// PropertyStatisticsJFaker class
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PropertyStatisticsJFaker {
    private static final String URL = "jdbc:mysql://localhost:3306/ihl_db";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static void displayPropertyStatistics() {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT AVG(price) AS average_price FROM properties")) {

            if (resultSet.next()) {
                BigDecimal averagePrice = resultSet.getBigDecimal("average_price");

                System.out.println("Property Statistics:");
                System.out.println("Average Price: " + averagePrice);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
