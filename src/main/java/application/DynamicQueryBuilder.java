package application;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class DynamicQueryBuilder {

    private static final String URL = "jdbc:mysql://localhost:3306/ihl_db";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    private static final String SELECT_SQL = "SELECT * FROM properties WHERE id = ?";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    private static String buildConditions(String propertyType, String propertyStyle,
                                          boolean specifyPriceRange, boolean filterByLocation,
                                          String locations, boolean specifyBedrooms,
                                          int minBedrooms, boolean specifyBathrooms,
                                          int minBathrooms, boolean specifyDateRange,
                                          String startDate, String endDate) {

        StringBuilder conditionsBuilder = new StringBuilder();

        if (propertyType != null && !propertyType.isEmpty()) {
            conditionsBuilder.append("pType = '").append(propertyType).append("' AND ");
        }

        if (propertyStyle != null && !propertyStyle.isEmpty()) {
            conditionsBuilder.append("PropertyStyle = '").append(propertyStyle).append("' AND ");
        }

        // Add conditions for other columns as needed

        // Remove the trailing "AND" if conditions are present
        if (conditionsBuilder.length() > 0) {
            conditionsBuilder.setLength(conditionsBuilder.length() - 5);
        }

        return conditionsBuilder.toString();
    }

    private static String buildQuery(String columnsInput, String conditions, String orderBy) {
        StringBuilder queryBuilder = new StringBuilder("SELECT ");
        queryBuilder.append(columnsInput.equals("*") ? "*" : columnsInput);
        queryBuilder.append(" FROM properties");
        if (!conditions.isEmpty()) {
            queryBuilder.append(" WHERE ").append(conditions);
        }
        queryBuilder.append(" ").append(orderBy);
        return queryBuilder.toString();
    }

    private static void executeDynamicQuery(String finalQuery) {
        try (Connection connection = connect();
             PreparedStatement queryStatement = connection.prepareStatement(finalQuery);
             ResultSet queryResult = queryStatement.executeQuery()) {

            ResultSetMetaData metaData = queryResult.getMetaData();
            int columnCount = metaData.getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                System.out.print(metaData.getColumnName(i) + "\t");
            }
            System.out.println();

            while (queryResult.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(queryResult.getString(i) + "\t");
                }
                System.out.println();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void exportResults(ResultSet resultSet, String format) throws IOException, SQLException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Do you want to export the results to " + format + "? (Y/N): ");
        String exportChoice = scanner.next().toLowerCase();

        if (exportChoice.equals("y")) {
            String fileName = getFileName(format);
            switch (format) {
                case "text":
                    exportToTextFile(resultSet, fileName);
                    System.out.println("Results exported to " + fileName);
                    break;
                case "json":
                    exportToJSON(resultSet, fileName);
                    System.out.println("Results exported to " + fileName);
                    break;
                case "csv":
                    exportToCSV(resultSet, fileName);
                    System.out.println("Results exported to " + fileName);
                    break;
                default:
                    System.out.println("Invalid choice. No export performed.");
            }
        }
    }

    private static String getFileName(String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = dateFormat.format(new Date());

        switch (format) {
            case "text":
                return "search_results_" + timestamp + ".txt";
            case "json":
                return "search_results_" + timestamp + ".json";
            case "csv":
                return "search_results_" + timestamp + ".csv";
            default:
                throw new IllegalArgumentException("Invalid format: " + format);
        }
    }

    private static void exportToTextFile(ResultSet resultSet, String filePath) throws IOException, SQLException {
        // Implement the export to text file logic here
    }

    private static void exportToJSON(ResultSet resultSet, String filePath) throws IOException, SQLException {
        // Implement the export to JSON logic here
    }

    private static void exportToCSV(ResultSet resultSet, String filePath) throws IOException, SQLException {
        // Implement the export to CSV logic here
    }

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Dynamic Query Building and Generating Report");

            System.out.print("Enter property type (Residential-Single, Residential-Multi, Commercial): ");
            String propertyType = scanner.nextLine();

            System.out.print("Enter property style (Bungalow, Semi-Detached, Detached, Cottage, Terrace, Apartment): ");
            String propertyStyle = scanner.nextLine();

            System.out.print("Would you like to specify a price range? (yes/no): ");
            boolean specifyPriceRange = scanner.next().equalsIgnoreCase("yes");

            System.out.print("Do you want to filter by location? (yes/no): ");
            boolean filterByLocation = scanner.next().equalsIgnoreCase("yes");
            scanner.nextLine(); // Consume newline

            String locations = "";
            if (filterByLocation) {
                System.out.print("Enter desired locations, separated by commas (e.g., Dublin, Cork, Limerick): ");
                locations = scanner.nextLine();
            }

            System.out.print("Would you like to specify a number of bedrooms? (yes/no): ");
            boolean specifyBedrooms = scanner.next().equalsIgnoreCase("yes");
            int minBedrooms = 0;
            if (specifyBedrooms) {
                System.out.print("Enter the minimum number of bedrooms: ");
                minBedrooms = scanner.nextInt();
            }

            System.out.print("Would you like to specify a number of bathrooms? (yes/no): ");
            boolean specifyBathrooms = scanner.next().equalsIgnoreCase("yes");
            int minBathrooms = 0;
            if (specifyBathrooms) {
                System.out.print("Enter the minimum number of bathrooms: ");
                minBathrooms = scanner.nextInt();
            }

            System.out.print("Would you like to specify a date range of when the properties came to the market? (yes/no): ");
            boolean specifyDateRange = scanner.next().equalsIgnoreCase("yes");
            String startDate = "";
            String endDate = "";
            if (specifyDateRange) {
                System.out.print("Enter start date for added properties (YYYY-MM-DD): ");
                startDate = scanner.next();
                System.out.print("Enter end date for added properties (YYYY-MM-DD): ");
                endDate = scanner.next();
            }

            System.out.println("\nExecuting your query.....\n");

            // Build conditions based on user input
            String conditions = buildConditions(propertyType, propertyStyle, specifyPriceRange,
                    filterByLocation, locations, specifyBedrooms, minBedrooms,
                    specifyBathrooms, minBathrooms, specifyDateRange, startDate, endDate);

            // Prompt user for sorting preferences
            System.out.print("Enter column to sort by: ");
            String sortColumn = scanner.next();
            System.out.print("Enter sort order (ASC or DESC): ");
            String sortOrder = scanner.next().toUpperCase();
            String orderBy = "ORDER BY " + sortColumn + " " + sortOrder;

            // Prompt user for the format to export the results
            System.out.print("Would you like to save this search? (yes/no): ");
            boolean saveSearch = scanner.next().equalsIgnoreCase("yes");
            ResultSet resultSet = executeDynamicQuery(conditions, orderBy);
            if (saveSearch) {
                System.out.print("Please enter a filename for the search: ");
                String fileName = scanner.next();
                saveSearchToFile(resultSet, fileName);
            }

            // Display the search results
            displaySearchResults(resultSet);

            // Prompt user for exporting results
            System.out.print("Would you like to export the results? (yes/no): ");
            boolean exportResults = scanner.next().equalsIgnoreCase("yes");
            if (exportResults) {
                promptExportFormat(resultSet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static ResultSet executeDynamicQuery(String conditions, String orderBy) {
        try (Connection connection = connect()) {
            String columnsInput = "*"; // Default to select all columns
            String finalQuery = buildQuery(columnsInput, conditions, orderBy);
            return executeQuery(connection, finalQuery);
        } catch (SQLException e) {
            throw new RuntimeException("Error executing dynamic query: " + e.getMessage(), e);
        }
    }

    private static ResultSet executeQuery(Connection connection, String query) {
        try (PreparedStatement queryStatement = connection.prepareStatement(query)) {
            return queryStatement.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException("Error executing query: " + e.getMessage(), e);
        }
    }

    private static void displaySearchResults(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

        for (int i = 1; i <= columnCount; i++) {
            System.out.print(metaData.getColumnName(i) + "\t");
        }
        System.out.println();

        while (resultSet.next()) {
            for (int i = 1; i <= columnCount; i++) {
                System.out.print(resultSet.getString(i) + "\t");
            }
            System.out.println();
        }
    }

    private static void promptExportFormat(ResultSet resultSet) throws SQLException, IOException {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter the format to export (text/json/csv): ");
            String exportFormat = scanner.next().toLowerCase();

            switch (exportFormat) {
                case "text":
                    exportToTextFile(resultSet);
                    System.out.println("Results exported to text file.");
                    break;
                case "json":
                    exportToJSON(resultSet);
                    System.out.println("Results exported to JSON file.");
                    break;
                case "csv":
                    exportToCSV(resultSet);
                    System.out.println("Results exported to CSV file.");
                    break;
                default:
                    System.out.println("Invalid export format. No export performed.");
            }
        }
    }

    private static void saveSearchToFile(ResultSet resultSet, String fileName) throws SQLException, IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Write column names to the file
            for (int i = 1; i <= columnCount; i++) {
                writer.write(metaData.getColumnName(i));
                if (i < columnCount) {
                    writer.write("\t");
                }
            }
            writer.newLine();

            // Write query results to the file
            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    writer.write(resultSet.getString(i));
                    if (i < columnCount) {
                        writer.write("\t");
                    }
                }
                writer.newLine();
            }

            System.out.println("Search results saved to " + fileName);
        }
    }

    private static void exportToTextFile(ResultSet resultSet) throws IOException, SQLException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("search_results.txt"))) {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Write column names to the file
            for (int i = 1; i <= columnCount; i++) {
                writer.write(metaData.getColumnName(i));
                if (i < columnCount) {
                    writer.write("\t");
                }
            }
            writer.newLine();

            // Write query results to the file
            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    writer.write(resultSet.getString(i));
                    if (i < columnCount) {
                        writer.write("\t");
                    }
                }
                writer.newLine();
            }

            System.out.println("Results exported to search_results.txt");
        }
    }

    private static void exportToJSON(ResultSet resultSet) throws IOException, SQLException {
        // Implement the export to JSON logic here
    }

    private static void exportToCSV(ResultSet resultSet) throws IOException, SQLException {
        // Implement the export to CSV logic here
    }
}
