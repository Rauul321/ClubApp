package utilities;
import Server.Date;

/**
 * Utility class for parsing Date objects to and from String representations.
 */
public class DateParserUtils {
    public static String parseDate(Date date) {
        int day = date.getDay();
        int month = date.getMonth();
        int year = date.getYear();
        return String.format("%02d/%02d/%04d", day, month, year);
    }

    public static Date parseString(String dateStr) {
        String[] parts = dateStr.split("/");
        int day = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int year = Integer.parseInt(parts[2]);
        return new Date(day, month, year);
    }
}
