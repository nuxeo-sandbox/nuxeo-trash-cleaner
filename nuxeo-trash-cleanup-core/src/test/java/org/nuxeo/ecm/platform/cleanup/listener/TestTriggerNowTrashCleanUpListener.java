package org.nuxeo.ecm.platform.cleanup.listener;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.automation.test.AutomationFeature;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.trash.TrashService;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventService;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.ecm.core.test.DefaultRepositoryInit;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.ecm.platform.audit.AuditFeature;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.TransactionalFeature;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.nuxeo.ecm.platform.cleanup.TrashCleanUpConstant.QUERY;

@RunWith(FeaturesRunner.class)
@Features({AutomationFeature.class, AuditFeature.class})
@RepositoryConfig(init = DefaultRepositoryInit.class, cleanup = Granularity.METHOD)
@Deploy({
        "org.nuxeo.ecm.platform.nuxeo-trash-cleanup-core",
})
public class TestTriggerNowTrashCleanUpListener {

    @Inject
    protected TransactionalFeature transactionalFeature;

    @Inject
    EventService eventService;

    @Inject
    CoreSession session;

    @Before
    public void init() {
        DocumentModel dcm003 = session.createDocumentModel("/", "File003", "File");
        dcm003.setPropertyValue("dc:title", "File003");
        dcm003 = session.createDocument(dcm003);
        session.saveDocument(dcm003);
    }

    @Test
    @Deploy("org.nuxeo.ecm.platform.nuxeo-trash-cleanup-core:now-trashcleanup-test-config.xml")
    public void shouldLookForDeletedDocumentsBAF() {
        shouldLookForDeletedDocuments();
    }

    @Test
    @Deploy("org.nuxeo.ecm.platform.nuxeo-trash-cleanup-core:now-workers-trashcleanup-test-config.xml")
    public void shouldLookForDeletedDocumentsWorkers() {
        shouldLookForDeletedDocuments();
    }

    public void shouldLookForDeletedDocuments() {
        // GIVEN
        final DocumentModel dcm003 = session.getDocument(new PathRef("/File003"));
        assertThat(dcm003).isNotNull();
        Framework.getService(TrashService.class).trashDocument(dcm003);
        transactionalFeature.nextTransaction();
        DocumentModel foundDoc = session.query(QUERY).iterator().next();
        Boolean dclm = Framework.getService(TrashService.class).isTrashed(session, foundDoc.getRef());
        assertThat(dclm).isTrue();

        // WHEN
        EventContext ctx = new DocumentEventContext(session, session.getPrincipal(), null);
        Event event = ctx.newEvent("trashCleanUp");
        event.setInline(false);
        event.setImmediate(true);
        eventService.fireEvent(event);
        eventService.waitForAsyncCompletion();
        transactionalFeature.nextTransaction();

        // THEN
        DocumentModelList foundDoc2 = session.query(QUERY);
        assertThat(foundDoc2).isEmpty();
    }
}
