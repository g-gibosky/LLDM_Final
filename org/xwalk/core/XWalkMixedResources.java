package org.xwalk.core;

import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;

class XWalkMixedResources extends Resources {
    private Resources mLibraryResource;

    private boolean isCalledInLibrary() {
        for (StackTraceElement stack : Thread.currentThread().getStackTrace()) {
            String className = stack.getClassName();
            if (className.startsWith("org.chromium") || className.startsWith("org.xwalk.core.internal")) {
                return true;
            }
            if (className.startsWith(XWalkLibraryInterface.XWALK_CORE_PACKAGE) && !className.endsWith("XWalkMixedResources")) {
                return false;
            }
        }
        return false;
    }

    XWalkMixedResources(Resources base, Resources libraryResources) {
        super(base.getAssets(), base.getDisplayMetrics(), base.getConfiguration());
        this.mLibraryResource = libraryResources;
    }

    public CharSequence getText(int id) throws NotFoundException {
        boolean calledInLibrary = isCalledInLibrary();
        if (!calledInLibrary) {
            return super.getText(id);
        }
        try {
            return this.mLibraryResource.getText(id);
        } catch (NotFoundException e) {
            if (calledInLibrary) {
                return super.getText(id);
            }
            return this.mLibraryResource.getText(id);
        }
    }

    public XmlResourceParser getLayout(int id) throws NotFoundException {
        boolean calledInLibrary = isCalledInLibrary();
        if (!calledInLibrary) {
            return super.getLayout(id);
        }
        try {
            return this.mLibraryResource.getLayout(id);
        } catch (NotFoundException e) {
            if (calledInLibrary) {
                return super.getLayout(id);
            }
            return this.mLibraryResource.getLayout(id);
        }
    }

    public void getValue(int id, TypedValue outValue, boolean resolveRefs) {
        boolean calledInLibrary = isCalledInLibrary();
        if (calledInLibrary) {
            try {
                this.mLibraryResource.getValue(id, outValue, resolveRefs);
                return;
            } catch (NotFoundException e) {
                if (calledInLibrary) {
                    super.getValue(id, outValue, resolveRefs);
                    return;
                } else {
                    this.mLibraryResource.getValue(id, outValue, resolveRefs);
                    return;
                }
            }
        }
        super.getValue(id, outValue, resolveRefs);
    }

    public void getValueForDensity(int id, int density, TypedValue outValue, boolean resolveRefs) {
        boolean calledInLibrary = isCalledInLibrary();
        if (calledInLibrary) {
            try {
                this.mLibraryResource.getValueForDensity(id, density, outValue, resolveRefs);
                return;
            } catch (NotFoundException e) {
                if (calledInLibrary) {
                    super.getValueForDensity(id, density, outValue, resolveRefs);
                    return;
                } else {
                    this.mLibraryResource.getValueForDensity(id, density, outValue, resolveRefs);
                    return;
                }
            }
        }
        super.getValueForDensity(id, density, outValue, resolveRefs);
    }

    public int getIdentifier(String name, String defType, String defPackage) {
        int id;
        if (isCalledInLibrary()) {
            id = this.mLibraryResource.getIdentifier(name, defType, defPackage);
            return id != 0 ? id : super.getIdentifier(name, defType, defPackage);
        } else {
            id = super.getIdentifier(name, defType, defPackage);
            return id != 0 ? id : this.mLibraryResource.getIdentifier(name, defType, defPackage);
        }
    }

    public Drawable getDrawable(int id) {
        boolean calledInLibrary = isCalledInLibrary();
        if (!calledInLibrary) {
            return super.getDrawable(id);
        }
        try {
            return this.mLibraryResource.getDrawable(id);
        } catch (NotFoundException e) {
            if (calledInLibrary) {
                return super.getDrawable(id);
            }
            return this.mLibraryResource.getDrawable(id);
        }
    }
}
