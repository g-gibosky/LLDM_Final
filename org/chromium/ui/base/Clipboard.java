package org.chromium.ui.base;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;
import org.chromium.base.annotations.SuppressFBWarnings;
import org.chromium.ui.C0290R;
import org.chromium.ui.widget.Toast;

@JNINamespace("ui")
public class Clipboard {
    private final ClipboardManager mClipboardManager;
    private final Context mContext;

    public Clipboard(Context context) {
        this.mContext = context;
        this.mClipboardManager = (ClipboardManager) context.getSystemService("clipboard");
    }

    @CalledByNative
    private static Clipboard create(Context context) {
        return new Clipboard(context);
    }

    @CalledByNative
    private String getCoercedText() {
        ClipData clip = this.mClipboardManager.getPrimaryClip();
        if (clip != null && clip.getItemCount() > 0) {
            CharSequence sequence = clip.getItemAt(0).coerceToText(this.mContext);
            if (sequence != null) {
                return sequence.toString();
            }
        }
        return null;
    }

    @CalledByNative
    private String getHTMLText() {
        ClipData clip = this.mClipboardManager.getPrimaryClip();
        if (clip == null || clip.getItemCount() <= 0) {
            return null;
        }
        return clip.getItemAt(0).getHtmlText();
    }

    @SuppressFBWarnings({"UPM_UNCALLED_PRIVATE_METHOD"})
    @CalledByNative
    public void setText(String text) {
        setPrimaryClipNoException(ClipData.newPlainText("text", text));
    }

    @SuppressFBWarnings({"UPM_UNCALLED_PRIVATE_METHOD"})
    @CalledByNative
    private void setHTMLText(String html, String text) {
        setPrimaryClipNoException(ClipData.newHtmlText("html", text, html));
    }

    @SuppressFBWarnings({"UPM_UNCALLED_PRIVATE_METHOD"})
    @CalledByNative
    private void clear() {
        setPrimaryClipNoException(ClipData.newPlainText(null, null));
    }

    private void setPrimaryClipNoException(ClipData clip) {
        try {
            this.mClipboardManager.setPrimaryClip(clip);
        } catch (Exception e) {
            Toast.makeText(this.mContext, this.mContext.getString(C0290R.string.copy_to_clipboard_failure_message), 0).show();
        }
    }
}
