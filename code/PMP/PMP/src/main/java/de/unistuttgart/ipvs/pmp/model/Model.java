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
package de.unistuttgart.ipvs.pmp.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.logging.Level;

import android.content.pm.PackageManager.NameNotFoundException;
import android.os.IBinder;
import android.os.RemoteException;
import de.unistuttgart.ipvs.pmp.Constants;
import de.unistuttgart.ipvs.pmp.Log;
import de.unistuttgart.ipvs.pmp.PMPApplication;
import de.unistuttgart.ipvs.pmp.api.ipc.IPCConnection;
import de.unistuttgart.ipvs.pmp.model.assertion.Assert;
import de.unistuttgart.ipvs.pmp.model.assertion.ModelIntegrityError;
import de.unistuttgart.ipvs.pmp.model.assertion.ModelMisuseError;
import de.unistuttgart.ipvs.pmp.model.context.IContext;
import de.unistuttgart.ipvs.pmp.model.element.ElementPersistenceProvider;
import de.unistuttgart.ipvs.pmp.model.element.IModelElement;
import de.unistuttgart.ipvs.pmp.model.element.ModelElement;
import de.unistuttgart.ipvs.pmp.model.element.app.App;
import de.unistuttgart.ipvs.pmp.model.element.app.AppPersistenceProvider;
import de.unistuttgart.ipvs.pmp.model.element.app.IApp;
import de.unistuttgart.ipvs.pmp.model.element.contextannotation.IContextAnnotation;
import de.unistuttgart.ipvs.pmp.model.element.preset.IPreset;
import de.unistuttgart.ipvs.pmp.model.element.preset.Preset;
import de.unistuttgart.ipvs.pmp.model.element.preset.PresetPersistenceProvider;
import de.unistuttgart.ipvs.pmp.model.element.privacysetting.PrivacySetting;
import de.unistuttgart.ipvs.pmp.model.element.privacysetting.PrivacySettingPersistenceProvider;
import de.unistuttgart.ipvs.pmp.model.element.resourcegroup.IResourceGroup;
import de.unistuttgart.ipvs.pmp.model.element.resourcegroup.ResourceGroup;
import de.unistuttgart.ipvs.pmp.model.element.resourcegroup.ResourceGroupPersistenceProvider;
import de.unistuttgart.ipvs.pmp.model.element.servicefeature.ServiceFeature;
import de.unistuttgart.ipvs.pmp.model.element.servicefeature.ServiceFeaturePersistenceProvider;
import de.unistuttgart.ipvs.pmp.model.exception.InvalidPluginException;
import de.unistuttgart.ipvs.pmp.model.exception.InvalidXMLException;
import de.unistuttgart.ipvs.pmp.model.exception.PluginNotFoundException;
import de.unistuttgart.ipvs.pmp.model.ipc.IPCProvider;
import de.unistuttgart.ipvs.pmp.model.plugin.PluginProvider;
import de.unistuttgart.ipvs.pmp.model.server.ServerProvider;
import de.unistuttgart.ipvs.pmp.resource.Resource;
import de.unistuttgart.ipvs.pmp.resource.privacysetting.AbstractPrivacySetting;
import de.unistuttgart.ipvs.pmp.service.pmp.RegistrationResult;
import de.unistuttgart.ipvs.pmp.util.FileLog;
import de.unistuttgart.ipvs.pmp.xmlutil.XMLUtilityProxy;
import de.unistuttgart.ipvs.pmp.xmlutil.ais.IAIS;
import de.unistuttgart.ipvs.pmp.xmlutil.ais.IAISRequiredResourceGroup;
import de.unistuttgart.ipvs.pmp.xmlutil.ais.IAISServiceFeature;
import de.unistuttgart.ipvs.pmp.xmlutil.parser.common.ParserException;
import de.unistuttgart.ipvs.pmp.xmlutil.rgis.IRGIS;
import de.unistuttgart.ipvs.pmp.xmlutil.rgis.IRGISPrivacySetting;
import de.unistuttgart.ipvs.pmp.xmlutil.validator.issue.IIssue;

/**
 * <p>
 * The business logic a.k.a. the domain object model that stores all the {@link App}s, {@link Preset}s,
 * {@link PrivacySetting}s, {@link ResourceGroup}s and {@link ServiceFeature}s in use.
 * </p>
 * 
 * <p>
 * Internally, it automatically creates a {@link ModelCache}, the raw structure without data filled in yet, the
 * {@link ModelElement}s then load the required data on-demand by calling their associated
 * {@link ElementPersistenceProvider} and act as a cache data structure. They also save the data back when necessary.
 * </p>
 * 
 * <p>
 * More internally, the {@link ModelCache} object is maintained by the {@link PersistenceProvider} and its descendants.
 * The model gets this cache by observing the main singleton {@link PersistenceProvider}.
 * </p>
 * 
 * @author Tobias Kuhn
 * 
 */
