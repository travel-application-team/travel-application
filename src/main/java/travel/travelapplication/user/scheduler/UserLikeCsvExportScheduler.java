package travel.travelapplication.user.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import travel.travelapplication.user.application.UserLikeCsvExportService;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserLikeCsvExportScheduler {

  private final UserLikeCsvExportService userLikeCsvExportService;

  @Scheduled(cron = "0 0 0 * * *")
  public void exportUserLikesToCsv() {
    String path = userLikeCsvExportService.exportAllUserLikes();
    log.info("User likes exported to CSV at: {}", path);
  }
}
