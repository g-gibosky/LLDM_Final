package org.crosswalk.engine;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.util.Log;
import android.webkit.ValueCallback;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class XWalkFileChooser {
    private static final String ALL_AUDIO_TYPES = "audio/*";
    private static final String ALL_IMAGE_TYPES = "image/*";
    private static final String ALL_VIDEO_TYPES = "video/*";
    private static final String ANY_TYPES = "*/*";
    private static final String AUDIO_TYPE = "audio/";
    private static final String IMAGE_TYPE = "image/";
    public static final int INPUT_FILE_REQUEST_CODE = 1;
    private static final String PATH_PREFIX = "file:";
    private static final String SPLIT_EXPRESSION = ",";
    private static final String TAG = "XWalkFileChooser";
    private static final String VIDEO_TYPE = "video/";
    private static final String WRITE_EXTERNAL_STORAGE = "android.permission.WRITE_EXTERNAL_STORAGE";
    private Activity mActivity;
    private String mCameraPhotoPath;
    private ValueCallback<Uri> mFilePathCallback;

    public XWalkFileChooser(Activity activity) {
        this.mActivity = activity;
    }

    public boolean showFileChooser(ValueCallback<Uri> uploadFile, String acceptType, String capture) {
        this.mFilePathCallback = uploadFile;
        Intent takePictureIntent = new Intent("android.media.action.IMAGE_CAPTURE");
        if (takePictureIntent.resolveActivity(this.mActivity.getPackageManager()) != null) {
            File photoFile = createImageFile();
            if (photoFile != null) {
                this.mCameraPhotoPath = PATH_PREFIX + photoFile.getAbsolutePath();
                takePictureIntent.putExtra("PhotoPath", this.mCameraPhotoPath);
                takePictureIntent.putExtra("output", Uri.fromFile(photoFile));
            } else {
                takePictureIntent = null;
            }
        }
        Intent camcorder = new Intent("android.media.action.VIDEO_CAPTURE");
        Intent soundRecorder = new Intent("android.provider.MediaStore.RECORD_SOUND");
        Intent contentSelectionIntent = new Intent("android.intent.action.GET_CONTENT");
        contentSelectionIntent.addCategory("android.intent.category.OPENABLE");
        ArrayList<Intent> extraIntents = new ArrayList();
        if (!(acceptType.contains(SPLIT_EXPRESSION) || acceptType.contains(ANY_TYPES))) {
            if (capture.equals("true")) {
                if (!acceptType.startsWith(IMAGE_TYPE)) {
                    if (acceptType.startsWith(VIDEO_TYPE)) {
                        this.mActivity.startActivityForResult(camcorder, 1);
                        Log.d(TAG, "Started camcorder");
                    } else if (acceptType.startsWith(AUDIO_TYPE)) {
                        this.mActivity.startActivityForResult(soundRecorder, 1);
                        Log.d(TAG, "Started sound recorder");
                    }
                    return true;
                } else if (takePictureIntent != null) {
                    this.mActivity.startActivityForResult(takePictureIntent, 1);
                    Log.d(TAG, "Started taking picture");
                    return true;
                }
            } else if (acceptType.startsWith(IMAGE_TYPE)) {
                if (takePictureIntent != null) {
                    extraIntents.add(takePictureIntent);
                }
                contentSelectionIntent.setType(ALL_IMAGE_TYPES);
            } else if (acceptType.startsWith(VIDEO_TYPE)) {
                extraIntents.add(camcorder);
                contentSelectionIntent.setType(ALL_VIDEO_TYPES);
            } else if (acceptType.startsWith(AUDIO_TYPE)) {
                extraIntents.add(soundRecorder);
                contentSelectionIntent.setType(ALL_AUDIO_TYPES);
            }
        }
        if (extraIntents.isEmpty() && canWriteExternalStorage()) {
            if (takePictureIntent != null) {
                extraIntents.add(takePictureIntent);
            }
            extraIntents.add(camcorder);
            extraIntents.add(soundRecorder);
            contentSelectionIntent.setType(ANY_TYPES);
        }
        Intent chooserIntent = new Intent("android.intent.action.CHOOSER");
        chooserIntent.putExtra("android.intent.extra.INTENT", contentSelectionIntent);
        if (!extraIntents.isEmpty()) {
            chooserIntent.putExtra("android.intent.extra.INITIAL_INTENTS", (Parcelable[]) extraIntents.toArray(new Intent[0]));
        }
        this.mActivity.startActivityForResult(chooserIntent, 1);
        Log.d(TAG, "Started chooser");
        return true;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && this.mFilePathCallback != null) {
            Log.d(TAG, "Activity result: " + resultCode);
            Uri results = null;
            if (-1 == resultCode) {
                if (data != null && (data.getAction() != null || data.getData() != null)) {
                    String dataString = data.getDataString();
                    if (dataString != null) {
                        results = Uri.parse(dataString);
                    }
                    deleteImageFile();
                } else if (this.mCameraPhotoPath != null) {
                    results = Uri.parse(this.mCameraPhotoPath);
                }
            } else if (resultCode == 0) {
                deleteImageFile();
            }
            if (results != null) {
                Log.d(TAG, "Received file: " + results.toString());
            }
            this.mFilePathCallback.onReceiveValue(results);
            this.mFilePathCallback = null;
        }
    }

    private boolean canWriteExternalStorage() {
        boolean z = false;
        try {
            z = Arrays.asList(this.mActivity.getPackageManager().getPackageInfo(this.mActivity.getPackageName(), 4096).requestedPermissions).contains(WRITE_EXTERNAL_STORAGE);
        } catch (NameNotFoundException e) {
        } catch (NullPointerException e2) {
        }
        return z;
    }

    private File createImageFile() {
        if (Environment.getExternalStorageState().equals("mounted")) {
            String imageFileName = "JPEG_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + "_";
            File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            if (!storageDir.exists()) {
                storageDir.mkdirs();
            }
            try {
                File file = File.createTempFile(imageFileName, ".jpg", storageDir);
                Log.d(TAG, "Created image file: " + file.getAbsolutePath());
                return file;
            } catch (IOException e) {
                Log.e(TAG, "Unable to create Image File, please make sure permission 'WRITE_EXTERNAL_STORAGE' was added.");
                return null;
            }
        }
        Log.e(TAG, "External storage is not mounted.");
        return null;
    }

    private boolean deleteImageFile() {
        if (this.mCameraPhotoPath == null || !this.mCameraPhotoPath.contains(PATH_PREFIX)) {
            return false;
        }
        String filePath = this.mCameraPhotoPath.split(PATH_PREFIX)[1];
        boolean result = new File(filePath).delete();
        Log.d(TAG, "Delete image file: " + filePath + " result: " + result);
        return result;
    }
}
