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
package de.unistuttgart.ipvs.pmp.model.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Date;

import de.unistuttgart.ipvs.pmp.PMPApplication;
import de.unistuttgart.ipvs.pmp.jpmpps.JPMPPSConstants;
import de.unistuttgart.ipvs.pmp.jpmpps.io.request.AbstractRequest;
import de.unistuttgart.ipvs.pmp.jpmpps.io.request.RequestCommunicationEnd;
import de.unistuttgart.ipvs.pmp.jpmpps.io.request.RequestPresetSetLoad;
import de.unistuttgart.ipvs.pmp.jpmpps.io.request.RequestPresetSetSave;
import de.unistuttgart.ipvs.pmp.jpmpps.io.request.RequestResourceGroupPackage;
import de.unistuttgart.ipvs.pmp.jpmpps.io.request.RequestResourceGroups;
import de.unistuttgart.ipvs.pmp.jpmpps.io.response.AbstractResponse;
import de.unistuttgart.ipvs.pmp.jpmpps.io.response.CachedRequestResponse;
import de.unistuttgart.ipvs.pmp.jpmpps.io.response.PresetSetLoadResponse;
import de.unistuttgart.ipvs.pmp.jpmpps.io.response.PresetSetSaveResponse;
import de.unistuttgart.ipvs.pmp.jpmpps.io.response.ResourceGroupPackageResponse;
import de.unistuttgart.ipvs.pmp.jpmpps.io.response.ResourceGroupsResponse;
import de.unistuttgart.ipvs.pmp.jpmpps.model.LocalizedResourceGroup;
import de.unistuttgart.ipvs.pmp.model.assertion.Assert;
import de.unistuttgart.ipvs.pmp.model.assertion.ModelIntegrityError;
import de.unistuttgart.ipvs.pmp.model.assertion.ModelMisuseError;
import de.unistuttgart.ipvs.pmp.shared.Log;
import de.unistuttgart.ipvs.pmp.xmlutil.presetset.IPresetSet;
import de.unistuttgart.ipvs.pmp.xmlutil.presetset.PresetSet;

/**
 * @see IServerProvider
 * @author Tobias Kuhn
 * 
 */
public class ServerProvider implements IServerProvider {
    
    /*
     * constants
     */
    private static final String SERVER_URL = JPMPPSConstants.HOSTNAME;
    private static final int SERVER_PORT = JPMPPSConstants.PORT;
    
    private static final int BUFFER_SIZE = 32 * 1024;
    
    /**
     * Amount of time where the cache is that fresh that the server is not even contacted in milliseconds.
     */
    private static final long LOCAL_CACHE_ONLY_TIME = 60000L;
    
    private static final String APK_STR = ".apk";
    private static final String TEMPORARY_PATH = PMPApplication.getContext().getCacheDir().getAbsolutePath() + "/";
    
    /*
     * fields
     */
    private IServerDownloadCallback callback;
    
    /*
     * singleton stuff
     */
    
