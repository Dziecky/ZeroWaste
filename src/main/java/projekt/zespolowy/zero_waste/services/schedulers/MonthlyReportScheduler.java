package projekt.zespolowy.zero_waste.services.schedulers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.services.MonthlyReportService;
import projekt.zespolowy.zero_waste.services.UserService;

@Component
public class MonthlyReportScheduler {

    @Autowired
    private MonthlyReportService monthlyReportService;

    @Autowired
    private UserService userService;

    @Scheduled(cron = "0 52 13 19 * ?")
    public void scheduleMonthlyReports() {
        Long adminId = 10L; // zmień przy testowaniu na swój id
        User admin = userService.getAdminById(adminId);
        monthlyReportService.generateAndSendMonthlyReports(admin);
    }

}
