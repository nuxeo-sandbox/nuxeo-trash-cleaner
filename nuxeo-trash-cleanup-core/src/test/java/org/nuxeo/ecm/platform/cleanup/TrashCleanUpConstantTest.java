package org.nuxeo.ecm.platform.cleanup;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.ecm.platform.test.PlatformFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.nuxeo.ecm.platform.cleanup.TrashCleanUpConstant.QUERY;

@RunWith(FeaturesRunner.class)
@Features({PlatformFeature.class})
@Deploy({"org.nuxeo.ecm.platform.nuxeo-trash-cleanup-core"})
@RepositoryConfig(init = TrashCleanUpRepositoryInit.class, cleanup = Granularity.METHOD)
public class TrashCleanUpConstantTest {

    @Inject
    CoreSession session;

    @Test
    public void shouldLookForDeletedDocuments() {
        // GIVEN
        // WHEN
        DocumentModelList result = session.query(QUERY);
        // THEN
        assertThat(result).hasSize(6);
    }
}