package com.skillbridge.util;

import java.util.ArrayList;
import java.util.List;

// Background notification worker demonstrating Multithreading, Runnable, and Synchronization
public class NotificationManager implements Runnable {

    private static NotificationManager instance;
    private final List<String> notificationQueue;
    private volatile boolean running;

    private NotificationManager() {
        this.notificationQueue = new ArrayList<>();
        this.running = true;
    }

    // Thread-safe Singleton access
    public static synchronized NotificationManager getInstance() {
        if (instance == null) {
            instance = new NotificationManager();
            Thread workerThread = new Thread(instance, "SkillBridge-NotificationThread");
            workerThread.setDaemon(true);
            workerThread.start();
        }
        return instance;
    }

    // Synchronized method demonstrating safe concurrent access across threads
    public synchronized void sendNotification(String notification) {
        if (notification != null && !notification.trim().isEmpty()) {
            notificationQueue.add(notification.trim());
            notify(); // Wake up worker thread
        }
    }

    // Synchronized method to retrieve pending notifications
    public synchronized List<String> getPendingNotifications() {
        return new ArrayList<>(notificationQueue);
    }

    @Override
    public void run() {
        while (running) {
            String pendingTask = null;
            synchronized (this) {
                while (notificationQueue.isEmpty() && running) {
                    try {
                        wait(5000); // Wait for new notifications or periodic check
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
                if (!notificationQueue.isEmpty()) {
                    pendingTask = notificationQueue.remove(0);
                }
            }

            if (pendingTask != null) {
                // Simulate processing/dispatching notification
                System.out.println("[Background Notification Worker] Dispatched: " + pendingTask);
            }
        }
    }

    public void stop() {
        this.running = false;
        synchronized (this) {
            notifyAll();
        }
    }
}
