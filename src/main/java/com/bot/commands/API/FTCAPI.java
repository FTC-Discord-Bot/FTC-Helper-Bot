package com.bot.commands.API;

import java.io.UnsupportedEncodingException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import net.dv8tion.jda.api.EmbedBuilder;
import org.json.*;

import static com.bot.FTCHelperBot.*;


public class FTCAPI {

    private static final String getBasicAuthenticationHeader(String username, String password) {
        String valueToEncode = username + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(valueToEncode.getBytes());
    }

    public static HttpResponse<byte[]> get(String urlstr, String username, String password) {
        try {
            HttpClient client = HttpClient.newBuilder()
                    .authenticator(new Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(username, password.toCharArray());
                        }
                    })
                    .followRedirects(HttpClient.Redirect.ALWAYS)
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlstr))
                    .header("Authorization", getBasicAuthenticationHeader(username, password))
                    .build();

            return client.send(request, HttpResponse.BodyHandlers.ofByteArray());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static JSONObject getJSON(String urlstr, String username, String password) throws org.json.JSONException {
        HttpResponse<byte[]> response = get(urlstr, username, password);
        byte[] responseBody = response.body();
        String responseStr = null;
        if (responseBody != null && responseBody.length > 0) {
            responseStr = new String(responseBody);
        }

        if (response.statusCode() >=300){
            JSONObject errorObject = new JSONObject();
            errorObject.put("error", true);
            if (responseStr != null) {
                errorObject.put("message", responseStr);
            }
            return errorObject;
        } else {
            JSONObject object = new JSONObject(responseStr);return object;
        }
    }


    // get json with credentials from config
    public static JSONObject GetJSONC(String url) throws org.json.JSONException{
        return getJSON(url, FTC_USERNAME, FTC_PASSWORD);
    }


    // COMMANDS

    // utils

    public static boolean NotEmpty(Object value) {
        return !value.equals(null) && !value.equals("");
    }

    public static void addField(EmbedBuilder eb, String name, Object value, boolean inline) {
        //System.out.println(value);
        if (NotEmpty(value)) {
            eb.addField(name, String.valueOf(value), inline);
        }
    }

    // to ascii table!!!
    public static String ToTable(JSONArray data, String[] internalkeys, String[] keys, int[] highlightindex) {
        /*
            data is a JSONArray of JSONObjects with each being like an api data
            internalkeys is the internal keys for the api data
            keys is the key displayed

            TODO:
            StringBuilder???
         */

        // highlight
        boolean highlighton = highlightindex.length != 0;
        boolean[] highlight = new boolean[data.length()];
        if (highlighton) {
            for (int i = 0; i < data.length(); i++) {
                highlight[i] = false;
            }
            for (int i = 0; i < highlightindex.length; i++) {
                highlight[highlightindex[i]] = true;
            }
        }


        // setting up
        int[] length = new int[keys.length];
        int keysLength = keys.length;
        for (int i = 0; i < keysLength; i++) {
            length[i] = keys[i].length();
        }

        int a;
        int dataLength = data.length();
        int internalkeysLength = internalkeys.length;
        for (int i = 0; i < dataLength; i++) {
            JSONObject apidata = (JSONObject) data.get(i);

            for (int i2 = 0; i2 < internalkeysLength; i2++) {
                a = String.valueOf(apidata.get(internalkeys[i2])).length();
                length[i2] = a > length[i2] ? a : length[i2];
            }

        }


        String newline = "\n";
        String plus = highlighton ? "#" : "+";
        String row = plus;
        for (int i = 0; i < length.length; i++) {
            row = row + ("-").repeat(length[i]) + plus;
        }


        String table = "";
        table = table + row + newline;
        table = table + "|";
        for (int i2 = 0; i2 < keysLength; i2++) {
            String value = keys[i2];
            table = table + value + (" ").repeat(length[i2] - value.length()) + "|";
        }
        table = table + newline + row + newline;

        for (int i = 0; i < dataLength; i++) {
            JSONObject apidata = (JSONObject) data.get(i);

            /*
            addField(eb, "Rank: ", String.valueOf(rankingdata.get("rank")), inline);
            addField(eb, "Team: ", String.valueOf(rankingdata.get("teamNumber")), inline);
             */

            if (highlight[i]) {
                table = table + "+";
            } else {
                table = table + "|";
            }
            for (int i2 = 0; i2 < internalkeysLength; i2++) {
                String value = String.valueOf(apidata.get(internalkeys[i2]));
                table = table + value + (" ").repeat(length[i2] - value.length()) + "|";
            }


            table = table + newline + row + newline;
        }

        return (highlighton ? "```diff" : "```") + newline + table + "```";
    }

    // shorten a JSONArray
    public static JSONArray Shorten(JSONArray dataraw, int low, int high) {
        low = low < 0 ? 0 : low;
        high = high > dataraw.length() ? dataraw.length() : high;

        JSONArray data = new JSONArray();
        for (int i = low; i < high; i++) {
            data.put(dataraw.get(i));
        }
        return data;
    }
    // calculate stats
    public static double CalculateOPR() {
        // TODO
        return 0;
    }


    // cache

    /*
    public static Map CacheCreate() {
        Map<Integer,Object> map = new HashMap<>();
        return map;
    }
     */

    // shit code but it should work
    public static Object CacheGet(Map map, int index) {
        if (map.containsKey(index)) {
            return map.get(index);
        } else {
            return null;
        }
    }

    public static void CacheSet(Map<Integer, Object> map, int index, Object value, int maxSize) {
        map.put(index, value);
        if (map.size() > maxSize) {
            // If the map size exceeds the maximum, remove the oldest entry (first inserted).
            int oldestKey = map.keySet().iterator().next();
            map.remove(oldestKey);
        }
    }

    public static Object CacheGetS(Map map, String index) {
        if (map.containsKey(index)) {
            return map.get(index);
        } else {
            return null;
        }
    }

    public static void CacheSetS(Map<String, Object> map, String index, Object value, int maxSize) {
        map.put(index, value);
        if (map.size() > maxSize) {
            // If the map size exceeds the maximum, remove the oldest entry (first inserted).
            String oldestKey = map.keySet().iterator().next();
            map.remove(oldestKey);
        }
    }

    public static Map<Integer, Object> EventCache = new HashMap<>();
    public static Map<Integer, JSONObject> InfoCache = new HashMap<>();
    public static Map<Integer, Object> AwardCache = new HashMap<>();
    public static Map<String, Object> LeagueRankCache = new HashMap<>();



    // commands

    public static JSONObject GetInfoAPI(int team, int season) throws org.json.JSONException{
        JSONObject teamdata;
        if (InfoCache.get(team) == null) {
            JSONObject data = FTCAPI.GetJSONC("http://ftc-api.firstinspires.org/v2.0/" + season + "/teams?teamNumber=" + String.valueOf(team));
            if (data.has("error")) {
                return data;
            } else {
                teamdata = data.getJSONArray("teams").getJSONObject(0);

                // CALCULATE STATS
            /*
            opr
                oprauto
                oprteleop
            nonpenaltyopr
                nonpenaltyoprauto
                nonpenaltyoprteleop
            maxscore
             */
                teamdata.put("opr", "");


                InfoCache.put(team, teamdata);
                if (InfoCache.size() > 25) {
                    // If the map size exceeds the maximum, remove the oldest entry (first inserted).
                    int oldestKey = InfoCache.keySet().iterator().next();
                    InfoCache.remove(oldestKey);
                }
                System.out.println("Not Found cached data");
            }
        } else {
            teamdata = InfoCache.get(team);
            System.out.println("Found cached data");
        }
        return teamdata;

    }

    public static Boolean GetInfo(EmbedBuilder eb, int team, boolean inline, int season) {

        JSONObject teamdata = GetInfoAPI(team, season);
        if (teamdata.has("error")){
            if (teamdata.has("message")){
                addField(eb,"Error response :",teamdata.getString("message"),inline);
            }
            eb.setColor(ERROR_COLOR);
            eb.setTitle("Error fetching info about team " + team);
            return true;
        } else {
            eb.setTitle("Info about team " + team, "https://ftc-events.firstinspires.org/team/" + team);
            eb.setColor(MAIN_COLOR);
            String[] fieldNames = {
                    "Full name: ", "Short name: ", "School: ", "City: ", "State/Prov: ", "Country: ",
                    "Website: ", "Rookie year: ", "Robot name: ", "District: ", "HomeCMP: "
            };

            String[] dataKeys = {
                    "nameFull", "nameShort", "schoolName", "city", "stateProv", "country",
                    "website", "rookieYear", "robotName", "districtCode", "homeCMP"
            };
            int fieldNamesLength = fieldNames.length;
            for (int i = 0; i < fieldNamesLength; i++) {
                addField(eb, fieldNames[i], teamdata.get(dataKeys[i]), inline);
            }
            return false;
        }


    }


    public static StringBuilder TeamLocation(int team, int season) {
        JSONObject teamdata = GetInfoAPI(team, season);
        StringBuilder location;

        if (teamdata.has("error")) {
            location = new StringBuilder("Error: could not find team " + team);
        }else {
            location = new StringBuilder(String.valueOf(teamdata.get("city")) + "," + String.valueOf(teamdata.get("stateProv")) + "," + String.valueOf(teamdata.get("country")));
        }
        return location;
    }

    public static JSONArray GetEventAPI(int team, int season) {
        Object obj = CacheGet(EventCache, team);
        JSONArray events;
        if (obj == null) {
            JSONObject data = FTCAPI.GetJSONC("http://ftc-api.firstinspires.org/v2.0/" + season + "/events?teamNumber=" + team);
            events = data.getJSONArray("events");
            CacheSet(EventCache, team, events,10);
        } else {
            events = (JSONArray) obj;
        }
        return events;
    }

    public static JSONArray GetEventAPICode(String code, int season) {
        JSONArray events;

            JSONObject data = FTCAPI.GetJSONC("http://ftc-api.firstinspires.org/v2.0/" + season + "/events?eventCode=" + code);
            try {
                events = data.getJSONArray("events");
            } catch (JSONException e) {
                events = new JSONArray();
                events.put(data);
            }

        return events;
    }
    public static boolean GetEventUsingCode(EmbedBuilder eb,String eventCode, int season, boolean inline,boolean advanced) {
        JSONArray events = GetEventAPICode(eventCode, season);
        JSONObject eventdata = events.getJSONObject(0);
        if (eventdata.has("error")) {
            eb.setColor(ERROR_COLOR);
            eb.setTitle("Error fetching info about event " + eventCode);
            eb.addField("Error response: ", events.getJSONObject(0).getString("message"), false);
            return false;
        } else {
            if (advanced) {

                addField(eb, "Name: ", eventdata.get("name"), inline);
                addField(eb, "Venue: ", eventdata.get("venue"), inline);
                try {
                    addField(eb, "Address: ", "[" + eventdata.get("address").toString() + "](" + GoogleMapsAPI.directionsUrl(eventdata.get("address").toString()) + ")", inline);
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
                addField(eb, "City: ", eventdata.get("city"), inline);
                addField(eb, "State/Prov: ", eventdata.get("stateprov"), inline);
                addField(eb, "Country: ", eventdata.get("country"), inline);
                addField(eb, "Timezone: ", eventdata.get("timezone"), inline);
                addField(eb, "Start: ", eventdata.get("dateStart"), inline);
                addField(eb, "End: ", eventdata.get("dateEnd"), inline);
                addField(eb, "Website: ", eventdata.get("website"), inline);
                addField(eb, "Live Stream URL: ", eventdata.get("liveStreamUrl"), inline);
                addField(eb, "Webcasts: ", eventdata.get("webcasts"), inline);
                addField(eb, "Field Count: ", eventdata.get("fieldCount"), inline);
                addField(eb, "Published: ", eventdata.get("published"), inline);
                addField(eb, "Type: ", eventdata.get("type"), inline);
                addField(eb, "Type Name: ", eventdata.get("typeName"), inline);
                addField(eb, "Remote: ", eventdata.get("remote"), inline);
                addField(eb, "Hybrid: ", eventdata.get("hybrid"), inline);
                addField(eb, "Code: ", eventdata.get("code"), inline);
                addField(eb, "League Code: ", eventdata.get("leagueCode"), inline);
                addField(eb, "Region Code: ", eventdata.get("regionCode"), inline);
                addField(eb, "District Code: ", eventdata.get("districtCode"), inline);
                addField(eb, "Division Code: ", eventdata.get("divisionCode"), inline);
                addField(eb, "ID: ", eventdata.get("eventId"), inline);
            } else {
                addField(eb, "Name: ", eventdata.get("name"), inline);
                addField(eb, "Venue: ", eventdata.get("venue"), inline);
                try {
                    addField(eb, "Address: ", "[" + eventdata.get("address").toString() + "](" + GoogleMapsAPI.directionsUrl(eventdata.get("address").toString()) + ")", inline);
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
                addField(eb, "Start: ", eventdata.get("dateStart"), inline);
                addField(eb, "City State/Prov Country: ", eventdata.get("city") + " " + eventdata.get("stateprov") + " " + eventdata.get("country"), inline);
                addField(eb, "Code: ", eventdata.get("code"), inline);
                addField(eb, "Live Stream URL: ", eventdata.get("liveStreamUrl"), inline);

                return true;
            }

        }
        return true;
    }
    public static int GetEvent(EmbedBuilder eb, int team, int index, boolean inline, int season, Boolean advanced) {
        JSONArray events = GetEventAPI(team, season);
eb.clearFields();
     JSONObject eventdata = events.getJSONObject(index);
if (advanced) {

    addField(eb, "Name: ", eventdata.get("name"), inline);
    addField(eb, "Venue: ", eventdata.get("venue"), inline);
    try {
        addField(eb, "Address: ", "[" + eventdata.get("address").toString() + "](" + GoogleMapsAPI.directionsUrl(eventdata.get("address").toString()) + ")", inline);
    } catch (UnsupportedEncodingException e) {
        throw new RuntimeException(e);
    }
    addField(eb, "City: ", eventdata.get("city"), inline);
    addField(eb, "State/Prov: ", eventdata.get("stateprov"), inline);
    addField(eb, "Country: ", eventdata.get("country"), inline);
    addField(eb, "Timezone: ", eventdata.get("timezone"), inline);
    addField(eb, "Start: ", eventdata.get("dateStart"), inline);
    addField(eb, "End: ", eventdata.get("dateEnd"), inline);
    addField(eb, "Website: ", eventdata.get("website"), inline);
    addField(eb, "Live Stream URL: ", eventdata.get("liveStreamUrl"), inline);
    addField(eb, "Webcasts: ", eventdata.get("webcasts"), inline);
    addField(eb, "Field Count: ", eventdata.get("fieldCount"), inline);
    addField(eb, "Published: ", eventdata.get("published"), inline);
    addField(eb, "Type: ", eventdata.get("type"), inline);
    addField(eb, "Type Name: ", eventdata.get("typeName"), inline);
    addField(eb, "Remote: ", eventdata.get("remote"), inline);
    addField(eb, "Hybrid: ", eventdata.get("hybrid"), inline);
    addField(eb, "Code: ", eventdata.get("code"), inline);
    addField(eb, "League Code: ", eventdata.get("leagueCode"), inline);
    addField(eb, "Region Code: ", eventdata.get("regionCode"), inline);
    addField(eb, "District Code: ", eventdata.get("districtCode"), inline);
    addField(eb, "Division Code: ", eventdata.get("divisionCode"), inline);
    addField(eb, "ID: ", eventdata.get("eventId"), inline);
} else {
    addField(eb, "Name: ", eventdata.get("name"), inline);
    addField(eb, "Venue: ", eventdata.get("venue"), inline);
    try {
        addField(eb, "Address: ", "[" + eventdata.get("address").toString() + "](" + GoogleMapsAPI.directionsUrl(eventdata.get("address").toString()) + ")", inline);
    } catch (UnsupportedEncodingException e) {
        throw new RuntimeException(e);
    }
    addField(eb, "Start: ", eventdata.get("dateStart"), inline);
    addField(eb, "City State/Prov Country: ", eventdata.get("city") + " "+ eventdata.get("stateprov")+" "+ eventdata.get("country"), inline);
    addField(eb, "Code: ", eventdata.get("code"), inline);
    addField(eb, "Live Stream URL: ", eventdata.get("liveStreamUrl"), inline);


}
        int length = events.length();
        eb.setFooter("Page " + (index + 1) + "/" + length);
        return length;
    }


    public static JSONArray GetLeagueRankAPI(String regionCode, String leagueCode, int season) {
        Object obj = CacheGetS(LeagueRankCache, regionCode + ":" + leagueCode);
        JSONArray rankings;
        if (obj == null) {
            JSONObject data = FTCAPI.GetJSONC("http://ftc-api.firstinspires.org/v2.0/" + season + "/leagues/rankings/" + regionCode + "/" + leagueCode);
            try {
                rankings = data.getJSONArray("Rankings");

                // Continue with your logic for displaying the ranking data
                // ...

            } catch (JSONException e) {
                rankings = null;
            }
            CacheSetS(LeagueRankCache, regionCode + ":" + leagueCode, rankings,10);
        } else {
            rankings = (JSONArray) obj;
        }
        return rankings;
    }

    // if centerOverride is -1, it auto centers on team number
    public static int GetLeagueRankTeam(EmbedBuilder eb, String regionCode, String leagueCode, boolean inline, int season, int teamnumber, int centerOverride) {
        // Compact
        JSONArray rankingsraw = GetLeagueRankAPI(regionCode, leagueCode, season);

        // shorten rankings here
        int range = 2; // how much above and below to render
        int center = 0; // "center" place for rendering

        if (centerOverride != -1) {
            center = centerOverride;
        } else {
            for (int i = 0; i < rankingsraw.length(); i++) {
                if ((int) ((JSONObject) rankingsraw.get(i)).get("teamNumber") == teamnumber) {
                    center = i + 1;
                }
            }
        }

        int low = center - range - 1;
        int high = center + range;
        low = low < 0 ? 0 : low;
        high = high > rankingsraw.length() ? rankingsraw.length() : high;


        JSONArray rankings = new JSONArray();
        for (int i = low; i < high; i++) {
            rankings.put(rankingsraw.get(i));
        }



        String[] internalkeys = {"rank", "teamNumber", "wins", "losses", "ties"};
        String[] keys = {"Ranking", "Team", "Wins", "Losses", "Ties"};
        int[] highlightindex = {center - low - 1};




        eb.setDescription(ToTable(rankings, internalkeys, keys, highlightindex));
        return rankingsraw.length();
    }

    public static int GetLeagueRank(EmbedBuilder eb, String regionCode, String leagueCode, boolean inline, int season, int startIndex, int endIndex) {

        String[] internalkeys = {"rank", "teamNumber", "wins", "losses", "ties"};
        String[] keys = {"Ranking", "Team", "Wins", "Losses", "Ties"};
        int[] highlightindex = {};

        JSONArray rankings = GetLeagueRankAPI(regionCode, leagueCode, season);

        if (rankings == null) {
            eb.setDescription("No rankings found");
            return 0;
        } else {
            JSONArray shortenedRankings = Shorten(rankings, startIndex, endIndex);
            String table = ToTable(shortenedRankings, internalkeys, keys, highlightindex);
            eb.setDescription(table);
            return rankings.length();
        }



    }




//    public static void GetEventRank(EmbedBuilder eb, String code, boolean inline) {
//        // Compact
//        JSONArray rankings = GetEventRankAPI(code, BotConfig.DEFAULT_SEASON);
//
//        String[] internalkeys = {"rank", "teamNumber    ", "wins", "losses", "ties"};
//        String[] keys = {"Ranking", "Team", "Wins", "Losses", "Ties"};
//        int[] highlightindex = {};
//
////        int[] length = new int[keys.length];
////        for (int i = 0; i < keys.length; i++) {
////            length[i] = keys[i].length();
////        }
////
////        int a;
////        for (int i = 0; i < rankings.length(); i++) {
////            JSONObject rankingdata = (JSONObject) rankings.get(i);
////
////            for (int i2 = 0; i2 < internalkeys.length; i2++) {
////                a = String.valueOf(rankingdata.get(internalkeys[i2])).length();
////                length[i2] = a > length[i2] ? a : length[i2];
////            }
////
////        }
////
////
////        String newline = "\n";
////        String row = "+";
////        for (int i = 0; i < length.length; i++) {
////            row = row + ("-").repeat(length[i]) + "+";
////        }
////
////
////
////        String table = "";
////        table = table + row + newline;
////        table = table + "|";
////        for (int i2 = 0; i2 < keys.length; i2++) {
////            String value = keys[i2];
////            table = table + value + (" ").repeat(length[i2] - value.length()) + "|";
////        }
////        table = table + newline + row + newline;
////
////        for (int i = 0; i < rankings.length(); i++) {
////            JSONObject rankingdata = (JSONObject) rankings.get(i);
////
////            /*
////            addField(eb, "Rank: ", String.valueOf(rankingdata.get("rank")), inline);
////            addField(eb, "Team: ", String.valueOf(rankingdata.get("teamNumber")), inline);
////             */
////
////            table = table + "|";
////            for (int i2 = 0; i2 < internalkeys.length; i2++) {
////                String value = String.valueOf(rankingdata.get(internalkeys[i2]));
////                table = table + value + (" ").repeat(length[i2] - value.length()) + "|";
////            }
////
////
////            table = table + newline + row + newline;
////        }
//
//
//        eb.setDescription(ToTable(rankings, internalkeys, keys, highlightindex));
//
//    }
    public static JSONArray GetEventRankAPI(String code, int season) {
        JSONArray rankings;
        JSONObject data = FTCAPI.GetJSONC("http://ftc-api.firstinspires.org/v2.0/" + season + "/rankings/" + code);
        rankings = data.getJSONArray("Rankings");
        return rankings;
    }

    public static JSONArray GetAwardAPI(int season) {
        Object obj = CacheGet(AwardCache, season);
        JSONArray awards;
        if (obj == null) {
            JSONObject data = FTCAPI.GetJSONC("http://ftc-api.firstinspires.org/v2.0/" + season + "/awards/list");
            awards = data.getJSONArray("awards");
            CacheSet(AwardCache, season, awards,20);
        } else {
            awards = (JSONArray) obj;
        }
        return awards;
    }


    public static JSONArray GetAwardsByEventAPI(String eventCode, int season) {
        JSONObject data = FTCAPI.GetJSONC("http://ftc-api.firstinspires.org/v2.0/" + season + "/awards/" + eventCode);
        JSONArray awards = data.getJSONArray("awards");
        return awards;
    }

    public static JSONArray getAllianceDetailsAPI(String eventCode, int season) {
        JSONObject data = FTCAPI.GetJSONC("http://ftc-api.firstinspires.org/v2.0/" + season + "/alliances/" + eventCode);
        JSONArray alliances = data.getJSONArray("alliances");
        return alliances;
    }


    public static JSONArray getAllianceSelectionAPI(String eventCode, int season) {
        JSONObject data = FTCAPI.GetJSONC("http://ftc-api.firstinspires.org/v2.0/" + season + "/alliances/" + eventCode + "/selection");
        JSONArray alliances = data.getJSONArray("selections");
        return alliances;
    }

    public static JSONArray GetAwardsByTeamAtEventAPI(String eventCode, int teamCode, int season) {
        try {
            JSONObject data = FTCAPI.GetJSONC("http://ftc-api.firstinspires.org/v2.0/" + season + "/awards/" + eventCode + "/" + teamCode);
            JSONArray awards = data.getJSONArray("awards");
            return awards;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JSONObject getSeasonDataAPI(int season) {
        try {
            JSONObject data = FTCAPI.GetJSONC("http://ftc-api.firstinspires.org/v2.0/" + season);
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JSONArray GetAwardsByTeamAPI(int teamCode, int season) {
        try {
            JSONObject data = FTCAPI.GetJSONC("http://ftc-api.firstinspires.org/v2.0/" + season + "/awards/" + teamCode);
            JSONArray awards = data.getJSONArray("awards");
            return awards;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void GetAward(EmbedBuilder eb, int season, boolean inline) {
        JSONArray awards = GetAwardAPI(season);

        String out = "";
        for (int i = 0; i < awards.length(); i++) {
            JSONObject award = awards.getJSONObject(i);
            out = out + award.get("name");

            Object description = award.get("description");
            if (NotEmpty(description)) {
                out = out + "\n" + description;
            }

            out = out + "\n\n";
        }

        eb.setDescription(out.substring(0, 4095));

    }

    public static JSONArray getMatchResultsAPI(String eventCodeStringMatchResults, int seasonMatchResultsInt, int teamNumberMatchResultsInt) {


        JSONObject data = FTCAPI.GetJSONC("http://ftc-api.firstinspires.org/v2.0/" + seasonMatchResultsInt + "/matches/" + eventCodeStringMatchResults + "?teamNumber=" + teamNumberMatchResultsInt);
        JSONArray matches = data.getJSONArray("matches");
        return matches;
    }

    public static JSONArray getMatchScheduleAPI(String eventCode, int season, int teamNumber){
        JSONObject data = FTCAPI.GetJSONC("http://ftc-api.firstinspires.org/v2.0/" + season + "/schedule/" + eventCode+"?TeamNumber="+teamNumber);
        JSONArray matches = data.getJSONArray("schedule");
        return matches;

    }


}
