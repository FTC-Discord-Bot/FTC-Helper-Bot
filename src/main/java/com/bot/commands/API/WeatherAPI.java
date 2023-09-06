package com.bot.commands.API;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.bot.commands.helpers.WeatherCodes.convertCode;


public class WeatherAPI {


    public static String getWeather(String location, String UNITS, int returnType) throws IOException {
        String imperial = "&temperature_unit=fahrenheit&windspeed_unit=mph&precipitation_unit=inch";
        String metric = "";
        String units;
        if (UNITS.equals("imperial")) {
            units = imperial;
        } else {
            units = metric;
        }
        URL url = new URL("https://api.open-meteo.com/v1/forecast?" + location + "daily=weathercode,temperature_2m_max,temperature_2m_min,precipitation_sum,precipitation_hours&current_weather=true" + units + "&timezone=auto");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");


        if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();


            JSONObject jsonObject = new JSONObject(response.toString());
            JSONObject current = jsonObject.getJSONObject("current_weather");
            double temp = current.getInt("temperature");
            double wind = current.getInt("windspeed");
            int code = current.getInt("weathercode");
            JSONObject daily = jsonObject.getJSONObject("daily");
            JSONArray tempMaxArray = daily.getJSONArray("temperature_2m_max");
            double firstTempMax = tempMaxArray.getDouble(0);
            JSONArray tempMinArray = daily.getJSONArray("temperature_2m_min");
            double firstTempMin = tempMinArray.getDouble(0);
            JSONArray precipArray = daily.getJSONArray("precipitation_sum");
            double firstPrecip = precipArray.getDouble(0);
            JSONArray wcodeArray = daily.getJSONArray("weathercode");
            int firstWeather = wcodeArray.getInt(0);
            int secondWeather = wcodeArray.getInt(1);
            int thirdWeather = wcodeArray.getInt(2);
            int fourthWeather = wcodeArray.getInt(3);
            int fifthWeather = wcodeArray.getInt(4);
            int sixthWeather = wcodeArray.getInt(5);
            int seventhWeather = wcodeArray.getInt(6);


            JSONArray timeArraycode = daily.getJSONArray("time");
            String firstTime = timeArraycode.getString(0);
            String secondTime = timeArraycode.getString(1);
            String thirdTime = timeArraycode.getString(2);
            String fourthTime = timeArraycode.getString(3);
            String fifthTime = timeArraycode.getString(4);
            String sixthTime = timeArraycode.getString(5);
            String seventhTime = timeArraycode.getString(6);

            String convertedCodeCurrent = convertCode(code);
            String convertedCodeFirst = convertCode(firstWeather);


            String convertedCodeSecond = convertCode(secondWeather);
            String convertedCodeThird = convertCode(thirdWeather);
            String convertedCodeFourth = convertCode(fourthWeather);
            String convertedCodeFifth = convertCode(fifthWeather);
            String convertedCodeSixth = convertCode(sixthWeather);
            String convertedCodeSeventh = convertCode(seventhWeather);


            if (returnType == 1) {
                return String.valueOf(temp) + "|" + String.valueOf(wind) + "|" + convertedCodeCurrent + "|" + String.valueOf(firstTempMax) + "|" + String.valueOf(firstTempMin) + "|" + String.valueOf(firstPrecip) + "|" + convertedCodeFirst + "|" + UNITS;
            } else {

                return convertedCodeFirst + "|" + firstTime + "|" + convertedCodeSecond + "|" + secondTime + "|" + convertedCodeThird + "|" + thirdTime + "|" + convertedCodeFourth + "|" + fourthTime + "|" + convertedCodeFifth + "|" + fifthTime + "|" + convertedCodeSixth + "|" + sixthTime + "|" + convertedCodeSeventh + "|" + seventhTime;
            }
        } else {
            System.out.println("Failed to retrieve weather: " + con.getResponseCode());
        }
        con.disconnect();
        return null;
    }


}




