package Server;

import java.io.Serializable;

/**
 * Date class representing a date with day, month, and year.
 */
public class Date implements Serializable {
    private static final long serialVersionUID = 1958309447142644453L;
    private int day, month, year;

    public Date(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    @Override
    public String toString() {
        return String.format("%02d/%02d/%d", day, month, year);
    }
}
