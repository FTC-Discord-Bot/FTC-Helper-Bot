package com.bot.commands.helpers;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ConvertTime {
    public static String convertToRegularTime(String dateTimeStr) {
        // Parse the input string into a ZonedDateTime object
        ZonedDateTime dateTime = ZonedDateTime.parse(dateTimeStr);

        // Convert the date and time to the local timezone
        ZonedDateTime localDateTime = dateTime.withZoneSameInstant(ZoneId.systemDefault());

        // Create a DateTimeFormatter for the output format
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");

        // Format the local date and time into a string using the output formatter
        String outputString = localDateTime.format(outputFormatter);

        // Return the formatted string
        return outputString;
    }
}
