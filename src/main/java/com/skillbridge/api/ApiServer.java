package com.skillbridge.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.skillbridge.matching.MatchResult;
import com.skillbridge.matching.MatchingEngine;
import com.skillbridge.model.*;
import com.skillbridge.service.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * SkillBridge REST API Server
 * Production-ready, zero-overhead HTTP server exposing all platform services
 * for deployment on Render, Docker, or Cloud Web Services.
 */
public class ApiServer {

    private static final int DEFAULT_PORT = 8080;
    private static final ObjectMapper mapper = new ObjectMapper()
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ"));

    private final UserService userService = new UserService();
    private final SkillService skillService = new SkillService();
    private final SkillCategoryService categoryService = new SkillCategoryService();
    private final MatchingEngine matchingEngine = new MatchingEngine();
    private final ExchangeService exchangeService = new ExchangeService();
    private final GamificationService gamificationService = new GamificationService();
    private final AdminService adminService = new AdminService();

    public static void main(String[] args) throws IOException {
        int port = DEFAULT_PORT;
        String envPort = System.getenv("PORT");
        if (envPort != null && !envPort.isEmpty()) {
            try {
                port = Integer.parseInt(envPort);
            } catch (NumberFormatException ignored) {}
        }

        ApiServer server = new ApiServer();
        server.start(port);
    }

    public void start(int port) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.setExecutor(java.util.concurrent.Executors.newCachedThreadPool());

        // 1. Health check & Info
        server.createContext("/api/health", exchange -> sendJson(exchange, 200, Map.of(
                "status", "UP",
                "platform", "SkillBridge Cloud REST API",
                "database", "TiDB Cloud Serverless (MySQL 8.0)",
                "timestamp", System.currentTimeMillis()
        )));

        // 2. Authentication
        server.createContext("/api/auth/login", new AuthLoginHandler());
        server.createContext("/api/auth/register", new AuthRegisterHandler());

        // 3. Skills & Categories
        server.createContext("/api/skills", new SkillsHandler());
        server.createContext("/api/categories", new CategoriesHandler());

        // 4. Smart Matching Engine
        server.createContext("/api/matches", new MatchesHandler());

        // 5. Exchange Requests & Sessions
        server.createContext("/api/exchanges", new ExchangesHandler());

        // 6. Gamification, Badges & Points
        server.createContext("/api/gamification", new GamificationHandler());

        // 7. Users & Profile
        server.createContext("/api/users", new UsersHandler());

        // 8. Messages & Live Chat
        server.createContext("/api/messages", new MessagesHandler());

