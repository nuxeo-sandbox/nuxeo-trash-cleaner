package org.nuxeo.ecm.platform.cleanup.work;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.event.EventService;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.ecm.core.work.api.WorkManager;
import org.nuxeo.ecm.platform.audit.AuditFeature;
import org.nuxeo.ecm.platform.cleanup.TrashCleanUpRepositoryInit;
import org.nuxeo.ecm.platform.test.PlatformFeature;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.TransactionalFeature;

import javax.inject.Inject;

@RunWith(FeaturesRunner.class)
@Features({PlatformFeature.class, AuditFeature.class})
@Deploy({
        "org.nuxeo.ecm.platform.nuxeo-trash-cleanup-core",
        "org.nuxeo.ecm.platform.nuxeo-trash-cleanup-core:now-trashcleanup-test-config.xml"
})
@RepositoryConfig(init = TrashCleanUpRepositoryInit.class, cleanup = Granularity.METHOD)
public class TrashCleanUpWorkTest {

    private static final String SELECT_FROM_DOCUMENT_WHERE_ECM_IS_TRASHED_1 = "SELECT * FROM Document WHERE ecm:isTrashed = 1";

    @Inject
    CoreSession session;

    @Inject
    protected EventService eventService;

    @Inject
    protected TransactionalFeature transactionalFeature;

    @Test
    public void shouldDeleteSomeDocumentsTrashed() {
        // GIVEN
        Assertions.assertThat(countTrashedDocuments()).isEqualTo(6);
        // WHEN
        Framework.getService(WorkManager.class)
                .schedule(new TrashCleanUpWork(), WorkManager.Scheduling.IF_NOT_SCHEDULED, true);
        eventService.waitForAsyncCompletion();
        // THEN
        Assertions.assertThat(countTrashedDocuments()).isEqualTo(0);
    }

    protected static final Log log = LogFactory.getLog(TrashCleanUpWorkTest.class);

    protected int countTrashedDocuments() {
        // search for trashed documents
        transactionalFeature.nextTransaction();
        DocumentModelList documentModelList = session.query(SELECT_FROM_DOCUMENT_WHERE_ECM_IS_TRASHED_1);
        // To Debug
        documentModelList.forEach(x -> log.error("UID: " + x.getId() + " " + x.getName()));
        return documentModelList.size();
    }
}