package org.xwalk.core.internal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class XWalkExternalExtensionManagerBridge extends XWalkExternalExtensionManagerInternal {
    private XWalkCoreBridge coreBridge;
    private ReflectMethod getViewActivityMethod = new ReflectMethod(null, "getViewActivity", new Class[0]);
    private ReflectMethod getViewContextMethod = new ReflectMethod(null, "getViewContext", new Class[0]);
    private ReflectMethod loadExtensionStringMethod = new ReflectMethod(null, "loadExtension", new Class[0]);
    private ReflectMethod onActivityResultintintIntentMethod = new ReflectMethod(null, "onActivityResult", new Class[0]);
    private ReflectMethod onDestroyMethod = new ReflectMethod(null, "onDestroy", new Class[0]);
    private ReflectMethod onNewIntentIntentMethod = new ReflectMethod(null, "onNewIntent", new Class[0]);
    private ReflectMethod onPauseMethod = new ReflectMethod(null, "onPause", new Class[0]);
    private ReflectMethod onResumeMethod = new ReflectMethod(null, "onResume", new Class[0]);
    private ReflectMethod onStartMethod = new ReflectMethod(null, "onStart", new Class[0]);
    private ReflectMethod onStopMethod = new ReflectMethod(null, "onStop", new Class[0]);
    private Object wrapper;

    public Object getWrapper() {
        return this.wrapper;
    }

    public XWalkExternalExtensionManagerBridge(XWalkViewBridge view, Object wrapper) {
        super(view);
        this.wrapper = wrapper;
        reflectionInit();
    }

    public Activity getViewActivity() {
        if (this.getViewActivityMethod == null || this.getViewActivityMethod.isNull()) {
            return getViewActivitySuper();
        }
        return (Activity) this.getViewActivityMethod.invoke(new Object[0]);
    }

    public Activity getViewActivitySuper() {
        Activity ret = super.getViewActivity();
        if (ret == null) {
            return null;
        }
        return ret;
    }

    public Context getViewContext() {
        if (this.getViewContextMethod == null || this.getViewContextMethod.isNull()) {
            return getViewContextSuper();
        }
        return (Context) this.getViewContextMethod.invoke(new Object[0]);
    }

    public Context getViewContextSuper() {
        Context ret = super.getViewContext();
        if (ret == null) {
            return null;
        }
        return ret;
    }

    public void loadExtension(String extensionPath) {
        if (this.loadExtensionStringMethod == null || this.loadExtensionStringMethod.isNull()) {
            loadExtensionSuper(extensionPath);
            return;
        }
        this.loadExtensionStringMethod.invoke(extensionPath);
    }

    public void loadExtensionSuper(String extensionPath) {
        super.loadExtension(extensionPath);
    }

    public void onStart() {
        if (this.onStartMethod == null || this.onStartMethod.isNull()) {
            onStartSuper();
        } else {
            this.onStartMethod.invoke(new Object[0]);
        }
    }

    public void onStartSuper() {
        super.onStart();
    }

    public void onResume() {
        if (this.onResumeMethod == null || this.onResumeMethod.isNull()) {
            onResumeSuper();
        } else {
            this.onResumeMethod.invoke(new Object[0]);
        }
    }

    public void onResumeSuper() {
        super.onResume();
    }

    public void onPause() {
        if (this.onPauseMethod == null || this.onPauseMethod.isNull()) {
            onPauseSuper();
        } else {
            this.onPauseMethod.invoke(new Object[0]);
        }
    }

    public void onPauseSuper() {
        super.onPause();
    }

    public void onStop() {
        if (this.onStopMethod == null || this.onStopMethod.isNull()) {
            onStopSuper();
        } else {
            this.onStopMethod.invoke(new Object[0]);
        }
    }

    public void onStopSuper() {
        super.onStop();
    }

    public void onDestroy() {
        if (this.onDestroyMethod == null || this.onDestroyMethod.isNull()) {
            onDestroySuper();
        } else {
            this.onDestroyMethod.invoke(new Object[0]);
        }
    }

    public void onDestroySuper() {
        super.onDestroy();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (this.onActivityResultintintIntentMethod == null || this.onActivityResultintintIntentMethod.isNull()) {
            onActivityResultSuper(requestCode, resultCode, data);
            return;
        }
        this.onActivityResultintintIntentMethod.invoke(Integer.valueOf(requestCode), Integer.valueOf(resultCode), data);
    }

    public void onActivityResultSuper(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void onNewIntent(Intent intent) {
        this.onNewIntentIntentMethod.invoke(intent);
    }

    void reflectionInit() {
        this.coreBridge = XWalkCoreBridge.getInstance();
        if (this.coreBridge != null) {
            this.getViewActivityMethod.init(this.wrapper, null, "getViewActivity", new Class[0]);
            this.getViewContextMethod.init(this.wrapper, null, "getViewContext", new Class[0]);
            this.loadExtensionStringMethod.init(this.wrapper, null, "loadExtension", String.class);
            this.onStartMethod.init(this.wrapper, null, "onStart", new Class[0]);
            this.onResumeMethod.init(this.wrapper, null, "onResume", new Class[0]);
            this.onPauseMethod.init(this.wrapper, null, "onPause", new Class[0]);
            this.onStopMethod.init(this.wrapper, null, "onStop", new Class[0]);
            this.onDestroyMethod.init(this.wrapper, null, "onDestroy", new Class[0]);
            this.onActivityResultintintIntentMethod.init(this.wrapper, null, "onActivityResult", Integer.TYPE, Integer.TYPE, Intent.class);
            this.onNewIntentIntentMethod.init(this.wrapper, null, "onNewIntent", Intent.class);
        }
    }
}
