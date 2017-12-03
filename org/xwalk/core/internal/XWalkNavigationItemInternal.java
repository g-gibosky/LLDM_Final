package org.xwalk.core.internal;

import org.chromium.content_public.browser.NavigationEntry;

@XWalkAPI(createInternally = true)
public class XWalkNavigationItemInternal implements Cloneable {
    private NavigationEntry mEntry;

    XWalkNavigationItemInternal() {
        this.mEntry = null;
    }

    XWalkNavigationItemInternal(NavigationEntry entry) {
        this.mEntry = entry;
    }

    XWalkNavigationItemInternal(XWalkNavigationItemInternal item) {
        this.mEntry = item.mEntry;
    }

    @XWalkAPI
    public String getUrl() {
        return this.mEntry.getUrl();
    }

    @XWalkAPI
    public String getOriginalUrl() {
        return this.mEntry.getOriginalUrl();
    }

    @XWalkAPI
    public String getTitle() {
        return this.mEntry.getTitle();
    }

    protected synchronized XWalkNavigationItemInternal clone() {
        return new XWalkNavigationItemInternal(this);
    }
}
