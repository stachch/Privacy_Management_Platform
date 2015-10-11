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
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.content.Context;
import android.graphics.drawable.Drawable;
import dalvik.system.DexClassLoader;
import de.unistuttgart.ipvs.pmp.PMPApplication;
import de.unistuttgart.ipvs.pmp.model.assertion.Assert;
import de.unistuttgart.ipvs.pmp.model.assertion.ModelMisuseError;
import de.unistuttgart.ipvs.pmp.model.exception.InvalidPluginException;
import de.unistuttgart.ipvs.pmp.shared.Log;
import de.unistuttgart.ipvs.pmp.shared.resource.IPMPConnectionInterface;
import de.unistuttgart.ipvs.pmp.shared.resource.ResourceGroup;
import de.unistuttgart.ipvs.pmp.xmlutil.XMLUtilityProxy;
import de.unistuttgart.ipvs.pmp.xmlutil.revision.RevisionReader;
import de.unistuttgart.ipvs.pmp.xmlutil.rgis.IRGIS;

/**
 * @see IPluginProvider
 * @author Tobias Kuhn
 * 
 */
public class PluginProvider implements IPluginProvider {
    
    /*
     * constants 
     */
    
    private static final Context context = PMPApplication.getContext();
    
    private static final String PLUGIN_BASE_DIR_STR = "plugins";
    private static final File PLUGIN_BASE_DIR = context.getDir(PLUGIN_BASE_DIR_STR, Context.MODE_PRIVATE);
    
    private static final String PLUGIN_APK_DIR_STR = PLUGIN_BASE_DIR.getAbsolutePath() + "/apk/";
    private static final String PLUGIN_DEX_DIR_STR = PLUGIN_BASE_DIR.getAbsolutePath() + "/dex/";
    private static final String PLUGIN_ASSET_DIR_STR = PLUGIN_BASE_DIR.getAbsolutePath() + "/ass/";
    
    private static final File PLUGIN_APK_DIR = new File(PLUGIN_APK_DIR_STR);
    private static final File PLUGIN_DEX_DIR = new File(PLUGIN_DEX_DIR_STR);
    private static final File PLUGIN_ASSET_DIR = new File(PLUGIN_ASSET_DIR_STR);
    
    private static final String APK_STR = ".apk";
    private static final String XML_STR = ".xml";
    private static final String PNG_STR = ".png";
    
    private static final ClassLoader CLASS_LOADER = context.getClassLoader();
    
    // errors
    private static final String DURING_INSTALL = " (Trying to install '%s' from '%s' using main class '%s')";
    private static final String DURING_CACHE = " (Trying to load cache '%s' from '%s' using main class '%s')";
    
    private static final String ERROR_CLASS_NOT_FOUND = "Main class not found";
    private static final String ERROR_CLASS_NOT_CASTABLE = "Main class of wrong type";
    private static final String ERROR_CLASS_CONSTRUCTOR_ACCESS_NOT_ALLOWED = "Accessing constructor of main class not allowed";
    private static final String ERROR_CLASS_CONSTRUCTOR_NOT_FOUND = "Constructor of main class not found";
    private static final String ERROR_CLASS_CONSTRUCTOR_INVALID_ARGUMENT = "Constructor of main class expects invalid arguments";
    private static final String ERROR_CLASS_NOT_INSTANTIABLE = "Main class is not instantiable";
    private static final String ERROR_CLASS_CONSTRUCTOR_NOT_ACCESSIBLE = "Constructor of main class not accessible";
    private static final String ERROR_CLASS_CONSTRUCTOR_THROWS_EXCEPTION = "Constructor of main class throws exception";
    private static final String ERROR_APK_NOT_ACCESSIBLE = "Accessing the apk or the assets failed";
    private static final String ERROR_RGIS_NOT_ACCESSIBLE = "Accessing the cached RGIS failed";
    
    /*
     * fields
     */
    private Map<String, ResourceGroup> cache;
    private Map<String, IRGIS> cacheRGIS;
    private Map<String, Long> cacheRevision;
    
    /*
     * singleton stuff
     */
    
