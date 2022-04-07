package com.robertohuertas.endless;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;


public class TestAccessibleService extends AccessibilityService {
    private static final String TAG = GlobalConstant.LOG_TAG;
    private static final String TASK_LIST_VIEW_CLASS_NAME =
            "com.robertohuertas.endless.TestAccessibleService";
    private static final String PREFS_KEY_intent = "myPreferncesintent";
    private static final String PASSWORD_KEY_intent = "myPreferncesintent";
    FrameLayout mLayout;
    WindowManager wm;
    String parameter;
    int foo;
    int hethan;
    int dayOfWeek;
    String password;
    protected void Chephu() {
        // Create an overlay and display the action bar

        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        mLayout = new FrameLayout(this);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY;
        lp.format = PixelFormat.TRANSLUCENT;
        lp.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.TOP;
        LayoutInflater inflater = LayoutInflater.from(this);
        inflater.inflate(R.layout.action_bar, mLayout);
        wm.addView(mLayout, lp);
//        Log.d(TAG, "mLayoutmLayoutmLayoutmLayout---------else"+mLayout);
    }
    protected void Chephu2() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View rootView = inflater.inflate(R.layout.action_bar, mLayout);
        ListView listView = (ListView) rootView.findViewById(R.id.action_bar);
        ((ViewGroup) listView.getParent()).removeView(listView);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        String current_time;
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        current_time=sdf.format(calendar.getTime());
        Log.d("TẮTDỊCHVỤ",password);
        if(current_time.equals(password)){
            Log.d("TẮT DỊCH VỤ","TẮT DỊCH VỤTẮT DỊCH VỤTẮT DỊCH VỤTẮT DỊCH VỤ");
            disableSelf();
        }
        Log.d(TAG, "onAccessibilityEvent"+event.getEventType());
        if(event.getEventType()==AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED){
            ComponentName componentName = new ComponentName(event.getPackageName().
                    toString(), event.getClassName().toString());
            ActivityInfo activityInfo = tryGetActivity(componentName);
            boolean isActivity = activityInfo != null;
            Log.d(TAG, "onAccessibilityEvent"+isActivity);
            AccessibilityNodeInfo nodeInfo = event.getSource();
//            Log.d("@@","??????????????????????????=0: "+event);
//            Log.d("ddddddd0=2","================="+current_time);
//            Log.d("ddddddd0=2","================="+Common.thoigian);
//            Log.d("@@","??????????????????????????=1: "+findFirstViewByText(nodeInfo, "Bắt đầu ngay"));
//            AccessibilityNodeInfo nodeInfox=findFirstViewByText(nodeInfo, "Bắt đầu ngay");
//            Log.d("@@","??????????????????????????=3: "+findFirstViewByText(nodeInfo, "NGĂN"));
//            Log.d("@@","??????????????????????????"+findFirstViewByText(nodeInfo));
            if(isActivity) {
                if (findFirstViewByText(nodeInfo, "NGĂN") == null) {
//                    if (mLayout != null) {
//                        try {
//
//                            Chephu2();
//                        } catch (Exception e) {}
////                        mLayout = null;
//                        Log.d("ĐÃ XÓA--ĐÃ XÓA--ĐÃ XÓA", "???" + mLayout);
//                    }
                }
                if (findFirstViewByText(nodeInfo, "NGĂN") != null) {
                    Intent startMain = new Intent(Intent.ACTION_MAIN);
                    startMain.addCategory(Intent.CATEGORY_HOME);
                    startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(startMain);
                }
            }
        }
//        if(event.getEventType()==AccessibilityEvent.CONTENT_CHANGE_TYPE_PANE_DISAPPEARED){
//            ComponentName componentName = new ComponentName(event.getPackageName().toString(), event.getClassName().toString());
//            ActivityInfo activityInfo = tryGetActivity(componentName);
//            boolean isActivity = activityInfo != null;
//            if(isActivity){
//                AccessibilityNodeInfo nodeInfo = event.getSource();
//                if(nodeInfo!=null){
//                    Log.d(TAG, "tên gói đang chạy--event.getText()："+event.getSource());
//                    Log.d(TAG, "tên gói đang chạy---nodeInfo.getText()："+nodeInfo.getText());
//                    Toast.makeText(this, nodeInfo.getPackageName(), Toast.LENGTH_SHORT).show();
//                }
//            }
//        }

//        try{
//            AccessibilityNodeInfo rootNodeInfo = getRootInActiveWindow();
//            if(rootNodeInfo==null){
//                Log.e(TAG, "rootNodeInfo is null");
//                return;
//            }
//            List<AccessibilityNodeInfo> targetNodeInfoList = new ArrayList<>();
//            String[] targetTextArray = new String[]{
//                    "跳过",
//            };
//            for(String targetText:targetTextArray){
//                List<AccessibilityNodeInfo> nodeInfoList = rootNodeInfo.findAccessibilityNodeInfosByText("跳过");
//                if(nodeInfoList!=null && nodeInfoList.size()>0){
//                    targetNodeInfoList.addAll(nodeInfoList);
//                    for(AccessibilityNodeInfo nodeInfo:nodeInfoList){
//                        Log.d(TAG, "node:"+nodeInfo);
//                    }
//                }
//            }
//            Log.d(TAG, "找到"+targetNodeInfoList.size()+"个包含'跳过'的按钮");
//            for(AccessibilityNodeInfo nodeInfo:targetNodeInfoList){
//                Log.d(TAG, "模拟了一次点击事件");
//                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//            }
//        }catch (Exception e){
//            Log.e(TAG, "Exception:"+e.getMessage());
//            e.printStackTrace();
//        }
    }
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        wm.removeView(mLayout);
//    }

    @Override
    public void onInterrupt() {
        Log.e(TAG, "onInterrupt");
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        SharedPreferences sharedPreferences =
                getSharedPreferences(PREFS_KEY_intent, MODE_PRIVATE);
        password = sharedPreferences.getString(PASSWORD_KEY_intent, "");
        Log.d(TAG, "onServiceConnected");
        AccessibilityServiceInfo config = new AccessibilityServiceInfo();
        //配置监听的事件类型为界面变化|点击事件
        config.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED | AccessibilityEvent.TYPE_VIEW_CLICKED;
        config.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        config.flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS;
        setServiceInfo(config);
    }

    private ActivityInfo tryGetActivity(ComponentName componentName) {
        try {
            return getPackageManager().getActivityInfo(componentName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }
    private AccessibilityNodeInfo getListItemNodeInfo(AccessibilityNodeInfo source) {
        AccessibilityNodeInfo current = source;
        while (true) {
            AccessibilityNodeInfo parent = current.getParent();
            if (parent == null) {
                return null;
            }
            if (TASK_LIST_VIEW_CLASS_NAME.equals(parent.getClassName())) {
                return current;
            }
            // NOTE: Recycle the infos.
            AccessibilityNodeInfo oldCurrent = current;
            current = parent;
            oldCurrent.recycle();
        }
    }
    public ArrayList<AccessibilityNodeInfo> collectNodes() {
        ArrayList<AccessibilityNodeInfo> results = new ArrayList<>();

        try {
            collectNodes(getRootInActiveWindow(), results);
        } catch (Exception e) {
            //Handle exceptions, though, if you don't do stupid things exceptions shouldn't happen
        }

        return results;
    }

    private void collectNodes(AccessibilityNodeInfo nodeInfo, ArrayList<AccessibilityNodeInfo> nodes) {

        if (nodeInfo == null) return;

        nodes.add(nodeInfo);

        for (int i = 0; i < nodeInfo.getChildCount(); i++) {
            collectNodes(nodeInfo.getChild(i), nodes);

        }
    }
    AccessibilityNodeInfo findFirstViewByText(AccessibilityNodeInfo rootNode, String text) {
        if (rootNode == null) return null;
        if (rootNode.getText() != null && rootNode.getText().toString().equals(text))
            return rootNode;
        int childCount = rootNode.getChildCount();
        Log.d("@@","??????????????????????????=2: "+rootNode);
        for (int i = 0; i < childCount; i++) {
            AccessibilityNodeInfo tmpNode = rootNode.getChild(i);
            if (tmpNode != null) {
                AccessibilityNodeInfo res = findFirstViewByText(tmpNode, text);
                if (res != null) {
//                    res.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    return res;
                } else {
                    tmpNode.recycle();
                }
            }
        }
        return null;
    }
//    public void onDestroy(){
//        super.onDestroy();
//    }

//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        super.onStartCommand(intent, flags, startId);
//        Bundle extras = intent.getExtras();
//        parameter = extras.getString("thoigian");
//
//        foo = Integer.parseInt(parameter);
//        Calendar calendar = Calendar.getInstance();
//        dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
//        hethan= foo+dayOfWeek;
//        Log.d("ddddddd0","================="+parameter+"xxx"+hethan);
//            if(parameter.equals("end")){
//                stopSelf();
//            }
//        Log.d(TAG, "onServiceConnected");
//        AccessibilityServiceInfo config = new AccessibilityServiceInfo();
//        //配置监听的事件类型为界面变化|点击事件
//        config.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED | AccessibilityEvent.TYPE_VIEW_CLICKED;
//        config.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
//        config.flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS;
//        setServiceInfo(config);
//        return START_STICKY;
//    }
}


