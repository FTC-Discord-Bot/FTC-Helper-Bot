package com.bot.commands;

import com.bot.commands.API.*;
import com.bot.commands.helpers.CommandUserTimeDelay;
import com.bot.commands.helpers.ConvertDate;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.ScheduledEvent;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.bot.FTCHelperBot.*;
import static com.bot.commands.API.GitBookAPI.searchDocs;
import static com.bot.commands.API.GitHubAPI.*;
import static com.bot.commands.API.GoogleMapsAPI.directionsUrl;
import static com.bot.commands.helpers.ConvertTime.convertToRegularTime;
import static com.bot.commands.helpers.URLEncoderHelper.URLEncode;
import static net.dv8tion.jda.api.entities.emoji.Emoji.fromUnicode;


public class BotCommands extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {

      System.gc();

        FTCScoutAPI ftcScoutAPI = new FTCScoutAPI();
        //current milis
        long startCmnd = System.nanoTime();
        EmbedBuilder eb = new EmbedBuilder();

        // Author for Embed
        eb.setAuthor(BOT_NAME, event.getJDA().getSelfUser().getAvatarUrl(), event.getJDA().getSelfUser().getAvatarUrl());


        // Common method to handle command spam check
        // Check if user is spamming command
        String userID = event.getUser().getId();
        InteractionHook eventHook = event.getHook();

        if (!CommandUserTimeDelay.timeCheckUser(userID)) {
            CommandUserTimeDelay.timErrorEmbed(eb);
            event.replyEmbeds(eb.build()).setEphemeral(true).queue();
            return;
        } else {
            CommandUserTimeDelay.addTimeDelay(userID);
        }

