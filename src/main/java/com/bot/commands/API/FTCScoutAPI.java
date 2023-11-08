package com.bot.commands.API;

import com.bot.FTCHelperBot;
import net.dv8tion.jda.api.EmbedBuilder;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.zone.ZoneRules;
import java.util.*;
import java.util.zip.GZIPInputStream;

import static com.bot.commands.API.FTCAPI.addField;

public class FTCScoutAPI {
    // CACHES
    public static Map<String, JSONObject> teamSearchByName = new HashMap<>();
    public static Map<String, JSONObject> teamStatsByNumber = new HashMap<>();
    public static Map<String, JSONObject> todaysEvents = new HashMap<>();
    public static Map<String, JSONObject> eventRank = new HashMap<>();
    public static Map<String, JSONObject> eventsSearch = new HashMap<>();

    // OTHER STUFF
    public static final String GRAPHQL_ENDPOINT = "https://api.ftcscout.org/graphql";

    public static OkHttpClient httpClient;

    public FTCScoutAPI() {
        this.httpClient = new OkHttpClient.Builder()
                .addInterceptor(new GzipRequestInterceptor())
                .build();
    }

    public static JSONObject sendGraphQLRequest(String query) {

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("query", query);

            MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(jsonBody.toString(), mediaType);

            Request request = new Request.Builder()
                    .url(GRAPHQL_ENDPOINT)
                    .post(requestBody)
                    .addHeader("Accept-Encoding", "gzip") // We specify that we accept gzip-encoded responses
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .addHeader("Connection", "keep-alive")
                    .addHeader("DNT", "1")
                    .addHeader("Origin", "https://api.ftcscout.org")
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    JSONObject responseData = readResponseDataAsJson(response);

                    return responseData;
                } else {
                    return null;
                }
            } catch (Exception e) {
                return null;
            }

    }
    public static JSONObject getBestName(){
        FTCScoutAPI api = new FTCScoutAPI();

        String graphqlQuery = "query {\n" +
                "  getBestName {\n" +
                "    id\n" +
                "    team1 {\n" +
                "      number\n" +
                "      name\n" +
                "    }\n" +
                "    team2 {\n" +
                "      number\n" +
                "      name\n" +
                "    }\n" +
                "  }\n" +
                "}\n";
        JSONObject response = api.sendGraphQLRequest(graphqlQuery);
        return  response;
    }

    public static JSONObject voteBestName(int id, int vote){
        FTCScoutAPI api = new FTCScoutAPI();

        String graphqlQuery = "mutation {\n" +
                "  voteBestName(id: "+id+", vote: "+vote+") {\n" +
                "    id\n" +
                "    team1 {\n" +
                "      number\n" +
                "      name\n" +
                "    }\n" +
                "    team2 {\n" +
                "      number\n" +
                "      name\n" +
                "    }\n" +
                "  }\n" +
                "}";
        JSONObject response = api.sendGraphQLRequest(graphqlQuery);
        return  response;
    }

    public static JSONObject teamSearchByNameRequest(String query){
        JSONObject chachedResponse = teamSearchByName.get(query);
        if (chachedResponse != null) {
            return chachedResponse;
        } else {
            FTCScoutAPI api = new FTCScoutAPI();
            String graphQlQuery = ("query{\n" +
                    "  teamsSearch(\n" +
                    "\tsearchText: \"<>\"\n" +
                    "\tregion: ALL\n" +
                    "  limit:5\n" +
                    "  ){\n" +
                    "  name \n" +
                    "  number\n" +
                    "  schoolName\n" +
                    "  country\n" +
                    "    stateOrProvince\n" +
                    "    city\n" +
                    "  }\n" +
                    "}").replace("<>", query);

            JSONObject response = api.sendGraphQLRequest(graphQlQuery);

            if (response != null) {
            teamSearchByName.put(query, response);

                if (teamSearchByName.size() > 10) {
                    // If the map size exceeds the maximum, remove the oldest entry (first inserted).
                    String oldestKey = teamSearchByName.keySet().iterator().next();
                    teamSearchByName.remove(oldestKey);
                }
            }

            return response;
        }
    }

    public static JSONObject todaysEvents(){
        Date date = new Date();
        ZoneId pacificTimezone = ZoneId.of("America/Los_Angeles");
        ZonedDateTime utcDateTime = ZonedDateTime.now(ZoneId.of("UTC"));

        ZoneRules zoneRules = pacificTimezone.getRules();
        ZoneOffset currentOffset = zoneRules.getOffset(utcDateTime.toInstant());

        ZoneId targetTimezone = currentOffset == ZoneOffset.ofHours(-8) ? ZoneId.of("America/Los_Angeles") : ZoneId.of("America/Los_Angeles");

        ZonedDateTime pdtDateTime = utcDateTime.withZoneSameInstant(targetTimezone);

        DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        String pdtTimestamp = pdtDateTime.format(formatterDate);
        JSONObject cachedResponse = todaysEvents.get(pdtTimestamp);
        if (cachedResponse != null){
            return cachedResponse;
        } else {
            FTCScoutAPI api = new FTCScoutAPI();
            String query = "query {\n" +
                    "  eventsOnDate(date: null, type: Competition) {\n" +
                    "    name \n" +
                    "    location{\n" +
                    "      city\n" +
                    "      state \n" +
                    "      country\n" +
                    "    }\n" +
                    "    address\n" +
                    "    code\n" +
                    "  }\n" +
                    "}\n";
            JSONObject response = api.sendGraphQLRequest(query);
                if (todaysEvents.size() == 1) {
                    // If the map size exceeds the maximum, remove the oldest entry (first inserted).
                    String oldestKey = todaysEvents.keySet().iterator().next();
                    todaysEvents.remove(oldestKey);
                }
                todaysEvents.put(pdtTimestamp, response);

                 return response;


        }

    }

    public static JSONObject eventRankRequest(int season, String code){
        String query = "query{\n" +
                "  \n" +
                "  eventByCode(season: "+season+",code: "+code+") {\n" +
                "    teams {\n" +
                "      team {\n" +
                "        number\n" +
                "        name\n" +
                "      }\n" +
                "      stats {\n" +
                "        ...on TeamEventStats"+season+" {\n" +
                "          rank\n" +
                "          \n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";


        JSONObject cachedResponse = eventRank.get(code);
        if (cachedResponse != null){
            return cachedResponse;
        } else {
            FTCScoutAPI api = new FTCScoutAPI();
            JSONObject response = api.sendGraphQLRequest(query);

            if (response != null) {
                eventRank.put(code, response);

                if (eventRank.size() > 10) {
                    // If the map size exceeds the maximum, remove the oldest entry (first inserted).
                    String oldestKey = eventRank.keySet().iterator().next();
                    eventRank.remove(oldestKey);
                }
            }

            return response;
        }
    }


    private static JSONObject readResponseDataAsJson(Response response) throws IOException {
        if (response.header("Content-Encoding", "").equals("gzip")) {
            try (GZIPInputStream gzipInputStream = new GZIPInputStream(response.body().byteStream())) {
                String responseData = new String(gzipInputStream.readAllBytes());
                return new JSONObject(responseData);
            }
        } else {
            String responseData = response.body().string();
            return new JSONObject(responseData);
        }
    }

    private static class GzipRequestInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException, IOException {
            Request originalRequest = chain.request();
            Request compressedRequest = originalRequest.newBuilder()
                    .header("Accept-Encoding", "gzip")
                    .build();
            return chain.proceed(compressedRequest);
        }
    }

    private static DecimalFormat df = new DecimalFormat("#.####");

    private static String round(Object o) {
        Number n = (Number) o;
        df.setRoundingMode(RoundingMode.FLOOR);
        Double d = n.doubleValue();
        return df.format(d);
    }

    public static JSONObject teamStatsByNumberRequest(int team, int season){
        JSONObject cachedResponse = teamStatsByNumber.get(team+": "+season);
        if (cachedResponse != null) {
            return cachedResponse;
        } else {
            FTCScoutAPI api = new FTCScoutAPI();
            String graphQlQuery = ("query {\n" +
                    "  teamByNumber(number: "+team+") {\n" +
                    "    number\n" +
                    "    name\n" +
                    "    events(season: "+season+") {\n" +
                    "      eventCode\n" +
                    "      teamNumber\n" +
                    "      stats {\n" +
                    "        ... on TeamEventStats"+season+" {\n" +
                    "          opr {\n" +
                    "       autoPoints\n" +
                    "            dcPoints\n" +
                    "            totalPoints\n" +
                    "            totalPointsNp" +
                    "          }\n" +
                    "        }\n" +
                    "      }\n" +
                    "    }\n" +
                    "  }\n" +
                    "}");

            JSONObject response = api.sendGraphQLRequest(graphQlQuery);

            if (response != null) {
                teamStatsByNumber.put(team+":"+season, response);

                if (teamStatsByNumber.size() > 10) {
                    // If the map size exceeds the maximum, remove the oldest entry (first inserted).
                    String oldestKey = teamStatsByNumber.keySet().iterator().next();
                    teamStatsByNumber.remove(oldestKey);
                }
            }

            return response;
        }
    }


    public static void getTeamStatsByNumberEmbed(EmbedBuilder eb, JSONArray events, int index, boolean eventMode, boolean inline) {
        JSONObject eventdata = events.getJSONObject(index);
        if (eventdata == null) {
        }
        addField(eb, "Team: ", eventdata.get("teamNumber"), inline);
        JSONObject stats = null;
        try {
             stats = eventdata.getJSONObject("stats");
        } catch (JSONException e) {
        }
        JSONObject opr = null;
        if (stats != null) {
             opr = stats.getJSONObject("opr");
        }
        ArrayList<Integer> allopr = new ArrayList<>();
        ArrayList<Integer> allauto = new ArrayList<>();
        int topopr = 0;
        for (int i = 0; i < events.length(); i++) {
            if (events.getJSONObject(i).isNull("stats")) {
                continue;
            }
            allopr.add(events.getJSONObject(i).getJSONObject("stats").getJSONObject("opr").getInt("totalPointsNp"));
            allauto.add(events.getJSONObject(i).getJSONObject("stats").getJSONObject("opr").getInt("autoPoints"));
            int o = 0;
            try {
                o = events.getJSONObject(i).getJSONObject("stats").getJSONObject("opr").getInt("totalPointsNp");
                topopr = o > topopr ? o : topopr;
            } catch (JSONException e) {
            }

        }

        // Calculate the sum of all elements
        int sumAllOpr = 0;
        for (int num : allopr) {
            sumAllOpr += num;
        }

        int sumAllAuto = 0;
        for (int num : allauto) {
            sumAllAuto += num;
        }
        // TODO: Add LD (League Domination)
        if (allopr.size() == 0) {
            addField(eb, "Cannot calculate average opr", "no data", inline);
        } else {
            addField(eb, ":robot: Average Auto Points: ", round(sumAllAuto / allauto.size()), inline);
            addField(eb, ":bar_chart: Average OPR: ", round(sumAllOpr / allopr.size()), inline);
            addField(eb, ":fire: Top OPR: ", round(topopr), inline);
        }

        if (eventMode) {
            addField(eb, "Event Code: ", eventdata.get("eventCode"), inline);
            if (eventdata.isNull("stats")) {
                addField(eb, "Event OPR: ", "No Data", inline);
            } else {
                addField(eb, "Event OPR: ", round(opr.get("totalPointsNp")), inline);
            }
            int length = events.length();
            eb.setFooter("Page " + (index + 1) + "/" + length);
        }
    }

        public static JSONObject eventsSearchRequest(String query, int season) {
        JSONObject cachedResponse = eventsSearch.get(query);
        if (cachedResponse != null){
            return cachedResponse;
        } else {
            FTCScoutAPI api = new FTCScoutAPI();
            String graphQlQuery = "query{\n" +
                    "  eventsSearch(\n" +
                    "    searchText: \"<>\"\n" +
                    "    limit: 5\n" +
                    "    onlyWithMatches: false\n" +
                    "    season: %%\n" +
                    "    region: ALL\n" +
                    "    eventTypes: TRAD_AND_REMOTE\n" +
                    "  ){\n" +
                    "    name\n" +
                    "    venue\n" +
                    "    address\n" +
                    "    city\n" +
                    "    stateOrProvince\n" +
                    "    country\n" +
                    "    code\n" +
                    "  }\n" +
                    "\n" +
                    "}";
            graphQlQuery = graphQlQuery.replace("<>", query);
            graphQlQuery = graphQlQuery.replace("%%", String.valueOf(season));
            JSONObject response = api.sendGraphQLRequest(graphQlQuery);
            if (response != null) {
                eventsSearch.put(query, response);

                if (eventsSearch.size() > 5) {
                    // If the map size exceeds the maximum, remove the oldest entry (first inserted).
                    String oldestKey = eventsSearch.keySet().iterator().next();
                    eventsSearch.remove(oldestKey);
                }
            }
            return response;
        }
        }

    public static JSONObject sortTeamsByRank(JSONObject jsonObject) {
        try {
            // Step 1: Get the "teams" array from the JSONObject
            JSONObject data = jsonObject.getJSONObject("data");
            JSONObject eventByCode = data.getJSONObject("eventByCode");
            JSONArray teamsArray = eventByCode.getJSONArray("teams");

            // Step 2: Implement custom sorting based on the "rank" value
            for (int i = 0; i < teamsArray.length() - 1; i++) {
                for (int j = i + 1; j < teamsArray.length(); j++) {
                    JSONObject team1 = teamsArray.getJSONObject(i);
                    JSONObject team2 = teamsArray.getJSONObject(j);

                    // Retrieve rank values
                    int rank1 = team1.getJSONObject("stats").optInt("rank", Integer.MAX_VALUE);
                    int rank2 = team2.getJSONObject("stats").optInt("rank", Integer.MAX_VALUE);

                    // Swap teams if needed
                    if (rank1 > rank2) {
                        teamsArray.put(i, team2);
                        teamsArray.put(j, team1);
                    }
                }
            }

            // Step 3: Create a new JSONObject with the sorted "teams" array and return it
            JSONObject sortedJSONObject = new JSONObject();
            JSONObject sortedData = new JSONObject();
            JSONObject sortedEventByCode = new JSONObject();

            sortedEventByCode.put("teams", teamsArray);
            sortedData.put("eventByCode", sortedEventByCode);
            sortedJSONObject.put("data", sortedData);

            return sortedJSONObject;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null; // Return null if any errors occur
    }









    // TESTING

    public static void main(String[] args) {
        FTCScoutAPI api = new FTCScoutAPI();

//        String query = "query GetTeamByNumber {\n" +
//                "  teamByNumber(number: 13190) {\n" +
//                "    name\n" +
//                "  }\n" +
//                "}";



        String query = "query {\n" +
                "  teamByNumber(number: 13190) {\n" +
                "    number\n" +
                "    name\n" +
                "    events(season: "+FTCHelperBot.DEFAULT_SEASON+") {\n" +
                "      eventCode\n" +
                "      teamNumber\n" +
                "      stats {\n" +
                "        ... on TeamEventStats"+FTCHelperBot.DEFAULT_SEASON+" {\n" +
                "          opr {\n" +
                "            autoNavigationPoints\n" +
                "            autoNavigationPointsIndividual\n" +
                "            autoConePoints\n" +
                "            autoTerminalPoints\n" +
                "            autoGroundPoints\n" +
                "            autoLowPoints\n" +
                "            autoMediumPoints\n" +
                "            autoHighPoints\n" +
                "            dcTerminalPoints\n" +
                "            dcGroundPoints\n" +
                "            dcLowPoints\n" +
                "            dcMediumPoints\n" +
                "            dcHighPoints\n" +
                "            endgameNavigationPoints\n" +
                "            endgameNavigationPointsIndividual\n" +
                "            ownershipPoints\n" +
                "            coneOwnershipPoints\n" +
                "            beaconOwnershipPoints\n" +
                "            circuitPoints\n" +
                "            autoPoints\n" +
                "            dcPoints\n" +
                "            endgamePoints\n" +
                "            penaltyPoints\n" +
                "            totalPoints\n" +
                "            totalPointsNp\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";



        JSONObject response = api.sendGraphQLRequest(query);
        System.out.println(response);
    }

}