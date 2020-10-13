package org.nuxeo.ecm.platform.cleanup.listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.security.SecurityConstants;
import org.nuxeo.ecm.core.bulk.BulkService;
import org.nuxeo.ecm.core.bulk.message.BulkCommand;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.repository.RepositoryService;
import org.nuxeo.ecm.core.work.api.WorkManager;
import org.nuxeo.ecm.platform.cleanup.action.computation.TrashCleanUpAction;
import org.nuxeo.ecm.platform.cleanup.service.TrashCleanUp;
import org.nuxeo.ecm.platform.cleanup.work.TrashCleanUpWork;
import org.nuxeo.runtime.api.Framework;

import static org.nuxeo.ecm.platform.cleanup.TrashCleanUpConstant.QUERY;

public class TrashCleanUpListener implements EventListener {

    protected static final Log log = LogFactory.getLog(TrashCleanUpListener.class);

    @Override
    public void handleEvent(Event event) {
        log.info("Trash Clean Up Event triggered");

        TrashCleanUp trashCleanUp = Framework.getService(TrashCleanUp.class);

        // Use Worker
        if (trashCleanUp.useWorkers()) {
            log.info("Trash Clean Up Event with Workers");
            Framework.getService(WorkManager.class)
                    .schedule(new TrashCleanUpWork(), WorkManager.Scheduling.IF_NOT_SCHEDULED, true);
        } else {
            // Use BAF
            log.info("Trash Clean Up Event with BAF");
            BulkService bulkService = Framework.getService(BulkService.class);
            RepositoryService repositoryService = Framework.getService(RepositoryService.class);
            for (String repositoryName : repositoryService.getRepositoryNames()) {
                BulkCommand command = new BulkCommand
                        .Builder(TrashCleanUpAction.ACTION_NAME, QUERY)
                        .user(SecurityConstants.SYSTEM_USERNAME)
                        .repository(repositoryName)
                        .build();
                bulkService.submit(command);
            }
        }
    }
}
