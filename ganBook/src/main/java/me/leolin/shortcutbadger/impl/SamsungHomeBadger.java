package me.leolin.shortcutbadger.impl;

import java.util.List;

import me.leolin.shortcutbadger.ShortcutBadgeException;
import me.leolin.shortcutbadger.ShortcutBadger;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.ganbook.app.MyApp;

/**
 * Created with IntelliJ IDEA.
 * User: leolin
 * Date: 2013/11/14
 * Time: ה¸‹ו�ˆ7:15
 * To change this template use File | Settings | File Templates.
 */
public class SamsungHomeBadger extends ShortcutBadger {
//    private static final String CONTENT_URI = "content://com.sec.badge/apps?notify=true";

    public SamsungHomeBadger(Context context) {
        super(context);
    }

//    @Override
//    protected void executeBadge(int badgeCount) throws ShortcutBadgeException {
//        Uri mUri = Uri.parse(CONTENT_URI);
//        ContentResolver contentResolver = mContext.getContentResolver();
//        Cursor cursor = contentResolver.query(mUri, new String[]{"_id",}, "package=?", new String[]{getContextPackageName()}, null);
//        if (cursor.moveToNext()) {
//            int id = cursor.getInt(0);
//            ContentValues contentValues = new ContentValues();
//            contentValues.put("badgecount", badgeCount);
//            contentResolver.update(mUri, contentValues, "_id=?", new String[]{String.valueOf(id)});
//        } else {
//            ContentValues contentValues = new ContentValues();
//            contentValues.put("package", getContextPackageName());
//            contentValues.put("class", getEntryActivityName());
//            contentValues.put("badgecount", badgeCount);
//            contentResolver.insert(mUri, contentValues);
//        }
 
    @Override
    protected void executeBadge(int badgeCount) throws ShortcutBadgeException {
    	Context context = MyApp.context;
    	// gilad update
    	 String launcherClassName = getLauncherClassName(context);
         if (launcherClassName == null) {
             return;
         }
         Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
         intent.putExtra("badge_count", badgeCount);
         intent.putExtra("badge_count_package_name", context.getPackageName());
         intent.putExtra("badge_count_class_name", launcherClassName);
         context.sendBroadcast(intent);
    }
    
    
    private static String getLauncherClassName(Context context) {
    	PackageManager pm = context.getPackageManager();

    	Intent intent = new Intent(Intent.ACTION_MAIN);
    	intent.addCategory(Intent.CATEGORY_LAUNCHER);

    	List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
    	for (ResolveInfo resolveInfo : resolveInfos) {
    		String pkgName = resolveInfo.activityInfo.applicationInfo.packageName;
    		if (pkgName.equalsIgnoreCase(context.getPackageName())) {
    			String className = resolveInfo.activityInfo.name;
    			return className;
    		}
    	}
    	return null;
    }    
}
