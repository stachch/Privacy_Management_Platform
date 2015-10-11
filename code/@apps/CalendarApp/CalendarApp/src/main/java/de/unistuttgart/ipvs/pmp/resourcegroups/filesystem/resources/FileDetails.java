/*
 * Copyright 2012 pmp-android development team
 * Project: CalendarApp
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

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Stores details of a file. This is used for security reasons. If we would directly transfer a File object, the user
 * could easily modify files without using our resource (e.g <code>File.delete()</code>)
 * 
 * @author Patrick Strobel
 * @version 0.1.0
 */
public class FileDetails implements Parcelable {
    
    private String absolutePath;
    private String name;
    private String path;
    
    private boolean canRead;
    private boolean canWrite;
    
    private boolean isAbsolute;
    private boolean isDirectory;
    private boolean isFile;
    
    public static final Parcelable.Creator<FileDetails> CREATOR = new Parcelable.Creator<FileDetails>() {
        
        @Override
        public FileDetails createFromParcel(Parcel source) {
            return FileDetails.createFromParcel(source);
        }
        
        
        @Override
        public FileDetails[] newArray(int size) {
            return new FileDetails[size];
        }
    };
    
    
    private FileDetails() {
    }
    
    
    /**
     * Creates a FileDetails object using data from a file object
     * 
     * @param file
     *            Source of data used for this FileDetails object
     */
    protected FileDetails(File file) {
        this.absolutePath = file.getAbsolutePath();
        this.name = file.getName();
        this.path = file.getPath();
        
        this.canRead = file.canRead();
        this.canWrite = file.canWrite();
        
        this.isAbsolute = file.isAbsolute();
        this.isDirectory = file.isDirectory();
        this.isFile = file.isFile();
    }
    
    
    /**
     * Returns true, if file is readable
     * 
     * @see File#canRead()
     * @return True, if readable
     */
    public boolean canRead() {
        return this.canRead;
    }
    
    
    /**
     * Returns true, if file is writable
     * 
     * @see File#canWrite()
     * @return True, if writable
     */
    public boolean canWrite() {
        return this.canWrite;
    }
    
    
    /**
     * Returns the absolute path of this file
     * 
     * @see File#getAbsolutePath()
     * @return Absolute path
     */
    public String getAbsolutePath() {
        return this.absolutePath;
    }
    
    
    /**
     * Returns the name of this file
     * 
     * @see File#getName()
     * @return Name
     */
    public String getName() {
        return this.name;
    }
    
    
    /**
     * Returns the path of this file
     * 
     * @see File#getPath()
     * @return Path
     */
    public String getPath() {
        return this.path;
    }
    
    
    /**
     * Returns true, if path is absolute
     * 
     * @see File#isAbsolute()
     * @return True, if absolute
     */
    public boolean isAbsolute() {
        return this.isAbsolute;
    }
    
    
    /**
     * Returns true, if this is a directory
     * 
     * @see File#isDirectory()
     * @return True, if directory
     */
    public boolean isDirectory() {
        return this.isDirectory;
    }
    
    
    /**
     * Returns true, if this is a file
     * 
     * @see File#isFile()
     * @return True, if file
     */
    public boolean isFile() {
        return this.isFile;
    }
    
    
    @Override
    public int describeContents() {
        return 0;
    }
    
    
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // Store booleans in parcel
        boolean[] bools = new boolean[5];
        bools[0] = this.canRead;
        bools[1] = this.canWrite;
        bools[2] = this.isAbsolute;
        bools[3] = this.isDirectory;
        bools[4] = this.isFile;
        dest.writeBooleanArray(bools);
        
        // Store strings in parcel
        dest.writeString(this.absolutePath);
        dest.writeString(this.name);
        dest.writeString(this.path);
    }
    
    
    /**
     * Recreates the FileDetails object from a parcel
     * 
     * @param source
     *            Parcel
     * @return Recovered FileDetails object
     */
    private static FileDetails createFromParcel(Parcel source) {
        // TODO security check
        FileDetails f = new FileDetails();
        
        // Restore booleans from parcel
        boolean[] bools = new boolean[5];
        source.readBooleanArray(bools);
        f.canRead = bools[0];
        f.canWrite = bools[1];
        f.isAbsolute = bools[2];
        f.isDirectory = bools[3];
        f.isFile = bools[4];
        
        // Restore strings from parcel
        f.absolutePath = source.readString();
        f.name = source.readString();
        f.path = source.readString();
        return f;
    }
    
    
    /**
     * Get the string represetation for the list view
     */
    @Override
    public String toString() {
        return this.name + "\n" + this.absolutePath;
    }
}
