package com.bot.commands.API;


import com.bot.commands.helpers.URLEncoderHelper;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.bot.commands.helpers.URLEncoderHelper.URLEncode;

public class GoogleMapsAPI {
    public static String getStaticMapUrl(String location, int zoom) throws UnsupportedEncodingException {
        // Google Maps API Key is required for this to work. Not included in public version
        String apiKey = "GOOGLE_MAPS_TOKEN";
        String finLocation = URLEncode(location);
        String url = "https://maps.googleapis.com/maps/api/staticmap?center=" + finLocation + "&zoom=" + zoom + "&size=600x300&maptype=roadmap&markers=size:mid%7Ccolor:red%7C" + finLocation + "&key=" + apiKey;
        return url;
    }

    public static String directionsUrl(String location) throws UnsupportedEncodingException {
        String finLocation = URLEncode(location);
        String url = "https://www.google.com/maps/dir/?api=1&destination=" + finLocation;
        return url;
    }

    public static String geocodeLocation(String location) throws IOException {
        String finlocation = URLEncoderHelper.URLEncode(location);
        // Google Maps API Key is required for this to work. Not included in public version
        String apiKey = "GOOGLE_MAPS_TOKEN";
        String urlString = "https://maps.googleapis.com/maps/api/geocode/json?address=" + finlocation + "&key=" + apiKey;
            String cords = null;
        URL url = new URL(urlString);
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

            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONArray results = jsonResponse.getJSONArray("results");
            if (results.length() > 0) {
                JSONObject result = results.getJSONObject(0);
                JSONObject loc = result.getJSONObject("geometry").getJSONObject("location");
                double lat = loc.getDouble("lat");
                double lng = loc.getDouble("lng");
                 cords = "latitude="+lat + "&" + "longitude="+ lng+"&";

            } else {
                System.out.println("No results found");
            }
        } else {
            System.out.println("HTTP error code: " + responseCode);
        }
            return cords;
    }
}
