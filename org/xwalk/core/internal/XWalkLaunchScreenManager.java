package org.xwalk.core.internal;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyEvent;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import java.util.ArrayList;
import org.chromium.content.browser.ContentViewRenderView.FirstRenderedFrameListener;

public class XWalkLaunchScreenManager implements FirstRenderedFrameListener, OnShowListener, OnDismissListener, PageLoadListener {
    private static final String BORDER_MODE_REPEAT = "repeat";
    private static final String BORDER_MODE_ROUND = "round";
    private static final String BORDER_MODE_STRETCH = "stretch";
    private static String mIntentFilterStr;
    private Activity mActivity;
    private Context mContext;
    private int mCurrentOrientation;
    private boolean mCustomHideLaunchScreen;
    private boolean mFirstFrameReceived;
    private Dialog mLaunchScreenDialog;
    private BroadcastReceiver mLaunchScreenReadyWhenReceiver;
    private OrientationEventListener mOrientationListener;
    private boolean mPageLoadFinished;
    private ReadyWhenType mReadyWhen;
    private XWalkViewInternal mXWalkView;

    class C03502 extends BroadcastReceiver {
        C03502() {
        }

        public void onReceive(Context context, Intent intent) {
            XWalkLaunchScreenManager.this.mCustomHideLaunchScreen = true;
            XWalkLaunchScreenManager.this.hideLaunchScreenWhenReady();
        }
    }

    private enum BorderModeType {
        REPEAT,
        STRETCH,
        ROUND,
        NONE
    }

    private enum ReadyWhenType {
        FIRST_PAINT,
        USER_INTERACTIVE,
        COMPLETE,
        CUSTOM
    }

    public XWalkLaunchScreenManager(Context context, XWalkViewInternal xwView) {
        this.mXWalkView = xwView;
        this.mContext = context;
        try {
            this.mActivity = (Activity) this.mContext;
        } catch (ClassCastException e) {
        }
        mIntentFilterStr = this.mContext.getPackageName() + ".hideLaunchScreen";
    }

    public void displayLaunchScreen(String readyWhen, final String imageBorderList) {
        if (this.mXWalkView != null && this.mActivity != null) {
            setReadyWhen(readyWhen);
            this.mActivity.runOnUiThread(new Runnable() {

                class C03471 implements OnKeyListener {
                    C03471() {
                    }

                    public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {
                        if (keyCode == 4) {
                            XWalkLaunchScreenManager.this.performHideLaunchScreen();
                            XWalkLaunchScreenManager.this.mActivity.onBackPressed();
                        }
                        return true;
                    }
                }

                public void run() {
                    int bgResId = XWalkLaunchScreenManager.this.mContext.getResources().getIdentifier("launchscreen_bg", "drawable", XWalkLaunchScreenManager.this.mContext.getPackageName());
                    if (bgResId != 0) {
                        Drawable bgDrawable = null;
                        try {
                            bgDrawable = XWalkLaunchScreenManager.this.mContext.getResources().getDrawable(bgResId);
                        } catch (OutOfMemoryError e) {
                            e.printStackTrace();
                        }
                        if (bgDrawable != null) {
                            XWalkLaunchScreenManager.this.mLaunchScreenDialog = new Dialog(XWalkLaunchScreenManager.this.mContext, 16974064);
                            XWalkLaunchScreenManager.this.mLaunchScreenDialog.setOnKeyListener(new C03471());
                            XWalkLaunchScreenManager.this.mLaunchScreenDialog.setOnShowListener(XWalkLaunchScreenManager.this);
                            XWalkLaunchScreenManager.this.mLaunchScreenDialog.setOnDismissListener(XWalkLaunchScreenManager.this);
                            XWalkLaunchScreenManager.this.mLaunchScreenDialog.getWindow().setBackgroundDrawable(bgDrawable);
                            RelativeLayout root = XWalkLaunchScreenManager.this.getLaunchScreenLayout(imageBorderList);
                            if (root != null) {
                                XWalkLaunchScreenManager.this.mLaunchScreenDialog.setContentView(root);
                            }
                            XWalkLaunchScreenManager.this.mLaunchScreenDialog.show();
                            XWalkLaunchScreenManager.this.mOrientationListener = new OrientationEventListener(XWalkLaunchScreenManager.this.mContext, 3) {
                                public void onOrientationChanged(int ori) {
                                    if (XWalkLaunchScreenManager.this.mLaunchScreenDialog != null && XWalkLaunchScreenManager.this.mLaunchScreenDialog.isShowing() && XWalkLaunchScreenManager.this.getScreenOrientation() != XWalkLaunchScreenManager.this.mCurrentOrientation) {
                                        RelativeLayout root = XWalkLaunchScreenManager.this.getLaunchScreenLayout(imageBorderList);
                                        if (root != null) {
                                            XWalkLaunchScreenManager.this.mLaunchScreenDialog.setContentView(root);
                                        }
                                    }
                                }
                            };
                            XWalkLaunchScreenManager.this.mOrientationListener.enable();
                            if (XWalkLaunchScreenManager.this.mReadyWhen == ReadyWhenType.CUSTOM) {
                                XWalkLaunchScreenManager.this.registerBroadcastReceiver();
                            }
                        }
                    }
                }
            });
        }
    }

