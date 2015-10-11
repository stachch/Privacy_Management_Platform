package de.unistuttgart.ipvs.pmp.resourcegroups.filesystem.resources;

import java.util.List;
import de.unistuttgart.ipvs.pmp.resourcegroups.filesystem.resources.FileDetails;

/**
 * Interface for all file-access functions. For details, see documentation
 * of {@see ExternalFileAccess} when using "ext_..." resources or 
 * {@see GenericFileAccess} when using "gen" resource.
 * @author Patrick Strobel
 * @version 0.1.0
 */
interface IFileAccess {
    
    /**
     * Reads a file stored on the device into a string.
     * 
     * @param path
     *            <b>"ext_..." resources:</b><br /> 
     *			  Path to the file in the external directory as sub-directory of the currently selected external directory.
     *            For example, if this resource gives access to the Music-Directory and <code>path</code> is set to 
     *            <code>example/testFile.txt</code>, then <code>Music/example/testFile.txt</code> will be read).
     *   	      <br /><br />
     *            <b>"gen" resource:</b><br />
     *			  Path to the file beginning from the device's root folder.
     * @return Data of the selected file.
     * @throws IllegalAccessError
     *             Thrown, if app's privacy setting is not set or the <code>path</code> parameters contains character for
     *             switching into a upper directory (typically <code>../</code>) (only in "ext_" resources).
     * @throws RemoteException
     *             Thrown, if file is not readable (e.g. does not exist).
     */
    String read(String path);
    
    /**
     * Writes a given string into a file.
     * 
     * @param path
     *            <b>"ext_..." resources:</b><br /> 
     *			  Path to the file in the external directory the string should be written to.
     *            For example, if this resource gives access to the Music-Directory and <code>path</code> is set to 
     *            <code>example/testFile.txt</code>, then the string will be written to
     *            <code>Music/example/testFile.txt</code>).
     *   	      <br /><br />
     *            <b>"gen" resource:</b><br />
     *			  Path to the file beginning from the device's root folder.
     * @param data
     *            Date to write into the selected file.
     * @param append
     *            True, if data should be appended to the existing file data. Otherwise it's data will be overwritten.
     * @return True, if data was successfully written.
     * @throws IllegalAccessError
     *             Thrown, if app's privacy setting is not set or the <code>path</code> parameters contains character for
     *             switching into a upper directory (typically <code>../</code>) (only in "ext_" resources).
     */
    boolean write(String path, String data, boolean append);
    
    /**
     * Deletes a file or directory. Directories can only be deleted if they do not have any files.
     * 
     * @param path
     *            <b>"ext_..." resources:</b><br /> 
     *			  Path of the file or directory which should be deleted.
     *            For example, if this resource gives access to the Music-Directory and <code>path</code> is set to 
     *            <code>example/testDir</code>, then <code>Music/example/testDir</code> will be deleted).
     *   	      <br /><br />
     *            <b>"gen" resource:</b><br />
     *			  Path to the file or directory beginning from the device's root folder.
     * @return True, if file or directory was deleted successfully.
     * @throws IllegalAccessError
     *             Thrown, if app's privacy setting is not set or the <code>path</code> parameters contains character for
     *             switching into a upper directory (typically <code>../</code>) (only in "ext_" resources).
     */
    boolean delete(String path);
    
    /**
     * Returns a list of all files and directories in a given directory.
     * 
     * @param path
     *            <b>"ext_..." resources:</b><br /> 
     *			  Path of the parent directory.
     *            For example, if this resource gives access to the Music-Directory and <code>path</code> is set to 
     *            <code>example/testDir</code>, then a list of all files and sub-directories in
     *            <code>Music/example/testDir</code> will be generated).
     *   	      <br /><br />
     *            <b>"gen" resource:</b><br />
     *			  Path to the file or directory beginning from the device's root folder.
     * @return List of detailed file information data or null, if path points to a non existing directory or a file
     * @throws IllegalAccessError
     *             Thrown, if the app's privacy setting is not set or the <code>path</code> parameters contains character
     *             for switching into a upper directory (typically <code>../</code>) (only in "ext_" resources).
     */
    List<FileDetails> list(String directory);
    
    /**
     * Creates all directories that are not existing.
     * 
     * @param path
     *            <b>"ext_..." resources:</b><br /> 
     *			  Directory path.
     *            For example, if this resource gives access to the Music-Directory and <code>path</code> is set to 
     *            <code>example/testDir</code>, then <code>Music/example/testFDir</code> will be created).
     *   	      <br /><br />
     *            <b>"gen" resource:</b><br />
     *			  Path to the file or directory beginning from the device's root folder.
     * @return True, if directories where created successfully.
     * @throws IllegalAccessError
     *             Thrown, if the app's privacy setting is not set or the <code>path</code> parameters contains character
     *             for switching into a upper directory (typically <code>../</code>) (only in "ext_" resources).
     */
    boolean makeDirs(String path);
}