        switch (event.getName()) {

            // DEBUG
            case "debug-ping":


                // Directly reply with the ping value
                event.reply("Ping: " + event.getJDA().getGatewayPing() + "ms").queue();
                break;



            // UTILITY

            case "learn-java":



                eb.setTitle("Learn Java");
                eb.setColor(MAIN_COLOR);
                eb.addField(" :notebook_with_decorative_cover: Resources", "[Learn Java for FTC](https://www.google.com/url?sa=t&rct=j&q=&esrc=s&source=web&cd=&ved=2ahUKEwiRvpvhs5n9AhU2l2oFHVeODwoQFnoECA0QAQ&url=https%3A%2F%2Fraw.githubusercontent.com%2Falan412%2FLearnJavaForFTC%2Fmaster%2FLearnJavaForFTC.pdf&usg=AOvVaw0bxMsz-JTgs9FYR0rH1rc0) \n [Intro to FTC Software](https://team-13190.gitbook.io/intro-to-ftc-software/)", false);
                        //event.replyEmbeds(eb.build()).queue();
                event.replyEmbeds(eb.build()).setEphemeral(false)
                        .addActionRow(
                                Button.danger(event.getUser().getId() + ":delete", fromUnicode("\uD83D\uDDD1\uFE0F"))

                        )
                        .queue();

                break;

            // FTC API
            case "team-info": {

                OptionMapping seasonOpt = event.getOption("season");
                OptionMapping teamNumber = event.getOption("teamnumber");
                int season = seasonOpt == null
                        ? DEFAULT_SEASON // default
                        : (int) seasonOpt.getAsLong();
                assert teamNumber != null;
                int team = Objects.requireNonNull(teamNumber).getAsInt();
                eb.setTitle("Info about team " + team, "https://ftc-events.firstinspires.org/team/" + team);
                eb.setColor(MAIN_COLOR);

                if (FTCAPI.GetInfo(eb, team, INLINE, season)){
                                event.replyEmbeds(eb.build()).setEphemeral(true)
                            .addActionRow(
                                    Button.danger(event.getUser().getId() + ":delete", fromUnicode("\uD83D\uDDD1\uFE0F"))
                            )
                            .queue();
                } else {


                                event.replyEmbeds(eb.build()).setEphemeral(false) .addActionRow(
                                    Button.danger(event.getUser().getId() + ":delete", fromUnicode("\uD83D\uDDD1\uFE0F"))
                            )
                            .queue();
                }
            }
            break;
            case "events": {


                event.deferReply().queue();

                OptionMapping teamNumber = event.getOption("teamnumber");
                OptionMapping seasonOPT = event.getOption("season");
                OptionMapping eventsAdvancedOPT = event.getOption("advanced");
                int season = seasonOPT == null
                        ? DEFAULT_SEASON // default
                        : seasonOPT.getAsInt();
                int team = teamNumber.getAsInt();

                Boolean eventsAdvanced = eventsAdvancedOPT == null
                        ? false // default
                        : eventsAdvancedOPT.getAsBoolean();

                eb.setTitle("Events for team " + team);
                eb.setColor(MAIN_COLOR);


                int length = FTCAPI.GetEvent(eb, team, 0, INLINE, season, eventsAdvanced);




                String userId = userID;


                eventHook.sendMessageEmbeds(eb.build())
                        .addActionRow(
                                Button.primary(userId + ":event:0:left:" + team + ":" + length+":"+eventsAdvanced, fromUnicode("⬅")),
                                Button.danger(event.getUser().getId() + ":delete", fromUnicode("\uD83D\uDDD1\uFE0F")),
                                Button.primary(userId + ":event:0:right:" + team + ":" + length+":"+eventsAdvanced, fromUnicode("➡"))
                        )
                        .queue();
            }
            break;
            case "team-league-rank": {


                event.deferReply().queue();

                OptionMapping teamNumber = event.getOption("teamnumber");
                OptionMapping seasonOPT = event.getOption("season");
                int season = seasonOPT == null
                        ? DEFAULT_SEASON // default
                        : (int) seasonOPT.getAsLong();
                int team = teamNumber.getAsInt();

                JSONArray events = FTCAPI.GetEventAPI(team, season);

                String regionCode, leagueCode;
                regionCode = leagueCode = "";
                boolean foundCodes = false;
                for (int i = 0; i < events.length(); i++) {
                    JSONObject eventdata = events.getJSONObject(i);
                    if (!eventdata.isNull("regionCode") && !eventdata.isNull("leagueCode")) {
                        regionCode = String.valueOf(eventdata.get("regionCode"));
                        leagueCode = String.valueOf(eventdata.get("leagueCode"));
                        foundCodes = true;
                        break;
                    }
                }





                eb.setTitle("League ranking for team " + team);

                String userId = event.getUser().getId();

                if (foundCodes) {
                    eb.setColor(MAIN_COLOR);
                    int length = FTCAPI.GetLeagueRankTeam(eb, regionCode, leagueCode, INLINE, season, team, -1);
                    eventHook.sendMessageEmbeds(eb.build())
                            .addActionRow(
                                    Button.danger(userId + ":delete", fromUnicode("\uD83D\uDDD1\uFE0F"))
                            )
                            .queue();
                } else {
                    eb.setColor(ERROR_COLOR);
                    eb.setDescription("Unable to find a valid league for this team.");
                    eventHook.sendMessageEmbeds(eb.build())
                            .addActionRow(
                                    Button.danger(userId + ":delete", fromUnicode("\uD83D\uDDD1\uFE0F"))
                            )
                            .queue();
                }

                break;


            }

            case "league-rank": {
                event.deferReply().queue();
                OptionMapping seasonOPT = event.getOption("season");
                OptionMapping regionAndLeagueOrTeam = event.getOption("region-league-or-team");

                int season = seasonOPT == null
                        ? DEFAULT_SEASON // default
                        : (int) seasonOPT.getAsLong();
                Boolean isTeam;

                // Teams only have int numbers. Deterime if League or Team
                int teamNumber = 0;
                String regionAndLeague = null;
                try {
                     teamNumber = Integer.parseInt(regionAndLeagueOrTeam.getAsString());
                    isTeam = true;

                } catch (NumberFormatException e) {

                     regionAndLeague = regionAndLeagueOrTeam.getAsString();
                    isTeam = false;
                }

                String regionCode, leagueCode;
            if (isTeam) {
                JSONArray events = FTCAPI.GetEventAPI(teamNumber, season);

                regionCode = leagueCode = "";
                boolean foundCodes = false;
                for (int i = 0; i < events.length(); i++) {
//                    System.out.println(i);
                    JSONObject eventdata = events.getJSONObject(i);
                    if (eventdata.isNull("regionCode") == false && eventdata.isNull("leagueCode") == false) {
                        regionCode = String.valueOf(eventdata.get("regionCode"));
                        leagueCode = String.valueOf(eventdata.get("leagueCode"));
                        foundCodes = true;
                        break;
                    }

                }
                if (foundCodes) {
                    eb.setTitle("League ranking for " + regionCode + " " + leagueCode);
                    eb.setColor(MAIN_COLOR);
                    int length = FTCAPI.GetLeagueRank(eb, regionCode, leagueCode, INLINE, season,0,10);
                    String userId = event.getUser().getId();
                                eventHook.sendMessageEmbeds(eb.build())
                            .addActionRow(
                                    Button.primary(userId + ":league-rank:10:left:" +length + ":"+season+":"+regionCode+":"+leagueCode, fromUnicode("⬅")),
                                    Button.danger(userId + ":delete", fromUnicode("\uD83D\uDDD1\uFE0F")),
                                    Button.primary(userId + ":league-rank:10:right:" + length + ":"+season+":"+regionCode+":"+leagueCode, fromUnicode("➡"))
                            )
                            .queue();
                } else {
                    eb.setTitle("Unable to find a valid league for this team. Please enter it manually");
                    eb.setColor(ERROR_COLOR);
                    eb.setDescription("Unable to find a valid league for this team.");
                    String userId = event.getUser().getId();
                                eventHook.sendMessageEmbeds(eb.build())
                            .addActionRow(
                                    Button.danger(userId + ":delete", fromUnicode("\uD83D\uDDD1\uFE0F"))
                            )
                            .queue();
                }

            } else {
                // split region and league :
                String[] regionAndLeagueSplit = regionAndLeague.split(":");
                regionCode = regionAndLeagueSplit[0];
                leagueCode = regionAndLeagueSplit[1];
                int length = FTCAPI.GetLeagueRank(eb, regionCode, leagueCode, INLINE, season, 0, 10);
                if (length == 0) {
                    eb.setTitle("Leage or Region Code is invalid");
                    eb.setColor(ERROR_COLOR);

                    eventHook.sendMessageEmbeds(eb.build()).setEphemeral(true)
                            .setActionRow(
                                    Button.danger(event.getUser().getId() + ":delete", fromUnicode("\uD83D\uDDD1\uFE0F"))
                                    )
                            .queue();
                } else {
                    eb.setTitle("League ranking for " + regionCode + " " + leagueCode);
                    eb.setColor(MAIN_COLOR);
                    String userId = event.getUser().getId();
                                eventHook.sendMessageEmbeds(eb.build())
                            .addActionRow(
                                    Button.primary(userId + ":league-rank:10:left:" + teamNumber + ":" + season + ":" + regionCode + ":" + leagueCode, fromUnicode("⬅")),
                                    Button.danger(userId + ":delete", fromUnicode("\uD83D\uDDD1\uFE0F")),
                                    Button.primary(userId + ":league-rank:10:right:" + teamNumber + ":" + +season + ":" + regionCode + ":" + leagueCode, fromUnicode("➡"))
                            )
                            .queue();
                }
            }
break;
            }

            case "event-rank": {

                String userId = event.getUser().getId();



                event.deferReply().queue();

                // https://github.com/DV8FromTheWorld/JDA/blob/8852b5e9ed07182deaed284a067b1fe68da5936a/src/examples/java/SlashBotExample.java#L195
                OptionMapping eventCode = event.getOption("code");
                OptionMapping seasonOPT = event.getOption("season");
                int season = seasonOPT == null
                        ? DEFAULT_SEASON
                        : (int) seasonOPT.getAsLong();
                String code = eventCode.getAsString();

                JSONObject response = ftcScoutAPI.eventRankRequest(season,code);
                JSONObject sortedResponse = FTCScoutAPI.sortTeamsByRank(response);

                JSONObject data = sortedResponse.getJSONObject("data");
                JSONObject eventByCode = data.getJSONObject("eventByCode");
                JSONArray teams = eventByCode.getJSONArray("teams");
                int length = teams.length();
                if (sortedResponse == null){
                    eb.setTitle("Error");
                    eb.setColor(ERROR_COLOR);
                    eb.setDescription("Unable to find event with code " + code + " for season " + season);
                    eventHook.sendMessageEmbeds(eb.build()).setEphemeral(true)
                            .setActionRow(
                                    Button.danger(event.getUser().getId() + ":delete", fromUnicode("\uD83D\uDDD1\uFE0F"))
                            )
                            .queue();

                } else {
                    if (length == 0){
                        eb.setTitle("Error");
                        eb.setColor(ERROR_COLOR);
                        eb.setDescription("Unable to find event with code " + code + " for season " + season);
                        eventHook.sendMessageEmbeds(eb.build()).setEphemeral(true)
                                .setActionRow(
                                        Button.danger(event.getUser().getId() + ":delete", fromUnicode("\uD83D\uDDD1\uFE0F"))
                                )
                                .queue();
                    }
                    Boolean lessthan10 = length < 10;
                    // Top 10
                    if (length > 10) {


                        for (int i = 1; i <= 10; i++) {
                            JSONObject teamFull = teams.getJSONObject(i - 1);
                            JSONObject team = teamFull.getJSONObject("team");
                            JSONObject stats = teamFull.getJSONObject("stats");
                            String teamNumber = String.valueOf(team.get("number"));
                            String nameTeam = String.valueOf(team.get("name"));
                            int rank = stats.getInt("rank");
                            eb.addField("Rank: "+ rank, teamNumber+"  "+nameTeam, INLINE);
                        }
                    } else {
                        for (int i = 1; i <= length; i++) {
                            JSONObject teamFull = teams.getJSONObject(i - 1);
                            JSONObject team = teamFull.getJSONObject("team");
                            JSONObject stats = teamFull.getJSONObject("stats");
                            String teamNumber = String.valueOf(team.get("number"));
                            int rank = stats.getInt("rank");
                            String nameTeam = String.valueOf(team.get("name"));
                            eb.addField("Rank: "+ rank, teamNumber+"  "+nameTeam, INLINE);
                        }
                    }
                    eb.setTitle("Event ranking for " + code);
                    eb.setColor(MAIN_COLOR);
                    int index;
            if (lessthan10){
                 index = length;
            } else  {
                 index = 10;
            }
            eventHook.sendMessageEmbeds(eb.build())
                            .addActionRow(
                                    Button.primary(userId + ":event-rank:"+index+":left:" + code + ":" + season+":"+length, fromUnicode("⬅")),
                                    Button.danger(userId + ":delete", fromUnicode("\uD83D\uDDD1\uFE0F")),
                                    Button.primary(userId + ":event-rank:"+index+":right:" + code + ":" + season+":"+length, fromUnicode("➡"))
                            )
                            .queue();
                }


            }
            break;
            case "awards": {
                event.deferReply().queue();

                // https://github.com/DV8FromTheWorld/JDA/blob/8852b5e9ed07182deaed284a067b1fe68da5936a/src/examples/java/SlashBotExample.java#L195
                OptionMapping teamNumber = event.getOption("teamnumber");
                int team = teamNumber.getAsInt();

                eb.setTitle("Event ranking for team " + team);
                eb.setColor(MAIN_COLOR);

                FTCAPI.GetAward(eb, 2022, INLINE);

                        //event.replyEmbeds(eb.build()).queue();
                eventHook.sendMessageEmbeds(eb.build()).queue();
            }
            break;
            case "awards-list": {
                OptionMapping season = event.getOption("season");

                int seasonInt = season != null ? season.getAsInt() : DEFAULT_SEASON;


                JSONArray Awards = FTCAPI.GetAwardAPI(seasonInt);

                Set<String> addedNames = new HashSet<>(); // initialize an empty set to keep track of added names
                StringBuilder awardsBuilder = new StringBuilder(); // initialize a StringBuilder to concatenate unique award names
                int length = Awards.length();
                for (int i = 0; i < length; i++) {
                    JSONObject Award = Awards.getJSONObject(i);
                    String AwardName = Award.getString("name");
                    if (!addedNames.contains(AwardName)) { // check if the name is already in the set
                        addedNames.add(AwardName); // add the name to the set
                        awardsBuilder.append(AwardName).append("\n"); // append the name to the StringBuilder
                    }
                }

                String awardsString = awardsBuilder.toString(); // convert the StringBuilder to a String
                eb.addField("Award Names", "```" + awardsString + "```", false); // add the concatenated string as a single field to the EmbedBuilder


                eb.setTitle("FTC Awards List");
                eb.setDescription("Season : " + seasonInt);
                eb.setColor(MAIN_COLOR);
                event.replyEmbeds(eb.build()).setEphemeral(false)
                        .addActionRow(
                                Button.danger(event.getUser().getId() + ":delete", fromUnicode("\uD83D\uDDD1\uFE0F"))

                        )
                        .queue();


            }
            break;
            case "predict": {



                OptionMapping teamNumber = event.getOption("teamnumber");
                int team = teamNumber.getAsInt();

                eb.setTitle("predictftc.org for team " + team, "https://predictftc.org/2022/team/" + team);
                eb.setColor(MAIN_COLOR);

                eb.setDescription("Note: predictftc does not have an api so it is just linked, click on the title.");

                       event.replyEmbeds(eb.build())
                       .addActionRow(
                               Button.danger(event.getUser().getId() + ":delete", fromUnicode("\uD83D\uDDD1\uFE0F")),
                                Button.link("https://predictftc.org/2022/team/" + team, "predictftc.org")
                                        .withEmoji(Emoji.fromUnicode("\uD83D\uDD17"))
                       )
                       .queue();
            }
            break;
            case "scout-event": {
                event.deferReply().queue();

                // https://github.com/DV8FromTheWorld/JDA/blob/8852b5e9ed07182deaed284a067b1fe68da5936a/src/examples/java/SlashBotExample.java#L195
                OptionMapping teamNumber = event.getOption("teamnumber");
                int team = teamNumber.getAsInt();


                eb.setTitle("ftcscout.org for team " + team);
                eb.setColor(MAIN_COLOR);


                JSONArray events = ftcScoutAPI.teamStatsByNumberRequest(team).getJSONObject("data").getJSONObject("teamByNumber").getJSONArray("events");

                FTCScoutAPI.getTeamStatsByNumberEmbed(eb, events, 0, true, INLINE);

//                System.out.println(events);



                        //event.replyEmbeds(eb.build()).queue();
                eventHook.sendMessageEmbeds(eb.build())
                        .addActionRow(
                                Button.primary(event.getUser().getId() + ":scout:0:left:" + team + ":" + events.length(), fromUnicode("⬅")),
                                Button.danger(event.getUser().getId() + ":delete", fromUnicode("\uD83D\uDDD1\uFE0F")),
                                Button.primary(event.getUser().getId() + ":scout:0:right:" + team + ":" + events.length(), fromUnicode("➡"))
                        )
                        .queue();
                break;
            }
            case "scout": {
                event.deferReply().queue();

                // https://github.com/DV8FromTheWorld/JDA/blob/8852b5e9ed07182deaed284a067b1fe68da5936a/src/examples/java/SlashBotExample.java#L195
                OptionMapping teamNumber = event.getOption("teamnumber");
                int team = teamNumber.getAsInt();


                eb.setTitle("ftcscout.org for team " + team);
                eb.setColor(MAIN_COLOR);

                JSONObject teamStats = ftcScoutAPI.teamStatsByNumberRequest(team);
                JSONObject teamByNumber = null;
                try {
                     teamByNumber = teamStats.getJSONObject("data").getJSONObject("teamByNumber");
                } catch (JSONException e) {
                    eb.setTitle("Team not found");
                    eb.setDescription("Team " + team + " not found");
                    eb.setColor(ERROR_COLOR);
                    eventHook.sendMessageEmbeds(eb.build()).setEphemeral(true)
                            .addActionRow(
                                    Button.danger(event.getUser().getId() + ":delete", fromUnicode("\uD83D\uDDD1\uFE0F"))

                            ).queue();
                    return;
                }
                JSONArray events = teamByNumber.getJSONArray("events");

                String teamName = teamByNumber.getString("name");
                eb.setDescription(teamName);
                FTCScoutAPI.getTeamStatsByNumberEmbed(eb, events, 0, false, INLINE);

//                System.out.println(events);



                eb.setAuthor(BOT_NAME, event.getJDA().getSelfUser().getAvatarUrl(), event.getJDA().getSelfUser().getAvatarUrl());
                //event.replyEmbeds(eb.build()).queue();
                eventHook.sendMessageEmbeds(eb.build())
                        .addActionRow(
                                Button.danger(event.getUser().getId() + ":delete", fromUnicode("\uD83D\uDDD1\uFE0F")),
                                Button.link("https://ftcscout.org/teams/" + team, "FTC Scout")
                                        .withEmoji(fromUnicode("\uD83D\uDD2D")) // Link Button with label and emoji
                        )
                        .queue();
                break;
            }
            case "ftc-scores": {


                event.deferReply().queue();

                OptionMapping teamNumber = event.getOption("teamnumber");
                int team = teamNumber.getAsInt();
                String teamStatsURL = "https://ftcscores.com/team/" + team;
                eb.setTitle("ftcscores.com for team " + team, teamStatsURL);
                eb.setColor(MAIN_COLOR);

                eb.addField("Note: This is a link to the teams FTC Scores dashboard", "", INLINE);

                        //event.replyEmbeds(eb.build()).queue();
                eventHook.sendMessageEmbeds(eb.build())
                        .addActionRow(

                                Button.danger(event.getUser().getId() + ":delete", fromUnicode("\uD83D\uDDD1\uFE0F")),
                                Button.link(teamStatsURL, "FTC Scores")
                                        .withEmoji(fromUnicode("\uD83D\uDD17")) // Link Button with label and emoji
                        )
                        .queue();
            }
            break;
            case "top": {
                event.deferReply().queue();

                //http://ftc-api.firstinspires.org/v2.0/{season}/rankings/{eventCode}

            }
            break;

            case "discord-event-details": {


                if (event.getGuild().getScheduledEvents().isEmpty()) {
                    eb.setTitle("No Events Right Now");
                    eb.setDescription(":disappointed_relieved:");
                    eb.setColor(ERROR_COLOR);
                                event.replyEmbeds(eb.build()).setEphemeral(true).queue();
                    break;
                }
                int countLoop = 0;

                List<ScheduledEvent> listOfEvents = event.getGuild().getScheduledEvents();
                for (ScheduledEvent scheduledEvent : listOfEvents) {
                    countLoop++;

                    String nameEVNT = scheduledEvent.getName();
                    String locationEVNT = scheduledEvent.getLocation();
                    String timeEVNT = String.valueOf(scheduledEvent.getStartTime());
                    String finTime = convertToRegularTime(timeEVNT);
                    String directionsURL;
                    VoiceChannel voiceChannel = null;
                    try {
                        voiceChannel = scheduledEvent.getGuild().getVoiceChannelById(locationEVNT);
                    } catch (NumberFormatException e) {
                        try {
                            directionsURL = directionsUrl(locationEVNT);
                        } catch (UnsupportedEncodingException c) {
                            throw new RuntimeException(c);
                        }
                        eb.addField("Location of event : ", "[" + locationEVNT + "](" + directionsURL + ")", false);


                    }

                    if (voiceChannel != null) {
                        eb.addField("VC of event : ", voiceChannel.getAsMention(), false);
                    }

                    String descriptionEVNT = scheduledEvent.getDescription();
                    String imageEVNT = scheduledEvent.getImageUrl();
                    if (descriptionEVNT == null) {
                        descriptionEVNT = "No description";
                    }

                    eb.setTitle(nameEVNT);
                    eb.setThumbnail(imageEVNT);
                    eb.setColor(MAIN_COLOR);
                    eb.addField("Time of event : ", finTime, false);
                    eb.addField("description of event : ", descriptionEVNT, false);


                    if (countLoop == 1) {
                        event.replyEmbeds(eb.build())
                                .addActionRow(
                                        Button.danger(event.getUser().getId() + ":delete", fromUnicode("\uD83D\uDDD1\uFE0F"))

                                )
                                .queue();
                    } else {
                        eventHook.sendMessageEmbeds(eb.build()).setEphemeral(false)
                                .addActionRow(
                                        Button.danger(event.getUser().getId() + ":delete", fromUnicode("\uD83D\uDDD1\uFE0F"))

                                )
                                .queue();
                    }
                }
                break;
            }
            case "repo-details": {
                OptionMapping repoName = event.getOption("name-owner");
                String Repo = repoName.getAsString();
                if (!Repo.contains("/")) {
                    eb.setTitle("Error");
                    eb.setColor(ERROR_COLOR);
                    eb.setDescription("Remember to include the owner of the repo and name seperated by a /, for example: `owner/repo`");
                    event.replyEmbeds(eb.build()).setEphemeral(true).queue();

                    return;
                }
                String[] repoStats;
                try {
                    repoStats = repoStats(Repo);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                String imgURL = repoStats[0];

                if (GitHubAPIerrorCheck(event, eb, repoStats))return;

                eb.setTitle("Repository for " + Repo);
                eb.setColor(MAIN_COLOR);
                eb.addField(" :books: repository", "[Repo](https://github.com/" + Repo + ")", false);

                eb.setThumbnail(imgURL);
                eb.setFooter("Powered by GitHub Rest API", "https://github.githubassets.com/images/modules/logos_page/GitHub-Mark.png");

                event.replyEmbeds(eb.build())
                        .addActionRow(
                                Button.danger(event.getUser().getId() + ":delete", fromUnicode("\uD83D\uDDD1\uFE0F"))

                        )
                        .queue();

                break;
            }
            case "latest-commit": {
                OptionMapping repoName = event.getOption("name-owner");
                String Repo = repoName.getAsString();
                if (!Repo.contains("/")) {
                    eb.setTitle("Error");
                    eb.setColor(ERROR_COLOR);
                    eb.setDescription("Remember to include the owner of the repo and name seperated by a /, for example: `owner/repo`");
                    event.replyEmbeds(eb.build()).setEphemeral(true).queue();

                    return;
                }
                String[] latestCommitResponse;
                try {
                    latestCommitResponse = latestCommit(Repo);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if (GitHubAPIerrorCheck(event, eb, latestCommitResponse))return;


                String name;
                String message;
                String url;
                String sha;

                name = latestCommitResponse[0];
                message = latestCommitResponse[1];
                url = latestCommitResponse[2];
                sha = latestCommitResponse[3];

                int total;
                int additions;
                int deletions;

                int[] commitStatsResponse;
                try {
                    commitStatsResponse = commitstats(Repo, sha);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                total = commitStatsResponse[0];
                additions = commitStatsResponse[1];
                deletions = commitStatsResponse[2];

                eb.setTitle("Latest commit for "+Repo+" repository");
                eb.addField("URL : ", "[commit](" + url + ")", false);
                eb.setColor(MAIN_COLOR);
                if (name != null) {
                    eb.addField("Commiter : ", name, false);
                }
                if (message != null) {
                    eb.addField("Commit message : ", message, false);
                }
                eb.addField(":pencil: Total changes : ", String.valueOf(total), true);
                eb.addField(":green_circle: Additions : ", String.valueOf(additions), true);
                eb.addField(":red_circle: Deletions : ", String.valueOf(deletions), true);
                        eb.setFooter("Powered by GitHub Rest API", "https://github.githubassets.com/images/modules/logos_page/GitHub-Mark.png");

                event.replyEmbeds(eb.build())
                        .addActionRow(
                                Button.danger(event.getUser().getId() + ":delete", fromUnicode("\uD83D\uDDD1\uFE0F"))

                        )
                        .queue();

                break;
            }



            case "clear":


                if (event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                    OptionMapping number = event.getOption("amount");
                    if (number.getAsInt() > 100) {
                        eb.setTitle("You can't delete more than 100 messages");
                        eb.setColor(ERROR_COLOR);
                                        event.replyEmbeds(eb.build()).setEphemeral(true).queue();
                        break;
                    } else {
                        OptionMapping userOption = event.getOption("user");
                        Member member = userOption == null ? null : event.getGuild().getMember(userOption.getAsUser());
                        List<Message> messages = event.getChannel().getHistory().retrievePast(number.getAsInt()).complete();
                        if (member == null) {
                            event.getChannel().purgeMessages(messages);
                        } else {
                            messages.removeIf(m -> !m.getAuthor().equals(member.getUser()));
                            event.getChannel().purgeMessages(messages);
                        }
                        eb.setTitle("Cleared channel");
                        eb.setDescription(number.getAsInt() + " messages deleted :wastebasket:");
                        eb.setColor(MAIN_COLOR);
                                        event.replyEmbeds(eb.build()).setEphemeral(true).queue();
                        break;
                    }
                } else {
                    eb.setTitle("You don't have permission to do that");
                    eb.setColor(ERROR_COLOR);
                                event.replyEmbeds(eb.build()).setEphemeral(true).queue();
                    break;
                }

            case "weather":


                OptionMapping selectedUnit = event.getOption("measurement-unit");
                String DEFAULT_UNIT = "imperial";
                String finalUnit = selectedUnit == null ? DEFAULT_UNIT : selectedUnit.getAsString();


                OptionMapping teamNumber = event.getOption("team-number");
                int team = teamNumber.getAsInt();
                StringBuilder location = FTCAPI.TeamLocation(team, DEFAULT_SEASON);
                if (location.toString().contains("Error")) {
                    eb.setTitle(location.toString());
                    eb.setColor(ERROR_COLOR);
                                event.replyEmbeds(eb.build()).setEphemeral(true).queue();
                    return;
                }
                String cords;
                String[] splitCords;
                String lat;
                String lon;
                String finCords;
                try {
                    cords = GeoCodeAPI.GeoCode(location.toString());
                    if (cords == null) {
                        eb.setTitle("Team not found");
                        eb.setColor(ERROR_COLOR);
                                        event.replyEmbeds(eb.build()).setEphemeral(true).queue();
                        return;
                    } else {
                        splitCords = cords.split(",", 0);
                        lat = splitCords[0];
                        lon = splitCords[1];
                        finCords = "latitude=" + lat + "&" + "longitude=" + lon + "&";
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                String combinedVariables;


                try {
                    combinedVariables = WeatherAPI.getWeather(finCords, finalUnit, 1);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                String[] variablesArray = combinedVariables.split("\\|");

                double temp = Double.parseDouble(variablesArray[0]);
                double wind = Double.parseDouble(variablesArray[1]);
                String convertedCodeCurrent = variablesArray[2];
                double firstTempMax = Double.parseDouble(variablesArray[3]);
                double firstTempMin = Double.parseDouble(variablesArray[4]);
                double firstPrecip = Double.parseDouble(variablesArray[5]);
                String convertedCodeFirst = variablesArray[6];
                String units = variablesArray[7];
                ArrayList<String> unitsForEmbed = new ArrayList<String>();

                if (units.equals("imperial")) {
                    unitsForEmbed.add("°F");
                    unitsForEmbed.add("mp/h");
                    unitsForEmbed.add("in");
                } else if (units.equals("metric")) {
                    unitsForEmbed.add("°C");
                    unitsForEmbed.add("km/s");
                    unitsForEmbed.add("mm");
                }


                eb.setTitle("Weather for team : " + team);
                eb.setDescription("Location : " + location);
                eb.addField("```Current Weather```", "", false);
                eb.addField("Current conditions : ", convertedCodeCurrent, true);
                eb.addField("Current temperature : ", ":thermometer: " + String.valueOf(temp) + " " + unitsForEmbed.get(0), true);
                eb.addField("Current wind : ", ":wind_blowing_face: " + String.valueOf(wind) + " " + unitsForEmbed.get(1), true);
                eb.addField("```Weather Today```", "", false);
                eb.addField("Daily conditions : ", convertedCodeFirst, true);
                eb.addField("Highest temperature : ", ":small_red_triangle: :thermometer: " + String.valueOf(firstTempMax) + " " + unitsForEmbed.get(0), true);
                eb.addField("Lowest temperature : ", ":small_red_triangle_down: :thermometer: " + String.valueOf(firstTempMin) + " " + unitsForEmbed.get(0), true);
                eb.addField("Rain amount : ", ":umbrella:  " + String.valueOf(firstPrecip) + " " + unitsForEmbed.get(2), true);

                eb.setFooter("Powered by Open Meteo", "https://github.com/open-meteo/open-meteo/blob/main/Public/apple-touch-icon.png?raw=true");
                eb.setColor(MAIN_COLOR);
                        event.replyEmbeds(eb.build()).setEphemeral(false)
                        .addActionRow(
                                Button.danger(event.getUser().getId() + ":delete", fromUnicode("\uD83D\uDDD1\uFE0F"))

                        )
                        .queue();


                break;
            case "forecast":


                OptionMapping teamNumber2 = event.getOption("teamnumber");
                int team2 = teamNumber2.getAsInt();
                StringBuilder location2 = FTCAPI.TeamLocation(team2, DEFAULT_SEASON);

                if (location2.toString().contains("Error")) {
                    eb.setTitle(location2.toString());
                    eb.setColor(ERROR_COLOR);
                                event.replyEmbeds(eb.build()).setEphemeral(true).queue();
                    return;
                }
                String cords2;
                String[] splitCords2;
                String lat2;
                String lon2;
                String finCords2;
                try {
                    cords2 = GeoCodeAPI.GeoCode(location2.toString());
                    if (cords2 == null) {
                        eb.setTitle("Team not found");
                        eb.setColor(ERROR_COLOR);
                                        event.replyEmbeds(eb.build()).setEphemeral(true).queue();
                        return;
                    } else {
                        splitCords2 = cords2.split(",", 0);
                        lat2 = splitCords2[0];
                        lon2 = splitCords2[1];
                        finCords2 = "latitude=" + lat2 + "&" + "longitude=" + lon2 + "&";
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                String combinedVariables2 = null;
                try {
                    combinedVariables = WeatherAPI.getWeather(finCords2, "imperial", 2);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                String[] variablesArray2 = combinedVariables.split("\\|");

                String convertedcodeFirst = variablesArray2[0];
                String firstTime = variablesArray2[1];
                String convertedcodeSecond = variablesArray2[2];
                String secondTime = variablesArray2[3];
                String convertedcodeThird = variablesArray2[4];
                String thirdTime = variablesArray2[5];
                String convertedcodeFourth = variablesArray2[6];
                String fourthTime = variablesArray2[7];
                String convertedcodeFifth = variablesArray2[8];
                String fifthTime = variablesArray2[9];
                String convertedcodeSixth = variablesArray2[10];
                String sixthTime = variablesArray2[11];
                String convertedcodeSeventh = variablesArray2[12];
                String seventhTime = variablesArray2[13];


                eb.setTitle("Weather for team : " + team2);
                eb.setDescription("Location : " + location2);
                eb.addField(firstTime, convertedcodeFirst, false);
                eb.addField(secondTime, convertedcodeSecond, false);
                eb.addField(thirdTime, convertedcodeThird, false);
                eb.addField(fourthTime, convertedcodeFourth, false);
                eb.addField(fifthTime, convertedcodeFifth, false);
                eb.addField(sixthTime, convertedcodeSixth, false);
                eb.addField(seventhTime, convertedcodeSeventh, false);
                eb.setFooter("Powered by Open Meteo", "https://github.com/open-meteo/open-meteo/blob/main/Public/apple-touch-icon.png?raw=true");
                eb.setColor(MAIN_COLOR);
                        event.replyEmbeds(eb.build()).setEphemeral(false)
                        .addActionRow(
                                Button.danger(event.getUser().getId() + ":delete", fromUnicode("\uD83D\uDDD1\uFE0F"))

                        )
                        .queue();

                break;
            case "docs":


                event.reply("Chose the Docs you want to see :")
                        .addActionRow(
                                StringSelectMenu.create("Docs Options")
                                        .addOptions(SelectOption.of("Java", "java")
                                                .withDescription("Language Docs")
                                                .withEmoji(fromUnicode("☕")))
                                        .addOptions(SelectOption.of("RoadRunner", "road-runner")
                                                .withDescription("Autonomous Software")
                                                .withEmoji(fromUnicode("\uD83C\uDFC3")))
                                        .addOptions(SelectOption.of("FTC SDK", "ftc-sdk")
                                                .withDescription("SDK Docs")
                                                .withEmoji(fromUnicode("\uD83E\uDD16")))
                                        .addOptions(SelectOption.of("Open CV", "open-cv")
                                                .withDescription("Computer Vision")
                                                .withEmoji(fromUnicode("\uD83D\uDCF7")))
                                        .addOptions(SelectOption.of("FTC Lib", "ftc-lib")
                                                .withDescription("Enhanced FTC SDK")
                                                .withEmoji(fromUnicode("\uD83E\uDDED")))
                                        .addOptions(SelectOption.of("PID", "pid")
                                                .withDescription("PID Controller")
                                                .withEmoji(fromUnicode("\uD83D\uDCCA")))

                                        .build())
                        .setEphemeral(false)
                        .addActionRow(
                                Button.danger(event.getUser().getId() + ":delete", fromUnicode("\uD83D\uDDD1\uFE0F"))

                        )
                        .queue();
                break;


            case "fetch-docs":

                OptionMapping queryOption = event.getOption("query");
                JSONObject fetchDocsResponse;
                try {
                     fetchDocsResponse = searchDocs(LEARN_SOFTWARE_SPACE_ID, URLEncode(queryOption.getAsString()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                if (fetchDocsResponse == null) {
                    eb.setTitle("Docs not found");
                    eb.setColor(ERROR_COLOR);
                                event.replyEmbeds(eb.build()).setEphemeral(true).queue();
                    return;
                } else {
                    // Get array Items
                    JSONArray itemsFetchDocs = fetchDocsResponse.getJSONArray("items");

                    // Get first item
                    JSONObject firstItemFetchDocs = itemsFetchDocs.getJSONObject(0);

                    String titleFetchDocs = firstItemFetchDocs.getString("title");
                    String pathFetchDocs = firstItemFetchDocs.getString("path");

                    JSONArray sectionsFetchDocs = firstItemFetchDocs.getJSONArray("sections");

                    JSONObject firstSectionFetchDocs = sectionsFetchDocs.getJSONObject(0);

                    String bodyFetchDocs = firstSectionFetchDocs.getString("body");
                    // If body is over limit shorten it
                    if (bodyFetchDocs.length() > 1018) {
                        bodyFetchDocs = bodyFetchDocs.substring(0, 1014);
                        bodyFetchDocs = bodyFetchDocs + " ...";
                    }
                    String linkFetchDocs = "https://team-13190.gitbook.io/intro-to-ftc-software/" + pathFetchDocs;
                    eb.setTitle(":placard: Docs for : " + titleFetchDocs);
                    eb.setDescription(":mag: Query for " + queryOption.getAsString());
                    eb.addField(":link: Path", pathFetchDocs, false);
                    eb.addField(":book: Body", "```"+bodyFetchDocs+"```", false);
                    eb.setColor(MAIN_COLOR);
                                event.replyEmbeds(eb.build()).setEphemeral(false)
                            .addActionRow(
                                    Button.danger(event.getUser().getId() + ":delete", fromUnicode("\uD83D\uDDD1\uFE0F")),
                                    //Link Button
                                    Button.link(linkFetchDocs, "Full Documentation")
                                            .withEmoji(fromUnicode("\uD83D\uDD17"))
                            )
                            .queue();

                }

                break;

            case "game-manual":


                eb.setTitle("Game Manual");
                eb.setDescription("The Game Manual is the official guide to the FIRST Tech Challenge game.");
                eb.addField("Game Manual 1 :books: :one: - Traditional", "[Traditional events 1](https://www.firstinspires.org/sites/default/files/uploads/resource_library/ftc/game-manual-part-1-traditional-events.pdf)", false);
                eb.addField("Game Manual 1 :books: :one: - Remote ", "[Remote events 1](https://www.firstinspires.org/sites/default/files/uploads/resource_library/ftc/game-manual-part-1-remote-events.pdf)", false);
                eb.addField("Game Manual 2 :books: :two: - Traditional", "[Traditional events 2](https://www.firstinspires.org/sites/default/files/uploads/resource_library/ftc/game-manual-part-2-traditional.pdf)", false);
                eb.addField("Game Manual 2 :books: :two: - Remote", "[Remote events 2](https://www.firstinspires.org/sites/default/files/uploads/resource_library/ftc/game-manual-part-2-remote.pdf)", false);
                eb.addField("Game Manual 0 :link: ", "[Open source resources for all seasons](https://firstinspiresst01.blob.core.windows.net/first-energize-ftc/game-manual-part-3-traditional.pdf)", false);
                eb.setColor(MAIN_COLOR);
                        event.replyEmbeds(eb.build()).setEphemeral(false).addActionRow(
                                        Button.danger(event.getUser().getId() + ":delete", fromUnicode("\uD83D\uDDD1\uFE0F"))

                                )
                                .queue();
                break;


            case "run-code":
/*
Code X API is not working for the time being so the command is disabled
 */

                event.deferReply().queue(); // Tell discord we received the command, send a thinking... message to the user
                String error = " ";
                OptionMapping codeIn = event.getOption("code");
                OptionMapping languageIn = event.getOption("language");
                OptionMapping fileIn = event.getOption("file-code");
                Boolean isFile;
                String fileContents = null;
                String code;
                if (fileIn == null) {
                    isFile = false;
                } else {
                    isFile = true;
                    CompletableFuture<InputStream> inputStreamFuture = fileIn.getAsAttachment().retrieveInputStream();

                    // Wait for the CompletableFuture to complete and get the InputStream
                    try {
                        InputStream inputStream = inputStreamFuture.get(); // This blocks until the future is completed
                        if (inputStream != null) {
                            // Read the contents of the InputStream and convert it to a string
                            fileContents = new BufferedReader(new InputStreamReader(inputStream))
                                    .lines()
                                    .collect(Collectors.joining(System.lineSeparator()));
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }
                if (isFile){
                    code = fileContents;
                } else {
                    code = codeIn.getAsString();
                }
                String language = languageIn.getAsString();
                String result;
                String[] supportedLangs = {"java","py","cpp","c","go","cs","js"};
                Boolean isItSupported = Arrays.stream(supportedLangs).anyMatch(language::equals);
                if (isItSupported) {
                    try {
                        result = CodeXAPI.compileCode(code, language);
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                    if (result.contains("Failed")){
                        eb.clear();
                        eb.setTitle("Code Compiler");
                        eb.setDescription(result);
                        eb.setColor(ERROR_COLOR);
                        event.getHook().editOriginalEmbeds(eb.build())
                                .setActionRow(
                                        Button.danger(event.getUser().getId() + ":delete", fromUnicode("\uD83D\uDDD1\uFE0F"))

                                )
                                .queue();
                        return;
                    }
                    //return codeOut+","+error+","+time;
                    String[] resultArray = result.split("\\|");
                    String output = resultArray[0];
                    if (resultArray[1].equals(" ")) {
                        error = " ";
                    } else {
                        error = resultArray[1];
                    }

                    // String time = resultArray[2];

                    if (error.equals(" ")) {
                        eb.setTitle("Code Compiler");
                        eb.addField("Code :pencil: ", "```" + code + "```", false);
                        eb.addField("Language :computer: ", "```" + language + "```", false);
                        eb.addField("Result :white_check_mark: ", "```" + output + "```", false);
                        eb.setColor(MAIN_COLOR);
                    } else {
                        eb.setTitle("Code Compiler");
                        eb.addField("Code :pencil: ", "```" + code + "```", false);
                        eb.addField("Language :computer: ", "```" + language + "```", false);
                        eb.addField("Error :x: ", "```" + error + "```", false);
                        eb.setColor(ERROR_COLOR);
                    }
                    eb.setFooter("Powered by CodeX API", "https://rapidapi.com/cdn/images?url=https://rapidapi-prod-apis.s3.amazonaws.com/aec56ade-c018-4dec-a93d-11b1552c15b0.png");
                                eventHook.sendMessageEmbeds(eb.build()).setEphemeral(false)
                            .addActionRow(
                                    Button.danger(event.getUser().getId() + ":delete", fromUnicode("\uD83D\uDDD1\uFE0F"))

                            )
                            .queue();
                    break;
                } else {
                    eb.setTitle("Sorry but that isn't a supported language code");
                    eb.addField("Language Name","Language Code",false);
                    eb.addBlankField(false);
                    eb.addField("Java","java",false);
                    eb.addField("Python","py",false);
                    eb.addField("C++","cpp",false);
                    eb.addField("C","c",false);
                    eb.addField("Go","go",false);
                    eb.addField("C#","cs",false);
                    eb.addField("JavaScript","js",false);
                    eb.setColor(ERROR_COLOR);
                                eb.setFooter("Powered by CodeX API", "https://rapidapi.com/cdn/images?url=https://rapidapi-prod-apis.s3.amazonaws.com/aec56ade-c018-4dec-a93d-11b1552c15b0.png");
                    eventHook.sendMessageEmbeds(eb.build()).setEphemeral(true)
                            .addActionRow(
                                    Button.danger(event.getUser().getId() + ":delete", fromUnicode("\uD83D\uDDD1\uFE0F"))

                            )
                            .queue();


                }
                break;

            case "vote":


                OptionMapping question = event.getOption("question");
                OptionMapping option1 = event.getOption("option1");
                OptionMapping option2 = event.getOption("option2");
                OptionMapping option3 = event.getOption("option3");
                OptionMapping option4 = event.getOption("option4");
                OptionMapping option5 = event.getOption("option5");
                OptionMapping option6 = event.getOption("option6");
                OptionMapping option7 = event.getOption("option7");
                OptionMapping option8 = event.getOption("option8");
                OptionMapping option9 = event.getOption("option9");
                OptionMapping option10 = event.getOption("option10");

                String option3String = option3 != null ? option3.getAsString() : null;
                String option4String = option4 != null ? option4.getAsString() : null;
                String option5String = option5 != null ? option5.getAsString() : null;
                String option6String = option6 != null ? option6.getAsString() : null;
                String option7String = option7 != null ? option7.getAsString() : null;
                String option8String = option8 != null ? option8.getAsString() : null;
                String option9String = option9 != null ? option9.getAsString() : null;
                String option10String = option10 != null ? option10.getAsString() : null;



                String questionString = question.getAsString();
                String option1String = option1.getAsString();
                String option2String = option2.getAsString();


                // Check if option 3 - 10 are null
                // If not null, add to eb.addField

                //code here :
                eb.setTitle("Vote");
                eb.addField("Question :question: ", "```" + questionString + "```", false);
                eb.addField("Option 1 :one: ", "```" + option1String + "```", false);
                eb.addField("Option 2 :two: ", "```" + option2String + "```", false);
                if (option3String != null) {
                    eb.addField("Option 3 :three: ", "```" + option3String + "```", false);
                }

                if (option4String != null) {
                    eb.addField("Option 4 :four: ", "```" + option4String + "```", false);
                }

                if (option5String != null) {
                    eb.addField("Option 5 :five: ", "```" + option5String + "```", false);
                }

                if (option6String != null) {
                    eb.addField("Option 6 :six: ", "```" + option6String + "```", false);
                }

                if (option7String != null) {
                    eb.addField("Option 7 :seven: ", "```" + option7String + "```", false);
                }

                if (option8String != null) {
                    eb.addField("Option 8 :eight: ", "```" + option8String + "```", false);
                }

                if (option9String != null) {
                    eb.addField("Option 9 :nine: ", "```" + option9String + "```", false);
                }

                if (option10String != null) {
                    eb.addField("Option 10 :keycap_ten: ", "```" + option10String + "```", false);
                }
                eb.setColor(MAIN_COLOR);


                event.getChannel().sendMessageEmbeds(eb.build()).queue(msg -> {
                    msg.addReaction(Emoji.fromUnicode("\u0031\uFE0F\u20E3")).queue();
                    msg.addReaction(Emoji.fromUnicode("\u0032\uFE0F\u20E3")).queue();

                    if (option3 != null) {
                        msg.addReaction(Emoji.fromUnicode("\u0033\uFE0F\u20E3")).queue();
                    }
                    if (option4 != null) {
                        msg.addReaction(Emoji.fromUnicode("\u0034\uFE0F\u20E3")).queue();
                    }
                    if (option5 != null) {
                        msg.addReaction(Emoji.fromUnicode("\u0035\uFE0F\u20E3")).queue();
                    }
                    if (option6 != null) {
                        msg.addReaction(Emoji.fromUnicode("\u0036\uFE0F\u20E3")).queue();
                    }
                    if (option7 != null) {
                        msg.addReaction(Emoji.fromUnicode("\u0037\uFE0F\u20E3")).queue();
                    }
                    if (option8 != null) {
                        msg.addReaction(Emoji.fromUnicode("\u0038\uFE0F\u20E3")).queue();
                    }
                    if (option9 != null) {
                        msg.addReaction(Emoji.fromUnicode("\u0039\uFE0F\u20E3")).queue();
                    }
                    if (option10 != null) {
                        msg.addReaction(Emoji.fromUnicode("\uD83D\uDD1F")).queue();
                    }

                });

                event.reply("Vote has been created!").setEphemeral(true).queue();


                break;



            case "search-repos":


                OptionMapping searchTerm = event.getOption("query");

                String StringSearchTerm = searchTerm.getAsString();

                String[] resultsFromSearch = null;
                try {
                    resultsFromSearch = searchRepos(StringSearchTerm);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if (GitHubAPIerrorCheck(event, eb, resultsFromSearch)) return;


                String name1 = resultsFromSearch[0];
                String url1 = resultsFromSearch[1];
                String description1 = resultsFromSearch[2];
                String language1 = resultsFromSearch[3];
                String image1 = resultsFromSearch[4];

                eb.setTitle("Results for repo search : " + StringSearchTerm + " :books:");

                eb.addField("Name", name1, false);
                eb.addField("URL", url1, false);
                eb.addField("Description", description1, false);
                eb.addField("Language", language1, false);
                eb.setThumbnail(image1);


                eb.setColor(MAIN_COLOR);
                        eb.setFooter("Powered by GitHub Rest API", "https://github.githubassets.com/images/modules/logos_page/GitHub-Mark.png");
                event.replyEmbeds(eb.build()).setEphemeral(false)
                        .addActionRow(
                                Button.danger(event.getUser().getId() + ":delete", fromUnicode("\uD83D\uDDD1\uFE0F"))

                        )
                        .queue();

                break;


            case "apod":


                String APOD_Response = null;
                try {
                    APOD_Response = NasaAPI.getAPOD();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }


                if (APOD_Response == null) {
                    event.reply("Error Fetching APOD").setEphemeral(true).queue();
                    return;
                } else {

                    String[] APOD_Response_Array = APOD_Response.split("\\|");
                    //imgURL + "|" + title + "|" + explanation;
                    String APOD_imgURL = APOD_Response_Array[0];
                    String APOD_Title = APOD_Response_Array[1];
                    String APOD_Explanation = APOD_Response_Array[2];

                    //Value cannot be longer than 1024 characters. (explanation)
                    eb.setTitle("Astronomy Picture of the Day");
                    eb.addField("Title :keyboard: ", APOD_Title, false);


                    if (APOD_Explanation.length() > 1024) {
                        APOD_Explanation = APOD_Explanation.substring(0, 1020);
                        APOD_Explanation = APOD_Explanation + " ...";
                    }


                    eb.addField("Explanation :book: ", APOD_Explanation, false);
                    eb.setImage(APOD_imgURL);
                    eb.setColor(MAIN_COLOR);
                                eb.setFooter("Powered by NASA's API", "https://www.nasa.gov/sites/default/files/thumbnails/image/nasa-logo-web-rgb.png");
                    event.replyEmbeds(eb.build()).setEphemeral(false)
                            .addActionRow(
                                    Button.danger(event.getUser().getId() + ":delete", fromUnicode("\uD83D\uDDD1\uFE0F"))
                            ).queue();

                }

                break;

            case "event-awards":


                OptionMapping eventCode = event.getOption("event");
                OptionMapping highlightTeamNumber = event.getOption("highlight-teamnumber");
                OptionMapping season2 = event.getOption("season");

                int seasonInt2 = season2 != null ? season2.getAsInt() : DEFAULT_SEASON;
                String highlightTeamNumberString = "0";
                if (highlightTeamNumber != null) {
                    highlightTeamNumberString = highlightTeamNumber.getAsString();
                }

                String eventCodeString = eventCode.getAsString();
                JSONArray eventAwards = FTCAPI.GetAwardsByEventAPI(eventCodeString, seasonInt2);
                List<String> awardStrings = new ArrayList<>();

                if (eventAwards.length() == 0) {
                    awardStrings.add("No awards for this event");
                } else {
                    for (int i = 0; i < eventAwards.length(); i++) {
                        JSONObject eventAward = eventAwards.getJSONObject(i);
                        String awardTeamNumber = eventAward.optString("teamNumber", "");
                        if (!awardTeamNumber.equals("")) {
                            String awardName = eventAward.getString("name");
                            String awardString = awardTeamNumber + " : " + awardName;
                            if (awardTeamNumber.equals(highlightTeamNumberString)) {
                                awardString = ">>>  " + awardString;
                            }
                            awardStrings.add(awardString);
                        }
                    }
                }

                if (awardStrings.isEmpty()) {
                    awardStrings.add("No awards for this event");
                }

                List<String> fieldStrings = new ArrayList<>();
                StringBuilder currentField = new StringBuilder();
                int currentLength = 0;

                for (String awardString : awardStrings) {
                    if (currentLength + awardString.length() > 1010) {
                        fieldStrings.add(currentField.toString());
                        currentField = new StringBuilder();
                        currentLength = 0;
                    }
                    currentField.append(awardString).append("\n\n");
                    currentLength += awardString.length() + 2;
                }
                fieldStrings.add(currentField.toString());

                int fieldCount = fieldStrings.size();
                for (int i = 0; i < fieldCount; i++) {
                    String fieldString = fieldStrings.get(i);
                    String fieldName = "Event Awards" + (i + 1 == 1 ? "" : " " + (i + 1));
                    eb.addField(fieldName, "```" + fieldString + "```", false);
                }

                eb.setTitle("FTC Event Awards");
                eb.setDescription("Event Code : " + eventCodeString);
                eb.setColor(MAIN_COLOR);
                event.replyEmbeds(eb.build()).setEphemeral(false)
                        .addActionRow(
                                Button.danger(event.getUser().getId() + ":delete", fromUnicode("\uD83D\uDDD1\uFE0F"))
                        ).queue();
                break;

            case "team-awards-at-event":


                OptionMapping eventCode2 = event.getOption("event");
                OptionMapping teamNumberAtEvent = event.getOption("teamnumber");
                OptionMapping seasonTeamAtEvent = event.getOption("season");

                String eventCodeString2 = eventCode2.getAsString();

                int seasonInt3 = seasonTeamAtEvent != null ? seasonTeamAtEvent.getAsInt() : DEFAULT_SEASON;
                // Use default team if null
                int teamNumberAtEventINT = teamNumberAtEvent.getAsInt();

                JSONArray awards = FTCAPI.GetAwardsByTeamAtEventAPI(eventCodeString2, teamNumberAtEventINT, seasonInt3);

                StringBuilder awardStringForTeamAtEvent = new StringBuilder();

                if (awards.length() == 0) {
                    awardStringForTeamAtEvent.append("No awards for this team at this event");
                } else {
                    for (int i = 0; i < awards.length(); i++) {
                        JSONObject award = awards.getJSONObject(i);
                        String awardName = award.getString("name");
                        awardStringForTeamAtEvent.append(awardName).append("\n");
                    }
                }
                eb.setTitle("FTC Team Awards at Event");
                eb.setDescription("Event Code : " + eventCodeString2 + "\nTeam Number : " + teamNumberAtEventINT);
                eb.addField("Awards", "```" + awardStringForTeamAtEvent + "```", false);
                eb.setColor(MAIN_COLOR);
                event.replyEmbeds(eb.build()).setEphemeral(false)
                        .addActionRow(
                                Button.danger(event.getUser().getId() + ":delete", fromUnicode("\uD83D\uDDD1\uFE0F"))
                        ).queue();


                break;


            case "team-awards":


                OptionMapping teamNumberForTeamAwards = event.getOption("teamnumber");
                OptionMapping season = event.getOption("season");

                int seasonInt = season != null ? season.getAsInt() : DEFAULT_SEASON;

                int teamNumberINT = teamNumberForTeamAwards.getAsInt();

                JSONArray teamAwards = FTCAPI.GetAwardsByTeamAPI(teamNumberINT, seasonInt);

                StringBuilder awardStringForTeam = new StringBuilder();
            if (teamAwards == null){
                eb.setTitle("FTC Team Awards");
                eb.setDescription("Error finding team awards");
                eb.setColor(ERROR_COLOR);
                event.replyEmbeds(eb.build()).setEphemeral(true).queue();
            } else {
                if (teamAwards.length() == 0) {
                    awardStringForTeam.append("No awards for this team");
                } else {
                    for (int i = 0; i < teamAwards.length(); i++) {
                        JSONObject award = teamAwards.getJSONObject(i);
                        String awardName = award.getString("name");
                        awardStringForTeam.append(awardName).append("\n");
                    }
                }
                eb.setTitle("FTC Team Awards");
                eb.setDescription("Team Number : " + teamNumberINT);
                eb.addField("Season : ", String.valueOf(seasonInt), false);
                eb.addField("Awards", "```" + awardStringForTeam + "```", false);
                eb.setColor(MAIN_COLOR);
                event.replyEmbeds(eb.build()).setEphemeral(false)
                        .addActionRow(
                                Button.danger(event.getUser().getId() + ":delete", fromUnicode("\uD83D\uDDD1\uFE0F"))
                        ).queue();
            }
                break;


            case "alliance-details":


                OptionMapping eventCode3 = event.getOption("event");
                OptionMapping season3 = event.getOption("season");

                String eventCodeString3 = eventCode3.getAsString();
                int alllianceDetailsSeason = season3 != null ? season3.getAsInt() : DEFAULT_SEASON;

                JSONArray alliances = FTCAPI.getAllianceDetailsAPI(eventCodeString3, alllianceDetailsSeason);


                if (alliances.length() == 0) {
                    eb.addField("Error: ", "No alliances for this event", false);
                } else {
                    for (int i = 0; i < alliances.length(); i++) {


                        JSONObject alliance = alliances.getJSONObject(i);
                        String allianceName = alliance.getString("name");
                        String allianceCaptain = alliance.get("captain").toString();
                        String allianceRound1 = alliance.get("round1").toString();
                        String allianceRound2 = alliance.get("round2").toString();
                        String allianceRound3 = alliance.get("round3").toString();

                        if (allianceRound1.equals("null")) {
                            allianceRound1 = "None";
                        } else if (allianceRound2.equals("null")) {
                            allianceRound2 = "None";
                        } else if (allianceRound3.equals("null")) {
                            eb.addField("Alliance Name : " + allianceName, "Captain : " + allianceCaptain + "\nRound 1 : " + allianceRound1 + "\nRound 2 : " + allianceRound2, false);
                        } else {

                            eb.addField("Alliance Name : " + allianceName, "Captain : " + allianceCaptain + "\nRound 1 : " + allianceRound1 + "\nRound 2 : " + allianceRound2 + "\nRound 3 : " + allianceRound3, false);
                        }
                    }
                }

                eb.setTitle("FTC Alliance Details");
                eb.setDescription("Event Code : " + eventCodeString3);
                eb.addField("Season : ", String.valueOf(alllianceDetailsSeason), false);
                eb.setColor(MAIN_COLOR);
                event.replyEmbeds(eb.build()).setEphemeral(false)
                        .addActionRow(
                                Button.danger(event.getUser().getId() + ":delete", fromUnicode("\uD83D\uDDD1\uFE0F"))
                        ).queue();

                break;


            case "alliance-selection":


                OptionMapping eventCode4 = event.getOption("event");
                OptionMapping season4 = event.getOption("season");

                String eventCodeString4 = eventCode4.getAsString();
                int alllianceSelectionSeason = season4 != null ? season4.getAsInt() : DEFAULT_SEASON;

                JSONArray alliances2 = FTCAPI.getAllianceSelectionAPI(eventCodeString4, alllianceSelectionSeason);

                if (alliances2.length() == 0) {
                    eb.addField("Error: ", "No alliances selection details for this event. Try the /alliance-details command.", false);
                } else {
                    for (int i = 0; i < alliances2.length(); i++) {


                        JSONObject alliance = alliances2.getJSONObject(i);
                        String allianceIndex = alliance.get("index").toString();
                        String allianceTeam = alliance.get("team").toString();
                        String allianceResult = alliance.get("result").toString();

                        if (allianceResult.equals("ACCEPT")) {
                            eb.addField("Alliance Index : " + allianceIndex, "Team : " + allianceTeam + "\nResult : " + allianceResult + " :white_check_mark:", false);

                        } else if (allianceResult.equals("CAPTAIN")) {
                            eb.addField("Alliance Index : " + allianceIndex, "Team : " + allianceTeam + "\nResult : " + allianceResult + " :crown:", false);
                        } else if (allianceResult.equals("DECLINE")) {
                            eb.addField("Alliance Index : " + allianceIndex, "Team : " + allianceTeam + "\nResult : " + allianceResult + " :x:", false);
                        } else {
                            eb.addField("Alliance Index : " + allianceIndex, "Team : " + allianceTeam + "\nResult : " + allianceResult, false);
                        }
                    }
                }
                eb.setTitle("FTC Alliance Selection");
                eb.setDescription("Event Code : " + eventCodeString4);
                eb.addField("Season : ", String.valueOf(alllianceSelectionSeason), false);
                eb.setColor(MAIN_COLOR);
                event.replyEmbeds(eb.build()).setEphemeral(false)
                        .addActionRow(
                                Button.danger(event.getUser().getId() + ":delete", fromUnicode("\uD83D\uDDD1\uFE0F"))
                        ).queue();

                break;

            case "season-data":


                OptionMapping seasonDataSeason = event.getOption("season");

                int seasonDataSeasonInt = seasonDataSeason != null ? seasonDataSeason.getAsInt() : DEFAULT_SEASON;


                JSONObject seasonData = FTCAPI.getSeasonDataAPI(seasonDataSeasonInt);





                int eventCount = seasonData.getInt("eventCount");
                String gameName = seasonData.getString("gameName");
                String kickoff = seasonData.getString("kickoff");
                int rookieStart = seasonData.getInt("rookieStart");
                int teamCount = seasonData.getInt("teamCount");

                //convert kickoff to readable date

                String kickoffDate = ConvertDate.convertToDateTime(kickoff);


                eb.setTitle("FTC Season Data, for season " + seasonDataSeasonInt);


                eb.addField("Event Count : ", String.valueOf(eventCount), false);
                eb.addField("Game Name : ", gameName, false);
                // eb.addField("Kickoff : ", kickoffDate, false);
               // eb.addField("Rookie Start : ", String.valueOf(rookieStart), false); date is off
                eb.addField("Team Count : ", String.valueOf(teamCount), false);

                eb.setColor(MAIN_COLOR);
                event.replyEmbeds(eb.build()).setEphemeral(false)
                        .addActionRow(
                                Button.danger(event.getUser().getId() + ":delete", fromUnicode("\uD83D\uDDD1\uFE0F"))
                        ).queue();

                break;


            case "match-results": {
                int lengthMatchResults = 3;
                int iterateMatchResults = 0;


                OptionMapping eventCodeMatchResults = event.getOption("event");
                OptionMapping seasonMatchResults = event.getOption("season");
                OptionMapping teamNumberMatchResults = event.getOption("teamnumber");

                String eventCodeStringMatchResults = eventCodeMatchResults.getAsString();
                int seasonMatchResultsInt = seasonMatchResults != null ? seasonMatchResults.getAsInt() : DEFAULT_SEASON;
                int teamNumberMatchResultsInt = teamNumberMatchResults != null ? teamNumberMatchResults.getAsInt() : 0;


                JSONArray matchResults = FTCAPI.getMatchResultsAPI(eventCodeStringMatchResults, seasonMatchResultsInt, teamNumberMatchResultsInt);

                //Check if null
                if (matchResults == null) {
                    eb.setTitle("Error");
                    eb.setDescription("Event Code : " + eventCodeStringMatchResults + " | Season : " + seasonMatchResultsInt + " | Team : " + teamNumberMatchResultsInt);
                    eb.addField("Error : ", "No data found", false);
                    eb.setColor(ERROR_COLOR);
                    event.replyEmbeds(eb.build()).setEphemeral(true)
                            .addActionRow(
                                    Button.danger(event.getUser().getId() + ":delete", fromUnicode("\uD83D\uDDD1\uFE0F"))
                            ).queue();
                    return;
                } else {
                    eb.setTitle("FTC Match Results");
                    eb.setColor(MAIN_COLOR);
                    eb.setDescription("Event Code : " + eventCodeStringMatchResults + " | Season : " + seasonMatchResultsInt + " | Team : " + teamNumberMatchResultsInt);
                    for (int i = 0; i < lengthMatchResults; i++) {
                        JSONObject matchResultsObject = matchResults.getJSONObject(i);
                        //Get match data
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


                        //Teams
                        StringBuilder resultsTeams = new StringBuilder();
                        JSONArray teamsMatchResults = matchResultsObject.getJSONArray("teams");
                        for (int a = 0; a < teamsMatchResults.length(); a++) {
                            int ResultsteamNumber = teamsMatchResults.getJSONObject(a).getInt("teamNumber");
                            String ResultsteamStation = teamsMatchResults.getJSONObject(a).getString("station");

                            if (ResultsteamNumber == teamNumberMatchResultsInt) {
                                resultsTeams.append(":star: > ").append(ResultsteamNumber).append(" - ").append(ResultsteamStation).append("\n");
                            } else {

                                resultsTeams.append(ResultsteamNumber).append(" - ").append(ResultsteamStation).append("\n");
                            }
                        }

                        eb.addField("Match " + matchNumber + " - " + description, "", false);
                        eb.addField(":busts_in_silhouette: Teams", resultsTeams.toString(), false);
                        //Find Winner
                        if (scoreRedFinal > scoreBlueFinal)
                            eb.addField("Match " + matchNumber + " - Score :scales: ", ":medal: :red_circle: Red: `" + scoreRedFinal + "` :blue_circle: Blue: `" + scoreBlueFinal + "`", true);
                        else if (scoreRedFinal < scoreBlueFinal)
                            eb.addField("Match " + matchNumber + " - Score :scales: ", ":red_circle: Red: `" + scoreRedFinal + "` :blue_circle: Blue: `" + scoreBlueFinal + "` :medal:", true);
                        else if (scoreRedFinal == scoreBlueFinal)
                            eb.addField("Match " + matchNumber + " - Score :scales: ", ":red_circle: Red: `" + scoreRedFinal + "` :blue_circle: Blue: `" + scoreBlueFinal + "`", true);

                        // For 3 terms reply, and for next 3 send to chanel and continue


                    }
                    event.replyEmbeds(eb.build()).setEphemeral(false)
                            .addActionRow(
                                    Button.primary(eventCodeStringMatchResults + ":results:0:left:" + teamNumberMatchResultsInt + ":" + lengthMatchResults + ":" + eventCodeStringMatchResults + ":" + seasonMatchResultsInt, fromUnicode("⬅")),
                                    Button.danger(event.getUser().getId() + ":delete", fromUnicode("\uD83D\uDDD1\uFE0F")),
                                    Button.primary(eventCodeStringMatchResults + ":results:0:right:" + teamNumberMatchResultsInt + ":" + lengthMatchResults + ":" + eventCodeStringMatchResults + ":" + seasonMatchResultsInt, fromUnicode("➡"))
                            ).queue();
                    break;
                }

            }
            case "match-schedule": {

                int matchScheduleTerms = 3;



                //Get options
                String eventCodeStringMatchSchedule = event.getOption("event").getAsString();
                int seasonMatchScheduleInt = event.getOption("season") != null ? event.getOption("season").getAsInt() : DEFAULT_SEASON;
                int teamNumberMatchScheduleInt = event.getOption("teamnumber").getAsInt();
                //Get data
                JSONArray matchSchedule = FTCAPI.getMatchScheduleAPI(eventCodeStringMatchSchedule, seasonMatchScheduleInt, teamNumberMatchScheduleInt);

                //Check if data is null
                if (matchSchedule.isNull(0)) {
                    eb.setTitle("FTC Match Schedule");
                    eb.setColor(ERROR_COLOR);
                    eb.setDescription("Event Code : " + eventCodeStringMatchSchedule + " | Season : " + seasonMatchScheduleInt + " | Team : " + teamNumberMatchScheduleInt);
                    eb.addField("Error", "No data found. Team is either not at this event or match schedule is unavailable.", false);
                    event.replyEmbeds(eb.build()).setEphemeral(true)
                            .addActionRow(
                                    Button.danger(event.getUser().getId() + ":delete", fromUnicode("\uD83D\uDDD1\uFE0F"))
                            ).queue();
                    return;
                } else {
                    //parse matchSchedule
                    int lengthMatchSchedule = 3;
                    eb.setTitle("FTC Match Schedule");
                    eb.setColor(MAIN_COLOR);
                    eb.setDescription("Event Code : " + eventCodeStringMatchSchedule + " | Season : " + seasonMatchScheduleInt + " | Team : " + teamNumberMatchScheduleInt);
                    //iterate through matchSchedule and add buttons at end
                    for (int i = 0; i < lengthMatchSchedule; i++) {
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
                    event.replyEmbeds(eb.build()).setEphemeral(false)
                            .addActionRow(
                                    Button.primary(eventCodeStringMatchSchedule + ":schedule:0:left:" + teamNumberMatchScheduleInt + ":" + lengthMatchSchedule + ":" + seasonMatchScheduleInt, fromUnicode("⬅")),
                                    Button.danger(event.getUser().getId() + ":delete", fromUnicode("\uD83D\uDDD1\uFE0F")),
                                    Button.primary(eventCodeStringMatchSchedule + ":schedule:0:right:" + teamNumberMatchScheduleInt + ":" + lengthMatchSchedule + ":" + seasonMatchScheduleInt, fromUnicode("➡"))
                            ).queue();

                }


                break;
            }

            case "bot-info": {
                long timeSeconds;
                long uptime;
                long timeMinutes;
                Runtime rt = Runtime.getRuntime();
                RuntimeMXBean rb = ManagementFactory.getRuntimeMXBean();
                OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
                uptime = rb.getUptime();
                timeSeconds = TimeUnit.MILLISECONDS.toSeconds(uptime);
                timeMinutes = TimeUnit.MILLISECONDS.toMinutes(uptime);
                String osName = osBean.getName();
                String osVersion = osBean.getVersion();
                String architecture = osBean.getArch();
                int availableProcessors = osBean.getAvailableProcessors();
//find ram
                long total_mem = rt.totalMemory();
                long free_mem = rt.freeMemory();
                long used_mem = total_mem - free_mem;

                eb.setTitle("Bot Info");
                eb.setColor(MAIN_COLOR);
                        //eb.addField("Uptime", timeSeconds + " seconds \n "+timeMinutes + " minutes", false);
                if (timeSeconds > 60) {
                    if (timeMinutes > 60) {

                        // hour and hours
                        if (timeMinutes / 60 == 1) {
                            eb.addField(":hourglass: Uptime", timeMinutes / 60 + " hour", false);
                        } else {
                            eb.addField(":hourglass: Uptime", timeMinutes / 60 + " hours", false);
                        }

                    } else {
                        // minute and minutes
                        if (timeMinutes == 1) {
                            eb.addField(":hourglass: Uptime", timeMinutes + " minute", false);
                        } else {
                            eb.addField(":hourglass: Uptime", timeMinutes + " minutes", false);
                        }
                    }
                } else {
                    if (timeSeconds == 1) {
                        eb.addField(":hourglass: Uptime", timeSeconds + " second", false);
                    } else {
                        eb.addField(":hourglass: Uptime", timeSeconds + " seconds", false);
                    }
                }
                eb.addField(":computer: OS", osName + " " + osVersion + " " + architecture, false);
                eb.addField(":brain: Available Processors", String.valueOf(availableProcessors), false);
                eb.addField(":floppy_disk: Memory", used_mem / 1000000 + " MB / " + total_mem / 1000000 + " MB", false);
                        event.replyEmbeds(eb.build()).setEphemeral(false)
                        .addActionRow(
                                Button.danger(event.getUser().getId() + ":delete", fromUnicode("\uD83D\uDDD1\uFE0F"))
                        ).queue();


                break;


            }



            case "ask-ai": {
                //buffer so time for slash command doesn't expire
                event.deferReply().queue();

                // question input
                OptionMapping questionAi = event.getOption("question");
                String questionAiString = questionAi.getAsString();
                // get response
                JSONObject response;
                try {
                     response = GitBookAPI.askAIDocs(LEARN_SOFTWARE_SPACE_ID,URLEncode(questionAiString));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
    eb.setFooter("Powered by GitBook Lens AI","https://d33wubrfki0l68.cloudfront.net/42cc9dc1dd30eee1bd3438e853a8cf0f82165f0c/ea050/assets/images/tool-icons/gitbook.png");
        if (response.has("answer")) {
            JSONObject reponseAnswer = response.getJSONObject("answer");
            String answer = reponseAnswer.getString("text");
            JSONArray followupQuestions = reponseAnswer.getJSONArray("followupQuestions");
            String followupQuestionString;
            if (followupQuestions.length() == 0) {
                followupQuestionString = "No followup questions, feel free to ask another question!";
            } else {
                followupQuestionString = followupQuestions.getString(0);
            }
            // find the shortest followup question out of them all

            if (answer.length() > 1000) {
                answer = answer.substring(0, 1000) + "...";
            }
            eb.setTitle("GitBook AI Response");
            eb.addField(":question: Question", questionAiString, false);
            eb.addField(":speech_balloon: Answer", answer, false);
            eb.addField(":grey_question: Followup Question", followupQuestionString + "\n</ask-ai:" + event.getCommandId() + ">", false);

            eb.setColor(MAIN_COLOR);

            eventHook.sendMessageEmbeds(eb.build()).setEphemeral(false)
                    .addActionRow(
                            Button.danger(event.getUser().getId() + ":delete", fromUnicode("\uD83D\uDDD1\uFE0F")),
                            Button.link("https://team-13190.gitbook.io/intro-to-ftc-software/", "Go to Docs")
                                    .withEmoji(Emoji.fromUnicode("\uD83D\uDCD6"))

                    )
                    .queue();
        } else {
            eb.setTitle("Docs AI Response");
            eb.setDescription("No answer found for your question, please try again!");
            eb.setColor(ERROR_COLOR);

            eventHook.sendMessageEmbeds(eb.build()).setEphemeral(true)
                    .addActionRow(
                            Button.danger(event.getUser().getId() + ":delete", fromUnicode("\uD83D\uDDD1\uFE0F")),
                            Button.link("https://team-13190.gitbook.io/intro-to-ftc-software/", "Go to Docs")
                                    .withEmoji(Emoji.fromUnicode("\uD83D\uDCD6"))
                    )
                    .queue();
        }
                break;

            }

            case "search-teams-by-name":{
                OptionMapping query = event.getOption("query");
                String queryStr = query.getAsString();

                int responseteamCount = 0;
                JSONObject response = ftcScoutAPI.teamSearchByNameRequest(queryStr);
                if (response == null){
                    eb.setTitle("Error");
                    eb.setDescription("Error getting data from FTC Scout API");
                    eb.setColor(ERROR_COLOR);
                    event.replyEmbeds(eb.build()).setEphemeral(true).queue();
                } else {
                    JSONObject data = response.getJSONObject("data");
                    JSONArray teams = data.getJSONArray("teamsSearch");
                    if (teams.length() == 0) {
                        eb.setTitle("No teams found");
                        eb.setDescription("No teams found with the name " + queryStr);
                        eb.setColor(ERROR_COLOR);
                        event.replyEmbeds(eb.build()).setEphemeral(true).queue();
                        return;
                    } else {
                        responseteamCount = teams.length();
                        // get data about each team
                        JSONObject team1 = teams.getJSONObject(0);
                        String team1Name = team1.getString("name");
                        int team1Number = team1.getInt("number");
                        String team1SchoolName = team1.getString("schoolName");
                        String team1Country = team1.getString("country");
                        String team1State = team1.getString("stateOrProvince");
                        String team1City = team1.getString("city");
                        String team1Location = team1City + ", " + team1State + ", " + team1Country;

                        eb.setTitle("Team Search Results for query: " + queryStr);
                        eb.addField("Number", String.valueOf(team1Number), false);
                        eb.addField("Name", team1Name, false);
                        eb.addField("School Name", team1SchoolName, false);
                        eb.addField("Location", team1Location, false);
                        eb.setColor(MAIN_COLOR);
                        eb.setFooter("Page " + "1" + "/" + responseteamCount, null);

                        event.replyEmbeds(eb.build()).setEphemeral(false)
                                .addActionRow(
                                        Button.primary(event.getUser().getId() + ":team-search:0:left:" + queryStr + ":" + responseteamCount, fromUnicode("⬅")),
                                        Button.danger(event.getUser().getId() + ":delete", fromUnicode("\uD83D\uDDD1\uFE0F")),
                                        Button.primary(event.getUser().getId() + ":team-search:0:right:" + queryStr + ":" + responseteamCount, fromUnicode("➡"))

                                )
                                .queue();
                    }
                }
                break;

            }

            case "todays-events":{
                JSONObject response = ftcScoutAPI.todaysEvents();
                if (response == null){
                    eb.setTitle("Error");
                    eb.setDescription("Error getting data from FTC Scout API");
                    eb.setColor(ERROR_COLOR);
                    event.replyEmbeds(eb.build()).setEphemeral(true).queue();
                } else {
                    // Get Array length
                    JSONObject data = response.getJSONObject("data");
                    JSONArray events = data.getJSONArray("todaysEvents");
                    int todaysEventCount = events.length();
                    if (todaysEventCount == 0){
                        eb.setTitle("No events today");
                        eb.setDescription("There are no events today");
                        eb.setColor(ERROR_COLOR);
                        event.replyEmbeds(eb.build()).setEphemeral(true).queue();
                    } else {
                        eb.setTitle("Events Today");
                        // Get data about each event
                        JSONObject event1 = events.getJSONObject(0);
                        String event1Name = event1.getString("name");


                        String event1Country = event1.getString("country");
                        String event1State = event1.getString("stateOrProvince");
                        String event1City = event1.getString("city");
                        String event1Location = event1City + ", " + event1State + ", " + event1Country;
                        String event1Address = event1.getString("address");
                        String event1Code = event1.getString("code");

                        eb.addField("Name", event1Name, false);
                        eb.addField("Location", event1Location, false);

                        try {
                            eb.addField("Address", "["+event1Address+"]("+directionsUrl(event1Address)+")", false);
                        } catch (UnsupportedEncodingException e) {
                            throw new RuntimeException(e);
                        }
                        eb.addField("Code", event1Code, false);
                        eb.setColor(MAIN_COLOR);
                        eb.setFooter("Page " + "1" + "/" + todaysEventCount, null);

                        event.replyEmbeds(eb.build()).setEphemeral(false)
                                .addActionRow(
                                        Button.primary(event.getUser().getId() + ":todays-events:0:left:" + todaysEventCount, fromUnicode("⬅")),
                                        Button.danger(event.getUser().getId() + ":delete", fromUnicode("\uD83D\uDDD1\uFE0F")),
                                        Button.primary(event.getUser().getId() + ":todays-events:0:right:" + todaysEventCount, fromUnicode("➡"))

                                )
                                .queue();
                    }
                break;
                }

            }
            case "events-search": {
                OptionMapping query = event.getOption("query");
                String queryStr = query.getAsString();

                int seasonEventSearch = event.getOption("season") != null ? event.getOption("season").getAsInt() : DEFAULT_SEASON;


                JSONObject response = ftcScoutAPI.eventsSearchRequest(queryStr, seasonEventSearch);

                if (response == null) {
                    eb.setTitle("Error");
                    eb.setDescription("Error getting data from FTC Scout API");
                    eb.setColor(ERROR_COLOR);
                    event.replyEmbeds(eb.build()).setEphemeral(true).queue();
                } else {
                    // Get Array length
                    JSONObject data = response.getJSONObject("data");
                    JSONArray events = data.getJSONArray("eventsSearch");
                    int eventSearchCount = events.length();
                    if (eventSearchCount == 0) {
                        eb.setTitle("No events found");
                        eb.setDescription("No events found with the name " + queryStr);
                        eb.setColor(ERROR_COLOR);
                        event.replyEmbeds(eb.build()).setEphemeral(true).queue();
                    } else {
                        eb.setTitle("Events Search Results for query: " + queryStr);
                        // Get data about each event
                        JSONObject event1 = events.getJSONObject(0);
                        String event1Name = event1.getString("name");
                        String event1Country = event1.getString("country");
                        String event1State = event1.getString("stateOrProvince");
                        String event1City = event1.getString("city");
                        String event1Location = event1City + ", " + event1State + ", " + event1Country;
                        String event1Address = event1.getString("address");
                        String event1Code = event1.getString("code");

                        eb.addField("Name", event1Name, false);
                        eb.addField("Location", event1Location, false);

                        try {
                            eb.addField("Address", "[" + event1Address + "](" + directionsUrl(event1Address) + ")", false);
                        } catch (UnsupportedEncodingException e) {
                            throw new RuntimeException(e);
                        }
                        eb.addField("Code", event1Code, false);
                        eb.setColor(MAIN_COLOR);
                        eb.setFooter("Page " + "1" + "/" + eventSearchCount, null);

                        event.replyEmbeds(eb.build()).setEphemeral(false)
                                .addActionRow(
                                        Button.primary(event.getUser().getId() + ":events-search:0:left:" + queryStr + ":" + eventSearchCount+":"+seasonEventSearch, fromUnicode("⬅")),
                                        Button.danger(event.getUser().getId() + ":delete", fromUnicode("\uD83D\uDDD1\uFE0F")),
                                        Button.primary(event.getUser().getId() + ":events-search:0:right:" + queryStr + ":" + eventSearchCount+":"+seasonEventSearch, fromUnicode("➡"))

                                )
                                .queue();
                    }
                }
                break;

            }

            case "event-info":{
                OptionMapping eventCodeEventInfo = event.getOption("event");
                String eventCodeEventInfoStr = eventCodeEventInfo.getAsString();
                int seasonEventInfo = event.getOption("season") != null ? event.getOption("season").getAsInt() : DEFAULT_SEASON;
                boolean advancedEventInfo = event.getOption("advanced") != null ? event.getOption("advanced").getAsBoolean() : false;
                eb.setColor(MAIN_COLOR);

                eb.setTitle("Event Info for: " + eventCodeEventInfoStr);

                if (!FTCAPI.GetEventUsingCode(eb,eventCodeEventInfoStr,seasonEventInfo,false,advancedEventInfo)){
                    event.replyEmbeds(eb.build()).setEphemeral(true).queue();
                } else {
                    event.replyEmbeds(eb.build()).setEphemeral(false)
                            .addActionRow(
                                    Button.danger(event.getUser().getId() + ":delete", fromUnicode("\uD83D\uDDD1\uFE0F"))
                            ).queue();
                }
                break;
            }

            case "help":{
                eb.setTitle("Help");
                eb.setColor(MAIN_COLOR);
                eb.setDescription("Commands list");
                eb.addField("- team commands", "```team-info (Info about team based on teamNumber)\nteam-awards (All time team awards)\nteam-awards-at-event (Team awards at a specific event)\nscout (Opr info about team)\nscout-event (Opr data at certain team events)\nteam-league-rank (The league rank of a team)\nevents (Get events the team has participated in, true or false for advanced view)\nsearch-teams-by-name (Search for teams by their name)\nweather (Get weather at the teams location)\nforecast (Get forecast at the teams location)```", false);
                eb.addField("- event commands", "```event-info (Info about event based on eventCode)\ntodays-events (Events happening today)\nevents-search (Search for events by text query)\nmatch-schedule (Match schedule for an event)\nmatch-results (Results of all matches at event, or specify teamNumber for just those teams matches)\nevent-awards (Get awards for all teams at that event)\nalliance-selection (Alliance selection info at a event)\nalliance-details (Get details about alliance selection at a event)```", false);
                eb.addField("- learning commands", "```learn-java (Learning resources about Java)\ndocs (Get all sorts of documentation links about FTC topics)\nask-ai (Ask an AI trained on documentation)```", false);
                eb.addField("Discord server", "https://discord.gg/hC7PkKfCau", false);

                event.replyEmbeds(eb.build()).setEphemeral(false)
                        .addActionRow(
                                Button.danger(event.getUser().getId() + ":delete", fromUnicode("\uD83D\uDDD1\uFE0F"))
                        ).queue();
                break;
            }


            default:
                // https://github.com/DV8FromTheWorld/JDA/blob/8852b5e9ed07182deaed284a067b1fe68da5936a/src/examples/java/SlashBotExample.java#L111
                event.reply("I can't handle that command right now :(").setEphemeral(true).queue();

                break;

        }
        //end time
        long endTime = System.nanoTime();
        long duration = (endTime - startCmnd);
        long durationMilis = TimeUnit.NANOSECONDS.toMillis(duration);
        System.out.println("\u001B[32m Command took " + durationMilis + " miliseconds \u001B[0m");
    }

    // Methods

    private static boolean GitHubAPIerrorCheck(@NotNull SlashCommandInteractionEvent event, EmbedBuilder eb, String[] returnedArray) {
        if (returnedArray[0].equals("Error")){
            eb.setTitle("Error");
            eb.setDescription(returnedArray[1]);
            eb.setColor(ERROR_COLOR);
                eb.setFooter("Powered by GitHub Rest API", "https://github.githubassets.com/images/modules/logos_page/GitHub-Mark.png");
            event.replyEmbeds(eb.build()).setEphemeral(true).queue();

            return true;
        }
        return false;
    }
}

