package com.gczy.axc.aixiangchou;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by lixin on 18/12/14.
 */

public class BadgeUtilDeskTop {
    /**
     * 对外提供调用的方法
     *
     * @param context 上下文
     * @param count   数量
     */

    public static void setBadgeCount(Context context, int count) {

        if (count <= 0) {

            count = 0;

        } else {

            count = Math.max(0, Math.min(count, 99));

        }
        if ("xiaomi".equalsIgnoreCase(Build.MANUFACTURER)) {

            sendToXiaoMi(context, count);     //ok,但是小米miui9要自己去通知栏设置为重要通知，否则不生效

        } else if ("sony".equalsIgnoreCase(Build.MANUFACTURER)) {

            sendToSony(context, count);//未测试

        } else if (Build.MANUFACTURER.toLowerCase().contains("samsung") ||

                Build.MANUFACTURER.toLowerCase().contains("lg")) {

            sendToSamsumg(context, count);//三星ok  lg未测试

        } else if (Build.MANUFACTURER.toLowerCase().contains("htc")) {

            setBadgeOfHTC(context, count);//未测试

        }

//        else if (Build.MANUFACTURER.toLowerCase().contains("nova")) {

//            setBadgeOfNova(context, count);

//        }

        else if (Build.MANUFACTURER.toLowerCase().contains("vivo")) {

            sendToVivo(context, count);//vivoX9Splus不支持

        } else if (Build.MANUFACTURER.toLowerCase().contains("huawei")) {

            sendToHuawei(context, count);//EMUI 3.1及以上

        } else {

            Log.e("smg", "不支持桌面显示角标");

        }

    }


