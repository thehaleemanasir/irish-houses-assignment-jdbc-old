package application;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.Scanner;

public class DynamicSQLQuery {

    public static void main(String[] args) throws SQLException {
        try (Connection connection = DatabaseUtility.getConnection()) {
            // Step 1: Prompt user for columns
            System.out.println("Enter columns to select (comma-separated or * for all): ");
            Scanner scanner = new Scanner(System.in);
            String columns = scanner.nextLine();

            // Step 2: Ask for filtering conditions
            System.out.println("Do you want to add filtering conditions? (y/n): ");
            String filterChoice = scanner.nextLine();
            String conditions = "";
            if (filterChoice.equalsIgnoreCase("y")) {
                System.out.println("Enter filtering conditions (e.g., bedrooms > 3, city = 'Limerick'): ");
                conditions = scanner.nextLine();
            }

            // Step 3: Allow for multiple conditions
            while (true) {
                System.out.println("Do you want to add more conditions? (y/n): ");
                String moreConditionsChoice = scanner.nextLine();
                if (moreConditionsChoice.equalsIgnoreCase("y")) {
                    System.out.println("Enter additional condition: ");
                    conditions += " AND " + scanner.nextLine();
                } else {
                    break;
                }
            }

            // Step 4: Query for sorting preferences
            System.out.println("Do you want to add sorting preferences? (y/n): ");
            String sortChoice = scanner.nextLine();
            String orderBy = "";
            if (sortChoice.equalsIgnoreCase("y")) {
                System.out.println("Enter column to sort by: ");
                String sortBy = scanner.nextLine();
                System.out.println("Enter sort order (ASC or DESC): ");
                String sortOrder = scanner.nextLine();
                orderBy = " ORDER BY " + sortBy + " " + sortOrder;
            }

            // Step 5: Construct the SQL query string
            String query = "SELECT " + columns + " FROM properties";
            if (!conditions.isEmpty()) {
                query += " WHERE " + conditions;
            }
            query += orderBy;

            // Step 6: Execute the constructed query
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(query)) {

                // Step 7: Prompt user for file format
                System.out.println("Choose the file format to save the results (1: Text, 2: JSON, 3: CSV): ");
                int fileFormatChoice = scanner.nextInt();

                // Step 8: Save results in the selected format
                switch (fileFormatChoice) {
                    case 1:
                        saveAsTextFile(resultSet);
                        break;
                    case 2:
                        saveAsJsonFile(resultSet);
                        break;
                    case 3:
                        saveAsCsvFile(resultSet);
                        break;
                    default:
                        System.out.println("Invalid choice. Exiting.");
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static void saveAsTextFile(ResultSet resultSet) {
        try (FileWriter fileWriter = new FileWriter("query_results.txt")) {
            // Write column names
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                fileWriter.write(metaData.getColumnName(i) + "\t");
            }
            fileWriter.write("\n");

            // Write query results
            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    fileWriter.write(resultSet.getString(i) + "\t");
                }
                fileWriter.write("\n");
            }
            System.out.println("Results saved to query_results.txt.");
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private static void saveAsJsonFile(ResultSet resultSet) {
        try {
            exportToJSON(resultSet, "query_results.json");
            System.out.println("Results saved to query_results.json.");
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private static void exportToJSON(ResultSet resultSet, String filePath) throws IOException, SQLException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            writer.write("[");
            while (resultSet.next()) {
                writer.write("{");
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    String columnValue = resultSet.getString(i);
                    writer.write("\"" + columnName + "\": \"" + columnValue + "\"");
                    if (i < columnCount) {
                        writer.write(", ");
                    }
                }
                writer.write("}");
                if (!resultSet.isLast()) {
                    writer.write(", ");
                }
            }
            writer.write("]");
        }
    }
    private static void saveAsCsvFile(ResultSet resultSet) {
        try (FileWriter fileWriter = new FileWriter("query_results.csv")) {
            // Write column names
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                fileWriter.write(metaData.getColumnName(i) + ",");
            }
            fileWriter.write("\n");

            // Write query results
            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    fileWriter.write(resultSet.getString(i) + ",");
                }
                fileWriter.write("\n");
            }
            System.out.println("Results saved to query_results.csv.");
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }
}
