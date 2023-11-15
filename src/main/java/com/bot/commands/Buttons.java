package com.bot.commands;

import com.bot.commands.API.FTCAPI;
import com.bot.commands.API.FTCScoutAPI;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import static com.bot.FTCHelperBot.*;
import static com.bot.commands.API.GoogleMapsAPI.directionsUrl;
import static net.dv8tion.jda.api.entities.emoji.Emoji.fromUnicode;

public class Buttons extends ListenerAdapter {

    EmbedBuilder eb = new EmbedBuilder();

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {

        String[] id = event.getComponentId().split(":"); // this is the custom id we specified in our button

        String type = id[1];
        FTCScoutAPI ftcScoutAPI = new FTCScoutAPI();


        // Check that the button is for the user that clicked it, otherwise just ignore the event (let interaction fail)

        //event.deferEdit().queue(); // acknowledge the button was clicked, otherwise the interaction will fail

        switch (type) {
            case "event": {
                String userId = id[0];
                String eventsAdvancedGet = id[6];
                Boolean eventsAdvanced;
                if (eventsAdvancedGet.equals("true")) {
                    eventsAdvanced = true;
                } else {
                    eventsAdvanced = false;
                }
                if (!userId.equals(event.getUser().getId()))
                    return;
                int index = Integer.parseInt(id[2]);
                String direction = id[3];
                int team = Integer.parseInt(id[4]);
                int length = Integer.parseInt(id[5]);

                if (direction.equals("left")) {
                    if (index == 0) {

                    } else {
                        index = index - 1;
                    }
                } else {
                    if (index == length - 1) {

                    } else {
                        index = index + 1;
                    }
                }


                eb.setTitle("Events for team " + team);
                eb.setColor(MAIN_COLOR);
                eb.setAuthor("FTC Helper Bot", event.getJDA().getSelfUser().getAvatarUrl(), event.getJDA().getSelfUser().getAvatarUrl());

                FTCAPI.GetEvent(eb, team, index, INLINE, DEFAULT_SEASON, eventsAdvanced);

                //Message message = event.getMessage();


                //message.editMessage(MessageEditRequest.new());
                event.editMessage(event.getMessage().getContentDisplay())
                        .setEmbeds(eb.build())
                        .setActionRow(
                                Button.primary(userId + ":event:" + index + ":left:" + team + ":" + length + ":" + eventsAdvanced, Emoji.fromUnicode("⬅")),
                                Button.danger(event.getUser().getId() + ":delete", fromUnicode("\uD83D\uDDD1\uFE0F")),
                                Button.primary(userId + ":event:" + index + ":right:" + team + ":" + length + ":" + eventsAdvanced, Emoji.fromUnicode("➡"))
                        )
                        .queue();
                break;
            }
            case "results":
                int indexResults = Integer.parseInt(id[2]);
                String directionResults = id[3];
                int teamResults = Integer.parseInt(id[4]);
                int lengthResults = Integer.parseInt(id[5]);
                String eventCodeStringMatchResults = id[6];
                int seasonMatchResultsInt = Integer.parseInt(id[7]);

                JSONArray matchResults = FTCAPI.getMatchResultsAPI(eventCodeStringMatchResults, seasonMatchResultsInt, teamResults);

                int numberOfTermsResults = indexResults * lengthResults;
                int howManyMoreTermsResults = numberOfTermsResults + lengthResults;

                if (directionResults.equals("left")) {
                    if (indexResults == 0) {

                    } else {
                        indexResults = indexResults - 1;
                        numberOfTermsResults = indexResults * lengthResults;
                        howManyMoreTermsResults = numberOfTermsResults + lengthResults;
                    }
                } else {
                    if (matchResults.length() <= howManyMoreTermsResults) {
                        howManyMoreTermsResults = matchResults.length();
                    } else {
                        indexResults = indexResults + 1;
                        numberOfTermsResults = indexResults * lengthResults;
                        howManyMoreTermsResults = numberOfTermsResults + lengthResults;
                    }
                }


                eb.clear();
                eb.setDescription("Event Code : " + eventCodeStringMatchResults + " | Season : " + seasonMatchResultsInt + " | Team : " + teamResults);
                eb.setTitle("FTC Match Results");
                eb.setColor(MAIN_COLOR);
                eb.setAuthor("FTC Helper Bot", event.getJDA().getSelfUser().getAvatarUrl(), event.getJDA().getSelfUser().getAvatarUrl());
                eb.setFooter("Powered by FTC API", "https://www.firstinspires.org/sites/default/files/uploads/resource_library/brand/thumbnails/FIRST-Icon.png");

                System.out.println("On term: " + numberOfTermsResults + " to " + howManyMoreTermsResults + " out of " + matchResults.length() + " terms");
                for (int i = numberOfTermsResults; i < howManyMoreTermsResults && i < matchResults.length(); i++) {
                    JSONObject matchResultsObject = matchResults.getJSONObject(i);
                    // Get match data
                    String actualStartTime = matchResultsObject.getString("actualStartTime");
                    String description = matchResultsObject.getString("description");
                    String tournamentLevel = matchResultsObject.getString("tournamentLevel");
                    int series = matchResultsObject.getInt("series");
                    int matchNumber = matchResultsObject.getInt("matchNumber");
                    int scoreRedFinal = matchResultsObject.getInt("scoreRedFinal");
                    int scoreRedFoul = matchResultsObject.getInt("scoreRedFoul");
                    int scoreRedAuto = matchResultsObject.getInt("scoreRedAuto");
                    int scoreBlueFinal = matchResultsObject.getInt("scoreBlueFinal");
                    int scoreBlueFoul = matchResultsObject.getInt("scoreBlueFoul");
                    int scoreBlueAuto = matchResultsObject.getInt("scoreBlueAuto");
                    String postResultTime = matchResultsObject.getString("postResultTime");
                    String modifiedOn = matchResultsObject.getString("modifiedOn");
                    // Teams
                    StringBuilder resultsTeams = new StringBuilder();
                    JSONArray teamsMatchResults = matchResultsObject.getJSONArray("teams");
                    for (int a = 0; a < teamsMatchResults.length(); a++) {
                        int ResultsteamNumber = teamsMatchResults.getJSONObject(a).getInt("teamNumber");
                        String ResultsteamStation = teamsMatchResults.getJSONObject(a).getString("station");
                        if (ResultsteamNumber == teamResults) {
                            resultsTeams.append(":star: > ").append(ResultsteamNumber).append(" - ").append(ResultsteamStation).append("\n");
                        } else {

                            resultsTeams.append(ResultsteamNumber).append(" - ").append(ResultsteamStation).append("\n");
                        }
                    }

                    eb.addField("Match " + matchNumber + " - " + description, "", false);
                    eb.addField(":busts_in_silhouette: Teams", resultsTeams.toString(), false);
                    // Find Winner
                    if (scoreRedFinal > scoreBlueFinal)
                        eb.addField("Match " + matchNumber + " - Score :scales: ", ":medal: :red_circle: Red: `" + scoreRedFinal + "` :blue_circle: Blue: `" + scoreBlueFinal + "`", true);
                    else if (scoreRedFinal < scoreBlueFinal)
                        eb.addField("Match " + matchNumber + " - Score :scales: ", ":red_circle: Red: `" + scoreRedFinal + "` :blue_circle: Blue: `" + scoreBlueFinal + "` :medal:", true);
                    else if (scoreRedFinal == scoreBlueFinal)
                        eb.addField("Match " + matchNumber + " - Score :scales: ", ":red_circle: Red: `" + scoreRedFinal + "` :blue_circle: Blue: `" + scoreBlueFinal + "`", true);
                }


                event.editMessage(event.getMessage().getContentDisplay())
                        .setEmbeds(eb.build())
                        .setActionRow(
                                Button.primary(eventCodeStringMatchResults + ":results:" + indexResults + ":left:" + teamResults + ":" + lengthResults + ":" + eventCodeStringMatchResults + ":" + seasonMatchResultsInt, Emoji.fromUnicode("⬅")),
                                Button.danger(event.getUser().getId() + ":delete", fromUnicode("\uD83D\uDDD1\uFE0F")),
                                Button.primary(eventCodeStringMatchResults + ":results:" + indexResults + ":right:" + teamResults + ":" + lengthResults + ":" + eventCodeStringMatchResults + ":" + seasonMatchResultsInt, Emoji.fromUnicode("➡"))
                        ).queue();
                break;
            case "schedule":


                //                                    Button.primary(eventCodeStringMatchSchedule + ":schedule:0:right:"+teamNumberMatchScheduleInt+":"+lengthMatchSchedule+":"+seasonMatchScheduleInt, Emoji.fromUnicode("➡"))


                int indexSchedule = Integer.parseInt(id[2]);
                String directionSchedule = id[3];
                int teamSchedule = Integer.parseInt(id[4]);
                int lengthSchedule = Integer.parseInt(id[5]);
                String eventCodeStringMatchSchedule = id[0];
                int seasonMatchScheduleInt = Integer.parseInt(id[6]);

                int numberOfTermsSchedule = indexSchedule * lengthSchedule;
                int howManyMoreTermsSchedule = numberOfTermsSchedule + lengthSchedule;


                JSONArray matchSchedule = FTCAPI.getMatchScheduleAPI(eventCodeStringMatchSchedule, seasonMatchScheduleInt, teamSchedule);

                if (directionSchedule.equals("left")) {
                    if (indexSchedule == 0) {
                        indexSchedule = 0;
                        numberOfTermsSchedule = 0;
                        howManyMoreTermsSchedule = numberOfTermsSchedule + lengthSchedule;
                    } else {
                        indexSchedule = indexSchedule - 1;
                        numberOfTermsSchedule = indexSchedule * lengthSchedule;
                        howManyMoreTermsSchedule = numberOfTermsSchedule + lengthSchedule;
                    }
                } else {
                    if (matchSchedule.length() <= howManyMoreTermsSchedule) {
                        howManyMoreTermsSchedule = matchSchedule.length();
                    } else {
                        indexSchedule = indexSchedule + 1;
                        numberOfTermsSchedule = indexSchedule * lengthSchedule;
                        howManyMoreTermsSchedule = numberOfTermsSchedule + lengthSchedule;
                    }
                }


                eb.clear();
                eb.setTitle("FTC Match Schedule");
                eb.setColor(MAIN_COLOR);
                eb.setAuthor("FTC Helper Bot", event.getJDA().getSelfUser().getAvatarUrl(), event.getJDA().getSelfUser().getAvatarUrl());
                eb.setFooter("Powered by FTC API", "https://www.firstinspires.org/sites/default/files/uploads/resource_library/brand/thumbnails/FIRST-Icon.png");
                eb.setDescription("Event Code : " + eventCodeStringMatchSchedule + " | Season : " + seasonMatchScheduleInt + " | Team : " + teamSchedule);
                for (int i = numberOfTermsSchedule; i < howManyMoreTermsSchedule && i < matchSchedule.length(); i++) {
                    JSONObject matchObject = matchSchedule.getJSONObject(i);
                    String description = matchObject.getString("description");
                    int field = matchObject.getInt("field");
                    JSONArray teams = matchObject.getJSONArray("teams");
                    String startTime = matchObject.getString("startTime");
                    int matchNumber = matchObject.getInt("matchNumber");

                    StringBuilder teamsString = new StringBuilder();
                    for (int a = 0; a < teams.length(); a++) {
                        int teamNumberTeamsSchedule = teams.getJSONObject(a).getInt("teamNumber");
                        String station = teams.getJSONObject(a).getString("station");
                        teamsString.append(teamNumberTeamsSchedule).append(" - ").append(station).append("\n");
                    }
                    eb.addField("Match " + matchNumber + " - " + description, "", false);
                    eb.addField(":busts_in_silhouette: Teams", teamsString.toString(), false);
                }

                event.editMessage(event.getMessage().getContentDisplay())
                        .setEmbeds(eb.build())
                        .setActionRow(
                                Button.primary(eventCodeStringMatchSchedule + ":schedule:" + indexSchedule + ":left:" + teamSchedule + ":" + lengthSchedule + ":" + seasonMatchScheduleInt, Emoji.fromUnicode("⬅")),
                                Button.danger(event.getUser().getId() + ":delete", fromUnicode("\uD83D\uDDD1\uFE0F")),
                                Button.primary(eventCodeStringMatchSchedule + ":schedule:" + indexSchedule + ":right:" + teamSchedule + ":" + lengthSchedule + ":" + seasonMatchScheduleInt, Emoji.fromUnicode("➡"))
                        ).queue();


                break;

            case "delete":
                if (event.getUser().getId().equals(id[0]) || event.getMember().hasPermission(Permission.MESSAGE_MANAGE)) {
                    event.deferEdit().queue();
                    event.getMessage().delete().queue();
                    System.out.println("Deleted message " + event.getMessage().getId());
                } else {
                    event.getHook().sendMessage("You can't delete this message!").setEphemeral(true).queue();
                }
                break;


            case "league-rank": {
               int currentIndex = Integer.parseInt(id[2]);
                String userId = id[0];
                String direction = id[3];
                int season = Integer.parseInt(id[5]);
                int length = Integer.parseInt(id[4]);
                String regionCode = id[6];
                String leagueCode = id[7];


                if (direction.equals("right")){
                    // check if it goes over length. Each time the index is +10 and if it its uneven
                    // the page will have less than 10 elements
                    if (length - (currentIndex+10) <= -10){
                    } else {
                        currentIndex = currentIndex + 10;
                    }


                } else {
                if (currentIndex - 10 <= 0){

                } else {
                    currentIndex = currentIndex - 10;
                }
                }

                int firstIndex = currentIndex - 10;


                int Newlength = FTCAPI.GetLeagueRank(eb, regionCode,leagueCode,INLINE,season,firstIndex,currentIndex);
                eb.setTitle("League ranking for " + regionCode + " " + leagueCode);
                eb.setColor(MAIN_COLOR);
                eb.setAuthor("FTC Helper Bot", event.getJDA().getSelfUser().getAvatarUrl(), event.getJDA().getSelfUser().getAvatarUrl());
        event.editMessage(event.getMessage().getContentDisplay())
                .setEmbeds(eb.build())
                .setActionRow(
                        Button.primary(userId + ":league-rank:"+currentIndex+":left:"+ length + ":"+season+":"+regionCode+":"+leagueCode, fromUnicode("⬅")),
                        Button.danger(userId + ":delete", fromUnicode("\uD83D\uDDD1\uFE0F")),
                        Button.primary(userId + ":league-rank:"+currentIndex+":right:"+ length + ":"+season+":"+regionCode+":"+leagueCode, fromUnicode("➡"))
                ) .queue();
                break;
            }



            case "event-rank": {
               //                                    Button.primary(userId + ":event-rank:10:right:" + code + ":" + season+":"+length, fromUnicode("➡"))
                int currentIndex = Integer.parseInt(id[2]);
                String userId = id[0];
                String direction = id[3];
                String code = id[4];
                int season = Integer.parseInt(id[5]);
                int length = Integer.parseInt(id[6]);
                int index = Integer.parseInt(id[2]);

                // 10 items per page and index is how many already

                if (direction.equals("right")) {
                   if (index+10>length){
                          index = length;
                     } else {
                          index = index + 10;
                   }
                } else {
                    // left
                    if (index-10<=0){

                    } else {
                        index = index - (index % 10 == 0 ? 10 : index % 10);
                    }
                }
                int firstIndex;
                if (index - 10 <= 0) {
                    firstIndex = 0;
                } else {
                    firstIndex = index - (index % 10 == 0 ? 10 : index % 10);
                }

                JSONObject response = ftcScoutAPI.eventRankRequest(season,code);
                JSONObject sortedResponse = FTCScoutAPI.sortTeamsByRank(response);

                JSONObject data = sortedResponse.getJSONObject("data");
                JSONObject eventByCode = data.getJSONObject("eventByCode");
                JSONArray teams = eventByCode.getJSONArray("teams");

                eb.clear();

                eb.setTitle("Event ranking for " + code);
                eb.setColor(MAIN_COLOR);

                for (int i = firstIndex; i < index; i++) {
                    JSONObject teamFull = teams.getJSONObject(i);
                    JSONObject team = teamFull.getJSONObject("team");
                    JSONObject stats = teamFull.getJSONObject("stats");
                    String teamNumber = String.valueOf(team.get("number"));
                    String nameTeam = String.valueOf(team.get("name"));
                    int rank = stats.getInt("rank");
                    eb.addField("Rank: " + rank, teamNumber + "  " + nameTeam, INLINE);
                }
                eb.setAuthor(BOT_NAME, event.getJDA().getSelfUser().getAvatarUrl(), event.getJDA().getSelfUser().getAvatarUrl());

                event.editMessage(event.getMessage().getContentDisplay())
                        .setEmbeds(eb.build())
                        .setActionRow(
                                Button.primary(userId + ":event-rank:"+index+":left:" + code + ":" + season+":"+length, fromUnicode("⬅")),
                                Button.danger(userId + ":delete", fromUnicode("\uD83D\uDDD1\uFE0F")),
                                Button.primary(userId + ":event-rank:"+index+":right:" + code + ":" + season+":"+length, fromUnicode("➡"))
                        ) .queue();
                break;
            }

            case "team-search":{
               // Button.primary(event.getUser().getId() + ":team-search:0:left:"+queryStr+":"+ responseteamCount, fromUnicode("⬅")),

                int currentIndex = Integer.parseInt(id[2]);
                String userId = id[0];
                String queryStr = id[4];
                String direction = id[3];
                int responseteamCount = Integer.parseInt(id[5]);



                JSONObject response = ftcScoutAPI.teamSearchByNameRequest(queryStr);
                JSONObject data = response.getJSONObject("data");
                JSONArray teams = data.getJSONArray("teamsSearch");



           if (direction.equals("right")) {
               if (currentIndex + 1 >= responseteamCount) {
                   currentIndex = responseteamCount - 1;
               } else {
                currentIndex++;
               }
           } else {
               if (currentIndex - 1 < 0) {
                currentIndex = 0;
               } else {
                currentIndex--;
               }
           }
            eb.clear();
              JSONObject team = teams.getJSONObject(currentIndex);
                String teamName = team.getString("name");
                    int teamNumber = team.getInt("number");
                    String teamSchoolName = team.getString("schoolName");
                    JSONObject teamLocationJSON = team.getJSONObject("location");
                    String teamCountry = teamLocationJSON.getString("country");
                    String teamState = teamLocationJSON.getString("state");
                    String teamCity = teamLocationJSON.getString("city");
                    String teamLocation = teamCity + ", " + teamState + ", " + teamCountry;

                    eb.setTitle("Team Search Results for query: " + queryStr);
                    eb.addField("Number", String.valueOf(teamNumber), false);
                    eb.addField("Name", teamName, false);
                    eb.addField("School Name", teamSchoolName, false);
                    eb.addField("Location", teamLocation, false);
                    eb.setColor(MAIN_COLOR);
                    eb.setFooter("Page " + (currentIndex+1) + "/" + responseteamCount, null);
                eb.setAuthor(BOT_NAME, event.getJDA().getSelfUser().getAvatarUrl(), event.getJDA().getSelfUser().getAvatarUrl());

                event.editMessage(event.getMessage().getContentDisplay())
                        .setEmbeds(eb.build())
                            .setActionRow(
                                    Button.primary(event.getUser().getId() + ":team-search:"+currentIndex+":left:"+queryStr+":"+ responseteamCount, fromUnicode("⬅")),
                                    Button.danger(event.getUser().getId() + ":delete", fromUnicode("\uD83D\uDDD1\uFE0F")),
                                    Button.primary(event.getUser().getId() + ":team-search:"+currentIndex+":right:"+queryStr+":"+responseteamCount, fromUnicode("➡"))
                                    )
                            .queue();
                break;


            }

            case "todays-events": {
                //Button.primary(event.getUser().getId() + ":todays-events:0:right:" + todaysEventCount, fromUnicode("➡"))
                int currentIndex = Integer.parseInt(id[2]);
                String userId = id[0];
                String direction = id[3];
                int todaysEventCount = Integer.parseInt(id[4]);

                if (direction.equals("right")) {
                    if (currentIndex + 1 >= todaysEventCount) {
                        currentIndex = todaysEventCount - 1;
                    } else {
                        currentIndex++;
                    }
                } else {
                    if (currentIndex - 1 < 0) {
                        currentIndex = 0;
                    } else {
                        currentIndex--;
                    }
                }

                eb.clear();
                eb.setTitle("Events Today");

                JSONObject response = ftcScoutAPI.todaysEvents();
                JSONObject data = response.getJSONObject("data");
                JSONArray events = data.getJSONArray("eventsOnDate");

                JSONObject eventCurrent = events.getJSONObject(currentIndex);
                JSONObject locationEvent = eventCurrent.getJSONObject("location");

                String eventName = eventCurrent.getString("name");
                String eventCountry = locationEvent.getString("country");
                String eventState = locationEvent.getString("state");
                String eventCity = locationEvent.getString("city");
                String eventLocation = eventCity + ", " + eventState + ", " + eventCountry;
                String eventAddress = eventCurrent.getString("address");
                String eventCode = eventCurrent.getString("code");
                eb.addField("Name", eventName, false);
                eb.addField("Location", eventLocation, false);

                try {
                    eb.addField("Address", "["+eventAddress+"]("+directionsUrl(eventAddress)+")", false);
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
                eb.addField("Code", eventCode, false);
                eb.setFooter("Page " + (currentIndex+1) + "/" + todaysEventCount, null);
                eb.setColor(MAIN_COLOR);

                eb.setAuthor(BOT_NAME, event.getJDA().getSelfUser().getAvatarUrl(), event.getJDA().getSelfUser().getAvatarUrl());
            event.editMessage(event.getMessage().getContentDisplay())
                    .setEmbeds(eb.build())
                        .setActionRow(
                                Button.primary(event.getUser().getId() + ":todays-events:"+currentIndex+":left:"+todaysEventCount, fromUnicode("⬅")),
                                Button.danger(event.getUser().getId() + ":delete", fromUnicode("\uD83D\uDDD1\uFE0F")),
                                Button.primary(event.getUser().getId() + ":todays-events:"+currentIndex+":right:"+todaysEventCount, fromUnicode("➡"))
                                )
                        .queue();
                break;


            }
            case "events-search":{
                int currentIndex = Integer.parseInt(id[2]);
                String direction = id[3];
                String query = id[4];
                int eventSearchCount = Integer.parseInt(id[5]);
                int season = Integer.parseInt(id[6]);

                if (direction.equals("right")) {
                    if (currentIndex + 1 >= eventSearchCount) {
                        currentIndex = eventSearchCount - 1;
                    } else {
                        currentIndex++;
                    }
                } else {
                    if (currentIndex - 1 < 0) {
                        currentIndex = 0;
                    } else {
                        currentIndex--;
                    }
                }
                eb.clear();
                JSONObject response = ftcScoutAPI.eventsSearchRequest(query, season);


                JSONObject data = response.getJSONObject("data");
                JSONArray events = data.getJSONArray("eventsSearch");
                eb.setTitle("Events Search Results for query: " + query);
                // Get data about each event
                JSONObject eventCurrent = events.getJSONObject(currentIndex);
                String eventCurrentName = eventCurrent.getString("name");
                String eventCurrentAddress = eventCurrent.getString("address");
                String eventCurrentCode = eventCurrent.getString("code");

                eb.addField("Name", eventCurrentName, false);
                try {
                    eb.addField("Address", "[" + eventCurrentAddress + "](" + directionsUrl(eventCurrentAddress) + ")", false);
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
                eb.addField("Code", eventCurrentCode, false);
                eb.setColor(MAIN_COLOR);
                eb.setFooter("Page " + (currentIndex + 1) + "/" + eventSearchCount, null);
                eb.setAuthor(BOT_NAME, event.getJDA().getSelfUser().getAvatarUrl(), event.getJDA().getSelfUser().getAvatarUrl());

                event.editMessage(event.getMessage().getContentDisplay())
                        .setEmbeds(eb.build())
                        .setActionRow(
                        Button.primary(event.getUser().getId() + ":events-search:" + currentIndex + ":left:" + query + ":" + eventSearchCount + ":" + season, fromUnicode("⬅")),
                        Button.danger(event.getUser().getId() + ":delete", fromUnicode("\uD83D\uDDD1\uFE0F")),
                        Button.primary(event.getUser().getId() + ":events-search:" + currentIndex + ":right:" + query + ":" + eventSearchCount + ":" + season, fromUnicode("➡"))
                ).queue();

            break;
            }
            case "scout": {
                String userId = id[0];
                int currentIndex = Integer.parseInt(id[2]);
                String direction = id[3];
                int team = Integer.parseInt(id[4]);
                int length = Integer.parseInt(id[5]);
                int season = Integer.parseInt(id[6]);

                if (direction.equals("right")) {
                    if (currentIndex + 1 >= length) {
                        currentIndex = length - 1;
                    } else {
                        currentIndex++;
                    }
                } else {
                    if (currentIndex - 1 < 0) {
                        currentIndex = 0;
                    } else {
                        currentIndex--;
                    }
                }


                eb.clear();
                eb.setTitle("ftcscout.org for team " + team);
                eb.setColor(MAIN_COLOR);

                JSONArray events = ftcScoutAPI.teamStatsByNumberRequest(team,season).getJSONObject("data").getJSONObject("teamByNumber").getJSONArray("events");

                FTCScoutAPI.getTeamStatsByNumberEmbed(eb, events, currentIndex, true, INLINE);

                eb.setFooter("Page " + (currentIndex+1) + "/" + length, null);
                eb.setAuthor(BOT_NAME, event.getJDA().getSelfUser().getAvatarUrl(), event.getJDA().getSelfUser().getAvatarUrl());
                event.editMessage(event.getMessage().getContentDisplay())
                        .setEmbeds(eb.build())
                        .setActionRow(
                                Button.primary(event.getUser().getId() + ":scout:" + currentIndex + ":left:" + team + ":" + length+":"+season, fromUnicode("⬅")),
                                Button.danger(event.getUser().getId() + ":delete", fromUnicode("\uD83D\uDDD1\uFE0F")),
                                Button.primary(event.getUser().getId() + ":scout:" + currentIndex + ":right:" + team + ":" + length+":"+season, fromUnicode("➡"))
                        )
                        .queue();
            }
            case "vote-best-name":{
                String userID = id[0];
                int idToVote = Integer.parseInt(id[2]);
                int numberToVote = Integer.parseInt(id[3]);
                JSONObject bestNameResponse = ftcScoutAPI.voteBestName(idToVote,numberToVote);
                JSONObject data = bestNameResponse.getJSONObject("data");

                JSONObject getBestName = data.getJSONObject("voteBestName");

                int idBestName = getBestName.getInt("id");

                JSONObject team1 = getBestName.getJSONObject("team1");
                int team1Number = team1.getInt("number");
                String team1Name = team1.getString("name");

                JSONObject team2 = getBestName.getJSONObject("team2");
                int team2Number = team2.getInt("number");
                String team2Name = team2.getString("name");

                eb.setTitle(" Vote : The Best Team Name");
                eb.setDescription(team1Name+" or "+team2Name);
                eb.setFooter("Vote between two random names, results will be revealed on a blog post on ftcscout.org","https://user-images.githubusercontent.com/24487638/261329471-2f0034fc-6c5d-48f3-ae66-ac1acf5fff48.png");
                eb.setColor(MAIN_COLOR);
                event.editMessage(event.getMessage().getContentDisplay())
                        .setEmbeds(eb.build())
                        .setActionRow(
                                Button.primary(event.getUser().getId() + ":vote-best-name:"+idBestName+":"+team1Number,team1Name),
                                Button.danger(event.getUser().getId() + ":delete", fromUnicode("\uD83D\uDDD1\uFE0F")),
                                Button.primary(event.getUser().getId() + ":vote-best-name:"+idBestName+":"+team2Number,team2Name)

                        )
                        .queue();
                break;
            }


                default:

                break;
        }



    }
}
