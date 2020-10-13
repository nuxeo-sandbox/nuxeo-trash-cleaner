package org.nuxeo.ecm.platform.cleanup.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.query.sql.model.OrderByExprs;
import org.nuxeo.ecm.core.query.sql.model.QueryBuilder;
import org.nuxeo.ecm.platform.audit.api.AuditQueryBuilder;
import org.nuxeo.ecm.platform.audit.api.AuditReader;
import org.nuxeo.ecm.platform.audit.api.LogEntry;
import org.nuxeo.ecm.platform.cleanup.service.extension.TrashCleanUpConfigDescriptor;
import org.nuxeo.ecm.platform.cleanup.utils.DateUtils;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.model.DefaultComponent;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static org.nuxeo.ecm.core.query.sql.model.Predicates.eq;

public class TrashCleanUpImpl extends DefaultComponent implements TrashCleanUp {

    protected static final Log log = LogFactory.getLog(TrashCleanUp.class);

    private static final String DOC_UUID = "docUUID";
    private static final String EVENT_ID = "eventId";
    private static final String DOCUMENT_TRASHED = "documentTrashed";
    private static final String EVENT_DATE = "eventDate";

    public static final String TRASH_CLEAN_UP_EP = "trashcleanup";

    protected TrashCleanUpConfigDescriptor trashCleanUpConfigDescriptor;

    @Override
    public void activate(ComponentContext context) {
        trashCleanUpConfigDescriptor = new TrashCleanUpConfigDescriptor();
    }

    @Override
    public void deactivate(ComponentContext context) {
        trashCleanUpConfigDescriptor = null;
    }

    /**
     * Component implementation.
     */
    @Override
    public void registerContribution(Object contribution, String extensionPoint, ComponentInstance contributor) {
        if (TRASH_CLEAN_UP_EP.equals(extensionPoint)) {
            trashCleanUpConfigDescriptor = (TrashCleanUpConfigDescriptor) contribution;

        } else {
            log.error("Unable to handle unknown extensionPoint " + extensionPoint);
        }
    }

    @Override
    public Boolean useWorkers() {
        return Optional.ofNullable(trashCleanUpConfigDescriptor.getUseWorker())
                .orElse(Boolean.FALSE);
    }

    @Override
    public void cleanUp(List<String> ids, CoreSession session) {
        AuditReader reader = Framework.getService(AuditReader.class);
        log.info("Start trying to delete: [" + ids + "]");
        ids.forEach(id -> {
            log.info("Start trying to delete: [" + id + "]");
            QueryBuilder builder = new AuditQueryBuilder()
                    .predicate(eq(DOC_UUID, id))
                    .and(eq(EVENT_ID, DOCUMENT_TRASHED))
                    .order(OrderByExprs.desc(EVENT_DATE));

            reader.queryLogs(builder).stream()
                    .findFirst()
                    .filter(x -> DATE_WITH_ADDED_CONTRIB_VALUES.apply(x).isBefore(LocalDateTime.now()))
                    .map(LogEntry::getDocUUID)
                    .map(IdRef::new)
                    // It could happened that a document does not exist anymore because removeDocument
                    // Removes this document and all its children, if any.
                    .filter(session::exists)
                    .filter(session::isTrashed)
                    .ifPresent(refDoc -> {
                        try {
                            session.removeDocument(refDoc);
                        } catch (NuxeoException ex) {
                            // Log when there is an error
                            log.error("The document referenced as: " + refDoc + " could not be deleted.", ex);
                        }
                    });
            log.info("Finished deleting: [" + id + "]");
        });
        session.save();
    }

    public Function<LogEntry, LocalDateTime> DATE_WITH_ADDED_CONTRIB_VALUES = (x) -> {
        Calendar logDate = Calendar.getInstance();
        logDate.setTime(x.getLogDate());
        logDate.add(Calendar.YEAR, getYears());
        logDate.add(Calendar.MONTH, getMonths());
        logDate.add(Calendar.DAY_OF_MONTH, getDays());
        logDate.add(Calendar.HOUR, getHours());
        logDate.add(Calendar.MINUTE, getMinutes());
        logDate.add(Calendar.SECOND, getSeconds());
        return DateUtils.toLocalDateTime(logDate);
    };

    protected Integer getDays() {
        return Optional.ofNullable(trashCleanUpConfigDescriptor.getDays())
                .orElse(0);
    }

    protected Integer getMonths() {
        return Optional.ofNullable(trashCleanUpConfigDescriptor.getMonths())
                .orElse(0);
    }

    protected Integer getSeconds() {
        return Optional.ofNullable(trashCleanUpConfigDescriptor.getSeconds())
                .orElse(0);
    }

    protected Integer getYears() {
        return Optional.ofNullable(trashCleanUpConfigDescriptor.getYears())
                .orElse(0);
    }

    protected Integer getHours() {
        return Optional.ofNullable(trashCleanUpConfigDescriptor.getHours())
                .orElse(0);
    }

    protected Integer getMinutes() {
        return Optional.ofNullable(trashCleanUpConfigDescriptor.getMinutes())
                .orElse(0);
    }
}
