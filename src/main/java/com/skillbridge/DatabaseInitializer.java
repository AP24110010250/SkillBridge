package com.skillbridge;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.Statement;
import com.skillbridge.util.DatabaseConnection;

// Database initialization utility for executing schema and sample data scripts
public class DatabaseInitializer {

    // Execute SQL script from file path
    public static boolean executeSqlFile(String filePath) {
        System.out.println("Executing SQL script: " + filePath);
        Connection connection = null;
        Statement statement = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.createStatement();

            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String trimmed = line.trim();
                    // Skip comments
                    if (trimmed.startsWith("--") || trimmed.startsWith("/*") || trimmed.isEmpty()) {
                        continue;
                    }
                    sb.append(line).append(" ");
                    if (trimmed.endsWith(";")) {
                        String sql = sb.toString().trim();
                        if (sql.endsWith(";")) {
                            sql = sql.substring(0, sql.length() - 1);
                        }
                        if (!sql.isEmpty()) {
                            statement.execute(sql);
                        }
                        sb.setLength(0);
                    }
                }
            }

            System.out.println("Successfully executed: " + filePath);
            return true;
        } catch (Exception e) {
            System.err.println("Error reading or executing SQL file " + filePath + ": " + e.getMessage());
            return false;
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (Exception ignored) {
                }
            }
            DatabaseConnection.closeConnection(connection);
        }
    }
}
