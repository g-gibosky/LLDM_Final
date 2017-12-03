package org.xwalk.core;

import SevenZip.Compression.LZMA.Decoder;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.os.SystemClock;
import android.util.Log;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

class XWalkDecompressor {
    private static final int LZMA_OUTSIZE = 8;
    private static final int LZMA_PROP_SIZE = 5;
    private static final String[] MANDATORY_LIBRARIES = new String[]{"libxwalkcore.so"};
    private static final String[] MANDATORY_RESOURCES = new String[]{"libxwalkcore.so", "classes.dex", "icudtl.dat", "xwalk.pak", "xwalk_100_percent.pak"};
    private static final int STREAM_BUFFER_SIZE = 4096;
    private static final String TAG = "XWalkLib";

    private static class DecompressResourceTask implements Callable<Boolean> {
        File mDestFile;
        ZipEntry mZipEntry;
        ZipFile mZipFile;

        DecompressResourceTask(ZipFile zipFile, ZipEntry zipEntry, File destFile) {
            this.mZipFile = zipFile;
            this.mZipEntry = zipEntry;
            this.mDestFile = destFile;
        }

        public Boolean call() {
            try {
                Log.d(XWalkDecompressor.TAG, "Decompressing " + this.mZipEntry.getName());
                XWalkDecompressor.extractLzmaToFile(this.mZipFile.getInputStream(this.mZipEntry), this.mDestFile);
                return Boolean.valueOf(true);
            } catch (IOException e) {
                Log.e(XWalkDecompressor.TAG, e.getLocalizedMessage());
                return Boolean.valueOf(false);
            }
        }
    }

    XWalkDecompressor() {
    }

