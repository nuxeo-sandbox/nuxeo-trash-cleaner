package org.nuxeo.ecm.platform.cleanup;

public class TrashCleanUpConstant {

    private TrashCleanUpConstant() {
        // Do nothing
    }

    // No need to order by path, everything is at the same level in the trash
    public static String QUERY = "SELECT ecm:uuid FROM Document WHERE ecm:isVersion = 0 AND ecm:isTrashed = 1";
}
