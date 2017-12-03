package org.xwalk.core;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnShowListener;
import android.view.View;
import android.widget.Button;
import java.util.ArrayList;
import java.util.Iterator;

public class XWalkDialogManager {
    public static final int DIALOG_ARCHITECTURE_MISMATCH = 4;
    public static final int DIALOG_DECOMPRESSING = 11;
    public static final int DIALOG_DOWNLOADING = 12;
    public static final int DIALOG_DOWNLOAD_ERROR = 6;
    public static final int DIALOG_NEWER_VERSION = 3;
    public static final int DIALOG_NOT_FOUND = 1;
    public static final int DIALOG_OLDER_VERSION = 2;
    public static final int DIALOG_SELECT_STORE = 7;
    public static final int DIALOG_SIGNATURE_CHECK_ERROR = 5;
    public static final int DIALOG_UNSUPPORTED_STORE = 8;
    private static final String TAG = "XWalkLib";
    private Dialog mActiveDialog;
    private AlertDialog mArchitectureMismatchDialog;
    private Context mContext;
    private ProgressDialog mDecompressingDialog;
    private AlertDialog mDownloadErrorDialog;
    private ProgressDialog mDownloadingDialog;
    private AlertDialog mNewerVersionDialog;
    private AlertDialog mNotFoundDialog;
    private AlertDialog mOlderVersionDialog;
    private AlertDialog mSelectStoreDialog;
    private AlertDialog mSignatureCheckErrorDialog;
    private AlertDialog mUnsupportedStoreDialog;

    private static class ButtonAction {
        Runnable mClickAction;
        boolean mMandatory;
        int mWhich;

        ButtonAction(int which, Runnable command, boolean mandatory) {
            this.mWhich = which;
            this.mClickAction = command;
            this.mMandatory = mandatory;
        }
    }

    public XWalkDialogManager(Context context) {
        this.mContext = context;
    }

    public void setAlertDialog(int id, AlertDialog dialog) {
        if ((dialog instanceof ProgressDialog) || (dialog instanceof DatePickerDialog) || (dialog instanceof TimePickerDialog)) {
            throw new IllegalArgumentException("The type of dialog must be AlertDialog");
        } else if (id == 1) {
            this.mNotFoundDialog = dialog;
        } else if (id == 2) {
            this.mOlderVersionDialog = dialog;
        } else if (id == 3) {
            this.mNewerVersionDialog = dialog;
        } else if (id == 4) {
            this.mArchitectureMismatchDialog = dialog;
        } else if (id == 5) {
            this.mSignatureCheckErrorDialog = dialog;
        } else if (id == 6) {
            this.mDownloadErrorDialog = dialog;
        } else if (id == 7) {
            this.mSelectStoreDialog = dialog;
        } else if (id == 8) {
            this.mUnsupportedStoreDialog = dialog;
        } else {
            throw new IllegalArgumentException("Invalid dialog id " + id);
        }
    }

    public void setProgressDialog(int id, ProgressDialog dialog) {
        if (id == 11) {
            this.mDecompressingDialog = dialog;
        } else if (id == 12) {
            this.mDownloadingDialog = dialog;
        } else {
            throw new IllegalArgumentException("Invalid dialog id " + id);
        }
    }

