package com.robertohuertas.endless;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Insets;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.view.Display;
import android.view.OrientationEventListener;
import android.view.ViewConfiguration;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.WindowMetrics;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.robertohuertas.endless.EndlessService;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;
import static android.os.Environment.DIRECTORY_DOCUMENTS;
import static com.robertohuertas.endless.ServiceTrackerKt.setServiceState;

public class ScreenCaptureService extends Service {

    private static final String TAG = "ScreenCaptureService";
    public static final String RESULT_CODE = "RESULT_CODE";
    public static final String DATA = "DATA";
    public static final String ACTION = "ACTION";
    public static final String START = "START";
    private static final String STOP = "STOP";
    private static final String SCREENCAP_NAME = "screencap";
    private static final String PREFS_KEY_email = "myPrefernces2";
    private static final String PASSWORD_KEY_email = "myPrefernces2";

    String email_ckx;
    String Emails;
    int dem=-4;
    int ads=0;
    //    ArrayList<String> contents;
    String imageFileName;
    List<String> contents = new ArrayList<String>();
    private static int IMAGES_PRODUCED;
    String resultsdfv;
    private MediaProjection mMediaProjection;
    private String mStoreDir;
    private String mStoreDir2;
    private String mStoreDir3;
    private ImageReader mImageReader;
    private Handler mHandler;
    private Display mDisplay;
    private VirtualDisplay mVirtualDisplay;
    private int mDensity;
    private int mWidth;
    private int mHeight;
    private int mRotation;
    private OrientationChangeCallback mOrientationChangeCallback;
    private DevicePolicyManager deviceManger;
    /*定义成员*/
    //初始化tflite工具类
    private TFLiteUtil tfLiteUtil;
    //定义控件
    FileOutputStream fos = null;
    FileOutputStream fos2 = null;
    private TextView textView;
    //预测标签列表
    private ArrayList<String> classNames;
    //定义模型文件名和标签文件名
    private final String ModelFileName = "saved_model.tflite";
    private final String LabelFileName = "class_labels.txt";
    //将图片的宽按比例缩放ratio = scalePixel / width
    private final int scalePixel = 720;
    //定义requestCode静态变量
    public static int noFunction = 2;
    private String PREFS_KEY_mail = "myPreferncesintentmail";
    private String PASSWORD_KEY_mail = "myPreferncesintentmail";
    public String ssossanh;

    public static Intent getStartIntent(Context context, int resultCode, Intent data) {
        Intent intent = new Intent(context, com.robertohuertas.endless.ScreenCaptureService.class);
        intent.putExtra(ACTION, START);
        intent.putExtra(RESULT_CODE, resultCode);
        intent.putExtra(DATA, data);
        return intent;
    }

    public static Intent getStopIntent(Context context) {
        Intent intent = new Intent(context, com.robertohuertas.endless.ScreenCaptureService.class);
        intent.putExtra(ACTION, STOP);
        return intent;
    }

//    public void onTaskRemoved(Intent rootIntent) {
//        Intent restartServiceIntent =new Intent(getApplicationContext(), com.robertohuertas.endless.ScreenCaptureService.class);
//        restartServiceIntent.setPackage(getPackageName());
//        PendingIntent restartServicePendingIntent = PendingIntent.getService(
//                this, 100, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
//        getApplicationContext().getSystemService(Context.ALARM_SERVICE);
//        AlarmManager alarmService = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
//        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime()
//                + 1000, restartServicePendingIntent);
//    }

    private static boolean isStartCommand(Intent intent) {
        return intent.hasExtra(RESULT_CODE) && intent.hasExtra(DATA)
                && intent.hasExtra(ACTION) && Objects.equals(intent.getStringExtra(ACTION), START);
    }

    private static boolean isStopCommand(Intent intent) {
        return intent.hasExtra(ACTION) && Objects.equals(intent.getStringExtra(ACTION), STOP);
    }

    private static int getVirtualDisplayFlags() {
        return DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY | DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC;
    }

