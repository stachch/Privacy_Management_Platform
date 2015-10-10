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
package de.unistuttgart.ipvs.pmp.model.context.time;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Class to store a specific time for {@link TimeContextCondition}.
 * 
 * @author Tobias Kuhn
 * 
 */
public class TimeContextTime implements Comparable<TimeContextTime> {
    
    public static final int SECONDS_PER_MINUTE = 60;
    public static final int MINUTES_PER_HOUR = 60;
    public static final int HOURS_PER_DAY = 24;
    public static final int SECONDS_PER_HOUR = SECONDS_PER_MINUTE * MINUTES_PER_HOUR;
    public static final int SECONDS_PER_DAY = SECONDS_PER_HOUR * HOURS_PER_DAY;
    
    private int hour, minute, second;
    
    
    public TimeContextTime(int hour, int minute, int second) {
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }
    
    
    public TimeContextTime(TimeContextTime timeContextTime) {
        this.hour = timeContextTime.hour;
        this.minute = timeContextTime.minute;
        this.second = timeContextTime.second;
    }
    
    
    public TimeContextTime() {
        Calendar cal = Calendar.getInstance();
        this.hour = cal.get(Calendar.HOUR_OF_DAY);
        this.minute = cal.get(Calendar.MINUTE);
        this.second = cal.get(Calendar.SECOND);
    }
    
    
    public int getHour() {
        return this.hour;
    }
    
    
    /**
     * Sets a lenient hour.
     * 
     * @param hour
     */
    public void setHour(int hour) {
        this.hour = hour % HOURS_PER_DAY;
    }
    
    
    public int getMinute() {
        return this.minute;
    }
    
    
    /**
     * Sets a lenient minute.
     * 
     * @param minute
     */
    public void setMinute(int minute) {
        int lenientHrs = minute / MINUTES_PER_HOUR;
        if (lenientHrs != 0) {
            setHour(getHour() + lenientHrs);
        }
        this.minute = minute % MINUTES_PER_HOUR;
    }
    
    
    public int getSecond() {
        return this.second;
    }
    
    
    /**
     * Sets a lenient second.
     * 
     * @param second
     */
    public void setSecond(int second) {
        int lenientMins = second / SECONDS_PER_MINUTE;
        if (lenientMins != 0) {
            setMinute(getMinute() + lenientMins);
        }
        this.second = second % SECONDS_PER_MINUTE;
    }
    
    
    /**
     * Converts this time from a specific {@link TimeZone} to another. <b>The result is only valid for <i>right
     * now</i>!</b> * (Or whatever <i>right now</i> is when calculating between TimeZones...) This is because daylight
     * saving times must be respected.
     * 
     * @param timeZone
     */
    public void convertTimeZone(TimeZone from, TimeZone to) {
        long now = System.currentTimeMillis();
        int secDiff = (to.getOffset(now) - from.getOffset(now)) / 1000;
        setSecond(getSecond() + secDiff);
    }
    
    
    /**
     * Calculates the difference in seconds from <code>this</code> to <code>to</code>.
     * 
     * @param to
     * @param assumeNextDayIfWrap
     *            if set to true and <code>to</code> is earlier than <code>this</code>, it is assumed that
     *            <code>to</code> lies on the next day
     * @return
     */
    public int getDifferenceInSeconds(TimeContextTime to, boolean assumeNextDayIfWrap) {
        if (assumeNextDayIfWrap && (compareTo(to) > 0)) {
            return SECONDS_PER_DAY - to.getDifferenceInSeconds(this, false);
        } else {
            return (to.second - this.second) + (to.minute - this.minute) * SECONDS_PER_MINUTE + (to.hour - this.hour)
                    * SECONDS_PER_HOUR;
        }
        
    }
    
    
    @Override
    public boolean equals(Object o) {
        if ((o == null) || (!(o instanceof TimeContextTime))) {
            return false;
        }
        TimeContextTime another = (TimeContextTime) o;
        return (this.second == another.second) && (this.minute == another.minute) && (this.hour == another.hour);
    }
    
    
    @Override
    public int hashCode() {
        return this.second + SECONDS_PER_MINUTE * this.minute + SECONDS_PER_HOUR * this.hour;
    }
    
    
    @Override
    public int compareTo(TimeContextTime another) {
        return (this.second - another.second) + (this.minute - another.minute) * SECONDS_PER_MINUTE
                + (this.hour - another.hour) * SECONDS_PER_HOUR;
    }
    
    
    @Override
    public String toString() {
        return String.format("%02d:%02d:%02d", this.hour, this.minute, this.second);
    }
}
