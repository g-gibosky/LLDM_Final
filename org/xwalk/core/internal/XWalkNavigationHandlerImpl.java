package org.xwalk.core.internal;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;
import java.net.URI;
import org.chromium.components.navigation_interception.NavigationParams;
import org.chromium.ui.base.PageTransition;

public class XWalkNavigationHandlerImpl implements XWalkNavigationHandler {
    private static final String ACTION_GEO_PREFIX = "geo:";
    private static final String ACTION_INTENT_PREFIX = "intent:";
    private static final String ACTION_MAIL_PREFIX = "mailto:";
    private static final String ACTION_MARKET_PREFIX = "market:";
    private static final String ACTION_SMS_PREFIX = "sms:";
    private static final String ACTION_TEL_PREFIX = "tel:";
    public static final String EXTRA_BROWSER_FALLBACK_URL = "browser_fallback_url";
    private static final String PROTOCOL_WTAI_MC_PREFIX = "wtai://wp/mc;";
    private static final String PROTOCOL_WTAI_PREFIX = "wtai://";
    private static final String TAG = "XWalkNavigationHandlerImpl";
    private Context mContext;
    private String mFallbackUrl;

    public XWalkNavigationHandlerImpl(Context context) {
        this.mContext = context;
    }

    public boolean handleNavigation(NavigationParams params) {
        String url = params.url;
        if (UrlUtilities.isAcceptedScheme(url)) {
            return false;
        }
        Intent intent;
        if (url.startsWith(PROTOCOL_WTAI_PREFIX)) {
            intent = createIntentForWTAI(url);
        } else {
            intent = createIntentForActionUri(url);
        }
        if (intent == null && shouldOverrideUrlLoadingInternal(params)) {
            return true;
        }
        if (intent == null || !startActivity(intent)) {
            return handleUrlByMimeType(url);
        }
        return true;
    }

    protected boolean startActivity(Intent intent) {
        try {
            if (!(this.mContext instanceof Activity)) {
                intent.addFlags(PageTransition.CHAIN_START);
            }
            this.mContext.startActivity(intent);
            return true;
        } catch (ActivityNotFoundException e) {
            Log.w(TAG, "Activity not found for Intent:");
            Log.w(TAG, intent.toUri(0));
            return false;
        }
    }

    private Intent createIntentForWTAI(String url) {
        if (!url.startsWith(PROTOCOL_WTAI_MC_PREFIX)) {
            return null;
        }
        String mcUrl = ACTION_TEL_PREFIX + url.substring(PROTOCOL_WTAI_MC_PREFIX.length());
        Intent intent = new Intent("android.intent.action.DIAL");
        intent.setData(Uri.parse(mcUrl));
        return intent;
    }

    private Intent createIntentForActionUri(String url) {
        Intent intent;
        if (url.startsWith(ACTION_TEL_PREFIX)) {
            intent = new Intent("android.intent.action.DIAL");
            intent.setData(Uri.parse(url));
            return intent;
        } else if (url.startsWith(ACTION_GEO_PREFIX)) {
            intent = new Intent("android.intent.action.VIEW");
            intent.setData(Uri.parse(url));
            return intent;
        } else if (url.startsWith(ACTION_MAIL_PREFIX)) {
            intent = new Intent("android.intent.action.VIEW");
            intent.setData(Uri.parse(url));
            return intent;
        } else if (url.startsWith(ACTION_SMS_PREFIX)) {
            String address;
            intent = new Intent("android.intent.action.VIEW");
            int parmIndex = url.indexOf(63);
            if (parmIndex == -1) {
                address = url.substring(4);
            } else {
                address = url.substring(4, parmIndex);
                String query = Uri.parse(url).getQuery();
                if (query != null && query.startsWith("body=")) {
                    intent.putExtra("sms_body", query.substring(5));
                }
            }
            intent.setData(Uri.parse(ACTION_SMS_PREFIX + address));
            intent.putExtra("address", address);
            intent.setType("vnd.android-dir/mms-sms");
            return intent;
        } else if (!url.startsWith(ACTION_MARKET_PREFIX)) {
            return null;
        } else {
            intent = new Intent("android.intent.action.VIEW");
            intent.setData(Uri.parse(url));
            return intent;
        }
    }

