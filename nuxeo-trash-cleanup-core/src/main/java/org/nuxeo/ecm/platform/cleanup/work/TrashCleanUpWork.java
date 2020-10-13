package org.nuxeo.ecm.platform.cleanup.work;

import org.nuxeo.ecm.core.api.IterableQueryResult;
import org.nuxeo.ecm.core.query.QueryParseException;
import org.nuxeo.ecm.core.query.sql.NXQL;
import org.nuxeo.ecm.core.work.AbstractWork;
import org.nuxeo.ecm.platform.cleanup.TrashCleanUpConstant;
import org.nuxeo.ecm.platform.cleanup.service.TrashCleanUp;
import org.nuxeo.runtime.api.Framework;

import java.util.Collections;

@Deprecated
public class TrashCleanUpWork extends AbstractWork {

    private static final String ECM_UUID = "ecm:uuid";

    @Override
    public void work() {

        openSystemSession();
        setProgress(Progress.PROGRESS_0_PC);
        setStatus("Start");
        try (IterableQueryResult results = session.queryAndFetch(TrashCleanUpConstant.QUERY, NXQL.NXQL)) {
            results.forEach(result -> {
                String id = (String) result.get(ECM_UUID);
                Framework.getService(TrashCleanUp.class).cleanUp(Collections.singletonList(id), session);
            });
        } catch (QueryParseException e) {
            // ignore, proxies disabled
        }
        session.save();
        setProgress(Progress.PROGRESS_100_PC);
        setStatus("Done");
    }

    @Override
    public String getTitle() {
        return "TrashCleanUpWorker";
    }

    @Override
    public String getCategory() {
        return "documentMaintenance";
    }

}
