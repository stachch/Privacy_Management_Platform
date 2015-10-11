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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility-Class for string-to-file and file-to-string functions
 * 
 * @author Patrick Strobel
 * @version 0.1.1
 */
public class Utils {
    
    /**
     * Reads a file and returns it data as a string-object
     * 
     * @param file
     *            File to read
     * @return Read data
     * @throws IOException
     *             See {@link BufferedReader#read()}
     * @throws FileNotFoundException
     *             {@link BufferedReader#BufferedReader(java.io.Reader)}
     */
    public static String readFileToString(File file) throws IOException, FileNotFoundException {
        
        BufferedReader reader = new BufferedReader(new FileReader(file));
        
        StringBuilder result = new StringBuilder();
        String line = reader.readLine();
        result.append(line);
        
        while ((line = reader.readLine()) != null) {
            result.append("\n");
            result.append(line);
        }
        reader.close();
        return result.toString();
    }
    
    
    /**
     * Writes a string to a file
     * 
     * @param file
     *            File to which this string should be written
     * @param data
     *            Data to write
     * @param append
     *            True, if data should be appended
     * @throws IOException
     *             See {@link BufferedWriter#BufferedWriter(java.io.Writer)}
     */
    public static void writeStringToFile(File file, String data, boolean append) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file, append));
        writer.write(data);
        writer.close();
    }
    
    
    /**
     * Gets a list of all files and directories
     * 
     * @param file
     *            Path to the base directory
     * @return List of FileDetails objects
     */
    public static List<FileDetails> getFileDetailsList(File file) {
        File[] fileArray = file.listFiles();
        
        // The user has selected a file instead of a directory
        if (fileArray == null) {
            return null;
        }
        
        // Generate our FileDetails-List
        List<FileDetails> detailsList = new ArrayList<FileDetails>();
        
        for (File f : fileArray) {
            detailsList.add(new FileDetails(f));
        }
        
        return detailsList;
    }
}
