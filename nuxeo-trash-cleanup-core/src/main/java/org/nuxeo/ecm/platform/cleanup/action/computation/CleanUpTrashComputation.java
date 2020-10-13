package org.nuxeo.ecm.platform.cleanup.action.computation;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.bulk.action.computation.AbstractBulkComputation;
import org.nuxeo.ecm.platform.cleanup.service.TrashCleanUp;
import org.nuxeo.runtime.api.Framework;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import static org.nuxeo.ecm.platform.cleanup.action.computation.TrashCleanUpAction.ACTION_NAME;

public class CleanUpTrashComputation extends AbstractBulkComputation {

    public CleanUpTrashComputation() {
        super(ACTION_NAME);
    }

    @Override
    protected void compute(CoreSession session, List<String> ids, Map<String, Serializable> properties) {
        Framework.getService(TrashCleanUp.class).cleanUp(ids, session);
    }

}