    public void onFirstFrameReceived() {
        this.mFirstFrameReceived = true;
        hideLaunchScreenWhenReady();
    }

    public void onShow(DialogInterface dialog) {
        this.mActivity.getWindow().setBackgroundDrawable(null);
        if (this.mFirstFrameReceived) {
            hideLaunchScreenWhenReady();
        }
    }

    public void onDismiss(DialogInterface dialog) {
        this.mOrientationListener.disable();
        this.mOrientationListener = null;
    }

    public void onPageFinished(String url) {
        this.mPageLoadFinished = true;
        hideLaunchScreenWhenReady();
    }

    public static String getHideLaunchScreenFilterStr() {
        return mIntentFilterStr;
    }

    public int getScreenOrientation() {
        Display display = this.mActivity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        if (size.x < size.y) {
            return 1;
        }
        return 2;
    }

    private RelativeLayout getLaunchScreenLayout(String imageBorderList) {
        String[] borders = imageBorderList.split(";");
        if (borders.length < 1) {
            return parseImageBorder("");
        }
        int orientation = getScreenOrientation();
        this.mCurrentOrientation = orientation;
        if (borders.length < 2 || orientation != 2) {
            if (borders.length != 3 || orientation != 1) {
                return parseImageBorder(borders[0]);
            }
            if (borders[2].equals("empty")) {
                return parseImageBorder("");
            }
            if (borders[2].isEmpty()) {
                return parseImageBorder(borders[0]);
            }
            return parseImageBorder(borders[2]);
        } else if (borders[1].equals("empty")) {
            return parseImageBorder("");
        } else {
            if (borders[1].isEmpty()) {
                return parseImageBorder(borders[0]);
            }
            return parseImageBorder(borders[1]);
        }
    }

    private int getSuitableSize(int maxSize, int divider) {
        int finalSize = divider;
        float minMod = (float) divider;
        while (divider > 1) {
            int mod = maxSize % divider;
            if (mod == 0) {
                return divider;
            }
            if (((float) mod) < minMod) {
                minMod = (float) mod;
                finalSize = divider;
            }
            divider--;
        }
        return finalSize;
    }