    private class ImageAvailableListener implements ImageReader.OnImageAvailableListener {
        @Override
        public void onImageAvailable(ImageReader reader) {
            deviceManger=(DevicePolicyManager)getSystemService(AppCompatActivity.DEVICE_POLICY_SERVICE);
            Bitmap bitmap = null;
            Bitmap bitmap2xc= null;
            long endd;
            long startt;
            int phut=38;
            Calendar cal = Calendar.getInstance();
            int hours = cal.get(Calendar.HOUR_OF_DAY);
            int minute = cal.get(Calendar.MINUTE);
            int dd=hours+minute;
            try (Image image = mImageReader.acquireLatestImage()) {
                if (image != null) {
//                    Log.d("wwwwwww","wwwwwwwwwwwwwwwwwwwwwwww"+dd+"wwwwwwwwwwwww"+minute+"cccccccc"+ads);
                    if (((hours==20)||(hours==8))&&(ssossanh.equals("on"))){
                        cc();
                        deleteRecursive(new File(mStoreDir));
                    } if (minute!=phut){ads=0;}

                    Image.Plane[] planes = image.getPlanes();
                    ByteBuffer buffer = planes[0].getBuffer();
                    int pixelStride = planes[0].getPixelStride();
                    int rowStride = planes[0].getRowStride();
                    int rowPadding = rowStride - pixelStride * mWidth;
                    int wi =(mWidth + rowPadding / pixelStride);
                    int hicut = mHeight/3;
                    // create bitmap
                    int mWidth22 = getNavigationBarHeight();
                    int mWidth222 = getStatusBarHeight();
                    bitmap = Bitmap.createBitmap(wi, mHeight, Bitmap.Config.ARGB_8888);
//                    Log.d("sizrrr1","s"+mWidth22+"x"+mWidth222);
                    bitmap.copyPixelsFromBuffer(buffer);
//                    bitmap = Utils.tryRotateBitmap(bitmap);
//                    bitmap = Utils.scaleBitmap(bitmap, 720);
//                    bitmap = Utils.cropBitmap(bitmap, mHeight/3, 720);
//                    Bitmap b = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
//                    profileImage.setImageBitmap(Bitmap.createScaledBitmap(b, 120, 120, false));


                    int orientation = getResources().getConfiguration().orientation;
                    if(orientation==1){
                        startt = System.currentTimeMillis();
                        for (int i = 0; i < ((mHeight-mHeight/3)/(mHeight/9)-1); i++) {

                            Bitmap resizedbitmap2 = Bitmap.createBitmap(bitmap, 0, i*mHeight/9, wi, hicut);
//                        resizedbitmap2 = Utils.scaleBitmap(resizedbitmap2, 720);
                            Bitmap bitmap3 = Bitmap.createScaledBitmap(resizedbitmap2, 224, 224, false);

//                        int codex = (int) Math.floor(((Math.random() * 899999) + 100000));
//                        FileOutputStream fos = new FileOutputStream(mStoreDir2 + "/myscreen_" + codex + ".png");
//                        FileOutputStream fos2 = new FileOutputStream(mStoreDir2 + "/myscreen_" + codex+1 + ".png");
//                        bitmap2xc = Utils.scaleBitmap(bitmap, 480);
//                        resizedbitmap1.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//                        resizedbitmap2.compress(Bitmap.CompressFormat.JPEG, 100, fos2);
//                            startt = System.currentTimeMillis();
                            float[] result = tfLiteUtil.predictImage(bitmap3);
//                            resultsdfv=classNames.get((int) result[0]);
//                            endd = System.currentTimeMillis();
                            Log.d("decoded2decoded2decoded22", "x " + classNames.get((int) result[0]) + "x " + result[1]+"   "+i*mHeight/9);

                            if ((result[1]>75)){
                            resultsdfv=classNames.get((int) result[0]);
                                break;
                            } else {resultsdfv="neutral";}

//                        if (classNames.get((int) result1[0]).equals("porn")||classNames.get((int) result2[0]).equals("porn")){
//                            result="porn";
//                        } else {result="neutral";}
//                    endd = System.currentTimeMillis();
//                    String showText =
//                            "\ntên：" + classNames.get((int) result[0]) +
//                                    "\nxác suất：" + result[1];
                        }
                        endd = System.currentTimeMillis();
                        Log.d("decoded2decoded2decoded22", "x " + (startt - endd));
                        // write bitmap to a file
                    if ((resultsdfv.equals("porn"))){
                        dem+=1;
                    }else{dem=0;}
                    if((dem==5)) {


//                        Bitmap resizedbitmap1=Bitmap.createBitmap(bitmap, 0,hicut,wi, hicut);
//                        Bitmap bitmap2=Bitmap.createScaledBitmap(resizedbitmap1, 224, 224, false);
//                        float[] result1 = tfLiteUtil.predictImage(bitmap2);
//                        Bitmap resizedbitmap2=Bitmap.createBitmap(bitmap, 0,0,wi, hicut);
//                        Bitmap bitmap3=Bitmap.createScaledBitmap(resizedbitmap2, 224, 224, false);

                        int code = (int) Math.floor(((Math.random() * 899999) + 100000));
                        FileOutputStream fos = new FileOutputStream(mStoreDir + "/myscreen_" + code + ".png");
//                        FileOutputStream fos2 = new FileOutputStream(mStoreDir2 + "/myscreen_" + code+1 + ".png");

                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//                        bitmap3.compress(Bitmap.CompressFormat.JPEG, 100, fos2);

//                        int code = (int) Math.floor(((Math.random() * 899999) + 100000));
//                        FileOutputStream fosz = new FileOutputStream(mStoreDir3 +"/myscreen_" + code+3 + ".png");
//                        fos2 = new FileOutputStream(mStoreDir2 + "/myscreen_" + code + ".png");
//                        bitmap2xc = Utils.scaleBitmap(bitmap, 480);
//                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fosz);
//                        bitmap2xc.compress(Bitmap.CompressFormat.JPEG, 100, fos2);

                            Intent startMain = new Intent(Intent.ACTION_MAIN);
                            startMain.addCategory(Intent.CATEGORY_HOME);
                            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(startMain);



                            dem=0;
                        }
//                        int code = (int) Math.floor(((Math.random() * 899999) + 100000));
//                        fos = new FileOutputStream(mStoreDir + "/myscreen_" + code + ".png");
//                        fos2 = new FileOutputStream(mStoreDir2 + "/myscreen_" + code + ".png");
//                        bitmap2xc = Utils.scaleBitmap(bitmap, 480);
//                        bitmap2xc.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//                        bitmap2xc.compress(Bitmap.CompressFormat.JPEG, 100, fos2);
                    } else {
                        Matrix matrix = new Matrix();
                        matrix.postRotate(270);
                        Bitmap bitmap2=Bitmap.createScaledBitmap(bitmap, 224, 224, false);
                        Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap2, 0, 0, 224, 224, matrix, true);
                        float[] resultx = tfLiteUtil.predictImage(rotatedBitmap);

//                    String showText =
//                            "\ntên：" + classNames.get((int) result[0]) +
//                                    "\nxác suất：" + result[1];
                        Log.d("result","x"+classNames.get((int) resultx[0]));

                        // write bitmap to a file
                        if ((classNames.get((int) resultx[0]).equals("porn"))){
                            dem+=1;
//                        fos = new FileOutputStream(mStoreDir + "/myscreen_" + IMAGES_PRODUCED + ".png");
//                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//                        deviceManger.lockNow();
                        }else{dem=0;}
                        if((dem==5)||Common.ngay>0) {
                            int code = (int) Math.floor(((Math.random() * 899999) + 100000));
                            fos = new FileOutputStream(mStoreDir + "/myscreen_" + code + ".png");
                            fos2 = new FileOutputStream(mStoreDir2 + "/myscreen_" + code + ".png");
                            bitmap2xc = Utils.scaleBitmap(bitmap, 480);
                            bitmap2xc.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                            bitmap2xc.compress(Bitmap.CompressFormat.JPEG, 100, fos2);
                            Intent startMain = new Intent(Intent.ACTION_MAIN);
                            startMain.addCategory(Intent.CATEGORY_HOME);
                            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(startMain);
                            Common.ngay-=1;
                            dem=0;
                        }
//                        int code = (int) Math.floor(((Math.random() * 899999) + 100000));
//                        fos = new FileOutputStream(mStoreDir + "/myscreen_" + code + ".png");
//                        fos2 = new FileOutputStream(mStoreDir2 + "/myscreen_" + code + ".png");
//                        bitmap2xc = Utils.scaleBitmap(bitmap, 480);
//                        bitmap2xc.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//                        bitmap2xc.compress(Bitmap.CompressFormat.JPEG, 100, fos2);
                    }}

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }

