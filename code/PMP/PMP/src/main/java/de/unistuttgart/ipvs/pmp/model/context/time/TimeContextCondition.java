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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.unistuttgart.ipvs.pmp.model.exception.InvalidConditionException;

/**
 * The parsed condition for a {@link TimeContext}.
 * 
 * @author Tobias Kuhn
 * 
 */
public class TimeContextCondition {
    
    private static Map<String, TimeContextCondition> cache = new HashMap<String, TimeContextCondition>();
    
    private static Pattern CONDITION_PATTERN = Pattern
            .compile("((utc)?)([0-2][0-9]):([0-5][0-9]):([0-5][0-9])-([0-2][0-9]):([0-5][0-9]):([0-5][0-9])-(.)([0-9,]*)");
    
    
    /**
     * Parses a {@link TimeContextCondition} from a string.
     * 
     * @param condition
     * @return
     */
    public static TimeContextCondition parse(String condition) throws InvalidConditionException {
        if (condition == null) {
            throw new InvalidConditionException("TimeContextCondition may not be null.");
        }
        TimeContextCondition result = cache.get(condition);
        
        if (result == null) {
            Matcher match = CONDITION_PATTERN.matcher(condition);
            if (!match.matches()) {
                throw new InvalidConditionException("TimeContextCondition was not formatted properly: " + condition);
            }
            
            boolean utc = match.group(1).length() > 0;
            int beginHour = Integer.parseInt(match.group(3));
            int beginMin = Integer.parseInt(match.group(4));
            int beginSec = Integer.parseInt(match.group(5));
            int endHour = Integer.parseInt(match.group(6));
            int endMin = Integer.parseInt(match.group(7));
            int endSec = Integer.parseInt(match.group(8));
            TimeContextIntervalType tccit = TimeContextIntervalType.getForIdentifier(match.group(9).charAt(0));
            List<Integer> tccdList = tccit.makeDays(match.group(10));
            
            result = new TimeContextCondition(utc, new TimeContextTime(beginHour, beginMin, beginSec),
                    new TimeContextTime(endHour, endMin, endSec), tccit, tccdList);
            cache.put(condition, result);
        }
        
        return result;
    }
    
    /**
     * Whether the time is fixed at a point, i.e. e.g. 08:00 always at this time zone,
     * then the time is converted to UTC and the information is in UTC.
     * If this is false the time is always relative to the local time zone of the user.
     */
    private boolean isUTC;
    
    /**
     * Begin and end during a 24-hrs period. May wrap.
     */
    private TimeContextTime begin, end;
    
    /**
     * The interval to repeat the time, i.e. which days
     */
    private TimeContextIntervalType interval;
    
    /**
     * The specific days in the interval
     */
    private List<Integer> days;
    
    
    public TimeContextCondition(boolean isUTC, TimeContextTime begin, TimeContextTime end,
            TimeContextIntervalType interval, List<Integer> days) {
        this.isUTC = isUTC;
        this.begin = begin;
        this.end = end;
        this.interval = interval;
        this.days = days;
    }
    
    
    @Override
    public String toString() {
        return String.format("%s%02d:%02d:%02d-%02d:%02d:%02d-%s%s", this.isUTC ? "utc" : "", this.begin.getHour(),
                this.begin.getMinute(), this.begin.getSecond(), this.end.getHour(), this.end.getMinute(),
                this.end.getSecond(), this.interval.getIdentifier(), this.interval.makeList(this.days));
    }
    
    
    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof TimeContextCondition)) {
            return false;
        }
        
        TimeContextCondition tcc = (TimeContextCondition) o;
        
        return this.isUTC == tcc.isUTC && this.begin.equals(tcc.begin) && this.end.equals(tcc.end)
                && this.interval.equals(tcc.interval) && this.days.equals(tcc.days);
    }
    
    
    @Override
    public int hashCode() {
        return Boolean.valueOf(this.isUTC).hashCode() ^ this.begin.hashCode() ^ this.end.hashCode()
                ^ this.interval.hashCode() ^ this.days.hashCode();
    }
    
    
    /**
     * Checks whether the condition is satisfied in the state
     * 
     * @param state
     * @return
     */
    public boolean satisfiedIn(long state) {
        Calendar cal;
        if (this.isUTC) {
            cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        } else {
            cal = Calendar.getInstance();
        }
        cal.setTimeInMillis(state);
        
        // check day is okay
        switch (this.interval) {
            case REPEAT_DAILY:
                break;
            
            case REPEAT_WEEKLY:
                if (!this.days.contains(cal.get(Calendar.DAY_OF_WEEK))) {
                    return false;
                }
                break;
            
            case REPEAT_MONTHLY:
                if (!this.days.contains(cal.get(Calendar.DAY_OF_MONTH))) {
                    return false;
                }
                break;
            
            case REPEAT_YEARLY:
                if ((this.days.size() != 2) || (this.days.get(0) != cal.get(Calendar.MONTH))
                        || (this.days.get(1) != cal.get(Calendar.DAY_OF_MONTH))) {
                    return false;
                }
                break;
        }
        
        // check time
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);
        int sec = cal.get(Calendar.SECOND);
        TimeContextTime now = new TimeContextTime(hour, min, sec);
        
        boolean timeWraps = this.begin.compareTo(this.end) > 0;
        boolean dateBetweenBeginAndEnd = (this.begin.compareTo(now) <= 0) && (this.end.compareTo(now) >= 0);
        
        // either it's NOT wrapping AND     begin <= date <= end
        //     or it's     wrapping AND NOT begin <= date <= end
        return timeWraps ^ dateBetweenBeginAndEnd;
    }
    
    
    /*
     * Getters / Setters for view
     */
    
    protected boolean isUTC() {
        return this.isUTC;
    }
    
    
    protected void setUTC(boolean isUTC) {
        this.isUTC = isUTC;
    }
    
    
    protected TimeContextTime getBegin() {
        return this.begin;
    }
    
    
    protected TimeContextTime getEnd() {
        return this.end;
    }
    
    
    protected TimeContextIntervalType getInterval() {
        return this.interval;
    }
    
    
    protected void setInterval(TimeContextIntervalType interval) {
        this.interval = interval;
    }
    
    
    protected List<Integer> getDays() {
        return this.days;
    }
    
    
    public boolean representsWholeDay() {
        return this.begin.getDifferenceInSeconds(this.end, true) >= TimeContextTime.SECONDS_PER_DAY - 1;
    }
    
    
    public String getHumanReadable() {
        StringBuilder result = new StringBuilder();
        result.append(this.begin.toString());
        result.append(" - ");
        result.append(this.end.toString());
        result.append(" ");
        
        if (this.isUTC) {
            result.append("(UTC) ");
        }
        
        switch (this.interval) {
            case REPEAT_DAILY:
                result.append("repeating daily");
                break;
            case REPEAT_WEEKLY:
                result.append("repeating weekly on days of week ");
                result.append(this.interval.makeList(this.days));
                break;
            case REPEAT_MONTHLY:
                result.append("repeating monthly on days of month ");
                result.append(this.interval.makeList(this.days));
                break;
            case REPEAT_YEARLY:
                result.append("repeating yearly on ");
                result.append(this.days.get(1));
                result.append(".");
                result.append(this.days.get(0));
                result.append(".");
                break;
        }
        
        return result.toString();
    }
}