    private ImageView getSubImageView(Bitmap img, int x, int y, int width, int height, BorderModeType mode, int maxWidth, int maxHeight) {
        if (img == null) {
            return null;
        }
        if (width <= 0 || height <= 0) {
            return null;
        }
        if (!new Rect(0, 0, img.getWidth(), img.getHeight()).contains(new Rect(x, y, x + width, y + height))) {
            return null;
        }
        Bitmap subImage = Bitmap.createBitmap(img, x, y, width, height);
        ImageView subImageView = new ImageView(this.mContext);
        if (mode == BorderModeType.ROUND) {
            int originW = subImage.getWidth();
            int originH = subImage.getHeight();
            int newW = originW;
            int newH = originH;
            if (maxWidth > 0) {
                newW = getSuitableSize(maxWidth, originW);
            }
            if (maxHeight > 0) {
                newH = getSuitableSize(maxHeight, originH);
            }
            subImage = Bitmap.createScaledBitmap(subImage, newW, newH, true);
            mode = BorderModeType.REPEAT;
        }
        if (mode == BorderModeType.REPEAT) {
            BitmapDrawable drawable = new BitmapDrawable(this.mContext.getResources(), subImage);
            drawable.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
            subImageView.setImageDrawable(drawable);
            subImageView.setScaleType(ScaleType.FIT_XY);
            return subImageView;
        } else if (mode == BorderModeType.STRETCH) {
            subImageView.setImageBitmap(subImage);
            subImageView.setScaleType(ScaleType.FIT_XY);
            return subImageView;
        } else {
            subImageView.setImageBitmap(subImage);
            return subImageView;
        }
    }