        server.start();
        System.out.println("=================================================");
        System.out.println("  SkillBridge REST API Server is LIVE on port " + port);
        System.out.println("  Base URL: http://localhost:" + port + "/api");
        System.out.println("  Connected to: TiDB Cloud Database");
        System.out.println("=================================================");
    }

    // CORS & JSON Response Utility
    private static void sendJson(HttpExchange exchange, int statusCode, Object data) throws IOException {
        addCorsHeaders(exchange);
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        byte[] bytes = mapper.writeValueAsBytes(data);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static void sendError(HttpExchange exchange, int statusCode, String message) throws IOException {
        sendJson(exchange, statusCode, Map.of("error", message, "statusCode", statusCode));
    }

    private static void addCorsHeaders(HttpExchange exchange) {
        var h = exchange.getResponseHeaders();
        h.set("Access-Control-Allow-Origin", "*");
        h.set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        h.set("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With");
    }

    private static Map<String, String> parseQueryParams(String query) {
        Map<String, String> params = new HashMap<>();
        if (query == null || query.isEmpty()) return params;
        for (String pair : query.split("&")) {
            String[] kv = pair.split("=");
            if (kv.length == 2) {
                params.put(kv[0], java.net.URLDecoder.decode(kv[1], StandardCharsets.UTF_8));
            } else if (kv.length == 1) {
                params.put(kv[0], "");
            }
        }
        return params;
    }

    // HANDLERS

    private class AuthLoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendError(exchange, 405, "Method Not Allowed");
                return;
            }

            try (InputStream is = exchange.getRequestBody()) {
                Map<String, String> body = mapper.readValue(is, Map.class);
                String email = body.get("email");
                String password = body.get("password");

                User user = userService.loginUser(email, password);
                if (user != null) {
                    sendJson(exchange, 200, Map.of(
                            "success", true,
                            "user", user
                    ));
                } else {
                    sendError(exchange, 401, "Invalid email or password");
                }
            } catch (Exception e) {
                sendError(exchange, 400, "Error: " + e.getMessage());
            }
        }
    }

    private class AuthRegisterHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendError(exchange, 405, "Method Not Allowed");
                return;
            }

            try (InputStream is = exchange.getRequestBody()) {
                Map<String, String> body = mapper.readValue(is, Map.class);
                String fullName = body.get("fullName");
                String email = body.get("email");
                String password = body.get("password");
                String role = body.getOrDefault("role", "STUDENT");
                String bio = body.getOrDefault("bio", "");
                String availability = body.getOrDefault("availability", "Weekdays");

                User created = userService.registerUser(fullName, email, password, role, bio, availability);
                if (created != null) {
                    sendJson(exchange, 201, Map.of("success", true, "user", created));
                } else {
                    sendError(exchange, 400, "Registration failed: Email may already exist");
                }
            } catch (Exception e) {
                sendError(exchange, 400, "Registration error: " + e.getMessage());
            }
        }
    }

    private class SkillsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            try {
                if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                    List<Skill> skills = skillService.getAllSkills();
                    sendJson(exchange, 200, skills);
                } else if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                    try (InputStream is = exchange.getRequestBody()) {
                        Map<String, Object> body = mapper.readValue(is, Map.class);
                        String name = (String) body.get("skillName");
                        int catId = ((Number) body.get("categoryId")).intValue();
                        String desc = (String) body.getOrDefault("description", "");
                        Skill s = skillService.createSkill(catId, name, desc);
                        sendJson(exchange, 201, s);
                    }
                }
            } catch (Exception e) {
                sendError(exchange, 500, e.getMessage());
            }
        }
    }

    private class CategoriesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            try {
                List<SkillCategory> list = categoryService.getAllCategories();
                sendJson(exchange, 200, list);
            } catch (Exception e) {
                sendError(exchange, 500, e.getMessage());
            }
        }
    }

    private class MatchesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            try {
                Map<String, String> q = parseQueryParams(exchange.getRequestURI().getQuery());
                int userId = Integer.parseInt(q.getOrDefault("userId", "1"));
                List<MatchResult> matches = matchingEngine.findMatchesForUser(userId);
                sendJson(exchange, 200, matches);
            } catch (Exception e) {
                sendError(exchange, 500, "Failed to compute matches: " + e.getMessage());
            }
        }
    }

    private class ExchangesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            try {
                if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                    Map<String, String> q = parseQueryParams(exchange.getRequestURI().getQuery());
                    int userId = Integer.parseInt(q.getOrDefault("userId", "1"));
                    List<ExchangeRequest> received = exchangeService.getReceivedRequests(userId);
                    List<ExchangeRequest> sent = exchangeService.getSentRequests(userId);
                    List<ExchangeSession> sessions = exchangeService.getUserSessions(userId);
                    sendJson(exchange, 200, Map.of(
                            "receivedRequests", received,
                            "sentRequests", sent,
                            "sessions", sessions
                    ));
                } else if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                    try (InputStream is = exchange.getRequestBody()) {
                        Map<String, Object> body = mapper.readValue(is, Map.class);
                        int senderId = ((Number) body.get("senderId")).intValue();
                        int receiverId = ((Number) body.get("receiverId")).intValue();
                        int sOffered = ((Number) body.get("offeredSkillId")).intValue();
                        int sWanted = ((Number) body.get("wantedSkillId")).intValue();
                        String msg = (String) body.getOrDefault("message", "Let's exchange skills!");

                        ExchangeRequest req = exchangeService.createRequest(senderId, receiverId, sOffered, sWanted, msg);
                        sendJson(exchange, 201, req);
                    }
                }
            } catch (Exception e) {
                sendError(exchange, 500, e.getMessage());
            }
        }
    }

    private class GamificationHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            try {
                Map<String, String> q = parseQueryParams(exchange.getRequestURI().getQuery());
                int userId = Integer.parseInt(q.getOrDefault("userId", "1"));
                int points = gamificationService.getUserPoints(userId);
                List<AchievementBadge> badges = gamificationService.getUserBadges(userId);
                List<LearningGoal> goals = gamificationService.getUserGoals(userId);

                sendJson(exchange, 200, Map.of(
                        "userId", userId,
                        "points", points,
                        "level", points >= 100 ? "Level 2: Skill Apprentice" : "Level 1: Peer Explorer",
                        "badges", badges,
                        "goals", goals
                ));
            } catch (Exception e) {
                sendError(exchange, 500, e.getMessage());
            }
        }
    }

    private class UsersHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            try {
                Map<String, String> q = parseQueryParams(exchange.getRequestURI().getQuery());
                if (q.containsKey("id")) {
                    int userId = Integer.parseInt(q.get("id"));
                    User u = userService.getUserById(userId);
                    if (u != null) {
                        List<UserSkillOffered> offered = skillService.getUserOfferedSkills(userId);
                        List<UserSkillWanted> wanted = skillService.getUserWantedSkills(userId);
                        sendJson(exchange, 200, Map.of(
                                "user", u,
                                "offeredSkills", offered,
                                "wantedSkills", wanted
                        ));
                    } else {
                        sendError(exchange, 404, "User not found");
                    }
                } else {
                    List<User> users = userService.getAllUsers();
                    sendJson(exchange, 200, users);
                }
            } catch (Exception e) {
                sendError(exchange, 500, e.getMessage());
            }
        }
    }

    private class MessagesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            addCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            try {
                if ("GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                    Map<String, String> q = parseQueryParams(exchange.getRequestURI().getQuery());
                    int user1 = Integer.parseInt(q.getOrDefault("user1", "1"));
                    int user2 = Integer.parseInt(q.getOrDefault("user2", "2"));
                    List<Message> conv = exchangeService.getConversation(user1, user2);
                    sendJson(exchange, 200, conv);
                } else if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                    try (InputStream is = exchange.getRequestBody()) {
                        Map<String, Object> body = mapper.readValue(is, Map.class);
                        int senderId = ((Number) body.get("senderId")).intValue();
                        int receiverId = ((Number) body.get("receiverId")).intValue();
                        String text = (String) body.get("messageText");
                        Message sent = exchangeService.sendMessage(senderId, receiverId, text);
                        sendJson(exchange, 201, sent);
                    }
                }
            } catch (Exception e) {
                sendError(exchange, 500, e.getMessage());
            }
        }
    }
}