    public AlertDialog getAlertDialog(int id) {
        if (id == 1) {
            if (this.mNotFoundDialog == null) {
                this.mNotFoundDialog = buildAlertDialog();
                setTitle(this.mNotFoundDialog, C0315R.string.startup_not_found_title);
                setMessage(this.mNotFoundDialog, C0315R.string.startup_not_found_message);
                setPositiveButton(this.mNotFoundDialog, C0315R.string.xwalk_get_crosswalk);
                setNegativeButton(this.mNotFoundDialog, C0315R.string.xwalk_close);
            }
            return this.mNotFoundDialog;
        } else if (id == 2) {
            if (this.mOlderVersionDialog == null) {
                this.mOlderVersionDialog = buildAlertDialog();
                setTitle(this.mOlderVersionDialog, C0315R.string.startup_older_version_title);
                setMessage(this.mOlderVersionDialog, C0315R.string.startup_older_version_message);
                setPositiveButton(this.mOlderVersionDialog, C0315R.string.xwalk_get_crosswalk);
                setNegativeButton(this.mOlderVersionDialog, C0315R.string.xwalk_close);
            }
            return this.mOlderVersionDialog;
        } else if (id == 3) {
            if (this.mNewerVersionDialog == null) {
                this.mNewerVersionDialog = buildAlertDialog();
                setTitle(this.mNewerVersionDialog, C0315R.string.startup_newer_version_title);
                setMessage(this.mNewerVersionDialog, C0315R.string.startup_newer_version_message);
                setNegativeButton(this.mNewerVersionDialog, C0315R.string.xwalk_close);
            }
            return this.mNewerVersionDialog;
        } else if (id == 4) {
            if (this.mArchitectureMismatchDialog == null) {
                this.mArchitectureMismatchDialog = buildAlertDialog();
                setTitle(this.mArchitectureMismatchDialog, C0315R.string.startup_architecture_mismatch_title);
                setMessage(this.mArchitectureMismatchDialog, C0315R.string.startup_architecture_mismatch_message);
                setPositiveButton(this.mArchitectureMismatchDialog, C0315R.string.xwalk_get_crosswalk);
                setNegativeButton(this.mArchitectureMismatchDialog, C0315R.string.xwalk_close);
            }
            return this.mArchitectureMismatchDialog;
        } else if (id == 5) {
            if (this.mSignatureCheckErrorDialog == null) {
                this.mSignatureCheckErrorDialog = buildAlertDialog();
                setTitle(this.mSignatureCheckErrorDialog, C0315R.string.startup_signature_check_error_title);
                setMessage(this.mSignatureCheckErrorDialog, C0315R.string.startup_signature_check_error_message);
                setNegativeButton(this.mSignatureCheckErrorDialog, C0315R.string.xwalk_close);
            }
            return this.mSignatureCheckErrorDialog;
        } else if (id == 6) {
            if (this.mDownloadErrorDialog == null) {
                this.mDownloadErrorDialog = buildAlertDialog();
                setTitle(this.mDownloadErrorDialog, C0315R.string.crosswalk_install_title);
                setMessage(this.mDownloadErrorDialog, C0315R.string.download_failed_message);
                setPositiveButton(this.mDownloadErrorDialog, C0315R.string.xwalk_retry);
                setNegativeButton(this.mDownloadErrorDialog, C0315R.string.xwalk_cancel);
            }
            return this.mDownloadErrorDialog;
        } else if (id == 7) {
            if (this.mSelectStoreDialog == null) {
                this.mSelectStoreDialog = buildAlertDialog();
                setTitle(this.mSelectStoreDialog, C0315R.string.crosswalk_install_title);
                setPositiveButton(this.mSelectStoreDialog, C0315R.string.xwalk_continue);
            }
            return this.mSelectStoreDialog;
        } else if (id == 8) {
            if (this.mUnsupportedStoreDialog == null) {
                this.mUnsupportedStoreDialog = buildAlertDialog();
                setTitle(this.mUnsupportedStoreDialog, C0315R.string.crosswalk_install_title);
                setMessage(this.mUnsupportedStoreDialog, C0315R.string.unsupported_store_message);
                setNegativeButton(this.mUnsupportedStoreDialog, C0315R.string.xwalk_close);
            }
            return this.mUnsupportedStoreDialog;
        } else {
            throw new IllegalArgumentException("Invalid dialog id " + id);
        }
    }

    public ProgressDialog getProgressDialog(int id) {
        if (id == 11) {
            if (this.mDecompressingDialog == null) {
                this.mDecompressingDialog = buildProgressDialog();
                setTitle(this.mDecompressingDialog, C0315R.string.crosswalk_install_title);
                setMessage(this.mDecompressingDialog, C0315R.string.decompression_progress_message);
                setNegativeButton(this.mDecompressingDialog, C0315R.string.xwalk_cancel);
                this.mDecompressingDialog.setProgressStyle(0);
            }
            return this.mDecompressingDialog;
        } else if (id == 12) {
            if (this.mDownloadingDialog == null) {
                this.mDownloadingDialog = buildProgressDialog();
                setTitle(this.mDownloadingDialog, C0315R.string.crosswalk_install_title);
                setMessage(this.mDownloadingDialog, C0315R.string.download_progress_message);
                setNegativeButton(this.mDownloadingDialog, C0315R.string.xwalk_cancel);
                this.mDownloadingDialog.setProgressStyle(1);
            }
            return this.mDownloadingDialog;
        } else {
            throw new IllegalArgumentException("Invalid dialog id " + id);
        }
    }

    void showInitializationError(int status, Runnable cancelCommand, Runnable downloadCommand) {
        AlertDialog dialog;
        ArrayList<ButtonAction> actions = new ArrayList();
        if (status == 2) {
            dialog = getAlertDialog(1);
            actions.add(new ButtonAction(-1, downloadCommand, true));
            actions.add(new ButtonAction(-2, cancelCommand, false));
        } else if (status == 3) {
            dialog = getAlertDialog(2);
            actions.add(new ButtonAction(-1, downloadCommand, true));
            actions.add(new ButtonAction(-2, cancelCommand, false));
        } else if (status == 4) {
            dialog = getAlertDialog(3);
            actions.add(new ButtonAction(-2, cancelCommand, true));
        } else if (status == 6) {
            dialog = getAlertDialog(4);
            actions.add(new ButtonAction(-1, downloadCommand, true));
            actions.add(new ButtonAction(-2, cancelCommand, false));
        } else if (status == 7) {
            dialog = getAlertDialog(5);
            actions.add(new ButtonAction(-2, cancelCommand, true));
        } else {
            throw new IllegalArgumentException("Invalid status " + status);
        }
        showDialog(dialog, actions);
    }

