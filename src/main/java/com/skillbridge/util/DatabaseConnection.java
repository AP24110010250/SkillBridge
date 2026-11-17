package com.skillbridge.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import com.skillbridge.exception.DatabaseException;

/**
 * Manages MySQL database connections using JDBC, with .env file and environment variable support.
 */
public class DatabaseConnection {

    private static final Properties properties = new Properties();
    private static final Map<String, String> envVars = new HashMap<>();

    static {
        try {
            // Load MySQL Connector/J driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // 1. Load default properties from resources
            InputStream inputStream = DatabaseConnection.class
                    .getClassLoader()
                    .getResourceAsStream("database.properties");

            if (inputStream != null) {
                properties.load(inputStream);
            } else {
                properties.setProperty("db.url", "jdbc:mysql://localhost:3306/skillbridge_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
                properties.setProperty("db.user", "root");
                properties.setProperty("db.password", "chavathegreat");
            }

            // 2. Load .env file if present
            loadEnvFile();
        } catch (Exception e) {
            System.err.println("Error initializing database configuration: " + e.getMessage());
        }
    }

    private static void loadEnvFile() {
        File[] candidateFiles = new File[] {
                new File(".env"),
                new File("../.env")
        };
        for (File file : candidateFiles) {
            if (file.exists() && file.isFile()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        line = line.trim();
                        if (line.isEmpty() || line.startsWith("#")) continue;
                        int eqIdx = line.indexOf('=');
                        if (eqIdx > 0) {
                            String key = line.substring(0, eqIdx).trim();
                            String val = line.substring(eqIdx + 1).trim();
                            if ((val.startsWith("\"") && val.endsWith("\"")) || (val.startsWith("'") && val.endsWith("'"))) {
                                val = val.substring(1, val.length() - 1);
                            }
                            envVars.put(key, val);
                        }
                    }
                    System.out.println("Loaded database configuration from " + file.getAbsolutePath());
                    break;
                } catch (Exception ex) {
                    System.err.println("Warning: Failed to read .env file: " + ex.getMessage());
                }
            }
        }
    }

    private static String getEnvOrProperty(String key) {
        String sysVal = System.getenv(key);
        if (sysVal != null && !sysVal.isEmpty()) {
            return sysVal;
        }
        return envVars.get(key);
    }

    private DatabaseConnection() {
    }

    public static Connection getConnection() throws DatabaseException {
        try {
            String url = properties.getProperty("db.url");
            String user = properties.getProperty("db.user");
            String password = properties.getProperty("db.password");

            // Check for raw connection URI: DATABASE_URL=mysql://user:pass@host:port/database
            String rawDatabaseUrl = getEnvOrProperty("DATABASE_URL");
            if (rawDatabaseUrl != null && rawDatabaseUrl.startsWith("mysql://")) {
                parseAndApplyRawUrl(rawDatabaseUrl);
            }

            // Environment overrides
            String envUrl = getEnvOrProperty("SKILLBRIDGE_DB_URL");
            String envUser = getEnvOrProperty("SKILLBRIDGE_DB_USER");
            String envPass = getEnvOrProperty("SKILLBRIDGE_DB_PASS");

            if (envUrl != null && !envUrl.isEmpty()) {
                url = envUrl;
            }
            if (envUser != null && !envUser.isEmpty()) {
                user = envUser;
            }
            if (envPass != null) {
                password = envPass;
            }

            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to connect to database: " + e.getMessage(), e);
        }
    }

    private static void parseAndApplyRawUrl(String raw) {
        try {
            // Format: mysql://user:password@host:port/database
            String clean = raw.substring("mysql://".length());
            int atIdx = clean.indexOf('@');
            if (atIdx > 0) {
                String auth = clean.substring(0, atIdx);
                String hostAndDb = clean.substring(atIdx + 1);

                int colonIdx = auth.indexOf(':');
                if (colonIdx > 0) {
                    envVars.putIfAbsent("SKILLBRIDGE_DB_USER", auth.substring(0, colonIdx));
                    envVars.putIfAbsent("SKILLBRIDGE_DB_PASS", auth.substring(colonIdx + 1));
                } else {
                    envVars.putIfAbsent("SKILLBRIDGE_DB_USER", auth);
                }

                String jdbcUrl = "jdbc:mysql://" + hostAndDb;
                if (!jdbcUrl.contains("sslMode")) {
                    jdbcUrl += (jdbcUrl.contains("?") ? "&" : "?") + "sslMode=VERIFY_IDENTITY&useSSL=true";
                }
                envVars.putIfAbsent("SKILLBRIDGE_DB_URL", jdbcUrl);
            }
        } catch (Exception ignored) {
        }
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}