public class Model implements IModel, Observer {
    
    /**
     * Actual model content. May be null if no cache is present.
     */
    private ModelCache cache;
    
    /**
     * Security-mechanism against using the same dex class loader twice for the same dexs, producing a SIGBUS.
     * Do not persist this, a restart should clear the list since it fixes the issue.
     */
    private Set<String> unallowedInstall;
    
    /**
     * Singleton stuff
     */
    private static final Model instance = new Model();
    
    
    private Model() {
        this.cache = null;
        this.unallowedInstall = new HashSet<String>();
        PersistenceProvider.getInstance().addObserver(this);
    }
    
    
    public static IModel getInstance() {
        return instance;
    }
    
    
    /**
     * Checks whether the model is already cached.If not, loads the data from the persistence and thus creates the
     * whole model data structures.
     */
    private void checkCached() {
        if (this.cache == null) {
            PersistenceProvider.getInstance().reloadDatabaseConnection();
        }
    }
    
    
    @Override
    public void update(Observable observable, Object data) {
        // new ModelCache from the PersistenceProvider
        this.cache = (ModelCache) data;
    }
    
    
    /*
     * Actual overridden content
     */
    
    @Override
    public List<IApp> getApps() {
        checkCached();
        return new ArrayList<IApp>(this.cache.getApps().values());
    }
    
    
    @Override
    public IApp getApp(String appPackage) {
        checkCached();
        Assert.nonNull(appPackage, ModelMisuseError.class, Assert.ILLEGAL_NULL, "appPackage", appPackage);
        
        return this.cache.getApps().get(appPackage);
    }
    
    
    @Override
    public RegistrationResult registerApp(final String appPackage) {
        checkCached();
        Assert.nonNull(appPackage, ModelMisuseError.class, Assert.ILLEGAL_NULL, "appPackage", appPackage);
        Assert.isNull(getApp(appPackage), ModelMisuseError.class, Assert.ILLEGAL_ALREADY_INSTALLED, "appPackage",
                appPackage);
        
        // check XML
        try {
            InputStream xmlStream = PMPApplication.getContext().getPackageManager()
                    .getResourcesForApplication(appPackage).getAssets().open(PersistenceConstants.APP_XML_NAME);
            
            IAIS ais = XMLUtilityProxy.getAppUtil().parse(xmlStream);
            
            List<IIssue> validation = XMLUtilityProxy.getAppUtil().getValidator().validateAIS(ais, false);
            if (validation.size() > 0) {
                /* error during XML validation */
                FileLog.get().logWithForward(this, null, FileLog.GRANULARITY_COMPONENT_CHANGES, Level.WARNING,
                        "App '%s' has failed registration with PMP: XML file contains errors.", appPackage);
                
                StringBuilder sb = new StringBuilder();
                boolean notFirst = false;
                for (IIssue issue : validation) {
                    if (notFirst) {
                        sb.append(", ");
                    }
                    notFirst = true;
                    
                    sb.append(issue.toString());
                }
                
                return new RegistrationResult(false, sb.toString());
            }
            
            // check service availability
            IPCConnection ipcc = new IPCConnection(PMPApplication.getContext());
            try {
                ipcc.setDestinationService(appPackage);
                if (ipcc.getBinder() == null) {
                    /* error during connecting to service */
                    FileLog.get().logWithForward(this, null, FileLog.GRANULARITY_COMPONENT_CHANGES, Level.WARNING,
                            "App '%s' has failed registration with PMP: Service not available.", appPackage);
                    return new RegistrationResult(false, "Service not available.");
                }
            } finally {
                ipcc.disconnect();
            }
            
            // verify RG revision availability
            for (IAISServiceFeature aissf : ais.getServiceFeatures()) {
                for (IAISRequiredResourceGroup aisrrg : aissf.getRequiredResourceGroups()) {
                    IResourceGroup rg = getResourceGroup(aisrrg.getIdentifier());
                    
                    if ((rg != null) && (rg.getRevision() < new Long(aisrrg.getMinRevision()))) {
                        /* error during resource group request */
                        FileLog.get()
                                .logWithForward(
                                        this,
                                        null,
                                        FileLog.GRANULARITY_COMPONENT_CHANGES,
                                        Level.WARNING,
                                        "App '%s' has failed registration with PMP: Requests newer ResourceGroups than installed.",
                                        appPackage);
                        return new RegistrationResult(false, "Requesting newer ResourceGroups not supported.");
                    }
                    
                }
            }
            
            // apply new app to DB, then model
            App newApp = new AppPersistenceProvider(null).createElementData(appPackage);
            Assert.nonNull(newApp, ModelIntegrityError.class, Assert.ILLEGAL_NULL, "newApp", newApp);
            this.cache.getApps().put(appPackage, newApp);
            this.cache.getServiceFeatures().put(newApp, new HashMap<String, ServiceFeature>());
            
            // apply new SF to DB, then model
            for (IAISServiceFeature sf : ais.getServiceFeatures()) {
                ServiceFeature newSF = new ServiceFeaturePersistenceProvider(null).createElementData(newApp,
                        sf.getIdentifier(), sf.getRequiredResourceGroups());
                Assert.nonNull(newSF, ModelIntegrityError.class, Assert.ILLEGAL_NULL, "newSF", newSF);
                this.cache.getServiceFeatures().get(newApp).put(sf.getIdentifier(), newSF);
            }
            
            // remember that illegal presets have to be enabled once their missing apps get installed
            IPCProvider.getInstance().startUpdate();
            try {
                for (Preset p : this.cache.getAllPresets()) {
                    if (!p.isAvailable() && !p.isDeleted()) {
                        p.forceRecache();
                        
                        // if the preset was only missing this app, rollout the changes
                        if (p.isAvailable() && !p.isDeleted()) {
                            p.rollout();
                        }
                    }
                }
            } finally {
                IPCProvider.getInstance().endUpdate();
            }
            
            // "Hello thar, App!"
            FileLog.get().logWithForward(this, null, FileLog.GRANULARITY_COMPONENT_CHANGES, Level.CONFIG,
                    "App '%s' has successfully registered with PMP.", appPackage);
            return new RegistrationResult(true);
            
        } catch (final IOException ioe) {
            /* error during finding files */
            FileLog.get().logWithForward(this, ioe, FileLog.GRANULARITY_COMPONENT_CHANGES, Level.WARNING,
                    "App '%s' has failed registration with PMP: Could not find associated files.", appPackage);
            return new RegistrationResult(false, ioe.getMessage());
            
        } catch (final NameNotFoundException nnfe) {
            /* error during finding files */
            FileLog.get().logWithForward(this, nnfe, FileLog.GRANULARITY_COMPONENT_CHANGES, Level.WARNING,
                    "App '%s' has failed registration with PMP: Could not find associated files.", appPackage);
            return new RegistrationResult(false, nnfe.getMessage());
            
        } catch (final ParserException xmlpe) {
            /* error during XML validation */
            FileLog.get().logWithForward(this, xmlpe, FileLog.GRANULARITY_COMPONENT_CHANGES, Level.WARNING,
                    "App '%s' has failed registration with PMP: Could not verify XML file.", appPackage);
            return new RegistrationResult(false, xmlpe.getMessage());
        }
    }
    
    
    @Override
    public boolean unregisterApp(String appPackage) {
        checkCached();
        Assert.nonNull(appPackage, ModelMisuseError.class, Assert.ILLEGAL_NULL, "appPackage", appPackage);
        
        App app = this.cache.getApps().get(appPackage);
        if (app == null) {
            return false;
        } else {
            
            app.lightweightDelete();
            this.cache.getApps().remove(appPackage);
            
            IPCProvider.getInstance().startUpdate();
            try {
                // remember that presets have to be disabled once their required apps get uninstalled
                for (IPreset preset : app.getAssignedPresets()) {
                    // this time, there's no way but to cast (or run manually through all apps)                     
                    Assert.instanceOf(preset, Preset.class, ModelIntegrityError.class, Assert.ILLEGAL_CLASS, "preset",
                            preset);
                    Preset castPreset = (Preset) preset;
                    
                    // since these presets were assigned to the app they now are guaranteed not to be available.
                    if (!castPreset.isDeleted()) {
                        castPreset.forceRecache();
                        castPreset.rollout();
                    }
                    
                }
            } finally {
                IPCProvider.getInstance().endUpdate();
            }
            
            FileLog.get().logWithForward(this, null, FileLog.GRANULARITY_COMPONENT_CHANGES, Level.CONFIG,
                    "App '%s' was removed from PMP.", appPackage);
            return true;
        }
    }
    
    
    @Override
    public List<IResourceGroup> getResourceGroups() {
        checkCached();
        return new ArrayList<IResourceGroup>(this.cache.getResourceGroups().values());
    }
    
    
    @Override
    public IResourceGroup getResourceGroup(String rgPackage) {
        checkCached();
        Assert.nonNull(rgPackage, ModelMisuseError.class, Assert.ILLEGAL_NULL, "rgPackage", rgPackage);
        return this.cache.getResourceGroups().get(rgPackage);
    }
    
    
    @Override
    public boolean installResourceGroup(String rgPackage, boolean dontDownload) throws InvalidXMLException,
            InvalidPluginException {
        checkCached();
        Assert.nonNull(rgPackage, ModelMisuseError.class, Assert.ILLEGAL_NULL, "rgPackage", rgPackage);
        Assert.isNull(getResourceGroup(rgPackage), ModelMisuseError.class, Assert.ILLEGAL_ALREADY_INSTALLED,
                "rgPackage", rgPackage);
        if (this.unallowedInstall.contains(rgPackage)) {
            throw new ModelMisuseError(Assert.format(Assert.ILLEGAL_SIGBUS_INSTALL, "rgPackage", rgPackage));
        }
        
        try {
            
            if (!dontDownload) {
                // download the plugin
                File temp = ServerProvider.getInstance().downloadResourceGroup(rgPackage);
                Assert.nonNull(temp, PluginNotFoundException.class, Assert.ILLEGAL_PACKAGE, "rgPackage", rgPackage);
                
                // add it
                FileInputStream fis;
                try {
                    fis = new FileInputStream(temp);
                    try {
                        PluginProvider.getInstance().injectFile(rgPackage, fis);
                    } finally {
                        try {
                            fis.close();
                        } catch (IOException ioe) {
                            Log.e(this, "IO exception during install RG", ioe);
                        }
                    }
                } catch (FileNotFoundException fnfe) {
                    throw new ModelIntegrityError(Assert.format(Assert.ILLEGAL_MISSING_FILE, rgPackage, rgPackage));
                }
                
                // remove temporary file
                if (!temp.delete()) {
                    Log.e(this, "Could not delete temporary file: " + temp.getAbsolutePath());
                }
            }
            
            // install the plugin
            PluginProvider.getInstance().install(rgPackage);
            
            // get the RGIS
            IRGIS rgis = PluginProvider.getInstance().getRGIS(rgPackage);
            
            // check it is correct
            List<IIssue> validation = XMLUtilityProxy.getRGUtil().getValidator().validateRGIS(rgis, false);
            if (validation.size() > 0) {
                FileLog.get().logWithForward(this, null, FileLog.GRANULARITY_COMPONENT_CHANGES, Level.WARNING,
                        "ResourceGroup '%s' has failed registration with PMP: XML contains errors.", rgPackage);
                
                StringBuilder sb = new StringBuilder();
                boolean notFirst = false;
                for (IIssue issue : validation) {
                    if (notFirst) {
                        sb.append(", ");
                    }
                    notFirst = true;
                    
                    sb.append(issue.toString());
                }
                
                throw new InvalidXMLException(sb.toString());
            }
            
            // check it is valid
            de.unistuttgart.ipvs.pmp.resource.ResourceGroup rg = PluginProvider.getInstance().getResourceGroupObject(
                    rgPackage);
            // inconsistencies
            if (!rgPackage.equals(rgis.getIdentifier())) {
                FileLog.get().logWithForward(this, null, FileLog.GRANULARITY_COMPONENT_CHANGES, Level.WARNING,
                        "ResourceGroup '%s' has failed registration with PMP: XML inconsistent with PMP data.",
                        rgPackage);
                throw new InvalidXMLException("ResourceGroup package (parameter, XML)", rgPackage, rgis.getIdentifier());
            }
            if (!rgis.getIdentifier().equals(rg.getRgPackage())) {
                FileLog.get().logWithForward(this, null, FileLog.GRANULARITY_COMPONENT_CHANGES, Level.WARNING,
                        "ResourceGroup '%s' has failed registration with PMP: XML inconsistent with PMP data.",
                        rgPackage);
                throw new InvalidXMLException("ResourceGroup package (XML, object)", rgis.getIdentifier(),
                        rg.getRgPackage());
            }
            for (IRGISPrivacySetting ps : rgis.getPrivacySettings()) {
                // mode-ps
                if (ps.getIdentifier().equals(PersistenceConstants.MODE_PRIVACY_SETTING)) {
                    FileLog.get().logWithForward(
                            this,
                            null,
                            FileLog.GRANULARITY_COMPONENT_CHANGES,
                            Level.WARNING,
                            "ResourceGroup '%s' has failed registration with PMP:"
                                    + " XML must not contain Privacy Setting 'Mode'.", rgPackage);
                    throw new InvalidXMLException("XML must not contain Privacy Setting 'Mode'.");
                }
                
                // does it already exist in the model?
                AbstractPrivacySetting<?> aps = rg.getPrivacySetting(ps.getIdentifier());
                if (aps == null) {
                    FileLog.get().logWithForward(this, null, FileLog.GRANULARITY_COMPONENT_CHANGES, Level.WARNING,
                            "ResourceGroup '%s' has failed registration with PMP: XML inconsistent with PMP data.",
                            rgPackage);
                    throw new InvalidXMLException("PrivacySetting (XML, objects) incosistency: " + ps.getIdentifier()
                            + "defined in the XML, but not found in the Java ResourceGroup object.");
                }
            }
            // check they implemented the resources correct
            for (String res : rg.getResources()) {
                Resource r = rg.getResource(res);
                
                IBinder nb = r.getAndroidInterface(Constants.PMP_IDENTIFIER);
                IBinder mb = r.getMockedAndroidInterface(Constants.PMP_IDENTIFIER);
                IBinder cb = r.getCloakedAndroidInterface(Constants.PMP_IDENTIFIER);
                
                // let's check everybody's here.
                if (nb == null || mb == null || cb == null) {
                    FileLog.get()
                            .logWithForward(
                                    this,
                                    null,
                                    FileLog.GRANULARITY_COMPONENT_CHANGES,
                                    Level.WARNING,
                                    "ResourceGroup '%s' has failed registration with PMP: Resource '%s' does not provide all IBinders.",
                                    rgPackage, res);
                    throw new InvalidPluginException("Resource '" + res + "' does not provide all IBinders.");
                }
                
                // disallow anonymous classes
                if (nb.getClass().isAnonymousClass() || mb.getClass().isAnonymousClass()
                        || cb.getClass().isAnonymousClass()) {
                    FileLog.get()
                            .logWithForward(
                                    this,
                                    null,
                                    FileLog.GRANULARITY_COMPONENT_CHANGES,
                                    Level.WARNING,
                                    "ResourceGroup '%s' has failed registration with PMP: Resource '%s' does provide illegal anonymous IBinders.",
                                    rgPackage, res);
                    throw new InvalidPluginException("Resource '" + res + "' does provide illegal anonymous IBinders.");
                }
                
                // assert that normal, mocking and cloaking are really DIFFERENT classes
                if (nb.getClass().isAssignableFrom(mb.getClass()) || mb.getClass().isAssignableFrom(nb.getClass())) {
                    FileLog.get()
                            .logWithForward(
                                    this,
                                    null,
                                    FileLog.GRANULARITY_COMPONENT_CHANGES,
                                    Level.WARNING,
                                    "ResourceGroup '%s' has failed registration with PMP:"
                                            + " Resource '%s' may not provide normal and mocked IBinders which are subtypes of each other.",
                                    rgPackage, res);
                    throw new InvalidPluginException("Resource '" + res
                            + "' may not provide normal and mocked IBinders which are subtypes of each other.");
                }
                if (nb.getClass().isAssignableFrom(cb.getClass()) || cb.getClass().isAssignableFrom(nb.getClass())) {
                    FileLog.get()
                            .logWithForward(
                                    this,
                                    null,
                                    FileLog.GRANULARITY_COMPONENT_CHANGES,
                                    Level.WARNING,
                                    "ResourceGroup '%s' has failed registration with PMP:"
                                            + " Resource '%s' may not provide normal and cloaked IBinders which are subtypes of each other.",
                                    rgPackage, res);
                    throw new InvalidPluginException("Resource '" + res
                            + "' may not provide normal and cloaked IBinders which are subtypes of each other.");
                }
                if (mb.getClass().isAssignableFrom(cb.getClass()) || cb.getClass().isAssignableFrom(mb.getClass())) {
                    FileLog.get()
                            .logWithForward(
                                    this,
                                    null,
                                    FileLog.GRANULARITY_COMPONENT_CHANGES,
                                    Level.WARNING,
                                    "ResourceGroup '%s' has failed registration with PMP:"
                                            + " Resource '%s' may not provide mocked and cloaked IBinders which are subtypes of each other.",
                                    rgPackage, res);
                    throw new InvalidPluginException("Resource '" + res
                            + "' may not provide mocked and cloaked IBinders which are subtypes of each other.");
                }
                
                // assert that they are still implementing the SAME interface
                try {
                    if (!nb.getInterfaceDescriptor().equals(mb.getInterfaceDescriptor())) {
                        FileLog.get()
                                .logWithForward(
                                        this,
                                        null,
                                        FileLog.GRANULARITY_COMPONENT_CHANGES,
                                        Level.WARNING,
                                        "ResourceGroup '%s' has failed registration with PMP:"
                                                + " Resource '%s' may not provide normal and mocked IBinders which do implement a different interface.",
                                        rgPackage, res);
                        throw new InvalidPluginException(
                                "Resource '"
                                        + res
                                        + "' may not provide normal and mocked IBinders which do implement a different interface.");
                    }
                    if (!nb.getInterfaceDescriptor().equals(cb.getInterfaceDescriptor())) {
                        FileLog.get()
                                .logWithForward(
                                        this,
                                        null,
                                        FileLog.GRANULARITY_COMPONENT_CHANGES,
                                        Level.WARNING,
                                        "ResourceGroup '%s' has failed registration with PMP:"
                                                + " Resource '%s' may not provide normal and cloaked IBinders which do implement a different interface.",
                                        rgPackage, res);
                        throw new InvalidPluginException(
                                "Resource '"
                                        + res
                                        + "' may not provide normal and cloaked IBinders which do implement a different interface.");
                    }
                    if (!mb.getInterfaceDescriptor().equals(cb.getInterfaceDescriptor())) {
                        FileLog.get()
                                .logWithForward(
                                        this,
                                        null,
                                        FileLog.GRANULARITY_COMPONENT_CHANGES,
                                        Level.WARNING,
                                        "ResourceGroup '%s' has failed registration with PMP:"
                                                + " Resource '%s' may not provide mocked and cloaked IBinders which do implement a different interface.",
                                        rgPackage, res);
                        throw new InvalidPluginException(
                                "Resource '"
                                        + res
                                        + "' may not provide mocked and cloaked IBinders which do implement a different interface.");
                    }
                    
                } catch (RemoteException re) {
                    FileLog.get()
                            .logWithForward(
                                    this,
                                    re,
                                    FileLog.GRANULARITY_COMPONENT_CHANGES,
                                    Level.WARNING,
                                    "ResourceGroup '%s' has failed registration with PMP:"
                                            + " Resource '%s' has thrown RemoteException during check of interface descriptors.",
                                    rgPackage, res);
                    throw new InvalidPluginException("Resource '" + res
                            + "' has thrown RemoteException during check of interface descriptors.");
                }
                
            }
            
            // apply new RG to DB, then model
            ResourceGroup newRG = new ResourceGroupPersistenceProvider(null).createElementData(rgPackage);
            Assert.nonNull(newRG, ModelIntegrityError.class, Assert.ILLEGAL_NULL, "newRG", newRG);
            this.cache.getResourceGroups().put(rgPackage, newRG);
            this.cache.getPrivacySettings().put(newRG, new HashMap<String, PrivacySetting>());
            
            // create the mock/cloak PS
            PrivacySetting modePS = new PrivacySettingPersistenceProvider(null).createElementData(newRG,
                    PersistenceConstants.MODE_PRIVACY_SETTING, false);
            this.cache.getPrivacySettings().get(newRG).put(PersistenceConstants.MODE_PRIVACY_SETTING, modePS);
            
            // apply new PS to DB, then model
            for (IRGISPrivacySetting ps : rgis.getPrivacySettings()) {
                PrivacySetting newPS = new PrivacySettingPersistenceProvider(null).createElementData(newRG,
                        ps.getIdentifier(), ps.isRequestable());
                Assert.nonNull(newPS, ModelIntegrityError.class, Assert.ILLEGAL_NULL, "newPS", newPS);
                this.cache.getPrivacySettings().get(newRG).put(ps.getIdentifier(), newPS);
            }
            
            IPCProvider.getInstance().startUpdate();
            try {
                // remember that illegal service features have to be enabled once their missing PS get installed
                for (App app : this.cache.getApps().values()) {
                    boolean appChanged = false;
                    
                    for (ServiceFeature sf : this.cache.getServiceFeatures().get(app).values()) {
                        if (!sf.isAvailable()) {
                            sf.forceRecache();
                            
                            // if the service feature was only missing this RG, rollout the changes
                            if (sf.isAvailable()) {
                                appChanged = true;
                            }
                        }
                    }
                    
                    if (appChanged) {
                        app.verifyServiceFeatures();
                    }
                }
                
                // remember that illegal presets have to be enabled once their missing PS get installed
                for (Preset p : this.cache.getAllPresets()) {
                    if (!p.isAvailable()) {
                        p.forceRecache();
                        
                        // if the preset was only missing this RG, rollout the changes
                        if (p.isAvailable()) {
                            p.rollout();
                        }
                    }
                }
                
            } finally {
                IPCProvider.getInstance().endUpdate();
            }
            
            FileLog.get().logWithForward(this, null, FileLog.GRANULARITY_COMPONENT_CHANGES, Level.CONFIG,
                    "ResourceGroup '%s' has been successfully installed.", rgPackage);
            return true;
        } catch (IncompatibleClassChangeError icce) {
            /* error due to invalid API */
            FileLog.get().logWithForward(this, icce, FileLog.GRANULARITY_COMPONENT_CHANGES, Level.WARNING,
                    "ResourceGroup '%s' has failed registration with PMP: Using an API that now is out of date.",
                    rgPackage);
            throw new InvalidPluginException("Using an API that now is out of date.", icce);
        } catch (LinkageError le) {
            /* error due to invalid class loading */
            FileLog.get().logWithForward(
                    this,
                    le,
                    FileLog.GRANULARITY_COMPONENT_CHANGES,
                    Level.WARNING,
                    "ResourceGroup '%s' has failed registration with PMP:"
                            + " An unexpected error occurred during linking the class files.", rgPackage);
            throw new InvalidPluginException("An unexpected error occurred during linking the class files.", le);
        } catch (ParserException xmlpe) {
            /* error during XML validation */
            FileLog.get().logWithForward(this, xmlpe, FileLog.GRANULARITY_COMPONENT_CHANGES, Level.WARNING,
                    "ResourceGroup '%s' has failed registration with PMP: Could not verify XML file.", rgPackage);
            throw new InvalidXMLException("Could not verify XML file.", xmlpe);
        }
    }
    
    
    @Override
    public boolean uninstallResourceGroup(String rgPackage) {
        checkCached();
        Assert.nonNull(rgPackage, ModelMisuseError.class, Assert.ILLEGAL_NULL, "rgPackage", rgPackage);
        
        ResourceGroup rg = this.cache.getResourceGroups().get(rgPackage);
        if (rg == null) {
            return false;
        } else {
            
            rg.delete();
            // delete the class files / apk / etc
            PluginProvider.getInstance().uninstall(rgPackage);
            this.unallowedInstall.add(rgPackage);
            this.cache.getResourceGroups().remove(rgPackage);
            
            IPCProvider.getInstance().startUpdate();
            try {
                // remember that service features have to be disabled once their required PS get uninstalled
                for (App app : this.cache.getApps().values()) {
                    boolean appChanged = false;
                    
                    for (ServiceFeature sf : this.cache.getServiceFeatures().get(app).values()) {
                        if (sf.isAvailable()) {
                            sf.forceRecache();
                            
                            // if the service feature will be missing this RG, rollout the changes
                            if (!sf.isAvailable()) {
                                appChanged = true;
                            }
                        }
                    }
                    
                    if (appChanged) {
                        app.verifyServiceFeatures();
                    }
                }
                
                // remember that presets have to be disabled once their required PS get uninstalled
                for (Preset preset : this.cache.getAllPresets()) {
                    if (preset.isAvailable() && !preset.isDeleted()) {
                        preset.forceRecache();
                        
                        // if the preset will be missing this RG, rollout the changes
                        if (!preset.isAvailable()) {
                            preset.rollout();
                        }
                    }
                }
            } finally {
                IPCProvider.getInstance().endUpdate();
            }
            
            FileLog.get().logWithForward(this, null, FileLog.GRANULARITY_COMPONENT_CHANGES, Level.CONFIG,
                    "ResourceGroup '%s' was removed from PMP.", rgPackage);
            return true;
        }
    }
    
    
    @Override
    public List<IPreset> getPresets() {
        checkCached();
        return new ArrayList<IPreset>(this.cache.getAllPresets());
    }
    
    
    @Override
    public List<IPreset> getPresets(ModelElement creator) {
        checkCached();
        Assert.isValidCreator(creator, ModelMisuseError.class, Assert.ILLEGAL_CREATOR, "creator", creator);
        
        Map<String, Preset> creatorPresets = this.cache.getPresets().get(creator);
        if (creatorPresets == null) {
            return new ArrayList<IPreset>();
        } else {
            return new ArrayList<IPreset>(creatorPresets.values());
        }
    }
    
    
    @Override
    public IPreset getPreset(IModelElement creator, String presetIdentifier) {
        checkCached();
        Assert.isValidCreator(creator, ModelMisuseError.class, Assert.ILLEGAL_CREATOR, "creator", creator);
        Assert.nonNull(presetIdentifier, ModelMisuseError.class, Assert.ILLEGAL_NULL, "identifier", presetIdentifier);
        
        Map<String, Preset> creatorPresets = this.cache.getPresets().get(creator);
        if (creatorPresets == null) {
            return null;
        } else {
            return creatorPresets.get(presetIdentifier);
        }
    }
    
    
    @Override
    public IPreset addPreset(IModelElement creator, String presetIdentifier, String name, String description) {
        checkCached();
        Assert.isValidCreator(creator, ModelMisuseError.class, Assert.ILLEGAL_CREATOR, "creator", creator);
        Assert.nonNull(presetIdentifier, ModelMisuseError.class, Assert.ILLEGAL_NULL, "identifier", presetIdentifier);
        Assert.nonNull(name, ModelMisuseError.class, Assert.ILLEGAL_NULL, "name", name);
        Assert.nonNull(description, ModelMisuseError.class, Assert.ILLEGAL_NULL, "description", description);
        Assert.isNull(getPreset(creator, presetIdentifier), ModelMisuseError.class, Assert.ILLEGAL_ALREADY_INSTALLED,
                "preset", (creator == null ? "null" : creator.toString()) + ", " + presetIdentifier);
        
        Preset newPreset = new PresetPersistenceProvider(null).createElementData(creator, presetIdentifier, name,
                description);
        Assert.nonNull(newPreset, ModelIntegrityError.class, Assert.ILLEGAL_NULL, "newPreset", newPreset);
        
        Map<String, Preset> creatorMap = this.cache.getPresets().get(creator);
        if (creatorMap == null) {
            creatorMap = new HashMap<String, Preset>();
            this.cache.getPresets().put(creator, creatorMap);
        }
        creatorMap.put(presetIdentifier, newPreset);
        return newPreset;
    }
    
    
    @Override
    public IPreset addUserPreset(String name, String description) {
        checkCached();
        Assert.nonNull(name, ModelMisuseError.class, Assert.ILLEGAL_NULL, "name", name);
        Assert.nonNull(description, ModelMisuseError.class, Assert.ILLEGAL_NULL, "description", description);
        
        // prepare standard
        Map<String, Preset> creatorMap = this.cache.getPresets().get(null);
        int suffix = 1;
        String identifier = name;
        
        // find free identifier
        if (creatorMap != null) {
            while (creatorMap.get(identifier) != null) {
                suffix++;
                identifier = name + suffix;
            }
        }
        
        // create
        return addPreset(null, identifier, name, description);
    }
    
    
    @Override
    public boolean removePreset(IModelElement creator, String presetIdentifier) {
        checkCached();
        Assert.nonNull(presetIdentifier, ModelMisuseError.class, Assert.ILLEGAL_NULL, "identifier", presetIdentifier);
        Assert.isValidCreator(creator, ModelMisuseError.class, Assert.ILLEGAL_CREATOR, "creator", creator);
        
        // does the creator map exist?
        Map<String, Preset> creatorMap = this.cache.getPresets().get(creator);
        if (creatorMap == null) {
            return false;
        } else {
            // does the preset exist?
            Preset p = creatorMap.get(presetIdentifier);
            
            if (p == null) {
                return false;
            } else {
                p.delete();
                
                // update model
                creatorMap.remove(presetIdentifier);
                
                // remove old CAs
                this.cache.getContextAnnotations().remove(p);
                
                IPCProvider.getInstance().startUpdate();
                try {
                    for (IApp app : p.getAssignedApps()) {
                        // this time, there's no way but to cast (or run manually through all apps)
                        Assert.instanceOf(app, App.class, ModelIntegrityError.class, Assert.ILLEGAL_CLASS, "app", app);
                        App castApp = (App) app;
                        castApp.removePreset(p);
                    }
                } finally {
                    IPCProvider.getInstance().endUpdate();
                }
                
                return true;
            }
        }
    }
    
    
    @Override
    public void clearAll() {
        PersistenceProvider.getInstance().getDoh().cleanTables();
        PersistenceProvider.getInstance().releaseCache();
    }
    
    
    @Override
    public List<IContext> getContexts() {
        checkCached();
        return new ArrayList<IContext>(this.cache.getContexts());
    }
    
    
    @Override
    public List<IContextAnnotation> getContextAnnotations() {
        checkCached();
        return new ArrayList<IContextAnnotation>(this.cache.getAllContextAnnotations());
    }
    
    
    @Override
    public List<IContextAnnotation> getContextAnnotations(IContext context) {
        checkCached();
        Assert.nonNull(context, ModelMisuseError.class, Assert.ILLEGAL_NULL, "context", context);
        
        List<IContextAnnotation> allCAs = this.cache.getAllContextAnnotations();
        List<IContextAnnotation> result = new ArrayList<IContextAnnotation>();
        for (IContextAnnotation ca : allCAs) {
            if (ca.getContext().equals(context)) {
                result.add(ca);
            }
        }
        
        return result;
    }
}