    void showDownloadError(Runnable cancelCommand, Runnable downloadCommand) {
        AlertDialog dialog = getAlertDialog(6);
        ArrayList<ButtonAction> actions = new ArrayList();
        actions.add(new ButtonAction(-1, downloadCommand, true));
        actions.add(new ButtonAction(-2, cancelCommand, false));
        showDialog(dialog, actions);
    }

    void showSelectStore(Runnable downloadCommand, String storeName) {
        AlertDialog dialog = getAlertDialog(7);
        setMessage(dialog, this.mContext.getString(C0315R.string.select_store_message).replace("STORE_NAME", storeName));
        ArrayList<ButtonAction> actions = new ArrayList();
        actions.add(new ButtonAction(-1, downloadCommand, true));
        showDialog(dialog, actions);
    }

    void showUnsupportedStore(Runnable cancelCommand) {
        AlertDialog dialog = getAlertDialog(8);
        ArrayList<ButtonAction> actions = new ArrayList();
        actions.add(new ButtonAction(-2, cancelCommand, true));
        showDialog(dialog, actions);
    }

    void showDecompressProgress(Runnable cancelCommand) {
        ProgressDialog dialog = getProgressDialog(11);
        ArrayList<ButtonAction> actions = new ArrayList();
        actions.add(new ButtonAction(-2, cancelCommand, false));
        showDialog(dialog, actions);
    }

    void showDownloadProgress(Runnable cancelCommand) {
        ProgressDialog dialog = getProgressDialog(12);
        ArrayList<ButtonAction> actions = new ArrayList();
        actions.add(new ButtonAction(-2, cancelCommand, false));
        showDialog(dialog, actions);
    }

    void dismissDialog() {
        this.mActiveDialog.dismiss();
        this.mActiveDialog = null;
    }

    void setProgress(int progress, int max) {
        ProgressDialog dialog = this.mActiveDialog;
        dialog.setIndeterminate(false);
        dialog.setMax(max);
        dialog.setProgress(progress);
    }

    boolean isShowingDialog() {
        return this.mActiveDialog != null && this.mActiveDialog.isShowing();
    }

    private AlertDialog buildAlertDialog() {
        AlertDialog dialog = new Builder(this.mContext).create();
        dialog.setIcon(17301543);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    private ProgressDialog buildProgressDialog() {
        ProgressDialog dialog = new ProgressDialog(this.mContext);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    private void setTitle(AlertDialog dialog, int resourceId) {
        dialog.setTitle(this.mContext.getString(resourceId));
    }

    private void setMessage(AlertDialog dialog, int resourceId) {
        setMessage(dialog, this.mContext.getString(resourceId));
    }

    private void setMessage(AlertDialog dialog, String text) {
        text = text.replaceAll("APP_NAME", XWalkEnvironment.getApplicationName());
        if (text.startsWith("this")) {
            text = text.replaceFirst("this", "This");
        }
        dialog.setMessage(text);
    }

    private void setPositiveButton(AlertDialog dialog, int resourceId) {
        dialog.setButton(-1, this.mContext.getString(resourceId), (OnClickListener) null);
    }

    private void setNegativeButton(AlertDialog dialog, int resourceId) {
        dialog.setButton(-2, this.mContext.getString(resourceId), (OnClickListener) null);
    }

    private void showDialog(final AlertDialog dialog, final ArrayList<ButtonAction> actions) {
        dialog.setOnShowListener(new OnShowListener() {
            public void onShow(DialogInterface d) {
                Iterator i$ = actions.iterator();
                while (i$.hasNext()) {
                    ButtonAction action = (ButtonAction) i$.next();
                    Button button = dialog.getButton(action.mWhich);
                    if (button == null) {
                        if (action.mMandatory) {
                            throw new RuntimeException("Button " + action.mWhich + " is mandatory");
                        }
                    } else if (action.mClickAction != null) {
                        final Runnable command = action.mClickAction;
                        button.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View view) {
                                XWalkDialogManager.this.dismissDialog();
                                command.run();
                            }
                        });
                    }
                }
            }
        });
        this.mActiveDialog = dialog;
        this.mActiveDialog.show();
    }
}
