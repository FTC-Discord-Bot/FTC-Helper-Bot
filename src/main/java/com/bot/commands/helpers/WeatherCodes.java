package com.bot.commands.helpers;

public class WeatherCodes {

    public static String convertCode(int code){
        String converted;


        switch (code) {
            case 0:
                converted = ":sunny: Clear sky";
                break;
            case 1:
                converted = ":sunny: Mainly clear";
                break;
            case 2:
                converted = ":partly_sunny: Partly cloudy";
                break;
            case 3:
                converted = ":white_sun_cloud: Overcast";
                break;
            case 45:
                converted = ":fog: Fog";
                break;
            case 48:
                converted = ":snowflake: :fog: Rime fog";
                break;
            case 51:
                converted = ":white_sun_rain_cloud: Drizzle: Light";
                break;
            case 53:
                converted = ":white_sun_rain_cloud: Drizzle: Moderate";
                break;
            case 55:
                converted = ":white_sun_rain_cloud: Drizzle: Dense";
                break;
            case 56:
                converted = ":white_sun_rain_cloud: :snowflake: Freezing Drizzle: Light";
                break;
            case 57:
                converted = ":white_sun_rain_cloud: :snowflake: Freezing Drizzle: Dense";
                break;
            case 61:
                converted = ":cloud_rain: Rain: Slight";
                break;
            case 63:
                converted = ":cloud_rain: Rain: Moderate";
                break;
            case 65:
                converted = ":cloud_rain: Rain: Heavy";
                break;
            case 66:
                converted = ":cloud_rain: :snowflake: Freezing Rain: Light";
                break;
            case 67:
                converted = ":cloud_rain: :snowflake: Freezing Rain: Heavy";
                break;
            case 71:
                converted = ":cloud_snow: Snow fall: Slight";
                break;
            case 73:
                converted = ":cloud_snow: Snow fall: Moderate";
                break;
            case 75:
                converted = ":cloud_snow: Snow fall: Heavy";
                break;
            case 77:
                converted = ":cloud_snow: Snow grains";
                break;
            case 80:
                converted = ":cloud_rain: Rain showers: Slight";
                break;
            case 81:
                converted = ":cloud_rain: Rain showers: Moderate";
                break;
            case 82:
                converted = ":cloud_rain: Rain showers: Violent";
                break;
            case 85:
                converted = ":cloud_snow: Snow showers: Slight";
                break;
            case 86:
                converted = ":cloud_snow: Snow showers: Heavy";
                break;
            case 95:
                converted = ":cloud_lightning: Thunderstorm: Slight";
                break;
            case 96:
                converted = ":cloud_lightning: Thunderstorm: Slight hail";
                break;
            case 99:
                converted = ":cloud_lightning: Thunderstorm: Heavy hail";
                break;
            default:
                converted = "Unknown code value";
                break;
        }
        return converted;
    }
}
