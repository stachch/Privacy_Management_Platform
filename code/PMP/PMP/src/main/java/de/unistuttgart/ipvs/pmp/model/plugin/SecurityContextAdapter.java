/*
 * Copyright 2012 pmp-android development team
 * Project: PMP
 * Project-Site: https://github.com/stachch/Privacy_Management_Platform
 * 
 * ---------------------------------------------------------------------
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.unistuttgart.ipvs.pmp.model.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.view.Display;
import de.unistuttgart.ipvs.pmp.model.element.app.IApp;
import de.unistuttgart.ipvs.pmp.model.element.resourcegroup.IResourceGroup;

/**
 * Adapter between real {@link Context} and the functionality to that extent that should be given to resource groups.
 * (which is currently everything)
 * 
 * This class should probably be reviewed and have security restrictions implemented.
 * 
 * @author Tobias Kuhn
 *         
 */
public class SecurityContextAdapter extends Context {
    
    private final Context adaptee;
    
    
    /**
     * 
     * @param adaptee
     *            a full Android {@link Context}
     * @param rg
     *            the rg requesting the context
     * @param app
     *            the app the rg requested the context for
     */
    public SecurityContextAdapter(Context adaptee, IResourceGroup rg, IApp app) {
        this.adaptee = adaptee;
    }
    
    
    @Override
    public AssetManager getAssets() {
        return this.adaptee.getAssets();
    }
    
    
    @Override
    public Resources getResources() {
        return this.adaptee.getResources();
    }
    
    
    @Override
    public PackageManager getPackageManager() {
        return this.adaptee.getPackageManager();
    }
    
    
    @Override
    public ContentResolver getContentResolver() {
        return this.adaptee.getContentResolver();
    }
    
    
    @Override
    public Looper getMainLooper() {
        return this.adaptee.getMainLooper();
    }
    
    
    @Override
    public Context getApplicationContext() {
        return this.adaptee.getApplicationContext();
    }
    
    
    @Override
    public void setTheme(int resid) {
        this.adaptee.setTheme(resid);
    }
    
    
    @Override
    public Theme getTheme() {
        return this.adaptee.getTheme();
    }
    
    
    @Override
    public ClassLoader getClassLoader() {
        return this.adaptee.getClassLoader();
    }
    
    
    @Override
    public String getPackageName() {
        return this.adaptee.getPackageName();
    }
    
    
    @Override
    public ApplicationInfo getApplicationInfo() {
        return this.adaptee.getApplicationInfo();
    }
    
    
    @Override
    public SharedPreferences getSharedPreferences(String name, int mode) {
        return this.adaptee.getSharedPreferences(name, mode);
    }
    
    
    @Override
    public FileInputStream openFileInput(String name) throws FileNotFoundException {
        return this.adaptee.openFileInput(name);
    }
    
    
    @Override
    public FileOutputStream openFileOutput(String name, int mode) throws FileNotFoundException {
        return this.adaptee.openFileOutput(name, mode);
    }
    
    
    @Override
    public boolean deleteFile(String name) {
        return this.adaptee.deleteFile(name);
    }
    
    
    @Override
    public File getFileStreamPath(String name) {
        return this.adaptee.getFileStreamPath(name);
    }
    
    
    @Override
    public File getFilesDir() {
        return this.adaptee.getFilesDir();
    }
    
    
    @Override
    public File getCacheDir() {
        return this.adaptee.getCacheDir();
    }
    
    
    @Override
    public String[] fileList() {
        return this.adaptee.fileList();
    }
    
    
    @Override
    public File getDir(String name, int mode) {
        return this.adaptee.getDir(name, mode);
    }
    
    
    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, CursorFactory factory) {
        return this.adaptee.openOrCreateDatabase(name, mode, factory);
    }
    
    
    @Override
    public boolean deleteDatabase(String name) {
        return this.adaptee.deleteDatabase(name);
    }
    
    
    @Override
    public File getDatabasePath(String name) {
        return this.adaptee.getDatabasePath(name);
    }
    
    
    @Override
    public String[] databaseList() {
        return this.adaptee.databaseList();
    }
    
    
    @SuppressWarnings("deprecation")
    @Override
    public Drawable getWallpaper() {
        return this.adaptee.getWallpaper();
    }
    
    
    @SuppressWarnings("deprecation")
    @Override
    public Drawable peekWallpaper() {
        return this.adaptee.peekWallpaper();
    }
    
    
    @SuppressWarnings("deprecation")
    @Override
    public int getWallpaperDesiredMinimumWidth() {
        return this.adaptee.getWallpaperDesiredMinimumWidth();
    }
    
    
    @SuppressWarnings("deprecation")
    @Override
    public int getWallpaperDesiredMinimumHeight() {
        return this.adaptee.getWallpaperDesiredMinimumHeight();
    }
    
    
    @SuppressWarnings("deprecation")
    @Override
    public void setWallpaper(Bitmap bitmap) throws IOException {
        this.adaptee.setWallpaper(bitmap);
    }
    
    
    @SuppressWarnings("deprecation")
    @Override
    public void setWallpaper(InputStream data) throws IOException {
        this.adaptee.setWallpaper(data);
    }
    
    
    @SuppressWarnings("deprecation")
    @Override
    public void clearWallpaper() throws IOException {
        this.adaptee.clearWallpaper();
    }
    
    
    @Override
    public void startActivity(Intent intent) {
        this.adaptee.startActivity(intent);
    }
    
    
    @Override
    public void startIntentSender(IntentSender intent, Intent fillInIntent, int flagsMask, int flagsValues,
            int extraFlags) throws SendIntentException {
        this.adaptee.startIntentSender(intent, fillInIntent, flagsMask, flagsValues, extraFlags);
        
    }
    
    
    @Override
    public void sendBroadcast(Intent intent) {
        this.adaptee.sendBroadcast(intent);
    }
    
    
    @Override
    public void sendBroadcast(Intent intent, String receiverPermission) {
        this.adaptee.sendBroadcast(intent, receiverPermission);
    }
    
    
    @Override
    public void sendOrderedBroadcast(Intent intent, String receiverPermission) {
        this.adaptee.sendOrderedBroadcast(intent, receiverPermission);
    }
    
    
    @Override
    public void sendOrderedBroadcast(Intent intent, String receiverPermission, BroadcastReceiver resultReceiver,
            Handler scheduler, int initialCode, String initialData, Bundle initialExtras) {
        this.adaptee.sendOrderedBroadcast(intent, receiverPermission, resultReceiver, scheduler, initialCode,
                initialData, initialExtras);
    }
    
    
    @SuppressWarnings("deprecation")
    @Override
    public void sendStickyBroadcast(Intent intent) {
        this.adaptee.sendStickyBroadcast(intent);
    }
    
    
    @SuppressWarnings("deprecation")
    @Override
    public void sendStickyOrderedBroadcast(Intent intent, BroadcastReceiver resultReceiver, Handler scheduler,
            int initialCode, String initialData, Bundle initialExtras) {
        this.adaptee.sendStickyOrderedBroadcast(intent, resultReceiver, scheduler, initialCode, initialData,
                initialExtras);
    }
    
    
    @SuppressWarnings("deprecation")
    @Override
    public void removeStickyBroadcast(Intent intent) {
        this.adaptee.removeStickyBroadcast(intent);
    }
    
    
    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        return this.adaptee.registerReceiver(receiver, filter);
    }
    
    
    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter, String broadcastPermission,
            Handler scheduler) {
        return this.adaptee.registerReceiver(receiver, filter, broadcastPermission, scheduler);
    }
    
    
    @Override
    public void unregisterReceiver(BroadcastReceiver receiver) {
        this.adaptee.unregisterReceiver(receiver);
        
    }
    
    
    @Override
    public ComponentName startService(Intent service) {
        return this.adaptee.startService(service);
    }
    
    
    @Override
    public boolean stopService(Intent service) {
        return this.adaptee.stopService(service);
    }
    
    
    @Override
    public boolean bindService(Intent service, ServiceConnection conn, int flags) {
        return this.adaptee.bindService(service, conn, flags);
    }
    
    
    @Override
    public void unbindService(ServiceConnection conn) {
        this.adaptee.unbindService(conn);
    }
    
    
    @Override
    public boolean startInstrumentation(ComponentName className, String profileFile, Bundle arguments) {
        return this.adaptee.startInstrumentation(className, profileFile, arguments);
    }
    
    
    @Override
    public Object getSystemService(String name) {
        return this.adaptee.getSystemService(name);
    }
    
    
    @Override
    public int checkPermission(String permission, int pid, int uid) {
        return this.adaptee.checkPermission(permission, pid, uid);
    }
    
    
    @Override
    public int checkCallingPermission(String permission) {
        return this.adaptee.checkCallingPermission(permission);
    }
    
    
    @Override
    public int checkCallingOrSelfPermission(String permission) {
        return this.adaptee.checkCallingOrSelfPermission(permission);
    }
    
    
    @Override
    public void enforcePermission(String permission, int pid, int uid, String message) {
        this.adaptee.enforcePermission(permission, pid, uid, message);
    }
    
    
    @Override
    public void enforceCallingPermission(String permission, String message) {
        this.adaptee.enforceCallingPermission(permission, message);
    }
    
    
    @Override
    public void enforceCallingOrSelfPermission(String permission, String message) {
        this.adaptee.enforceCallingOrSelfPermission(permission, message);
    }
    
    
    @Override
    public void grantUriPermission(String toPackage, Uri uri, int modeFlags) {
        this.adaptee.grantUriPermission(toPackage, uri, modeFlags);
    }
    
    
    @Override
    public void revokeUriPermission(Uri uri, int modeFlags) {
        this.adaptee.revokeUriPermission(uri, modeFlags);
    }
    
    
    @Override
    public int checkUriPermission(Uri uri, int pid, int uid, int modeFlags) {
        return this.adaptee.checkUriPermission(uri, pid, uid, modeFlags);
    }
    
    
    @Override
    public int checkCallingUriPermission(Uri uri, int modeFlags) {
        return this.adaptee.checkCallingUriPermission(uri, modeFlags);
    }
    
    
    @Override
    public int checkCallingOrSelfUriPermission(Uri uri, int modeFlags) {
        return this.adaptee.checkCallingOrSelfUriPermission(uri, modeFlags);
    }
    
    
    @Override
    public int checkUriPermission(Uri uri, String readPermission, String writePermission, int pid, int uid,
            int modeFlags) {
        return this.adaptee.checkUriPermission(uri, readPermission, writePermission, pid, uid, modeFlags);
    }
    
    
    @Override
    public void enforceUriPermission(Uri uri, int pid, int uid, int modeFlags, String message) {
        this.adaptee.enforceUriPermission(uri, pid, uid, modeFlags, message);
    }
    
    
    @Override
    public void enforceCallingUriPermission(Uri uri, int modeFlags, String message) {
        this.adaptee.enforceCallingUriPermission(uri, modeFlags, message);
    }
    
    
    @Override
    public void enforceCallingOrSelfUriPermission(Uri uri, int modeFlags, String message) {
        this.adaptee.enforceCallingOrSelfUriPermission(uri, modeFlags, message);
    }
    
    
    @Override
    public void enforceUriPermission(Uri uri, String readPermission, String writePermission, int pid, int uid,
            int modeFlags, String message) {
        this.adaptee.enforceUriPermission(uri, readPermission, writePermission, pid, uid, modeFlags, message);
    }
    
    
    @Override
    public Context createPackageContext(String packageName, int flags) throws NameNotFoundException {
        return this.adaptee.createPackageContext(packageName, flags);
    }
    
    
    @Override
    public File getExternalCacheDir() {
        return this.adaptee.getExternalCacheDir();
    }
    
    
    @Override
    public File getExternalFilesDir(String type) {
        return this.adaptee.getExternalFilesDir(type);
    }
    
    
    @Override
    public String getPackageCodePath() {
        return this.adaptee.getPackageCodePath();
    }
    
    
    @Override
    public String getPackageResourcePath() {
        return this.adaptee.getPackageResourcePath();
    }
    
    
    @Override
    public File[] getExternalFilesDirs(String type) {
        return this.adaptee.getExternalFilesDirs(type);
    }
    
    
    @Override
    public File getObbDir() {
        return this.adaptee.getObbDir();
    }
    
    
    @Override
    public File[] getObbDirs() {
        return this.adaptee.getObbDirs();
    }
    
    
    @Override
    public File[] getExternalCacheDirs() {
        return this.adaptee.getExternalCacheDirs();
    }
    
    
    @Override
    public SQLiteDatabase openOrCreateDatabase(String name, int mode, CursorFactory factory,
            DatabaseErrorHandler errorHandler) {
        return this.adaptee.openOrCreateDatabase(name, mode, factory, errorHandler);
    }
    
    
    @Override
    public void startActivity(Intent intent, Bundle options) {
        this.adaptee.startActivity(intent, options);
    }
    
    
    @Override
    public void startActivities(Intent[] intents) {
        this.adaptee.startActivities(intents);
    }
    
    
    @Override
    public void startActivities(Intent[] intents, Bundle options) {
        this.adaptee.startActivities(intents, options);
    }
    
    
    @Override
    public void startIntentSender(IntentSender intent, Intent fillInIntent, int flagsMask, int flagsValues,
            int extraFlags, Bundle options) throws SendIntentException {
        this.adaptee.startIntentSender(intent, fillInIntent, flagsMask, flagsValues, extraFlags, options);
    }
    
    
    @Override
    public void sendBroadcastAsUser(Intent intent, UserHandle user) {
        this.adaptee.sendBroadcastAsUser(intent, user);
    }
    
    
    @Override
    public void sendBroadcastAsUser(Intent intent, UserHandle user, String receiverPermission) {
        this.adaptee.sendBroadcastAsUser(intent, user, receiverPermission);
    }
    
    
    @Override
    public void sendOrderedBroadcastAsUser(Intent intent, UserHandle user, String receiverPermission,
            BroadcastReceiver resultReceiver, Handler scheduler, int initialCode, String initialData,
            Bundle initialExtras) {
        this.adaptee.sendOrderedBroadcastAsUser(intent, user, receiverPermission, resultReceiver, scheduler,
                initialCode, initialData, initialExtras);
    }
    
    
    @SuppressWarnings("deprecation")
    @Override
    public void sendStickyBroadcastAsUser(Intent intent, UserHandle user) {
        this.adaptee.sendStickyBroadcastAsUser(intent, user);
    }
    
    
    @SuppressWarnings("deprecation")
    @Override
    public void sendStickyOrderedBroadcastAsUser(Intent intent, UserHandle user, BroadcastReceiver resultReceiver,
            Handler scheduler, int initialCode, String initialData, Bundle initialExtras) {
        this.adaptee.sendStickyOrderedBroadcastAsUser(intent, user, resultReceiver, scheduler, initialCode, initialData,
                initialExtras);
    }
    
    
    @SuppressWarnings("deprecation")
    @Override
    public void removeStickyBroadcastAsUser(Intent intent, UserHandle user) {
        this.adaptee.removeStickyBroadcastAsUser(intent, user);
    }
    
    
    @Override
    public Context createConfigurationContext(Configuration overrideConfiguration) {
        return this.adaptee.createConfigurationContext(overrideConfiguration);
    }
    
    
    @Override
    public Context createDisplayContext(Display display) {
        return this.adaptee.createDisplayContext(display);
    }
    
    
    @Override
    public File getNoBackupFilesDir() {
        return this.adaptee.getNoBackupFilesDir();
    }
    
    
    @Override
    public File getCodeCacheDir() {
        return this.adaptee.getCodeCacheDir();
    }
    
    
    @Override
    public File[] getExternalMediaDirs() {
        return this.adaptee.getExternalMediaDirs();
    }
    
    
    @Override
    public String getSystemServiceName(Class<?> serviceClass) {
        return this.adaptee.getSystemServiceName(serviceClass);
    }
    
    
    @Override
    public int checkSelfPermission(String permission) {
        return checkSelfPermission(permission);
    }
    
}
