package com.bot.commands.helpers;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ConvertDate {

    public static String convertToDateTime(String isoTimestamp) {
        // Parse the ISO 8601 string into a LocalDateTime object
        LocalDateTime localDateTime = LocalDateTime.parse(isoTimestamp);

        // Convert the LocalDateTime to a ZonedDateTime using the current timezone
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());

        // Format the ZonedDateTime into a string with the "Month Day Year" format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, uuuu");
        String formattedDate = zonedDateTime.format(formatter);

        return formattedDate;
    }
}
