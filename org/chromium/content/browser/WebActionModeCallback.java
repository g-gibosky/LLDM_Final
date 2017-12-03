package org.chromium.content.browser;

import android.annotation.TargetApi;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.Resources.NotFoundException;
import android.graphics.Rect;
import android.os.Build.VERSION;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import java.util.List;
import org.chromium.content.C0174R;
import org.chromium.ui.base.DeviceFormFactor;

public class WebActionModeCallback implements Callback {
    public static final int MENU_ITEM_PROCESS_TEXT = 4;
    public static final int MENU_ITEM_SHARE = 1;
    public static final int MENU_ITEM_WEB_SEARCH = 2;
    protected final ActionHandler mActionHandler;
    private final Context mContext;
    private boolean mEditable;
    private boolean mIsDestroyed;
    private boolean mIsInsertion;
    private boolean mIsPasswordType;

    public interface ActionHandler {
        void copy();

        void cut();

        boolean isIncognito();

        boolean isInsertion();

        boolean isSelectActionModeAllowed(int i);

        boolean isSelectionEditable();

        boolean isSelectionPassword();

        void onDestroyActionMode();

        void onGetContentRect(Rect rect);

        void paste();

        void processText(Intent intent);

        void search();

        void selectAll();

        void share();
    }

    public WebActionModeCallback(Context context, ActionHandler actionHandler) {
        this.mContext = context;
        this.mActionHandler = actionHandler;
    }

    protected Context getContext() {
        return this.mContext;
    }

    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.setTitle(DeviceFormFactor.isTablet(getContext()) ? getContext().getString(C0174R.string.actionbar_textselection_title) : null);
        mode.setSubtitle(null);
        this.mEditable = this.mActionHandler.isSelectionEditable();
        this.mIsPasswordType = this.mActionHandler.isSelectionPassword();
        this.mIsInsertion = this.mActionHandler.isInsertion();
        createActionMenu(mode, menu);
        return true;
    }

    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        boolean isEditableNow = this.mActionHandler.isSelectionEditable();
        boolean isPasswordNow = this.mActionHandler.isSelectionPassword();
        boolean isInsertionNow = this.mActionHandler.isInsertion();
        if (this.mEditable == isEditableNow && this.mIsPasswordType == isPasswordNow && this.mIsInsertion == isInsertionNow) {
            return false;
        }
        this.mEditable = isEditableNow;
        this.mIsPasswordType = isPasswordNow;
        this.mIsInsertion = isInsertionNow;
        menu.clear();
        createActionMenu(mode, menu);
        return true;
    }

    private void createActionMenu(ActionMode mode, Menu menu) {
        try {
            mode.getMenuInflater().inflate(C0174R.menu.select_action_menu, menu);
        } catch (NotFoundException e) {
            new MenuInflater(getContext()).inflate(C0174R.menu.select_action_menu, menu);
        }
        if (this.mIsInsertion) {
            menu.removeItem(C0174R.id.select_action_menu_select_all);
            menu.removeItem(C0174R.id.select_action_menu_cut);
            menu.removeItem(C0174R.id.select_action_menu_copy);
            menu.removeItem(C0174R.id.select_action_menu_share);
            menu.removeItem(C0174R.id.select_action_menu_web_search);
            return;
        }
        if (!(this.mEditable && canPaste())) {
            menu.removeItem(C0174R.id.select_action_menu_paste);
        }
        if (!this.mEditable) {
            menu.removeItem(C0174R.id.select_action_menu_cut);
        }
        if (this.mEditable || !this.mActionHandler.isSelectActionModeAllowed(1)) {
            menu.removeItem(C0174R.id.select_action_menu_share);
        }
        if (this.mEditable || this.mActionHandler.isIncognito() || !this.mActionHandler.isSelectActionModeAllowed(2)) {
            menu.removeItem(C0174R.id.select_action_menu_web_search);
        }
        if (this.mIsPasswordType) {
            menu.removeItem(C0174R.id.select_action_menu_copy);
            menu.removeItem(C0174R.id.select_action_menu_cut);
            return;
        }
        initializeTextProcessingMenu(menu);
    }

    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        if (this.mIsDestroyed) {
            return true;
        }
        int id = item.getItemId();
        int groupId = item.getGroupId();
        if (id == C0174R.id.select_action_menu_select_all) {
            this.mActionHandler.selectAll();
            return true;
        } else if (id == C0174R.id.select_action_menu_cut) {
            this.mActionHandler.cut();
            mode.finish();
            return true;
        } else if (id == C0174R.id.select_action_menu_copy) {
            this.mActionHandler.copy();
            mode.finish();
            return true;
        } else if (id == C0174R.id.select_action_menu_paste) {
            this.mActionHandler.paste();
            mode.finish();
            return true;
        } else if (id == C0174R.id.select_action_menu_share) {
            this.mActionHandler.share();
            mode.finish();
            return true;
        } else if (id == C0174R.id.select_action_menu_web_search) {
            this.mActionHandler.search();
            mode.finish();
            return true;
        } else if (groupId != C0174R.id.select_action_menu_text_processing_menus) {
            return false;
        } else {
            this.mActionHandler.processText(item.getIntent());
            return true;
        }
    }

    public void onDestroyActionMode(ActionMode mode) {
        this.mIsDestroyed = true;
        this.mActionHandler.onDestroyActionMode();
    }

    public void onGetContentRect(ActionMode mode, View view, Rect outRect) {
        if (!this.mIsDestroyed) {
            this.mActionHandler.onGetContentRect(outRect);
        }
    }

    private boolean canPaste() {
        return ((ClipboardManager) getContext().getSystemService("clipboard")).hasPrimaryClip();
    }

    private void initializeTextProcessingMenu(Menu menu) {
        if (VERSION.SDK_INT >= 23 && this.mActionHandler.isSelectActionModeAllowed(4)) {
            List<ResolveInfo> supportedActivities = getContext().getPackageManager().queryIntentActivities(createProcessTextIntent(), 0);
            for (int i = 0; i < supportedActivities.size(); i++) {
                ResolveInfo resolveInfo = (ResolveInfo) supportedActivities.get(i);
                menu.add(C0174R.id.select_action_menu_text_processing_menus, 0, i, resolveInfo.loadLabel(getContext().getPackageManager())).setIntent(createProcessTextIntentForResolveInfo(resolveInfo)).setShowAsAction(1);
            }
        }
    }

    @TargetApi(23)
    private Intent createProcessTextIntent() {
        return new Intent().setAction("android.intent.action.PROCESS_TEXT").setType("text/plain");
    }

    @TargetApi(23)
    private Intent createProcessTextIntentForResolveInfo(ResolveInfo info) {
        return createProcessTextIntent().putExtra("android.intent.extra.PROCESS_TEXT_READONLY", !this.mActionHandler.isSelectionEditable()).setClassName(info.activityInfo.packageName, info.activityInfo.name);
    }
}
