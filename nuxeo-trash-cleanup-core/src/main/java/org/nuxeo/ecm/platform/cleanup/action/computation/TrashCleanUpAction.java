package org.nuxeo.ecm.platform.cleanup.action.computation;

import org.nuxeo.lib.stream.computation.Topology;
import org.nuxeo.runtime.stream.StreamProcessorTopology;

import java.util.Arrays;
import java.util.Map;

import static org.nuxeo.ecm.core.bulk.BulkServiceImpl.STATUS_STREAM;
import static org.nuxeo.lib.stream.computation.AbstractComputation.INPUT_1;
import static org.nuxeo.lib.stream.computation.AbstractComputation.OUTPUT_1;

public class TrashCleanUpAction implements StreamProcessorTopology {

    public static final String ACTION_NAME = "cleanUpTrash";

    @Override
    public Topology getTopology(Map<String, String> map) {
        return Topology.builder()
                .addComputation(CleanUpTrashComputation::new,
                        Arrays.asList(INPUT_1 + ":" + ACTION_NAME, //
                                OUTPUT_1 + ":" + STATUS_STREAM))
                .build();
    }
}
