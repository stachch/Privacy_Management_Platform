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

import java.util.List;

import de.unistuttgart.ipvs.pmp.model.context.IContext;
import de.unistuttgart.ipvs.pmp.model.element.IModelElement;
import de.unistuttgart.ipvs.pmp.model.element.ModelElement;
import de.unistuttgart.ipvs.pmp.model.element.app.IApp;
import de.unistuttgart.ipvs.pmp.model.element.contextannotation.IContextAnnotation;
import de.unistuttgart.ipvs.pmp.model.element.preset.IPreset;
import de.unistuttgart.ipvs.pmp.model.element.resourcegroup.IResourceGroup;
import de.unistuttgart.ipvs.pmp.model.exception.InvalidPluginException;
import de.unistuttgart.ipvs.pmp.model.exception.InvalidXMLException;
import de.unistuttgart.ipvs.pmp.model.server.ServerProvider;
import de.unistuttgart.ipvs.pmp.service.pmp.RegistrationResult;

/**
 * The {@link IModel} provides all {@link IApp}s, {@link IPreset}s and {@link IResourceGroup}s known by PMP.
 * 
 * This interface is implemented by the {@link Model} class.
 * 
 * @author Jakob Jarosch
 */
public interface IModel {
    
    /**
     * @return all {@link IApp}s known by PMP.
     */
    public List<IApp> getApps();
    
    
    /**
     * Returns the corresponding {@link IApp} to an identifier of an {@link IApp}.
     * 
     * @param appPackage
     *            Corresponding {@link IApp} identifier
     * @return the requested {@link IApp} or null if the {@link IApp} does not exists in PMP.
     */
    public IApp getApp(String appPackage);
    
    
    /**
     * Registers a new {@link IApp} at PMP.
     * 
     * <p>
     * <b>This method is executed asynchronously so the termination of this method will not mean the {@link IApp}
     * registration has succeeded.</b>
     * </p>
     * 
     * @param appPackage
     *            The identifier for the {@link IApp} which should be registered.
     * @throws InvalidXMLException
     *             if the XML of the app is somehow corrupt
     */
    public RegistrationResult registerApp(String appPackage);
    
    
    /**
     * Removes the registration of a registered {@link IApp}. Does not touch the presets it was assigned to.
     * 
     * @param appPackage
     *            The identifier for the {@link IApp} which should be unregistered.
     * @return true, if and only if the app was found and removed
     */
    public boolean unregisterApp(String appPackage);
    
    
    /**
     * @return all {@link IResourceGroup}s known by PMP.
     */
    public List<IResourceGroup> getResourceGroups();
    
    
    /**
     * Returns the corresponding {@link IResourceGroup} to an identifier of a {@link IResourceGroup}.
     * 
     * @param rgPackage
     *            Corresponding {@link IResourceGroup} identifier
     * @return the requested {@link IResourceGroup} or null if the {@link IResourceGroup} does not exists in
     *         PMP.
     */
    public IResourceGroup getResourceGroup(String rgPackage);
    
    
    /**
     * Installs an arbitrary new {@link IResourceGroup} at PMP.
     * 
     * <p>
     * <b>This method will cause a network connection to the resource group server via the {@link ServerProvider}.</b>
     * </p>
     * 
     * @param rgPackage
     *            The identifier for the {@link IResourceGroup} which should be registered.
     * @param dontDownload
     *            If set to true, the installation will not try to open a connection to the server but assume the file
     *            is already present.
     * @return true, if the installation was successful, false if an error occurred
     * @throws InvalidXMLException
     *             if the XML of the supplied resource group is somehow corrupt
     * @throws InvalidPluginException
     *             if the supplied resource group is somehow corrupt
     */
    public boolean installResourceGroup(String rgPackage, boolean dontDownload) throws InvalidXMLException,
            InvalidPluginException;
    
    
    /**
     * Uninstalls an installed {@link IResourceGroup} at PMP.
     * 
     * @param rgPackage
     *            The identifier for the {@link IResourceGroup} which should be registered.
     * @return true, if the uninstallation was successful, false if an error occurred
     */
    public boolean uninstallResourceGroup(String rgPackage);
    
    
    /**
     * @return all {@link IPreset}s known by PMP.
     */
    public List<IPreset> getPresets();
    
    
    /**
     * @param creator
     *            null, if the user created this preset, the {@link IApp} or {@link IResourceGroup} if the
     *            {@link IPreset} is bundled.
     * @return all {@link IPreset}s which were created by creator or null, if none found
     */
    public List<IPreset> getPresets(ModelElement creator);
    
    
    /**
     * Returns a specific existing {@link IPreset}.
     * 
     * @param creator
     *            null, if the user created this preset, the {@link IApp} or {@link IResourceGroup} if the
     *            {@link IPreset} is bundled.
     * @param presetIdentifier
     *            a unique (for creator) identifier for this preset
     * @return the corresponding {@link IPreset} or null, if none found
     */
    public IPreset getPreset(IModelElement creator, String presetIdentifier);
    
    
    /**
     * Adds a new {@link IPreset} to PMP.
     * 
     * @param creator
     *            null, if the user created this preset, the {@link IApp} or {@link IResourceGroup} if the
     *            {@link IPreset} is bundled.
     * @param presetIdentifier
     *            a unique (for creator) identifier for this preset
     * @param name
     *            The name of the {@link IPreset}.
     * @param description
     *            The description of the {@link IPreset}.
     * @return the {@link IPreset} that was created
     */
    public IPreset addPreset(IModelElement creator, String presetIdentifier, String name, String description);
    
    
    /**
     * Convenience function to simply add a new preset created by a user. This function guarantees the preset will get
     * an identifier that is not yet taken, based on the name of the preset.
     * 
     * @param name
     *            The name of the {@link IPreset}.
     * @param description
     *            The description of the {@link IPreset}.
     * @return the {@link IPreset} that was created
     */
    public IPreset addUserPreset(String name, String description);
    
    
    /**
     * Removes an existing {@link IPreset} ultimately from PMP. This cannot be undone. Consider using
     * {@link IPreset#setDeleted(boolean)} alternatively.
     * 
     * @param creator
     *            null, if the user created this preset, the {@link IApp} or {@link IResourceGroup} if the
     *            {@link IPreset} is bundled.
     * @param presetIdentifier
     *            a unique identifier for this preset
     * @return true, if and only if the preset was found and removed
     */
    public boolean removePreset(IModelElement creator, String presetIdentifier);
    
    
    /**
     * 
     * @return all {@link IContext}s known by PMP.
     */
    public List<IContext> getContexts();
    
    
    /**
     * 
     * @return all {@link IContextAnnotation} known by PMP.
     */
    public List<IContextAnnotation> getContextAnnotations();
    
    
    /**
     * 
     * @param context
     *            the context that the resulting {@link IContextAnnotation}s must use
     * @return all {@link IContextAnnotation} using a specific context
     */
    public List<IContextAnnotation> getContextAnnotations(IContext context);
    
    
    /**
     * Will clear the complete model on both data and persistence layer.
     */
    public void clearAll();
    
}
