package com.skillbridge;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import com.skillbridge.exception.UnauthorizedAccessException;
import com.skillbridge.model.Admin;
import com.skillbridge.model.PlatformStatistics;
import com.skillbridge.model.Student;
import com.skillbridge.model.User;
import com.skillbridge.model.UserReport;
import com.skillbridge.service.AdminService;
import com.skillbridge.service.UserService;

public class AdminServiceTest {

    @Test
    public void testAdminSecurityAndPlatformManagement() throws Exception {
        AdminService adminService = new AdminService();
        UserService userService = new UserService();

        // 1. Setup Admin user (User 4 from seed data) and regular Student user (User 1)
        User adminUser = userService.getUserById(4);
        assertNotNull(adminUser);
        assertEquals("ADMIN", adminUser.getRole());

        User regularStudent = userService.getUserById(1);
        assertNotNull(regularStudent);
        assertEquals("STUDENT", regularStudent.getRole());

        // 2. Security Check: Non-admin trying to access statistics or reports must throw UnauthorizedAccessException
        assertThrows(UnauthorizedAccessException.class, () -> {
            adminService.getPlatformStatistics(regularStudent);
        });

        assertThrows(UnauthorizedAccessException.class, () -> {
            adminService.getAllReports(regularStudent);
        });

        // 3. Regular student reports another user
        UserReport report = adminService.reportUser(1, 3, "Spamming exchange requests repeatedly");
        assertNotNull(report);
        assertTrue(report.getReportId() > 0);
        assertEquals("PENDING", report.getStatus());

        // 4. Admin reviews reports and updates status
        List<UserReport> reports = adminService.getAllReports(adminUser);
        assertFalse(reports.isEmpty());

        boolean handled = adminService.handleReport(adminUser, report.getReportId(), "RESOLVED");
        assertTrue(handled);

        // 5. Admin retrieves platform metrics
        PlatformStatistics stats = adminService.getPlatformStatistics(adminUser);
        assertNotNull(stats);
        assertTrue(stats.getTotalUsers() >= 4);
        assertTrue(stats.getTotalSkills() >= 4);
        assertNotNull(stats.getCategoryDistribution());
        assertFalse(stats.getCategoryDistribution().isEmpty());
    }
}
