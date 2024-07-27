package application;// PropertyStatisticsJFaker class

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PropertyStatisticsJFaker {

    public static void displayPropertyStatistics() throws SQLException 
    {
        try (Connection connection = DatabaseUtility.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT AVG(price) AS average_price FROM properties")) {

            if (resultSet.next()) {
                BigDecimal averagePrice = resultSet.getBigDecimal("average_price");

                System.out.println("Property Statistics:");
                System.out.println("Average Price: " + averagePrice);
            }
        }
    }
}
