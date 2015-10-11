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
package de.unistuttgart.ipvs.pmp.shared.service.pmp;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * A RegistrationResult representation for the registration at {@link IPMPServiceRegistration}.
 * 
 * @author Jakob Jarosch
 */
public class RegistrationResult implements Parcelable {
    
    private boolean success;
    private String message;
    
    
    /**
     * @see RegistrationResult#RegistrationState(boolean, String)
     */
    public RegistrationResult(boolean success) {
        this.success = success;
    }
    
    
    /**
     * Creates a new {@link RegistrationResult} object, with given values.
     * 
     * @param success
     *            The success of the registration, true means successful, false means registration failed.
     * @param message
     *            A message which describes what exactly is the reason for a failed registration.
     */
    public RegistrationResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    
    /**
     * @return the success of the registration, true means successful, false means registration failed.
     */
    public boolean getSuccess() {
        return this.success;
    }
    
    
    /**
     * @return a message which describes what exactly is the reason for a failed registration.
     */
    public String getMessage() {
        return this.message;
    }
    
    
    /**
     * Constructor for regenerating Java object of an parcel from this object. Normally called by
     * {@link Parcelable.Creator#createFromParcel(Parcel)} of the {@link RegistrationResult#CREATOR} variable.
     * 
     * @param source
     *            Parcel-Source
     */
    protected RegistrationResult(Parcel source) {
        boolean[] bools = new boolean[1];
        source.readBooleanArray(bools);
        this.success = bools[0];
        
        this.message = source.readString();
    }
    
    
    /**
     * {@link RegistrationResult#writeToParcel(Parcel, int)} is called when the App Object is sent through an
     * {@link IBinder}. Therefore all data of the object have to be written into the {@link Parcel}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeBooleanArray(new boolean[] { this.success });
        dest.writeString(this.message);
    }
    
    
    @Override
    public int describeContents() {
        return 0;
    }
    
    /**
     * Required Creator for the {@link Parcelable} regeneration.
     */
    public static final Parcelable.Creator<RegistrationResult> CREATOR = new Parcelable.Creator<RegistrationResult>() {
        
        @Override
        public RegistrationResult createFromParcel(Parcel source) {
            return new RegistrationResult(source);
        }
        
        
        @Override
        public RegistrationResult[] newArray(int size) {
            return new RegistrationResult[size];
        }
    };
}