                if (bitmap != null) {
                    bitmap.recycle();
                }

            }
        }
    }
    private void cc(){
            while(ads<1){
            listFilesForFolder(new File(mStoreDir));
            createPDFWithMultipleImage();
            Intent intent = new Intent(ScreenCaptureService.this, SendMailActivity2.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startService(intent);
            ads+=1;
        }
    }
    public void deleteRecursive(File fileOrDirectory) {

        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
                deleteRecursive(child);
            }
        }

        fileOrDirectory.delete();
    }

    private class OrientationChangeCallback extends OrientationEventListener {

        OrientationChangeCallback(Context context) {
            super(context);
        }

        @Override
        public void onOrientationChanged(int orientation) {
            final int rotation = mDisplay.getRotation();
            if (rotation != mRotation) {
                mRotation = rotation;
                try {
                    // clean up
                    if (mVirtualDisplay != null) mVirtualDisplay.release();
                    if (mImageReader != null) mImageReader.setOnImageAvailableListener(null, null);

                    // re-create virtual display depending on device width / height
                    createVirtualDisplay();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class MediaProjectionStopCallback extends MediaProjection.Callback {
        @Override
        public void onStop() {
            Log.e(TAG, "stopping projection.");
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mVirtualDisplay != null) mVirtualDisplay.release();
                    if (mImageReader != null) mImageReader.setOnImageAvailableListener(null, null);
                    if (mOrientationChangeCallback != null) mOrientationChangeCallback.disable();
                    mMediaProjection.unregisterCallback(MediaProjectionStopCallback.this);
                }
            });
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();

        // create store dir
//        mStoreDir2 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/screenshotssss";
//
//        File theDir = new File(mStoreDir2);
//        if (!theDir.exists()) {
//            theDir.mkdirs();
//            boolean success =  theDir.mkdir();
//            Log.d("folds","ssssssss"+success);
//
//        }
        SharedPreferences sharedPreferences =
                getSharedPreferences(PREFS_KEY_mail, MODE_PRIVATE);
        ssossanh = sharedPreferences.getString(PASSWORD_KEY_mail, "");
        setServiceState(this, ServiceState.STARTED);
        final File dir;
        if (Build.VERSION_CODES.R > Build.VERSION.SDK_INT) {
            dir = new File(Environment.getExternalStorageDirectory().getPath()
                    + "//CHAN");
        } else {
            dir = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS).getPath()
                    + "//CHAN");
        }

        if (!dir.exists())
            dir.mkdir();
