package com.skillbridge.service;

import java.util.List;
import com.skillbridge.dao.AdminDao;
import com.skillbridge.dao.UserDao;
import com.skillbridge.exception.DatabaseException;
import com.skillbridge.exception.UnauthorizedAccessException;
import com.skillbridge.model.PlatformStatistics;
import com.skillbridge.model.User;
import com.skillbridge.model.UserReport;

// Service handling platform administration, user management, reports, and system statistics
public class AdminService {

    private final AdminDao adminDao;
    private final UserDao userDao;

    public AdminService() {
        this.adminDao = new AdminDao();
        this.userDao = new UserDao();
    }

    public AdminService(AdminDao adminDao, UserDao userDao) {
        this.adminDao = adminDao;
        this.userDao = userDao;
    }

    // Role-based validation guard
    private void verifyAdminRole(User caller) throws UnauthorizedAccessException {
        if (caller == null || !"ADMIN".equalsIgnoreCase(caller.getRole())) {
            throw new UnauthorizedAccessException("Administrative access denied: caller lacks ADMIN privileges");
        }
    }

    // 1. Submit a user report (accessible to any regular user)
    public UserReport reportUser(int reporterId, int reportedUserId, String reason) throws DatabaseException {
        if (reporterId == reportedUserId) {
            throw new IllegalArgumentException("Cannot report your own account");
        }
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("Report reason is required");
        }

        UserReport report = new UserReport(0, reporterId, reportedUserId, reason.trim(), "PENDING");
        adminDao.insertReport(report);
        return report;
    }

    // 2. Admin actions: View all abuse reports
    public List<UserReport> getAllReports(User adminUser) throws DatabaseException, UnauthorizedAccessException {
        verifyAdminRole(adminUser);
        return adminDao.findAllReports();
    }

    // 3. Admin actions: Resolve or dismiss report
    public boolean handleReport(User adminUser, int reportId, String newStatus) throws DatabaseException, UnauthorizedAccessException {
        verifyAdminRole(adminUser);
        return adminDao.updateReportStatus(reportId, newStatus != null ? newStatus.toUpperCase() : "RESOLVED");
    }

    // 4. Admin actions: Suspend/deactivate user account
    public boolean deactivateUser(User adminUser, int targetUserId) throws DatabaseException, UnauthorizedAccessException {
        verifyAdminRole(adminUser);
        User target = userDao.findById(targetUserId);
        if (target == null) {
            return false;
        }
        // Set reliability to 0 and bio to suspended
        target.setReliabilityScore(0.0);
        target.setBio("[ACCOUNT SUSPENDED BY ADMIN]");
        return userDao.update(target);
    }

    // 5. Admin actions: Fetch real-time platform metrics
    public PlatformStatistics getPlatformStatistics(User adminUser) throws DatabaseException, UnauthorizedAccessException {
        verifyAdminRole(adminUser);
        return adminDao.getPlatformStatistics();
    }
}
