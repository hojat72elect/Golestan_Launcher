package com.hojat72elect.Golestan_Launcher

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import com.facebook.react.bridge.*
import org.json.JSONArray
import org.json.JSONObject

class LauncherModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

    override fun getName(): String {
        return "LauncherModule"
    }

    @ReactMethod
    fun getInstalledApps(promise: Promise) {
        try {
            val packageManager: PackageManager = reactApplicationContext.packageManager
            val mainIntent = Intent(Intent.ACTION_MAIN, null)
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
            
            val resolveInfoList: List<ResolveInfo> = packageManager.queryIntentActivities(mainIntent, 0)
            val appsArray = JSONArray()

            for (resolveInfo in resolveInfoList) {
                val appInfo = packageManager.getApplicationInfo(resolveInfo.activityInfo.packageName, 0)
                val appObject = JSONObject()
                
                appObject.put("packageName", resolveInfo.activityInfo.packageName)
                appObject.put("appName", packageManager.getApplicationLabel(appInfo).toString())
                appObject.put("activityName", resolveInfo.activityInfo.name)
                appObject.put("icon", "")
                
                appsArray.put(appObject)
            }

            val appsMap = Arguments.makeNativeMap(
                mapOf("apps" to appsArray.toString())
            )
            
            promise.resolve(appsMap)
        } catch (e: Exception) {
            promise.reject("ERROR", e.message)
        }
    }

    @ReactMethod
    fun launchApp(packageName: String, activityName: String, promise: Promise) {
        try {
            val intent = Intent()
            intent.setClassName(packageName, activityName)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            
            reactApplicationContext.startActivity(intent)
            promise.resolve(true)
        } catch (e: Exception) {
            promise.reject("ERROR", e.message)
        }
    }
}