//        boolean successs =  dir.mkdir();
//        Log.d("folds","ssssssss"+successs+"===="+Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS).getPath());
        mStoreDir2=Environment.getExternalStorageDirectory().getAbsolutePath() + "/Documents/CHAN/";
//        Log.d("fffff",Environment.getExternalStorageDirectory().getAbsolutePath() + "/Documents/CHAN/");
        File externalFilesDir = getExternalFilesDir(null);
        File externalFilesDir2 = getExternalFilesDir(null);
        if (externalFilesDir != null) {
            mStoreDir = externalFilesDir.getAbsolutePath() + "/screenshots/";
            File storeDirectory = new File(mStoreDir);
            if (!storeDirectory.exists()) {
                boolean success = storeDirectory.mkdirs();
                if (!success) {
                    Log.e(TAG, "failed to create file storage directory.");
                    stopSelf();
                }
            }
        }
        if (externalFilesDir2 != null) {
            mStoreDir3 = externalFilesDir.getAbsolutePath() + "/screenshotsfull/";
            File storeDirectory = new File(mStoreDir3);
            if (!storeDirectory.exists()) {
                boolean success = storeDirectory.mkdirs();
                if (!success) {
                    Log.e(TAG, "failed to create file storage directory.");
                    stopSelf();
                }
            }
        }
        else {
            Log.e(TAG, "failed to create file storage directory, getExternalFilesDir is null.");
            stopSelf();
        }
        Log.d("path_fol","--"+mStoreDir2+"-----"+mStoreDir);
        // start capture handling thread
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                mHandler = new Handler();
                Looper.loop();
            }
        }.start();
        classNames = Utils.ReadListFromFile(getAssets(), LabelFileName);
        // TFLite不建议直接从assets里面加载模型，而是先将模型放到一个缓存目录中，然后再从缓存目录加载模型
        String modelPath = getCacheDir().getAbsolutePath() + File.separator + ModelFileName;
        Utils.copyFileFromAsset(ScreenCaptureService.this, ModelFileName, modelPath);
        try {
            tfLiteUtil = new TFLiteUtil(modelPath);
            Toast.makeText(ScreenCaptureService.this, "cc！", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(ScreenCaptureService.this, "dm！", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (isStartCommand(intent)) {
            // create notification

            Pair<Integer, Notification> notification = NotificationUtils.getNotification(this);
            startForeground(notification.first, notification.second);
            // start projection
            int resultCode = intent.getIntExtra(RESULT_CODE, Activity.RESULT_CANCELED);
            Intent data = intent.getParcelableExtra(DATA);
//            Log.d("caijday",""+resultCode+" ss "+data);
            startProjection(resultCode, data);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else if (isStopCommand(intent)) {
            stopProjection();
            stopSelf();
        } else {
            stopSelf();
        }

        return START_NOT_STICKY;
    }

    private void startProjection(int resultCode, Intent data) {
        MediaProjectionManager mpManager =
                (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        if (mMediaProjection == null) {
            mMediaProjection = mpManager.getMediaProjection(resultCode, data);
            if (mMediaProjection != null) {
                // display metrics
                mDensity = Resources.getSystem().getDisplayMetrics().densityDpi;
                WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                mDisplay = windowManager.getDefaultDisplay();

                // create virtual display depending on device width / height
                createVirtualDisplay();

                // register orientation change callback
                mOrientationChangeCallback = new OrientationChangeCallback(this);
                if (mOrientationChangeCallback.canDetectOrientation()) {
                    mOrientationChangeCallback.enable();
                }

                // register media projection stop callback
                mMediaProjection.registerCallback(new MediaProjectionStopCallback(), mHandler);
            }
        }
    }

    private void stopProjection() {
        if (mHandler != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mMediaProjection != null) {
                        mMediaProjection.stop();
                    }
                }
            });
        }
    }
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
    public int getNavigationBarHeight()
    {
        Context context = this;
        boolean hasMenuKey = ViewConfiguration.get(context).hasPermanentMenuKey();
        int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0 && !hasMenuKey)
        {
            return getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }
    @SuppressLint("WrongConstant")
    private void createVirtualDisplay() {
        // get width and height

        mWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        int mWidth22 = getNavigationBarHeight();
        int mWidth222 = getStatusBarHeight();
        mHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
//        int dx=getRealHeight();
//        Log.d("sizerrr2","x"+mWidth22+"c"+mWidth222+"xx"+mHeight);
        // start capture reader
        mImageReader = ImageReader.newInstance(mWidth, mHeight+mWidth22+mWidth222, PixelFormat.RGBA_8888, 2);
        mVirtualDisplay = mMediaProjection.createVirtualDisplay(SCREENCAP_NAME, mWidth, mHeight,
                mDensity, getVirtualDisplayFlags(), mImageReader.getSurface(), null, mHandler);
        mImageReader.setOnImageAvailableListener(new ImageAvailableListener(), mHandler);
    }
    public void listFilesForFolder(final File folder) {
        File externalFilesDir = getExternalFilesDir(null);
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                listFilesForFolder(fileEntry);
//                Log.d("dsdfffffffff","contentscontentscontentsfileEntry.isDirectory()"+fileEntry.isDirectory());
            } else {
                System.out.println(fileEntry.getName());
                contents.add(externalFilesDir.getAbsolutePath() + "/screenshots/"+fileEntry.getName());
            }
        }
