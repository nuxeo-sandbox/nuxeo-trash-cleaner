package org.nuxeo.ecm.platform.cleanup.service;

import org.nuxeo.ecm.core.api.CoreSession;

import java.util.List;

public interface TrashCleanUp {

    /**
     * Clean Up
     *
     * @param ids
     * @param session
     */
    void cleanUp(List<String> ids, CoreSession session);

    /**
     * Use workers, otherwise use the BAF
     *
     * @return
     */
    Boolean useWorkers();
}
