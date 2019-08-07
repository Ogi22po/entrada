package nl.sidnlabs.entrada;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import lombok.extern.log4j.Log4j2;
import nl.sidnlabs.entrada.service.ArchiveService;

@Log4j2
@Component
public class ScheduledMaintenance {

  private ArchiveService archiveService;
  private SharedContext sharedContext;

  public ScheduledMaintenance(ArchiveService archiveService, SharedContext sharedContext) {
    this.archiveService = archiveService;
    this.sharedContext = sharedContext;
  }

  /**
   * Execute maintenance every x minutes but wait 1 minute before enabling the schedule
   */
  @Scheduled(fixedDelayString = "#{${entrada.maintenance.interval:3600}*60*1000}",
      initialDelay = 60 * 1000)
  public void run() {
    log.info("Start maintenance");

    if (!sharedContext.isEnabled()) {
      // processing not enabled
      log.info("Maintenance is currently not enabled");
      return;
    }

    sharedContext.setMaintenanceStatus(true);

    // clean file table, prevent building up a huge history
    archiveService.clean();

    sharedContext.setMaintenanceStatus(false);

    log.info("Finished maintenance");
  }

}