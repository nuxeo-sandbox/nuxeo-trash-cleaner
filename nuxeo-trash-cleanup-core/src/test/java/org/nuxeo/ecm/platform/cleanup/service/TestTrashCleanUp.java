package org.nuxeo.ecm.platform.cleanup.service;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.automation.test.AutomationFeature;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.trash.TrashService;
import org.nuxeo.ecm.core.test.NoopRepositoryInit;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.ecm.platform.audit.AuditFeature;
import org.nuxeo.ecm.platform.audit.api.LogEntry;
import org.nuxeo.ecm.platform.audit.impl.LogEntryImpl;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.TransactionalFeature;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

@RunWith(FeaturesRunner.class)
@Features({AutomationFeature.class, AuditFeature.class})
@Deploy({
        "org.nuxeo.ecm.platform.nuxeo-trash-cleanup-core"
})
@RepositoryConfig(init = NoopRepositoryInit.class, cleanup = Granularity.METHOD)
public class TestTrashCleanUp {

    private static final int EXPECTED_YEAR = 3;
    private static final int EXPECTED_MONTH = 1;
    private static final int EXPECTED_DAYS = 2;
    private static final int EXPECTED_HOUR = 9;
    private static final int EXPECTED_SECOND = 8;

    @Inject
    CoreSession session;

    @Inject
    protected TrashCleanUp trashcleanup;

    @Inject
    protected TransactionalFeature transactionalFeature;

    @Test
    public void testService() {
        assertNotNull(trashcleanup);
    }

    @Before
    public void init() {
        DocumentModel dcm001 = session.createDocumentModel("/", "File001", "File");
        dcm001.setPropertyValue("dc:title", "File001");
        dcm001 = session.createDocument(dcm001);

        DocumentModel dcm002 = session.createDocumentModel("/", "File002", "File");
        dcm002.setPropertyValue("dc:title", "File002");
        dcm002 = session.createDocument(dcm002);

        Framework.getService(TrashService.class).trashDocument(dcm001);
        Framework.getService(TrashService.class).trashDocument(dcm002);

        transactionalFeature.nextTransaction();
    }

    @Test
    @Deploy({
            "org.nuxeo.ecm.platform.nuxeo-trash-cleanup-core:now-trashcleanup-test-config.xml"
    })
    public void shouldCallServiceDocumentsAreAllTrashed() {
        // GIVEN
        assertThat(session.query("SELECT * FROM Document WHERE ecm:isTrashed = 1")).hasSize(2);
        assertThat(session.query("SELECT * FROM Document WHERE ecm:isTrashed = 0")).hasSize(4);
        // WHEN
        final List<String> dsml = session.query("SELECT * FROM Document WHERE ecm:isTrashed = 1").stream()
                .map(DocumentModel::getId)
                .collect(Collectors.toList());
        trashcleanup.cleanUp(dsml, session);
        // THEN
        assertThat(session.query("SELECT * FROM Document WHERE ecm:isTrashed = 1")).isEmpty();
        assertThat(session.query("SELECT * FROM Document WHERE ecm:isTrashed = 0")).hasSize(4);
    }

    @Test
    @Deploy({
            "org.nuxeo.ecm.platform.nuxeo-trash-cleanup-core:now-trashcleanup-test-config.xml"
    })
    public void shouldCallServiceDocumentsSomeDocumentsHaveBeenUnTrashed() {
        // GIVEN
        final List<String> dsml = session.query("SELECT * FROM Document WHERE ecm:isTrashed = 1").stream()
                .map(DocumentModel::getId)
                .collect(Collectors.toList());

        assertThat(dsml).hasSize(2);
        assertThat(session.query("SELECT * FROM Document WHERE ecm:isTrashed = 0")).hasSize(4);

        session.query("SELECT * FROM Document WHERE ecm:isTrashed = 1").forEach(y -> {
            Framework.getService(TrashService.class).untrashDocument(session.getDocument(y.getRef()));
        });

        transactionalFeature.nextTransaction();

        assertThat(session.query("SELECT * FROM Document WHERE ecm:isTrashed = 1")).hasSize(0);
        assertThat(session.query("SELECT * FROM Document WHERE ecm:isTrashed = 0")).hasSize(6);
        // WHEN
        trashcleanup.cleanUp(dsml, session);
        // THEN
        assertThat(session.query("SELECT * FROM Document WHERE ecm:isTrashed = 1")).isEmpty();
        assertThat(session.query("SELECT * FROM Document WHERE ecm:isTrashed = 0")).hasSize(6);
    }

    @Test
    @Deploy({
            "org.nuxeo.ecm.platform.nuxeo-trash-cleanup-core:trashcleanup-test-config.xml"
    })
    public void shouldLoadContribution() {
        // GIVEN
        TrashCleanUpImpl trashcleanupImpl = ((TrashCleanUpImpl) trashcleanup);
        // WHEN
        final Integer resultDays = trashcleanupImpl.getDays();
        final Integer resultMonths = trashcleanupImpl.getMonths();
        final Integer resultSeconds = trashcleanupImpl.getSeconds();
        final Integer resultYears = trashcleanupImpl.getYears();
        final Integer resultHours = trashcleanupImpl.getHours();
        final Boolean useWorker = trashcleanupImpl.useWorkers();
        // THEN
        assertThat(resultYears).isEqualTo(EXPECTED_YEAR);
        assertThat(resultMonths).isEqualTo(EXPECTED_MONTH);
        assertThat(resultDays).isEqualTo(EXPECTED_DAYS);
        assertThat(resultHours).isEqualTo(EXPECTED_HOUR);
        assertThat(resultSeconds).isEqualTo(EXPECTED_SECOND);
        assertThat(useWorker).isTrue();
    }

    @Test
    @Deploy({
            "org.nuxeo.ecm.platform.nuxeo-trash-cleanup-core:trashcleanup-test-config.xml"
    })
    public void dateWithAddedDate() {

        // GIVEN
        Calendar cld = Calendar.getInstance();
        cld.set(2007, Calendar.FEBRUARY, 3, 0, 0, 0);
        LogEntry input = new LogEntryImpl();
        input.setLogDate(cld.getTime());
        // WHEN
        LocalDateTime result = ((TrashCleanUpImpl) trashcleanup).DATE_WITH_ADDED_CONTRIB_VALUES.apply(input);

        // <trashCleanUpConfig years="3" months="1" days="2" hours="9" minutes="4" seconds="8"/>
        Assertions.assertThat(result).isEqualTo(LocalDateTime.of(2010, 3, 5, 9, 4, 8));
    }
}