    public static boolean isLibraryCompressed() {
        String[] arr$ = MANDATORY_LIBRARIES;
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            try {
                try {
                    openRawResource(arr$[i$]).close();
                } catch (IOException e) {
                }
                i$++;
            } catch (NotFoundException e2) {
                return false;
            }
        }
        return true;
    }

    public static boolean decompressLibrary() {
        String libDir = XWalkEnvironment.getPrivateDataDir();
        File f = new File(libDir);
        if (f.exists() && f.isFile()) {
            f.delete();
        }
        if (!f.exists() && !f.mkdirs()) {
            return false;
        }
        long start = SystemClock.uptimeMillis();
        String[] arr$ = MANDATORY_LIBRARIES;
        int len$ = arr$.length;
        int i$ = 0;
        while (i$ < len$) {
            String library = arr$[i$];
            try {
                Log.d(TAG, "Decompressing " + library);
                extractLzmaToFile(openRawResource(library), new File(libDir, library));
                i$++;
            } catch (NotFoundException e) {
                Log.d(TAG, library + " not found");
                return false;
            } catch (IOException e2) {
                Log.e(TAG, e2.getLocalizedMessage());
                return false;
            }
        }
        Log.d(TAG, "Time to decompress : " + (SystemClock.uptimeMillis() - start) + " ms");
        return true;
    }

    public static boolean isResourceCompressed(String libFile) {
        Throwable th;
        ZipFile zipFile = null;
        try {
            ZipFile zipFile2 = new ZipFile(libFile);
            try {
                for (String resource : MANDATORY_RESOURCES) {
                    if (zipFile2.getEntry("assets" + File.separator + resource + ".lzma") == null) {
                        try {
                            zipFile2.close();
                        } catch (IOException e) {
                        } catch (NullPointerException e2) {
                        }
                        zipFile = zipFile2;
                        return false;
                    }
                }
                try {
                    zipFile2.close();
                } catch (IOException e3) {
                } catch (NullPointerException e4) {
                }
                zipFile = zipFile2;
                return true;
            } catch (IOException e5) {
                zipFile = zipFile2;
                try {
                    zipFile.close();
                    return false;
                } catch (IOException e6) {
                    return false;
                } catch (NullPointerException e7) {
                    return false;
                }
            } catch (Throwable th2) {
                th = th2;
                zipFile = zipFile2;
                try {
                    zipFile.close();
                } catch (IOException e8) {
                } catch (NullPointerException e9) {
                }
                throw th;
            }
        } catch (IOException e10) {
            zipFile.close();
            return false;
        } catch (Throwable th3) {
            th = th3;
            zipFile.close();
            throw th;
        }
    }

    public static boolean extractResource(String libFile, String destDir) {
        IOException e;
        Throwable th;
        Log.d(TAG, "Extract resource from Apk " + libFile);
        long start = SystemClock.uptimeMillis();
        ZipFile zipFile = null;
        try {
            ZipFile zipFile2 = new ZipFile(libFile);
            try {
                for (String resource : MANDATORY_RESOURCES) {
                    ZipEntry entry;
                    if (isNativeLibrary(resource)) {
                        String abi = XWalkEnvironment.getDeviceAbi();
                        entry = zipFile2.getEntry("lib" + File.separator + abi + File.separator + resource);
                        if (entry == null && XWalkEnvironment.is64bitDevice()) {
                            if (abi.equals("arm64-v8a")) {
                                abi = "armeabi-v7a";
                            } else if (abi.equals("x86_64")) {
                                abi = "x86";
                            }
                            entry = zipFile2.getEntry("lib" + File.separator + abi + File.separator + resource);
                        }
                    } else {
                        entry = isAsset(resource) ? zipFile2.getEntry("assets" + File.separator + resource) : zipFile2.getEntry(resource);
                    }
                    if (entry == null) {
                        Log.e(TAG, resource + " not found");
                        try {
                            zipFile2.close();
                        } catch (IOException e2) {
                        } catch (NullPointerException e3) {
                        }
                        zipFile = zipFile2;
                        return false;
                    }
                    Log.d(TAG, "Extracting " + resource);
                    extractStreamToFile(zipFile2.getInputStream(entry), new File(destDir, resource));
                }
                try {
                    zipFile2.close();
                } catch (IOException e4) {
                } catch (NullPointerException e5) {
                }
                Log.d(TAG, "Time to extract : " + (SystemClock.uptimeMillis() - start) + " ms");
                zipFile = zipFile2;
                return true;
            } catch (IOException e6) {
                e = e6;
                zipFile = zipFile2;
            } catch (Throwable th2) {
                th = th2;
                zipFile = zipFile2;
            }
        } catch (IOException e7) {
            e = e7;
            try {
                Log.d(TAG, e.getLocalizedMessage());
                try {
                    zipFile.close();
                    return false;
                } catch (IOException e8) {
                    return false;
                } catch (NullPointerException e9) {
                    return false;
                }
            } catch (Throwable th3) {
                th = th3;
                try {
                    zipFile.close();
                } catch (IOException e10) {
                } catch (NullPointerException e11) {
                }
                throw th;
            }
        }
    }

    public static boolean decompressResource(String libFile, String destDir) {
        IOException e;
        Throwable th;
        Log.d(TAG, "Decompress resource from Apk " + libFile);
        long start = SystemClock.uptimeMillis();
        List<Callable<Boolean>> taskList = new ArrayList(MANDATORY_RESOURCES.length);
        ExecutorService pool = Executors.newFixedThreadPool(MANDATORY_RESOURCES.length);
        ZipFile zipFile = null;
        boolean success = true;
        try {
            ZipFile zipFile2 = new ZipFile(libFile);
            try {
                for (String resource : MANDATORY_RESOURCES) {
                    ZipEntry entry = zipFile2.getEntry("assets" + File.separator + resource + ".lzma");
                    if (entry == null) {
                        Log.e(TAG, resource + " not found");
                        try {
                            zipFile2.close();
                        } catch (IOException e2) {
                        } catch (NullPointerException e3) {
                        }
                        zipFile = zipFile2;
                        return false;
                    }
                    taskList.add(new DecompressResourceTask(zipFile2, entry, new File(destDir, resource)));
                }
                try {
                    for (Future<Boolean> f : pool.invokeAll(taskList)) {
                        success &= ((Boolean) f.get()).booleanValue();
                    }
                    pool.shutdown();
                    try {
                        zipFile2.close();
                    } catch (IOException e4) {
                    } catch (NullPointerException e5) {
                    }
                    Log.d(TAG, "Time to decompress : " + (SystemClock.uptimeMillis() - start) + " ms");
                    zipFile = zipFile2;
                    return success;
                } catch (Exception e6) {
                    Log.d(TAG, "Failed to execute decompression");
                    pool.shutdown();
                    try {
                        zipFile2.close();
                    } catch (IOException e7) {
                    } catch (NullPointerException e8) {
                    }
                    zipFile = zipFile2;
                    return false;
                } catch (RejectedExecutionException e9) {
                    RejectedExecutionException rejectedExecutionException = e9;
                    Log.d(TAG, "Failed to execute decompression");
                    pool.shutdown();
                    zipFile2.close();
                    zipFile = zipFile2;
                    return false;
                } catch (Exception e10) {
                    Log.d(TAG, "Failed to execute decompression");
                    pool.shutdown();
                    zipFile2.close();
                    zipFile = zipFile2;
                    return false;
                }
            } catch (IOException e11) {
                e = e11;
                zipFile = zipFile2;
            } catch (Throwable th2) {
                th = th2;
                zipFile = zipFile2;
            }
        } catch (IOException e12) {
            e = e12;
            try {
                Log.d(TAG, e.getLocalizedMessage());
                try {
                    zipFile.close();
                    return false;
                } catch (IOException e13) {
                    return false;
                } catch (NullPointerException e14) {
                    return false;
                }
            } catch (Throwable th3) {
                th = th3;
                try {
                    zipFile.close();
                } catch (IOException e15) {
                } catch (NullPointerException e16) {
                }
                throw th;
            }
        }
    }

    private static boolean isNativeLibrary(String resource) {
        return resource.endsWith(".so");
    }

    private static boolean isAsset(String resource) {
        return resource.endsWith(".dat") || resource.endsWith(".pak");
    }

    private static InputStream openRawResource(String library) throws NotFoundException {
        Context context = XWalkEnvironment.getApplicationContext();
        Resources res = context.getResources();
        return res.openRawResource(res.getIdentifier(library.split("\\.")[0], "raw", context.getPackageName()));
    }

    private static void extractLzmaToFile(InputStream srcStream, File destFile) throws IOException {
        IOException e;
        Throwable th;
        InputStream input = null;
        OutputStream output = null;
        try {
            OutputStream output2;
            InputStream input2 = new BufferedInputStream(srcStream);
            try {
                output2 = new BufferedOutputStream(new FileOutputStream(destFile));
            } catch (IOException e2) {
                e = e2;
                input = input2;
                try {
                    if (destFile.isFile()) {
                        destFile.delete();
                    }
                    throw e;
                } catch (Throwable th2) {
                    th = th2;
                    try {
                        output.flush();
                    } catch (IOException e3) {
                    } catch (NullPointerException e4) {
                    }
                    try {
                        output.close();
                    } catch (IOException e5) {
                    } catch (NullPointerException e6) {
                    }
                    try {
                        input.close();
                    } catch (IOException e7) {
                    } catch (NullPointerException e8) {
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                input = input2;
                output.flush();
                output.close();
                input.close();
                throw th;
            }
            try {
                byte[] properties = new byte[5];
                if (input2.read(properties, 0, 5) != 5) {
                    throw new IOException("Input lzma file is too short");
                }
                Decoder decoder = new Decoder();
                if (decoder.SetDecoderProperties(properties)) {
                    long outSize = 0;
                    for (int i = 0; i < 8; i++) {
                        int v = input2.read();
                        if (v < 0) {
                            Log.w(TAG, "Can't read stream size");
                        }
                        outSize |= ((long) v) << (i * 8);
                    }
                    if (decoder.Code(input2, output2, outSize)) {
                        try {
                            output2.flush();
                        } catch (IOException e9) {
                        } catch (NullPointerException e10) {
                        }
                        try {
                            output2.close();
                        } catch (IOException e11) {
                        } catch (NullPointerException e12) {
                        }
                        try {
                            input2.close();
                            return;
                        } catch (IOException e13) {
                            return;
                        } catch (NullPointerException e14) {
                            return;
                        }
                    }
                    throw new IOException("Error in data stream");
                }
                throw new IOException("Incorrect lzma properties");
            } catch (IOException e15) {
                e = e15;
                output = output2;
                input = input2;
                if (destFile.isFile()) {
                    destFile.delete();
                }
                throw e;
            } catch (Throwable th4) {
                th = th4;
                output = output2;
                input = input2;
                output.flush();
                output.close();
                input.close();
                throw th;
            }
        } catch (IOException e16) {
            e = e16;
            if (destFile.isFile()) {
                destFile.delete();
            }
            throw e;
        }
    }

    private static void extractStreamToFile(InputStream input, File file) throws IOException {
        IOException e;
        Throwable th;
        OutputStream outputStream = null;
        try {
            input.available();
            OutputStream output = new FileOutputStream(file);
            try {
                byte[] buffer = new byte[4096];
                while (true) {
                    int len = input.read(buffer);
                    if (len >= 0) {
                        output.write(buffer, 0, len);
                    } else {
                        try {
                            break;
                        } catch (IOException e2) {
                        } catch (NullPointerException e3) {
                        }
                    }
                }
                output.flush();
                try {
                    output.close();
                } catch (IOException e4) {
                } catch (NullPointerException e5) {
                }
                try {
                    input.close();
                } catch (IOException e6) {
                } catch (NullPointerException e7) {
                }
            } catch (IOException e8) {
                e = e8;
                outputStream = output;
                try {
                    if (file.isFile()) {
                        file.delete();
                    }
                    throw e;
                } catch (Throwable th2) {
                    th = th2;
                    try {
                        outputStream.flush();
                    } catch (IOException e9) {
                    } catch (NullPointerException e10) {
                    }
                    try {
                        outputStream.close();
                    } catch (IOException e11) {
                    } catch (NullPointerException e12) {
                    }
                    try {
                        input.close();
                    } catch (IOException e13) {
                    } catch (NullPointerException e14) {
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                outputStream = output;
                outputStream.flush();
                outputStream.close();
                input.close();
                throw th;
            }
        } catch (IOException e15) {
            e = e15;
            if (file.isFile()) {
                file.delete();
            }
            throw e;
        }
    }
}
