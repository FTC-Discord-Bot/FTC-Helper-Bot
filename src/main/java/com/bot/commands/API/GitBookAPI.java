package com.bot.commands.API;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import static com.bot.FTCHelperBot.GITBOOK_TOKEN;


public class GitBookAPI {
    public static JSONObject searchDocs(String spaceID, String query) throws IOException {
        URL url = new URL("https://api.gitbook.com/v1/spaces/" + spaceID + "/search?query=" + query);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        // add authorization token
        con.setRequestProperty("Authorization", "Bearer " + GITBOOK_TOKEN);
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

            return jsonObject;


        } else {
            return null;
        }
    }

    public static JSONObject askAIDocs(String spaceID, String query) throws IOException {
        // URL for the API endpoint
        URL url = new URL("https://api.gitbook.com/v1/spaces/" + spaceID + "/search/ask");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        // Set the request method to POST
        con.setRequestMethod("POST");

        // Add the required headers
        con.setRequestProperty("Authorization", "Bearer " + GITBOOK_TOKEN);
        con.setRequestProperty("Content-Type", "application/json");

        // JSON payload for the query
        String jsonPayload = "{\"query\": \"" + query + "\"}";

        // Enable output and send the JSON payload to the server
        con.setDoOutput(true);
        OutputStream os = con.getOutputStream();
        os.write(jsonPayload.getBytes());
        os.flush();
        os.close();

        // Check the response code
        if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
            // Read the response from the server
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Parse the JSON response
            JSONObject jsonObject = new JSONObject(response.toString());

            return jsonObject;
        } else {
            // Handle error response, if needed
            System.err.println("Error response code: " + con.getResponseCode());
            return null;
        }
    }
}
