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
package de.unistuttgart.ipvs.pmp.model.context.location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.unistuttgart.ipvs.pmp.model.exception.InvalidConditionException;
import de.unistuttgart.ipvs.pmp.util.location.PMPGeoPoint;

/**
 * The parsed condition for a {@link LocationContext}.
 * 
 * @author Tobias Kuhn
 * 
 */
public class LocationContextCondition {
    
    /**
     * Earth radius in kilometers. Assumes earth is a sphere.
     */
    private static final double EARTH_RADIUS = 6371.0;
    
    private static Map<String, LocationContextCondition> cache = new HashMap<String, LocationContextCondition>();
    
    private static Pattern CONDITION_PATTERN = Pattern
            .compile("([0-9\\.]+);([0-9\\.]+);(1|0);(([0-9\\.]+~[0-9\\.]+--)+)");
    
    
    /**
     * Parses a {@link LocationContextCondition} from a string.
     * 
     * @param condition
     * @return
     */
    public static LocationContextCondition parse(String condition) throws InvalidConditionException {
        if (condition == null) {
            throw new InvalidConditionException("LocationContextCondition may not be null.");
        }
        LocationContextCondition result = cache.get(condition);
        
        if (result == null) {
            Matcher match = CONDITION_PATTERN.matcher(condition);
            if (!match.matches()) {
                throw new InvalidConditionException("LocationContextCondition was not formatted properly: " + condition);
            }
            
            List<PMPGeoPoint> poly = new ArrayList<PMPGeoPoint>();
            String[] coords = match.group(4).split("--");
            for (String coord2 : coords) {
                String[] coord = coord2.split("~");
                poly.add(new PMPGeoPoint(Double.valueOf(coord[0]), Double.valueOf(coord[1])));
            }
            
            result = new LocationContextCondition(Double.parseDouble(match.group(1)),
                    Double.parseDouble(match.group(2)), match.group(3).equals("1"), poly);
            cache.put(condition, result);
        }
        
        return result;
    }
    
    
    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof LocationContextCondition)) {
            return false;
        }
        
        LocationContextCondition lcc = (LocationContextCondition) o;
        return this.polygon.equals(lcc.polygon) && this.uncertainty == lcc.uncertainty
                && this.hysteresis == lcc.hysteresis && this.negate == lcc.negate && this.lastCheck == lcc.lastCheck;
    }
    
    
    @Override
    public int hashCode() {
        return this.polygon.hashCode() ^ Double.valueOf(this.uncertainty).hashCode()
                ^ Double.valueOf(this.hysteresis).hashCode() ^ Boolean.valueOf(this.negate).hashCode()
                ^ (Boolean.valueOf(this.negate).hashCode() << 8);
    }
    
    /**
     * The polygon that defines the area to select. The space which is selected is inside the order of the points. CW
     * mean the outside, CCW the inside.
     */
    private List<PMPGeoPoint> polygon;
    
    /**
     * How far you can possibly be outside of the polygon (in meters)
     */
    private double uncertainty;
    
    /**
     * State-bound hysteresis, i.e. if you're inside you need to go this far outside to toggle deactive, if you're
     * outside you need to go this far in to toggle active.
     * (in meters)
     */
    private double hysteresis;
    
    /**
     * Whether this means the inside of the polygon or the outside.
     */
    private boolean negate;
    
    /**
     * We save the result of the last check for this {@link LocationContextCondition} so that hysteresis is possible.
     * We can do this because the {@link LocationContextCondition} are actually cached (see static methods).
     */
    private boolean lastCheck;
    
    
    public LocationContextCondition(double uncertainty, double hysteresis, boolean negate, List<PMPGeoPoint> polygon) {
        if (hysteresis >= uncertainty) {
            throw new IllegalArgumentException("Hysteresis must not be equal or larger than uncertainty.");
        }
        if (polygon == null || polygon.size() == 0) {
            throw new IllegalArgumentException("Polygon must not be empty.");
        }
        if ((hysteresis < 0.0) || (uncertainty < 0.0)) {
            throw new IllegalArgumentException("Hysteresis and uncertainty must be positive values.");
        }
        if (uncertainty >= 2.0 * Math.PI * EARTH_RADIUS) {
            uncertainty = 2.0 * Math.PI * EARTH_RADIUS;
        }
        
        this.uncertainty = uncertainty;
        this.hysteresis = hysteresis;
        this.negate = negate;
        this.polygon = polygon;
        this.lastCheck = false;
    }
    
    
    @Override
    public String toString() {
        StringBuffer pointList = new StringBuffer();
        for (PMPGeoPoint lcgp : this.polygon) {
            pointList.append(lcgp.getLatitude());
            pointList.append("~");
            pointList.append(lcgp.getLongitude());
            pointList.append("--");
        }
        
        return String.format(Locale.ENGLISH, "%f;%f;%s;%s", this.uncertainty, this.hysteresis, this.negate ? "1" : "0",
                pointList.toString());
    }
    
    
    /**
     * Checks whether the condition is satisfied in the state
     * 
     * @param state
     * @return
     */
    public boolean satisfiedIn(LocationContextState state) {
        // change the desired uncertainty based on the last state and the hysteresis
        double uncertainty = this.uncertainty + (this.lastCheck ? this.hysteresis : -this.hysteresis);
        
        // we do this first because the point-in-polygon test might suffer problems
        // if the single point is too close to the polygon
        if (geoEllipseIntersectsPolygon(state, state.getAccuracy() + uncertainty)) {
            this.lastCheck = true;
            return true;
            
        } else if (pointInPolygon(state)) {
            this.lastCheck = true;
            return true;
        }
        
        this.lastCheck = false;
        return false;
    }
    
    
    /**
     * Tests whether a circle around p with diameter dist meters does intersect the polygon.
     * 
     * @param p
     * @param dist
     * @return
     * @deprecated I believe {@link #geoEllipseIntersectsPolygon(PMPGeoPoint, double)} works better, but I'm not sure
     *             yet.
     */
    @SuppressWarnings("unused")
    @Deprecated
    private boolean geoCircleIntersectsPolygon(PMPGeoPoint p, double dist) {
        
        // TODO this could be bogus, this is actually an ellipse-ray-intersection (for spheres)
        // as defined by p.getNorthDistance() and p.getEastDistance()
        
        // convert dist from km in degrees (this is an approximation)
        double distDeg = dist / (2.0 * 1000.0 * Math.PI * EARTH_RADIUS);
        if (distDeg > 180.0) {
            distDeg -= 180.0;
        }
        
        // for each line in the polygon
        for (int i = 0; i < this.polygon.size() - 1; i++) {
            double latOrig = this.polygon.get(i).getLatitude();
            double lonOrig = this.polygon.get(i).getLongitude();
            double latDir = this.polygon.get(i + 1).getLatitude() - latOrig;
            double lonDir = this.polygon.get(i + 1).getLongitude() - lonOrig;
            
            // |o + t*d| = dist^2 (for sphere in 0,0)
            // <o+td, o+td> = dist^2
            // o^2 + t^2d^2 + 2td = dist^2
            // t^2d^2 + 2otd + o^2 - dist^2 = 0
            
            // transform the ray into the MCS of p
            latOrig -= p.getLatitude();
            lonOrig -= p.getLongitude();
            
            double a = latDir * latDir + lonDir * lonDir;
            double b = 2.0 * (latOrig * latDir + lonOrig * lonDir);
            double c = latOrig * latOrig + lonOrig * lonOrig - distDeg * distDeg;
            
            double t[] = solveQE(a, b, c);
            for (double solution : t) {
                if ((solution >= 0.0) && (solution <= 1.0)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    
    /**
     * Tests whether a lat/lon correct ellipse around p with half axes dist meters does intersect the polygon.
     * 
     * @param p
     * @param dist
     * @return
     */
    private boolean geoEllipseIntersectsPolygon(PMPGeoPoint p, double dist) {
        
        // convert dist from km in degrees
        double latDistDeg = p.getNorthDistance(dist);
        double lonDistDeg = p.getEastDistance(dist);
        
        // for each line in the polygon
        for (int i = 0; i < this.polygon.size() - 1; i++) {
            double latOrig = this.polygon.get(i).getLatitude();
            double lonOrig = this.polygon.get(i).getLongitude();
            double latDir = this.polygon.get(i + 1).getLatitude() - latOrig;
            double lonDir = this.polygon.get(i + 1).getLongitude() - lonOrig;
            
            // transform the ray into the MCS of p
            latOrig -= p.getLatitude();
            lonOrig -= p.getLongitude();
            
            // o + t*d where t needs to be in [0;1] to be the polygon's edge
            // (centered in p)
            // x²/a² + y²/b² = 1 (ellipse)
            // p = (x,y):
            // t² ( dx²/a² + dy²/b²) + 2t (ox*dx/a² + oy*dy/b²) + (ox²/a² + oy²/b² - 1) = 0
            // note that x = Longitude and y = Latitude
            // thus a = lonDistDeg, b = latDistDeg
            
            double a = sqr(lonDir / lonDistDeg) + sqr(latDir / latDistDeg);
            double b = 2.0 * (((lonOrig * lonDir) / sqr(lonDistDeg)) + ((latOrig * latDir) / sqr(latDistDeg)));
            double c = sqr(lonOrig / lonDistDeg) + sqr(latOrig / latDistDeg) - 1.0;
            
            double[] t = solveQE(a, b, c);
            for (double solution : t) {
                if ((solution >= 0.0) && (solution <= 1.0)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    
    private double sqr(double arg) {
        return arg * arg;
    }
    
    
    /**
     * Solves a quadratic equation without introducing FP numeric subtraction loss of significance.
     * 
     * @param a
     * @param b
     * @param c
     * @return zero, one or two x so, that ax<sup>2</sup>+bx+c = 0, whereas the x are sorted by their value
     */
    private double[] solveQE(double a, double b, double c) {
        double det = b * b - 4 * a * c;
        
        if (det < 0) {
            // imaginary result
            return new double[0];
        }
        det = Math.sqrt(det);
        
        double q = -0.5 * (b + ((b >= 0) ? +1 : -1) * det);
        double x1 = q / a;
        double x2 = c / q;
        
        if (x1 < x2) {
            return new double[] { x1, x2 };
        } else if (x2 < x1) {
            return new double[] { x2, x1 };
        } else {
            return new double[] { x1 };
        }
    }
    
    
    /**
     * Tests point in polygon by using the even-odd crossing algorithm. May suffer problems if the point is too near to
     * the polygon.
     * 
     * @param p
     * @return
     */
    private boolean pointInPolygon(PMPGeoPoint p) {
        /*
         * imagine a ray cast from p in direction (1,1) for simplicity's sake
         */
        
        int intersections = this.negate ? 1 : 0;
        
        // for each line in the polygon
        for (int i = 0; i < this.polygon.size() - 1; i++) {
            double lat = this.polygon.get(i).getLatitude();
            double lon = this.polygon.get(i).getLongitude();
            double latD = this.polygon.get(i + 1).getLatitude() - lat;
            double lonD = this.polygon.get(i + 1).getLongitude() - lon;
            
            // o+seg*d = 0+1t
            
            // find the segment parameter, i.e. whether the intersection
            // is on the selected part of the line segment, i.e. seg in [0,1]
            double seg = (p.getLatitude() + lon - p.getLongitude() - lat) / (latD + lonD);
            if ((seg < 0) || (seg > 1)) {
                continue;
            }
            
            // find whether it intersects the ray or the line behind zero, i.e. t < 0
            double t = p.getLatitude() + seg * latD - lat;
            if (t > 0) {
                intersections++;
            }
            
        }
        return intersections % 2 == 1;
    }
    
    
    /*
     * Getters / Setters for view
     */
    
    public double getUncertainty() {
        return this.uncertainty;
    }
    
    
    public void setUncertainty(double uncertainty) {
        this.uncertainty = uncertainty;
    }
    
    
    public double getHysteresis() {
        return this.hysteresis;
    }
    
    
    public void setHysteresis(double hysteresis) {
        this.hysteresis = hysteresis;
    }
    
    
    public List<PMPGeoPoint> getPolygon() {
        return this.polygon;
    }
    
    
    public double[] getPolygonLatitudeArray() {
        double[] result = new double[this.polygon.size()];
        
        for (int i = 0; i < result.length; i++) {
            result[i] = this.polygon.get(i).getLatitude();
        }
        
        return result;
    }
    
    
    public double[] getPolygonLongitudeArray() {
        double[] result = new double[this.polygon.size()];
        
        for (int i = 0; i < result.length; i++) {
            result[i] = this.polygon.get(i).getLongitude();
        }
        
        return result;
    }
    
    
    public String toHumanReadable() {
        StringBuilder result = new StringBuilder();
        
        for (PMPGeoPoint point : this.polygon) {
            result.append(point.toString());
            result.append("; ");
        }
        
        result.append("Uncertainty ");
        result.append(this.uncertainty);
        result.append("m, ");
        result.append("Hysteresis ");
        result.append(this.hysteresis);
        result.append("m");
        
        return result.toString();
    }
    
    
    public boolean isNegated() {
        return this.negate;
    }
    
    
    public void setNegated(boolean negate) {
        this.negate = negate;
    }
    
}
