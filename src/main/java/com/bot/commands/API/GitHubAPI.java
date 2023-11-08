package com.bot.commands.API;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

import org.json.*;


public class GitHubAPI {

    public static String commitActivity(String ownerRepo, String periodWMY) {
        String url = "https://img.shields.io/github/commit-activity/" + periodWMY + "/" + ownerRepo;

        return url;
    }

    public static String[] latestCommit(String OwnerRepo) throws IOException {
        URL url = new URL("https://api.github.com/repos/" + OwnerRepo + "/commits?per_page=1&page=1");
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
            JSONArray jsonArray = new JSONArray(response.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                JSONObject author = jsonObject.getJSONObject("commit").getJSONObject("author");
                String CMTurl = jsonObject.getString("html_url");
                JSONObject commit = jsonObject.getJSONObject("commit");
                String message = commit.getString("message");
                String name = author.getString("name");
                String sha = jsonObject.getString("sha");

                return new String[]{name,message,CMTurl,sha};


            }
        } else {
            System.out.println("Failed to retrieve the latest commit information from the GitHub API. Response code: " + con.getResponseCode());
        }
        con.disconnect();
        return new String[]{"Error","API Error, Response Code : "+con.getResponseCode()};
    }

    public static int[] commitstats(String OwnerRepo, String sha) throws IOException {
        URL url = new URL("https://api.github.com/repos/" + OwnerRepo + "/commits/" + sha);
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
            if (jsonObject.has("message")) {
                System.out.println("Error: " + jsonObject.getString("message"));

                return new int[]{0};
            }

            if (!jsonObject.has("stats")) {
                System.out.println("Error: Stats not found in JSON object");

                return new int[]{0};
            }

            JSONObject stats = jsonObject.getJSONObject("stats");
            int total = stats.getInt("total");
            int additions = stats.getInt("additions");
            int deletions = stats.getInt("deletions");

           return new int[]{total, additions, deletions};
        } else {
            System.out.println("Response Code GITHUB API " + con.getResponseCode());
        }

        con.disconnect();
        return new int[]{0};
    }

    public static String[] repoStats(String OwnerRepo) throws IOException {
        URL url = new URL("https://api.github.com/repos/" + OwnerRepo);
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
            if (jsonObject.has("message")) {
                System.out.println("Error: " + jsonObject.getString("message"));
                return new String[]{"Error",jsonObject.getString("message")};
            }


            JSONObject owner = jsonObject.getJSONObject("owner");
            String image = owner.getString("avatar_url");


                return new String[]{image};

        } else {
            System.out.println("Response Code GITHUB API " + con.getResponseCode());
        }

        con.disconnect();
        return new String[]{"Error","API Error, Response Code : "+con.getResponseCode()};
    }


    public static String[] searchRepos(String query) throws IOException {
        String Finquery = query.replace(" ", "+");
        URL url = new URL("https://api.github.com/search/repositories?q=" + Finquery + "in:name&sort=stars");

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
            if (jsonObject.has("message")) {
                System.out.println("Error: " + jsonObject.getString("message"));
                return new String[]{"Error",jsonObject.getString("message")};
            }

            int totalCount = jsonObject.getInt("total_count");

            if (totalCount == 0) {
                System.out.println("Error: No results");
                return new String[]{"Error","No results"};
            } else{

                JSONArray items = jsonObject.getJSONArray("items");
                JSONObject item1 = items.getJSONObject(0);

                String description1 = null;
                String language1 = null;
                // Value cannot be longer than 1024 characters. (Description)
                String name1 = item1.getString("name");
                String url1 = item1.getString("html_url");
                if (item1.isNull("description")) {
                    description1 = "No description provided";
                } else if (item1.getString("description").length() > 1024) {
                    description1 = item1.getString("description").substring(0, 1024);
                } else {
                    description1 = item1.getString("description");
                }
                if (item1.isNull("language")) {
                    language1 = "No language provided";
                } else {
                    language1 = item1.getString("language");
                }

                String image1 = item1.getJSONObject("owner").getString("avatar_url");

                return new String[]{name1,url1,description1,language1,image1};
            }
        }
        return new String[]{"Error","API call issue"};
    }
}