    /**
     * 向小米手机发送未读消息数广播
     *
     * @param count
     */

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)

    private static void sendToXiaoMi(Context context, int count) {

        NotificationManager mNotificationManager = (NotificationManager) context


                .getSystemService(Context.NOTIFICATION_SERVICE);


        Notification.Builder builder = new Notification.Builder(context)


                .setContentTitle("通知").setContentText("您有" + count + "条消息未读").setSmallIcon(R.mipmap.ic_launcher_axc);


        Notification notification = builder.build();


        try {


            Field field = notification.getClass().getDeclaredField("extraNotification");


            Object extraNotification = field.get(notification);


            Method method = extraNotification.getClass().getDeclaredMethod("setMessageCount", int.class);


            method.invoke(extraNotification, count);


        } catch (Exception e) {


            e.printStackTrace();


        }


        mNotificationManager.notify(0, notification);


//        try {

//            Class miuiNotificationClass = Class.forName("android.app.MiuiNotification");

//            Object miuiNotification = miuiNotificationClass.newInstance();

//            Field field = miuiNotification.getClass().getDeclaredField("messageCount");

//            field.setAccessible(true);

//            field.set(miuiNotification, String.valueOf(count == 0 ? "" : (count > 99 ? "99+" : count))); // 设置信息数-->这种发送必须是miui

//            // 6才行

//        } catch (Exception e) {

//            e.printStackTrace();

//            // miui 6之前的版本

//            Intent localIntent = new Intent("android.intent.action.APPLICATION_MESSAGE_UPDATE");

//            localIntent.putExtra("android.intent.extra.update_application_component_name",

//                    context.getPackageName() + "/"  + getLauncherClassName(context));

//            localIntent.putExtra("android.intent.extra.update_application_message_text",

//                    String.valueOf(count == 0 ? "" : (count > 99 ? "99+" : count)));

//            context.sendBroadcast(localIntent);

//        }

    }


    /**
     * 向索尼手机发送未读消息数广播<br/>
     * <p>
     * 据说：需添加权限：<uses-permission
     * <p>
     * android:name="com.sonyericsson.home.permission.BROADCAST_BADGE" /> [未验证]
     *
     * @param count
     */

    private static void sendToSony(Context context, int count) {

        String launcherClassName = getLauncherClassName(context);

        if (launcherClassName == null) {

            return;

        }


        boolean isShow = true;

        if (count == 0) {

            isShow = false;

        }

        Intent localIntent = new Intent();

        localIntent.setAction("com.sonyericsson.home.action.UPDATE_BADGE");

        localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.SHOW_MESSAGE",

                isShow);// 是否显示

        localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.ACTIVITY_NAME",

                launcherClassName);// 启动页

        localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.MESSAGE",

                String.valueOf(count == 0 ? "" : (count > 99 ? "99+" : count)));// 数字

        localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.PACKAGE_NAME",

                context.getPackageName());// 包名

        context.sendBroadcast(localIntent);

    }


    /**
     * 向三星手机发送未读消息数广播    ok
     *
     * @param count
     */

    private static void sendToSamsumg(Context context, int count) {

        String launcherClassName = getLauncherClassName(context);

        if (launcherClassName == null) {

            return;

        }

        Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");

        intent.putExtra("badge_count", count);

        intent.putExtra("badge_count_package_name", context.getPackageName());

        intent.putExtra("badge_count_class_name", launcherClassName);

        context.sendBroadcast(intent);

    }


    /**
     * 向ViVo手机发送未读消息数广播
     *
     * @param count
     */

    private static void sendToVivo(Context context, int count) {

        String launcherClassName = getLauncherClassName(context);

        if (launcherClassName == null) {

            return;

        }

        Intent localIntent = new Intent("launcher.action.CHANGE_APPLICATION_NOTIFICATION_NUM");

        localIntent.putExtra("packageName", context.getPackageName());

        localIntent.putExtra("className", launcherClassName);

        localIntent.putExtra("notificationNum", count);

        context.sendBroadcast(localIntent);

    }


    /**
     * 向华为手机发送未读消息数广播
     *
     * @param count
     */

    private static void sendToHuawei(Context context, int count) {

        String launcherClassName = getLauncherClassName(context);

        if (launcherClassName == null) {

            return;

        }

        try {

            Bundle localBundle = new Bundle();

            localBundle.putString("package", context.getPackageName());

            localBundle.putString("class", launcherClassName);

            localBundle.putInt("badgenumber", count);

            context.getContentResolver().call(Uri.parse("content://com.huawei.android.launcher.settings/badge/"), "change_badge", null, localBundle);

        } catch (Exception e) {

            e.printStackTrace();

            Log.e("HUAWEI" + " Badge error", "set Badge failed");

        }

    }


    /**
     * 设置HTC的Badge
     *
     * @param context context
     * @param count   count
     */

    private static void setBadgeOfHTC(Context context, int count) {

        Intent intentNotification = new Intent("com.htc.launcher.action.SET_NOTIFICATION");

        ComponentName localComponentName = new ComponentName(context.getPackageName(),

                getLauncherClassName(context));

        intentNotification.putExtra("com.htc.launcher.extra.COMPONENT", localComponentName.flattenToShortString());

        intentNotification.putExtra("com.htc.launcher.extra.COUNT", count);

        context.sendBroadcast(intentNotification);


        Intent intentShortcut = new Intent("com.htc.launcher.action.UPDATE_SHORTCUT");

        intentShortcut.putExtra("packagename", context.getPackageName());

        intentShortcut.putExtra("count", count);

        context.sendBroadcast(intentShortcut);

    }


    /**
     * 设置Nova的Badge
     *
     * @param context context
     * @param count   count
     */

    private static void setBadgeOfNova(Context context, int count) {

        ContentValues contentValues = new ContentValues();

        contentValues.put("tag", context.getPackageName() + "/" +

                getLauncherClassName(context));

        contentValues.put("count", count);

        context.getContentResolver().insert(Uri.parse("content://com.teslacoilsw.notifier/unread_count"),

                contentValues);

    }

    /**
     * 重置、清除Badge未读显示数<br/>
     *
     * @param context
     */

    public static void resetBadgeCount(Context context) {

        setBadgeCount(context, 0);

    }


    /**
     * 获取Launcher的名字,
     *
     * @param context 上下文
     * @return launcher name
     */

    private static String getLauncherClassName(Context context) {

        PackageManager packageManager = context.getPackageManager();


        Intent intent = new Intent(Intent.ACTION_MAIN);

        // 添加包名限制.

        intent.setPackage(context.getPackageName());

        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        ResolveInfo info = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);

        if (info == null) {

            info = packageManager.resolveActivity(intent, 0);

        }


        return info.activityInfo.name;

    }
}
