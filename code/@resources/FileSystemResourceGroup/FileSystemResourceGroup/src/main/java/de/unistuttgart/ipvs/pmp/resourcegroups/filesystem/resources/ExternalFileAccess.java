/*
 * Copyright 2012 pmp-android development team
 * Project: FileSystemResourceGroup
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
package de.unistuttgart.ipvs.pmp.resourcegroups.filesystem.resources;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import android.os.Environment;
import android.os.RemoteException;
import de.unistuttgart.ipvs.pmp.resourcegroups.filesystem.PrivacySettings;
import de.unistuttgart.ipvs.pmp.shared.Log;

/**
 * Handles the access to external directories.
 * 
 * @author Patrick Strobel
 * @version 0.2.1
 */
public class ExternalFileAccess extends IFileAccess.Stub {
    
    private String app;
    private ExternalFileAccessResource resource;
    
    public enum Directories {
        BASE_DIR,
        MUSIC,
        PODCASTS,
        RINGTONES,
        ALARMS,
        NOTIFICATIONS,
        PICTURES,
        MOVIES,
        DOWNLOAD
    };
    
    private enum Functions {
        READ,
        WRITE,
        LIST,
        DELETE,
        MAKE_DIRS
    };
    
    private final Directories directory;
    
    private static final String SWITCHING_EXCEPTION = "Switching in upper directories is not allowed";
    
    
    /**
     * Creates a new instance
     * 
     * @param app
     *            Identifier of the app that is using this access handler
     * @param resource
     *            Resource this access handler object belongs to
     * @param dir
     *            External directory this gives access to
     */
    protected ExternalFileAccess(String app, ExternalFileAccessResource resource, Directories dir) {
        this.app = app;
        this.resource = resource;
        this.directory = dir;
        
    }
    
    
    /**
     * Reads a file stored in the external directory into a string.
     * 
     * @param path
     *            Path to the file in the external directory as sub-directory of the currently selected external
     *            directory. For example, if this resource gives access to the Music-Directory and <code>path</code> is
     *            set to <code>example/testFile.txt</code>, then <code>Music/example/testFile.txt</code> will be read).
     * @return Data of the selected file.
     * @throws IllegalAccessError
     *             Thrown, if app's privacy setting is not set or the <code>path</code> parameters contains character
     *             for
     *             switching into a upper directory (typically <code>../</code>).
     * @throws RemoteException
     *             Thrown, if file is not readable (e.g. does not exist).
     */
    @Override
    public String read(String path) throws RemoteException {
        // Check if application is allowed to use this function
        if (!privacySettingSet(Functions.READ)) {
            throw new SecurityException();
        }
        
        try {
            return Utils.readFileToString(getExternalDirectory(path));
        } catch (FileNotFoundException e) {
            Log.d(this, "Cannot open file: " + path, e);
            throw new IllegalArgumentException();
        } catch (IOException e) {
            Log.d(this, "Cannot read file", e);
            throw new IllegalArgumentException();
        } catch (IllegalArgumentException e) {
            Log.d(this, SWITCHING_EXCEPTION, e);
            throw new SecurityException();
        }
        
    }
    
    
    /**
     * Writes a given string into a file in the external directory
     * 
     * @param path
     *            Path to the file in the external directory the string should be written to. For example, if this
     *            resource gives access to the Music-Directory and <code>path</code> is set to
     *            <code>example/testFile.txt</code>, then the string will be written to
     *            <code>Music/example/testFile.txt</code>).
     * @param data
     *            Date to write into the selected file.
     * @param append
     *            True, if data should be appended to the existing file data. Otherwise it's data will be overwritten.
     * @return True, if data was successfully written.
     * @throws IllegalAccessError
     *             Thrown, if app's privacy setting is not set or the <code>path</code> parameters contains character
     *             for
     *             switching into a upper directory (typically <code>../</code>).
     */
    @Override
    public boolean write(String path, String data, boolean append) throws RemoteException {
        // Check if application is allowed to use this function
        if (!privacySettingSet(Functions.WRITE)) {
            throw new SecurityException();
        }
        
        try {
            Utils.writeStringToFile(getExternalDirectory(path), data, append);
            return true;
        } catch (IOException e) {
            Log.d(this, "Cannot write data to " + path);
            e.printStackTrace();
            return false;
        } catch (IllegalArgumentException e) {
            Log.d(this, SWITCHING_EXCEPTION, e);
            throw new SecurityException();
        }
    }
    
    
    /**
     * Deletes a file or directory in the external directory. Directories can only be deleted if they do not have any
     * files.
     * 
     * @param path
     *            Path of the file or directory which should be deleted. For example, if this resource gives access to
     *            the Music-Directory and <code>path</code> is set to <code>example/testDir</code>, then
     *            <code>Music/example/testDir</code> will be deleted).
     * @return True, if file or directory was deleted successfully.
     * @throws IllegalAccessError
     *             Thrown, if app's privacy setting is not set or the <code>path</code> parameters contains character
     *             for
     *             switching into a upper directory (typically <code>../</code>).
     */
    @Override
    public boolean delete(String path) throws RemoteException {
        // Check if application is allowed to use this function
        if (!privacySettingSet(Functions.DELETE)) {
            throw new SecurityException();
        }
        try {
            return getExternalDirectory(path).delete();
        } catch (IllegalArgumentException e) {
            Log.d(this, SWITCHING_EXCEPTION, e);
            throw new SecurityException();
        }
    }
    
    
    /**
     * Returns a list of all files and directories in a given external directory.
     * 
     * @param directory
     *            Path of the parent directory. For example, if this resource gives access to the Music-Directory and
     *            <code>path</code> is set to <code>example/testDir</code>, then a list of all files and sub-directories
     *            in <code>Music/example/testDir</code> will be generated).
     * @return List of detailed file information data or null, if path points to a non existing directory or a file.
     * @throws IllegalAccessError
     *             Thrown, if the app's privacy setting is not set or the <code>path</code> parameters contains
     *             character
     *             for switching into a upper directory (typically <code>../</code>).
     */
    @Override
    public List<FileDetails> list(String directory) throws RemoteException {
        // Check if application is allowed to use this function
        if (!privacySettingSet(Functions.LIST)) {
            throw new SecurityException();
        }
        
        try {
            return Utils.getFileDetailsList(getExternalDirectory(directory));
        } catch (IllegalArgumentException e) {
            Log.d(this, SWITCHING_EXCEPTION, e);
            throw new SecurityException();
        }
    }
    
    
    /**
     * Creates all directories that are not existing
     * 
     * @see File#mkdirs()
     * @param path
     *            Directory path. For example, if this resource gives access to the Music-Directory and
     *            <code>path</code> is set to <code>example/testDir</code>, then <code>Music/example/testFDir</code>
     *            will be created).
     * @return True, if directories where created successfully.
     * @throws IllegalAccessError
     *             Thrown, if the app's privacy setting is not set or the <code>path</code> parameters contains
     *             character
     *             for switching into a upper directory (typically <code>../</code>).
     */
    @Override
    public boolean makeDirs(String path) throws RemoteException {
        // Check if application is allowed to use this function
        if (!privacySettingSet(Functions.MAKE_DIRS)) {
            throw new SecurityException();
        }
        
        try {
            return getExternalDirectory(path).mkdirs();
        } catch (IllegalArgumentException e) {
            Log.d(this, SWITCHING_EXCEPTION, e);
            throw new SecurityException();
            
        }
    }
    
    
    /**
     * Returns the path to the external storage (&lt;Directory&gt;/&lt;subpath&gt;)
     * 
     * @param subpath
     *            Sub-path &lt;subpath&gt; in the external directory.
     * @return Computed path to the external storage.
     * @throws IllegalArgumentException
     *             Thrown, if the sub-path string contains characters for switching into an upper directory (typically
     *             "../").
     */
    private File getExternalDirectory(String subpath) throws IllegalArgumentException {
        // Prevent switching into an upper directory using "../"
        if (subpath.contains("..")) {
            throw new SecurityException();
            
        }
        
        File root = Environment.getExternalStorageDirectory();
        File baseDir = null;
        
        switch (this.directory) {
            case BASE_DIR:
                baseDir = root;
                break;
            case MUSIC:
                baseDir = new File(root, "Music");
                break;
            case PODCASTS:
                baseDir = new File(root, "Podcasts");
                break;
            case RINGTONES:
                baseDir = new File(root, "Ringtones");
                break;
            case ALARMS:
                baseDir = new File(root, "Alarms");
                break;
            case NOTIFICATIONS:
                baseDir = new File(root, "Notifications");
                break;
            case PICTURES:
                baseDir = new File(root, "Pictures");
                break;
            case MOVIES:
                baseDir = new File(root, "Movies");
                break;
            case DOWNLOAD:
                baseDir = new File(root, "Download");
                break;
        }
        
        return new File(baseDir, subpath);
    }
    
    
    /**
     * Checks if a specific privacy setting is set for an application.
     * 
     * @param privacySettingName
     *            The privacy setting to check.
     * @return True, if privacy setting is set for this application.
     */
    private boolean privacySettingSet(Functions function) {
        String privacySettingName = null;
        
        switch (function) {
        // Read function
            case READ:
                switch (this.directory) {
                    case BASE_DIR:
                        privacySettingName = PrivacySettings.EXTERNAL_BASE_DIR_READ;
                        break;
                    case MUSIC:
                        privacySettingName = PrivacySettings.EXTERNAL_MUSIC_READ;
                        break;
                    case PODCASTS:
                        privacySettingName = PrivacySettings.EXTERNAL_PODCASTS_READ;
                        break;
                    case RINGTONES:
                        privacySettingName = PrivacySettings.EXTERNAL_RINGTONES_READ;
                        break;
                    case ALARMS:
                        privacySettingName = PrivacySettings.EXTERNAL_ALARMS_READ;
                        break;
                    case NOTIFICATIONS:
                        privacySettingName = PrivacySettings.EXTERNAL_NOTIFICATIONS_READ;
                        break;
                    case PICTURES:
                        privacySettingName = PrivacySettings.EXTERNAL_PICTURES_READ;
                        break;
                    case MOVIES:
                        privacySettingName = PrivacySettings.EXTERNAL_MOVIES_READ;
                        break;
                    case DOWNLOAD:
                        privacySettingName = PrivacySettings.EXTERNAL_DOWNLOAD_READ;
                        break;
                }
                break;
            case WRITE:
                // Write function
                switch (this.directory) {
                    case BASE_DIR:
                        privacySettingName = PrivacySettings.EXTERNAL_BASE_DIR_WRITE;
                        break;
                    case MUSIC:
                        privacySettingName = PrivacySettings.EXTERNAL_MUSIC_WRITE;
                        break;
                    case PODCASTS:
                        privacySettingName = PrivacySettings.EXTERNAL_PODCASTS_WRITE;
                        break;
                    case RINGTONES:
                        privacySettingName = PrivacySettings.EXTERNAL_RINGTONES_WRITE;
                        break;
                    case ALARMS:
                        privacySettingName = PrivacySettings.EXTERNAL_ALARMS_WRITE;
                        break;
                    case NOTIFICATIONS:
                        privacySettingName = PrivacySettings.EXTERNAL_NOTIFICATIONS_WRITE;
                        break;
                    case PICTURES:
                        privacySettingName = PrivacySettings.EXTERNAL_PICTURES_WRITE;
                        break;
                    case MOVIES:
                        privacySettingName = PrivacySettings.EXTERNAL_MOVIES_WRITE;
                        break;
                    case DOWNLOAD:
                        privacySettingName = PrivacySettings.EXTERNAL_DOWNLOAD_WRITE;
                        break;
                }
                break;
            case LIST:
                // List function
                switch (this.directory) {
                    case BASE_DIR:
                        privacySettingName = PrivacySettings.EXTERNAL_BASE_DIR_LIST;
                        break;
                    case MUSIC:
                        privacySettingName = PrivacySettings.EXTERNAL_MUSIC_LIST;
                        break;
                    case PODCASTS:
                        privacySettingName = PrivacySettings.EXTERNAL_PODCASTS_LIST;
                        break;
                    case RINGTONES:
                        privacySettingName = PrivacySettings.EXTERNAL_RINGTONES_LIST;
                        break;
                    case ALARMS:
                        privacySettingName = PrivacySettings.EXTERNAL_ALARMS_LIST;
                        break;
                    case NOTIFICATIONS:
                        privacySettingName = PrivacySettings.EXTERNAL_NOTIFICATIONS_LIST;
                        break;
                    case PICTURES:
                        privacySettingName = PrivacySettings.EXTERNAL_PICTURES_LIST;
                        break;
                    case MOVIES:
                        privacySettingName = PrivacySettings.EXTERNAL_MOVIES_LIST;
                        break;
                    case DOWNLOAD:
                        privacySettingName = PrivacySettings.EXTERNAL_DOWNLOAD_LIST;
                        break;
                }
                break;
            case DELETE:
                // Delete function
                switch (this.directory) {
                    case BASE_DIR:
                        privacySettingName = PrivacySettings.EXTERNAL_BASE_DIR_DELETE;
                        break;
                    case MUSIC:
                        privacySettingName = PrivacySettings.EXTERNAL_MUSIC_DELETE;
                        break;
                    case PODCASTS:
                        privacySettingName = PrivacySettings.EXTERNAL_PODCASTS_DELETE;
                        break;
                    case RINGTONES:
                        privacySettingName = PrivacySettings.EXTERNAL_RINGTONES_DELETE;
                        break;
                    case ALARMS:
                        privacySettingName = PrivacySettings.EXTERNAL_ALARMS_DELETE;
                        break;
                    case NOTIFICATIONS:
                        privacySettingName = PrivacySettings.EXTERNAL_NOTIFICATIONS_DELETE;
                        break;
                    case PICTURES:
                        privacySettingName = PrivacySettings.EXTERNAL_PICTURES_DELETE;
                        break;
                    case MOVIES:
                        privacySettingName = PrivacySettings.EXTERNAL_MOVIES_DELETE;
                        break;
                    case DOWNLOAD:
                        privacySettingName = PrivacySettings.EXTERNAL_DOWNLOAD_DELETE;
                        break;
                }
                break;
            
            case MAKE_DIRS:
                // Delete function
                switch (this.directory) {
                    case BASE_DIR:
                        privacySettingName = PrivacySettings.EXTERNAL_BASE_DIR_MAKE_DIRS;
                        break;
                    case MUSIC:
                        privacySettingName = PrivacySettings.EXTERNAL_MUSIC_MAKE_DIRS;
                        break;
                    case PODCASTS:
                        privacySettingName = PrivacySettings.EXTERNAL_PODCASTS_MAKE_DIRS;
                        break;
                    case RINGTONES:
                        privacySettingName = PrivacySettings.EXTERNAL_RINGTONES_MAKE_DIRS;
                        break;
                    case ALARMS:
                        privacySettingName = PrivacySettings.EXTERNAL_ALARMS_MAKE_DIRS;
                        break;
                    case NOTIFICATIONS:
                        privacySettingName = PrivacySettings.EXTERNAL_NOTIFICATIONS_MAKE_DIRS;
                        break;
                    case PICTURES:
                        privacySettingName = PrivacySettings.EXTERNAL_PICTURES_MAKE_DIRS;
                        break;
                    case MOVIES:
                        privacySettingName = PrivacySettings.EXTERNAL_MOVIES_MAKE_DIRS;
                        break;
                    case DOWNLOAD:
                        privacySettingName = PrivacySettings.EXTERNAL_DOWNLOAD_MAKE_DIRS;
                        break;
                }
                break;
        }
        
        return PrivacySettings.privacySettingSet(privacySettingName, this.app, this.resource);
    }
}