    private static final IServerProvider instance = new ServerProvider();
    
    
    public static IServerProvider getInstance() {
        return instance;
    }
    
    
    private ServerProvider() {
        if (!PMPApplication.getContext().getCacheDir().mkdirs() && !PMPApplication.getContext().getCacheDir().exists()) {
            Log.e(this, "Error while creating directory in ServerProvider.");
        }
        this.callback = NullServerDownloadCallback.instance;
    }
    
    
    public AbstractResponse handleRequest(AbstractRequest request) throws IOException, ClassNotFoundException {
        Socket tcpSocket = new Socket(SERVER_URL, SERVER_PORT);
        
        Object result = null;
        
        ObjectOutputStream oos = new ObjectOutputStream(tcpSocket.getOutputStream());
        ObjectInputStream ois = new ObjectInputStream(tcpSocket.getInputStream());
        try {
            
            Log.d(this, "Sending request " + request.toString() + " ...");
            this.callback.step(3, 7);
            oos.writeObject(request);
            
            Log.d(this, "Receiving ...");
            this.callback.step(4, 7);
            result = ois.readObject();
            if (!(result instanceof AbstractResponse)) {
                throw new ClassNotFoundException();
            }
            
            Log.d(this, "Sending RequestCommunicationEnd ...");
            this.callback.step(5, 7);
            oos.writeObject(new RequestCommunicationEnd());
            
        } finally {
            ois.close();
            oos.close();
            tcpSocket.close();
        }
        
        return (AbstractResponse) result;
    }
    
    
    /**
     * Retrieves an {@link AbstractResponse} that fits the request.
     * 
     * @param requestString
     *            comType == REQUEST_APK ? rgPackage : search string
     * @param comType
     *            which kind of request is associated with this string
     * @return the corresponding {@link AbstractResponse}, or null if an error occurred
     */
    public AbstractResponse getResponseFor(String requestString, CommunicationType comType) {
        String cacheHash;
        switch (comType) {
            case REQUEST_RESOURCE_GROUP_APK:
                cacheHash = "r" + String.valueOf(requestString.hashCode());
                break;
            
            default:
                cacheHash = String.valueOf(requestString.hashCode());
                break;
        }
        
        this.callback.step(0, 7);
        
        // check whether cache would be available
        AbstractResponse cachedResponse = null;
        File cacheFile = new File(TEMPORARY_PATH + cacheHash);
        if (cacheFile.exists()) {
            try {
                
                FileInputStream fis = new FileInputStream(cacheFile);
                try {
                    ObjectInputStream ois = new ObjectInputStream(fis);
                    try {
                        
                        Object o = ois.readObject();
                        if (!(o instanceof AbstractResponse)) {
                            cachedResponse = null;
                        }
                        cachedResponse = (AbstractResponse) o;
                        
                    } finally {
                        ois.close();
                    }
                } finally {
                    fis.close();
                }
                
            } catch (IOException e) {
                Log.e(this, "IOException during loading cache", e);
                cachedResponse = null;
                
            } catch (ClassNotFoundException e) {
                Log.e(this, "ClassNotFoundException during loading cache", e);
                cachedResponse = null;
                
            }
        }
        
        Log.v(this, "Having cache == " + (cachedResponse == null ? "null" : cachedResponse.toString()));
        this.callback.step(1, 7);
        
        // if the cache is that new it is extremely unlikely that something has changed
        // e.g. we're installing several RGs
        if ((cachedResponse != null) && (cacheFile.lastModified() + LOCAL_CACHE_ONLY_TIME > System.currentTimeMillis())) {
            Log.v(this, "Using fresh cache");
            this.callback.step(1, 1);
            return cachedResponse;
        }
        
        // send request
        AbstractRequest request = null;
        switch (comType) {
            case REQUEST_RESOURCE_GROUP_APK:
                request = new RequestResourceGroupPackage(requestString);
                if ((cachedResponse != null) && (cachedResponse instanceof ResourceGroupPackageResponse)) {
                    ResourceGroupPackageResponse rgpr = (ResourceGroupPackageResponse) cachedResponse;
                    request.setCacheHash(rgpr.getCacheHash());
                }
                break;
            
            case REQUEST_SEARCH_RESULTS:
                request = new RequestResourceGroups(
                        PMPApplication.getContext().getResources().getConfiguration().locale.getDisplayLanguage(),
                        requestString);
                if ((cachedResponse != null) && (cachedResponse instanceof ResourceGroupsResponse)) {
                    ResourceGroupsResponse rgr = (ResourceGroupsResponse) cachedResponse;
                    request.setCacheHash(rgr.getHash());
                }
                break;
        }
        
        if (request == null) {
            throw new ModelIntegrityError(Assert.format(Assert.ILLEGAL_NULL, "request", null));
        }
        
        this.callback.step(2, 7);
        
        // handle request, fetch response
        AbstractResponse response;
        try {
            response = handleRequest(request);
        } catch (IOException e) {
            Log.e(this, "IOException during " + request.getClass().getSimpleName(), e);
            return null;
        } catch (ClassNotFoundException e) {
            Log.e(this, "ClassNotFoundException during " + request.getClass().getSimpleName(), e);
            return null;
        }
        
        this.callback.step(6, 7);
        
        if (response instanceof CachedRequestResponse) {
            // okay to use cache     
            Log.v(this, "Received Cache-OK message, using cache");
            this.callback.step(6, 6);
            return cachedResponse;
            
        } else {
            // overwrite cache
            
            try {
                FileOutputStream fos = new FileOutputStream(cacheFile);
                try {
                    ObjectOutputStream oos = new ObjectOutputStream(fos);
                    try {
                        oos.writeObject(response);
                    } finally {
                        oos.close();
                    }
                } finally {
                    fos.close();
                }
            } catch (IOException e) {
                Log.e(this, "IOException during " + response.getClass().getSimpleName(), e);
            }
            
            Log.v(this, "New cache written");
            this.callback.step(7, 7);
            return response;
        }
        
    }
    
    
    @Override
    public LocalizedResourceGroup[] findResourceGroups(String searchPattern) {
        Assert.nonNull(searchPattern, ModelMisuseError.class, Assert.ILLEGAL_NULL, "searchPattern", searchPattern);
        
        // get response
        AbstractResponse response = getResponseFor(searchPattern, CommunicationType.REQUEST_SEARCH_RESULTS);
        if ((response == null) || !(response instanceof ResourceGroupsResponse)) {
            return null;
        }
        
        ResourceGroupsResponse rgr = (ResourceGroupsResponse) response;
        
        return rgr.getResourceGroups();
    }
    
    
    @Override
    public File downloadResourceGroup(String rgPackage) {
        Assert.nonNull(rgPackage, ModelMisuseError.class, Assert.ILLEGAL_NULL, "rgPackage", rgPackage);
        try {
            
            File tmp = new File(TEMPORARY_PATH + rgPackage + APK_STR);
            FileOutputStream fos = new FileOutputStream(tmp);
            try {
                // get response
                AbstractResponse response = getResponseFor(rgPackage, CommunicationType.REQUEST_RESOURCE_GROUP_APK);
                if ((response == null) || !(response instanceof ResourceGroupPackageResponse)) {
                    return null;
                }
                
                // copy file
                ResourceGroupPackageResponse rgpr = (ResourceGroupPackageResponse) response;
                InputStream is = rgpr.getResourceGroupInputStream();
                try {
                    byte[] buffer = new byte[BUFFER_SIZE];
                    
                    int read = -1;
                    do {
                        read = is.read(buffer, 0, BUFFER_SIZE);
                        if (read > -1) {
                            fos.write(buffer, 0, read);
                        }
                    } while (read > -1);
                    
                } finally {
                    is.close();
                }
            } finally {
                fos.close();
            }
            
            return tmp;
            
        } catch (IOException ioe) {
            Log.e(this, "IO exception during downloading RG " + rgPackage, ioe);
            return null;
        }
    }
    
    
    @Override
    public void setCallback(IServerDownloadCallback callback) {
        if (callback == null) {
            this.callback = NullServerDownloadCallback.instance;
        } else {
            this.callback = callback;
        }
    }
    
    
    @Override
    public void cleanCache() {
        for (File f : new File(TEMPORARY_PATH).listFiles()) {
            if (!f.delete()) {
                Log.w(this, "Could not clean cache: " + f.getName());
            }
        }
        
    }
    
    
    @Override
    public Date getFindResourceGroupsCacheDate(String searchPattern) {
        Assert.nonNull(searchPattern, ModelMisuseError.class, Assert.ILLEGAL_NULL, "searchPattern", searchPattern);
        
        String cacheHash = String.valueOf(searchPattern.hashCode());
        File cacheFile = new File(TEMPORARY_PATH + cacheHash);
        if (!cacheFile.exists()) {
            return new Date();
        } else {
            return new Date(cacheFile.lastModified());
        }
    }
    
    
    @Override
    public String storePresetSet(IPresetSet presetSet) {
        try {
            PresetSetSaveResponse rpss = ((PresetSetSaveResponse) handleRequest(new RequestPresetSetSave(presetSet)));
            return rpss.isSuccess() ? rpss.getId() : rpss.getMessage();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    
    @Override
    public IPresetSet loadPresetSet(String name) {
        Assert.nonNull(name, ModelMisuseError.class, Assert.ILLEGAL_NULL, "name", name);
        try {
            return (PresetSet) ((PresetSetLoadResponse) handleRequest(new RequestPresetSetLoad(name))).getPresetSet();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
