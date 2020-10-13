package org.nuxeo.ecm.platform.cleanup;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.trash.TrashService;
import org.nuxeo.ecm.core.test.annotations.RepositoryInit;
import org.nuxeo.runtime.api.Framework;

import java.util.Arrays;

public class TrashCleanUpRepositoryInit implements RepositoryInit {

    @Override
    public void populate(CoreSession session) {
        DocumentModel fold001 = session.createDocumentModel("/", "F1", "Folder");
        fold001.setPropertyValue("dc:title", "F1");
        fold001 = session.createDocument(fold001);

        DocumentModel fold002 = session.createDocumentModel("/F1", "F2", "Folder");
        fold002.setPropertyValue("dc:title", "F2");
        fold002 = session.createDocument(fold002);

        session.saveDocuments(Arrays.asList(fold001, fold002).toArray(new DocumentModel[]{}));

        DocumentModel dcm001 = session.createDocumentModel("/F1", "File001", "File");
        dcm001.setPropertyValue("dc:title", "File001");
        dcm001 = session.createDocument(dcm001);

        DocumentModel dcm002 = session.createDocumentModel("/F1/F2", "File002", "File");
        dcm002.setPropertyValue("dc:title", "File002");
        dcm002 = session.createDocument(dcm002);

        DocumentModel dcm003 = session.createDocumentModel("/", "File003", "File");
        dcm003.setPropertyValue("dc:title", "File003");
        dcm003 = session.createDocument(dcm003);

        DocumentModel dcm004 = session.createDocumentModel("/", "File004", "File");
        dcm004.setPropertyValue("dc:title", "File004");
        dcm004 = session.createDocument(dcm004);

        session.saveDocuments(Arrays.asList(dcm001, dcm002, dcm003, dcm004).toArray(new DocumentModel[]{}));

        Framework.getService(TrashService.class).trashDocument(fold001);
        Framework.getService(TrashService.class).trashDocument(fold002);

        Framework.getService(TrashService.class).trashDocument(dcm001);
        Framework.getService(TrashService.class).trashDocument(dcm002);
        Framework.getService(TrashService.class).trashDocument(dcm003);
        Framework.getService(TrashService.class).trashDocument(dcm004);
    }
}