//        Log.d("dsdfffffffff","contentscontentscontentscontents"+contents);
    }

    private void createPDFWithMultipleImage(){
        File file = getOutputFile();
        final File folder = new File(mStoreDir);
        listFilesForFolder(folder);
        int i=0;
        if (file != null){
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                PdfDocument pdfDocument = new PdfDocument();

                for (String uri : contents){
                    Bitmap bitmap = BitmapFactory.decodeFile(uri);
                    PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(
                            bitmap.getWidth(),bitmap.getHeight(), (i + 1)).create();
                    PdfDocument.Page page = pdfDocument.startPage(pageInfo);
                    Canvas canvas = page.getCanvas();
                    Paint paint = new Paint();
                    paint.setColor(Color.BLUE);
                    canvas.drawPaint(paint);
                    canvas.drawBitmap(bitmap, 0f, 0f, null);
                    pdfDocument.finishPage(page);
                    bitmap.recycle();
                    i+=1;
                }
                pdfDocument.writeTo(fileOutputStream);
                pdfDocument.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private File getOutputFile(){
        File root = new File(this.getExternalFilesDir(null),"MyPDFFolder");

        boolean isFolderCreated = true;

        if (!root.exists()){
            isFolderCreated = root.mkdir();
        }

        if (isFolderCreated) {
            imageFileName = "PDFX";
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
            return new File(root, imageFileName + ".pdf");
        }
        else {
            Toast.makeText(this, "Folder is not created", Toast.LENGTH_SHORT).show();
            return null;
        }
    }
}