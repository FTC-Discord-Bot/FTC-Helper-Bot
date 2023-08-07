package com.bot.commands.API;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.bot.FTCHelperBot.NASA_API_KEY;

public class NasaAPI {
    public static String getAPOD() throws IOException {
        URL url = new URL("https://api.nasa.gov/planetary/apod?api_key="+NASA_API_KEY);
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

            String imgURL = jsonObject.getString("url");
            String title = jsonObject.getString("title");
            String explanation = jsonObject.getString("explanation");

            return imgURL + "|" + title + "|" + explanation;


    } else {
            System.out.println("Failed to retrieve the latest APOD, Response code: " + con.getResponseCode());
            return null;
        }


    }
}
