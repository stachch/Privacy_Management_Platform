/*
 * Copyright 2012 pmp-android development team
 * Project: PMP-API
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
package de.unistuttgart.ipvs.pmp.shared.api;

import de.unistuttgart.ipvs.pmp.shared.resource.Resource;

/**
 * A storage object for the identity of a {@link Resource} in PMP.
 * 
 * @author Tobias Kuhn
 *         
 */
public class PMPResourceIdentifier {
    
    private final String resourceGroup;
    private final String resource;
    
    
    private PMPResourceIdentifier(String resourceGroup, String resource) {
        this.resourceGroup = resourceGroup;
        this.resource = resource;
    }
    
    
    public String getResourceGroup() {
        return this.resourceGroup;
    }
    
    
    public String getResource() {
        return this.resource;
    }
    
    
    /**
     * Creates a new {@link PMPResourceIdentifier} identifying one specific resource throughout PMP. This is useful for
     * abbreviating calls to the {@link PMP} API. You can store the
     * returned object for as long as you want, but this is entirely optional.
     * 
     * @param resourceGroupPackage
     *            the package of the resource group you want to access
     * @param resource
     *            the identifier of the resource inside the resource group
     * @return an object that identifies the specific resource throughout PMP
     */
    public static PMPResourceIdentifier make(String resourceGroupPackage, String resource) {
        return new PMPResourceIdentifier(resourceGroupPackage, resource);
    }
}
