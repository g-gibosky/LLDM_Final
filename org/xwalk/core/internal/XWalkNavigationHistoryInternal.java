package org.xwalk.core.internal;

import java.io.Serializable;
import org.chromium.content_public.browser.NavigationHistory;

@XWalkAPI(createInternally = true)
public class XWalkNavigationHistoryInternal implements Cloneable, Serializable {
    private NavigationHistory mHistory;
    private XWalkViewInternal mXWalkView;

    @XWalkAPI
    public enum DirectionInternal {
        BACKWARD,
        FORWARD
    }

    XWalkNavigationHistoryInternal() {
        this.mXWalkView = null;
        this.mHistory = null;
    }

    XWalkNavigationHistoryInternal(XWalkViewInternal view, NavigationHistory history) {
        this.mXWalkView = view;
        this.mHistory = history;
    }

    XWalkNavigationHistoryInternal(XWalkNavigationHistoryInternal history) {
        this.mXWalkView = history.mXWalkView;
        this.mHistory = history.mHistory;
    }

    @XWalkAPI
    public int size() {
        return this.mHistory.getEntryCount();
    }

    @XWalkAPI
    public boolean hasItemAt(int index) {
        return index >= 0 && index <= size() - 1;
    }

    @XWalkAPI
    public XWalkNavigationItemInternal getItemAt(int index) {
        if (index < 0 || index >= size()) {
            return null;
        }
        return new XWalkNavigationItemInternal(this.mHistory.getEntryAtIndex(index));
    }

    @XWalkAPI
    public XWalkNavigationItemInternal getCurrentItem() {
        return getItemAt(getCurrentIndex());
    }

    @XWalkAPI
    public boolean canGoBack() {
        return this.mXWalkView.canGoBack();
    }

    @XWalkAPI
    public boolean canGoForward() {
        return this.mXWalkView.canGoForward();
    }

    @XWalkAPI
    public void navigate(DirectionInternal direction, int steps) {
        switch (direction) {
            case FORWARD:
                this.mXWalkView.navigateTo(steps);
                return;
            case BACKWARD:
                this.mXWalkView.navigateTo(-steps);
                return;
            default:
                return;
        }
    }

    @XWalkAPI
    public int getCurrentIndex() {
        return this.mHistory.getCurrentEntryIndex();
    }

    @XWalkAPI
    public void clear() {
        this.mXWalkView.clearHistory();
    }

    protected synchronized XWalkNavigationHistoryInternal clone() {
        return new XWalkNavigationHistoryInternal(this);
    }
}
