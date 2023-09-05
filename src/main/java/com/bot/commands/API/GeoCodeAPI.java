package com.bot.commands.API;

import com.bot.commands.helpers.URLEncoderHelper;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class GeoCodeAPI {

    public static String GeoCode (String locationInWords) throws IOException {

        String finLocation = URLEncoderHelper.URLEncode(locationInWords);
        String stringUrl = "https://geocode.maps.co/search?q=" + finLocation;
        URL url = new URL(stringUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        int responseCode = con.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JSONArray jsonResponse = new JSONArray(response.toString());
            if (jsonResponse.length() == 0) {
                return(GoogleMapsAPI.geocodeLocation(locationInWords));
            } else {
                JSONObject FirstResponse = jsonResponse.getJSONObject(0);

                String lat = FirstResponse.getString("lat");
                String lon = FirstResponse.getString("lon");
                return lat + "," + lon;
            }
    }
        return null;
    }
}