    private boolean handleUrlByMimeType(String url) {
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(url));
        if (shouldHandleMimeType(mimeType)) {
            Intent sendIntent = new Intent();
            sendIntent.setAction("android.intent.action.VIEW");
            sendIntent.setDataAndType(Uri.parse(url), mimeType);
            if (sendIntent.resolveActivity(this.mContext.getPackageManager()) != null) {
                startActivity(sendIntent);
                return true;
            }
        }
        return false;
    }

    private boolean shouldHandleMimeType(String mimeType) {
        if (mimeType == null || !mimeType.startsWith("application/") || mimeType == "application/xhtml+xml" || mimeType == "application/xml") {
            return false;
        }
        return true;
    }

    public String getFallbackUrl() {
        return this.mFallbackUrl;
    }

    public void resetFallbackUrl() {
        this.mFallbackUrl = null;
    }

    private boolean shouldOverrideUrlLoadingInternal(NavigationParams params) {
        String url = params.url;
        try {
            Intent intent = Intent.parseUri(url, 1);
            int pageTransitionCore = params.pageTransitionType & 255;
            boolean isLink = pageTransitionCore == 0;
            boolean isFormSubmit = pageTransitionCore == 7;
            boolean isFromIntent = (params.pageTransitionType & PageTransition.FROM_API) != 0;
            boolean isForwardBackNavigation = (params.pageTransitionType & 16777216) != 0;
            boolean isExternalProtocol = !UrlUtilities.isAcceptedScheme(url);
            boolean typedRedirectToExternalProtocol = (pageTransitionCore == 1) && params.isRedirect && isExternalProtocol;
            boolean hasBrowserFallbackUrl = false;
            String browserFallbackUrl = UrlUtilities.safeGetStringExtra(intent, EXTRA_BROWSER_FALLBACK_URL);
            if (browserFallbackUrl == null || !UrlUtilities.isValidForIntentFallbackNavigation(browserFallbackUrl)) {
                browserFallbackUrl = null;
            } else {
                hasBrowserFallbackUrl = true;
            }
            if (isForwardBackNavigation) {
                return false;
            }
            boolean linkNotFromIntent = isLink && !isFromIntent;
            boolean incomingIntentRedirect = isLink && isFromIntent && params.isRedirect;
            boolean isRedirectFromFormSubmit = isFormSubmit && params.isRedirect;
            if (!typedRedirectToExternalProtocol && !linkNotFromIntent && !incomingIntentRedirect && !isRedirectFromFormSubmit) {
                return false;
            }
            if (url.matches(".*youtube\\.com.*[?&]pairingCode=.*")) {
                return false;
            }
            if (UrlUtilities.getIntentHandlers(this.mContext, intent).size() > 0) {
                if (hasBrowserFallbackUrl) {
                    intent.removeExtra(EXTRA_BROWSER_FALLBACK_URL);
                }
                intent.addCategory("android.intent.category.BROWSABLE");
                intent.setComponent(null);
                if (VERSION.SDK_INT >= 15) {
                    Intent selector = intent.getSelector();
                    if (selector != null) {
                        selector.addCategory("android.intent.category.BROWSABLE");
                        selector.setComponent(null);
                    }
                }
                intent.putExtra("com.android.browser.application_id", this.mContext.getPackageName());
                intent.addFlags(PageTransition.CHAIN_START);
                if (!isExternalProtocol) {
                    if (!UrlUtilities.isSpecializedHandlerAvailable(this.mContext, intent)) {
                        return false;
                    }
                    if (params.referrer != null && (isLink || isFormSubmit)) {
                        URI currentUri;
                        try {
                            currentUri = new URI(url);
                            URI uri = new URI(params.referrer);
                        } catch (Exception e) {
                            currentUri = null;
                            URI previousUri = null;
                        }
                        if (!(currentUri == null || previousUri == null || !TextUtils.equals(currentUri.getHost(), previousUri.getHost()))) {
                            Intent previousIntent;
                            try {
                                previousIntent = Intent.parseUri(params.referrer, 1);
                            } catch (Exception e2) {
                                previousIntent = null;
                            }
                            if (previousIntent != null) {
                                if (UrlUtilities.getIntentHandlers(this.mContext, previousIntent).containsAll(UrlUtilities.getIntentHandlers(this.mContext, intent))) {
                                    return false;
                                }
                            }
                        }
                    }
                }
                if (intent == null || !startActivity(intent)) {
                    return false;
                }
                return true;
            } else if (hasBrowserFallbackUrl) {
                this.mFallbackUrl = browserFallbackUrl;
                return false;
            } else {
                String packagename = intent.getPackage();
                if (packagename == null) {
                    return false;
                }
                try {
                    Intent intent2 = new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + packagename + "&referrer=" + this.mContext.getPackageName()));
                    try {
                        intent2.addCategory("android.intent.category.BROWSABLE");
                        intent2.setPackage("com.android.vending");
                        intent2.addFlags(PageTransition.CHAIN_START);
                        this.mContext.startActivity(intent2);
                        return true;
                    } catch (ActivityNotFoundException e3) {
                        intent = intent2;
                        return false;
                    }
                } catch (ActivityNotFoundException e4) {
                    return false;
                }
            }
        } catch (Exception ex) {
            Log.w(TAG, "Bad URI=" + url + " ex=" + ex);
            return false;
        }
    }
}
