package de.unistuttgart.ipvs.pmp.resourcegroups.filesystem.resources;

import java.util.List;
import de.unistuttgart.ipvs.pmp.resourcegroups.filesystem.resources.FileDetails;

interface IFileAccess {
    
    String read(String path);
    boolean write(String path, String data, boolean append);
    boolean delete(String path);
    List<FileDetails> list(String directory);
    boolean makeDirs(String path);
}
