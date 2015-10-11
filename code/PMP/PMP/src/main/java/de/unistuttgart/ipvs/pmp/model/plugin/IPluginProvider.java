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

import java.io.InputStream;

import android.graphics.drawable.Drawable;
import de.unistuttgart.ipvs.pmp.model.exception.InvalidPluginException;
import de.unistuttgart.ipvs.pmp.shared.resource.ResourceGroup;
import de.unistuttgart.ipvs.pmp.xmlutil.rgis.IRGIS;

/**
 * Provider for managing all {@link ResourceGroup} plugins in PMP.
 * 
 * @author Tobias Kuhn
 * 
 */
public interface IPluginProvider {
    
    /**
     * Injects a random apk from an {@link InputStream}. Does not install it.
     * 
     * @param rgPackage
     *            the main package of the resource group's apk
     * @param input
     *            the input apk file
     */
    public abstract void injectFile(String rgPackage, InputStream input);
    
    
    /**
     * Installs a specific resource group.
     * 
     * @param rgPackage
     *            the main package of the resource group's apk
     * @throws InvalidPluginException
     *             if the supplied plugin is somehow corrupt
     */
    public abstract void install(String rgPackage) throws InvalidPluginException;
    
    
    /**
     * Uninstalls, i.e. removes all data associated with the identified resource group.
     * 
     * @param rgPackage
     *            the main package of the resource group's apk
     */
    public abstract void uninstall(String rgPackage);
    
    
    /**
     * Looks for the object that is running for this resource group.
     * 
     * @param rgPackage
     *            the main package of the resource group's apk
     * @return the one and only instance of the identified resource group in PMP
     * @throws InvalidPluginException
     *             if the supplied plugin is somehow corrupt
     */
    public abstract ResourceGroup getResourceGroupObject(String rgPackage) throws InvalidPluginException;
    
    
    /**
     * Looks for the cached RGIS of the resource group.
     * 
     * @param rgPackage
     *            the main package of the resource group's apk
     * @return the XML stream for the specified resource group or null if it wasn't found which should not happen
     * @throws InvalidPluginException
     *             if the supplied plugin is somehow corrupt
     */
    public abstract IRGIS getRGIS(String rgPackage) throws InvalidPluginException;
    
    
    /**
     * Looks for the cached icon of terhe resource group.
     * 
     * @param rgPackage
     *            the main package of the resource group's apk
     * @return the icon for the specified resource group or null if it wasn't found which should not happen
     * @throws InvalidPluginException
     *             if the supplied plugin is somehow corrupt
     */
    public abstract Drawable getIcon(String rgPackage) throws InvalidPluginException;
    
    
    /**
     * Looks for the cached revision of the resource group.
     * 
     * @param rgPackage
     *            the main package of the resource group's apk
     * @return the revision for the specified resource group
     * @throws InvalidPluginException
     *             if the supplied plugin is somehow corrupt
     */
    public abstract long getRevision(String rgPackage) throws InvalidPluginException;
    
}
