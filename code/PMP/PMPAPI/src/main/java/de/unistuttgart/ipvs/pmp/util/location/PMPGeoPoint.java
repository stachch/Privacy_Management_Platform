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
package de.unistuttgart.ipvs.pmp.util.location;

import com.google.android.maps.GeoPoint;

/**
 * A universally usable geo point.
 * 
 * @author Tobias Kuhn
 * 
 */
public class PMPGeoPoint {
    
    private double latitude, longitude;
    
    private static final double WGS84_SEMI_MAJOR_AXIS = 6378137.0;
    private static final double WGS84_SEMI_MINOR_AXIS = 6356752.314;
    
    // TODO use elipses?
    // Vincenty's formulae
    // does not seem to be easy though
    // Sample Lengths Nashville to Los Angeles: Sphere - 2887.3 km <-> WGS84 - 2892.8 km
    
    private static final double APPROX_SPHERE_RADIUS = (2.0 * WGS84_SEMI_MAJOR_AXIS + WGS84_SEMI_MINOR_AXIS) / 3.0;
    
    private static final double TWOPI = Math.PI * 2.0;
    private static final double PIDIV2 = Math.PI / 2.0;
    private static final double DEG_TO_RAD = TWOPI / 360.0;
    private static final double RAD_TO_DEG = 360.0 / TWOPI;
    
    
    /*
     * Constructor, setter, getter,...
     */
    
    public PMPGeoPoint(double latitude, double longitude) {
        setLatitude(latitude);
        setLongitude(longitude);
    }
    
    
    public double getLatitude() {
        return this.latitude;
    }
    
    
    public void setLatitude(double latitude) {
        if ((latitude < -90.0) || (latitude > +90.0)) {
            throw new IllegalArgumentException("-90.0 <= Latitude <= +90.0");
        }
        this.latitude = latitude;
    }
    
    
    public double getLongitude() {
        return this.longitude;
    }
    
    
    public void setLongitude(double longitude) {
        if ((longitude < -180.0) || (longitude > 180.0)) {
            throw new IllegalArgumentException("-180.0 <= Longitude <= 180.0");
        }
        this.longitude = longitude;
    }
    
    
    public void setPoint(double latitude, double longitude) {
        setLatitude(latitude);
        setLongitude(longitude);
    }
    
    
    public double[] getPointArray() {
        return new double[] { getLatitude(), getLongitude() };
    }
    
    
    public GeoPoint getGoogleGeoPoint() {
        return new GeoPoint((int) (1E6 * getLatitude()), (int) (1E6 * getLongitude()));
    }
    
    
    /*
     * Geometrical stuff
     */
    
    /**
     * @param to
     * @return the distance between this and to in meter
     */
    public double getDistance(PMPGeoPoint to) {
        double lat1 = getLatitude() * DEG_TO_RAD;
        double lat2 = to.getLatitude() * DEG_TO_RAD;
        double lon1 = getLongitude() * DEG_TO_RAD;
        double lon2 = to.getLongitude() * DEG_TO_RAD;
        
        // spherical law of cosines
        double centralAngle = Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon2 - lon1);
        return APPROX_SPHERE_RADIUS * Math.acos(centralAngle);
    }
    
    
    /**
     * @param meter
     *            the distance in meters
     * @return the distance in degrees latitude in northern direction for this position
     */
    public double getNorthDistance(double meter) {
        return inDistance(meter, 0.0).getLatitude() - getLatitude();
    }
    
    
    /**
     * @param meter
     *            the distance in meters
     * @return the distance in degrees latitude in southern direction for this position
     */
    public double getSouthDistance(double meter) {
        return inDistance(-meter, 0.0).getLatitude() - getLatitude();
    }
    
    
    /**
     * @param meter
     *            the distance in meters
     * @return the distance in degrees longitude in western direction for this position
     */
    public double getWestDistance(double meter) {
        return inDistance(0.0, -meter).getLongitude() - getLongitude();
    }
    
    
    /**
     * @param meter
     *            the distance in meters
     * @return the distance in degrees longitude in eastern direction for this position
     */
    public double getEastDistance(double meter) {
        return inDistance(0.0, meter).getLongitude() - getLongitude();
    }
    
    
    /**
     * 
     * @param meterNorth
     *            the distance in northern direction
     * @param meterEast
     *            the distance in southern direction
     * @return the geo point in meter north distance to the north and meter east distance to the east
     */
    public PMPGeoPoint inDistance(double meterNorth, double meterEast) {
        if ((Math.abs(meterNorth) < 1E-3) && (Math.abs(meterEast) < 1E-3)) {
            throw new IllegalArgumentException("Distance must have a significant size, at least one must be > 1mm.");
        }
        double angle = Math.atan2(meterEast, meterNorth);
        if (angle < 0.0) {
            angle = TWOPI + angle;
        }
        double radius = Math.sqrt(meterNorth * meterNorth + meterEast * meterEast);
        return inDistanceAngle(radius, angle);
    }
    
    
    /**
     * 
     * @param meter
     *            the distance in meters
     * @param angle
     *            the angle in compass radians(!)
     * @return the geo point in angle direction and meter distance
     */
    public PMPGeoPoint inDistanceAngle(double meter, double angle) {
        meter %= TWOPI * APPROX_SPHERE_RADIUS;
        
        double deltaN = Math.cos(angle) * meter;
        double deltaE = Math.sin(angle) * meter;
        
        // dN / U = angle / 2pi
        double angleDeltaN = TWOPI * (deltaN / APPROX_SPHERE_RADIUS);
        // in latitude, the circle is only cos(lat) * r long 
        double angleDeltaE = TWOPI * deltaE / (Math.cos(getLatitude() * DEG_TO_RAD) * APPROX_SPHERE_RADIUS);
        
        double lonRad = (getLongitude() * DEG_TO_RAD + angleDeltaE);
        while (lonRad > Math.PI) {
            lonRad -= TWOPI;
        }
        while (lonRad < -Math.PI) {
            lonRad += TWOPI;
        }
        
        double latRad = getLatitude() * DEG_TO_RAD + angleDeltaN;
        // if the latitude operation moves the geopoint to the other side of the earth
        if (latRad > PIDIV2) {
            latRad = Math.PI - latRad;
            lonRad = (lonRad + Math.PI) % TWOPI;
        } else if (latRad < -PIDIV2) {
            latRad = -Math.PI - latRad;
            lonRad = (lonRad + Math.PI) % TWOPI;
        }
        
        return new PMPGeoPoint(latRad * RAD_TO_DEG, lonRad * RAD_TO_DEG);
    }
    
    
    /**
     * 
     * @param meter
     * @return a random point from this point in maximum meter distance, i.e. in the circle with radius meters
     */
    public PMPGeoPoint smear(double meter) {
        return inDistanceAngle(meter * Math.random(), 2.0 * Math.PI * Math.random());
    }
    
    
    @Override
    public String toString() {
        return String.format("%.6f %s, %.6f %s", Math.abs(this.latitude), this.latitude > 0 ? "N" : "S",
                Math.abs(this.longitude), this.longitude > 0 ? "E" : "W");
    }
    
    
    @Override
    public int hashCode() {
        return Double.valueOf(this.latitude).hashCode() ^ Double.valueOf(this.longitude).hashCode();
    }
    
    
    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof PMPGeoPoint)) {
            return false;
        }
        PMPGeoPoint pgp = (PMPGeoPoint) o;
        return pgp.latitude == this.latitude && pgp.longitude == this.longitude;
    }
    
}
