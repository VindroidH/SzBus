package com.vindroid.szbus.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class FileKit {
    public static boolean save(Context context, String data, String filename) {
        if (TextUtils.isEmpty(data)) {
            return false;
        }
        return save(context, data.getBytes(), filename);
    }

    public static boolean save(Context context, byte[] data, String filename) {
        FileOutputStream fos = null;
        try {
            try {
                fos = context.openFileOutput(filename, 0);
                fos.write(data);
                fos.flush();
                try {
                    fos.close();
                } catch (Exception ignored) {
                }
            } catch (Exception e2) {
                Log.e("FileKit", e2.getMessage(), e2);
            }
            return false;
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception ignored) {
            }
        }
    }

    public static String getString(Context context, String filename) {
        String result = null;
        FileInputStream fis = null;
        try {
            try {
                if (exists(context, filename)) {
                    fis = context.openFileInput(filename);
                    result = StringKit.stream2String(fis);
                }
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (Exception e) {
                        Log.e("FileKit", e.getMessage(), e);
                    }
                }
            } catch (Exception e2) {
                Log.e("FileKit", e2.getMessage(), e2);
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (Exception e3) {
                        Log.e("FileKit", e3.getMessage(), e3);
                    }
                }
            }
            return result;
        } catch (Throwable th) {
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception e4) {
                    Log.e("FileKit", e4.getMessage(), e4);
                }
            }
            throw th;
        }
    }

    public static boolean save(Context context, Object object, String filename) {
        boolean result = false;
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = context.openFileOutput(filename, 0);
            ObjectOutputStream oos2 = new ObjectOutputStream(fos);
            try {
                oos2.writeObject(object);
                result = true;
                try {
                    oos2.close();
                } catch (Exception e) {
                    Log.e("FileKit", e.getMessage(), e);
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception e) {
                oos = oos2;
                Log.e("FileKit", e.getMessage(), e);
                try {
                    oos.close();
                } catch (Exception e3) {
                    Log.e("FileKit", e3.getMessage(), e3);
                }
                if (fos != null) {
                    fos.close();
                }
                return result;
            }
        } catch (Exception e) {
            Log.e("FileKit", e.getMessage(), e);
        }
        return result;
    }

    public static Object getObject(Context context, String filename) {
        Object object = null;
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            if (exists(context, filename)) {
                fis = context.openFileInput(filename);
                ObjectInputStream ois2 = new ObjectInputStream(fis);
                try {
                    object = ois2.readObject();
                    ois = ois2;
                } catch (Exception e) {
                    e = e;
                    ois = ois2;
                    Log.e("FileKit", e.getMessage(), e);
                    try {
                        ois.close();
                    } catch (Exception e2) {
                        Log.e("FileKit", e2.getMessage(), e2);
                    }
                    if (fis != null) {
                        fis.close();
                    }
                    return object;
                }
            }
            if (ois != null) {
                try {
                    ois.close();
                } catch (Exception e4) {
                    Log.e("FileKit", e4.getMessage(), e4);
                }
            }
            if (fis != null) {
                fis.close();
            }
        } catch (Exception e) {
            Log.e("FileKit", e.getMessage(), e);
        }
        return object;
    }

    public static boolean remove(Context context, String filename) {
        boolean flag = false;
        if (context == null || filename == null) {
            return false;
        }
        File file = context.getFileStreamPath(filename);
        if (file != null && file.exists() && file.isFile()) {
            flag = file.delete();
        }
        return flag;
    }

    public static boolean exists(Context context, String filename) {
        File file = context.getFileStreamPath(filename);
        return file != null && file.exists();
    }
}
