package io.github.syst3ms.skriptparser.util;

import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.TimeZone;

public class SkriptDate implements Comparable<SkriptDate> {
    // TODO make a config for this
    public final static String DATE_FORMAT = "EEEE dd MMMM yyyy HH:mm:ss.SSS zzzXXX";
    public final static Locale DATE_LOCALE = Locale.US;
    private static TimeZone TIME_ZONE = TimeZone.getDefault();
    public final static int MILLIS_PER_DAY = 86400000;

    private long timestamp;

    private SkriptDate(long timestamp, TimeZone zone) {
        this.timestamp = timestamp - zone.getOffset(timestamp);
    }

    /**
     * Get a new {@link SkriptDate} with the current time
     * @return new {@link SkriptDate} with the current time
     */
    public static SkriptDate now() {
        return of(System.currentTimeMillis());
    }

    public static SkriptDate of(long timestamp) {
        return of(timestamp, TIME_ZONE);
    }

    public static SkriptDate of(long timestamp, TimeZone zone) {
        return new SkriptDate(timestamp, zone);
    }

    /**
     * The current day when it started.
     * @return the current day like it would just start
     */
	public static SkriptDate today() {
	    var local = LocalDate.now(TIME_ZONE.toZoneId()).atStartOfDay(TIME_ZONE.toZoneId());
	    return of(local.toEpochSecond() * 1000);
	}

    public static TimeZone getTimeZone() {
        return TIME_ZONE;
    }

    /**
     * Get the timestamp of this date.
     * @return The timestamp in milliseconds
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * The String representation of this date using a certain format
     * @param format the format
     * @return the string representation of this date
     */
    public String toString(String format) {
        return new SimpleDateFormat(format, DATE_LOCALE).format(new java.util.Date(timestamp));
    }

    /**
     * Get the difference between 2 dates.
     * @param other the other date
     * @return the duration between the dates
     */
    public Duration difference(SkriptDate other) {
        return Duration.ofMillis(timestamp - other.getTimestamp()).abs();
    }

    /**
     * Add a {@link Duration} to this date.
     * @param span {@link Duration} to add
     */
    public void add(Duration span) {
        timestamp += span.toMillis();
    }

    /**
     * Subtract a {@link Duration} from this date.
     * @param span {@link Duration} to subtract
     */
    public void subtract(Duration span) {
        timestamp -= span.toMillis();
    }

    /**
     * Get a new instance of this date with the added Duration.
     * @param span {@link Duration} to add to this Date
     * @return new {@link SkriptDate} with the added Duration
     */
    public SkriptDate plus(Duration span) {
        return of(timestamp + span.toMillis());
    }

    /**
     * Get a new instance of this date with the subtracted Duration.
     * @param span {@link Duration} to subtract from this Date
     * @return new {@link SkriptDate} with the subtracted Duration
     */
    public SkriptDate minus(Duration span) {
        return of(timestamp - span.toMillis());
    }

    /**
     * Get the {@link LocalDate} instance of this date.
     * @return the {@link LocalDate} instance of this date
     */
    public LocalDateTime toLocalDateTime() {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), TIME_ZONE.toZoneId());
    }

    @Override
    public int compareTo(@Nullable SkriptDate other) {
        long d = other == null ? timestamp : timestamp - other.timestamp;
        return d < 0 ? -1 : d > 0 ? 1 : 0;
    }

    public String toString() {
        return toString(DATE_FORMAT);
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
        return result;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof SkriptDate))
            return false;
        return compareTo((SkriptDate) obj) == 0;
    }
}