    private static final IPluginProvider instance = new PluginProvider();
    
    
    public static IPluginProvider getInstance() {
        return instance;
    }
    
    
    private PluginProvider() {
        this.cache = new HashMap<String, ResourceGroup>();
        this.cacheRGIS = new HashMap<String, IRGIS>();
        this.cacheRevision = new HashMap<String, Long>();
        
        if (!PLUGIN_BASE_DIR.mkdirs() && !PLUGIN_BASE_DIR.exists()) {
            Log.e(this, "Error while creating directory in PluginProvider: " + PLUGIN_BASE_DIR.getAbsolutePath());
        }
        
        if (!PLUGIN_APK_DIR.mkdirs() && !PLUGIN_APK_DIR.exists()) {
            Log.e(this, "Error while creating directory in PluginProvider: " + PLUGIN_APK_DIR.getAbsolutePath());
        }
        
        if (!PLUGIN_DEX_DIR.mkdirs() && !PLUGIN_DEX_DIR.exists()) {
            Log.e(this, "Error while creating directory in PluginProvider: " + PLUGIN_DEX_DIR.getAbsolutePath());
        }
        
        if (!PLUGIN_ASSET_DIR.mkdirs() && !PLUGIN_ASSET_DIR.exists()) {
            Log.e(this, "Error while creating directory in PluginProvider: " + PLUGIN_ASSET_DIR.getAbsolutePath());
        }
    }
    
    
    /**
     * Assures that the identified resource group is loaded.
     * 
     * @param rgPackage
     * @throws InvalidPluginException
     *             if the supplied plugin is somehow corrupt
     */
    private void checkCached(String rgPackage) throws InvalidPluginException {
        String errorMsg;
        
        // RGIS
        if (this.cacheRGIS.get(rgPackage) == null) {
            try {
                this.cacheRGIS.put(rgPackage, loadRGIS(rgPackage));
                
            } catch (IOException ioe) {
                errorMsg = String.format(ERROR_RGIS_NOT_ACCESSIBLE + DURING_CACHE, rgPackage, PLUGIN_ASSET_DIR_STR
                        + rgPackage + ".xml", "Unknown");
                Log.e(this, errorMsg, ioe);
                throw new ModelMisuseError(Assert.format(Assert.ILLEGAL_PLUGIN_FAULT, "rgPackage", rgPackage));
            }
        }
        
        String apkName = PLUGIN_APK_DIR_STR + rgPackage + APK_STR;
        
        // object
        if (this.cache.get(rgPackage) == null) {
            String className = this.cacheRGIS.get(rgPackage).getClassName();
            
            try {
                this.cache.put(rgPackage, loadRGObject(rgPackage, apkName, className));
                
            } catch (ClassNotFoundException cnfe) {
                errorMsg = String.format(ERROR_CLASS_NOT_FOUND + DURING_CACHE, rgPackage, apkName, className);
                Log.e(this, errorMsg, cnfe);
                throw new InvalidPluginException(errorMsg, cnfe);
            } catch (NoSuchMethodException nsme) {
                errorMsg = String.format(ERROR_CLASS_CONSTRUCTOR_NOT_FOUND + DURING_CACHE, rgPackage, apkName,
                        className);
                Log.e(this, errorMsg, nsme);
                throw new InvalidPluginException(errorMsg, nsme);
            } catch (InstantiationException ie) {
                errorMsg = String.format(ERROR_CLASS_NOT_INSTANTIABLE + DURING_CACHE, rgPackage, apkName, className);
                Log.e(this, errorMsg, ie);
                throw new InvalidPluginException(errorMsg, ie);
            } catch (IllegalAccessException iae) {
                errorMsg = String.format(ERROR_CLASS_CONSTRUCTOR_NOT_ACCESSIBLE + DURING_CACHE, rgPackage, apkName,
                        className);
                Log.e(this, errorMsg, iae);
                throw new InvalidPluginException(errorMsg, iae);
            } catch (InvocationTargetException ite) {
                errorMsg = String.format(ERROR_CLASS_CONSTRUCTOR_THROWS_EXCEPTION + DURING_CACHE, rgPackage, apkName,
                        className);
                Log.e(this, errorMsg, ite);
                throw new InvalidPluginException(errorMsg, ite);
            }
            
        }
        
        // revision
        if (this.cacheRevision.get(rgPackage) == null) {
            Long revision = RevisionReader.get().readRevision(new File(apkName));
            
            this.cacheRevision.put(rgPackage, revision);
        }
        
    }
    
    
    /**
     * Copies the data in input stream to the target.
     * 
     * @param inputStream
     * @param target
     */
    private void copyFile(InputStream inputStream, String target) {
        try {
            FileOutputStream fos = new FileOutputStream(target);
            
            try {
                byte[] buffer = new byte[32 * 1024];
                
                while (inputStream.available() > 0) {
                    int read = inputStream.read(buffer);
                    fos.write(buffer, 0, read);
                }
                
            } finally {
                fos.close();
                
            }
        } catch (IOException ioe) {
            Log.e(this, "IO exception during copy file to " + target, ioe);
        }
        
    }
    
    
    /**
     * Well duh-huh.
     * 
     * @param fileName
     */
    private void deleteFile(String fileName) {
        if (!(new File(fileName).delete())) {
            Log.e(this, "Error while deleting " + fileName);
        }
    }
    
    
    @Override
    public void injectFile(String rgPackage, InputStream input) {
        Assert.nonNull(rgPackage, ModelMisuseError.class, Assert.ILLEGAL_NULL, "rgPackage", rgPackage);
        Assert.nonNull(input, ModelMisuseError.class, Assert.ILLEGAL_NULL, "input", input);
        copyFile(input, PLUGIN_APK_DIR_STR + rgPackage + APK_STR);
    }
    
    
    @Override
    public void install(String rgPackage) throws InvalidPluginException {
        Assert.nonNull(rgPackage, ModelMisuseError.class, Assert.ILLEGAL_NULL, "rgPackage", rgPackage);
        
        // identify the important attributes first
        String apkName = PLUGIN_APK_DIR_STR + rgPackage + APK_STR;
        String className = "unknown";
        String errorMsg;
        
        try {
            
            // extract the XML, so we have the information from there
            ZipFile zipApk = new ZipFile(apkName);
            try {
                ZipEntry xmlEntry = zipApk.getEntry("assets/rgis" + XML_STR);
                if (xmlEntry != null) {
                    copyFile(zipApk.getInputStream(xmlEntry), PLUGIN_ASSET_DIR_STR + rgPackage + XML_STR);
                } else {
                    throw new IOException("assets/rgis" + XML_STR + " missing.");
                }
                
                // create the RGIS
                IRGIS rgis = loadRGIS(rgPackage);
                className = rgis.getClassName();
                
                // extract icon
                ZipEntry iconEntry = zipApk.getEntry(rgis.getIconLocation());
                if (iconEntry != null) {
                    copyFile(zipApk.getInputStream(iconEntry), PLUGIN_ASSET_DIR_STR + rgPackage + PNG_STR);
                } else {
                    throw new IOException(rgis.getIconLocation() + " missing.");
                }
                
                // load main class
                ResourceGroup rg = loadRGObject(rgPackage, apkName, className);
                
                // establish the revision
                Long revision = RevisionReader.get().readRevision(new File(apkName));
                
                // store in cache
                this.cache.put(rgPackage, rg);
                this.cacheRGIS.put(rgPackage, rgis);
                this.cacheRevision.put(rgPackage, revision);
                
            } finally {
                zipApk.close();
            }
            
        } catch (ClassNotFoundException cnfe) {
            errorMsg = String.format(ERROR_CLASS_NOT_FOUND + DURING_INSTALL, rgPackage, apkName, className);
            Log.e(this, errorMsg, cnfe);
            throw new InvalidPluginException(errorMsg, cnfe);
        } catch (ClassCastException cce) {
            errorMsg = String.format(ERROR_CLASS_NOT_CASTABLE + DURING_INSTALL, rgPackage, apkName, className);
            Log.e(this, errorMsg, cce);
            throw new InvalidPluginException(errorMsg, cce);
        } catch (SecurityException se) {
            errorMsg = String.format(ERROR_CLASS_CONSTRUCTOR_ACCESS_NOT_ALLOWED + DURING_INSTALL, rgPackage, apkName,
                    className);
            Log.e(this, errorMsg, se);
            throw new InvalidPluginException(errorMsg, se);
        } catch (NoSuchMethodException nsme) {
            errorMsg = String.format(ERROR_CLASS_CONSTRUCTOR_NOT_FOUND + DURING_INSTALL, rgPackage, apkName, className);
            Log.e(this, errorMsg, nsme);
            throw new InvalidPluginException(errorMsg, nsme);
        } catch (IllegalArgumentException iae) {
            errorMsg = String.format(ERROR_CLASS_CONSTRUCTOR_INVALID_ARGUMENT + DURING_INSTALL, rgPackage, apkName,
                    className);
            Log.e(this, errorMsg, iae);
            throw new InvalidPluginException(errorMsg, iae);
        } catch (InstantiationException ie) {
            errorMsg = String.format(ERROR_CLASS_NOT_INSTANTIABLE + DURING_INSTALL, rgPackage, apkName, className);
            Log.e(this, errorMsg, ie);
            throw new InvalidPluginException(errorMsg, ie);
        } catch (IllegalAccessException iae) {
            errorMsg = String.format(ERROR_CLASS_CONSTRUCTOR_NOT_ACCESSIBLE + DURING_INSTALL, rgPackage, apkName,
                    className);
            Log.e(this, errorMsg, iae);
            throw new InvalidPluginException(errorMsg, iae);
        } catch (InvocationTargetException ite) {
            errorMsg = String.format(ERROR_CLASS_CONSTRUCTOR_THROWS_EXCEPTION + DURING_INSTALL, rgPackage, apkName,
                    className);
            Log.e(this, errorMsg, ite);
            throw new InvalidPluginException(errorMsg, ite);
        } catch (IOException ioe) {
            errorMsg = String.format(ERROR_APK_NOT_ACCESSIBLE + DURING_INSTALL, rgPackage, apkName, className);
            Log.e(this, errorMsg, ioe);
            throw new InvalidPluginException(errorMsg, ioe);
        }
    }
    
    
    /**
     * Creates a new {@link ResourceGroup} object for a resource group.
     * 
     * @param rgPackage
     *            the package identifying the resource group
     * @param apkName
     *            the apk containing the resource group
     * @param className
     *            the name of the main class of the resource group
     * @return a (new) object for the resource group
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private ResourceGroup loadRGObject(String rgPackage, String apkName, String className)
            throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException,
            InvocationTargetException {
        DexClassLoader classLoader = new DexClassLoader(apkName, PLUGIN_DEX_DIR_STR, null, CLASS_LOADER);
        
        Class<?> clazz = classLoader.loadClass(rgPackage + "." + className);
        Class<? extends ResourceGroup> rgClazz = clazz.asSubclass(ResourceGroup.class);
        Constructor<? extends ResourceGroup> rgConstruct = rgClazz.getConstructor(IPMPConnectionInterface.class);
        ResourceGroup rg = rgConstruct.newInstance(PMPConnectionInterface.getInstance());
        return rg;
    }
    
    
    /**
     * Loads the RGIS for a specific resource group.
     * 
     * @param rgPackage
     *            identifier of the resource group
     * @return the RGIS for the resource group
     * @throws FileNotFoundException
     * @throws IOException
     */
    private IRGIS loadRGIS(String rgPackage) throws FileNotFoundException, IOException {
        FileInputStream fis = new FileInputStream(PLUGIN_ASSET_DIR_STR + rgPackage + ".xml");
        try {
            return XMLUtilityProxy.getRGUtil().parse(fis);
        } finally {
            fis.close();
        }
    }
    
    
    @Override
    public void uninstall(String rgPackage) {
        Assert.nonNull(rgPackage, ModelMisuseError.class, Assert.ILLEGAL_NULL, "rgPackage", rgPackage);
        this.cache.remove(rgPackage);
        this.cacheRGIS.remove(rgPackage);
        this.cacheRevision.remove(rgPackage);
        deleteFile(PLUGIN_ASSET_DIR_STR + rgPackage + PNG_STR);
        deleteFile(PLUGIN_ASSET_DIR_STR + rgPackage + XML_STR);
        deleteFile(PLUGIN_APK_DIR_STR + rgPackage + APK_STR);
    }
    
    
    @Override
    public ResourceGroup getResourceGroupObject(String rgPackage) throws InvalidPluginException {
        Assert.nonNull(rgPackage, ModelMisuseError.class, Assert.ILLEGAL_NULL, "rgPackage", rgPackage);
        checkCached(rgPackage);
        return this.cache.get(rgPackage);
    }
    
    
    @Override
    public IRGIS getRGIS(String rgPackage) throws InvalidPluginException {
        Assert.nonNull(rgPackage, ModelMisuseError.class, Assert.ILLEGAL_NULL, "rgPackage", rgPackage);
        checkCached(rgPackage);
        return this.cacheRGIS.get(rgPackage);
        
    }
    
    
    @Override
    public Drawable getIcon(String rgPackage) throws InvalidPluginException {
        Assert.nonNull(rgPackage, ModelMisuseError.class, Assert.ILLEGAL_NULL, "rgPackage", rgPackage);
        checkCached(rgPackage);
        return Drawable.createFromPath(PLUGIN_ASSET_DIR_STR + rgPackage + ".png");
    }
    
    
    @Override
    public long getRevision(String rgPackage) throws InvalidPluginException {
        Assert.nonNull(rgPackage, ModelMisuseError.class, Assert.ILLEGAL_NULL, "rgPackage", rgPackage);
        checkCached(rgPackage);
        Long result = this.cacheRevision.get(rgPackage);
        if (result == null) {
            result = 0L;
        }
        return result;
    }
    
}
