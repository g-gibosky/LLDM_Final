package org.xwalk.core.internal;

@XWalkAPI(noInstance = true)
public class XWalkViewDatabaseInternal {
    @XWalkAPI
    public static synchronized boolean hasFormData() {
        boolean hasFormData;
        synchronized (XWalkViewDatabaseInternal.class) {
            hasFormData = XWalkFormDatabase.hasFormData();
        }
        return hasFormData;
    }

    @XWalkAPI(reservable = true)
    public static synchronized void clearFormData() {
        synchronized (XWalkViewDatabaseInternal.class) {
            XWalkFormDatabase.clearFormData();
        }
    }
}
