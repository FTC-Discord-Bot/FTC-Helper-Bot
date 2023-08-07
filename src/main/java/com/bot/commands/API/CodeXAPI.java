package com.bot.commands.API;

import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static com.bot.commands.helpers.URLEncoderHelper.URLEncode;
@Deprecated
/*
The Code X API is down for the time being
 */
public class CodeXAPI {
    public static String compileCode(String code, String language) throws UnsupportedEncodingException {
        String fincode = URLEncode(code);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.codex.jaagrav.in"))
                .header("content-type", "application/x-www-form-urlencoded")
                .method("POST", HttpRequest.BodyPublishers.ofString("code=" + fincode + "&language=" + language))
                .build();
        HttpResponse<String> response = null;
        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (response.statusCode() == HttpURLConnection.HTTP_OK) {
            JSONObject jsonObject = new JSONObject(response.body());
            //   String timeStamp = jsonObject.getString("timeStamp");
            String output = jsonObject.getString("output");


            String error = jsonObject.getString("error");
            //String info = jsonObject.getString("info");
            String codeOut = jsonObject.getString("output");


            return codeOut + "| " + error;
        } else {
            return "Failed to compile code. Response code: " + response.statusCode();
        }
    }
}