    private int getStatusBarHeight() {
        int resourceId = this.mContext.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return this.mContext.getResources().getDimensionPixelSize(resourceId);
        }
        return 25;
    }

    private RelativeLayout parseImageBorder(String imageBorder) {
        int topBorder = 0;
        int rightBorder = 0;
        int leftBorder = 0;
        int bottomBorder = 0;
        BorderModeType horizontalMode = BorderModeType.STRETCH;
        BorderModeType verticalMode = BorderModeType.STRETCH;
        if (imageBorder.equals("empty")) {
            imageBorder = "";
        }
        String[] items = imageBorder.split(" ");
        ArrayList<String> borders = new ArrayList();
        ArrayList<BorderModeType> modes = new ArrayList();
        for (String item : items) {
            if (item.endsWith("px")) {
                borders.add(item.replaceAll("px", ""));
            } else {
                if (item.equals(BORDER_MODE_REPEAT)) {
                    modes.add(BorderModeType.REPEAT);
                } else {
                    if (item.equals(BORDER_MODE_STRETCH)) {
                        modes.add(BorderModeType.STRETCH);
                    } else {
                        if (item.equals(BORDER_MODE_ROUND)) {
                            modes.add(BorderModeType.ROUND);
                        }
                    }
                }
            }
        }
        try {
            if (borders.size() == 1) {
                bottomBorder = Integer.valueOf((String) borders.get(0)).intValue();
                leftBorder = bottomBorder;
                rightBorder = bottomBorder;
                topBorder = bottomBorder;
            } else if (borders.size() == 2) {
                bottomBorder = Integer.valueOf((String) borders.get(0)).intValue();
                topBorder = bottomBorder;
                leftBorder = Integer.valueOf((String) borders.get(1)).intValue();
                rightBorder = leftBorder;
            } else if (borders.size() == 3) {
                leftBorder = Integer.valueOf((String) borders.get(1)).intValue();
                rightBorder = leftBorder;
                topBorder = Integer.valueOf((String) borders.get(0)).intValue();
                bottomBorder = Integer.valueOf((String) borders.get(2)).intValue();
            } else if (borders.size() == 4) {
                topBorder = Integer.valueOf((String) borders.get(0)).intValue();
                rightBorder = Integer.valueOf((String) borders.get(1)).intValue();
                leftBorder = Integer.valueOf((String) borders.get(2)).intValue();
                bottomBorder = Integer.valueOf((String) borders.get(3)).intValue();
            }
        } catch (NumberFormatException e) {
            bottomBorder = 0;
            leftBorder = 0;
            rightBorder = 0;
            topBorder = 0;
        }
        DisplayMetrics matrix = this.mContext.getResources().getDisplayMetrics();
        topBorder = (int) TypedValue.applyDimension(1, (float) topBorder, matrix);
        rightBorder = (int) TypedValue.applyDimension(1, (float) rightBorder, matrix);
        leftBorder = (int) TypedValue.applyDimension(1, (float) leftBorder, matrix);
        bottomBorder = (int) TypedValue.applyDimension(1, (float) bottomBorder, matrix);
        if (modes.size() == 1) {
            verticalMode = (BorderModeType) modes.get(0);
            horizontalMode = verticalMode;
        } else if (modes.size() == 2) {
            horizontalMode = (BorderModeType) modes.get(0);
            verticalMode = (BorderModeType) modes.get(1);
        }
        int imgResId = this.mContext.getResources().getIdentifier("launchscreen_img", "drawable", this.mContext.getPackageName());
        if (imgResId == 0) {
            return null;
        }
        Bitmap img = BitmapFactory.decodeResource(this.mContext.getResources(), imgResId);
        if (img == null) {
            return null;
        }
        RelativeLayout relativeLayout = new RelativeLayout(this.mContext);
        relativeLayout.setLayoutParams(new LayoutParams(-1, -1));
        if (borders.size() == 0) {
            View imageView = new ImageView(this.mContext);
            imageView.setImageBitmap(img);
            ViewGroup.LayoutParams layoutParams = new LayoutParams(-2, -2);
            layoutParams.addRule(13, -1);
            relativeLayout.addView(imageView, layoutParams);
            return relativeLayout;
        }
        Display display = this.mActivity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        if ((this.mActivity.getWindow().getAttributes().flags & 1024) == 0) {
            size.y -= getStatusBarHeight();
        }
        View subImageView = getSubImageView(img, 0, 0, leftBorder, topBorder, BorderModeType.NONE, 0, 0);
        if (subImageView != null) {
            layoutParams = new LayoutParams(-2, -2);
            layoutParams.addRule(9, -1);
            layoutParams.addRule(10, -1);
            relativeLayout.addView(subImageView, layoutParams);
        }
        subImageView = getSubImageView(img, leftBorder, 0, (img.getWidth() - leftBorder) - rightBorder, topBorder, horizontalMode, (size.x - leftBorder) - rightBorder, 0);
        if (subImageView != null) {
            layoutParams = new LayoutParams(-1, -2);
            layoutParams.addRule(10, -1);
            layoutParams.addRule(14, -1);
            layoutParams.leftMargin = leftBorder;
            layoutParams.rightMargin = rightBorder;
            relativeLayout.addView(subImageView, layoutParams);
        }
        subImageView = getSubImageView(img, img.getWidth() - rightBorder, 0, rightBorder, topBorder, BorderModeType.NONE, 0, 0);
        if (subImageView != null) {
            layoutParams = new LayoutParams(-2, -2);
            layoutParams.addRule(11, -1);
            layoutParams.addRule(10, -1);
            relativeLayout.addView(subImageView, layoutParams);
        }
        subImageView = getSubImageView(img, 0, topBorder, leftBorder, (img.getHeight() - topBorder) - bottomBorder, verticalMode, 0, (size.y - topBorder) - bottomBorder);
        if (subImageView != null) {
            layoutParams = new LayoutParams(-2, -1);
            layoutParams.addRule(9, -1);
            layoutParams.addRule(13, -1);
            layoutParams.topMargin = topBorder;
            layoutParams.bottomMargin = bottomBorder;
            relativeLayout.addView(subImageView, layoutParams);
        }
        subImageView = getSubImageView(img, leftBorder, topBorder, (img.getWidth() - leftBorder) - rightBorder, (img.getHeight() - topBorder) - bottomBorder, BorderModeType.NONE, 0, 0);
        if (subImageView != null) {
            subImageView.setScaleType(ScaleType.FIT_XY);
            layoutParams = new LayoutParams(-1, -1);
            layoutParams.leftMargin = leftBorder;
            layoutParams.topMargin = topBorder;
            layoutParams.rightMargin = rightBorder;
            layoutParams.bottomMargin = bottomBorder;
            relativeLayout.addView(subImageView, layoutParams);
        }
        subImageView = getSubImageView(img, img.getWidth() - rightBorder, topBorder, rightBorder, (img.getHeight() - topBorder) - bottomBorder, verticalMode, 0, (size.y - topBorder) - bottomBorder);
        if (subImageView != null) {
            layoutParams = new LayoutParams(-2, -1);
            layoutParams.addRule(13, -1);
            layoutParams.addRule(11, -1);
            layoutParams.topMargin = topBorder;
            layoutParams.bottomMargin = bottomBorder;
            relativeLayout.addView(subImageView, layoutParams);
        }
        subImageView = getSubImageView(img, 0, img.getHeight() - bottomBorder, leftBorder, bottomBorder, BorderModeType.NONE, 0, 0);
        if (subImageView != null) {
            layoutParams = new LayoutParams(-2, -2);
            layoutParams.addRule(9, -1);
            layoutParams.addRule(12, -1);
            relativeLayout.addView(subImageView, layoutParams);
        }
        subImageView = getSubImageView(img, leftBorder, img.getHeight() - bottomBorder, (img.getWidth() - leftBorder) - rightBorder, bottomBorder, horizontalMode, (size.x - leftBorder) - rightBorder, 0);
        if (subImageView != null) {
            layoutParams = new LayoutParams(-1, -2);
            layoutParams.addRule(14, -1);
            layoutParams.addRule(12, -1);
            layoutParams.leftMargin = leftBorder;
            layoutParams.rightMargin = rightBorder;
            relativeLayout.addView(subImageView, layoutParams);
        }
        subImageView = getSubImageView(img, img.getWidth() - rightBorder, img.getHeight() - bottomBorder, rightBorder, bottomBorder, BorderModeType.NONE, 0, 0);
        if (subImageView == null) {
            return relativeLayout;
        }
        layoutParams = new LayoutParams(-2, -2);
        layoutParams.addRule(11, -1);
        layoutParams.addRule(12, -1);
        relativeLayout.addView(subImageView, layoutParams);
        return relativeLayout;
    }

    private void registerBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter(mIntentFilterStr);
        this.mLaunchScreenReadyWhenReceiver = new C03502();
        this.mContext.registerReceiver(this.mLaunchScreenReadyWhenReceiver, intentFilter);
    }

    private void hideLaunchScreenWhenReady() {
        if (this.mLaunchScreenDialog != null && this.mFirstFrameReceived) {
            if (this.mReadyWhen == ReadyWhenType.FIRST_PAINT) {
                performHideLaunchScreen();
            } else if (this.mReadyWhen == ReadyWhenType.USER_INTERACTIVE) {
                performHideLaunchScreen();
            } else if (this.mReadyWhen == ReadyWhenType.COMPLETE) {
                if (this.mPageLoadFinished) {
                    performHideLaunchScreen();
                }
            } else if (this.mReadyWhen == ReadyWhenType.CUSTOM && this.mCustomHideLaunchScreen) {
                performHideLaunchScreen();
            }
        }
    }

    private void performHideLaunchScreen() {
        if (this.mLaunchScreenDialog != null) {
            this.mLaunchScreenDialog.dismiss();
            this.mLaunchScreenDialog = null;
        }
        if (this.mReadyWhen == ReadyWhenType.CUSTOM) {
            this.mContext.unregisterReceiver(this.mLaunchScreenReadyWhenReceiver);
        }
    }

    private void setReadyWhen(String readyWhen) {
        if (readyWhen.equals("first-paint")) {
            this.mReadyWhen = ReadyWhenType.FIRST_PAINT;
        } else if (readyWhen.equals("user-interactive")) {
            this.mReadyWhen = ReadyWhenType.USER_INTERACTIVE;
        } else if (readyWhen.equals("complete")) {
            this.mReadyWhen = ReadyWhenType.COMPLETE;
        } else if (readyWhen.equals("custom")) {
            this.mReadyWhen = ReadyWhenType.CUSTOM;
        } else {
            this.mReadyWhen = ReadyWhenType.FIRST_PAINT;
        }
    }
}
